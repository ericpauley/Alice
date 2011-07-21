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
public class TextureMap extends Element {
	public static final Property IMAGE_PROPERTY = new Property( TextureMap.class, "IMAGE" );
	public static final Property FORMAT_PROPERTY = new Property( TextureMap.class, "FORMAT" );

	public static final int RGB = 1;
	public static final int ALPHA = 2;
	public static final int RGBA = RGB|ALPHA;
	public static final int LUMINANCE = 4;
	public static final int LUMINANCE_PLUS_ALPHA = LUMINANCE|ALPHA;

	private int m_format = RGB;
	private java.awt.Image m_image = null;
	private edu.cmu.cs.stage3.alice.scenegraph.renderer.RenderTarget m_renderTargetWithLatestImage = null;

	public java.awt.Image getImage() {
		if( m_renderTargetWithLatestImage != null ) {
			m_image = m_renderTargetWithLatestImage.getImage( this );
			//todo? onPropertyChange( IMAGE_PROPERTY )
		}
		return m_image;
	}
	public void setImage( java.awt.Image image ) {
		if( m_image != image ) {
			m_image = image;
			onPropertyChange( IMAGE_PROPERTY );
		}
		m_renderTargetWithLatestImage = null;
	}

	public void touchImage() {
		onPropertyChange( IMAGE_PROPERTY );
		m_renderTargetWithLatestImage = null;
	}

	public edu.cmu.cs.stage3.alice.scenegraph.renderer.RenderTarget getRenderTargetWithLatestImage() {
		return m_renderTargetWithLatestImage;
	}
	public void setRenderTargetWithLatestImage( edu.cmu.cs.stage3.alice.scenegraph.renderer.RenderTarget renderTargetWithLatestImage ) {
		m_renderTargetWithLatestImage = renderTargetWithLatestImage;
	}

	public int getFormat() {
		return m_format;
	}
	public void setFormat( int format ) {
		if( m_format != format ) {
			m_format = format;
			onPropertyChange( FORMAT_PROPERTY );
		}
	}
}
