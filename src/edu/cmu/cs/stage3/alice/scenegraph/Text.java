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
public class Text extends Geometry {
	public static final Property TEXT_PROPERTY = new Property( Text.class, "TEXT" );
	public static final Property FONT_PROPERTY = new Property( Text.class, "FONT" );
	public static final Property EXTRUSION_PROPERTY = new Property( Text.class, "EXTRUSION" );
	private String m_text = null;
	private java.awt.Font m_font = null;
	private javax.vecmath.Vector3d m_extrusion = new javax.vecmath.Vector3d( 0, 0, 1 );

	public String getText() {
		return m_text;
	}
	public void setText( String text ) {
		if( notequal( m_text, text ) ) {
			m_text = text;
			onPropertyChange( TEXT_PROPERTY );
		}
	}
	public java.awt.Font getFont() {
		return m_font;
	}
	public void setFont( java.awt.Font font ) {
		if( notequal( m_font, font ) ) {
			m_font = font;
			onPropertyChange( FONT_PROPERTY );
		}
	}
	//todo javax.vecmath.Vector3d->spline
	public javax.vecmath.Vector3d getExtrusion() {
		return m_extrusion;
	}
	public void setExtrusion( javax.vecmath.Vector3d extrusion ) {
		if( notequal( m_extrusion, extrusion ) ) {
			m_extrusion = extrusion;
			onPropertyChange( EXTRUSION_PROPERTY );
		}
	}
	
	protected void updateBoundingBox() {
		//todo
	}
	
	protected void updateBoundingSphere() {
		//todo
	}
	
	public void transform( javax.vecmath.Matrix4d trans ) {
		//todo
	}
}
