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

package edu.cmu.cs.stage3.math;

public class BezierQuadratic extends BasisMatrixQuadratic  {
	private static final javax.vecmath.Matrix3d s_h = new javax.vecmath.Matrix3d( 1,-2,1,  -2,2,0,  1,0,0 );
	public BezierQuadratic( javax.vecmath.Vector3d g ) {
		super( s_h, g );
	}
	public BezierQuadratic( double g0, double g1, double g2 ) {
		this( new javax.vecmath.Vector3d( g0, g1, g2 ) );
	}
    //todo: optimize?
    //public double evaluate( double t ) {
    //    double b = m_g.z - 2*m_g.y + m_g.x;
    //    double c = 2*m_g.y - 2*m_g.x;
    //    double d = m_g.x;
    //    return b*t*t + c*t + d;
	//}
}
