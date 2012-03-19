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
 * defined by a regualar pyramidal frustum
 * 
 * @author Dennis Cosgrove
 */
public class SymmetricPerspectiveCamera extends Camera {
	public static final Property VERTICAL_VIEWING_ANGLE_PROPERTY = new Property(SymmetricPerspectiveCamera.class, "VERTICAL_VIEWING_ANGLE");
	public static final Property HORIZONTAL_VIEWING_ANGLE_PROPERTY = new Property(SymmetricPerspectiveCamera.class, "HORIZONTAL_VIEWING_ANGLE");
	private double m_verticalViewingAngle = 0.5;
	private double m_horizontalViewingAngle = Double.NaN;

	/**
	 * @see #setHorizontalViewingAngle
	 */
	public double getHorizontalViewingAngle() {
		return m_horizontalViewingAngle;
	}
	/**
	 * sets the horizontal viewing angle property.<br>
	 * if the value is Double.NaN then it left to the renderer to decide.
	 * 
	 * @param double (default: Double.NaN)
	 * @see #getHorizontalViewingAngle
	 * @see #setVerticalViewingAngle
	 */
	public void setHorizontalViewingAngle(double horizontalViewingAngle) {
		if (m_horizontalViewingAngle != horizontalViewingAngle) {
			m_horizontalViewingAngle = horizontalViewingAngle;
			onPropertyChange(HORIZONTAL_VIEWING_ANGLE_PROPERTY);
		}
	}

	/**
	 * @see #setVerticalViewingAngle
	 */
	public double getVerticalViewingAngle() {
		return m_verticalViewingAngle;
	}
	/**
	 * sets the vertical viewing angle property.<br>
	 * if the value is Double.NaN then it left to the renderer to decide.
	 * 
	 * @param double (default: 0.5)
	 * @see #getVerticalViewingAngle
	 * @see #setHorizontalViewingAngle
	 */
	public void setVerticalViewingAngle(double verticalViewingAngle) {
		if (m_verticalViewingAngle != verticalViewingAngle) {
			m_verticalViewingAngle = verticalViewingAngle;
			onPropertyChange(VERTICAL_VIEWING_ANGLE_PROPERTY);
		}
	}

	// todo

	@Override
	public javax.vecmath.Matrix4d getProjection() {
		javax.vecmath.Matrix4d m = new javax.vecmath.Matrix4d();
		double w = 1 / Math.tan(getHorizontalViewingAngle() * 0.5);
		double h = 1 / Math.tan(getVerticalViewingAngle() * 0.5);
		double farPlane = getFarClippingPlaneDistance();
		double nearPlane = getNearClippingPlaneDistance();
		double Q = farPlane / (farPlane - nearPlane);
		m.m00 = w;
		m.m11 = h;
		m.m22 = Q;
		m.m32 = -Q * nearPlane;
		m.m23 = 1;
		return m;
	}

}