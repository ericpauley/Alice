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
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;

public class TIFFImage extends SimpleRenderedImage {

    private static final boolean DEBUG = true;

    SeekableStream stream;
    int tileSize;
    int tilesX, tilesY;
    long[] tileOffsets;
    long tileByteCounts[];
    char colormap[];
    char bitsPerSample[];
    int samplesPerPixel;
    int extraSamples;
    int compression;
    byte palette[];
    int bands;
    char sampleFormat[];

    // Fax compression related variables
    long tiffT4Options;
    long tiffT6Options;
    int fillOrder;
    boolean decodePaletteAsShorts;

    boolean isBigEndian;

    // LZW compression related variable
    int predictor;

    // Image types
    int image_type;
    int dataType;

    private static final int TYPE_BILEVEL_WHITE_IS_ZERO      = 0;
    private static final int TYPE_BILEVEL_BLACK_IS_ZERO      = 1;
    private static final int TYPE_GREYSCALE_WHITE_IS_ZERO    = 2;
    private static final int TYPE_GREYSCALE_BLACK_IS_ZERO    = 3;
    private static final int TYPE_RGB                        = 4;
    private static final int TYPE_ARGB_PRE                   = 5;
    private static final int TYPE_ARGB                       = 6;
    private static final int TYPE_ORGB                       = 7;
    private static final int TYPE_RGB_EXTRA                  = 8;
    private static final int TYPE_PALETTE                    = 9;
    private static final int TYPE_TRANS                      = 10;

    // Compression types
    public static final int COMP_NONE                        = 1;
    public static final int COMP_FAX_G3_1D                   = 2;
    public static final int COMP_FAX_G3_2D                   = 3;
    public static final int COMP_FAX_G4_2D                   = 4;
    public static final int COMP_LZW                         = 5;
    public static final int COMP_PACKBITS                    = 32773;

    // Decoders
    private TIFFFaxDecoder decoder = null;
    private TIFFLZWDecoder lzwDecoder = null;

