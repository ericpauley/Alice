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

package edu.cmu.cs.stage3.alice.core.bubble;

class SubText {
	public SubText( String text, java.awt.geom.Rectangle2D bound, int yOffset ) {
		m_text = text;
		m_bound = new java.awt.geom.Rectangle2D.Double( bound.getX(), bound.getY()+yOffset, bound.getWidth(), bound.getHeight() );
	}
	public String getText() {
		return m_text;
	}
	public java.awt.geom.Rectangle2D getBound() {
		return m_bound;
	}
	public java.awt.geom.Rectangle2D getSafeBound() {
		return new java.awt.geom.Rectangle2D.Double( m_bound.getX(), m_bound.getY(), m_bound.getWidth(), m_bound.getHeight() );
	}

	int getPixelX() {
		return (int)m_bound.getX();
	}
	int getPixelY() {
		return (int)m_bound.getY();
	}
	private String m_text;
	private java.awt.geom.Rectangle2D m_bound;

	
	public String toString() {
		return m_text + " " + m_bound.getX() + " " + m_bound.getY() + " " + m_bound.getWidth() + " " + m_bound.getHeight();
	}
}

public abstract class Bubble {
	private String m_text = null;
	private java.awt.Color m_backgroundColor = java.awt.Color.white;
	private java.awt.Color m_foregroundColor = java.awt.Color.black;
	private java.awt.Font m_font = null;
	private boolean m_isShowing = true;
	private edu.cmu.cs.stage3.alice.core.ReferenceFrame m_referenceFrame = null;
	private javax.vecmath.Vector3d m_offsetFromReferenceFrame = new javax.vecmath.Vector3d();

	protected java.util.Vector m_subTexts = new java.util.Vector();
	protected java.awt.Point m_pixelOffset = null;
	private java.awt.Point m_origin = new java.awt.Point();
	
	private int m_charsPerLine = 32;

	public String getText() {
		return m_text;
	}
	public void setText( String text ) {
		m_text = text;
		m_subTexts.clear();
	}
	public java.awt.Color getBackgroundColor() {
		return m_backgroundColor;
	}
	public void setBackgroundColor( java.awt.Color backgroundColor ) {
		m_backgroundColor = backgroundColor;
	}
	public java.awt.Color getForegroundColor() {
		return m_foregroundColor;
	}
	public void setForegroundColor( java.awt.Color foregroundColor ) {
		m_foregroundColor = foregroundColor;
	}
	public java.awt.Font getFont() {
		return m_font;
	}
	public void setFont( java.awt.Font font ) {
		m_font = font;
	}
	public void setCharactersPerLine( int charsPerLine ) {
		m_charsPerLine = charsPerLine;
	}
	public boolean isShowing() {
		return m_isShowing;
	}
	public void setIsShowing( boolean isShowing ) {
		m_isShowing = isShowing;
	}
	public void show() {
		setIsShowing( true );
	}
	public void hide() {
		setIsShowing( false );
	}
	public edu.cmu.cs.stage3.alice.core.ReferenceFrame getReferenceFrame() {
		return m_referenceFrame;
	}
	public void setReferenceFrame( edu.cmu.cs.stage3.alice.core.ReferenceFrame referenceFrame ) {
		m_referenceFrame = referenceFrame;
	}
	public javax.vecmath.Vector3d getOffsetFromReferenceFrame() {
		return m_offsetFromReferenceFrame;
	}
	public void setOffsetFromReferenceFrame( javax.vecmath.Vector3d offsetFromReferenceFrame ) {
		m_offsetFromReferenceFrame = offsetFromReferenceFrame;
	}

	protected static final int PAD_X = 10;
	protected static final int PAD_Y = 10;

	public java.awt.Point getPixelOffset() {
		return m_pixelOffset;
	}
	public void setPixelOffset( java.awt.Point pixelOffset ) {
		m_pixelOffset = pixelOffset;
	}

