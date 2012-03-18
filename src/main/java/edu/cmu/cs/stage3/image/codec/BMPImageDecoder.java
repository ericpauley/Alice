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

import java.awt.Point;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 */
public class BMPImageDecoder extends ImageDecoderImpl {

    public BMPImageDecoder(InputStream input, ImageDecodeParam param) {
        super(input, param);
    }

    
	public RenderedImage decodeAsRenderedImage(int page) throws IOException {
        if (page != 0) {
            throw new IOException(JaiI18N.getString("BMPImageDecoder8"));
        }
        return new BMPImage(input);
    }
}

class BMPImage extends SimpleRenderedImage {

    // BMP variables
    private BufferedInputStream inputStream;
    private long bitmapFileSize;
    private long bitmapOffset;
    private long compression;
    private long imageSize;
    private byte palette[];
    private int imageType;
    private int numBands;
    private boolean isBottomUp;
    private int bitsPerPixel;
    private int redMask, greenMask, blueMask;

    // BMP Image types
    private static final int VERSION_2_1_BIT = 0;
    private static final int VERSION_2_4_BIT = 1;
    private static final int VERSION_2_8_BIT = 2;
    private static final int VERSION_2_24_BIT = 3;

    private static final int VERSION_3_1_BIT = 4;
    private static final int VERSION_3_4_BIT = 5;
    private static final int VERSION_3_8_BIT = 6;
    private static final int VERSION_3_24_BIT = 7;

    private static final int VERSION_3_NT_16_BIT = 8;
    private static final int VERSION_3_NT_32_BIT = 9;

    private static final int VERSION_4_1_BIT = 10;
    private static final int VERSION_4_4_BIT = 11;
    private static final int VERSION_4_8_BIT = 12;
    private static final int VERSION_4_16_BIT = 13;
    private static final int VERSION_4_24_BIT = 14;
    private static final int VERSION_4_32_BIT = 15;

    // Color space types
    private static final int LCS_CALIBRATED_RGB = 0;
    private static final int LCS_sRGB = 1;
    private static final int LCS_CMYK = 2;

    // Compression Types
    private static final int BI_RGB = 0;
    private static final int BI_RLE8 = 1;
    private static final int BI_RLE4 = 2;
    private static final int BI_BITFIELDS = 3;

    // Shifts for each color component
    private int blueBits, greenBits, redBits;

    private WritableRaster theTile = null;

