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

public class Vector4 extends javax.vecmath.Vector4d {
	public Vector4() {
	}
	public Vector4(double x, double y, double z, double w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	public Vector4(double[] v) {
		this(v[0], v[1], v[2], v[3]);
	}
	public Vector4(javax.vecmath.Tuple3d v, double w) {
		this(v.x, v.y, v.z, w);
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o != null && o instanceof Vector4) {
			Vector4 v = (Vector4) o;
			return x == v.x && y == v.y && z == v.z && w == v.w;
		} else {
			return false;
		}
	}
	public double[] getArray() {
		double[] a = {x, y, z, w};
		return a;
	}
	public void setArray(double[] a) {
		x = a[0];
		y = a[1];
		z = a[2];
		w = a[3];
	}
	public void set(Vector4 other) {
		x = other.x;
		y = other.y;
		z = other.z;
		w = other.w;
	}
	public boolean equals(Vector4 v) {
		return x == v.x && y == v.y && z == v.z && w == v.w;
	}
	public double getItem(int i) {
		switch (i) {
			case 0 :
				return x;
			case 1 :
				return y;
			case 2 :
				return z;
			case 3 :
				return w;
		}
		throw new IllegalArgumentException();
	}
	public void setItem(int i, double v) {
		switch (i) {
			case 0 :
				x = v;
				return;
			case 1 :
				y = v;
				return;
			case 2 :
				z = v;
				return;
			case 3 :
				w = v;
				return;
		}
		throw new IllegalArgumentException();
	}
	public static Vector4 negate(Vector4 v) {
		return new Vector4(-v.x, -v.y, -v.z, -v.w);
	}
	public static double dotProduct(Vector4 a, Vector4 b) {
		return a.x * b.x + a.y * b.y + a.z * b.z + a.w * b.w;
	}

	public static Vector4 multiply(javax.vecmath.Vector4d a, javax.vecmath.Matrix4d b) {
		Vector4 ab = new Vector4();
		ab.x = a.x * b.m00 + a.y * b.m10 + a.z * b.m20 + a.w * b.m30;
		ab.y = a.x * b.m01 + a.y * b.m11 + a.z * b.m21 + a.w * b.m31;
		ab.z = a.x * b.m02 + a.y * b.m12 + a.z * b.m22 + a.w * b.m32;
		ab.w = a.x * b.m03 + a.y * b.m13 + a.z * b.m23 + a.w * b.m33;
		return ab;
	}
	public static Vector4 multiply(javax.vecmath.Matrix4d a, javax.vecmath.Vector4d b) {
		Vector4 ab = new Vector4();
		ab.x = b.x * a.m00 + b.y * a.m01 + b.z * a.m02 + b.w * a.m03;
		ab.y = b.x * a.m10 + b.y * a.m11 + b.z * a.m12 + b.w * a.m13;
		ab.z = b.x * a.m20 + b.y * a.m21 + b.z * a.m22 + b.w * a.m23;
		ab.w = b.x * a.m30 + b.y * a.m31 + b.z * a.m32 + b.w * a.m33;
		return ab;
	}
	public void transform(javax.vecmath.Matrix4d m) {
		set(Vector4.multiply(this, m));
	}

	@Override
	public String toString() {
		return "edu.cmu.cs.stage3.math.Vector4[x=" + x + ",y=" + y + ",z=" + z + ",w=" + w + "]";
	}
	public static Vector4 valueOf(String s) {
		String[] markers = {"edu.cmu.cs.stage3.math.Vector4[x=", ",y=", ",z=", ",w=", "]"};
		double[] values = new double[markers.length - 1];
		for (int i = 0; i < values.length; i++) {
			int begin = s.indexOf(markers[i]) + markers[i].length();
			int end = s.indexOf(markers[i + 1]);
			values[i] = Double.valueOf(s.substring(begin, end)).doubleValue();
		}
		return new Vector4(values);
	}
}
