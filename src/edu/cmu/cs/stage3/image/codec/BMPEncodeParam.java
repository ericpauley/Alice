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
 * the BMP format.
 *
 * <p> This class allows for the specification of various parameters
 * while encoding (writing) a BMP format image file.  By default, the
 * version used is VERSION_3, no compression is used, and the data layout
 * is bottom_up, such that the pixels are stored in bottom-up order, the
 * first scanline being stored last.
 *
 * <p><b> This class is not a committed part of the JAI API.  It may
 * be removed or changed in future releases of JAI.</b>
 *
 */
public class BMPEncodeParam implements ImageEncodeParam {

    // version constants

    /** Constant for BMP version 2. */
    public static final int VERSION_2 = 0;

    /** Constant for BMP version 3. */
    public static final int VERSION_3 = 1;

    /** Constant for BMP version 4. */
    public static final int VERSION_4 = 2;

    // Default values
    private int version = VERSION_3;
    private boolean compressed = false;
    private boolean topDown = false;

    /**
     * Constructs an BMPEncodeParam object with default values for parameters.
     */
    public BMPEncodeParam() {}

    /** Sets the BMP version to be used. */
    public void setVersion(int versionNumber) {
	checkVersion(versionNumber);
	this.version = versionNumber;
    }

    /** Returns the BMP version to be used. */
    public int getVersion() {
	return version;
    }

    /** If set, the data will be written out compressed, if possible. */
    public void setCompressed(boolean compressed) {
	this.compressed = compressed;
    }

    /**
     * Returns the value of the parameter <code>compressed</code>.
     */
    public boolean isCompressed() {
	return compressed;
    }

    /**
     * If set, the data will be written out in a top-down manner, the first
     * scanline being written first.
     */
    public void setTopDown(boolean topDown) {
	this.topDown = topDown;
    }

    /**
     * Returns the value of the <code>topDown</code> parameter.
     */
    public boolean isTopDown() {
	return topDown;
    }

    // Method to check whether we can handle the given version.
    private void checkVersion(int versionNumber) {
	if ( !(versionNumber == VERSION_2 ||
	       versionNumber == VERSION_3 ||
	       versionNumber == VERSION_4) ) {
	    throw new RuntimeException(JaiI18N.getString("BMPEncodeParam0"));
	}
    }

}