    /**
     * Constructor for BMPImage
     *
     * @param stream
     */
    public BMPImage(InputStream stream) {
	if (stream instanceof BufferedInputStream) {
	    inputStream = (BufferedInputStream)stream;
	} else {
	    inputStream = new BufferedInputStream(stream);
	}

	try {

	    // Start File Header
	    if (!(readUnsignedByte(inputStream) == 'B' &&
 		  readUnsignedByte(inputStream) == 'M')) {
		throw new
		    RuntimeException(JaiI18N.getString("BMPImageDecoder0"));
	    }

	    // Read file size
	    bitmapFileSize = readDWord(inputStream);

	    // Read the two reserved fields
	    readWord(inputStream);
	    readWord(inputStream);

	    // Offset to the bitmap from the beginning
	    bitmapOffset = readDWord(inputStream);

	    // End File Header

	    // Start BitmapCoreHeader
	    long size = readDWord(inputStream);

	    if (size == 12) {
		width = readWord(inputStream);
		height = readWord(inputStream);
	    } else {
		width = readLong(inputStream);
		height = readLong(inputStream);
	    }

	    int planes = readWord(inputStream);
	    bitsPerPixel = readWord(inputStream);

	    properties.put("color_planes", new Integer(planes));
	    properties.put("bits_per_pixel", new Integer(bitsPerPixel));

	    // As BMP always has 3 rgb bands, except for Version 5,
	    // which is bgra
	    numBands = 3;

	    if (size == 12) {
		// Windows 2.x and OS/2 1.x
		properties.put("bmp_version", "BMP v. 2.x");

		// Classify the image type
		if (bitsPerPixel == 1) {
		    imageType = VERSION_2_1_BIT;
		} else if (bitsPerPixel == 4) {
		    imageType = VERSION_2_4_BIT;
		} else if (bitsPerPixel == 8) {
		    imageType = VERSION_2_8_BIT;
		} else if (bitsPerPixel == 24) {
		    imageType = VERSION_2_24_BIT;
		}

		// Read in the palette
		int numberOfEntries = (int)((bitmapOffset-14-size) / 3);
		int sizeOfPalette = numberOfEntries*3;
		palette = new byte[sizeOfPalette];
		inputStream.read(palette, 0, sizeOfPalette);
		properties.put("palette", palette);
	    } else {

		compression = readDWord(inputStream);
		imageSize = readDWord(inputStream);
		long xPelsPerMeter = readLong(inputStream);
		long yPelsPerMeter = readLong(inputStream);
		long colorsUsed = readDWord(inputStream);
		long colorsImportant = readDWord(inputStream);

		switch((int)compression) {
		case BI_RGB:
		    properties.put("compression", "BI_RGB");
		    break;

		case BI_RLE8:
		    properties.put("compression", "BI_RLE8");
		    break;

		case BI_RLE4:
		    properties.put("compression", "BI_RLE4");
		    break;

		case BI_BITFIELDS:
		    properties.put("compression", "BI_BITFIELDS");
		    break;
		}

		properties.put("x_pixels_per_meter", new Long(xPelsPerMeter));
		properties.put("y_pixels_per_meter", new Long(yPelsPerMeter));
		properties.put("colors_used", new Long(colorsUsed));
		properties.put("colors_important", new Long(colorsImportant));

		if (size == 40) {
		    // Windows 3.x and Windows NT
		    switch((int)compression) {

		    case BI_RGB:  // No compression
		    case BI_RLE8:  // 8-bit RLE compression
		    case BI_RLE4:  // 4-bit RLE compression

			// Read in the palette
			int numberOfEntries = (int)((bitmapOffset-14-size) / 4);
			int sizeOfPalette = numberOfEntries*4;
			palette = new byte[sizeOfPalette];
			inputStream.read(palette, 0, sizeOfPalette);
			properties.put("palette", palette);

			if (bitsPerPixel == 1) {
			    imageType = VERSION_3_1_BIT;
			} else if (bitsPerPixel == 4) {
			    imageType = VERSION_3_4_BIT;
			} else if (bitsPerPixel == 8) {
			    imageType = VERSION_3_8_BIT;
			} else if (bitsPerPixel == 24) {
			    imageType = VERSION_3_24_BIT;
			} else if (bitsPerPixel == 16) {
			    imageType = VERSION_3_NT_16_BIT;
			    redMask = 0x7C00;
			    greenMask = 0x3E0;
			    blueMask = 0x1F;
			    properties.put("red_mask", new Integer(redMask));
			    properties.put("green_mask", new Integer(greenMask));
			    properties.put("blue_mask", new Integer(blueMask));
			    computeShifts();
			} else if (bitsPerPixel == 32) {
			    imageType = VERSION_3_NT_32_BIT;
			    redMask   = 0x00FF0000;
			    greenMask = 0x0000FF00;
			    blueMask  = 0x000000FF;
			    properties.put("red_mask", new Integer(redMask));
			    properties.put("green_mask", new Integer(greenMask));
			    properties.put("blue_mask", new Integer(blueMask));
			    computeShifts();
			}

			properties.put("bmp_version", "BMP v. 3.x");
			break;

		    case BI_BITFIELDS:

			if (bitsPerPixel == 16) {
			    imageType = VERSION_3_NT_16_BIT;
			} else if (bitsPerPixel == 32) {
			    imageType = VERSION_3_NT_32_BIT;
			}

			// BitsField encoding
			redMask = (int)readDWord(inputStream);
			greenMask = (int)readDWord(inputStream);
			blueMask = (int)readDWord(inputStream);

			properties.put("red_mask", new Integer(redMask));
			properties.put("green_mask", new Integer(greenMask));
			properties.put("blue_mask", new Integer(blueMask));

			computeShifts();

			if (colorsUsed != 0) {
			    // there is a palette
			    sizeOfPalette = (int)colorsUsed*4;
			    palette = new byte[sizeOfPalette];
			    inputStream.read(palette, 0, sizeOfPalette);
			    properties.put("palette", palette);
			}

			properties.put("bmp_version", "BMP v. 3.x NT");
			break;

		    default:
			throw new
			 RuntimeException(JaiI18N.getString("BMPImageDecoder1"));
		    }
		} else if (size == 108) {
		    // Windows 4.x BMP

		    properties.put("bmp_version", "BMP v. 4.x");

		    // rgb masks, valid only if comp is BI_BITFIELDS
		    redMask = (int)readDWord(inputStream);
		    greenMask = (int)readDWord(inputStream);
		    blueMask = (int)readDWord(inputStream);
		    // Only supported for 32bpp BI_RGB argb
		    int alphaMask = (int)readDWord(inputStream);
		    long csType = readDWord(inputStream);
		    int redX = readLong(inputStream);
		    int redY = readLong(inputStream);
		    int redZ = readLong(inputStream);
		    int greenX = readLong(inputStream);
		    int greenY = readLong(inputStream);
		    int greenZ = readLong(inputStream);
		    int blueX = readLong(inputStream);
		    int blueY = readLong(inputStream);
		    int blueZ = readLong(inputStream);
		    long gammaRed = readDWord(inputStream);
		    long gammaGreen = readDWord(inputStream);
		    long gammaBlue = readDWord(inputStream);

		    // Read in the palette
		    int numberOfEntries = (int)((bitmapOffset-14-size) / 4);
		    int sizeOfPalette = numberOfEntries*4;
		    palette = new byte[sizeOfPalette];
		    inputStream.read(palette, 0, sizeOfPalette);

		    if (palette != null || palette.length != 0) {
			properties.put("palette", palette);
		    }

		    switch((int)csType) {
		    case LCS_CALIBRATED_RGB:
			// All the new fields are valid only for this case
			properties.put("color_space", "LCS_CALIBRATED_RGB");
			properties.put("redX", new Integer(redX));
			properties.put("redY", new Integer(redY));
			properties.put("redZ", new Integer(redZ));
			properties.put("greenX", new Integer(greenX));
			properties.put("greenY", new Integer(greenY));
			properties.put("greenZ", new Integer(greenZ));
			properties.put("blueX", new Integer(blueX));
			properties.put("blueY", new Integer(blueY));
			properties.put("blueZ", new Integer(blueZ));
			properties.put("gamma_red", new Long(gammaRed));
			properties.put("gamma_green", new Long(gammaGreen));
			properties.put("gamma_blue", new Long(gammaBlue));

			// break;
			throw new
			 RuntimeException(JaiI18N.getString("BMPImageDecoder2"));

		    case LCS_sRGB:
			// Default Windows color space
			properties.put("color_space", "LCS_sRGB");
			break;

		    case LCS_CMYK:
			properties.put("color_space", "LCS_CMYK");
			//		    break;
			throw new
			 RuntimeException(JaiI18N.getString("BMPImageDecoder2"));
		    }

		    if (bitsPerPixel == 1) {
			imageType = VERSION_4_1_BIT;
		    } else if (bitsPerPixel == 4) {
			imageType = VERSION_4_4_BIT;
		    } else if (bitsPerPixel == 8) {
			imageType = VERSION_4_8_BIT;
		    } else if (bitsPerPixel == 16) {
			imageType = VERSION_4_16_BIT;
			if ((int)compression == BI_RGB) {
			    redMask = 0x7C00;
			    greenMask = 0x3E0;
			    blueMask = 0x1F;
			}
		    } else if (bitsPerPixel == 24) {
			imageType = VERSION_4_24_BIT;
		    } else if (bitsPerPixel == 32) {
			imageType = VERSION_4_32_BIT;
			if ((int)compression == BI_RGB) {
			    redMask   = 0x00FF0000;
			    greenMask = 0x0000FF00;
			    blueMask  = 0x000000FF;
			}
		    }

		    properties.put("red_mask", new Integer(redMask));
		    properties.put("green_mask", new Integer(greenMask));
		    properties.put("blue_mask", new Integer(blueMask));
		    properties.put("alpha_mask", new Integer(alphaMask));

		    computeShifts();
		} else {
		    properties.put("bmp_version", "BMP v. 5.x");
		    throw new
			RuntimeException(JaiI18N.getString("BMPImageDecoder4"));
		}
	    }
	} catch (IOException ioe) {
	    throw new RuntimeException(JaiI18N.getString("BMPImageDecoder5"));
	}

	if (height > 0) {
	    // bottom up image
	    isBottomUp = true;
	} else {
	    // top down image
	    isBottomUp = false;
	    height = Math.abs(height);
	}

	// Reset Image Layout so there's only one tile.
	tileWidth = width;
	tileHeight = height;

	// When number of bitsPerPixel is <= 8, we use IndexColorModel.
 	if (bitsPerPixel == 1 || bitsPerPixel == 4 || bitsPerPixel == 8) {

	    numBands = 1;

	    if (bitsPerPixel == 8) {
		sampleModel =
		    RasterFactory.createPixelInterleavedSampleModel(
							   DataBuffer.TYPE_BYTE,
							   width, height,
							   numBands);
	    } else {
		// 1 and 4 bit pixels can be stored in a packed format.
		sampleModel =
		    new MultiPixelPackedSampleModel(DataBuffer.TYPE_BYTE,
						    width, height,
						    bitsPerPixel);
	    }

	    // Create IndexColorModel from the palette.
	    byte r[], g[], b[];
	    int size;
	    if (imageType == VERSION_2_1_BIT ||
		imageType == VERSION_2_4_BIT ||
		imageType == VERSION_2_8_BIT) {

		size = palette.length/3;

		if (size > 256) {
		    size = 256;
		}

		int off;
		r = new byte[size];
		g = new byte[size];
		b = new byte[size];
		for (int i=0; i<size; i++) {
		    off = 3 * i;
		    b[i] = palette[off];
		    g[i] = palette[off+1];
		    r[i] = palette[off+2];
		}
	    } else {
		size = palette.length/4;

		if (size > 256) {
		    size = 256;
		}

		int off;
		r = new byte[size];
		g = new byte[size];
		b = new byte[size];
		for (int i=0; i<size; i++) {
		    off = 4 * i;
		    b[i] = palette[off];
		    g[i] = palette[off+1];
		    r[i] = palette[off+2];
		}
	    }

	    colorModel = new IndexColorModel(bitsPerPixel, size, r, g, b);

	} else {
	    numBands = 3;
	    // Create SampleModel
	    sampleModel =
		RasterFactory.createPixelInterleavedSampleModel(
                    DataBuffer.TYPE_BYTE, width, height, numBands);

            colorModel =
                ImageCodec.createComponentColorModel(sampleModel);
    	}
    }

