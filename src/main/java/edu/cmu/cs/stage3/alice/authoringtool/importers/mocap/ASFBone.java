/*
 * Copyright (c) 1999-2003, Carnegie Mellon University. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 
 * 3. Products derived from the software may not be called "Alice",
 *    nor may "Alice" appear in their name, without prior written
 *    permission of Carnegie Mellon University.
 * 
 * 4. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *    "This product includes software developed by Carnegie Mellon University"
 */

package edu.cmu.cs.stage3.alice.authoringtool.importers.mocap;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: Stage3</p>
 * @author Ben Buchwald
 * @version 1.0
 */

import java.util.Vector;
import java.util.ListIterator;
import edu.cmu.cs.stage3.pratt.maxkeyframing.*;
import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.alice.scenegraph.Vertex3d;
import edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray;
//import edu.cmu.cs.stage3.alice.core.ReferenceFrame;
import edu.cmu.cs.stage3.alice.core.Direction;
import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.response.CompositeResponse;
import edu.cmu.cs.stage3.alice.core.Pose;

import edu.cmu.cs.stage3.math.MathUtilities;
import edu.cmu.cs.stage3.math.Quaternion;
import edu.cmu.cs.stage3.math.Matrix33;
import edu.cmu.cs.stage3.math.Matrix44;
import edu.cmu.cs.stage3.math.Vector3;

public class ASFBone {
    public static final Integer DOF_TX = new Integer(0);
    public static final Integer DOF_TY = new Integer(1);
    public static final Integer DOF_TZ = new Integer(2);
    public static final Integer DOF_RX = new Integer(3);
    public static final Integer DOF_RY = new Integer(4);
    public static final Integer DOF_RZ = new Integer(5);
    public static final Integer DOF_L = new Integer(6);

	private static final edu.cmu.cs.stage3.alice.core.World scene = new edu.cmu.cs.stage3.alice.core.World();

    public String name;
    public javax.vecmath.Vector3d direction;
    public double length;


    public Matrix33 model_transform;
    public Matrix33 base_axis;
    public Matrix33 axis;
    public javax.vecmath.Vector3d base_position;
    public javax.vecmath.Vector3d position;

    public Vector dof;
    public Vector children;

    public javax.vecmath.Vector3d endPoint;
    public Model model;
    public Model realMod;

    public ASFBone parent;

    public double width;

    public PositionKeyframeResponse positionKeyframeAnim;
    public QuaternionKeyframeResponse quaternionKeyframeAnim;
    public ScaleKeyframeResponse lengthKeyframeAnim;

    public CatmullRomSpline positionSpline;
    public QuaternionSlerpSpline quaternionSpline;
    public CatmullRomSpline lengthSpline;

    public double lastTime;
    public boolean hasFrame;
    public boolean accumulated;

    public ASFBone() {
        name = "";
        dof = new Vector();
        length = .04;
        width = .04;
        children = new Vector();
        base_position = new javax.vecmath.Vector3d(0,0,0);
        position = new javax.vecmath.Vector3d(0,0,0);
        direction = new javax.vecmath.Vector3d(0,0,1);
        base_axis = new Matrix33();
        axis = new Matrix33();

        realMod = null;
        lastTime = Double.NEGATIVE_INFINITY;
        hasFrame = false;
        accumulated = false;

        parent = null;

        positionKeyframeAnim = new PositionKeyframeResponse();
		positionKeyframeAnim.name.set( "bonePositionKeyframeAnim" );
		positionSpline = new CatmullRomSpline();
        positionKeyframeAnim.spline.set( positionSpline );
        positionKeyframeAnim.duration.set(null);

        quaternionKeyframeAnim = new QuaternionKeyframeResponse();
		quaternionKeyframeAnim.name.set( "jointAngleKeyframeAnim" );
		quaternionSpline = new QuaternionSlerpSpline();
        quaternionKeyframeAnim.spline.set( quaternionSpline );
        quaternionKeyframeAnim.duration.set(null);

        lengthKeyframeAnim = new ScaleKeyframeResponse();
		lengthKeyframeAnim.name.set( "boneLengthKeyframeAnim" );
		lengthSpline = new CatmullRomSpline();
        lengthKeyframeAnim.spline.set( lengthSpline );
        lengthKeyframeAnim.duration.set(null);
    }

