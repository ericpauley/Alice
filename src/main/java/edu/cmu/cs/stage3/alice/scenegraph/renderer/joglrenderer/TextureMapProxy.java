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

package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

//import java.awt.Graphics;
//import java.awt.color.ColorSpace;
//import java.awt.image.BufferedImage;
//import java.awt.image.ColorModel;
//import java.awt.image.ComponentColorModel;
//import java.awt.image.DataBuffer;
//import java.awt.image.DataBufferByte;
//import java.awt.image.Raster;
//import java.awt.image.WritableRaster;
//import java.nio.ByteBuffer;
//import java.nio.ByteOrder;
//import java.util.Hashtable;

import java.awt.Transparency;

import edu.cmu.cs.stage3.alice.core.ExceptionWrapper;

class TextureMapProxy extends ElementProxy {
	private int m_width;
	private int m_height;
	private int m_width2;
	private int m_height2;
	private float m_uFactor;
	private float m_vFactor;
	private java.nio.ByteBuffer m_byteBuffer;
	private boolean m_isPrepared = false;

	private edu.cmu.cs.stage3.alice.scenegraph.TextureMap getSGTexture() {
		return (edu.cmu.cs.stage3.alice.scenegraph.TextureMap) getSceneGraphElement();
	}
	public boolean isPotentiallyAlphaBlended() {
		return false;// getSGTexture().getIsPotentiallyAlphaBlended() ;
	}
	public boolean isImageSet() {
		return getSGTexture().getImage() != null;
	}

	private static int getPowerOf2(int fold) {
		int ret = 2;
		while (ret < fold) {
			ret *= 2;
		}
		return ret;
	}