	protected java.awt.Point getOrigin() {
		return m_origin;
	}
	public java.awt.geom.Rectangle2D getTotalBound() {
		if( m_subTexts.size()>0 ) {
			SubText subText0 = (SubText)m_subTexts.elementAt( 0 );
			java.awt.geom.Rectangle2D totalBound = subText0.getSafeBound();
			for( int i=1; i<m_subTexts.size(); i++ ) {
				SubText subTextI = (SubText)m_subTexts.elementAt( i );
				java.awt.geom.Rectangle2D.union( totalBound, subTextI.getBound(), totalBound );
			}
			return totalBound;
		} else {
			return null;
		}
	}

	public void calculateBounds( edu.cmu.cs.stage3.alice.scenegraph.renderer.RenderTarget rt ) {
		java.awt.geom.Rectangle2D totalBound = getTotalBound();
		if( totalBound == null ) {
			if( m_text != null ) {
				if( m_font != null ) {
					int n = m_charsPerLine;
					m_subTexts.clear();
					java.awt.font.FontRenderContext frc = new java.awt.font.FontRenderContext( null, false, false );
					int offsetY = 0;
					int i = 0;
					while( i<m_text.length() ) {
						int limit;
						if( i+n > m_text.length() ) {
							limit = m_text.length();
						} else {
							limit = m_text.indexOf( ' ', i+n );
							if( limit == -1 ) {
								limit = m_text.length();
							}
						}
						String substring = m_text.substring( i, limit );
						java.awt.geom.Rectangle2D boundI = m_font.getStringBounds( substring, frc );
						m_subTexts.addElement( new SubText( substring, boundI, offsetY ) );
						offsetY += boundI.getHeight();
						i = limit + 1;
					}
				}
			}
		}
	}
	public void calculateOrigin( edu.cmu.cs.stage3.alice.scenegraph.renderer.RenderTarget rt ) {
		m_origin.x = 300;
		m_origin.y = 300;
		if( m_referenceFrame != null ) {
			edu.cmu.cs.stage3.alice.scenegraph.Camera[] sgCameras = rt.getCameras();
			if( sgCameras.length > 0 ) {
				edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera = sgCameras[ 0 ];
				edu.cmu.cs.stage3.alice.core.Camera camera = (edu.cmu.cs.stage3.alice.core.Camera)sgCamera.getBonus();
				if( camera != null && camera != m_referenceFrame ) {
					javax.vecmath.Vector3d xyzInCamera = m_referenceFrame.transformTo( m_offsetFromReferenceFrame, camera );
					javax.vecmath.Vector3d xyzInViewport = rt.transformFromCameraToViewport( xyzInCamera, sgCamera );
					m_origin.x = (int)xyzInViewport.x;
					m_origin.y = (int)xyzInViewport.y;
				}
			}
		}
	}

	protected abstract void paintBackground( java.awt.Graphics g );
	protected void paint( java.awt.Graphics g ) {
		if( m_isShowing ) {
			paintBackground( g );
			if( m_text != null ) {
				if( m_font != null ) {
					g.setFont( m_font );
				}
				g.setColor( m_foregroundColor );
				if( m_subTexts.size() > 0 ) {
					SubText subText0 = (SubText)m_subTexts.elementAt( 0 );
					int offsetX = m_pixelOffset.x;
					int offsetY = m_pixelOffset.y - (int)subText0.getBound().getY();
					for( int i=0; i<m_subTexts.size(); i++ ) {
						SubText subTextI = (SubText)m_subTexts.elementAt( i );
						java.awt.geom.Rectangle2D boundI = subTextI.getBound();
						int x = (int)( offsetX + boundI.getX() );
						int y = (int)( offsetY + boundI.getY() );
						//g.setColor( java.awt.Color.red );
						//g.drawRect( x, y, (int)boundI.getWidth(), (int)boundI.getHeight() );
						//g.setColor( m_foregroundColor );
						g.drawString( subTextI.getText(), x, y );
					}

				}
				//g.drawString( m_text, m_rect.x + PAD, m_rect.y + PAD + 16 );
			}
		}
	}
}