    public ASFBone(String n) {
        this();
        name = n;
    }

    private IndexedTriangleArray buildUnitCube() {

        Vertex3d[] vertices = new Vertex3d[24];
        int[] indices = new int[36];
        indices[0] = 0; indices[1] = 1; indices[2] = 2;
        indices[3] = 0; indices[4] = 2; indices[5] = 3;
        indices[6] = 4; indices[7] = 5; indices[8] = 6;
        indices[9] = 4; indices[10] = 6; indices[11] = 7;
        indices[12] = 8; indices[13] = 9; indices[14] = 10;
        indices[15] = 8; indices[16] = 10; indices[17] = 11;
        indices[18] = 12; indices[19] = 13; indices[20] = 14;
        indices[21] = 12; indices[22] = 14; indices[23] = 15;
        indices[24] = 16; indices[25] = 17; indices[26] = 18;
        indices[27] = 16; indices[28] = 18; indices[29] = 19;
        indices[30] = 20; indices[31] = 21; indices[32] = 22;
        indices[33] = 20; indices[34] = 22; indices[35] = 23;

        vertices[0] = Vertex3d.createXYZIJKUV(-.5,.5,0,   0,0,-1, 0,0);
        vertices[1] = Vertex3d.createXYZIJKUV(.5,.5,0,    0,0,-1, 0,0);
        vertices[2] = Vertex3d.createXYZIJKUV(.5,-.5,0,   0,0,-1, 0,0);
        vertices[3] = Vertex3d.createXYZIJKUV(-.5,-.5,0,  0,0,-1, 0,0);

        vertices[4] = Vertex3d.createXYZIJKUV(.5,.5,0,      1,0,0,  0,0);
        vertices[5] = Vertex3d.createXYZIJKUV(.5,.5,1,      1,0,0,  0,0);
        vertices[6] = Vertex3d.createXYZIJKUV(.5,-.5,1,     1,0,0,  0,0);
        vertices[7] = Vertex3d.createXYZIJKUV(.5,-.5,0,     1,0,0,  0,0);

        vertices[8] =  Vertex3d.createXYZIJKUV(.5,.5,1,     0,0,1,  0,0);
        vertices[9] =  Vertex3d.createXYZIJKUV(-.5,.5,1,    0,0,1,  0,0);
        vertices[10] = Vertex3d.createXYZIJKUV(-.5,-.5,1,   0,0,1,  0,0);
        vertices[11] = Vertex3d.createXYZIJKUV(.5,-.5,1,    0,0,1,  0,0);

        vertices[12] = Vertex3d.createXYZIJKUV(-.5,.5,1,    -1,0,0, 0,0);
        vertices[13] = Vertex3d.createXYZIJKUV(-.5,.5,0,    -1,0,0, 0,0);
        vertices[14] = Vertex3d.createXYZIJKUV(-.5,-.5,0,   -1,0,0, 0,0);
        vertices[15] = Vertex3d.createXYZIJKUV(-.5,-.5,1,   -1,0,0, 0,0);

        vertices[16] = Vertex3d.createXYZIJKUV(-.5,.5,1,    0,1,0,  0,0);
        vertices[17] = Vertex3d.createXYZIJKUV(.5,.5,1,     0,1,0,  0,0);
        vertices[18] = Vertex3d.createXYZIJKUV(.5,.5,0,     0,1,0,  0,0);
        vertices[19] = Vertex3d.createXYZIJKUV(-.5,.5,0,    0,1,0,  0,0);

        vertices[20] = Vertex3d.createXYZIJKUV(-.5,-.5,0,   0,-1,0, 0,0);
        vertices[21] = Vertex3d.createXYZIJKUV(.5,-.5,0,    0,-1,0, 0,0);
        vertices[22] = Vertex3d.createXYZIJKUV(.5,-.5,1,    0,-1,0, 0,0);
        vertices[23] = Vertex3d.createXYZIJKUV(-.5,-.5,1,   0,-1,0, 0,0);

        IndexedTriangleArray ita = new IndexedTriangleArray();
        ita.indices.set(indices);
        ita.vertices.set(vertices);
        return ita;
    }