    /**
     * Constructs a TIFFImage that acquires its data from a given
     * SeekableStream and reads from a particular IFD of the stream.
     * The index of the first IFD is 0.
     *
     * @param stream the SeekableStream to read from.
     * @param param an instance of TIFFDecodeParam, or null.
     * @param directory the index of the IFD to read from.
     */
    public TIFFImage(SeekableStream stream,
                     TIFFDecodeParam param,
                     int directory)
        throws IOException {

        this.stream = stream;
	if (param == null) {
	    param = new TIFFDecodeParam();
	}

	decodePaletteAsShorts = param.getDecodePaletteAsShorts();

        // Read the specified directory.
        TIFFDirectory dir = new TIFFDirectory(stream, directory);

	// Check whether big endian or little endian format is used.
	isBigEndian = dir.isBigEndian();

	// Set basic image layout
        minX = minY = 0;
        width = (int)dir.getFieldAsLong(TIFFImageDecoder.TIFF_IMAGE_WIDTH);
        height = (int)dir.getFieldAsLong(TIFFImageDecoder.TIFF_IMAGE_LENGTH);

	int photometric_interp = (int)dir.getFieldAsLong(
			      TIFFImageDecoder.TIFF_PHOTOMETRIC_INTERPRETATION);

	// Read the TIFF_BITS_PER_SAMPLE field
	TIFFField bitsField =
	    dir.getField(TIFFImageDecoder.TIFF_BITS_PER_SAMPLE);

	if (bitsField == null) {
	    // Default
     	    bitsPerSample = new char[1];
	    bitsPerSample[0] = 1;
	} else {
	    bitsPerSample = bitsField.getAsChars();
	}

	for (int i = 1; i < bitsPerSample.length; i++) {
	    if (bitsPerSample[i] != bitsPerSample[1]) {
		throw new RuntimeException(
					JaiI18N.getString("TIFFImageDecoder19"));
	    }
	}

	// Get the number of samples per pixel
	TIFFField sfield = dir.getField(TIFFImageDecoder.TIFF_SAMPLES_PER_PIXEL);
	if (sfield == null) {
	    samplesPerPixel = 1;
	} else {
	    samplesPerPixel = (int)sfield.getAsLong(0);
	}

	// Figure out if any extra samples are present.
	TIFFField efield = dir.getField(TIFFImageDecoder.TIFF_EXTRA_SAMPLES);
	if (efield == null) {
	    extraSamples = 0;
	} else {
	    extraSamples = (int)efield.getAsLong(0);
	}

	// Read the TIFF_SAMPLE_FORMAT tag to see whether the data might be
	// signed or floating point
	TIFFField sampleFormatField =
	    dir.getField(TIFFImageDecoder.TIFF_SAMPLE_FORMAT);

	if (sampleFormatField != null) {
	    sampleFormat = sampleFormatField.getAsChars();

	    // Check that all the samples have the same format
	    for (int l=1; l<sampleFormat.length; l++) {
		if (sampleFormat[l] != sampleFormat[0]) {
		    throw new RuntimeException(
					JaiI18N.getString("TIFFImageDecoder20"));
		}
	    }

	} else {
	    sampleFormat = new char[] {1};
	}

	if (sampleFormat[0] == 1 || sampleFormat[0] == 4) {

	    // Unsigned or unknown
	    if (bitsPerSample[0] == 8) {
		dataType = DataBuffer.TYPE_BYTE;
	    } else if (bitsPerSample[0] == 16) {
		dataType = DataBuffer.TYPE_USHORT;
	    } else if (bitsPerSample[0] == 32) {
		dataType = DataBuffer.TYPE_INT;
	    }

	} else if (sampleFormat[0] == 2) {
	    // Signed

	    if (bitsPerSample[0] == 1 || bitsPerSample[0] == 4 ||
		bitsPerSample[0] == 8) {

		throw new
		    RuntimeException(JaiI18N.getString("TIFFImageDecoder21"));

	    } else if (bitsPerSample[0] == 16) {
		dataType = DataBuffer.TYPE_SHORT;
	    } else if (bitsPerSample[0] == 32) {
		dataType = DataBuffer.TYPE_INT;
	    }

	} else if (sampleFormat[0] == 3) {
	    // Floating point
	    //	    dataType = DataBuffer.TYPE_FLOAT;
	    throw new RuntimeException(JaiI18N.getString("TIFFImageDecoder22"));
	}

	if (dir.getField(TIFFImageDecoder.TIFF_TILE_WIDTH) != null) {
	    // Image is in tiled format
            tileWidth =
		(int)dir.getFieldAsLong(TIFFImageDecoder.TIFF_TILE_WIDTH);
            tileHeight =
		(int)dir.getFieldAsLong(TIFFImageDecoder.TIFF_TILE_LENGTH);
	    tileOffsets =
		(dir.getField(TIFFImageDecoder.TIFF_TILE_OFFSETS)).getAsLongs();
	    tileByteCounts =
	       dir.getField(TIFFImageDecoder.TIFF_TILE_BYTE_COUNTS).getAsLongs();

        } else {

            // Image is in stripped format, looks like tiles to us
            tileWidth = width;
	    TIFFField field = dir.getField(TIFFImageDecoder.TIFF_ROWS_PER_STRIP);
	    if (field == null) {
		// Default is infinity (2^32 -1), basically the entire image
		// TODO: Can do a better job of tiling here
		tileHeight = height;
	    } else {
		long l = field.getAsLong(0);
		long infinity = 1;
		infinity = (infinity << 32) - 1;
		if (l == infinity) {
		    // 2^32 - 1 (effectively infinity, entire image is 1 strip)
		    tileHeight = height;
		} else {
		    tileHeight = (int)l;
		}
	    }

	    TIFFField tileOffsetsField =
		dir.getField(TIFFImageDecoder.TIFF_STRIP_OFFSETS);
	    if (tileOffsetsField == null) {
		throw new
		    RuntimeException(JaiI18N.getString("TIFFImageDecoder11"));
	    } else {
		tileOffsets = tileOffsetsField.getAsLongs();
	    }

	    TIFFField tileByteCountsField =
		dir.getField(TIFFImageDecoder.TIFF_STRIP_BYTE_COUNTS);
	    if (tileByteCountsField == null) {
		throw new
		    RuntimeException(JaiI18N.getString("TIFFImageDecoder12"));
	    } else {
		tileByteCounts = tileByteCountsField.getAsLongs();
	    }
	}

	TIFFField fillOrderField =
	    dir.getField(TIFFImageDecoder.TIFF_FILL_ORDER);
	if (fillOrderField != null) {
	    fillOrder = fillOrderField.getAsInt(0);
	} else {
	    // Default Fill Order
	    fillOrder = 1;
	}

	// Figure out which kind of image we are dealing with.
	switch(photometric_interp) {

	case 0:

	    bands = 1;

	    // Bilevel or Grayscale - WhiteIsZero
	    if (bitsPerSample[0] == 1) {

		image_type = TYPE_BILEVEL_WHITE_IS_ZERO;

		// Keep pixels packed, use IndexColorModel
		sampleModel =
		    new MultiPixelPackedSampleModel(DataBuffer.TYPE_BYTE,
						    tileWidth,
						    tileHeight, 1);

		// Set up the palette
		byte r[] = new byte[] {(byte)255, (byte)0};
		byte g[] = new byte[] {(byte)255, (byte)0};
		byte b[] = new byte[] {(byte)255, (byte)0};

		colorModel = new IndexColorModel(1, 2, r, g, b);

	    } else {

		image_type = TYPE_GREYSCALE_WHITE_IS_ZERO;

		if (bitsPerSample[0] == 4) {
		    sampleModel =
			new MultiPixelPackedSampleModel(DataBuffer.TYPE_BYTE,
							tileWidth,
							tileHeight,
							4);

		    colorModel =
			ImageCodec.createGrayIndexColorModel(sampleModel, false);

		} else if (bitsPerSample[0] == 8) {
		    sampleModel =
			RasterFactory.createPixelInterleavedSampleModel(
							   DataBuffer.TYPE_BYTE,
							   tileWidth,
							   tileHeight,
							   bands);

		    colorModel =
			ImageCodec.createGrayIndexColorModel(sampleModel, false);

		} else if (bitsPerSample[0] == 16) {

		    sampleModel =
			RasterFactory.createPixelInterleavedSampleModel(
								      dataType,
								      tileWidth,
								      tileHeight,
								      bands);

		    colorModel =
			ImageCodec.createComponentColorModel(sampleModel);

		} else {
		    throw new IllegalArgumentException(
					JaiI18N.getString("TIFFImageDecoder14"));
		}
	    }

	    break;

	case 1:

	    bands = 1;

	    // Bilevel or Grayscale - BlackIsZero
	    if (bitsPerSample[0] == 1) {

		image_type = TYPE_BILEVEL_BLACK_IS_ZERO;

		// Keep pixels packed, use IndexColorModel
		sampleModel =
		    new MultiPixelPackedSampleModel(DataBuffer.TYPE_BYTE,
						    tileWidth,
						    tileHeight, 1);

		// Set up the palette
		byte r[] = new byte[] {(byte)0, (byte)255};
		byte g[] = new byte[] {(byte)0, (byte)255};
		byte b[] = new byte[] {(byte)0, (byte)255};

		// 1 Bit pixels packed into a byte, use IndexColorModel
		colorModel = new IndexColorModel(1, 2, r, g, b);

	    } else {

		image_type = TYPE_GREYSCALE_BLACK_IS_ZERO;

		if (bitsPerSample[0] == 4) {
		    sampleModel =
			new MultiPixelPackedSampleModel(DataBuffer.TYPE_BYTE,
							tileWidth,
							tileHeight,
							4);
		    colorModel =
			ImageCodec.createGrayIndexColorModel(sampleModel,
							     true);
		} else if (bitsPerSample[0] == 8) {
		    sampleModel =
			RasterFactory.createPixelInterleavedSampleModel(
							DataBuffer.TYPE_BYTE,
							tileWidth,
							tileHeight,
							bands);
		    colorModel =
			ImageCodec.createComponentColorModel(sampleModel);

		} else if (bitsPerSample[0] == 16) {

		    sampleModel =
			RasterFactory.createPixelInterleavedSampleModel(
								     dataType,
								     tileWidth,
								     tileHeight,
								     bands);
		    colorModel =
			ImageCodec.createComponentColorModel(sampleModel);

		} else {
		    throw new IllegalArgumentException(
					JaiI18N.getString("TIFFImageDecoder14"));
		}
	    }

	    break;

	case 2:
	    // RGB full color

	    bands = samplesPerPixel;

	    // RGB full color image
	    if (bitsPerSample[0] == 8) {

		sampleModel = RasterFactory.createPixelInterleavedSampleModel(
							  DataBuffer.TYPE_BYTE,
							  tileWidth,
							  tileHeight,
							  bands);
	    } else if (bitsPerSample[0] == 16) {

		sampleModel = RasterFactory.createPixelInterleavedSampleModel(
								     dataType,
								     tileWidth,
								     tileHeight,
								     bands);
	    } else {
		throw new
		    RuntimeException(JaiI18N.getString("TIFFImageDecoder15"));
	    }

	    if (samplesPerPixel < 3) {
		throw new
		    RuntimeException(JaiI18N.getString("TIFFImageDecoder1"));

	    } else if (samplesPerPixel == 3) {

		image_type = TYPE_RGB;
		// No alpha
		colorModel = ImageCodec.createComponentColorModel(sampleModel);

	    } else if (samplesPerPixel == 4) {

		if (extraSamples == 0) {

		    image_type = TYPE_ORGB;
		    // Transparency.OPAQUE signifies image data that is
		    // completely opaque, meaning that all pixels have an alpha
		    // value of 1.0. So the extra band gets ignored, which is
		    // what we want.
		    colorModel =
			createAlphaComponentColorModel(dataType, true,
						       false,
						       Transparency.OPAQUE);

		} else if (extraSamples == 1) {

		    image_type = TYPE_ARGB_PRE;
		    // Pre multiplied alpha.
		    colorModel =
			createAlphaComponentColorModel(dataType, true, true,
						       Transparency.TRANSLUCENT);

		} else if (extraSamples == 2) {

		    image_type = TYPE_ARGB;
		    // The extra sample here is unassociated alpha, usually a
		    // transparency mask, also called soft matte.
		    colorModel = createAlphaComponentColorModel(
							   dataType,
							   true,
							   false,
							   Transparency.BITMASK);
		}

	    } else {
		image_type = TYPE_RGB_EXTRA;

		// For this case we can't display the image, so there is no
		// point in trying to reformat the data to be BGR followed by
		// the ExtraSamples, the way Java2D would like it, because
		// Java2D can't display it anyway. Therefore create a sample
		// model with increasing bandOffsets, and keep the colorModel
		// as null, as there is no appropriate ColorModel.

		int bandOffsets[] = new int[bands];
		for (int i=0; i<bands; i++) {
		    bandOffsets[i] = i;
		}

		if (bitsPerSample[0] == 8) {

		    sampleModel =
			new PixelInterleavedSampleModel(DataBuffer.TYPE_BYTE,
							tileWidth, tileHeight,
							bands, bands * tileWidth,
							bandOffsets);
		    colorModel = null;

		} else if (bitsPerSample[0] == 16) {

		    sampleModel =
			new PixelInterleavedSampleModel(dataType,
							tileWidth,
							tileHeight,
							bands, bands * tileWidth,
							bandOffsets);
		    colorModel = null;
		}
	    }

	    break;

	case 3:

	    // Palette color
	    image_type = TYPE_PALETTE;

	    // Get the colormap
	    TIFFField cfield = dir.getField(TIFFImageDecoder.TIFF_COLORMAP);
	    if (cfield == null) {
		throw new
		    RuntimeException(JaiI18N.getString("TIFFImageDecoder2"));
	    } else {
		colormap = cfield.getAsChars();
	    }

	    // Could be either 1 or 3 bands depending on whether we use
	    // IndexColorModel or not.
	    if (decodePaletteAsShorts) {
		bands = 3;

		if (bitsPerSample[0] != 4 && bitsPerSample[0] != 8 &&
		    bitsPerSample[0] != 16) {
		    throw new RuntimeException(
					JaiI18N.getString("TIFFImageDecoder13"));
		}

		// If no SampleFormat tag was specified and if the
		// bitsPerSample are less than or equal to 8, then the
		// dataType was initially set to byte, but now we want to
		// expand the palette as shorts, so the dataType should
		// be ushort.
		if (dataType == DataBuffer.TYPE_BYTE) {
		    dataType = DataBuffer.TYPE_USHORT;
		}

		// Data will have to be unpacked into a 3 band short image
		// as we do not have a IndexColorModel that can deal with
		// a colormodel whose entries are of short data type.
		sampleModel =
		    RasterFactory.createPixelInterleavedSampleModel(dataType,
								    tileWidth,
								    tileHeight,
								    bands);
		colorModel = ImageCodec.createComponentColorModel(sampleModel);

	    } else {

		bands = 1;

		if (bitsPerSample[0] == 4) {
		    // Pixel data will not be unpacked, will use MPPSM to store
		    // packed data and IndexColorModel to do the unpacking.
		    sampleModel =
			new MultiPixelPackedSampleModel(DataBuffer.TYPE_BYTE,
							tileWidth,
							tileHeight,
							bitsPerSample[0]);
		} else if (bitsPerSample[0] == 8) {
		    sampleModel =
			RasterFactory.createPixelInterleavedSampleModel(
							    DataBuffer.TYPE_BYTE,
							    tileWidth,
							    tileHeight,
							    bands);
		} else if (bitsPerSample[0] == 16) {

		    // Here datatype has to be unsigned since we are storing
		    // indices into the IndexColorModel palette. Ofcourse
		    // the actual palette entries are allowed to be negative.
		    sampleModel =
			RasterFactory.createPixelInterleavedSampleModel(
							DataBuffer.TYPE_USHORT,
							tileWidth,
							tileHeight,
							bands);
		} else {
		    throw new RuntimeException(
					JaiI18N.getString("TIFFImageDecoder13"));
		}

		int bandLength = colormap.length/3;
		byte r[] = new byte[bandLength];
		byte g[] = new byte[bandLength];
		byte b[] = new byte[bandLength];

		int gIndex = bandLength;
		int bIndex = bandLength * 2;

		if (dataType == DataBuffer.TYPE_SHORT) {

		    for (int i=0; i<bandLength; i++) {
			r[i] = param.decodeSigned16BitsTo8Bits(
					 	     (short)colormap[i]);
			g[i] = param.decodeSigned16BitsTo8Bits(
						     (short)colormap[gIndex+i]);
			b[i] = param.decodeSigned16BitsTo8Bits(
						     (short)colormap[bIndex+i]);
		    }

		} else {

		    for (int i=0; i<bandLength; i++) {
			r[i] = param.decode16BitsTo8Bits(colormap[i] & 0xffff);
			g[i] = param.decode16BitsTo8Bits(colormap[gIndex+i] &
							 0xffff);
			b[i] = param.decode16BitsTo8Bits(colormap[bIndex+i] &
							 0xffff);
		    }

		}

		colorModel = new IndexColorModel(bitsPerSample[0],
						 bandLength, r, g, b);
	    }

	    break;

	case 4:
	    image_type = TYPE_TRANS;

	    // Transparency Mask
	    throw new RuntimeException(JaiI18N.getString("TIFFImageDecoder3"));
	    //	    break;

	default:
	    throw new RuntimeException(JaiI18N.getString("TIFFImageDecoder4"));
	}

	// Calculate number of tiles and the tileSize in bytes
        tilesX = (width + tileWidth - 1)/tileWidth;
        tilesY = (height + tileHeight - 1)/tileHeight;
        tileSize = tileWidth * tileHeight * bands;

	// Figure out what compression if any, is being used.
	TIFFField compField = dir.getField(TIFFImageDecoder.TIFF_COMPRESSION);
	if (compField != null) {

	    compression = compField.getAsInt(0);

	    // Fax T.4 compression options
	    if (compression == 3) {
		TIFFField t4OptionsField =
		    dir.getField(TIFFImageDecoder.TIFF_T4_OPTIONS);
		if (t4OptionsField != null) {
		    tiffT4Options = t4OptionsField.getAsLong(0);
		} else {
		    // Use default value
		    tiffT4Options = 0;
		}
	    }

	    // Fax T.6 compression options
	    if (compression == 4) {
		TIFFField t6OptionsField =
		    dir.getField(TIFFImageDecoder.TIFF_T6_OPTIONS);
		if (t6OptionsField != null) {
		    tiffT6Options = t6OptionsField.getAsLong(0);
		} else {
		    // Use default value
		    tiffT6Options = 0;
		}
	    }

	    // Fax encoding, need to create the Fax decoder.
	    if (compression == 2 || compression == 3 || compression == 4) {
		decoder = new TIFFFaxDecoder(fillOrder,
                                             tileWidth, tileHeight);
	    }

	    // LZW compression used, need to create the LZW decoder.
	    if (compression == 5) {
		TIFFField predictorField =
		    dir.getField(TIFFImageDecoder.TIFF_PREDICTOR);

		if (predictorField == null) {
		    predictor = 1;
		} else {
		    predictor = predictorField.getAsInt(0);

		    if (predictor != 1 && predictor != 2) {
			throw new RuntimeException(
				JaiI18N.getString("TIFFImageDecoder16"));
		    }

		    if (predictor == 2 && bitsPerSample[0] != 8) {
			throw new RuntimeException(bitsPerSample[0] +
					JaiI18N.getString("TIFFImageDecoder17"));
		    }
		}

		lzwDecoder = new TIFFLZWDecoder(tileWidth, predictor,
						samplesPerPixel);
	    }

	    if (compression != COMP_NONE &&
		compression != COMP_PACKBITS &&
		compression != COMP_FAX_G3_1D &&
		compression != COMP_FAX_G3_2D &&
		compression != COMP_FAX_G4_2D &&
		compression != COMP_LZW) {
		throw new
		    RuntimeException(JaiI18N.getString("TIFFImageDecoder0"));
	    }

	} else {
	    compression = COMP_NONE;
	}
    }

