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
 * linear fog affects visual elements based on their distance from a camera.
 *
 * <pre>
 *    z = distance from camera
 *    near = near distance
 *    far = far distance
 *
 *         far - z
 *    f = ----------
 *        far - near
 * </pre>
 * @author Dennis Cosgrove
 */
public class LinearFog extends Fog {
	public static final Property NEAR_DISTANCE_PROPERTY = new Property( LinearFog.class, "NEAR_DISTANCE" );
	public static final Property FAR_DISTANCE_PROPERTY = new Property( LinearFog.class, "FAR_DISTANCE" );
	private double m_nearDistance = 1;
	private double m_farDistance = 256;
	/**
	 * @see #setNearDistance
	 */
	public double getNearDistance() {
		return m_nearDistance;
	}
	/**
	 * sets the near distance property.<br>
	 *
	 * @param double (default: 1)
	 * @see #getNearDistance
	 * @see #setFarDistance
	 */
	public void setNearDistance( double nearDistance ) {
		if( m_nearDistance != nearDistance ) {
			m_nearDistance = nearDistance;
			onPropertyChange( NEAR_DISTANCE_PROPERTY );
		}
	}
	/**
	 * @see #setFarDistance
	 */
	public double getFarDistance() {
		return m_farDistance;
	}
	/**
	 * sets the far distance property.<br>
	 *
	 * @param double (default: 256)
	 * @see #getFarDistance
	 * @see #setNearDistance
	 */
	public void setFarDistance( double farDistance ) {
		if( m_farDistance != farDistance ) {
			m_farDistance = farDistance;
			onPropertyChange( FAR_DISTANCE_PROPERTY );
		}
	}

}
