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

package edu.cmu.cs.stage3.alice.core.util;

public class Polynomial {
    public static void evaluatePolynomial( edu.cmu.cs.stage3.math.Polynomial xPolynomial, edu.cmu.cs.stage3.math.Polynomial yPolynomial, double z, javax.vecmath.Point3d[] positions, javax.vecmath.Vector3d[] normals ) {
        if( positions.length != normals.length ) {
            throw new RuntimeException();
        }
        double dt = 1.0/(positions.length-1);
        double t = 0;
        for( int i=0; i<positions.length; i++ ) {
            double xt = xPolynomial.evaluate( t );
            double yt = yPolynomial.evaluate( t );

            if( positions[ i ] == null ) {
                positions[ i ] = new javax.vecmath.Point3d();
            }
            positions[ i ].x = xt;
            positions[ i ].y = yt;
            positions[ i ].z = z;

            double dxdt = xPolynomial.evaluateDerivative( t );
            double dydt = yPolynomial.evaluateDerivative( t );
            if( normals[ i ] == null ) {
                normals[ i ] = new javax.vecmath.Vector3d();
            }
            normals[ i ].x = -dydt;
            normals[ i ].y = dxdt;
            normals[ i ].z = 0;

            t += dt;
        }
    }

    public static void evaluateBezierQuadratic( javax.vecmath.Point2d p0, javax.vecmath.Point2d p1, javax.vecmath.Point2d p2, double z, javax.vecmath.Point3d[] positions, javax.vecmath.Vector3d[] normals ) {
        evaluatePolynomial( new edu.cmu.cs.stage3.math.BezierQuadratic( p0.x, p1.x, p2.x ), new edu.cmu.cs.stage3.math.BezierQuadratic( p0.y, p1.y, p2.y ), z, positions, normals );
    }
    public static void evaluateBezierCubic( javax.vecmath.Point2d p0, javax.vecmath.Point2d p1, javax.vecmath.Point2d p2, javax.vecmath.Point2d p3, double z, javax.vecmath.Point3d[] positions, javax.vecmath.Vector3d[] normals ) {
        evaluatePolynomial( new edu.cmu.cs.stage3.math.BezierCubic( p0.x, p1.x, p2.x, p3.x ), new edu.cmu.cs.stage3.math.BezierCubic( p0.y, p1.y, p2.y, p3.y ), z, positions, normals );
    }
}