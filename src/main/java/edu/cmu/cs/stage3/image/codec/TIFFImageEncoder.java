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
import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;

/**
 * A baseline TIFF writer. The writer outputs TIFF images in either Bilevel,
 * Greyscale, Palette color or Full Color modes.
 * 
 */
public class TIFFImageEncoder extends ImageEncoderImpl {

	long firstIFDOffset = 0;
	boolean skipByte = false;

	// Image Types
	private static final int TIFF_BILEVEL_WHITE_IS_ZERO = 0;
	private static final int TIFF_BILEVEL_BLACK_IS_ZERO = 1;
	private static final int TIFF_PALETTE = 2;
	private static final int TIFF_FULLCOLOR = 3;
	private static final int TIFF_GREYSCALE = 4;

	// Compression types
	private static final int COMP_NONE = 1;

	public TIFFImageEncoder(OutputStream output, ImageEncodeParam param) {
		super(output, param);
		if (this.param == null) {
			this.param = new TIFFEncodeParam();
		}
	}

	/**
	 * Encodes a RenderedImage and writes the output to the OutputStream
	 * associated with this ImageEncoder.
	 */

	@Override
	public void encode(RenderedImage im) throws IOException {

		// Currently all images are stored uncompressed
		// int compression = param.getCompression();
		int compression = COMP_NONE;

		// Currently we only write out strips, not rows
		// boolean isTiled = param.getWriteTiled();

		TIFFField fields[];

		int minX = im.getMinX();
		int minY = im.getMinY();
		int width = im.getWidth();
		int height = im.getHeight();

		SampleModel sampleModel = im.getSampleModel();
		int numBands = sampleModel.getNumBands();
		int sampleSize[] = sampleModel.getSampleSize();

		int dataType = sampleModel.getDataType();
		if (dataType != DataBuffer.TYPE_BYTE && dataType != DataBuffer.TYPE_SHORT && dataType != DataBuffer.TYPE_USHORT) {
			// Support only byte and (unsigned) short.
			throw new Error(JaiI18N.getString("TIFFImageEncoder0"));
		}

		boolean dataTypeIsShort = dataType == DataBuffer.TYPE_SHORT || dataType == DataBuffer.TYPE_USHORT;

		ColorModel colorModel = im.getColorModel();
		if (colorModel != null && colorModel instanceof IndexColorModel && dataTypeIsShort) {
			// Don't support (unsigned) short palette-color images.
			throw new Error(JaiI18N.getString("TIFFImageEncoder2"));
		}
		IndexColorModel icm = null;
		int sizeOfColormap = 0;
		int colormap[] = null;

		// Basic fields - have to be in increasing numerical order BILEVEL
		// ImageWidth 256
		// ImageLength 257
		// BitsPerSample 258
		// Compression 259
		// PhotoMetricInterpretation 262
		// StripOffsets 273
		// RowsPerStrip 278
		// StripByteCounts 279
		// XResolution 282
		// YResolution 283
		// ResolutionUnit 296

		// Above 11 + samplesPerPixel which we'll always write
		int numFields = 12;

		// And one more for (unsigned) short data.
		if (dataTypeIsShort) {
			numFields += 3;
		}

		int photometricInterpretation = 2;
		int imageType = TIFF_FULLCOLOR;

		// IMAGE TYPES POSSIBLE

		// Bilevel
		// BitsPerSample = 1
		// Compression = 1, 2, or 32773
		// PhotometricInterpretation either 0 or 1

		// Greyscale
		// BitsPerSample = 4 or 8
		// Compression = 1, 32773
		// PhotometricInterpretation either 0 or 1

		// Palette
		// ColorMap 320
		// BitsPerSample = 4 or 8
		// PhotometrciInterpretation = 3

		// Full color
		// BitsPerSample = 8, 8, 8
		// SamplesPerPixel = 3 or more 277
		// Compression = 1, 32773
		// PhotometricInterpretation = 2

		if (colorModel instanceof IndexColorModel) {

			icm = (IndexColorModel) colorModel;
			int mapSize = icm.getMapSize();

			if (sampleSize[0] == 1) {
				// Bilevel image

				if (mapSize != 2) {
					throw new IllegalArgumentException(JaiI18N.getString("TIFFImageEncoder1"));
				}

				byte r[] = new byte[mapSize];
				icm.getReds(r);
				byte g[] = new byte[mapSize];
				icm.getGreens(g);
				byte b[] = new byte[mapSize];
				icm.getBlues(b);

				if ((r[0] & 0xff) == 0 && (r[1] & 0xff) == 255 && (g[0] & 0xff) == 0 && (g[1] & 0xff) == 255 && (b[0] & 0xff) == 0 && (b[1] & 0xff) == 255) {

					imageType = TIFF_BILEVEL_BLACK_IS_ZERO;

				} else if ((r[0] & 0xff) == 255 && (r[1] & 0xff) == 0 && (g[0] & 0xff) == 255 && (g[1] & 0xff) == 0 && (b[0] & 0xff) == 255 && (b[1] & 0xff) == 0) {

					imageType = TIFF_BILEVEL_WHITE_IS_ZERO;

				} else {
					imageType = TIFF_PALETTE;
				}

			} else {
				// Palette color image.
				imageType = TIFF_PALETTE;
			}
		} else {

			// If it is not an IndexColorModel, it can either be a greyscale
			// image or a full color image

			if ((colorModel == null || colorModel.getColorSpace().getType() == ColorSpace.TYPE_GRAY) && numBands == 1) {
				// Greyscale image
				imageType = TIFF_GREYSCALE;
			} else {
				// Full color image
				imageType = TIFF_FULLCOLOR;
			}
		}

		switch (imageType) {

			case TIFF_BILEVEL_WHITE_IS_ZERO :
				photometricInterpretation = 0;
				break;

			case TIFF_BILEVEL_BLACK_IS_ZERO :
				photometricInterpretation = 1;
				break;

			case TIFF_GREYSCALE :
				// Since the CS_GRAY colorspace is always of type black_is_zero
				photometricInterpretation = 1;
				break;

			case TIFF_PALETTE :
				photometricInterpretation = 3;

				icm = (IndexColorModel) colorModel;
				sizeOfColormap = icm.getMapSize();

				byte r[] = new byte[sizeOfColormap];
				icm.getReds(r);
				byte g[] = new byte[sizeOfColormap];
				icm.getGreens(g);
				byte b[] = new byte[sizeOfColormap];
				icm.getBlues(b);

				int redIndex = 0,
				greenIndex = sizeOfColormap;
				int blueIndex = 2 * sizeOfColormap;
				colormap = new int[sizeOfColormap * 3];
				for (int i = 0; i < sizeOfColormap; i++) {
					colormap[redIndex++] = r[i] << 8 & 0xffff;
					colormap[greenIndex++] = g[i] << 8 & 0xffff;
					colormap[blueIndex++] = b[i] << 8 & 0xffff;
				}

				sizeOfColormap *= 3;

				// Since we will be writing the colormap field.
				numFields++;
				break;

			case TIFF_FULLCOLOR :
				photometricInterpretation = 2;
				break;

		}

		// Each strip is 8 rows.
		long rowsPerStrip = 8;
		int strips = (int) Math.ceil(height / 8.0);
		long stripByteCounts[] = new long[strips];

		long bytesPerRow = (long) Math.ceil(sampleSize[0] / 8.0 * width * numBands);

		long bytesPerStrip = bytesPerRow * rowsPerStrip;

		for (int i = 0; i < strips; i++) {
			stripByteCounts[i] = bytesPerStrip;
		}

		// Last strip may have lesser rows
		long lastStripRows = height - rowsPerStrip * (strips - 1);
		stripByteCounts[strips - 1] = lastStripRows * bytesPerRow;

		long totalBytesOfData = bytesPerStrip * (strips - 1) + stripByteCounts[strips - 1];

		// Since we always write the image data right after the File header,
		// so the first strip will always start after the 8 bytes of header.
		long stripOffsets[] = new long[strips];
		stripOffsets[0] = 8;
		for (int i = 1; i < strips; i++) {
			stripOffsets[i] = stripOffsets[i - 1] + stripByteCounts[i - 1];
		}

		// Create Directory
		fields = new TIFFField[numFields];

		// Set field counter
		int fld = 0;

		// Image Width
		fields[fld++] = new TIFFField(TIFFImageDecoder.TIFF_IMAGE_WIDTH, TIFFField.TIFF_LONG, 1, new long[]{width});

		// Image Length
		fields[fld++] = new TIFFField(TIFFImageDecoder.TIFF_IMAGE_LENGTH, TIFFField.TIFF_LONG, 1, new long[]{height});

		fields[fld++] = new TIFFField(TIFFImageDecoder.TIFF_BITS_PER_SAMPLE, TIFFField.TIFF_SHORT, numBands, sampleSize);

		fields[fld++] = new TIFFField(TIFFImageDecoder.TIFF_COMPRESSION, TIFFField.TIFF_SHORT, 1, new int[]{compression});

		fields[fld++] = new TIFFField(TIFFImageDecoder.TIFF_PHOTOMETRIC_INTERPRETATION, TIFFField.TIFF_SHORT, 1, new int[]{photometricInterpretation});

		fields[fld++] = new TIFFField(TIFFImageDecoder.TIFF_STRIP_OFFSETS, TIFFField.TIFF_LONG, strips, stripOffsets);

		fields[fld++] = new TIFFField(TIFFImageDecoder.TIFF_SAMPLES_PER_PIXEL, TIFFField.TIFF_SHORT, 1, new int[]{numBands});

		fields[fld++] = new TIFFField(TIFFImageDecoder.TIFF_ROWS_PER_STRIP, TIFFField.TIFF_LONG, 1, new long[]{rowsPerStrip});

		fields[fld++] = new TIFFField(TIFFImageDecoder.TIFF_STRIP_BYTE_COUNTS, TIFFField.TIFF_LONG, strips, stripByteCounts);

		fields[fld++] = new TIFFField(TIFFImageDecoder.TIFF_X_RESOLUTION, TIFFField.TIFF_RATIONAL, 1, new long[][]{{72, 1}});

		fields[fld++] = new TIFFField(TIFFImageDecoder.TIFF_Y_RESOLUTION, TIFFField.TIFF_RATIONAL, 1, new long[][]{{72, 1}});

		fields[fld++] = new TIFFField(TIFFImageDecoder.TIFF_RESOLUTION_UNIT, TIFFField.TIFF_SHORT, 1, new int[]{2});

		if (colormap != null) {
			fields[fld++] = new TIFFField(TIFFImageDecoder.TIFF_COLORMAP, TIFFField.TIFF_SHORT, sizeOfColormap, colormap);
		}

		// Data Sample Format Extension fields.
		if (dataTypeIsShort) {
			// SampleFormat
			int[] sampleFormat = new int[numBands];
			sampleFormat[0] = dataType == DataBuffer.TYPE_USHORT ? 1 : 2;
			for (int b = 1; b < numBands; b++) {
				sampleFormat[b] = sampleFormat[0];
			}
			fields[fld++] = new TIFFField(TIFFImageDecoder.TIFF_SAMPLE_FORMAT, TIFFField.TIFF_SHORT, numBands, sampleFormat);

			// SMinSampleValue: set to data type minimum.
			int[] minValue = new int[numBands];
			minValue[0] = dataType == DataBuffer.TYPE_USHORT ? 0 : Short.MIN_VALUE;
			for (int b = 1; b < numBands; b++) {
				minValue[b] = minValue[0];
			}
			fields[fld++] = new TIFFField(TIFFImageDecoder.TIFF_S_MIN_SAMPLE_VALUE, TIFFField.TIFF_SHORT, numBands, minValue);

			// SMaxSampleValue: set to data type maximum.
			int[] maxValue = new int[numBands];
			maxValue[0] = dataType == DataBuffer.TYPE_USHORT ? 65535 : Short.MAX_VALUE;
			for (int b = 1; b < numBands; b++) {
				maxValue[b] = maxValue[0];
			}
			fields[fld++] = new TIFFField(TIFFImageDecoder.TIFF_S_MAX_SAMPLE_VALUE, TIFFField.TIFF_SHORT, numBands, maxValue);
		}

		// File header always occupies 8 bytes and we write the image data
		// after that.
		firstIFDOffset = 8 + totalBytesOfData;
		// Must begin on a word boundary
		if (firstIFDOffset % 2 != 0) {
			skipByte = true;
			firstIFDOffset++;
		}

		// Writing of actual image data
		writeFileHeader(firstIFDOffset);

		// Buffer for up to 8 rows of pixels
		int[] pixels = new int[8 * width * numBands];

		// Also create a buffer to hold 8 lines of the data
		// to be written to the file, so we can use array writes.
		byte[] bpixels = null;
		if (dataType == DataBuffer.TYPE_BYTE) {
			bpixels = new byte[8 * width * numBands];
		} else if (dataTypeIsShort) {
			bpixels = new byte[16 * width * numBands];
		}

		// Process 8 rows at a time
		int lastRow = minY + height;
		for (int row = minY; row < lastRow; row += 8) {
			int rows = Math.min(8, lastRow - row);
			int size = rows * width * numBands;

			// Grab the pixels
			Raster src = im.getData(new Rectangle(minX, row, width, rows));
			src.getPixels(minX, row, width, rows, pixels);

			int index, remainder;

			int pixel = 0;;
			int k = 0;
			switch (sampleSize[0]) {

				case 1 :

					index = 0;

					// For each of the rows in a strip
					for (int i = 0; i < rows; i++) {

						// Write out the number of pixels exactly divisible by 8
						for (int j = 0; j < width / 8; j++) {

							pixel = pixels[index++] << 7 | pixels[index++] << 6 | pixels[index++] << 5 | pixels[index++] << 4 | pixels[index++] << 3 | pixels[index++] << 2 | pixels[index++] << 1 | pixels[index++];
							bpixels[k++] = (byte) pixel;
						}

						// Write out the pixels remaining after division by 8
						if (width % 8 > 0) {
							pixel = 0;
							for (int j = 0; j < width % 8; j++) {
								pixel |= pixels[index++] << 7 - j;
							}
							bpixels[k++] = (byte) pixel;
						}

					}
					output.write(bpixels, 0, rows * ((width + 7) / 8));

					break;

				case 4 :

					index = 0;

					// For each of the rows in a strip
					for (int i = 0; i < rows; i++) {

						// Write out the number of pixels that will fit into an
						// even number of nibbles.
						for (int j = 0; j < width / 2; j++) {
							pixel = pixels[index++] << 4 | pixels[index++];
							bpixels[k++] = (byte) pixel;
						}

						// Last pixel for odd-length lines
						if (width % 2 == 1) {
							pixel = pixels[index++] << 4;
							bpixels[k++] = (byte) pixel;
						}
					}
					output.write(bpixels, 0, rows * ((width + 1) / 2));
					break;

				case 8 :

					for (int i = 0; i < size; i++) {
						bpixels[i] = (byte) pixels[i];
					}
					output.write(bpixels, 0, size);
					break;

				case 16 :

					int l = 0;
					for (int i = 0; i < size; i++) {
						short value = (short) pixels[i];
						bpixels[l++] = (byte) ((value & 0xff00) >> 8);
						bpixels[l++] = (byte) (value & 0x00ff);
					}
					output.write(bpixels, 0, size * 2);
					break;

			}
		}

		// IFD - TIFFDirectory
		writeDirectory(fields, 0);
	}