    // Deal with 1 Bit images using IndexColorModels
    private void read1Bit(byte[] bdata, int paletteEntries) {

	int padding = 0;
	int bytesPerScanline = (int)Math.ceil(width/8.0);

	int remainder = bytesPerScanline % 4;
	if (remainder != 0) {
	    padding = 4 - remainder;
	}

	int imSize = (bytesPerScanline + padding) * height;

	// Read till we have the whole image
	byte values[] = new byte[imSize];
	try {
	    int bytesRead = 0;
	    while (bytesRead < imSize) {
		bytesRead += inputStream.read(values, bytesRead,
					      imSize - bytesRead);
	    }
	} catch (IOException ioe) {
	    throw new
		RuntimeException(JaiI18N.getString("BMPImageDecoder6"));
	}

	if (isBottomUp) {

	    // Convert the bottom up image to a top down format by copying
	    // one scanline from the bottom to the top at a time.

	    for (int i=0; i<height; i++) {
 		System.arraycopy(values,
				 imSize - (i+1)*(bytesPerScanline + padding),
				 bdata,
				 i*bytesPerScanline, bytesPerScanline);
	    }
	} else {

	   for (int i=0; i<height; i++) {
 		System.arraycopy(values,
				 i * (bytesPerScanline + padding),
				 bdata,
				 i * bytesPerScanline,
				 bytesPerScanline);
	    }
	}
    }

