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
public class Box extends Shape {
	public static final Property WIDTH_PROPERTY = new Property(Box.class, "WIDTH");
	public static final Property HEIGHT_PROPERTY = new Property(Box.class, "HEIGHT");
	public static final Property DEPTH_PROPERTY = new Property(Box.class, "DEPTH");

	private double m_width = 1;
	private double m_height = 1;
	private double m_depth = 1;

	public double getWidth() {
		return m_width;
	}
	public void setWidth(double width) {
		if (m_width != width) {
			m_width = width;
			onPropertyChange(WIDTH_PROPERTY);
			onBoundsChange();
		}
	}

	public double getHeight() {
		return m_height;
	}
	public void setHeight(double height) {
		if (m_height != height) {
			m_height = height;
			onPropertyChange(HEIGHT_PROPERTY);
			onBoundsChange();
		}
	}

	public double getDepth() {
		return m_depth;
	}
	public void setDepth(double depth) {
		if (m_depth != depth) {
			m_depth = depth;
			onPropertyChange(DEPTH_PROPERTY);
			onBoundsChange();
		}
	}

	@Override
	protected void updateBoundingBox() {
		double halfWidth = m_width * 0.5;
		double halfHeight = m_height * 0.5;
		double halfDepth = m_depth * 0.5;
		m_boundingBox = new edu.cmu.cs.stage3.math.Box(-halfWidth, -halfHeight, -halfDepth, halfWidth, halfHeight, halfDepth);
	}

	@Override
	protected void updateBoundingSphere() {
		double halfWidth = m_width * 0.5;
		double halfHeight = m_height * 0.5;
		double halfDepth = m_depth * 0.5;
		m_boundingSphere = new edu.cmu.cs.stage3.math.Sphere(0, 0, 0, Math.max(Math.max(halfWidth, halfHeight), halfDepth));
	}
}
