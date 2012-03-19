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

import java.awt.image.RenderedImage;
import java.io.IOException;

/**
 * A baseline TIFF reader. The reader has some functionality in addition to the
 * baseline specifications for Bilevel images, for which the group 3 and group 4
 * decompression schemes have been implemented. Support for LZW decompression
 * has also been added. Support for Horizontal differencing predictor decoding
 * is also included, when used with LZW compression. However, this support is
 * limited to data with bitsPerSample value of 8. When reading in RGB images,
 * support for alpha and extraSamples being present has been added. Support for
 * reading in images with 16 bit samples has been added. Support for the
 * SampleFormat tag (signed samples as well as floating-point samples) has also
 * been added. In all other cases, support is limited to Baseline
 * specifications.
 */
public class TIFFImageDecoder extends ImageDecoderImpl {

	// All the TIFF tags that we care about
	public static final int TIFF_IMAGE_WIDTH = 256;
	public static final int TIFF_IMAGE_LENGTH = 257;
	public static final int TIFF_BITS_PER_SAMPLE = 258;
	public static final int TIFF_COMPRESSION = 259;
	public static final int TIFF_PHOTOMETRIC_INTERPRETATION = 262;
	public static final int TIFF_FILL_ORDER = 266;
	public static final int TIFF_STRIP_OFFSETS = 273;
	public static final int TIFF_SAMPLES_PER_PIXEL = 277;
	public static final int TIFF_ROWS_PER_STRIP = 278;
	public static final int TIFF_STRIP_BYTE_COUNTS = 279;
	public static final int TIFF_X_RESOLUTION = 282;
	public static final int TIFF_Y_RESOLUTION = 283;
	public static final int TIFF_T4_OPTIONS = 292;
	public static final int TIFF_T6_OPTIONS = 293;
	public static final int TIFF_RESOLUTION_UNIT = 296;
	public static final int TIFF_PREDICTOR = 317;
	public static final int TIFF_COLORMAP = 320;
	public static final int TIFF_TILE_WIDTH = 322;
	public static final int TIFF_TILE_LENGTH = 323;
	public static final int TIFF_TILE_OFFSETS = 324;
	public static final int TIFF_TILE_BYTE_COUNTS = 325;
	public static final int TIFF_EXTRA_SAMPLES = 338;
	public static final int TIFF_SAMPLE_FORMAT = 339;
	public static final int TIFF_S_MIN_SAMPLE_VALUE = 340;
	public static final int TIFF_S_MAX_SAMPLE_VALUE = 341;

	public TIFFImageDecoder(SeekableStream input, ImageDecodeParam param) {
		super(input, param);
	}

	@Override
	public int getNumPages() throws IOException {
		return TIFFDirectory.getNumDirectories(input);
	}

	@Override
	public RenderedImage decodeAsRenderedImage(int page) throws IOException {
		if (page < 0 || page >= getNumPages()) {
			throw new IOException(JaiI18N.getString("TIFFImageDecoder9"));
		}
		return new TIFFImage(input, (TIFFDecodeParam) param, page);
	}
}
