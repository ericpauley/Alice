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

import java.awt.geom.PathIterator;
import javax.vecmath.*;
import edu.cmu.cs.stage3.alice.scenegraph.Vertex3d;
import edu.cmu.cs.stage3.alice.scenegraph.util.Triangle;

public class Polygon {

    private java.util.Vector segments;

    private Vertex3d[] triVertices = null;
    private int[] indices = null;

    public Polygon() {
        segments = new java.util.Vector();
    }

    public boolean parsePathIterator(PathIterator pi, Point2d offset, int curvature) {
        double[] coords = new double[6];
        int type=-1;
        boolean advance=true;

        while(!pi.isDone()) {
            type = pi.currentSegment(coords);
            switch (type) {
                case PathIterator.SEG_MOVETO:
                    if (!segments.isEmpty())
                        if (!((PolygonSegment)segments.firstElement()).contains(offset.x-coords[0],offset.y-coords[1])) {
                            return false;
                        }
                    PolygonSegment ps = new PolygonSegment();
                    advance = ps.parsePathIterator(pi,offset, curvature);
                    if (!ps.isNull()) {
                        segments.add(ps);
                    }
                    break;
                default:
            }
            if (advance && !pi.isDone())
                    pi.next();
        }

        return true;
    }

    public boolean isNull() {
        return segments.isEmpty();
    }

    public void triangulate(double extz) {
        triVertices = null; indices = null;
        if (segments.isEmpty()) return;

        edu.cmu.cs.stage3.alice.scenegraph.util.Triangulator triangulator = new edu.cmu.cs.stage3.alice.scenegraph.util.Triangulator();

        java.util.ListIterator li = segments.listIterator();
        while (li.hasNext()) {
            triangulator.addContour(((PolygonSegment)li.next()).points());
        }

        triangulator.triangulate();

        indices = new int[triangulator.triangles.size()*3*2];
        triVertices = new Vertex3d[triangulator.points.size()*2];

        Vector3d norm1 = new Vector3d(0,0,-1);
        Vector3d norm2 = new Vector3d(0,0,1);

        li = triangulator.points.listIterator();
        for (int i=0; li.hasNext(); i++) {
            Point2d curPoint = (Point2d)li.next();
            triVertices[i]=new Vertex3d(new Point3d(curPoint.x,curPoint.y,-extz/2),norm1,null,null,new TexCoord2f());
            triVertices[triangulator.points.size()+i]=new Vertex3d(new Point3d(curPoint.x,curPoint.y,extz/2),norm2,null,null,new TexCoord2f());
        }

        li = triangulator.triangles.listIterator();
        for (int i=0; li.hasNext(); i++) {
            Triangle curTri = (Triangle)li.next();
            indices[i*3]=triangulator.indexOfPoint(curTri.vertices[2]);
            indices[i*3+1]=triangulator.indexOfPoint(curTri.vertices[1]);
            indices[i*3+2]=triangulator.indexOfPoint(curTri.vertices[0]);
            indices[triangulator.triangles.size()*3+i*3]=triangulator.points.size()+indices[i*3+2];
            indices[triangulator.triangles.size()*3+i*3+1]=triangulator.points.size()+indices[i*3+1];
            indices[triangulator.triangles.size()*3+i*3+2]=triangulator.points.size()+indices[i*3];
        }


        genSideStrips(extz);

        li = segments.listIterator();
        while (li.hasNext()) {
            PolygonSegment seg = (PolygonSegment)li.next();
            Vertex3d[] newVertices = new Vertex3d[triVertices.length+seg.getSideVertices().length];
            System.arraycopy(triVertices,0,newVertices,0,triVertices.length);
            int offset=triVertices.length;
            System.arraycopy(seg.getSideVertices(),0,newVertices,triVertices.length,seg.getSideVertices().length);
            triVertices = newVertices;
            int[] newIndices = new int[indices.length+seg.getIndices().length];
            System.arraycopy(indices,0,newIndices,0,indices.length);
            for (int j=0; j<seg.getIndices().length; j++) {
                newIndices[indices.length+j]=seg.getIndices()[j]+offset;
            }
            indices = newIndices;
        }

    }

    protected void genSideStrips(double extz) {
        if (segments.isEmpty()) return;
        ((PolygonSegment)segments.firstElement()).genSideStrips(extz);
        java.util.ListIterator li = segments.listIterator();
        while (li.hasNext())
            ((PolygonSegment)li.next()).genSideStrips(extz);
    }

    public Vertex3d[] getVertices() {
        return triVertices;
    }

    public int[] getIndices() {
        return indices;
    }
}