    /**
     * Reads a private IFD from a given offset in the stream.  This
     * method may be used to obtain IFDs that are referenced
     * only by private tag values.
     */
    public TIFFDirectory getPrivateIFD(long offset) throws IOException {
        return new TIFFDirectory(stream, offset);
    }

    private WritableRaster tile00 = null;

    /**
     * Returns tile (tileX, tileY) as a Raster.
     */
    public synchronized Raster getTile(int tileX, int tileY) {
        if (tileX == 0 && tileY == 0 && tile00 != null) {
            return tile00;
        }

        if ((tileX < 0) || (tileX >= tilesX) ||
            (tileY < 0) || (tileY >= tilesY)) {
            throw new
		IllegalArgumentException(JaiI18N.getString("TIFFImageDecoder5"));
        }

	// Get the data array out of the DataBuffer
	byte bdata[] = null;
	short sdata[] = null;
	int idata[] = null;
	DataBuffer buffer = sampleModel.createDataBuffer();

	int dataType = sampleModel.getDataType();
	if (dataType == DataBuffer.TYPE_BYTE) {
	    bdata = ((DataBufferByte)buffer).getData();
	} else if (dataType == DataBuffer.TYPE_USHORT) {
	    sdata = ((DataBufferUShort)buffer).getData();
	} else if (dataType == DataBuffer.TYPE_SHORT) {
	    sdata = ((DataBufferShort)buffer).getData();
	}

        WritableRaster tile =
	    RasterFactory.createWritableRaster(sampleModel,
                                                   buffer,
                                                   new Point(tileXToX(tileX),
                                                             tileYToY(tileY)));

	// Variables used for swapping when converting from RGB to BGR
	byte bswap;
	short sswap;

	// Save original file pointer position and seek to tile data location.
	long save_offset = 0;
	try {
	    save_offset = stream.getFilePointer();
	    stream.seek(tileOffsets[tileY*tilesX + tileX]);
	} catch (IOException ioe) {
	    throw new RuntimeException(JaiI18N.getString("TIFFImageDecoder8"));
	}

	// Number of bytes in this tile (strip) after compression.
	int byteCount = (int)tileByteCounts[tileY*tilesX + tileX];

	// Find out the number of bytes in the current tile
	Rectangle tileRect = new Rectangle(tileXToX(tileX), tileYToY(tileY),
					   tileWidth, tileHeight);
	Rectangle newRect = tileRect.intersection(getBounds());
        int unitsInThisTile = newRect.width * newRect.height * bands;

	byte data[] = new byte[byteCount];

	switch(image_type) {
	case TYPE_BILEVEL_WHITE_IS_ZERO:
	case TYPE_BILEVEL_BLACK_IS_ZERO:
	    try {
		if (compression == COMP_PACKBITS) {
		    stream.readFully(data, 0, byteCount);

		    // Since the decompressed data will still be packed
		    // 8 pixels into 1 byte, calculate bytesInThisTile
		    int bytesInThisTile;
		    if ((newRect.width % 8) == 0) {
			bytesInThisTile = (newRect.width/8) * newRect.height;
		    } else {
			bytesInThisTile =
                            (newRect.width/8 + 1) * newRect.height;
		    }
		    decodePackbits(data, bytesInThisTile, bdata);
		} else if (compression == COMP_LZW) {
		    stream.readFully(data, 0, byteCount);
		    lzwDecoder.decode(data, bdata, newRect.height);
		} else if (compression == COMP_FAX_G3_1D) {
		    stream.readFully(data, 0, byteCount);
		    decoder.decode1D(bdata, data, newRect.x, newRect.height);
		} else if (compression == COMP_FAX_G3_2D) {
		    stream.readFully(data, 0, byteCount);
		    decoder.decode2D(bdata, data, newRect.x, newRect.height,
                                     tiffT4Options);
		} else if (compression == COMP_FAX_G4_2D) {
		    stream.readFully(data, 0, byteCount);
                    decoder.decodeT6(bdata, data, newRect.x, newRect.height,
                                     tiffT6Options);
		} else if (compression == COMP_NONE) {
		    stream.readFully(bdata, 0, byteCount);
		}

		stream.seek(save_offset);
	    } catch (IOException ioe) {
		throw new
		    RuntimeException(JaiI18N.getString("TIFFImageDecoder8"));
	    }

	    break;

	case TYPE_GREYSCALE_WHITE_IS_ZERO:
	case TYPE_GREYSCALE_BLACK_IS_ZERO:

	    try {

		if (bitsPerSample[0] == 16) {

		    if (compression == COMP_PACKBITS) {

			stream.readFully(data, 0, byteCount);

			// Since unitsInThisTile is the number of shorts,
			// but we do our decompression in terms of bytes, we
			// need to multiply unitsInThisTile by 2 in order to
			// figure out how many bytes we'll get after
			// decompression.
			int bytesInThisTile = unitsInThisTile * 2;

			byte byteArray[] = new byte[bytesInThisTile];
			decodePackbits(data, bytesInThisTile, byteArray);
			interpretBytesAsShorts(byteArray, sdata,
					       unitsInThisTile);

		    } else if (compression == COMP_LZW) {

			stream.readFully(data, 0, byteCount);

			// Since unitsInThisTile is the number of shorts,
			// but we do our decompression in terms of bytes, we
			// need to multiply unitsInThisTile by 2 in order to
			// figure out how many bytes we'll get after
			// decompression.
			byte byteArray[] = new byte[unitsInThisTile * 2];
			lzwDecoder.decode(data, byteArray, newRect.height);
			interpretBytesAsShorts(byteArray, sdata,
					       unitsInThisTile);

		    } else if (compression == COMP_NONE) {

			readShorts(byteCount/2, sdata);
		    }

		    // Since we are using a ComponentColorModel with this image,
		    // we need to change the WhiteIsZero data to BlackIsZero data
		    // so it will display properly.
		    if (image_type == TYPE_GREYSCALE_WHITE_IS_ZERO) {

			if (dataType == DataBuffer.TYPE_USHORT) {

			    for (int l = 0; l < sdata.length; l++) {
				sdata[l] = (short)(65535 - sdata[l]);
			    }

			} else if (dataType == DataBuffer.TYPE_SHORT) {

			    for (int l = 0; l < sdata.length; l++) {
				sdata[l] = (short)(~sdata[l]);
			    }
			}
		    }

		} else if (bitsPerSample[0] == 8) {

		    if (compression == COMP_PACKBITS) {

			stream.readFully(data, 0, byteCount);
			decodePackbits(data, unitsInThisTile, bdata);

		    } else if (compression == COMP_LZW) {

			stream.readFully(data, 0, byteCount);
			lzwDecoder.decode(data, bdata, newRect.height);

		    } else if (compression == COMP_NONE) {

			stream.readFully(bdata, 0, byteCount);
		    }

		} else if (bitsPerSample[0] == 4) {

		    if (compression == COMP_PACKBITS) {

			stream.readFully(data, 0, byteCount);

			// Since the decompressed data will still be packed
			// 2 pixels into 1 byte, calculate bytesInThisTile
			int bytesInThisTile;
			if ((newRect.width % 8) == 0) {
			    bytesInThisTile = (newRect.width/2) * newRect.height;
			} else {
			    bytesInThisTile = (newRect.width/2 + 1) *
				newRect.height;
			}

			decodePackbits(data, bytesInThisTile, bdata);

		    } else if (compression == COMP_LZW) {

			stream.readFully(data, 0, byteCount);
			lzwDecoder.decode(data, bdata, newRect.height);

		    } else {

			stream.readFully(bdata, 0, byteCount);
		    }
		}

		stream.seek(save_offset);

	    } catch (IOException ioe) {
		throw new
		    RuntimeException(JaiI18N.getString("TIFFImageDecoder8"));
	    }

	    break;

	case TYPE_RGB:

	    try {

		if (bitsPerSample[0] == 8) {

		    if (compression == COMP_NONE) {

			stream.readFully(bdata, 0, byteCount);

		    } else if (compression == COMP_LZW) {

			stream.readFully(data, 0, byteCount);
			lzwDecoder.decode(data, bdata, newRect.height);

		    } else if (compression == COMP_PACKBITS) {

			stream.readFully(data, 0, byteCount);
			decodePackbits(data, unitsInThisTile, bdata);

		    } else {
			throw new RuntimeException(
					JaiI18N.getString("TIFFImageDecoder18"));
		    }

		    // Change to BGR order, as Java2D displays that faster
		    for (int i=0; i<unitsInThisTile; i+=3) {
			bswap = bdata[i];
			bdata[i] = bdata[i+2];
			bdata[i+2] = bswap;
		    }

		} else if (bitsPerSample[0] == 16) {

		    if (compression == COMP_NONE) {

			readShorts(byteCount/2, sdata);

		    } else if (compression == COMP_LZW) {

			stream.readFully(data, 0, byteCount);

			// Since unitsInThisTile is the number of shorts,
			// but we do our decompression in terms of bytes, we
			// need to multiply unitsInThisTile by 2 in order to
			// figure out how many bytes we'll get after
			// decompression.
			byte byteArray[] = new byte[unitsInThisTile * 2];
			lzwDecoder.decode(data, byteArray, newRect.height);
			interpretBytesAsShorts(byteArray, sdata,
					       unitsInThisTile);

		    } else if (compression == COMP_PACKBITS) {

			stream.readFully(data, 0, byteCount);

			// Since unitsInThisTile is the number of shorts,
			// but we do our decompression in terms of bytes, we
			// need to multiply unitsInThisTile by 2 in order to
			// figure out how many bytes we'll get after
			// decompression.
			int bytesInThisTile = unitsInThisTile * 2;

			byte byteArray[] = new byte[bytesInThisTile];
			decodePackbits(data, bytesInThisTile, byteArray);
			interpretBytesAsShorts(byteArray, sdata,
					       unitsInThisTile);
		    } else {
			throw new RuntimeException(
					JaiI18N.getString("TIFFImageDecoder18"));
		    }

		    // Change to BGR order, as Java2D displays that faster
		    for (int i=0; i<unitsInThisTile; i+=3) {
			sswap = sdata[i];
			sdata[i] = sdata[i+2];
			sdata[i+2] = sswap;
		    }
		}

		stream.seek(save_offset);

	    } catch (IOException ioe) {
		throw new
		    RuntimeException(JaiI18N.getString("TIFFImageDecoder8"));
	    }

	    break;

	case TYPE_ORGB:
	case TYPE_ARGB_PRE:
	case TYPE_ARGB:
	    try {

		if (bitsPerSample[0] == 8) {

		    if (compression == COMP_NONE) {

			stream.readFully(bdata, 0, byteCount);

		    } else if (compression == COMP_LZW) {

			stream.readFully(data, 0, byteCount);
			lzwDecoder.decode(data, bdata, newRect.height);

		    } else if (compression == COMP_PACKBITS) {

			stream.readFully(data, 0, byteCount);
			decodePackbits(data, unitsInThisTile, bdata);

		    } else {
			throw new RuntimeException(
				   JaiI18N.getString("TIFFImageDecoder18"));
		    }

		    // Convert from RGBA to ABGR for Java2D
		    for (int i=0; i<unitsInThisTile; i+=4) {
			// Swap R and A
			bswap = bdata[i];
			bdata[i] = bdata[i+3];
			bdata[i+3] = bswap;

			// Swap G and B
			bswap = bdata[i+1];
			bdata[i+1] = bdata[i+2];
			bdata[i+2] = bswap;
		    }

		} else if (bitsPerSample[0] == 16) {

		    if (compression == COMP_NONE) {

			readShorts(byteCount/2, sdata);

		    } else if (compression == COMP_LZW) {

			stream.readFully(data, 0, byteCount);

			// Since unitsInThisTile is the number of shorts,
			// but we do our decompression in terms of bytes, we
			// need to multiply unitsInThisTile by 2 in order to
			// figure out how many bytes we'll get after
			// decompression.
			byte byteArray[] = new byte[unitsInThisTile * 2];
			lzwDecoder.decode(data, byteArray, newRect.height);
			interpretBytesAsShorts(byteArray, sdata,
					       unitsInThisTile);

		    } else if (compression == COMP_PACKBITS) {

			stream.readFully(data, 0, byteCount);

			// Since unitsInThisTile is the number of shorts,
			// but we do our decompression in terms of bytes, we
			// need to multiply unitsInThisTile by 2 in order to
			// figure out how many bytes we'll get after
			// decompression.
			int bytesInThisTile = unitsInThisTile * 2;

			byte byteArray[] = new byte[bytesInThisTile];
			decodePackbits(data, bytesInThisTile, byteArray);
			interpretBytesAsShorts(byteArray, sdata,
					       unitsInThisTile);
		    } else {
			throw new RuntimeException(
					JaiI18N.getString("TIFFImageDecoder18"));
		    }

		    // Change from RGBA to ABGR for Java2D's faster special cases
		    for (int i=0; i<unitsInThisTile; i+=4) {
			// Swap R and A
			sswap = sdata[i];
			sdata[i] = sdata[i+3];
			sdata[i+3] = sswap;

			// Swap G and B
			sswap = sdata[i+1];
			sdata[i+1] = sdata[i+2];
			sdata[i+2] = sswap;
		    }
		}

		stream.seek(save_offset);
	    } catch (IOException ioe) {
		throw new
		    RuntimeException(JaiI18N.getString("TIFFImageDecoder8"));
	    }

	    break;

	case TYPE_RGB_EXTRA:
	    try {

		if (bitsPerSample[0] == 8) {

		    if (compression == COMP_NONE) {

			stream.readFully(bdata, 0, byteCount);

		    } else if (compression == COMP_LZW) {

			stream.readFully(data, 0, byteCount);
			lzwDecoder.decode(data, bdata, newRect.height);

		    } else if (compression == COMP_PACKBITS) {

			stream.readFully(data, 0, byteCount);
			decodePackbits(data, unitsInThisTile, bdata);

		    } else {
			throw new RuntimeException(
				   JaiI18N.getString("TIFFImageDecoder18"));
		    }

		} else if (bitsPerSample[0] == 16) {

		    if (compression == COMP_NONE) {

			readShorts(byteCount/2, sdata);

		    } else if (compression == COMP_LZW) {

			stream.readFully(data, 0, byteCount);

			// Since unitsInThisTile is the number of shorts,
			// but we do our decompression in terms of bytes, we
			// need to multiply unitsInThisTile by 2 in order to
			// figure out how many bytes we'll get after
			// decompression.
			byte byteArray[] = new byte[unitsInThisTile * 2];
			lzwDecoder.decode(data, byteArray, newRect.height);
			interpretBytesAsShorts(byteArray, sdata,
					       unitsInThisTile);

		    } else if (compression == COMP_PACKBITS) {

			stream.readFully(data, 0, byteCount);

			// Since unitsInThisTile is the number of shorts,
			// but we do our decompression in terms of bytes, we
			// need to multiply unitsInThisTile by 2 in order to
			// figure out how many bytes we'll get after
			// decompression.
			int bytesInThisTile = unitsInThisTile * 2;

			byte byteArray[] = new byte[bytesInThisTile];
			decodePackbits(data, bytesInThisTile, byteArray);
			interpretBytesAsShorts(byteArray, sdata,
					       unitsInThisTile);
		    } else {
			throw new RuntimeException(
					JaiI18N.getString("TIFFImageDecoder18"));
		    }
		}

		stream.seek(save_offset);

	    } catch (IOException ioe) {
		throw new
		    RuntimeException(JaiI18N.getString("TIFFImageDecoder8"));
	    }

	    break;

	case TYPE_PALETTE:

	    if (bitsPerSample[0] == 16) {

		if (decodePaletteAsShorts) {

		    short tempData[]= null;

		    // At this point the data is 1 banded and will
		    // become 3 banded only after we've done the palette
		    // lookup, since unitsInThisTile was calculated with
		    // 3 bands, we need to divide this by 3.
		    int unitsBeforeLookup = unitsInThisTile / 3;

		    // Since unitsBeforeLookup is the number of shorts,
		    // but we do our decompression in terms of bytes, we
		    // need to multiply it by 2 in order to figure out
		    // how many bytes we'll get after decompression.
		    int entries = unitsBeforeLookup * 2;

		    // Read the data, if compressed, decode it, reset the pointer
		    try {

			if (compression == COMP_PACKBITS) {

			    stream.readFully(data, 0, byteCount);

			    byte byteArray[] = new byte[entries];
			    decodePackbits(data, entries, byteArray);
			    tempData = new short[unitsBeforeLookup];
			    interpretBytesAsShorts(byteArray, tempData,
						   unitsBeforeLookup);

			}  else if (compression == COMP_LZW) {

			    // Read in all the compressed data for this tile
			    stream.readFully(data, 0, byteCount);

			    byte byteArray[] = new byte[entries];
			    lzwDecoder.decode(data, byteArray, newRect.height);
			    tempData = new short[unitsBeforeLookup];
			    interpretBytesAsShorts(byteArray, tempData,
						   unitsBeforeLookup);

			} else if (compression == COMP_NONE) {

			    // byteCount tells us how many bytes are there
			    // in this tile, but we need to read in shorts,
			    // which will take half the space, so while
			    // allocating we divide byteCount by 2.
			    tempData = new short[byteCount/2];
			    readShorts(byteCount/2, tempData);
			}

			stream.seek(save_offset);

		    } catch (IOException ioe) {
			throw new
			RuntimeException(JaiI18N.getString("TIFFImageDecoder8"));
		    }

		    if (dataType == DataBuffer.TYPE_USHORT) {

			// Expand the palette image into an rgb image with ushort
			// data type.
			int cmapValue;
			int count = 0, lookup, len = colormap.length/3;
			int len2 = len * 2;
			for (int i=0; i<unitsBeforeLookup; i++) {
			    // Get the index into the colormap
			    lookup = tempData[i] & 0xffff;
			    // Get the blue value
			    cmapValue = colormap[lookup+len2];
			    sdata[count++] = (short)(cmapValue & 0xffff);
			    // Get the green value
			    cmapValue = colormap[lookup+len];
			    sdata[count++] = (short)(cmapValue & 0xffff);
			    // Get the red value
			    cmapValue = colormap[lookup];
			    sdata[count++] = (short)(cmapValue & 0xffff);
			}

		    } else if (dataType == DataBuffer.TYPE_SHORT) {

			// Expand the palette image into an rgb image with
			// short data type.
			int cmapValue;
			int count = 0, lookup, len = colormap.length/3;
			int len2 = len * 2;
			for (int i=0; i<unitsBeforeLookup; i++) {
			    // Get the index into the colormap
			    lookup = tempData[i] & 0xffff;
			    // Get the blue value
			    cmapValue = colormap[lookup+len2];
			    sdata[count++] = (short)cmapValue;
			    // Get the green value
			    cmapValue = colormap[lookup+len];
			    sdata[count++] = (short)cmapValue;
			    // Get the red value
			    cmapValue = colormap[lookup];
			    sdata[count++] = (short)cmapValue;
			}
		    }

		} else {

		    // No lookup being done here, when RGB values are needed,
		    // the associated IndexColorModel can be used to get them.

		    try {

			if (compression == COMP_PACKBITS) {

			    stream.readFully(data, 0, byteCount);

			    // Since unitsInThisTile is the number of shorts,
			    // but we do our decompression in terms of bytes, we
			    // need to multiply unitsInThisTile by 2 in order to
			    // figure out how many bytes we'll get after
			    // decompression.
			    int bytesInThisTile = unitsInThisTile * 2;

			    byte byteArray[] = new byte[bytesInThisTile];
			    decodePackbits(data, bytesInThisTile, byteArray);
			    interpretBytesAsShorts(byteArray, sdata,
						   unitsInThisTile);

			} else if (compression == COMP_LZW) {

			    stream.readFully(data, 0, byteCount);

			    // Since unitsInThisTile is the number of shorts,
			    // but we do our decompression in terms of bytes, we
			    // need to multiply unitsInThisTile by 2 in order to
			    // figure out how many bytes we'll get after
			    // decompression.
			    byte byteArray[] = new byte[unitsInThisTile * 2];
			    lzwDecoder.decode(data, byteArray, newRect.height);
			    interpretBytesAsShorts(byteArray, sdata,
						   unitsInThisTile);

			} else if (compression == COMP_NONE) {

			    readShorts(byteCount/2, sdata);
			}

			stream.seek(save_offset);

		    } catch (IOException ioe) {
			throw new
			RuntimeException(JaiI18N.getString("TIFFImageDecoder8"));
		    }
		}

	    } else if (bitsPerSample[0] == 8) {

		if (decodePaletteAsShorts) {

		    byte tempData[]= null;

		    // At this point the data is 1 banded and will
		    // become 3 banded only after we've done the palette
		    // lookup, since unitsInThisTile was calculated with
		    // 3 bands, we need to divide this by 3.
		    int unitsBeforeLookup = unitsInThisTile / 3;

		    // Read the data, if compressed, decode it, reset the pointer
		    try {

			if (compression == COMP_PACKBITS) {

			    stream.readFully(data, 0, byteCount);
			    tempData = new byte[unitsBeforeLookup];
			    decodePackbits(data, unitsBeforeLookup, tempData);

			}  else if (compression == COMP_LZW) {

			    stream.readFully(data, 0, byteCount);
			    tempData = new byte[unitsBeforeLookup];
			    lzwDecoder.decode(data, tempData, newRect.height);

			} else if (compression == COMP_NONE) {

			    tempData = new byte[byteCount];
			    stream.readFully(tempData, 0, byteCount);
			}

			stream.seek(save_offset);

		    } catch (IOException ioe) {
			throw new
			RuntimeException(JaiI18N.getString("TIFFImageDecoder8"));
		    }

		    // Expand the palette image into an rgb image with ushort
		    // data type.
		    int cmapValue;
		    int count = 0, lookup, len = colormap.length/3;
		    int len2 = len * 2;
		    for (int i=0; i<unitsBeforeLookup; i++) {
			// Get the index into the colormap
			lookup = tempData[i] & 0xff;
			// Get the blue value
			cmapValue = colormap[lookup+len2];
			sdata[count++] = (short)(cmapValue & 0xffff);
			// Get the green value
			cmapValue = colormap[lookup+len];
			sdata[count++] = (short)(cmapValue & 0xffff);
			// Get the red value
			cmapValue = colormap[lookup];
			sdata[count++] = (short)(cmapValue & 0xffff);
		    }
		} else {

		    // No lookup being done here, when RGB values are needed,
		    // the associated IndexColorModel can be used to get them.

		    try {

			if (compression == COMP_PACKBITS) {

			    stream.readFully(data, 0, byteCount);
			    decodePackbits(data, unitsInThisTile, bdata);

			} else if (compression == COMP_LZW) {

			    stream.readFully(data, 0, byteCount);
			    lzwDecoder.decode(data, bdata, newRect.height);

			} else if (compression == COMP_NONE) {

			    stream.readFully(bdata, 0, byteCount);
			}

			stream.seek(save_offset);

		    } catch (IOException ioe) {
			throw new
			RuntimeException(JaiI18N.getString("TIFFImageDecoder8"));
		    }
		}

	    } else if (bitsPerSample[0] == 4) {

		int padding = (newRect.width % 2 == 0) ? 0 : 1;
		int bytesPostDecoding = ((newRect.width/2 + padding) *
					 newRect.height);

		// Output short images
		if (decodePaletteAsShorts) {

		    byte tempData[] = null;

		    try {
			stream.readFully(data, 0, byteCount);
			stream.seek(save_offset);
		    } catch (IOException ioe) {
			throw new
 		        RuntimeException(JaiI18N.getString("TIFFImageDecoder8"));
		    }

		    // If compressed, decode the data.
		    if (compression == COMP_PACKBITS) {

			tempData = new byte[bytesPostDecoding];
			decodePackbits(data, bytesPostDecoding, tempData);

		    }  else if (compression == COMP_LZW) {

			tempData = new byte[bytesPostDecoding];
			lzwDecoder.decode(data, tempData, newRect.height);

		    } else if (compression == COMP_NONE) {

			tempData = data;
		    }

		    int bytes = unitsInThisTile / 3;

		    // Unpack the 2 pixels packed into each byte.
		    data = new byte[bytes];

		    int srcCount = 0, dstCount = 0;
		    for (int j=0; j<newRect.height; j++) {
			for (int i=0; i<newRect.width/2; i++) {
			    data[dstCount++] =
				(byte)((tempData[srcCount] & 0xf0) >> 4);
			    data[dstCount++] =
				(byte)(tempData[srcCount++] & 0x0f);
			}

			if (padding == 1) {
			    data[dstCount++] =
				(byte)((tempData[srcCount++] & 0xf0) >> 4);
			}
		    }

		    int len = colormap.length/3;
		    int len2 = len*2;
		    int cmapValue, lookup;
		    int count = 0;
		    for (int i=0; i<bytes; i++) {
			lookup = data[i] & 0xff;
			cmapValue = colormap[lookup+len2];
			sdata[count++] = (short)(cmapValue & 0xffff);
			cmapValue = colormap[lookup+len];
			sdata[count++] = (short)(cmapValue & 0xffff);
			cmapValue = colormap[lookup];
			sdata[count++] = (short)(cmapValue & 0xffff);
		    }
		} else {

		    // Output byte values, use IndexColorModel for unpacking
		    try {

			// If compressed, decode the data.
			if (compression == COMP_PACKBITS) {

			    stream.readFully(data, 0, byteCount);
			    decodePackbits(data, bytesPostDecoding, bdata);

			}  else if (compression == COMP_LZW) {

			    stream.readFully(data, 0, byteCount);
			    lzwDecoder.decode(data, bdata, newRect.height);

			} else if (compression == COMP_NONE) {

			    stream.readFully(bdata, 0, byteCount);
			}

			stream.seek(save_offset);

		    } catch (IOException ioe) {
			throw new
			RuntimeException(JaiI18N.getString("TIFFImageDecoder8"));
		    }
		}
	    } else {
		throw new
		    RuntimeException(JaiI18N.getString("TIFFImageDecoder7"));
	    }
	    break;

	case TYPE_TRANS:
	    break;
	}

        if (tileX == 0 && tileY == 0) {
            tile00 = tile;
        }
        return tile;
    }