    private IndexedTriangleArray buildBoneGeometry(double width, double length) {
        /*
        Transformable tmp = new Transformable();
        if (direction.x==0.0 && direction.z==0.0)
            tmp.setOrientationRightNow(direction,new Vector3(0,0,-1));
        else
            tmp.setOrientationRightNow(direction,new Vector3(0,1,0));
        Matrix44 coordSys = tmp.getLocalTransformation();
        */
        Matrix44 coordSys = Matrix44.IDENTITY;


        javax.vecmath.Vector4d point;
		javax.vecmath.Vector4d normal;

        Vertex3d[] vertices = new Vertex3d[24];
        int[] indices = new int[36];
        indices[0] = 0; indices[1] = 1; indices[2] = 2;
        indices[3] = 0; indices[4] = 2; indices[5] = 3;
        indices[6] = 4; indices[7] = 5; indices[8] = 6;
        indices[9] = 4; indices[10] = 6; indices[11] = 7;
        indices[12] = 8; indices[13] = 9; indices[14] = 10;
        indices[15] = 8; indices[16] = 10; indices[17] = 11;
        indices[18] = 12; indices[19] = 13; indices[20] = 14;
        indices[21] = 12; indices[22] = 14; indices[23] = 15;
        indices[24] = 16; indices[25] = 17; indices[26] = 18;
        indices[27] = 16; indices[28] = 18; indices[29] = 19;
        indices[30] = 20; indices[31] = 21; indices[32] = 22;
        indices[33] = 20; indices[34] = 22; indices[35] = 23;

        normal = MathUtilities.multiply(coordSys,new javax.vecmath.Vector4d(0,0,-1,0));
        point = MathUtilities.multiply(coordSys,new javax.vecmath.Vector4d(-width/2,width/2,0,1));
        vertices[0] = Vertex3d.createXYZIJKUV(point.x,point.y,point.z,   normal.x,normal.y,normal.z, 0,0);
        point = MathUtilities.multiply(coordSys,new javax.vecmath.Vector4d(width/2,width/2,0,1));
        vertices[1] = Vertex3d.createXYZIJKUV(point.x,point.y,point.z,   normal.x,normal.y,normal.z, 0,0);
        point = MathUtilities.multiply(coordSys,new javax.vecmath.Vector4d(width/2,-width/2,0,1));
        vertices[2] = Vertex3d.createXYZIJKUV(point.x,point.y,point.z,   normal.x,normal.y,normal.z, 0,0);
        point = MathUtilities.multiply(coordSys,new javax.vecmath.Vector4d(-width/2,-width/2,0,1));
        vertices[3] = Vertex3d.createXYZIJKUV(point.x,point.y,point.z,   normal.x,normal.y,normal.z, 0,0);

        normal = MathUtilities.multiply(coordSys,new javax.vecmath.Vector4d(1,0,0,0));
        point = MathUtilities.multiply(coordSys,new javax.vecmath.Vector4d(width/2,width/2,0,1));
        vertices[4] = Vertex3d.createXYZIJKUV(point.x,point.y,point.z,   normal.x,normal.y,normal.z, 0,0);
        point = MathUtilities.multiply(coordSys,new javax.vecmath.Vector4d(width/2,width/2,length,1));
        vertices[5] = Vertex3d.createXYZIJKUV(point.x,point.y,point.z,   normal.x,normal.y,normal.z, 0,0);
        point = MathUtilities.multiply(coordSys,new javax.vecmath.Vector4d(width/2,-width/2,length,1));
        vertices[6] = Vertex3d.createXYZIJKUV(point.x,point.y,point.z,   normal.x,normal.y,normal.z, 0,0);
        point = MathUtilities.multiply(coordSys,new javax.vecmath.Vector4d(width/2,-width/2,0,1));
        vertices[7] = Vertex3d.createXYZIJKUV(point.x,point.y,point.z,   normal.x,normal.y,normal.z, 0,0);

        //System.out.println(endPoint);
        //System.out.println(Vector3.interpolate(new Vector3(Vector4.multiply(new Vector4(width/2,width/2,length,1),coordSys)),new Vector3(Vector4.multiply(new Vector4(-width/2,-width/2,length,1),coordSys)),.5));

        point = MathUtilities.multiply(coordSys,new javax.vecmath.Vector4d(width/2,width/2,length,1));
        normal = MathUtilities.multiply(coordSys,new javax.vecmath.Vector4d(0,0,1,0));
        vertices[8] =  Vertex3d.createXYZIJKUV(point.x,point.y,point.z,   normal.x,normal.y,normal.z, 0,0);
        point = MathUtilities.multiply(coordSys,new javax.vecmath.Vector4d(-width/2,width/2,length,1));
        vertices[9] =  Vertex3d.createXYZIJKUV(point.x,point.y,point.z,   normal.x,normal.y,normal.z, 0,0);
        point = MathUtilities.multiply(coordSys,new javax.vecmath.Vector4d(-width/2,-width/2,length,1));
        vertices[10] = Vertex3d.createXYZIJKUV(point.x,point.y,point.z,   normal.x,normal.y,normal.z, 0,0);
        point = MathUtilities.multiply(coordSys,new javax.vecmath.Vector4d(width/2,-width/2,length,1));
        vertices[11] = Vertex3d.createXYZIJKUV(point.x,point.y,point.z,   normal.x,normal.y,normal.z, 0,0);

        point = MathUtilities.multiply(coordSys,new javax.vecmath.Vector4d(-width/2,width/2,length,1));
        normal = MathUtilities.multiply(coordSys,new javax.vecmath.Vector4d(-1,0,0,0));
        vertices[12] = Vertex3d.createXYZIJKUV(point.x,point.y,point.z,   normal.x,normal.y,normal.z, 0,0);
        point = MathUtilities.multiply(coordSys,new javax.vecmath.Vector4d(-width/2,width/2,0,1));
        vertices[13] = Vertex3d.createXYZIJKUV(point.x,point.y,point.z,   normal.x,normal.y,normal.z, 0,0);
        point = MathUtilities.multiply(coordSys,new javax.vecmath.Vector4d(-width/2,-width/2,0,1));
        vertices[14] = Vertex3d.createXYZIJKUV(point.x,point.y,point.z,   normal.x,normal.y,normal.z, 0,0);
        point = MathUtilities.multiply(coordSys,new javax.vecmath.Vector4d(-width/2,-width/2,length,1));
        vertices[15] = Vertex3d.createXYZIJKUV(point.x,point.y,point.z,   normal.x,normal.y,normal.z, 0,0);

        point = MathUtilities.multiply(coordSys,new javax.vecmath.Vector4d(-width/2,width/2,length,1));
        normal = MathUtilities.multiply(coordSys,new javax.vecmath.Vector4d(0,1,0,0));
        vertices[16] = Vertex3d.createXYZIJKUV(point.x,point.y,point.z,   normal.x,normal.y,normal.z, 0,0);
        point = MathUtilities.multiply(coordSys,new javax.vecmath.Vector4d(width/2,width/2,length,1));
        vertices[17] = Vertex3d.createXYZIJKUV(point.x,point.y,point.z,   normal.x,normal.y,normal.z, 0,0);
        point = MathUtilities.multiply(coordSys,new javax.vecmath.Vector4d(width/2,width/2,0,1));
        vertices[18] = Vertex3d.createXYZIJKUV(point.x,point.y,point.z,   normal.x,normal.y,normal.z, 0,0);
        point = MathUtilities.multiply(coordSys,new javax.vecmath.Vector4d(-width/2,width/2,0,1));
        vertices[19] = Vertex3d.createXYZIJKUV(point.x,point.y,point.z,   normal.x,normal.y,normal.z, 0,0);

        normal = MathUtilities.multiply(coordSys,new javax.vecmath.Vector4d(0,-1,0,0));
        point = MathUtilities.multiply(coordSys,new javax.vecmath.Vector4d(-width/2,-width/2,0,1));
        vertices[20] = Vertex3d.createXYZIJKUV(point.x,point.y,point.z,   normal.x,normal.y,normal.z, 0,0);
        point = MathUtilities.multiply(coordSys,new javax.vecmath.Vector4d(width/2,-width/2,0,1));
        vertices[21] = Vertex3d.createXYZIJKUV(point.x,point.y,point.z,   normal.x,normal.y,normal.z, 0,0);
        point = MathUtilities.multiply(coordSys,new javax.vecmath.Vector4d(width/2,-width/2,length,1));
        vertices[22] = Vertex3d.createXYZIJKUV(point.x,point.y,point.z,   normal.x,normal.y,normal.z, 0,0);
        point = MathUtilities.multiply(coordSys,new javax.vecmath.Vector4d(-width/2,-width/2,length,1));
        vertices[23] = Vertex3d.createXYZIJKUV(point.x,point.y,point.z,   normal.x,normal.y,normal.z, 0,0);

        IndexedTriangleArray ita = new IndexedTriangleArray();
        ita.indices.set(indices);
        ita.vertices.set(vertices);

        return ita;
    }

