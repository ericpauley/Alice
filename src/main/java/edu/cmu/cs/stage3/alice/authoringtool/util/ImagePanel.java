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
 * @author Jason Pratt
 */
public class ImagePanel extends javax.swing.JPanel {
	protected java.awt.Image image;
	protected java.awt.MediaTracker tracker;
	protected java.awt.Dimension size;

	public ImagePanel() {
		setOpaque( false );
		tracker = new java.awt.MediaTracker( this );
		size = new java.awt.Dimension();
	}

	public void setImage( java.awt.Image image ) {
		if( this.image != image ) {
			if( this.image != null ) {
				tracker.removeImage( this.image );
			}
			this.image = image;
			if( image != null ) {
				tracker.addImage( this.image, 0 );
			}
			try {
				tracker.waitForAll();
			} catch( InterruptedException e ) {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Interrupted while waiting for image to load.", e );
			}
			revalidate();
			repaint();
		}
	}

	
	protected void paintComponent( java.awt.Graphics g ) {
		if( image != null ) {
			g.drawImage( image, 0, 0, null );
		}
	}

	protected java.awt.Dimension getImageSize() {
		if( image != null ) {
			size.setSize( image.getWidth( null ), image.getHeight( null ) );
		} else {
			size.setSize( 0, 0 );
		}
		return size;
	}

	
	public java.awt.Dimension getMinimumSize() {
		return getImageSize();
	}
	
	public java.awt.Dimension getMaximumSize() {
		return getImageSize();
	}
	
	public java.awt.Dimension getPreferredSize() {
		return getImageSize();
	}
}