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
 * a point light emits rays in all directions.<br>
 * the position is inherent from absolute transformation of its container.<br>
 * useful in simulating a light bulb.<br>
 * <p>
 * when calculating a light's contribution to the illumination of a vertex, that light's color is multiplied a polynomial function of distance.<br>
 * this polynomial is controllable via constant, linear, and quadratic attenuation properties.<br>
 * <pre>
 *    d = distance from light to vertex position
 *    c = constant attenuation
 *    l = linear attenuation
 *    q = quadratic attenuation
 *
 *                              1
 *    attenuation factor = ------------
 *                         c + ld + qdd
 * </pre>
 * note: the default values of ( constant=1, linear=0, quadratic=0 ) reduce the attenuation factor to 1.
 *
 * @author Dennis Cosgrove
 */
public class PointLight extends Light {
	public static final Property CONSTANT_ATTENUATION_PROPERTY = new Property( PointLight.class, "CONSTANT_ATTENUATION" );
	public static final Property LINEAR_ATTENUATION_PROPERTY = new Property( PointLight.class, "LINEAR_ATTENUATION" );
	public static final Property QUADRATIC_ATTENUATION_PROPERTY = new Property( PointLight.class, "QUADRATIC_ATTENUATION" );
	private double m_constantAttenuation = 1;
	private double m_linearAttenuation = 0;
	private double m_quadraticAttenuation = 0;
	/**
	 * @see #setConstantAttenuation
	 */
	public double getConstantAttenuation() {
		return m_constantAttenuation;
	}
	/**
	 * sets the constant attenuation(kc) of the contribution factor ( 1 / ( kc + kl*d + kq*d*d ) ).
	 *
	 * @param constantAttenuation (default: 1)
	 * @see #getConstantAttenuation
	 * @see #setLinearAttenuation
	 * @see #setQuadraticAttenuation
	 */
	public void setConstantAttenuation( double constantAttenuation ) {
		if( m_constantAttenuation != constantAttenuation ) {
			m_constantAttenuation = constantAttenuation;
			onPropertyChange( CONSTANT_ATTENUATION_PROPERTY );
		}
	}
	/**
	 * @see #setLinearAttenuation
	 */
	public double getLinearAttenuation() {
		return m_linearAttenuation;
	}
	/**
	 * sets the linear attenuation(kl) of the contribution factor ( 1 / ( kc + kl*d + kq*d*d ) ).
	 *
	 * @param linearAttenuation (default: 0)
	 * @see #getLinearAttenuation
	 * @see #setConstantAttenuation
	 * @see #setQuadraticAttenuation
	 */
	public void setLinearAttenuation( double linearAttenuation ) {
		if( m_linearAttenuation != linearAttenuation ) {
			m_linearAttenuation = linearAttenuation;
			onPropertyChange( LINEAR_ATTENUATION_PROPERTY );
		}
	}
	/**
	 * @see #setQuadraticAttenuation
	 */
	public double getQuadraticAttenuation() {
		return m_quadraticAttenuation;
	}
	/**
	 * sets the quadratic attenuation(kq) of the contribution factor ( 1 / ( kc + kl*d + kq*d*d ) ).
	 *
	 * @param quadraticAttenuation (default: 0)
	 * @see #getQuadraticAttenuation
	 * @see #setConstantAttenuation
	 * @see #setLinearAttenuation
	 */
	public void setQuadraticAttenuation( double quadraticAttenuation ) {
		if( m_quadraticAttenuation != quadraticAttenuation ) {
			m_quadraticAttenuation = quadraticAttenuation;
			onPropertyChange( QUADRATIC_ATTENUATION_PROPERTY );
		}
	}
}