	private void writeFileHeader(long firstIFDOffset) throws IOException {
		// 8 byte image file header

		// Byte order used within the file - Big Endian
		output.write('M');
		output.write('M');

		// Magic value
		output.write(0);
		output.write(42);

		// Offset in bytes of the first IFD, must begin on a word boundary
		writeLong(firstIFDOffset);
	}

	private void writeDirectory(TIFFField fields[], int nextIFDOffset) throws IOException {

		if (skipByte) {
			output.write(0);
		}

		// 2 byte count of number of directory entries (fields)
		int numEntries = fields.length;

		long offsetBeyondIFD = firstIFDOffset + 12 * numEntries + 4 + 2;
		Vector tooBig = new Vector();

		TIFFField field;
		int tag;
		int type;
		int count;

		// Write number of fields in the IFD
		writeUnsignedShort(numEntries);

		for (int i = 0; i < numEntries; i++) {

			field = fields[i];

			// 12 byte field entry TIFFField

			// byte 0-1 Tag that identifies a field
			tag = field.getTag();
			writeUnsignedShort(tag);

			// byte 2-3 The field type
			type = field.getType();
			writeUnsignedShort(type);

			// bytes 4-7 the number of values of the indicated type
			count = field.getCount();
			writeLong(count);

			// bytes 8 - 11 the value offset
			if (count * sizeOfType[type] > 4) {

				// We need an offset as data won't fit into 4 bytes
				writeLong(offsetBeyondIFD);
				offsetBeyondIFD += count * sizeOfType[type];
				tooBig.add(new Integer(i));

			} else {

				writeValuesAsFourBytes(field);
			}

		}

		// Address of next IFD
		writeLong(nextIFDOffset);

		int index;
		// Write the tag values that did not fit into 4 bytes
		for (int i = 0; i < tooBig.size(); i++) {
			index = ((Integer) tooBig.elementAt(i)).intValue();
			writeValues(fields[index]);
		}
	}

