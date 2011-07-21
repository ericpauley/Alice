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
 
package edu.cmu.cs.stage3.alice.authoringtool.util;

/**
 * @author pratt
 */
public class HighlightingGlassPane extends javax.swing.JPanel {
	protected edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;
	protected java.awt.Component savedGlassPane;
	protected String highlightID;
	protected javax.swing.Timer blinkTimer;
	protected java.awt.Color highlightColor = java.awt.Color.red;
	protected MousePassThroughListener mousePassThroughListener = new MousePassThroughListener();

	public HighlightingGlassPane( edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool ) {
		this.authoringTool = authoringTool;

		setOpaque( false );
		addMouseListener( mousePassThroughListener );
		addMouseMotionListener( mousePassThroughListener );

		blinkTimer = new javax.swing.Timer( 10, new java.awt.event.ActionListener() {
			public void actionPerformed( java.awt.event.ActionEvent ev ) {
				java.awt.Rectangle r = HighlightingGlassPane.this.getHighlightRect();
				if( r != null ) {
					HighlightingGlassPane.this.highlightColor = new java.awt.Color( 1.0f, 0.0f, 0.0f, (float)(.5*Math.sin( System.currentTimeMillis()/400.0 ) + .5) );
					r.x -= 4;
					r.y -= 4;
					r.width += 8;
					r.height += 8;
					HighlightingGlassPane.this.repaint( r );
				}
			}
		} );
	}

	public void setHighlightID( String highlightID ) {
		boolean oldEnabled = blinkTimer.isRunning();
		setHighlightingEnabled(false);
		this.highlightID = highlightID;
		if (oldEnabled){
			setHighlightingEnabled(true);
		}
		
	}

	public void setHighlightingEnabled( boolean enabled ) {
		if( enabled ) {
			if( authoringTool.getJAliceFrame().getGlassPane() != this ) {
				savedGlassPane = authoringTool.getJAliceFrame().getGlassPane();
				authoringTool.setGlassPane( this );
				setVisible( true );
				blinkTimer.start();
				revalidate();
				repaint();
			}
		} else {
			if( authoringTool.getJAliceFrame().getGlassPane() != savedGlassPane ) {
				if (savedGlassPane == null){
					savedGlassPane = authoringTool.getJAliceFrame().getGlassPane();
				}
				else{
					authoringTool.setGlassPane( savedGlassPane );
				}
				blinkTimer.stop();
			}
		}
	}

	public java.awt.Rectangle getHighlightRect() {
		java.awt.Rectangle r = null;

		if( highlightID != null ) {
			try {
				if( ! authoringTool.isIDVisible( highlightID ) ) {
					authoringTool.makeIDVisible( highlightID );
				}

				r = authoringTool.getBoxForID( highlightID );
				if( (r != null) && (! r.isEmpty()) ) {
					r.x -= 2;
					r.y -= 2;
					r.width += 4;
					r.height += 4;
				}
			} catch( edu.cmu.cs.stage3.caitlin.stencilhelp.application.IDDoesNotExistException e ) {
				// do nothing
			}
		}

		return r;
	}

	
	protected void paintComponent( java.awt.Graphics g ) {
		super.paintComponent( g );
	
		java.awt.Rectangle r = getHighlightRect();
		if( (r != null) && (! r.isEmpty()) ) {
			g.setColor( highlightColor );
	
			for( int i = 0; i < 4; i++ ) {
				g.drawRect( r.x - i, r.y - i, r.width + 2*i, r.height + 2*i );
			}
		}
	}

	class MousePassThroughListener extends javax.swing.event.MouseInputAdapter {
		
		public void mouseMoved( java.awt.event.MouseEvent ev ) {
			redispatchMouseEvent( ev );
		}
		
		public void mouseDragged( java.awt.event.MouseEvent ev ) {
			redispatchMouseEvent( ev );
		}
		
		public void mouseClicked( java.awt.event.MouseEvent ev ) {
			redispatchMouseEvent( ev );
		}
		
		public void mouseEntered( java.awt.event.MouseEvent ev ) {
			redispatchMouseEvent( ev );
		}
		
		public void mouseExited( java.awt.event.MouseEvent ev ) {
			redispatchMouseEvent( ev );
		}
		
		public void mousePressed( java.awt.event.MouseEvent ev ) {
			redispatchMouseEvent( ev );
		}
		
		public void mouseReleased( java.awt.event.MouseEvent ev ) {
			redispatchMouseEvent( ev );
		}

		private void redispatchMouseEvent( java.awt.event.MouseEvent ev ) {
			HighlightingGlassPane.this.authoringTool.handleMouseEvent( ev );
		}
	}
}
