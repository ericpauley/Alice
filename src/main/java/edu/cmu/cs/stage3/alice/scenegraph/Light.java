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
public abstract class Light extends Affector {
	public static final Property COLOR_PROPERTY = new Property( Light.class, "COLOR" );
	public static final Property BRIGHTNESS_PROPERTY = new Property( Light.class, "BRIGHTNESS" );
	public static final Property RANGE_PROPERTY = new Property( Light.class, "RANGE" );
	private edu.cmu.cs.stage3.alice.scenegraph.Color m_color = new edu.cmu.cs.stage3.alice.scenegraph.Color( 1,1,1,1 );
	private double m_brightness = 1;
	private double m_range = 256;

	/**
	 * @see #setColor
	 */
	public edu.cmu.cs.stage3.alice.scenegraph.Color getColor() {
		return m_color;
	}
	/**
	 * sets the color property.<br>
	 * visual elements are illuminated by this color.<br>
	 *
	 * @param color (default: { 1, 1, 1 })
	 * @see #getColor
	 */
	public void setColor( edu.cmu.cs.stage3.alice.scenegraph.Color color ) {
		if( notequal( m_color, color ) ) {
			m_color = color;
			onPropertyChange( COLOR_PROPERTY );
		}
	}

	/**
	 * @see #setBrightness
	 */
	public double getBrightness() {
		return m_brightness;
	}
	/**
	 * sets the brightness property.<br>
	 * scaling factor applied to color property
	 *
	 * @param brightness (default: 1)
	 * @see #getBrightness
	 */
	public void setBrightness( double brightness ) {
		if( m_brightness != brightness ) {
			m_brightness = brightness;
			onPropertyChange( BRIGHTNESS_PROPERTY );
		}
	}

	/**
	 * @see #setRange
	 */
	public double getRange() {
		return m_range;
	}
	/**
	 * sets the range property.<br>
	 * defines a radius within which visual elements are illuminated.<br>
	 *
	 * @param range (default: 256)
	 * @see #getRange
	 */
	public void setRange( double range ) {
		if( m_range != range ) {
			m_range = range;
			onPropertyChange( RANGE_PROPERTY );
		}
	}
}
