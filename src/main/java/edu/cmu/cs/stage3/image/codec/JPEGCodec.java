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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 */
public final class JPEGCodec extends ImageCodec {

    public JPEGCodec() {}

    
	public String getFormatName() {
        return "jpeg";
    }

    
	public Class getEncodeParamClass() {
        return JPEGEncodeParam.class;
    }

    
	public Class getDecodeParamClass() {
        return Object.class;
    }

    
	public boolean canEncodeImage(RenderedImage im,
                                  ImageEncodeParam param) {
        return true;
    }

    
	protected ImageEncoder createImageEncoder(OutputStream dst,
                                              ImageEncodeParam param) {
        JPEGEncodeParam p = null;
        if (param != null) {
            p = (JPEGEncodeParam)param;
        }

        return new JPEGImageEncoder(dst, p);
    }

    
	protected ImageDecoder createImageDecoder(InputStream src,
                                              ImageDecodeParam param) {
        return new JPEGImageDecoder(src, null);
    }

    
	protected ImageDecoder createImageDecoder(File src,
                                              ImageDecodeParam param)
        throws IOException {
        return new JPEGImageDecoder(new FileInputStream(src), null);
    }

    
	protected ImageDecoder createImageDecoder(SeekableStream src,
                                              ImageDecodeParam param) {
        return new JPEGImageDecoder(src, null);
    }

    
	public int getNumHeaderBytes() {
        return 3;
    }

    
	public boolean isFormatRecognized(byte[] header) {
        return ((header[0] == (byte)0xff) &&
                (header[1] == (byte)0xd8) &&
                (header[2] == (byte)0xff));
    }
}