    public Model buildBone(ASFBone parent) {
        return buildBone(parent,false);
    }

    public Model buildBone(ASFBone parent, boolean length_geometry) {
        if (parent!=null) {
            base_position = parent.endPoint;
            model = new Model();
            model.isFirstClass.set(false);
            parent.model.addChild(model);
            parent.model.parts.add(model);
            //model.addToParent(parent.model);
            model.vehicle.set(parent.model);
        } else {
            model = new Model();
            model.isFirstClass.set(true);
            model.vehicle.set(scene);
        }
        this.parent = parent;

        model.setOrientationRightNow(base_axis,scene);
        model.setPositionRightNow(base_position,scene);

        base_axis = model.getOrientationAsAxes();
        base_position = model.getPosition();

        IndexedTriangleArray geom = buildBoneGeometry(.04,.04);
        model.addChild(geom);
        model.geometry.set(geom);
        model.name.set(name);

        if (parent!=null) {
            endPoint = MathUtilities.multiply(direction,length/direction.length());
            endPoint.add(parent.endPoint);
        } else
            endPoint = new javax.vecmath.Vector3d(0,0,0);

        if (length_geometry && length!=0.0 && parent!=null) {
            //model.isShowing.set(false,edu.cmu.cs.stage3.util.HowMuch.INSTANCE);

            Model unit_cube = new Model();
            geom = buildBoneGeometry(.04,length);//buildUnitCube();
            unit_cube.addChild(geom);
            unit_cube.geometry.set(geom);
            unit_cube.isFirstClass.set(false);
            unit_cube.name.set(model.name.getStringValue().concat("_bone"));
            model.addChild(unit_cube);
            model.parts.add(unit_cube);
            //unit_cube.addToParent(model);
            unit_cube.vehicle.set(model);
            if (direction.x==0.0 && direction.z==0.0)
                unit_cube.setOrientationRightNow(direction,new javax.vecmath.Vector3d(0,0,-1),scene);
            else
                unit_cube.setOrientationRightNow(direction,new javax.vecmath.Vector3d(0,1,0),scene);
        }

        ASFBone child;
        ListIterator li;
        li = children.listIterator();

        while(li.hasNext()) {
            child = (ASFBone)li.next();

            Model child_piece = child.buildBone(this);
        }
        return model;
    }