    // Method to read a 4 bit BMP image data
    private void read4Bit(byte[] bdata, int paletteEntries) {

	// Padding bytes at the end of each scanline
	int padding = 0;

	int bytesPerScanline = (int)Math.ceil(width/2.0);
	int remainder = bytesPerScanline % 4;
	if (remainder != 0) {
	    padding = 4 - remainder;
	}

	int imSize = (bytesPerScanline + padding) * height;

	// Read till we have the whole image
	byte values[] = new byte[imSize];
	try {
	    int bytesRead = 0;
	    while (bytesRead < imSize) {
		bytesRead += inputStream.read(values, bytesRead,
					      imSize - bytesRead);
	    }
	} catch (IOException ioe) {
	    throw new
		RuntimeException(JaiI18N.getString("BMPImageDecoder6"));
	}

	if (isBottomUp) {

	    // Convert the bottom up image to a top down format by copying
	    // one scanline from the bottom to the top at a time.
	    for (int i=0; i<height; i++) {
 		System.arraycopy(values,
				 imSize - (i+1)*(bytesPerScanline + padding),
				 bdata,
				 i*bytesPerScanline,
				 bytesPerScanline);
	    }
	} else {
	    for (int i=0; i<height; i++) {
 		System.arraycopy(values,
				 i * (bytesPerScanline + padding),
				 bdata,
				 i * bytesPerScanline,
				 bytesPerScanline);
	    }
	}
    }

