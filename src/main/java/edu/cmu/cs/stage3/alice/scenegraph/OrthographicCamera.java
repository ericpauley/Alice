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

package edu.cmu.cs.stage3.alice.scenegraph;

/**
 * @author Dennis Cosgrove
 */
public class OrthographicCamera extends Camera {
	public static final Property PLANE_PROPERTY = new Property(OrthographicCamera.class, "PLANE");
	private double[] m_plane = {-1, -1, 1, 1};

	/**
	 * @see #setPlane
	 */
	public double[] getPlane() {
		return m_plane;
	}
	/**
	 * sets the plane property.<br>
	 * the plane is defined { minX, minY, maxX, maxY } perpendicular to the
	 * forward vector.<br>
	 * 
	 * @param double (default: { -1, -1, 1, 1 })
	 * @see #getPlane
	 */
	public void setPlane(double[] plane) {
		if (notequal(m_plane, plane)) {
			m_plane = plane;
			onPropertyChange(PLANE_PROPERTY);
		}
	}
	public void setPlane(double minX, double minY, double maxX, double maxY) {
		setPlane(new double[]{minX, minY, maxX, maxY});
	}

	// todo

	@Override
	public javax.vecmath.Matrix4d getProjection() {
		javax.vecmath.Matrix4d m = new javax.vecmath.Matrix4d();
		return m;
	}

}