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

public abstract class BasisMatrixQuadratic implements Quadratic {
	private javax.vecmath.Matrix3d m_h;
	protected javax.vecmath.Vector3d m_g;  //todo: make private?
	protected BasisMatrixQuadratic( javax.vecmath.Matrix3d h, javax.vecmath.Vector3d g ) {
		m_h = h;
		m_g = g;
	}
	public double evaluate( double t ) {
		double tt = t*t;
		return ( tt*m_h.m00 + t*m_h.m10 + m_h.m20 ) * m_g.x +
			   ( tt*m_h.m01 + t*m_h.m11 + m_h.m21 ) * m_g.y +
			   ( tt*m_h.m02 + t*m_h.m12 + m_h.m22 ) * m_g.z;
	}
    public double evaluateDerivative( double t ) {
        double t2 = t*2;
		return ( t2*m_h.m00 + m_h.m10 ) * m_g.x +
			   ( t2*m_h.m01 + m_h.m11 ) * m_g.y +
			   ( t2*m_h.m02 + m_h.m12 ) * m_g.z;
    }
}
