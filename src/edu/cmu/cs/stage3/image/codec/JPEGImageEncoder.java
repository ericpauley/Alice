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

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.OutputStream;

import com.sun.image.codec.jpeg.JPEGQTable;


/**
 * An ImageEncoder for the JPEG (JFIF) file format.
 *
 * The common cases of single band grayscale and three or four band RGB images
 * are handled so as to minimize the amount of information required of the
 * programmer. See the comments pertaining to the constructor and the
 * <code>writeToStream()</code> method for more detailed information.
 *
 */
public class JPEGImageEncoder extends ImageEncoderImpl {

    private JPEGEncodeParam jaiEP = null;

    public JPEGImageEncoder(OutputStream output,
                            ImageEncodeParam param) {
        super(output, param);
        if (param != null) {
            jaiEP = (JPEGEncodeParam)param;
        }
    }

    //
    // Go through the settable encoding parameters and see
    // if any of them have been set. If so, transfer then to the
    // com.sun.image.codec.jpeg.JPEGEncodeParam object.
    //
    private void modifyEncodeParam(JPEGEncodeParam jaiEP,
          com.sun.image.codec.jpeg.JPEGEncodeParam j2dEP,
                                               int nbands) {

        int val;
        int[] qTab;
        for(int i=0; i<nbands; i++) {
            //
            // If subsampling factors were set, apply them
            //
            val = jaiEP.getHorizontalSubsampling(i);
            j2dEP.setHorizontalSubsampling(i, val);

            val = jaiEP.getVerticalSubsampling(i);
            j2dEP.setVerticalSubsampling(i, val);

            //
            // If new Q factors were supplied, apply them
            //
            if (jaiEP.isQTableSet(i)) {
                qTab = jaiEP.getQTable(i);
                val = jaiEP.getQTableSlot(i);
                j2dEP.setQTableComponentMapping(i, val);
                j2dEP.setQTable(val, new JPEGQTable(qTab));
            }
        }

        // Apply new quality, if set
        if (jaiEP.isQualitySet()) {
            float fval = jaiEP.getQuality();
            j2dEP.setQuality(fval, true);
        }

        // Apply new restart interval, if set
        val = jaiEP.getRestartInterval();
        j2dEP.setRestartInterval(val);

        // Write a tables-only abbreviated JPEG file
        if (jaiEP.getWriteTablesOnly() == true) {
            j2dEP.setImageInfoValid(false);
            j2dEP.setTableInfoValid(true);
        }

        // Write an image-only abbreviated JPEG file
        if (jaiEP.getWriteImageOnly() == true) {
            j2dEP.setTableInfoValid(false);
            j2dEP.setImageInfoValid(true);
        }

        // Write the JFIF (APP0) marker
        if (jaiEP.getWriteJFIFHeader() == false) {
            j2dEP.setMarkerData(
              com.sun.image.codec.jpeg.JPEGDecodeParam.APP0_MARKER, null);
        }

    }

    /**
     * Encodes a RenderedImage and writes the output to the
     * OutputStream associated with this ImageEncoder.
     */
    
	public void encode(RenderedImage im) throws IOException {
        //
        // Check data type and band count compatibility.
        // This implementation handles only 1 and 3 band source images.
        //
        SampleModel sampleModel = im.getSampleModel();
        ColorModel  colorModel  = im.getColorModel();

        // Must be a 1 or 3 component BYTE image
        int numBands  = colorModel.getNumColorComponents();
        int transType = sampleModel.getTransferType();
        if ((transType != DataBuffer.TYPE_BYTE) ||
            ((numBands != 1) && (numBands != 3) )) {
            throw new RuntimeException(JaiI18N.getString("JPEGImageEncoder0"));
        }

        // Must be GRAY or RGB
        int cspaceType = colorModel.getColorSpace().getType();
        if (cspaceType != ColorSpace.TYPE_GRAY &&
            cspaceType != ColorSpace.TYPE_RGB) {
            throw new Error(JaiI18N.getString("JPEGImageEncoder1"));
        }

        //
        // Create a BufferedImage to be encoded.
        // The JPEG interfaces really need a whole image.
        //
        WritableRaster wRas;
        BufferedImage bi;

        //
        // Get a contiguous raster. Jpeg compression can't work
        // on tiled data in most cases.
        // Also need to be sure that the raster doesn't have a
        // non-zero origin, since BufferedImage won't accept that.
        // (Bug ID 4253990)
        //
        wRas = (WritableRaster)im.getData();
        if (wRas.getMinX() != 0 || wRas.getMinY() != 0) {
            wRas = wRas.createWritableTranslatedChild(0, 0);
        }

        // First create the Java2D encodeParam based on the BufferedImage
        com.sun.image.codec.jpeg.JPEGEncodeParam j2dEP = null;
        if (colorModel instanceof IndexColorModel) {
            //
            // Need to expand the indexed data to components.
            // The convertToIntDiscrete method is used to perform this.
            //
            IndexColorModel icm = (IndexColorModel)colorModel;
            bi = icm.convertToIntDiscrete(wRas, false);
            j2dEP = com.sun.image.codec.jpeg.JPEGCodec.getDefaultJPEGEncodeParam(bi);
        } else {
            bi = new BufferedImage(colorModel, wRas, false, null);
            j2dEP = com.sun.image.codec.jpeg.JPEGCodec.getDefaultJPEGEncodeParam(bi);
        }


        // Now modify the Java2D encodeParam based on the options set
        // in the JAI encodeParam object.
        if (jaiEP != null) {
            modifyEncodeParam(jaiEP, j2dEP, numBands);
        }

        // Now create the encoder with the modified Java2D encodeParam
        com.sun.image.codec.jpeg.JPEGImageEncoder encoder;
        encoder = com.sun.image.codec.jpeg.JPEGCodec.createJPEGEncoder(
                    output, j2dEP);

        try {
          // Write the image data.
            encoder.encode(bi);
        } catch(IOException e) {
            throw new RuntimeException(e.getMessage());
        }

    }

}
