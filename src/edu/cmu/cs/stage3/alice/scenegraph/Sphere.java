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
public class Sphere extends Shape {
	public static final Property RADIUS_PROPERTY = new Property( Sphere.class, "RADIUS" );

	private double m_radius = 1;

	public double getRadius() {
		return m_radius;
	}
	public void setRadius( double radius ) {
		if( m_radius != radius ) {
			m_radius = radius;
			onPropertyChange( RADIUS_PROPERTY );
			onBoundsChange();
		}
	}

	
	protected void updateBoundingBox() {
		m_boundingBox = new edu.cmu.cs.stage3.math.Box( -m_radius, -m_radius, -m_radius,   m_radius, m_radius, m_radius );
	}
	
	protected void updateBoundingSphere() {
		m_boundingSphere = new edu.cmu.cs.stage3.math.Sphere( 0,0,0, m_radius );
	}
}
