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

public abstract class BasisMatrixCubic implements Cubic {
	private javax.vecmath.Matrix4d m_h;
	private javax.vecmath.Vector4d m_g;
	protected BasisMatrixCubic( javax.vecmath.Matrix4d h, javax.vecmath.Vector4d g ) {
		m_h = h;
		m_g = g;
	}
	public double evaluate( double t ) {
		double ttt = t*t*t;
		double tt = t*t;
		return ( ttt*m_h.m00 + tt*m_h.m10 + t*m_h.m20 + m_h.m30 ) * m_g.x +
			   ( ttt*m_h.m01 + tt*m_h.m11 + t*m_h.m21 + m_h.m31 ) * m_g.y +
			   ( ttt*m_h.m02 + tt*m_h.m12 + t*m_h.m22 + m_h.m32 ) * m_g.z +
			   ( ttt*m_h.m03 + tt*m_h.m13 + t*m_h.m23 + m_h.m33 ) * m_g.w;
	}
    public double evaluateDerivative( double t ) {
        double tt3 = t*t*3;
        double t2 = t*2;
		return ( tt3*m_h.m00 + t2*m_h.m10 + m_h.m20 ) * m_g.x +
			   ( tt3*m_h.m01 + t2*m_h.m11 + m_h.m21 ) * m_g.y +
			   ( tt3*m_h.m02 + t2*m_h.m12 + m_h.m22 ) * m_g.z +
			   ( tt3*m_h.m03 + t2*m_h.m13 + m_h.m23 ) * m_g.w;
    }
}