    public void setBasePose(Model mod) {
        setBasePose(mod,true);
    }

    public void setBasePose(Model mod, boolean use) {
        if (mod == null) mod = model;

        if (use) {
            realMod = mod;
            if (name.compareTo("root")==0) {
                mod.setPointOfViewRightNow(model.getPointOfView());
                if (model!=mod)
                    mod.turnRightNow(Direction.LEFT,.5);
            }

            base_axis = model.getOrientationAsAxes(mod.vehicle.getReferenceFrameValue());

            Matrix33 toCharacter = mod.getOrientationAsAxes();
            Matrix33 base_axis_inv = new Matrix33(base_axis);
            base_axis_inv.invert();

            model_transform = Matrix33.multiply(toCharacter,base_axis_inv);

            base_position = MathUtilities.subtract(mod.getPosition(),base_position);
        }

        ASFBone child;
        ListIterator li;
        li = children.listIterator();

        while(li.hasNext()) {
            child = (ASFBone)li.next();

            Model part = (edu.cmu.cs.stage3.alice.core.Model)mod.getChildNamed(child.name);
            /*Element[] parts = mod.search(new ElementNamedCriterion(child.name));
            if (parts.length==1) {
                part = (Model)parts[0];
            } else {
            */
            if (part==null) {
                BoneSelectDialog bsd;
                //if (parts.length==0) {
                    bsd = new BoneSelectDialog(child.name,mod.getChildren(edu.cmu.cs.stage3.alice.core.Model.class));
                /*} else {
                    bsd = new BoneSelectDialog(child.name,parts);
                }*/
                bsd.pack();
                bsd.setVisible( true );
                if (bsd.getSelectedPart()!=null) {
                    part = (Model)bsd.getSelectedPart();
                } else if (bsd.doDescend()) {
                    child.setBasePose(mod,false);
                }
            }

            if (part!= null) {
                child.setBasePose(part);
            }
        }

    }

