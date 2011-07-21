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

import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 */
public final class PNGCodec extends ImageCodec {

    public PNGCodec() {}

    
	public String getFormatName() {
        return "png";
    }

    
	public Class getEncodeParamClass() {
        return PNGEncodeParam.class;
    }

    
	public Class getDecodeParamClass() {
        return PNGDecodeParam.class;
    }

    
	public boolean canEncodeImage(RenderedImage im,
                                  ImageEncodeParam param) {
        SampleModel sampleModel = im.getSampleModel();

        // PNG doesn't handle float or double pixels
        int dataType = sampleModel.getTransferType();
        if ((dataType == DataBuffer.TYPE_FLOAT) ||
            (dataType == DataBuffer.TYPE_DOUBLE)) {
            return false;
        }

        int[] sampleSize = sampleModel.getSampleSize();
        int bitDepth = sampleSize[0];

        // Ensure all channels have the same bit depth
        for (int i = 1; i < sampleSize.length; i++) {
            if (sampleSize[i] != bitDepth) {
                return false;
            }
        }

        // Bit depth must be between 1 and 16
        if (bitDepth < 1 || bitDepth > 16) {
            return false;
        }

        // Number of bands must be between 1 and 4
        int numBands = sampleModel.getNumBands();
        if (numBands < 1 || numBands > 4) {
            return false;
        }

        // Palette images must be 1-banded, depth 8 or less
        ColorModel colorModel = im.getColorModel();
        if (colorModel instanceof IndexColorModel) {
            if (numBands != 1 || bitDepth > 8) {
                return false;
            }
        }

        // If a param object is supplied, check that it is of the right type
        if (param != null) {
            if (param instanceof PNGEncodeParam) {
                if (colorModel instanceof IndexColorModel) {
                    if (!(param instanceof PNGEncodeParam.Palette) ) {
                        return false;
                    }
                } else if (numBands < 3) {
                    if (!(param instanceof PNGEncodeParam.Gray) ) {
                        return false;
                    }
                } else {
                    if (!(param instanceof PNGEncodeParam.RGB) ) {
                        return false;
                    }
                }
            } else {
                return false;
            }
        }

        return true;
    }

    
	protected ImageEncoder createImageEncoder(OutputStream dst,
                                              ImageEncodeParam param) {
        PNGEncodeParam p = null;
        if (param != null) {
            p = (PNGEncodeParam)param;
        }
        return new PNGImageEncoder(dst, p);
    }

    
	protected ImageDecoder createImageDecoder(InputStream src,
                                              ImageDecodeParam param) {
        PNGDecodeParam p = null;
        if (param != null) {
            p = (PNGDecodeParam)param;
        }
        return new PNGImageDecoder(src, p);
    }

    
	protected ImageDecoder createImageDecoder(File src,
                                              ImageDecodeParam param)
        throws IOException {
        PNGDecodeParam p = null;
        if (param != null) {
            p = (PNGDecodeParam)param;
        }
        return new PNGImageDecoder(new FileInputStream(src), p);
    }

    
	protected ImageDecoder createImageDecoder(SeekableStream src,
                                              ImageDecodeParam param) {
        PNGDecodeParam p = null;
        if (param != null) {
            p = (PNGDecodeParam)param;
        }
        return new PNGImageDecoder(src, p);
    }

    
	public int getNumHeaderBytes() {
        return 8;
    }

    
	public boolean isFormatRecognized(byte[] header) {
        return ((header[0] == (byte)0x89) &&
                (header[1] == (byte)0x50) &&
                (header[2] == (byte)0x4e) &&
                (header[3] == (byte)0x47) &&
                (header[4] == (byte)0x0d) &&
                (header[5] == (byte)0x0a) &&
                (header[6] == (byte)0x1a) &&
                (header[7] == (byte)0x0a));
    }
}
