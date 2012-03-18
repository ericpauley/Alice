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

public class BezierCubic extends BasisMatrixCubic  {
	private static final javax.vecmath.Matrix4d s_h = new javax.vecmath.Matrix4d( -1,3,-3,1,   3,-6,3,0,  -3,3,0,0,   1,0,0,0 );
	public BezierCubic( javax.vecmath.Vector4d g ) {
		super( s_h, g );
	}
	public BezierCubic( double g0, double g1, double g2, double g3 ) {
		this( new javax.vecmath.Vector4d( g0, g1, g2, g3 ) );
	}
}