	private static final int[] sizeOfType = {0, // 0 = n/a
	1, // 1 = byte
	1, // 2 = ascii
	2, // 3 = short
	4, // 4 = long
	8, // 5 = rational
	1, // 6 = sbyte
	1, // 7 = undefined
	2, // 8 = sshort
	4, // 9 = slong
	8, // 10 = srational
	4, // 11 = float
	8 // 12 = double
	};

	private void writeValuesAsFourBytes(TIFFField field) throws IOException {

		int dataType = field.getType();
		int count = field.getCount();

		switch (dataType) {

		// unsigned 8 bits
			case TIFFField.TIFF_BYTE :
				byte bytes[] = field.getAsBytes();

				for (int i = 0; i < count; i++) {
					output.write(bytes[i]);
				}

				for (int i = 0; i < 4 - count; i++) {
					output.write(0);
				}

				break;

			// unsigned 16 bits
			case TIFFField.TIFF_SHORT :
				int shorts[] = field.getAsInts();

				for (int i = 0; i < count; i++) {
					writeUnsignedShort(shorts[i]);
				}

				for (int i = 0; i < 2 - count; i++) {
					writeUnsignedShort(0);
				}

				break;

			// unsigned 32 bits
			case TIFFField.TIFF_LONG :
				long longs[] = field.getAsLongs();

				for (int i = 0; i < count; i++) {
					writeLong(longs[i]);
				}
				break;
		}

	}