    public void addFrames() {
        addFrames(false,Matrix44.IDENTITY);
    }

    public void addFrames(Matrix44 accum) {
        addFrames(true,accum);
    }

    public void addFrames(boolean hasAccum, Matrix44 accum) {
        Matrix44 newAccum = Matrix44.IDENTITY;
        if (hasAccum)
            accumulated = true;
        if (hasFrame || hasAccum) {
            newAccum = Matrix44.multiply(new Matrix44(axis,position),accum);
            if (realMod!=null) {
                if (dof.contains(DOF_TX) || dof.contains(DOF_TY) || dof.contains(DOF_TZ))
                    //positionSpline.addKey(new Vector3SimpleKey(lastTime,newAccum.getPosition()));
                    positionSpline.addKey(new Vector3SimpleKey(lastTime,Matrix44.multiply(new Matrix44(model_transform,new javax.vecmath.Vector3d()),Matrix44.multiply(newAccum,new Matrix44(base_axis,new javax.vecmath.Vector3d()))).getPosition()));

                if (dof.contains(DOF_RX) || dof.contains(DOF_RY) || dof.contains(DOF_RZ) || hasAccum)
                    quaternionSpline.addKey(new QuaternionKey(lastTime,Matrix33.multiply(model_transform,Matrix33.multiply(newAccum.getAxes(),base_axis)).getQuaternion()));
            }
        }

        ASFBone child;
        ListIterator li;
        li = children.listIterator();

        while(li.hasNext()) {
            child = (ASFBone)li.next();

            if (realMod==null && (hasFrame || hasAccum)) {
                Matrix44 newInvBase = child.model.getPointOfView(model);
                newInvBase.invert();
                child.addFrames(Matrix44.multiply(Matrix44.multiply(child.model.getPointOfView(model),newAccum),newInvBase));
            } else {
                child.addFrames();
            }

        }
        hasFrame=false;
    }