    // Method to read 8 bit BMP image data
    private void read8Bit(byte[] bdata, int paletteEntries) {

	// Padding bytes at the end of each scanline
	int padding = 0;

	// width * bitsPerPixel should be divisible by 32
	int bitsPerScanline = width * 8;
	if ( bitsPerScanline%32 != 0) {
	    padding = (bitsPerScanline/32 + 1)*32 - bitsPerScanline;
	    padding = (int)Math.ceil(padding/8.0);
	}

	int imSize = (width + padding) * height;

	// Read till we have the whole image
	byte values[] = new byte[imSize];
	try {
	    int bytesRead = 0;
	    while (bytesRead < imSize) {
		bytesRead += inputStream.read(values, bytesRead,
					      imSize - bytesRead);
	    }
	} catch (IOException ioe) {
	    throw new
		RuntimeException(JaiI18N.getString("BMPImageDecoder6"));
	}

	if (isBottomUp) {

	    // Convert the bottom up image to a top down format by copying
	    // one scanline from the bottom to the top at a time.
	    for (int i=0; i<height; i++) {
 		System.arraycopy(values,
				 imSize - (i+1) * (width + padding),
				 bdata,
				 i * width,
				 width);
	    }
	} else {
	    for (int i=0; i<height; i++) {
 		System.arraycopy(values,
				 i * (width + padding),
				 bdata,
				 i * width,
				 width);
	    }
	}
    }

    // Method to read 24 bit BMP image data
    private void read24Bit(byte[] bdata) {
	// Padding bytes at the end of each scanline
	int padding = 0;

	// width * bitsPerPixel should be divisible by 32
	int bitsPerScanline = width * 24;
	if ( bitsPerScanline%32 != 0) {
	    padding = (bitsPerScanline/32 + 1)*32 - bitsPerScanline;
	    padding = (int)Math.ceil(padding/8.0);
	}

	int imSize = (int)imageSize;
	if (imSize == 0) {
	    imSize = (int)(bitmapFileSize - bitmapOffset);
	}

	// Read till we have the whole image
	byte values[] = new byte[imSize];
	try {
	    int bytesRead = 0;
	    while (bytesRead < imSize) {
		bytesRead += inputStream.read(values, bytesRead,
					      imSize - bytesRead);
	    }
	} catch (IOException ioe) {
	    // throw new RuntimeException(JaiI18N.getString("BMPImageDecoder6"));
	    throw new RuntimeException(ioe.getMessage());
	}

	int l=0, count;

	if (isBottomUp) {
	    int max = width*height*3-1;

	    count = -padding;
	    for (int i=0; i<height; i++) {
		l = max - (i+1)*width*3 + 1;
		count += padding;
		for (int j=0; j<width; j++) {
		    bdata[l++] = values[count++];
		    bdata[l++] = values[count++];
		    bdata[l++] = values[count++];
		}
	    }
	} else {
	    count = -padding;
	    for (int i=0; i<height; i++) {
		count += padding;
		for (int j=0; j<width; j++) {
		    bdata[l++] = values[count++];
		    bdata[l++] = values[count++];
		    bdata[l++] = values[count++];
		}
	    }
	}
    }

    private void read16Bit(byte bdata[]) {
	// Padding bytes at the end of each scanline
	int padding = 0;

	// width * bitsPerPixel should be divisible by 32
	int bitsPerScanline = width * 16;
	if ( bitsPerScanline%32 != 0) {
	    padding = (bitsPerScanline/32 + 1)*32 - bitsPerScanline;
	    padding = (int)Math.ceil(padding/8.0);
	}

	int imSize = (int)imageSize;
	if (imSize == 0) {
	    imSize = (int)(bitmapFileSize - bitmapOffset);
	}

	int l=0;

	try {
	    if (isBottomUp) {
		int max = width*height*3-1;

		for (int i=0; i<height; i++) {
		    l = max - (i+1)*width*3 + 1;
		    for (int j=0; j<width; j++) {
			int value = readWord(inputStream);

			bdata[l++] = (byte)((value & blueMask) >> blueBits);
			bdata[l++] = (byte)((value & greenMask) >> greenBits);
			bdata[l++] = (byte)((value & redMask) >> redBits);
		    }
		    for (int m=0; m<padding; m++) {
			inputStream.read();
		    }
		}
	    } else {
		for (int i=0; i<height; i++) {
		    for (int j=0; j<width; j++) {
			int value = readWord(inputStream);
			bdata[l++] = (byte)((value & blueMask) >> blueBits);
			bdata[l++] = (byte)((value & greenMask) >> greenBits);
			bdata[l++] = (byte)((value & redMask) >> redBits);
		    }
		    for (int m=0; m<padding; m++) {
			inputStream.read();
		    }
		}
	    }
	} catch (IOException ioe) {
	    throw new RuntimeException(JaiI18N.getString("BMPImageDecoder6"));
	}
    }

