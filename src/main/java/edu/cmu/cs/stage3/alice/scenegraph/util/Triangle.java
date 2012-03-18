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
import javax.vecmath.Vector2d;

public class Triangle {
    // remember triangles

    public Point2d[] vertices = new Point2d[3];

    public Triangle(Point2d a, Point2d b, Point2d c) {
        vertices[0]=a;
        vertices[1]=b;
        vertices[2]=c;
    }

    // static calls to do some stuff, almost never on real triangles
    private static Point2d[] sorted = new Point2d[3];

    public static double signedArea(Point2d vertex0,Point2d vertex1,Point2d vertex2) {
        Point2d temp;

        sorted[0] = vertex0;
        sorted[1] = vertex1;
        sorted[2] = vertex2;

        int sign = 1;

        if (Triangulator.pointCompare(sorted[0],sorted[1])>0) {
            sign = -sign;
            temp = sorted[0];
            sorted[0]=sorted[1];
            sorted[1]=temp;
        }
        if (Triangulator.pointCompare(sorted[1],sorted[2])>0) {
            sign = -sign;
            temp = sorted[1];
            sorted[1]=sorted[2];
            sorted[2]=temp;
        }
        if (Triangulator.pointCompare(sorted[0],sorted[1])>0) {
            sign = -sign;
            temp = sorted[0];
            sorted[0]=sorted[1];
            sorted[1]=temp;
        }

        return sign*( (sorted[0].x-sorted[1].x)*(sorted[1].y-sorted[2].y) + (sorted[1].y-sorted[0].y)*(sorted[1].x-sorted[2].x) );
    }

    public static int orientation(Point2d vertex0,Point2d vertex1,Point2d vertex2) {
        // 1 == CCW
        // -1 == CW
        double sa = signedArea(vertex0,vertex1,vertex2);

        if (sa<0) return -1;
        else if (sa>0) return 1;
        return 0;
    }

    private static Vector2d[] sides = new Vector2d[2];

    public static int convex(Point2d vertex0,Point2d vertex1,Point2d vertex2) {
        if (Triangulator.pointCompare(vertex0,vertex1)==0)
            return 1;
        else if (Triangulator.pointCompare(vertex1,vertex2)==0)
            return -1;
        else {
            int o = orientation(vertex0,vertex1,vertex2);
            if (o!=0)
                return o;
            else {
                sides[0] = new Vector2d(vertex0);
                sides[0].sub(vertex1);
                sides[1] = new Vector2d(vertex2);
                sides[1].sub(vertex1);

                 if (sides[0].dot(sides[1]) < 0)
                    return 0;
                else
                    return -2; // TODO: this really needs to expand out to other vertice
            }
        }
    }

    public static boolean inCone(Point2d vertex0,Point2d vertex1,Point2d vertex2,Point2d check) {
        int tri;

        if (convex(vertex0,vertex1,vertex2)>0) {
            if ((Triangulator.pointCompare(vertex0,check)!=0)  &&  (Triangulator.pointCompare(vertex1,check)!=0)) {
                tri = orientation(vertex0,vertex1,check);
                if ( tri < 0) // CW (check left of v_1 -> v_0)
                    return false;
                else if (tri == 0) {
                    if (Triangulator.pointCompare(vertex0,vertex1)<0) {
                        if (Triangulator.pointCompare(check,vertex0)<0 || Triangulator.pointCompare(check,vertex1)>0)
                            return false;
                    } else {
                        if (Triangulator.pointCompare(check,vertex1)<0 || Triangulator.pointCompare(check,vertex0)>0)
                            return false;
                    }
                }
            }
            if ((Triangulator.pointCompare(vertex2,check)!=0)  &&  (Triangulator.pointCompare(vertex1,vertex2)!=0)) {
                tri = orientation(vertex1,vertex2,check);
                if ( tri < 0) // CW (check right of v_1 -> v_2)
                    return false;
                else if (tri == 0) {
                    if (Triangulator.pointCompare(vertex1,vertex2)<0) {
                        if (Triangulator.pointCompare(check,vertex1)<0 || Triangulator.pointCompare(check,vertex2)>0)
                            return false;
                    } else {
                        if (Triangulator.pointCompare(check,vertex2)<0 || Triangulator.pointCompare(check,vertex1)>0)
                            return false;
                    }
                }
            }
        } else {
            if (orientation(vertex0,vertex1,check)<=0 && orientation(vertex1,vertex2,check)<0)
                return false;
        }
        return true;
    }
}