	private void writeValues(TIFFField field) throws IOException {

		int dataType = field.getType();
		int count = field.getCount();

		switch (dataType) {

		// unsigned 8 bits
			case TIFFField.TIFF_BYTE :
				byte bytes[] = field.getAsBytes();
				for (int i = 0; i < count; i++) {
					output.write(bytes[i]);
				}
				break;

			// unsigned 16 bits
			case TIFFField.TIFF_SHORT :
				int shorts[] = field.getAsInts();
				for (int i = 0; i < count; i++) {
					writeUnsignedShort(shorts[i]);
				}
				break;

			// unsigned 32 bits
			case TIFFField.TIFF_LONG :
				long longs[] = field.getAsLongs();
				for (int i = 0; i < count; i++) {
					writeLong(longs[i]);
				}
				break;

			case TIFFField.TIFF_RATIONAL :
				long rationals[][] = field.getAsRationals();
				for (int i = 0; i < count; i++) {
					writeLong(rationals[i][0]);
					writeLong(rationals[i][1]);
				}
				break;

		}

	}

	// Here s is never expected to have value greater than what can be
	// stored in 2 bytes.
	private void writeUnsignedShort(int s) throws IOException {
		output.write((s & 0xff00) >>> 8);
		output.write(s & 0x00ff);
	}

	private void writeLong(long l) throws IOException {
		output.write((int) ((l & 0xff000000) >>> 24));
		output.write((int) ((l & 0x00ff0000) >>> 16));
		output.write((int) ((l & 0x0000ff00) >>> 8));
		output.write((int) l & 0x000000ff);
	}

}