    private void read32Bit(byte bdata[]) {
	int imSize = (int)imageSize;
	if (imSize == 0) {
	    imSize = (int)(bitmapFileSize - bitmapOffset);
	}

	int l=0;

	try {
	    if (isBottomUp) {
		int max = width*height*3-1;

		for (int i=0; i<height; i++) {
		    l = max - (i+1)*width*3 + 1;
		    for (int j=0; j<width; j++) {
			int value = (int)readDWord(inputStream);

			bdata[l++] = (byte)((value & blueMask) >> blueBits);
			bdata[l++] = (byte)((value & greenMask) >> greenBits);
			bdata[l++] = (byte)((value & redMask) >> redBits);
		    }
		}
	    } else {
		for (int i=0; i<height; i++) {
		    for (int j=0; j<width; j++) {
			int value = (int)readDWord(inputStream);
			bdata[l++] = (byte)((value & blueMask) >> blueBits);
			bdata[l++] = (byte)((value & greenMask) >> greenBits);
			bdata[l++] = (byte)((value & redMask) >> redBits);
		    }
		}
	    }
	} catch (IOException ioe) {
	    throw new RuntimeException(JaiI18N.getString("BMPImageDecoder6"));
	}
    }

    private void readRLE8(byte bdata[]) {

	// If imageSize field is not provided, calculate it.
	int imSize = (int)imageSize;
	if (imSize == 0) {
	    imSize = (int)(bitmapFileSize - bitmapOffset);
	}

	int padding = 0;
	// If width is not 32 bit aligned, then while uncompressing each
	// scanline will have padding bytes, calculate the amount of padding
	int remainder = width % 4;
	if (remainder != 0) {
	    padding = 4 - remainder;
	}

	// Read till we have the whole image
	byte values[] = new byte[imSize];
	try {
	    int bytesRead = 0;
	    while (bytesRead < imSize) {
		bytesRead += inputStream.read(values, bytesRead,
					      imSize - bytesRead);
	    }
	} catch (IOException ioe) {
	    throw new RuntimeException(JaiI18N.getString("BMPImageDecoder6"));
	}

	// Since data is compressed, decompress it
	byte val[] = decodeRLE8(imSize, padding, values);

	// Uncompressed data does not have any padding
        imSize = width * height;

	if (isBottomUp) {

	    // Convert the bottom up image to a top down format by copying
	    // one scanline from the bottom to the top at a time.
	    // int bytesPerScanline = (int)Math.ceil((double)width/8.0);
	    int bytesPerScanline = width;
	    for (int i=0; i<height; i++) {
		System.arraycopy(val,
				 imSize - (i+1)*(bytesPerScanline),
				 bdata,
				 i*bytesPerScanline, bytesPerScanline);
	    }

	} else {

	    bdata = val;
	}
    }

    private byte[] decodeRLE8(int imSize, int padding, byte values[]) {

	byte val[] = new byte[width * height];
	int count = 0, l = 0;
	int value;
	boolean flag = false;

	while (count != imSize) {

	    value = values[count++] & 0xff;

	    if (value == 0) {
		switch(values[count++] & 0xff) {

		case 0:
		    // End-of-scanline marker
		    break;

		case 1:
		    // End-of-RLE marker
		    flag = true;
		    break;

		case 2:
		    // delta or vector marker
		    int xoff = values[count++] & 0xff;
		    int yoff = values[count] & 0xff;
		    // Move to the position xoff, yoff down
		    l += xoff + yoff*width;
		    break;

		default:
		    int end = values[count-1] & 0xff;
		    for (int i=0; i<end; i++) {
			val[l++] = (byte)(values[count++] & 0xff);
		    }

		    // Whenever end pixels can fit into odd number of bytes,
		    // an extra padding byte will be present, so skip that.
		    if (!isEven(end)) {
			count++;
		    }
		}
	    } else {
		for (int i=0; i<value; i++) {
		    val[l++] = (byte)(values[count] & 0xff);
		}
		count++;
	    }

	    // If End-of-RLE data, then exit the while loop
	    if (flag) {
		break;
	    }
	}

	return val;
    }

