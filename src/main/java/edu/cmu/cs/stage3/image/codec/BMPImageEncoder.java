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

import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.io.IOException;
import java.io.OutputStream;

/**
 * An ImageEncoder for the various versions of the BMP image file format.
 * 
 * Unless specified otherwise by the BMPDecodeParam object passed to the
 * constructor, Version 3 will be the default version used.
 * 
 * <p>
 * If the image to be encoded has an IndexColorModel and can be encoded using
 * upto 8 bits per pixel, the image will be written out as a Palette color image
 * with an appropriate number of bits per pixel. For example an image having a
 * 256 color IndexColorModel will be written out as a Palette image with 8 bits
 * per pixel while one with a 16 color palette will be written out as a Palette
 * image with 4 bits per pixel. For all other images, the 24 bit image format
 * will be used.
 * 
 * 
 */
public class BMPImageEncoder extends ImageEncoderImpl {

	private OutputStream output;
	private int version;
	private boolean isCompressed, isTopDown;
	private int w, h;

	/**
	 * An ImageEncoder for the BMP file format.
	 * 
	 * @param output
	 *            The OutputStream to write to.
	 * @param param
	 *            The BMPEncodeParam object.
	 */
	public BMPImageEncoder(OutputStream output, ImageEncodeParam param) {

		super(output, param);

		this.output = output;

		BMPEncodeParam bmpParam;
		if (param == null) {
			// Use default valued BMPEncodeParam
			bmpParam = new BMPEncodeParam();
		} else {
			bmpParam = (BMPEncodeParam) param;
		}

		version = bmpParam.getVersion();
		isCompressed = bmpParam.isCompressed();
		isTopDown = bmpParam.isTopDown();
	}

	/**
	 * Encodes a RenderedImage and writes the output to the OutputStream
	 * associated with this ImageEncoder.
	 */