    public Response buildAnim(CompositeResponse anim) {
        if (realMod!=null) {
            if (dof.contains(ASFBone.DOF_TX) || dof.contains(ASFBone.DOF_TY) || dof.contains(ASFBone.DOF_TZ)) {
                positionKeyframeAnim.subject.set(realMod);
                positionKeyframeAnim.name.set(name.concat(positionKeyframeAnim.name.getStringValue()));
                anim.addChild(positionKeyframeAnim);
                anim.componentResponses.add(positionKeyframeAnim);
            }
            if (dof.contains(ASFBone.DOF_RX) || dof.contains(ASFBone.DOF_RY) || dof.contains(ASFBone.DOF_RZ) || accumulated) {
                quaternionKeyframeAnim.subject.set(realMod);
                quaternionKeyframeAnim.name.set(name.concat(quaternionKeyframeAnim.name.getStringValue()));
                anim.addChild(quaternionKeyframeAnim);
                anim.componentResponses.add(quaternionKeyframeAnim);
            }
            /*
            if (dof.contains(ASFBone.DOF_L)) {
                lengthKeyframeAnim.subject.set(realMod);
                lengthKeyframeAnim.name.set(name.concat(lengthKeyframeAnim.name.getStringValue()));
                anim.addChild(lengthKeyframeAnim);
                anim.componentResponses.add(lengthKeyframeAnim);
            }
            */
        }

        ASFBone child;
        ListIterator li;
        li = children.listIterator();

        while(li.hasNext()) {
            child = (ASFBone)li.next();

            child.buildAnim(anim);
        }
        return anim;
    }

    public void buildPoses(Model rootMod, Pose startPose, Pose endPose) {
        Key positionKey;
        Key quaternionKey;
        Vector3 position;
        Quaternion orientation;


        if (realMod!=null) {
            position=realMod.getPosition();
            if (dof.contains(ASFBone.DOF_TX) || dof.contains(ASFBone.DOF_TY) || dof.contains(ASFBone.DOF_TZ)) {
                positionKey = positionSpline.getFirstKey();
				position = (Vector3)positionKey.createSample( positionKey.getValueComponents() );
			}
            orientation = realMod.getOrientationAsQuaternion();
            if (dof.contains(ASFBone.DOF_RX) || dof.contains(ASFBone.DOF_RY) || dof.contains(ASFBone.DOF_RZ) || accumulated) {
                quaternionKey = quaternionSpline.getFirstKey();
                orientation = (Quaternion)quaternionKey.createSample(quaternionKey.getValueComponents());
            }
            ((java.util.Hashtable)startPose.poseMap.getDictionaryValue()).put(realMod.getKey(rootMod),new Matrix44(orientation,position));


            if (dof.contains(ASFBone.DOF_TX) || dof.contains(ASFBone.DOF_TY) || dof.contains(ASFBone.DOF_TZ)) {
                positionKey = positionSpline.getLastKey();
				position = (Vector3)positionKey.createSample( positionKey.getValueComponents() );
			}
            if (dof.contains(ASFBone.DOF_RX) || dof.contains(ASFBone.DOF_RY) || dof.contains(ASFBone.DOF_RZ) || accumulated) {
                quaternionKey = quaternionSpline.getLastKey();
                orientation = (Quaternion)quaternionKey.createSample(quaternionKey.getValueComponents());
            }
            ((java.util.Hashtable)endPose.poseMap.getDictionaryValue()).put(realMod.getKey(rootMod),new Matrix44(orientation,position));
        }

        ASFBone child;
        ListIterator li;
        li = children.listIterator();

        while(li.hasNext()) {
            child = (ASFBone)li.next();

            child.buildPoses(rootMod,startPose, endPose);
        }
    }
}