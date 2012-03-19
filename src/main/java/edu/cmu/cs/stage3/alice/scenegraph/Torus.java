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
public class Torus extends Shape {
	public static final Property INNER_RADIUS_PROPERTY = new Property(Torus.class, "INNER_RADIUS");
	public static final Property OUTER_RADIUS_PROPERTY = new Property(Torus.class, "OUTER_RADIUS");

	private double m_innerRadius = 1;
	private double m_outerRadius = 2;

	public double getInnerRadius() {
		return m_innerRadius;
	}
	public void setInnerRadius(double innerRadius) {
		if (m_innerRadius != innerRadius) {
			m_innerRadius = innerRadius;
			onPropertyChange(INNER_RADIUS_PROPERTY);
			onBoundsChange();
		}
	}

	public double getOuterRadius() {
		return m_outerRadius;
	}
	public void setOuterRadius(double outerRadius) {
		if (m_outerRadius != outerRadius) {
			m_outerRadius = outerRadius;
			onPropertyChange(OUTER_RADIUS_PROPERTY);
			onBoundsChange();
		}
	}

	@Override
	protected void updateBoundingBox() {
		// todo
	}

	@Override
	protected void updateBoundingSphere() {
		// todo
	}
}
