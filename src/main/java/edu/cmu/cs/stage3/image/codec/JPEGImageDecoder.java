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

package edu.cmu.cs.stage3.image.codec;

/*
 * The contents of this file are subject to the  JAVA ADVANCED IMAGING
 * SAMPLE INPUT-OUTPUT CODECS AND WIDGET HANDLING SOURCE CODE  License
 * Version 1.0 (the "License"); You may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.sun.com/software/imaging/JAI/index.html
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 *
 * The Original Code is JAVA ADVANCED IMAGING SAMPLE INPUT-OUTPUT CODECS
 * AND WIDGET HANDLING SOURCE CODE.
 * The Initial Developer of the Original Code is: Sun Microsystems, Inc..
 * Portions created by: _______________________________________
 * are Copyright (C): _______________________________________
 * All Rights Reserved.
 * Contributor(s): _______________________________________
 */

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;

import com.sun.image.codec.jpeg.ImageFormatException;

/**
 */
public class JPEGImageDecoder extends ImageDecoderImpl {

	public JPEGImageDecoder(InputStream input, ImageDecodeParam param) {
		super(input, param);
	}

	@Override
	public RenderedImage decodeAsRenderedImage(int page) throws IOException {
		if (page != 0) {
			throw new IOException(JaiI18N.getString("JPEGImageDecoder0"));
		}
		return new JPEGImage(input);
	}
}

class JPEGImage extends SimpleRenderedImage {

	private BufferedImage image = null;

	/**
	 * Construct a JPEGmage.
	 * 
	 * @param stream
	 *            The JPEG InputStream.
	 */
	public JPEGImage(InputStream stream) {
		com.sun.image.codec.jpeg.JPEGImageDecoder decoder = com.sun.image.codec.jpeg.JPEGCodec.createJPEGDecoder(stream);
		try {
			// decodeAsBufferedImage performs default color conversions
			image = decoder.decodeAsBufferedImage();
		} catch (ImageFormatException e) {
			throw new RuntimeException(JaiI18N.getString("JPEGImageDecoder1"));
		} catch (IOException e) {
			throw new RuntimeException(JaiI18N.getString("JPEGImageDecoder2"));
		}

		minX = 0;
		minY = 0;
		tileWidth = width = image.getWidth();
		tileHeight = height = image.getHeight();

		// Force image to have a ComponentSampleModel
		// since SinglePixelPackedSampleModels are not working
		if (!(image.getSampleModel() instanceof ComponentSampleModel)) {
			int type = -1;
			int numBands = image.getSampleModel().getNumBands();
			if (numBands == 1) {
				type = BufferedImage.TYPE_BYTE_GRAY;
			} else if (numBands == 3) {
				type = BufferedImage.TYPE_3BYTE_BGR;
			} else if (numBands == 4) {
				type = BufferedImage.TYPE_4BYTE_ABGR;
			} else {
				throw new RuntimeException(JaiI18N.getString("JPEGImageDecoder3"));
			}

			BufferedImage bi = new BufferedImage(width, height, type);
			Graphics2D g = bi.createGraphics();
			g.drawRenderedImage(image, new AffineTransform());
			image = bi;
		}

		sampleModel = image.getSampleModel();
		colorModel = image.getColorModel();
	}

	@Override
	public synchronized Raster getTile(int tileX, int tileY) {
		if (tileX != 0 || tileY != 0) {
			throw new IllegalArgumentException(JaiI18N.getString("JPEGImageDecoder4"));
		}

		return image.getTile(0, 0);
	}
}
