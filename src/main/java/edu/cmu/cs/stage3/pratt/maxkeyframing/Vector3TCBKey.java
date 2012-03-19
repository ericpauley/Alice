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

package edu.cmu.cs.stage3.pratt.maxkeyframing;

/**
 * @author Jason Pratt
 */
public class Vector3TCBKey extends TCBKey {
	public Vector3TCBKey(double time, javax.vecmath.Vector3d value, double tension, double continuity, double bias) {
		super(time, new double[]{value.x, value.y, value.z}, tension, continuity, bias);
	}

	private javax.vecmath.Vector3d vSample = new javax.vecmath.Vector3d();

	@Override
	public Object createSample(double[] components) {
		vSample.x = components[0];
		vSample.y = components[1];
		vSample.z = components[2];
		return vSample;
		// return new edu.cmu.cs.stage3.math.Vector3( components[0],
		// components[1], components[2] );
	}

	public static Vector3TCBKey valueOf(String s) {
		java.util.StringTokenizer st = new java.util.StringTokenizer(s, " \t,[]");

		String className = st.nextToken(); // unused
		double time = Double.parseDouble(st.nextToken());
		javax.vecmath.Vector3d value = new javax.vecmath.Vector3d(Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken()));
		double tension = Double.parseDouble(st.nextToken());
		double continuity = Double.parseDouble(st.nextToken());
		double bias = Double.parseDouble(st.nextToken());

		return new Vector3TCBKey(time, value, tension, continuity, bias);
	}
}