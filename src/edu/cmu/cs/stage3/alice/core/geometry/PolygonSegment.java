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

package edu.cmu.cs.stage3.alice.core.geometry;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Stage3</p>
 * @author Ben Buchwald
 * @version 1.0
 */

import java.awt.Shape;
import java.awt.geom.PathIterator;
import javax.vecmath.*;
import edu.cmu.cs.stage3.math.MathUtilities;
import edu.cmu.cs.stage3.alice.scenegraph.Vertex3d;

public class PolygonSegment {
    private java.util.Vector points;
    private java.util.Vector normals;

    private Vertex3d[] sideVertices = null;
    private int[] indices = null;

    public PolygonSegment() {
        points = new java.util.Vector();
        normals = new java.util.Vector();
    }

    protected Shape getShape() {
        if (points.isEmpty()) return null;
        java.awt.geom.GeneralPath gp = new java.awt.geom.GeneralPath();
        gp.moveTo((float)((Point2d)points.firstElement()).x,(float)((Point2d)points.firstElement()).y);
        java.util.ListIterator li = points.listIterator(1);
        while (li.hasNext()) {
            Point2d cur = (Point2d)li.next();
            gp.lineTo((float)cur.x,(float)cur.y);
        }
        gp.closePath();
        return gp;
    }

    public boolean contains(double x, double y) {
        return getShape().contains(x,y);
    }

    protected void addPoint(Point2d point) {
        points.add(point);
        normals.setSize(normals.size()+2);
        if (points.size()>1) {
            double dx = ((Point2d)points.lastElement()).x-((Point2d)points.elementAt(points.size()-2)).x;
            double dy = ((Point2d)points.lastElement()).y-((Point2d)points.elementAt(points.size()-2)).y;
            double len = Math.sqrt(dx*dx+dy*dy);
            Vector3d a = new Vector3d(dx/len,0,dy/len);
            Vector3d b = new Vector3d(0,1,0);
            Vector3d c = MathUtilities.crossProduct(a,b);
            normals.setElementAt(new Vector3f(-(float)c.x,-(float)c.z,0),(points.size()-2)*2);
            normals.setElementAt(new Vector3f(-(float)c.x,-(float)c.z,0),(points.size()-1)*2+1);
        }
    }

    protected void addQuadraticSpline(Point2d cp1, Point2d cp2, Point2d offset, int numSegs) {
        if (points.isEmpty()) return;

        normals.setSize(normals.size()+2*numSegs);

        Point2d cp0 = new Point2d(-((Point2d)points.lastElement()).x+offset.x,-((Point2d)points.lastElement()).y+offset.y);

        Point3d[] newPositions = new Point3d[numSegs+1];
        Vector3d[] newNormals = new Vector3d[numSegs+1];

        edu.cmu.cs.stage3.alice.core.util.Polynomial.evaluateBezierQuadratic(cp0,cp1,cp2,0,newPositions,newNormals);

        normals.setElementAt(new Vector3f(newNormals[0]),(points.size()-1)*2);
        for (int i=1; i<=numSegs; i++) {
            points.add(new Point2d(offset.x-newPositions[i].x,offset.y-newPositions[i].y));
            normals.setElementAt(new Vector3f(newNormals[i]),(points.size()-1)*2);
            normals.setElementAt(new Vector3f(newNormals[i]),(points.size()-1)*2+1);
        }
    }

    protected void close() {
        if (points.isEmpty()) return;

        if (points.size()>1 && ((Point2d)points.lastElement()).equals((Point2d)points.firstElement())) {
            points.setSize(points.size()-1);
            normals.setSize(normals.size()-2);
        }
        if (points.size()>=3) {

            double dx = ((Point2d)points.firstElement()).x-((Point2d)points.lastElement()).x;
            double dy = ((Point2d)points.firstElement()).y-((Point2d)points.lastElement()).y;
            double len = Math.sqrt(dx*dx+dy*dy);
			Vector3d a = new Vector3d(dx/len,0,dy/len);
			Vector3d b = new Vector3d(0,1,0);
			Vector3d c = MathUtilities.crossProduct(a,b);
            normals.setElementAt(new Vector3f(-(float)c.x,-(float)c.z,0),1);
            normals.setElementAt(new Vector3f(-(float)c.x,-(float)c.z,0),(points.size()-1)*2);
        } else {
            points.clear();
            normals.clear();
        }
    }