	@Override
	public void encode(RenderedImage im) throws IOException {

		// Get image dimensions
		int minX = im.getMinX();
		int minY = im.getMinY();
		w = im.getWidth();
		h = im.getHeight();

		// Default is using 24 bits per pixel.
		int bitsPerPixel = 24;
		boolean isPalette = false;
		int paletteEntries = 0;
		IndexColorModel icm = null;

		SampleModel sm = im.getSampleModel();
		int numBands = sm.getNumBands();

		ColorModel cm = im.getColorModel();

		if (numBands != 1 && numBands != 3) {
			throw new IllegalArgumentException(JaiI18N.getString("BMPImageEncoder1"));
		}

		int sampleSize[] = sm.getSampleSize();
		if (sampleSize[0] > 8) {
			throw new RuntimeException(JaiI18N.getString("BMPImageEncoder2"));
		}

		for (int i = 1; i < sampleSize.length; i++) {
			if (sampleSize[i] != sampleSize[0]) {
				throw new RuntimeException(JaiI18N.getString("BMPImageEncoder3"));
			}
		}

		// Float and Double data cannot be written in a BMP format.
		int dataType = sm.getTransferType();
		if (dataType == DataBuffer.TYPE_USHORT || dataType == DataBuffer.TYPE_SHORT || dataType == DataBuffer.TYPE_INT || dataType == DataBuffer.TYPE_FLOAT || dataType == DataBuffer.TYPE_DOUBLE) {
			throw new RuntimeException(JaiI18N.getString("BMPImageEncoder0"));
		}

		// Number of bytes that a scanline for the image written out will have.
		int destScanlineBytes = w * numBands;

		// Currently we only write out uncompressed data, so ignore
		// isCompressed encoding parameter. aastha, 03/15/99
		int compression = 0;

		byte r[] = null, g[] = null, b[] = null, a[] = null;

		if (cm instanceof IndexColorModel) {

			isPalette = true;
			icm = (IndexColorModel) cm;
			paletteEntries = icm.getMapSize();

			if (paletteEntries <= 2) {

				bitsPerPixel = 1;
				destScanlineBytes = (int) Math.ceil(w / 8.0);

			} else if (paletteEntries <= 16) {

				bitsPerPixel = 4;
				destScanlineBytes = (int) Math.ceil(w / 2.0);

			} else if (paletteEntries <= 256) {

				bitsPerPixel = 8;

			} else {

				// Cannot be written as a Palette image. So write out as
				// 24 bit image.
				bitsPerPixel = 24;
				isPalette = false;
				paletteEntries = 0;
				destScanlineBytes = w * 3;
			}

			if (isPalette == true) {

				r = new byte[paletteEntries];
				g = new byte[paletteEntries];
				b = new byte[paletteEntries];
				a = new byte[paletteEntries];

				icm.getAlphas(a);
				icm.getReds(r);
				icm.getGreens(g);
				icm.getBlues(b);
			}

		} else {

			// Grey scale images
			if (numBands == 1) {

				isPalette = true;
				paletteEntries = 256;
				// int sampleSize[] = sm.getSampleSize();
				bitsPerPixel = sampleSize[0];

				destScanlineBytes = (int) Math.ceil(w * bitsPerPixel / 8.0);

				r = new byte[256];
				g = new byte[256];
				b = new byte[256];
				a = new byte[256];

				for (int i = 0; i < 256; i++) {
					r[i] = (byte) i;
					g[i] = (byte) i;
					b[i] = (byte) i;
					a[i] = (byte) i;
				}
			}
		}

		// actual writing of image data
		int fileSize = 0;
		int offset = 0;
		int headerSize = 0;
		int imageSize = 0;
		int xPelsPerMeter = 0;
		int yPelsPerMeter = 0;
		int colorsUsed = 0;
		int colorsImportant = paletteEntries;
		int padding = 0;

		// Calculate padding for each scanline
		int remainder = destScanlineBytes % 4;
		if (remainder != 0) {
			padding = 4 - remainder;
		}

		switch (version) {
			case BMPEncodeParam.VERSION_2 :
				offset = 26 + paletteEntries * 3;
				headerSize = 12;
				imageSize = (destScanlineBytes + padding) * h;
				fileSize = imageSize + offset;
				throw new RuntimeException(JaiI18N.getString("BMPImageEncoder5"));
				// break;

			case BMPEncodeParam.VERSION_3 :
				// FileHeader is 14 bytes, BitmapHeader is 40 bytes,
				// add palette size and that is where the data will begin
				offset = 54 + paletteEntries * 4;
				imageSize = (destScanlineBytes + padding) * h;
				fileSize = imageSize + offset;
				headerSize = 40;
				break;

			case BMPEncodeParam.VERSION_4 :
				headerSize = 108;
				throw new RuntimeException(JaiI18N.getString("BMPImageEncoder5"));
				// break;
		}

		writeFileHeader(fileSize, offset);

		writeInfoHeader(headerSize, bitsPerPixel);

		// compression
		writeDWord(compression);

		// imageSize
		writeDWord(imageSize);

		// xPelsPerMeter
		writeDWord(xPelsPerMeter);

		// yPelsPerMeter
		writeDWord(yPelsPerMeter);

		// Colors Used
		writeDWord(colorsUsed);

		// Colors Important
		writeDWord(colorsImportant);

		// palette
		if (isPalette == true) {

			// write palette
			switch (version) {

			// has 3 field entries
				case BMPEncodeParam.VERSION_2 :

					for (int i = 0; i < paletteEntries; i++) {
						output.write(b[i]);
						output.write(g[i]);
						output.write(r[i]);
					}
					break;

				// has 4 field entries
				default :

					for (int i = 0; i < paletteEntries; i++) {
						output.write(b[i]);
						output.write(g[i]);
						output.write(r[i]);
						output.write(a[i]);
					}
					break;
			}

		} // else no palette

		// Writing of actual image data

		int scanlineBytes = w * numBands;

		// Buffer for up to 8 rows of pixels
		int[] pixels = new int[8 * scanlineBytes];

		// Also create a buffer to hold one line of the data
		// to be written to the file, so we can use array writes.
		byte[] bpixels = new byte[destScanlineBytes];

		int l;

		if (!isTopDown) {
			// Process 8 rows at a time so all but the first will have a
			// multiple of 8 rows.
			int lastRow = minY + h;

			for (int row = lastRow - 1; row >= minY; row -= 8) {
				// Number of rows being read
				int rows = Math.min(8, row - minY + 1);

				// Get the pixels
				Raster src = im.getData(new Rectangle(minX, row - rows + 1, w, rows));

				src.getPixels(minX, row - rows + 1, w, rows, pixels);

				l = 0;

				// Last possible position in the pixels array
				int max = scanlineBytes * rows - 1;

				for (int i = 0; i < rows; i++) {

					// Beginning of each scanline in the pixels array
					l = max - (i + 1) * scanlineBytes + 1;

					writePixels(l, scanlineBytes, bitsPerPixel, pixels, bpixels, padding, numBands, icm);
				}
			}

		} else {
			// Process 8 rows at a time so all but the last will have a
			// multiple of 8 rows.
			int lastRow = minY + h;
			for (int row = minY; row < lastRow; row += 8) {
				int rows = Math.min(8, lastRow - row);

				// Get the pixels
				Raster src = im.getData(new Rectangle(minX, row, w, rows));
				src.getPixels(minX, row, w, rows, pixels);

				l = 0;
				for (int i = 0; i < rows; i++) {
					writePixels(l, scanlineBytes, bitsPerPixel, pixels, bpixels, padding, numBands, icm);
				}
			}
		}

	}

