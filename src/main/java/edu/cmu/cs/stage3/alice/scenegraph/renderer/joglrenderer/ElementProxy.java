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

package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

abstract class ElementProxy extends edu.cmu.cs.stage3.alice.scenegraph.renderer.AbstractProxy {
	protected static void copy(float[] dst, edu.cmu.cs.stage3.alice.scenegraph.Color src) {
		dst[0] = src.getRed();
		dst[1] = src.getGreen();
		dst[2] = src.getBlue();
		if (dst.length > 3) {
			dst[3] = src.getAlpha();
		}
	}
	// todo: are the negations appropriate for projection camera?
	protected static void copy(double[] dst, javax.vecmath.Matrix4d src) {
		dst[0] = (float) src.m00;
		dst[1] = (float) src.m01;
		dst[2] = -(float) src.m02;
		dst[3] = (float) src.m03;

		dst[4] = (float) src.m10;
		dst[5] = (float) src.m11;
		dst[6] = -(float) src.m12;
		dst[7] = (float) src.m13;

		dst[8] = -(float) src.m20;
		dst[9] = -(float) src.m21;
		dst[10] = (float) src.m22;
		dst[11] = -(float) src.m23;

		dst[12] = (float) src.m30;
		dst[13] = (float) src.m31;
		dst[14] = -(float) src.m32;
		dst[15] = (float) src.m33;
	}
	protected static void copy(double[] dst, javax.vecmath.Matrix3d src) {
		dst[0] = (float) src.m00;
		dst[1] = (float) src.m01;
		dst[2] = -(float) src.m02;
		dst[3] = 0;

		dst[4] = (float) src.m10;
		dst[5] = (float) src.m11;
		dst[6] = -(float) src.m12;
		dst[7] = 0;

		dst[8] = -(float) src.m20;
		dst[9] = -(float) src.m21;
		dst[10] = (float) src.m22;
		dst[11] = 0;

		dst[12] = 0;
		dst[13] = 0;
		dst[14] = 0;
		dst[15] = 1;
	}

	@Override
	public void initialize(edu.cmu.cs.stage3.alice.scenegraph.Element sgElement, edu.cmu.cs.stage3.alice.scenegraph.renderer.AbstractProxyRenderer renderer) {
		super.initialize(sgElement, renderer);
		// System.err.println( sgElement );
	}

	@Override
	protected void changed(edu.cmu.cs.stage3.alice.scenegraph.Property property, Object value) {
		if (property == edu.cmu.cs.stage3.alice.scenegraph.Element.NAME_PROPERTY) {} else if (property == edu.cmu.cs.stage3.alice.scenegraph.Element.BONUS_PROPERTY) {} else {
			edu.cmu.cs.stage3.alice.scenegraph.Element.warnln("unhandled property: " + property + " " + value);
		}
	}
}
