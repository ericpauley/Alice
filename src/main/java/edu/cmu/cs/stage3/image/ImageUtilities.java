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

package edu.cmu.cs.stage3.image;

public class ImageUtilities {
	private static java.awt.MediaTracker s_mediaTracker = new java.awt.MediaTracker( new java.awt.Panel() );
	private static java.awt.image.ImageObserver s_imageObserver = new java.awt.image.ImageObserver() {
		public boolean imageUpdate( java.awt.Image image, int infoflags, int x, int y, int width, int height ) {
			return true;
		}
	};
	private static void waitForImage( java.awt.Image image ) throws InterruptedException {
		s_mediaTracker.addImage( image, 0 );
		try {
			s_mediaTracker.waitForID( 0 );
		} finally {
			s_mediaTracker.removeImage( image );
		}
	}
	public static int getWidth( java.awt.Image image ) throws InterruptedException {
		waitForImage( image );
		return image.getWidth( s_imageObserver );
	}
	public static int getHeight( java.awt.Image image ) throws InterruptedException {
		waitForImage( image );
		return image.getHeight( s_imageObserver );
	}
	public static int[] getPixels( java.awt.Image image, int width, int height ) throws InterruptedException {
		int[] pixels = new int[ width*height ];
		java.awt.image.PixelGrabber pg = new java.awt.image.PixelGrabber( image, 0, 0, width, height, pixels, 0, width );
		pg.grabPixels();
		if ((pg.getStatus() & java.awt.image.ImageObserver.ABORT) != 0) {
			throw new RuntimeException( "image fetch aborted or errored" );
		}
		return pixels;		
	}


//		final Object lock = new Object();
//		class WidthTracker implements java.awt.image.ImageObserver {
//			private int m_width = -1;
//			public int getWidth() {
//				return m_width;
//			}
//			public boolean imageUpdate( java.awt.Image image, int infoflags, int x, int y, int width, int height ) {
//				if( ( infoflags & java.awt.image.ImageObserver.WIDTH ) != 0 ) {
//					m_width = width;
//					synchronized( lock ) {
//						lock.notify();
//					}
//					return false;						
//				} else {
//					return true;
//				}
//			}
//		}
//		WidthTracker widthTracker = new WidthTracker();
//		synchronized( lock ) {
//			image.getWidth( widthTracker );
//			lock.wait();
//		}
//		return widthTracker.getWidth();
}

