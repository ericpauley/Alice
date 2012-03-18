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
public class LightenDarkenFilter extends java.awt.image.RGBImageFilter {
	private int percent;

	/**
	 * positive percent value lightens, negative percent value darkens
	 */
	public static java.awt.Image createLightenedOrDarkenedImage( java.awt.Image i, int percent ) {
		LightenDarkenFilter filter = new LightenDarkenFilter( percent );
		java.awt.image.ImageProducer prod = new java.awt.image.FilteredImageSource( i.getSource(), filter );
		java.awt.Image filteredImage = java.awt.Toolkit.getDefaultToolkit().createImage( prod );

		return filteredImage;
	}

	/**
	 * positive percent value lightens, negative percent value darkens
	 */
	public LightenDarkenFilter( int p ) {
		percent = p;

		canFilterIndexColorModel = true;
	}

	
	public int filterRGB( int x, int y, int rgb ) {
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int b = (rgb >> 0) & 0xff;

		if( percent > 0 ) {
			r = 255 - ((255 - r)*(100 - percent)/100);
			g = 255 - ((255 - g)*(100 - percent)/100);
			b = 255 - ((255 - b)*(100 - percent)/100);
		} else {
			r = r*(100 + percent)/100;
			g = g*(100 + percent)/100;
			b = b*(100 + percent)/100;
		}

		if( r < 0 ) r = 0;
		if( g < 0 ) g = 0;
		if( b < 0 ) b = 0;

		if( r > 255 ) r = 255;
		if( g > 255 ) g = 255;
		if( b > 255 ) b = 255;

		return ( rgb & 0xff000000 ) | ( r << 16 ) | ( g << 8 ) | ( b << 0 );
	}
}