    private void readShorts(int shortCount, short shortArray[]) {

	// Since each short consists of 2 bytes, we need a
	// byte array of double size
	int byteCount = 2 * shortCount;
	byte byteArray[] = new byte[byteCount];

	try {
	    stream.readFully(byteArray, 0, byteCount);
	} catch (IOException ioe) {
	   throw new RuntimeException(JaiI18N.getString("TIFFImageDecoder8"));
	}

	interpretBytesAsShorts(byteArray, shortArray, shortCount);
    }

    // Method to interpret a byte array to a short array, depending on
    // whether the bytes are stored in a big endian or little endian format.
    private void interpretBytesAsShorts(byte byteArray[],
					short shortArray[],
					int shortCount) {

	int j;
	int firstByte, secondByte;

	if (isBigEndian) {

	    for (int i=0; i<shortCount; i++) {
		j = 2*i;
		firstByte = byteArray[j] & 0xff;
		secondByte = byteArray[j+1] & 0xff;
		shortArray[i] = (short)((firstByte << 8) + secondByte);
	    }

	} else {

	    for (int i=0; i<shortCount; i++) {
		j = 2*i;
		firstByte = byteArray[j] & 0xff;
		secondByte = byteArray[j+1] & 0xff;
		shortArray[i] = (short)((secondByte << 8) + firstByte);
	    }
	}
    }

