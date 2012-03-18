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

/**
 * An instance of <code>ImageEncodeParam</code> for encoding images in
 * the TIFF format.
 *
 * <p> This class allows for the specification of encoding parameters. By
 * default, the image is encoded without any compression, and is written
 * out consisting of strips, not tiles. The particular compression scheme
 * to be used can be specified by using the <code>setCompression</code>
 * method. The compression scheme specified will be honored only if it is
 * compatible with the type of image being written out. For example,
 * Group3 and Group4 compressions can only be used with Bilevel images.
 * Writing of tiled TIFF images can be enabled by calling the
 * <code>setWriteTiled</code> method.
 *
 * <p><b> This class is not a committed part of the JAI API.  It may
 * be removed or changed in future releases of JAI.</b>
 *
 */
public class TIFFEncodeParam implements ImageEncodeParam {

    public static final int COMPRESSION_NONE          = 1;
    public static final int COMPRESSION_PACKBITS      = 2;
    public static final int COMPRESSION_GROUP3_1D     = 3;
    public static final int COMPRESSION_GROUP3_2D     = 4;
    public static final int COMPRESSION_GROUP4        = 5;
    public static final int COMPRESSION_LZW           = 6;

    private int compression = COMPRESSION_NONE;
    private boolean writeTiled = false;

    /**
     * Constructs an TIFFEncodeParam object with default values for parameters.
     */
    public TIFFEncodeParam() {}

    /**
     * Returns the value of the compression parameter.
     */
    public int getCompression() {
	return compression;
    }

    /**
     * Specifies the type of compression to be used. The compression type
     * specified will be honored only if it is compatible with the image
     * being written out.
     *
     * @param compression    The compression type.
     */
    public void setCompression(int compression) {

	// Writing out compressed TIFF images is not implemented yet, 04/02/99.
	if (compression != COMPRESSION_NONE) {
	    throw new Error(JaiI18N.getString("TIFFEncodeParam0"));
	}

	if (compression != COMPRESSION_NONE &&
	    compression != COMPRESSION_PACKBITS &&
	    compression != COMPRESSION_GROUP3_1D &&
	    compression != COMPRESSION_GROUP3_2D &&
	    compression != COMPRESSION_GROUP4 &&
	    compression != COMPRESSION_LZW) {

	    throw new Error(JaiI18N.getString("TIFFEncodeParam1"));
	}

	this.compression = compression;
    }

    /**
     * Returns the value of the writeTiled parameter.
     */
    public boolean getWriteTiled() {
	return writeTiled;
    }

    /**
     * If set, the data will be written out in tiled format, instead of
     * in strips.
     *
     * @param writeTiled     Specifies whether the image data should be
     *                       wriiten out in tiled format.
     */
    public void setWriteTiled(boolean writeTiled) {

	// Currently only writing out in strips is implemented
	if (writeTiled == true) {
	    throw new Error(JaiI18N.getString("TIFFEncodeParam2"));
	}

	this.writeTiled = writeTiled;
    }

}