    public boolean parsePathIterator(PathIterator pi,Point2d offset, int curvature) {
        double[] coords = new double[6];
        int type=-1;

        while(!pi.isDone()) {
            type = pi.currentSegment(coords);
            switch (type) {
                case PathIterator.SEG_MOVETO:
                    if (!points.isEmpty()) {
                        close();
                        return false;
                    }
                    addPoint(new Point2d(offset.x-coords[0],offset.y-coords[1]));
                    break;
                case PathIterator.SEG_LINETO:
                    addPoint(new Point2d(offset.x-coords[0],offset.y-coords[1]));
                    break;
                case PathIterator.SEG_QUADTO:
                    addQuadraticSpline(new Point2d(coords[0],coords[1]),new Point2d(coords[2],coords[3]),offset,curvature);
                    break;
                case PathIterator.SEG_CUBICTO:
                    addPoint(new Point2d(offset.x-coords[0],offset.y-coords[1]));
                    addPoint(new Point2d(offset.x-coords[2],offset.y-coords[3]));
                    addPoint(new Point2d(offset.x-coords[4],offset.y-coords[5]));
                    break;
                case PathIterator.SEG_CLOSE:
                    close();
                    return true;
            }
            pi.next();
        }
        close();
        return true;
    }

    public boolean isNull() {
        return points.isEmpty();
    }

    public java.util.Vector points() {
        return points;
    }

    public void reverse() {
        java.util.Collections.reverse(points);
        java.util.Collections.reverse(normals);
    }

    public void genSideStrips(double extz/*, boolean outside*/) {
        sideVertices = null; indices = null;
        if (points.isEmpty()) return;

        sideVertices = new Vertex3d[points.size()*4];
        indices = new int[points.size()*6];

        java.util.ListIterator li = points.listIterator();
        for (int i=0; li.hasNext(); i++) {
            Point2d point = (Point2d)li.next();

            Point3d pos = new Point3d(point.x,point.y,-extz/2);
            sideVertices[i*2]=new Vertex3d(pos,new Vector3d((Vector3f)normals.elementAt(i*2)),null,null,new TexCoord2f());
            sideVertices[i*2+1]=new Vertex3d(pos,new Vector3d((Vector3f)normals.elementAt(i*2+1)),null,null,new TexCoord2f());
            pos = new Point3d(point.x,point.y,extz/2);
            sideVertices[points.size()*2+i*2]=new Vertex3d(pos,new Vector3d((Vector3f)normals.elementAt(i*2)),null,null,new TexCoord2f());
            sideVertices[points.size()*2+i*2+1]=new Vertex3d(pos,new Vector3d((Vector3f)normals.elementAt(i*2+1)),null,null,new TexCoord2f());
        }

        for (int i=0; i<points.size()-1; i++) {
            //if (outside) {
                indices[i*6]=2*i;
                indices[i*6+1]=2*i+3;
                indices[i*6+2]=2*i+3+points.size()*2;
                indices[i*6+3]=2*i;
                indices[i*6+4]=2*i+3+points.size()*2;
                indices[i*6+5]=2*i+points.size()*2;
            /*} else {
                indices[i*6]=2*i;
                indices[i*6+1]=2*i+3+points.length*2;
                indices[i*6+2]=2*i+3;
                indices[i*6+3]=2*i;
                indices[i*6+4]=2*i+points.length*2;
                indices[i*6+5]=2*i+3+points.length*2;
            }*/
        }
        //if (outside) {
            indices[(points.size()-1)*6]=2*(points.size()-1);
            indices[(points.size()-1)*6+1]=1;
            indices[(points.size()-1)*6+2]=1+points.size()*2;
            indices[(points.size()-1)*6+3]=2*(points.size()-1);
            indices[(points.size()-1)*6+4]=1+points.size()*2;
            indices[(points.size()-1)*6+5]=2*(points.size()-1)+points.size()*2;
        /*
        } else {
            indices[(points.length-1)*6]=2*(points.length-1);
            indices[(points.length-1)*6+1]=1+points.length*2;
            indices[(points.length-1)*6+2]=1;
            indices[(points.length-1)*6+3]=2*(points.length-1);
            indices[(points.length-1)*6+4]=2*(points.length-1)+points.length*2;
            indices[(points.length-1)*6+5]=1+points.length*2;
        }
        */

    }

    public Vertex3d[] getSideVertices() {
        return sideVertices;
    }

    public int[] getIndices() {
        return indices;
    }
}