    // Uncompress packbits compressed image data.
    private byte[] decodePackbits(byte data[], int arraySize, byte[] dst) {

	if (dst == null) {
	    dst = new byte[arraySize];
	}

	int srcCount = 0, dstCount = 0;
	byte repeat, b;

	try {

	    while (dstCount < arraySize) {

		b = data[srcCount++];

		if (b >= 0 && b <= 127) {

		    // literal run packet
		    for (int i=0; i<(b + 1); i++) {
			dst[dstCount++] = data[srcCount++];
		    }

		} else if (b <= -1 && b >= -127) {

		    // 2 byte encoded run packet
		    repeat = data[srcCount++];
		    for (int i=0; i<(-b + 1); i++) {
			dst[dstCount++] = repeat;
		    }

		} else {
		    // no-op packet. Do nothing
		    srcCount++;
		}
	    }
	} catch (java.lang.ArrayIndexOutOfBoundsException ae) {
	    throw new RuntimeException(JaiI18N.getString("TIFFImageDecoder10"));
	}

	return dst;
    }

    // Create ComponentColorModel for TYPE_RGB images
    private ComponentColorModel createAlphaComponentColorModel(
						   int dataType,
						   boolean hasAlpha,
						   boolean isAlphaPremultiplied,
						   int transparency) {

	ComponentColorModel ccm = null;
	int RGBBits[][] = new int[3][];

	RGBBits[0] = new int[] {8, 8, 8, 8};               // Byte
	RGBBits[1] = new int[] {16, 16, 16, 16};           // Short
	RGBBits[2] = new int[] {16, 16, 16, 16};           // UShort
	RGBBits[2] = new int[] {32, 32, 32, 32};           // Int

	ccm = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
				      RGBBits[dataType], hasAlpha,
				      isAlphaPremultiplied,
				      transparency,
				      dataType);
	return ccm;
    }
}