	private void writePixels(int l, int scanlineBytes, int bitsPerPixel, int pixels[], byte bpixels[], int padding, int numBands, IndexColorModel icm) throws IOException {

		int pixel = 0;

		int k = 0;
		switch (bitsPerPixel) {

			case 1 :

				for (int j = 0; j < scanlineBytes / 8; j++) {
					bpixels[k++] = (byte) (pixels[l++] << 7 | pixels[l++] << 6 | pixels[l++] << 5 | pixels[l++] << 4 | pixels[l++] << 3 | pixels[l++] << 2 | pixels[l++] << 1 | pixels[l++]);
				}

				// Partially filled last byte, if any
				if (scanlineBytes % 8 > 0) {
					pixel = 0;
					for (int j = 0; j < scanlineBytes % 8; j++) {
						pixel |= pixels[l++] << 7 - j;
					}
					bpixels[k++] = (byte) pixel;
				}
				output.write(bpixels, 0, (scanlineBytes + 7) / 8);

				break;

			case 4 :

				for (int j = 0; j < scanlineBytes / 2; j++) {
					pixel = pixels[l++] << 4 | pixels[l++];
					bpixels[k++] = (byte) pixel;
				}
				// Put the last pixel of odd-length lines in the 4 MSBs
				if (scanlineBytes % 2 == 1) {
					pixel = pixels[l] << 4;
					bpixels[k++] = (byte) pixel;
				}
				output.write(bpixels, 0, (scanlineBytes + 1) / 2);
				break;

			case 8 :
				for (int j = 0; j < scanlineBytes; j++) {
					bpixels[j] = (byte) pixels[l++];
				}
				output.write(bpixels, 0, scanlineBytes);
				break;

			case 24 :
				if (numBands == 3) {
					for (int j = 0; j < scanlineBytes; j += 3) {
						// Since BMP needs BGR format
						bpixels[k++] = (byte) pixels[l + 2];
						bpixels[k++] = (byte) pixels[l + 1];
						bpixels[k++] = (byte) pixels[l];
						l += 3;
					}
					output.write(bpixels, 0, scanlineBytes);
				} else {
					// Case where IndexColorModel had > 256 colors.
					int entries = icm.getMapSize();

					byte r[] = new byte[entries];
					byte g[] = new byte[entries];
					byte b[] = new byte[entries];

					icm.getReds(r);
					icm.getGreens(g);
					icm.getBlues(b);
					int index;

					for (int j = 0; j < scanlineBytes; j++) {
						index = pixels[l];
						bpixels[k++] = b[index];
						bpixels[k++] = g[index];
						bpixels[k++] = b[index];
						l++;
					}
					output.write(bpixels, 0, scanlineBytes * 3);
				}
				break;

		}

		// Write out the padding
		for (k = 0; k < padding; k++) {
			output.write(0);
		}

	}

	private void writeFileHeader(int fileSize, int offset) throws IOException {
		// magic value
		output.write('B');
		output.write('M');

		// File size
		writeDWord(fileSize);

		// reserved1 and reserved2
		output.write(0);
		output.write(0);
		output.write(0);
		output.write(0);

		// offset to image data
		writeDWord(offset);
	}

	private void writeInfoHeader(int headerSize, int bitsPerPixel) throws IOException {

		// size of header
		writeDWord(headerSize);

		// width
		writeDWord(w);

		// height
		writeDWord(h);

		// number of planes
		writeWord(1);

		// Bits Per Pixel
		writeWord(bitsPerPixel);
	}

	// Methods for little-endian writing
	public void writeWord(int word) throws IOException {
		output.write(word & 0xff);
		output.write((word & 0xff00) >> 8);
	}

	public void writeDWord(int dword) throws IOException {
		output.write(dword & 0xff);
		output.write((dword & 0xff00) >> 8);
		output.write((dword & 0xff0000) >> 16);
		output.write((dword & 0xff000000) >> 24);
	}
}