    private int[] readRLE4() {

	// If imageSize field is not specified, calculate it.
	int imSize = (int)imageSize;
	if (imSize == 0) {
	    imSize = (int)(bitmapFileSize - bitmapOffset);
	}

	int padding = 0;
	// If width is not 32 byte aligned, then while uncompressing each
	// scanline will have padding bytes, calculate the amount of padding
	int remainder = width % 4;
	if (remainder != 0) {
	    padding = 4 - remainder;
	}

	// Read till we have the whole image
	int values[] = new int[imSize];
	try {
	    for (int i=0; i<imSize; i++) {
		values[i] = inputStream.read();
	    }
	} catch(IOException ioe) {
	    throw new RuntimeException(JaiI18N.getString("BMPImageDecoder6"));
	}

	// Decompress the RLE4 compressed data.
	int val[] = decodeRLE4(imSize, padding, values);

	// Invert it as it is bottom up format.
	if (isBottomUp) {

	    int inverted[] = val;
	    val = new int[width * height];
	    int l = 0, index, lineEnd;

	    for (int i = height-1; i >= 0; i--) {
		index = i * width;
		lineEnd = l + width;
		while(l != lineEnd) {
		    val[l++] = inverted[index++];
		}
	    }
	}

	// This array will be used to call setPixels as the decompression
	// had unpacked the 4bit pixels each into an int.
	return val;
    }

    private int[] decodeRLE4(int imSize, int padding, int values[]) {

	int val[] = new int[width * height];
 	int count = 0, l = 0;
	int value;
	boolean flag = false;

	while (count != imSize) {

	    value = values[count++];

	    if (value == 0) {

		// Absolute mode
		switch(values[count++]) {

		case 0:
		    // End-of-scanline marker
		    break;

		case 1:
		    // End-of-RLE marker
		    flag = true;
		    break;

		case 2:
		    // delta or vector marker
		    int xoff = values[count++];
		    int yoff = values[count];
		    // Move to the position xoff, yoff down
		    l += xoff + yoff*width;
		    break;

		default:
		    int end = values[count-1];
		    for (int i=0; i<end; i++) {
			val[l++] = isEven(i) ? (values[count] & 0xf0) >> 4
			                     : (values[count++] & 0x0f);
		    }

		    // When end is odd, the above for loop does not
		    // increment count, so do it now.
		    if (!isEven(end)) {
			count++;
		    }

		    // Whenever end pixels can fit into odd number of bytes,
		    // an extra padding byte will be present, so skip that.
		    if ( !isEven((int)Math.ceil(end/2)) ) {
			count++;
		    }
		    break;
		}
	    } else {
		// Encoded mode
		int alternate[] = { (values[count] & 0xf0) >> 4,
				    values[count] & 0x0f };
		for (int i=0; i<value; i++) {
		    val[l++] = alternate[i%2];
		}

		count++;
	    }

	    // If End-of-RLE data, then exit the while loop
	    if (flag) {
		break;
	    }

	}

	return val;
    }

    private boolean isEven(int number) {
	return (number%2 == 0 ? true : false);
    }

    private void computeShifts() {
	redBits = computeShift(redMask);
	greenBits = computeShift(greenMask);
	blueBits = computeShift(blueMask);
    }

    private int computeShift(int hexNumber) {
	int factor = 0x1;
	int result = 0, count=0;

	while (result == 0) {
	    result = hexNumber & factor;
	    count++;
	    factor = factor << 1;
	}

	return (count-1);
    }

    // Windows defined data type reading methods - everything is little endian

    // Unsigned 8 bits
    private int readUnsignedByte(InputStream stream) throws IOException {
	return (stream.read() & 0xff);
    }

    // Unsigned 2 bytes
    private int readUnsignedShort(InputStream stream) throws IOException {
	int b1 = readUnsignedByte(stream);
	int b2 = readUnsignedByte(stream);
	return ((b2 << 8) | b1) & 0xffff;
    }

    // Signed 16 bits
    private int readShort(InputStream stream) throws IOException {
	int b1 = readUnsignedByte(stream);
	int b2 = readUnsignedByte(stream);
	return (b2 << 8) | b1;
    }

    // Unsigned 16 bits
    private int readWord(InputStream stream) throws IOException {
	return readUnsignedShort(stream);
    }

