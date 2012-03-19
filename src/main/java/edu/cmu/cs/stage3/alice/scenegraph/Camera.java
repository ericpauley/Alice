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
public abstract class Camera extends Component {
	public static final Property NEAR_CLIPPING_PLANE_DISTANCE_PROPERTY = new Property(Camera.class, "NEAR_CLIPPING_PLANE_DISTANCE");
	public static final Property FAR_CLIPPING_PLANE_DISTANCE_PROPERTY = new Property(Camera.class, "FAR_CLIPPING_PLANE_DISTANCE");
	public static final Property BACKGROUND_PROPERTY = new Property(Camera.class, "BACKGROUND");
	private double m_nearClippingPlaneDistance = 0.125;
	private double m_farClippingPlaneDistance = 256;
	private Background m_background = null;

	@Override
	protected void releasePass1() {
		if (m_background != null) {
			warnln("WARNING: released camera " + this + " still has background " + m_background + ".");
			setBackground(null);
		}
		super.releasePass1();
	}
	/**
	 * @see #setNearClippingPlaneDistance
	 */
	public double getNearClippingPlaneDistance() {
		return m_nearClippingPlaneDistance;
	}
	/**
	 * sets the near clipping plane distance property.<br>
	 * the plane is defined to be perpendicular to the forward vector at the
	 * near clipping plane distance.<br>
	 * objects in front of this plane are clipped.<br>
	 * 
	 * @param double (default: 0.1)
	 * @see #getNearClippingPlaneDistance
	 * @see #setFarClippingPlaneDistance
	 */
	public void setNearClippingPlaneDistance(double nearClippingPlaneDistance) {
		if (m_nearClippingPlaneDistance != nearClippingPlaneDistance) {
			m_nearClippingPlaneDistance = nearClippingPlaneDistance;
			onPropertyChange(NEAR_CLIPPING_PLANE_DISTANCE_PROPERTY);
		}
	}
	/**
	 * @see #setFarClippingPlaneDistance
	 */
	public double getFarClippingPlaneDistance() {
		return m_farClippingPlaneDistance;
	}
	/**
	 * sets the far clipping plane distance property.<br>
	 * the plane is defined to be perpendicular to the forward vector at the far
	 * clipping plane distance.<br>
	 * objects behind this plane are clipped.<br>
	 * 
	 * @param double (default: 256)
	 * @see #getFarClippingPlaneDistance
	 * @see #setNearClippingPlaneDistance
	 */
	public void setFarClippingPlaneDistance(double farClippingPlaneDistance) {
		if (m_farClippingPlaneDistance != farClippingPlaneDistance) {
			m_farClippingPlaneDistance = farClippingPlaneDistance;
			onPropertyChange(FAR_CLIPPING_PLANE_DISTANCE_PROPERTY);
		}
	}

	/**
	 * @see #setBackground
	 */
	public Background getBackground() {
		return m_background;
	}
	/**
	 * 
	 * @param Background
	 *            (default: null)
	 * @see #getBackground
	 */
	public void setBackground(Background background) {
		if (m_background != background) {
			m_background = background;
			onPropertyChange(BACKGROUND_PROPERTY);
		}
	}

	public abstract javax.vecmath.Matrix4d getProjection();
}