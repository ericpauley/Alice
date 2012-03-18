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
public class Cylinder extends Shape {
	public static final Property BASE_RADIUS_PROPERTY = new Property( Cylinder.class, "BASE_RADIUS" );
	public static final Property TOP_RADIUS_PROPERTY = new Property( Cylinder.class, "TOP_RADIUS" );
	public static final Property HEIGHT_PROPERTY = new Property( Cylinder.class, "HEIGHT" );

	private double m_baseRadius = 1;
	private double m_topRadius = 1;
	private double m_height = 1;

	public double getBaseRadius() {
		return m_baseRadius;
	}
	public void setBaseRadius( double baseRadius ) {
		if( m_baseRadius != baseRadius ) {
			m_baseRadius = baseRadius;
			onPropertyChange( BASE_RADIUS_PROPERTY );
			onBoundsChange();
		}
	}

	public double getTopRadius() {
		return m_topRadius;
	}
	public void setTopRadius( double topRadius ) {
		if( m_topRadius != topRadius ) {
			m_topRadius = topRadius;
			onPropertyChange( TOP_RADIUS_PROPERTY );
			onBoundsChange();
		}
	}

	public double getHeight() {
		return m_height;
	}
	public void setHeight( double height ) {
		if( m_height != height ) {
			m_height = height;
			onPropertyChange( HEIGHT_PROPERTY );
			onBoundsChange();
		}
	}

	
	protected void updateBoundingBox() {
		double radius = Math.max( m_baseRadius, m_topRadius );
		m_boundingBox = new edu.cmu.cs.stage3.math.Box( -radius, 0, -radius,   radius, m_height, radius );
	}
	
	protected void updateBoundingSphere() {
		double halfHeight = m_height * 0.5;
		double radius = Math.max( halfHeight, Math.max( m_baseRadius, m_topRadius ) );
		m_boundingSphere = new edu.cmu.cs.stage3.math.Sphere( 0,halfHeight,0, radius );
	}
}