	// todo? could hang on to some data (like bufferedImage) in case someone is
	// painting into a texture map or something and width,height aren't changing
	public boolean prepareByteBufferIfNecessary() {
		if (m_isPrepared) {
			return false;
		} else {
			java.awt.Image image = getSGTexture().getImage();
			if (image != null) {
				boolean isPotentiallyAlphaBlended = isPotentiallyAlphaBlended();
				java.awt.image.ColorModel colorModel;
				if (isPotentiallyAlphaBlended) {
					colorModel = new java.awt.image.ComponentColorModel(java.awt.color.ColorSpace.getInstance(java.awt.color.ColorSpace.CS_sRGB), new int[]{8, 8, 8, 8}, true, false, Transparency.TRANSLUCENT, java.awt.image.DataBuffer.TYPE_BYTE);
				} else {
					colorModel = new java.awt.image.ComponentColorModel(java.awt.color.ColorSpace.getInstance(java.awt.color.ColorSpace.CS_sRGB), new int[]{8, 8, 8, 0}, false, false, Transparency.OPAQUE, java.awt.image.DataBuffer.TYPE_BYTE);
				}

				try {
					m_width = edu.cmu.cs.stage3.image.ImageUtilities.getWidth(image);
					m_height = edu.cmu.cs.stage3.image.ImageUtilities.getHeight(image);
				} catch (InterruptedException ie) {
					throw new ExceptionWrapper(ie, "");
				}
				m_width2 = getPowerOf2(m_width);
				m_height2 = getPowerOf2(m_height);

				m_uFactor = m_width / (float) m_width2;
				m_vFactor = m_height / (float) m_height2;

				int bands;
				if (isPotentiallyAlphaBlended) {
					bands = 4;
				} else {
					bands = 3;
				}
				// System.err.println( bands );
				java.awt.image.WritableRaster raster = java.awt.image.Raster.createInterleavedRaster(java.awt.image.DataBuffer.TYPE_BYTE, m_width2, m_height2, bands, null);
				java.awt.image.BufferedImage bufferedImage = new java.awt.image.BufferedImage(colorModel, raster, false, new java.util.Hashtable());

				java.awt.Graphics g = bufferedImage.getGraphics();
				g.drawImage(image, 0, 0, null);

				byte[] data = ((java.awt.image.DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();

				m_byteBuffer = java.nio.ByteBuffer.allocateDirect(data.length);
				m_byteBuffer.order(java.nio.ByteOrder.nativeOrder());
				m_byteBuffer.put(data, 0, data.length);

				m_byteBuffer.rewind();
			} else {
				m_byteBuffer = null;
				m_width = 0;
				m_height = 0;
			}
			m_isPrepared = true;
			return true;
		}
	}

	public java.nio.ByteBuffer getPixels() {
		return m_byteBuffer;
	}
	public int getWidth() {
		return m_width;
	}
	public int getWidthPowerOf2() {
		return m_width2;
	}

	public int getHeight() {
		return m_height;
	}
	public int getHeightPowerOf2() {
		return m_height2;
	}

	public float mapU(float u) {
		return u * m_uFactor;
	}
	public float mapV(float v) {
		return (1 - v) * m_vFactor;
	}

	@Override
	protected void changed(edu.cmu.cs.stage3.alice.scenegraph.Property property, Object value) {
		// delay to avoid potential extra work if image and
		// isPotentiallyAlphaBlended both change
		if (property == edu.cmu.cs.stage3.alice.scenegraph.TextureMap.IMAGE_PROPERTY) {
			m_isPrepared = false;
		} else if (property == edu.cmu.cs.stage3.alice.scenegraph.TextureMap.FORMAT_PROPERTY) {
			m_isPrepared = false;
		} else {
			super.changed(property, value);
		}
	}
	// private int m_width;
	// private int m_height;
	// private int m_width2;
	// private int m_height2;
	// private double m_uFactor;
	// private double m_vFactor;
	// private ByteBuffer m_byteBuffer;
	// private boolean m_isChanged;
	//
	// public boolean isChanged() {
	// return m_isChanged;
	// }
	// public void setIsChanged( boolean isChanged ) {
	// m_isChanged = isChanged;
	// }
	//
	// public ByteBuffer getPixels() {
	// return m_byteBuffer;
	// }
	// public int getWidth() {
	// return m_width;
	// }
	// public int getWidthPowerOf2() {
	// return m_width2;
	// }
	//
	// public int getHeight() {
	// return m_height;
	// }
	// public int getHeightPowerOf2() {
	// return m_height2;
	// }
	//
	// private int get2Fold(int fold) {
	// int ret = 2;
	// while (ret < fold) {
	// ret *= 2;
	// }
	// return ret;
	// }
	//
	// public double mapU( double u ) {
	// return u * m_uFactor;
	// }
	// public double mapV( double v ) {
	// return (1-v)*m_vFactor;
	// }
	//
	// protected void changed( edu.cmu.cs.stage3.alice.scenegraph.Property
	// property, Object value ) {
	// if( property ==
	// edu.cmu.cs.stage3.alice.scenegraph.TextureMap.IMAGE_PROPERTY ) {
	// java.awt.image.BufferedImage bufferedImage =
	// (java.awt.image.BufferedImage)value;
	// if( bufferedImage != null ) {
	// //System.err.println( bufferedImage );
	// // ColorModel glAlphaColorModel = new
	// ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
	// // new int[] {8,8,8,8},
	// // true,
	// // false,
	// // ComponentColorModel.TRANSLUCENT,
	// // DataBuffer.TYPE_BYTE);
	//
	// ColorModel glColorModel = new
	// ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
	// new int[] {8,8,8,0},
	// false,
	// false,
	// ComponentColorModel.OPAQUE,
	// DataBuffer.TYPE_BYTE);
	//
	// WritableRaster raster;
	// BufferedImage texImage;
	//
	// m_width = bufferedImage.getWidth();
	// m_height = bufferedImage.getHeight();
	// m_width2 = get2Fold( m_width );
	// m_height2 = get2Fold( m_height );
	//
	// m_uFactor = m_width/(double)m_width2;
	// m_vFactor = m_height/(double)m_height2;
	//
	// // if (bufferedImage.getColorModel().hasAlpha()) {
	// // raster =
	// Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,m_width2,m_height2,4,null);
	// // texImage = new BufferedImage(glAlphaColorModel,raster,false,new
	// Hashtable());
	// // } else {
	// raster =
	// Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,m_width2,m_height2,3,null);
	// texImage = new BufferedImage(glColorModel,raster,false,new Hashtable());
	// // }
	//
	// Graphics g = texImage.getGraphics();
	// g.drawImage(bufferedImage,0,0,null);
	//
	// byte[] data = ((DataBufferByte)
	// texImage.getRaster().getDataBuffer()).getData();
	//
	// m_byteBuffer = ByteBuffer.allocateDirect(data.length);
	// m_byteBuffer.order(ByteOrder.nativeOrder());
	// m_byteBuffer.put(data, 0, data.length);
	//
	// // m_width = image.getWidth();
	// // m_height = image.getHeight();
	// // m_pixels = new int[ m_width * m_height ];
	// // for( int y=0; y<m_height; y++ ) {
	// // for( int x=0; x<m_width; x++ ) {
	// // m_pixels[ y*m_width + x ] = image.getRGB( x, m_height-y-1 );
	// // }
	// // }
	// } else {
	// m_byteBuffer = null;
	// m_width = 0;
	// m_height = 0;
	// }
	// m_isChanged = true;
	// } else if( property ==
	// edu.cmu.cs.stage3.alice.scenegraph.TextureMap.FORMAT_PROPERTY ) {
	// //todo
	// } else {
	// super.changed( property, value );
	// }
	// }
}
