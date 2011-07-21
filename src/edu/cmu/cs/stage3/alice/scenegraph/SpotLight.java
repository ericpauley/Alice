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
 * a spot light emits a cone of light; behaving like a point light within its inner beam angle; and falling off exponentially between its inner and outer beam angles.<br>
 * useful in simulating a luxo lamp.<br>
 *
 * @author Dennis Cosgrove
 */
public class SpotLight extends PointLight {
	public static final Property INNER_BEAM_ANGLE_PROPERTY = new Property( SpotLight.class, "INNER_BEAM_ANGLE" );
	public static final Property OUTER_BEAM_ANGLE_PROPERTY = new Property( SpotLight.class, "OUTER_BEAM_ANGLE" );
	public static final Property FALLOFF_PROPERTY = new Property( SpotLight.class, "FALLOFF" );
	private double m_innerBeamAngle = 0.4;
	private double m_outerBeamAngle = 0.5;
	private double m_falloff = 1;
	/**
	 * @see #setInnerBeamAngle
	 */
	public double getInnerBeamAngle() {
		return m_innerBeamAngle;
	}
	/**
	 * sets the inner beam angle property (geek term: umbra).<br>
	 *
	 * @param innerBeamAngle (default: 0.4; units: radians)
	 * @see #getInnerBeamAngle
	 */
	public void setInnerBeamAngle( double innerBeamAngle ) {
		if( m_innerBeamAngle != innerBeamAngle ) {
			m_innerBeamAngle = innerBeamAngle;
			onPropertyChange( INNER_BEAM_ANGLE_PROPERTY );
		}
	}
	/**
	 * @see #setOuterBeamAngle
	 */
	public double getOuterBeamAngle() {
		return m_outerBeamAngle;
	}
	/**
	 * sets the outer beam angle property (geek term: penumbra).<br>
	 *
	 * @param outerBeamAngle (default: 0.5; units: radians)
	 * @see #getOuterBeamAngle
	 */
	public void setOuterBeamAngle( double outerBeamAngle ) {
		if( m_outerBeamAngle != outerBeamAngle ) {
			m_outerBeamAngle = outerBeamAngle;
			onPropertyChange( OUTER_BEAM_ANGLE_PROPERTY );
		}
	}
	/**
	 * @see #setFalloff
	 */
	public double getFalloff() {
		return m_falloff;
	}
	/**
	 * sets the falloff property.<br>
	 *
	 * @param falloff (default: 1)
	 * @see #getFalloff
	 */
	public void setFalloff( double falloff ) {
		if( m_falloff != falloff ) {
			m_falloff = falloff;
			onPropertyChange( FALLOFF_PROPERTY );
		}
	}
}
