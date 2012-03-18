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

package edu.cmu.cs.stage3.alice.scenegraph.util;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Stage3</p>
 * @author Ben Buchwald
 * @version 1.0
 */

import javax.vecmath.Point2d;

public class SegmentBBox {
    Point2d[] pointBounds = new Point2d[2];
    double[] yBounds = new double[2];

    Point2d[] pointBoundsB = new Point2d[2];
    double[] yBoundsB = new double[2];

    public SegmentBBox(Point2d a, Point2d b) {
        if (Triangulator.pointCompare(a,b)<0) {
            pointBounds[0]=a;
            pointBounds[1]=b;
        } else {
            pointBounds[0]=b;
            pointBounds[1]=a;
        }
        if (a.y<b.y) {
            yBounds[0]=a.y;
            yBounds[1]=b.y;
        } else {
            yBounds[0]=b.y;
            yBounds[1]=a.y;
        }
    }

    public boolean boxOverlaps(Point2d a,Point2d b) {

        if (Triangulator.pointCompare(a,b)<0) {
            pointBoundsB[0]=a;
            pointBoundsB[1]=b;
        } else {
            pointBoundsB[0]=b;
            pointBoundsB[1]=a;
        }
        if (a.y<b.y) {
            yBoundsB[0]=a.y;
            yBoundsB[1]=b.y;
        } else {
            yBoundsB[0]=b.y;
            yBoundsB[1]=a.y;
        }


        if (Triangulator.pointCompare(pointBounds[1],pointBoundsB[0])<0)
            return false;
        if (Triangulator.pointCompare(pointBounds[0],pointBoundsB[1])>0)
            return false;
        if (yBounds[1]<yBoundsB[0])
            return false;
        if (yBounds[0]>yBoundsB[1])
            return false;

        return true;
    }

    public boolean segmentOverlaps(Point2d a,Point2d b) {
        if (!boxOverlaps(a,b))
            return false;

        if (Triangulator.pointCompare(pointBounds[0],pointBounds[1])==0 || Triangulator.pointCompare(pointBoundsB[0],pointBoundsB[1])==0)
            return false;
        if (Triangulator.pointCompare(pointBounds[0],pointBoundsB[0])==0 && Triangulator.pointCompare(pointBounds[1],pointBoundsB[1])==0)
            return true;

        int orient1 = Triangle.orientation(pointBounds[0],pointBounds[1],pointBoundsB[0]);
        int orient2 = Triangle.orientation(pointBounds[0],pointBounds[1],pointBoundsB[1]);

        if (orient1 == orient2 && orient1!=0)
            return false;


        if (orient1==0) {
            if (Triangulator.pointCompare(pointBounds[0],pointBoundsB[0])<0 && Triangulator.pointCompare(pointBoundsB[0],pointBounds[1])<0)
                return true;
            if (orient2==0 && Triangulator.pointCompare(pointBounds[0],pointBoundsB[1])<0 && Triangulator.pointCompare(pointBoundsB[1],pointBounds[1])<0)
                return true;
            return false;
        } else if (orient2==0) {
             if (Triangulator.pointCompare(pointBounds[0],pointBoundsB[1])<0 && Triangulator.pointCompare(pointBoundsB[1],pointBounds[1])<0)
                return true;
            return false;
        }

        if ( Triangle.orientation(pointBoundsB[0],pointBoundsB[1],pointBounds[0])==Triangle.orientation(pointBoundsB[0],pointBoundsB[1],pointBounds[1]) )
            return false;

        return true;
    }


}