    // Unsigned 4 bytes
    private long readUnsignedInt(InputStream stream) throws IOException {
	int b1 = readUnsignedByte(stream);
        int b2 = readUnsignedByte(stream);
        int b3 = readUnsignedByte(stream);
        int b4 = readUnsignedByte(stream);
	long l = ((b4 << 24) | (b3 << 16) | (b2 << 8) | b1);
	return l & 0xffffffff;
    }

    // Signed 4 bytes
    private int readInt(InputStream stream) throws IOException {
	int b1 = readUnsignedByte(stream);
        int b2 = readUnsignedByte(stream);
        int b3 = readUnsignedByte(stream);
        int b4 = readUnsignedByte(stream);
	return (b4 << 24) | (b3 << 16) | (b2 << 8) | b1;
    }

    // Unsigned 4 bytes
    private long readDWord(InputStream stream) throws IOException {
	return readUnsignedInt(stream);
    }

    // 32 bit signed value
    private int readLong(InputStream stream) throws IOException {
	return readInt(stream);
    }

    private synchronized Raster computeTile(int tileX, int tileY) {
        if (theTile != null) {
            return theTile;
        }

	// Create a new tile
	Point org = new Point(tileXToX(tileX), tileYToY(tileY));
	WritableRaster tile =
	    RasterFactory.createWritableRaster(sampleModel, org);
	DataBufferByte buffer = (DataBufferByte)tile.getDataBuffer();
	byte bdata[] = buffer.getData();

	// There should only be one tile.
	switch(imageType) {

	case VERSION_2_1_BIT:
	    // no compression
	    read1Bit(bdata, 3);
	    break;

	case VERSION_2_4_BIT:
	    // no compression
	    read4Bit(bdata, 3);
	    break;

	case VERSION_2_8_BIT:
	    // no compression
	    read8Bit(bdata, 3);
	    break;

	case VERSION_2_24_BIT:
	    // no compression
	    read24Bit(bdata);
	    break;

	case VERSION_3_1_BIT:
	    // 1-bit images cannot be compressed.
	    read1Bit(bdata, 4);
	    break;

	case VERSION_3_4_BIT:
	    switch((int)compression) {
	    case BI_RGB:
		read4Bit(bdata, 4);
		break;

	    case BI_RLE4:
		int pixels[] = readRLE4();
		tile.setPixels(0, 0, width, height, pixels);
		break;

	    default:
		throw new
		    RuntimeException(JaiI18N.getString("BMPImageDecoder3"));
	    }
	    break;

	case VERSION_3_8_BIT:
	    switch((int)compression) {
	    case BI_RGB:
		read8Bit(bdata, 4);
		    break;

	    case BI_RLE8:
		readRLE8(bdata);
		break;

	    default:
		throw new
		    RuntimeException(JaiI18N.getString("BMPImageDecoder3"));
	    }

	    break;

	case VERSION_3_24_BIT:
	    // 24-bit images are not compressed
	    read24Bit(bdata);
	    break;

	case VERSION_3_NT_16_BIT:
	    read16Bit(bdata);
	    break;

	case VERSION_3_NT_32_BIT:
	    read32Bit(bdata);
	    break;

	case VERSION_4_1_BIT:
	    read1Bit(bdata, 4);
	    break;

	case VERSION_4_4_BIT:
	    switch((int)compression) {

	    case BI_RGB:
		read4Bit(bdata, 4);
		break;

	    case BI_RLE4:
		int pixels[] = readRLE4();
		tile.setPixels(0, 0, width, height, pixels);
		break;

	    default:
		throw new
		    RuntimeException(JaiI18N.getString("BMPImageDecoder3"));
	    }

	case VERSION_4_8_BIT:
	    switch((int)compression) {

	    case BI_RGB:
		read8Bit(bdata, 4);
		break;

	    case BI_RLE8:
		readRLE8(bdata);
		break;

	    default:
		throw new
		    RuntimeException(JaiI18N.getString("BMPImageDecoder3"));
	    }
	    break;

	case VERSION_4_16_BIT:
	    read16Bit(bdata);
	    break;

	case VERSION_4_24_BIT:
	    read24Bit(bdata);
	    break;

	case VERSION_4_32_BIT:
	    read32Bit(bdata);
	    break;
	}

        theTile = tile;

	return tile;
    }

    public synchronized Raster getTile(int tileX, int tileY) {
        if ((tileX != 0) || (tileY != 0)) {
            throw new
		IllegalArgumentException(JaiI18N.getString("BMPImageDecoder7"));
        }
        return computeTile(tileX, tileY);
    }
}
