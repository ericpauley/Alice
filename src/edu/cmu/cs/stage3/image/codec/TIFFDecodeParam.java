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
 * An instance of <code>ImageDecodeParam</code> for decoding images in
 * the TIFF format.
 *
 * <p> To determine the number of images present in a TIFF file, use
 * the <code>getNumPages()</code> method on the
 * <code>ImageDecoder</code> object that will be used to perform the
 * decoding.  The desired page number may be passed as an argument to
 * the <code>ImageDecoder.decodeAsRaster)()</code> or
 * <code>decodeAsRenderedImage()</code> methods.
 *
 * <p> For TIFF Palette color images, the colorMap always has entries
 * of short data type, the color Black being represented by 0,0,0 and
 * White by 65536,65536,65536. In order to display these images, the
 * default behavior is to dither the short values down to 8 bits.
 * The dithering is done by calling the <code>decode16BitsTo8Bits</code>
 * method for each short value that needs to be dithered. The method has
 * the following implementation:
 * <code>
 *       byte b;
 *       short s;
 *       s = s & 0xffff;
 *       b = (byte)((s >> 8) & 0xff);
 * </code>
 * If a different algorithm is to be used for the dithering, this class
 * should be subclassed and an appropriate implementation should be
 * provided for the <code>decode16BitsTo8Bits</code> method in the subclass.
 *
 * <p>If the palette contains image data that is signed short, as specified
 * by the SampleFormat tag, the dithering is done by calling
 * <code>decodeSigned16BitsTo8Bits</code> instead. The method has the
 * following implementation:
 * <code>
 *       byte b;
 *       short s;
 *       b = (byte)((s + Short.MIN_VALUE) >> 8);
 * </code>
 * In order to use a different algorithm for the dithering, this class
 * should be subclassed and the method overridden.
 *
 * <p> If it is desired that the Palette be decoded such that the output
 * image is of short data type and no dithering is performed, the
 * <code>setDecodePaletteAsShorts</code> method should be used.
 *
 * <p><b> This class is not a committed part of the JAI API.  It may
 * be removed or changed in future releases of JAI.</b>
 *
 * @see TIFFDirectory
 */
public class TIFFDecodeParam implements ImageDecodeParam {

    private boolean decodePaletteAsShorts = false;

    /** Constructs a default instance of <code>TIFFDecodeParam</code>. */
    public TIFFDecodeParam() {
    }

    /**
     * If set, the entries in the palette will be decoded as shorts
     * and no short to byte lookup will be applied to them.
     */
    public void setDecodePaletteAsShorts(boolean decodePaletteAsShorts) {
	this.decodePaletteAsShorts = decodePaletteAsShorts;
    }

    /**
     * Returns <code>true</code> if palette entries will be decoded as
     * shorts, resulting in an output image with short datatype.
     */
    public boolean getDecodePaletteAsShorts() {
	return decodePaletteAsShorts;
    }

    /**
     * Returns an unsigned 8 bit value computed by dithering the unsigned
     * 16 bit value. Note that the TIFF specified short datatype is an
     * unsigned value, while Java's <code>short</code> datatype is a
     * signed value. Therefore the Java <code>short</code> datatype cannot
     * be used to store the TIFF specified short value. A Java
     * <code>int</code> is used as input instead to this method. The method
     * deals correctly only with 16 bit unsigned values.
     */
    public byte decode16BitsTo8Bits(int s) {
	return (byte)((s >> 8) & 0xffff);
    }

    /**
     * Returns an unsigned 8 bit value computed by dithering the signed
     * 16 bit value. This method deals correctly only with values in the
     * 16 bit signed range.
     */
    public byte decodeSigned16BitsTo8Bits(short s) {
	return (byte)((s + Short.MIN_VALUE) >> 8);
    }

}





