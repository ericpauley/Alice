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

public class ImageIO {
	private static final String[] s_codecNames = { "png", "jpeg", "tiff", "bmp", "gif" };
	private static final String[] s_pngExtensions = { "png" };
	private static final String[] s_jpegExtensions = { "jpeg", "jpg" };
	private static final String[] s_tiffExtensions = { "tiff", "tif" };
	private static final String[] s_bmpExtensions = { "bmp" };
	private static final String[] s_gifExtensions = { "gif" };
	public static String[] getCodecNames() {
		return s_codecNames;
	}
	public static String[] getExtensionsForCodec( String codecName ) {
		if( codecName.equals( "png" ) ) {
			return s_pngExtensions;
		} else if( codecName.equals( "jpeg" ) ) {
			return s_jpegExtensions;
		} else if( codecName.equals( "tiff" ) ) {
			return s_tiffExtensions;
		} else if( codecName.equals( "bmp" ) ) {
			return s_bmpExtensions;
		} else if( codecName.equals( "gif" ) ) {
			return s_gifExtensions;
		} else {
			return null;
		}
	}
	public static String mapExtensionToCodecName( String extension ) {
		String[] codecNames = ImageIO.getCodecNames();
		for( int i=0; i<codecNames.length; i++ ) {
			String[] extensions = getExtensionsForCodec( codecNames[i] );
			for( int j=0; j<extensions.length; j++ ) {
				if( extensions[j].equalsIgnoreCase( extension ) ) {
					return codecNames[i];
				}
			}
		}
		return null;
	}


    public static java.awt.Image load( String codecName, java.io.InputStream inputStream ) throws java.io.IOException {
        return load( codecName, inputStream, null );
    }
    public static java.awt.Image load( String codecName, java.io.InputStream inputStream, edu.cmu.cs.stage3.image.codec.ImageDecodeParam imageDecodeParam ) throws java.io.IOException {
		java.io.BufferedInputStream bufferedInputStream;
		if( inputStream instanceof java.io.BufferedInputStream ) {
			bufferedInputStream = (java.io.BufferedInputStream)inputStream;
		} else {
			bufferedInputStream = new java.io.BufferedInputStream( inputStream );
		}
		edu.cmu.cs.stage3.image.codec.ImageDecoder imageDecoder = edu.cmu.cs.stage3.image.codec.ImageCodec.createImageDecoder( codecName, bufferedInputStream, imageDecodeParam );
		java.awt.image.RenderedImage renderedImage = imageDecoder.decodeAsRenderedImage();

		if( renderedImage instanceof java.awt.Image ) {
			return (java.awt.Image)renderedImage;
		} else {
			java.awt.image.Raster raster = renderedImage.getData();
			java.awt.image.ColorModel colorModel = renderedImage.getColorModel();
			java.util.Hashtable properties = null;
			String[] propertyNames = renderedImage.getPropertyNames();
			if( propertyNames!=null ) {
				properties = new java.util.Hashtable();
				for( int i=0; i<propertyNames.length; i++ ) {
					String propertyName = propertyNames[i];
					properties.put( propertyName, renderedImage.getProperty( propertyName ) );
				}
			}
			java.awt.image.WritableRaster writableRaster;
			if( raster instanceof java.awt.image.WritableRaster ) {
				writableRaster = (java.awt.image.WritableRaster)raster;
			} else {
				writableRaster = raster.createCompatibleWritableRaster();
			}
			java.awt.image.BufferedImage bufferedImage = new java.awt.image.BufferedImage( renderedImage.getColorModel(), writableRaster, colorModel.isAlphaPremultiplied(), properties );
			return bufferedImage;
		}
	}
    public static void store( String codecName, java.io.OutputStream outputStream, java.awt.Image image ) throws InterruptedException, java.io.IOException {
        store( codecName, outputStream, image, null );
    }
    public static void store( String codecName, java.io.OutputStream outputStream, java.awt.Image image, edu.cmu.cs.stage3.image.codec.ImageEncodeParam imageEncodeParam ) throws InterruptedException, java.io.IOException {
		int width = ImageUtilities.getWidth( image ); 
		int height = ImageUtilities.getHeight( image ); 

		java.awt.image.RenderedImage renderedImage;

		if( codecName.equals( "jpeg" ) ) {
			java.awt.Image originalImage = image;
			image = new java.awt.image.BufferedImage( width, height, java.awt.image.BufferedImage.TYPE_3BYTE_BGR );
			java.awt.Graphics g = image.getGraphics();
			g.drawImage( originalImage, 0, 0, new java.awt.image.ImageObserver() {
				public boolean imageUpdate( java.awt.Image image, int infoflags, int x, int y, int width, int height ) {
					return true;
				}
			} );
			//todo: does dispose ensure the image is finished drawing?
			g.dispose();
		}
		if( image instanceof java.awt.image.RenderedImage ) {
			renderedImage = (java.awt.image.RenderedImage)image;
		} else {
			int[] pixels = ImageUtilities.getPixels( image, width, height );
			java.awt.image.BufferedImage bufferedImage = new java.awt.image.BufferedImage( width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB );
			bufferedImage.setRGB( 0, 0, width, height, pixels, 0, width );
			renderedImage = bufferedImage;
		}
		if( imageEncodeParam == null ) {
			if( codecName.equals( "png" ) ) {
				imageEncodeParam = edu.cmu.cs.stage3.image.codec.PNGEncodeParam.getDefaultEncodeParam( renderedImage );
			}
		}
		java.io.BufferedOutputStream bufferedOutputStream;
		if( outputStream instanceof java.io.BufferedOutputStream ) {
			bufferedOutputStream = (java.io.BufferedOutputStream)outputStream;
		} else {
			bufferedOutputStream = new java.io.BufferedOutputStream( outputStream );
		}

		edu.cmu.cs.stage3.image.codec.ImageEncoder imageEncoder = edu.cmu.cs.stage3.image.codec.ImageCodec.createImageEncoder( codecName, bufferedOutputStream, imageEncodeParam );
		imageEncoder.encode( renderedImage );
		bufferedOutputStream.flush();
	}
}

