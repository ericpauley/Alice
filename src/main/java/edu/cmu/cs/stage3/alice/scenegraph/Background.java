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
public class Background extends Element {
	public static final Property COLOR_PROPERTY = new Property( Background.class, "COLOR" );
	public static final Property TEXTURE_MAP_PROPERTY = new Property( Background.class, "TEXTURE_MAP" );
	public static final Property TEXTURE_MAP_SOURCE_RECTANGLE_PROPERTY = new Property( Background.class, "TEXTURE_MAP_SOURCE_RECTANGLE" );
	private edu.cmu.cs.stage3.alice.scenegraph.Color m_color = new edu.cmu.cs.stage3.alice.scenegraph.Color( 0, 0, 1, 1 );
	private TextureMap m_textureMap = null;
	private java.awt.Rectangle m_textureMapSourceRectangle = null;

	/**
	 * @see #setColor
	 */
	public edu.cmu.cs.stage3.alice.scenegraph.Color getColor() {
		return m_color;
	}
	/**
	 * sets the color property.
	 * used when a camera clears its viewport.
	 * the scenegraph is then rendered "in front of" this color.
	 * this property is ignored if if the image is not null.
	 *
	 * @param color
	 * @see #getColor
	 * @see #setImage
	 */
	public void setColor( edu.cmu.cs.stage3.alice.scenegraph.Color color ) {
		if( notequal( m_color, color ) ) {
			m_color = color;
			onPropertyChange( COLOR_PROPERTY );
		}
	}
	/**
	 * @see #setTextureMap
	 */
	public TextureMap getTextureMap() {
		return m_textureMap;
	}
	/**
	 * sets the texture map property.
	 * used when a camera clears its viewport.
	 * the scenegraph is then rendered "in front of" this texture map.
	 * this property takes precedence over the color if it is not null.
	 *
	 * @param TextureMap
	 * @see #getTextureMap
	 * @see #setColor
	 */
	public void setTextureMap( TextureMap textureMap ) {
		if( notequal( m_textureMap, textureMap ) ) {
			m_textureMap = textureMap;
			onPropertyChange( TEXTURE_MAP_PROPERTY );
		}
	}

	/**
	 * @see #setTextureMapSourceRectangle
	 */
	public java.awt.Rectangle getTextureMapSourceRectangle() {
		return m_textureMapSourceRectangle;
	}
	/**
	 * sets the texture map source rectangle property.
	 *
	 * @param java.awt.Rectangle
	 * @see #getTextureMapSourceRectangle
	 * @see #setTextureMapSourceRectangle
	 * @see #setTextureMap
	 */
	public void setTextureMapSourceRectangle( java.awt.Rectangle textureMapSourceRectangle ) {
		if( notequal( m_textureMapSourceRectangle, textureMapSourceRectangle ) ) {
			m_textureMapSourceRectangle = textureMapSourceRectangle;
			onPropertyChange( TEXTURE_MAP_SOURCE_RECTANGLE_PROPERTY );
		}
	}
}
