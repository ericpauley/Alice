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

public class Plane implements Cloneable {
	private double m_a;
	private double m_b;
	private double m_c;
	private double m_d;
	public Plane() {
		this(0, 0, 0, 0);
	}
	public Plane(double a, double b, double c, double d) {
		m_a = a;
		m_b = b;
		m_c = c;
		m_d = d;
	}
	public Plane(double[] array) {
		this(array[0], array[1], array[2], array[3]);
	}
	public Plane(javax.vecmath.Vector3d position, javax.vecmath.Vector3d normal) {
		this(normal.x, normal.y, normal.z, -(normal.x * position.x + normal.y * position.y + normal.z * position.z));
	}
	public Plane(javax.vecmath.Vector3d v0, javax.vecmath.Vector3d v1, javax.vecmath.Vector3d v2) {
		this(v0, MathUtilities.normalizeV(MathUtilities.crossProduct(MathUtilities.normalizeV(MathUtilities.subtract(v2, v1)), MathUtilities.normalizeV(MathUtilities.subtract(v0, v1)))));
	}

	@Override
	public synchronized Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o != null && o instanceof Plane) {
			Plane plane = (Plane) o;
			return m_a == plane.m_a && m_b == plane.m_b && m_c == plane.m_c && m_d == plane.m_d;
		} else {
			return false;
		}
	}

	public double intersect(Ray ray) {
		javax.vecmath.Point3d p = ray.getOrigin();
		javax.vecmath.Vector3d d = ray.getDirection();

		double denom = m_a * d.x + m_b * d.y + m_c * d.z;
		if (denom == 0) {
			return Double.NaN;
		} else {
			double numer = m_a * p.x + m_b * p.y + m_c * p.z + m_d;
			return -numer / denom;
		}
	}

	@Override
	public String toString() {
		return "edu.cmu.cs.stage3.math.Plane[a=" + m_a + ",b=" + m_b + ",c=" + m_c + ",d=" + m_d + "]";
	}
	public static Plane valueOf(String s) {
		String[] markers = {"edu.cmu.cs.stage3.math.Plane[a=", ",b=", ",c=", ",d=", "]"};
		double[] values = new double[markers.length - 1];
		for (int i = 0; i < values.length; i++) {
			int begin = s.indexOf(markers[i]) + markers[i].length();
			int end = s.indexOf(markers[i + 1]);
			values[i] = Double.valueOf(s.substring(begin, end)).doubleValue();
		}
		return new Plane(values);
	}
}
