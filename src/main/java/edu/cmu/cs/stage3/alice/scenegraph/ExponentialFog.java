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
 * exponential fog affects visual elements based on their distance from a camera.
 *
 * <pre>
 *    z = distance from camera
 *
 *
 *                1
 *    f = ------------------
 *          ( density * z )
 *        e
 *
 *
 * </pre>
 * @author Dennis Cosgrove
 */
public class ExponentialFog extends Fog {
	public static final Property DENSITY_PROPERTY = new Property( ExponentialFog.class, "DENSITY" );
	private double m_density = 1;
	/**
	 * @see #setDensity
	 */
	public double getDensity() {
		return m_density;
	}
	/**
	 * sets the density property.<br>
	 *
	 * @param double (default: 1)
	 * @see #getDensity
	 */
	public void setDensity( double density ) {
		if( m_density != density ) {
			m_density = density;
			onPropertyChange( DENSITY_PROPERTY );
		}
	}

}
