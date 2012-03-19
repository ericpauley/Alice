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

import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.SampleModel;

/**
 * This class represents image data which is stored such that each sample of a
 * pixel occupies one data element of the DataBuffer. It stores the N samples
 * which make up a pixel in N separate data array elements. Different bands may
 * be in different banks of the DataBuffer. Accessor methods are provided so
 * that image data can be manipulated directly. This class can support different
 * kinds of interleaving, e.g. band interleaving, scanline interleaving, and
 * pixel interleaving. Pixel stride is the number of data array elements between
 * two samples for the same band on the same scanline. Scanline stride is the
 * number of data array elements between a given sample and the corresponding
 * sample in the same column of the next scanline. Band offsets denote the
 * number of data array elements from the first data array element of the bank
 * of the DataBuffer holding each band to the first sample of the band. The
 * bands are numbered from 0 to N-1. This class can represent image data for the
 * dataTypes enumerated in java.awt.image.DataBuffer (all samples of a given
 * ComponentSampleModel are stored with the same precision) . All strides and
 * offsets must be non-negative.
 * 
 * @see java.awt.image.ComponentSampleModel
 */

public class ComponentSampleModelJAI extends ComponentSampleModel {

	/**
	 * Constructs a ComponentSampleModel with the specified parameters. The
	 * number of bands will be given by the length of the bandOffsets array. All
	 * bands will be stored in the first bank of the DataBuffer.
	 * 
	 * @param dataType
	 *            The data type for storing samples.
	 * @param w
	 *            The width (in pixels) of the region of image data described.
	 * @param h
	 *            The height (in pixels) of the region of image data described.
	 * @param pixelStride
	 *            The pixel stride of the region of image data described.
	 * @param scanlineStride
	 *            The line stride of the region of image data described.
	 * @param bandOffsets
	 *            The offsets of all bands.
	 */
	public ComponentSampleModelJAI(int dataType, int w, int h, int pixelStride, int scanlineStride, int bandOffsets[]) {
		super(dataType, w, h, pixelStride, scanlineStride, bandOffsets);
	}

	/**
	 * Constructs a ComponentSampleModel with the specified parameters. The
	 * number of bands will be given by the length of the bandOffsets array.
	 * Different bands may be stored in different banks of the DataBuffer.
	 * 
	 * @param dataType
	 *            The data type for storing samples.
	 * @param w
	 *            The width (in pixels) of the region of image data described.
	 * @param h
	 *            The height (in pixels) of the region of image data described.
	 * @param pixelStride
	 *            The pixel stride of the region of image data described.
	 * @param scanlineStride
	 *            The line stride of the region of image data described.
	 * @param bankIndices
	 *            The bank indices of all bands.
	 * @param bandOffsets
	 *            The band offsets of all bands.
	 */
	public ComponentSampleModelJAI(int dataType, int w, int h, int pixelStride, int scanlineStride, int bankIndices[], int bandOffsets[]) {
		super(dataType, w, h, pixelStride, scanlineStride, bankIndices, bandOffsets);
	}

	/**
	 * Returns the size of the data buffer (in data elements) needed for a data
	 * buffer that matches this ComponentSampleModel.
	 */
	private long getBufferSize() {
		int maxBandOff = bandOffsets[0];
		for (int i = 1; i < bandOffsets.length; i++) {
			maxBandOff = Math.max(maxBandOff, bandOffsets[i]);
		}

		long size = 0;
		if (maxBandOff >= 0) {
			size += maxBandOff + 1;
		}
		if (pixelStride > 0) {
			size += pixelStride * (width - 1);
		}
		if (scanlineStride > 0) {
			size += scanlineStride * (height - 1);
		}
		return size;
	}

	/**
	 * Preserves band ordering with new step factor...
	 */
	private int[] JAIorderBands(int orig[], int step) {
		int map[] = new int[orig.length];
		int ret[] = new int[orig.length];

		for (int i = 0; i < map.length; i++) {
			map[i] = i;
		}

		for (int i = 0; i < ret.length; i++) {
			int index = i;
			for (int j = i + 1; j < ret.length; j++) {
				if (orig[map[index]] > orig[map[j]]) {
					index = j;
				}
			}
			ret[map[index]] = i * step;
			map[index] = map[i];
		}
		return ret;
	}

	/**
	 * Creates a new ComponentSampleModel with the specified width and height.
	 * The new SampleModel will have the same number of bands, storage data
	 * type, interleaving scheme, and pixel stride as this SampleModel.
	 * 
	 * @param w
	 *            The width in pixels.
	 * @param h
	 *            The height in pixels
	 */

	@Override
	public SampleModel createCompatibleSampleModel(int w, int h) {
		SampleModel ret = null;
		long size;
		int minBandOff = bandOffsets[0];
		int maxBandOff = bandOffsets[0];
		for (int i = 1; i < bandOffsets.length; i++) {
			minBandOff = Math.min(minBandOff, bandOffsets[i]);
			maxBandOff = Math.max(maxBandOff, bandOffsets[i]);
		}
		maxBandOff -= minBandOff;

		int bands = bandOffsets.length;
		int bandOff[];
		int pStride = Math.abs(pixelStride);
		int lStride = Math.abs(scanlineStride);
		int bStride = Math.abs(maxBandOff);

		if (pStride > lStride) {
			if (pStride > bStride) {
				if (lStride > bStride) { // pix > line > band
					bandOff = new int[bandOffsets.length];
					for (int i = 0; i < bands; i++) {
						bandOff[i] = bandOffsets[i] - minBandOff;
					}
					lStride = bStride + 1;
					pStride = lStride * h;
				} else { // pix > band > line
					bandOff = JAIorderBands(bandOffsets, lStride * h);
					pStride = bands * lStride * h;
				}
			} else { // band > pix > line
				pStride = lStride * h;
				bandOff = JAIorderBands(bandOffsets, pStride * w);
			}
		} else {
			if (pStride > bStride) { // line > pix > band
				bandOff = new int[bandOffsets.length];
				for (int i = 0; i < bands; i++) {
					bandOff[i] = bandOffsets[i] - minBandOff;
				}
				pStride = bStride + 1;
				lStride = pStride * w;
			} else {
				if (lStride > bStride) { // line > band > pix
					bandOff = JAIorderBands(bandOffsets, pStride * w);
					lStride = bands * pStride * w;
				} else { // band > line > pix
					lStride = pStride * w;
					bandOff = JAIorderBands(bandOffsets, lStride * h);
				}
			}
		}

		// make sure we make room for negative offsets...
		int base = 0;
		if (scanlineStride < 0) {
			base += lStride * h;
			lStride *= -1;
		}
		if (pixelStride < 0) {
			base += pStride * w;
			pStride *= -1;
		}

		for (int i = 0; i < bands; i++) {
			bandOff[i] += base;
		}
		return new ComponentSampleModelJAI(dataType, w, h, pStride, lStride, bankIndices, bandOff);
	}

	/**
	 * This creates a new ComponentSampleModel with a subset of the bands of
	 * this ComponentSampleModel. The new ComponentSampleModel can be used with
	 * any DataBuffer that the existing ComponentSampleModel can be used with.
	 * The new ComponentSampleModel/DataBuffer combination will represent an
	 * image with a subset of the bands of the original
	 * ComponentSampleModel/DataBuffer combination.
	 * 
	 * @param bands
	 *            subset of bands of this ComponentSampleModel
	 */

	@Override
	public SampleModel createSubsetSampleModel(int bands[]) {
		int newBankIndices[] = new int[bands.length];
		int newBandOffsets[] = new int[bands.length];
		for (int i = 0; i < bands.length; i++) {
			int b = bands[i];
			newBankIndices[i] = bankIndices[b];
			newBandOffsets[i] = bandOffsets[b];
		}
		return new ComponentSampleModelJAI(dataType, width, height, pixelStride, scanlineStride, newBankIndices, newBandOffsets);
	}

	/**
	 * Creates a DataBuffer that corresponds to this ComponentSampleModel. The
	 * DataBuffer's data type, number of banks, and size will be consistent with
	 * this ComponentSampleModel.
	 */

	@Override
	public DataBuffer createDataBuffer() {
		DataBuffer dataBuffer = null;

		int size = (int) getBufferSize();
		switch (dataType) {
			case DataBuffer.TYPE_BYTE :
				dataBuffer = new DataBufferByte(size, numBanks);
				break;
			case DataBuffer.TYPE_USHORT :
				dataBuffer = new DataBufferUShort(size, numBanks);
				break;
			case DataBuffer.TYPE_INT :
				dataBuffer = new DataBufferInt(size, numBanks);
				break;
			case DataBuffer.TYPE_SHORT :
				dataBuffer = new DataBufferShort(size, numBanks);
				break;
			case DataBuffer.TYPE_FLOAT :
				dataBuffer = new DataBufferFloat(size, numBanks);
				break;
			case DataBuffer.TYPE_DOUBLE :
				dataBuffer = new DataBufferDouble(size, numBanks);
				break;
		}

		return dataBuffer;
	}

	/**
	 * Returns data for a single pixel in a primitive array of type
	 * TransferType. For a ComponentSampleModel, this will be the same as the
	 * data type, and samples will be returned one per array element. Generally,
	 * obj should be passed in as null, so that the Object will be created
	 * automatically and will be of the right primitive data type.
	 * <p>
	 * The following code illustrates transferring data for one pixel from
	 * DataBuffer <code>db1</code>, whose storage layout is described by
	 * ComponentSampleModel <code>csm1</code>, to DataBuffer <code>db2</code>,
	 * whose storage layout is described by ComponentSampleModel
	 * <code>csm2</code>. The transfer will generally be more efficient than
	 * using getPixel/setPixel.
	 * 
	 * <pre>
	 * ComponentSampleModel csm1, csm2;
	 * DataBufferInt db1, db2;
	 * csm2.setDataElements(x, y, csm1.getDataElements(x, y, null, db1), db2);
	 * </pre>
	 * 
	 * Using getDataElements/setDataElements to transfer between two
	 * DataBuffer/SampleModel pairs is legitimate if the SampleModels have the
	 * same number of bands, corresponding bands have the same number of bits
	 * per sample, and the TransferTypes are the same.
	 * <p>
	 * If obj is non-null, it should be a primitive array of type TransferType.
	 * Otherwise, a ClassCastException is thrown. An
	 * ArrayIndexOutOfBoundsException may be thrown if the coordinates are not
	 * in bounds, or if obj is non-null and is not large enough to hold the
	 * pixel data.
	 * 
	 * @param x
	 *            The X coordinate of the pixel location.
	 * @param y
	 *            The Y coordinate of the pixel location.
	 * @param obj
	 *            If non-null, a primitive array in which to return the pixel
	 *            data.
	 * @param data
	 *            The DataBuffer containing the image data.
	 */

	@Override
	public Object getDataElements(int x, int y, Object obj, DataBuffer data) {

		int type = getTransferType();
		int numDataElems = getNumDataElements();
		int pixelOffset = y * scanlineStride + x * pixelStride;

		switch (type) {

			case DataBuffer.TYPE_BYTE :

				byte[] bdata;

				if (obj == null) {
					bdata = new byte[numDataElems];
				} else {
					bdata = (byte[]) obj;
				}

				for (int i = 0; i < numDataElems; i++) {
					bdata[i] = (byte) data.getElem(bankIndices[i], pixelOffset + bandOffsets[i]);
				}

				obj = bdata;
				break;

			case DataBuffer.TYPE_USHORT :

				short[] usdata;

				if (obj == null) {
					usdata = new short[numDataElems];
				} else {
					usdata = (short[]) obj;
				}

				for (int i = 0; i < numDataElems; i++) {
					usdata[i] = (short) data.getElem(bankIndices[i], pixelOffset + bandOffsets[i]);
				}

				obj = usdata;
				break;

			case DataBuffer.TYPE_INT :

				int[] idata;

				if (obj == null) {
					idata = new int[numDataElems];
				} else {
					idata = (int[]) obj;
				}

				for (int i = 0; i < numDataElems; i++) {
					idata[i] = data.getElem(bankIndices[i], pixelOffset + bandOffsets[i]);
				}

				obj = idata;
				break;

			case DataBuffer.TYPE_SHORT :

				short[] sdata;

				if (obj == null) {
					sdata = new short[numDataElems];
				} else {
					sdata = (short[]) obj;
				}

				for (int i = 0; i < numDataElems; i++) {
					sdata[i] = (short) data.getElem(bankIndices[i], pixelOffset + bandOffsets[i]);
				}

				obj = sdata;
				break;

			case DataBuffer.TYPE_FLOAT :

				float[] fdata;

				if (obj == null) {
					fdata = new float[numDataElems];
				} else {
					fdata = (float[]) obj;
				}

				for (int i = 0; i < numDataElems; i++) {
					fdata[i] = data.getElemFloat(bankIndices[i], pixelOffset + bandOffsets[i]);
				}

				obj = fdata;
				break;

			case DataBuffer.TYPE_DOUBLE :

				double[] ddata;

				if (obj == null) {
					ddata = new double[numDataElems];
				} else {
					ddata = (double[]) obj;
				}

				for (int i = 0; i < numDataElems; i++) {
					ddata[i] = data.getElemDouble(bankIndices[i], pixelOffset + bandOffsets[i]);
				}

				obj = ddata;
				break;

		}

		return obj;
	}

	/**
	 * Returns the pixel data for the specified rectangle of pixels in a
	 * primitive array of type TransferType. For image data supported by the
	 * Java 2D API, this will be one of the dataTypes supported by
	 * java.awt.image.DataBuffer. Data may be returned in a packed format, thus
	 * increasing efficiency for data transfers. Generally, obj should be passed
	 * in as null, so that the Object will be created automatically and will be
	 * of the right primitive data type.
	 * <p>
	 * The following code illustrates transferring data for a rectangular region
	 * of pixels from DataBuffer <code>db1</code>, whose storage layout is
	 * described by SampleModel <code>sm1</code>, to DataBuffer <code>db2</code>
	 * , whose storage layout is described by SampleModel <code>sm2</code>. The
	 * transfer will generally be more efficient than using getPixels/setPixels.
	 * 
	 * <pre>
	 * SampleModel sm1, sm2;
	 * DataBuffer db1, db2;
	 * sm2.setDataElements(x, y, w, h, sm1.getDataElements(x, y, w, h, null, db1), db2);
	 * </pre>
	 * 
	 * Using getDataElements/setDataElements to transfer between two
	 * DataBuffer/SampleModel pairs is legitimate if the SampleModels have the
	 * same number of bands, corresponding bands have the same number of bits
	 * per sample, and the TransferTypes are the same.
	 * <p>
	 * If obj is non-null, it should be a primitive array of type TransferType.
	 * Otherwise, a ClassCastException is thrown. An
	 * ArrayIndexOutOfBoundsException may be thrown if the coordinates are not
	 * in bounds, or if obj is non-null and is not large enough to hold the
	 * pixel data.
	 * 
	 * @param x
	 *            The minimum X coordinate of the pixel rectangle.
	 * @param y
	 *            The minimum Y coordinate of the pixel rectangle.
	 * @param w
	 *            The width of the pixel rectangle.
	 * @param h
	 *            The height of the pixel rectangle.
	 * @param obj
	 *            If non-null, a primitive array in which to return the pixel
	 *            data.
	 * @param data
	 *            The DataBuffer containing the image data.
	 * @see #getNumDataElements
	 * @see #getTransferType
	 * @see java.awt.image.DataBuffer
	 */

	@Override
	public Object getDataElements(int x, int y, int w, int h, Object obj, DataBuffer data) {

		int type = getTransferType();
		int numDataElems = getNumDataElements();
		int cnt = 0;
		Object o = null;

		switch (type) {
			case DataBuffer.TYPE_BYTE : {
				byte[] btemp;
				byte[] bdata;

				if (obj == null) {
					bdata = new byte[numDataElems * w * h];
				} else {
					bdata = (byte[]) obj;
				}

				for (int i = y; i < y + h; i++) {
					for (int j = x; j < x + w; j++) {
						o = getDataElements(j, i, o, data);
						btemp = (byte[]) o;
						for (int k = 0; k < numDataElems; k++) {
							bdata[cnt++] = btemp[k];
						}
					}
				}
				obj = bdata;
				break;
			}
			case DataBuffer.TYPE_USHORT : {

				short[] usdata;
				short[] ustemp;

				if (obj == null) {
					usdata = new short[numDataElems * w * h];
				} else {
					usdata = (short[]) obj;
				}

				for (int i = y; i < y + h; i++) {
					for (int j = x; j < x + w; j++) {
						o = getDataElements(j, i, o, data);
						ustemp = (short[]) o;
						for (int k = 0; k < numDataElems; k++) {
							usdata[cnt++] = ustemp[k];
						}
					}
				}

				obj = usdata;
				break;
			}
			case DataBuffer.TYPE_INT : {

				int[] idata;
				int[] itemp;

				if (obj == null) {
					idata = new int[numDataElems * w * h];
				} else {
					idata = (int[]) obj;
				}

				for (int i = y; i < y + h; i++) {
					for (int j = x; j < x + w; j++) {
						o = getDataElements(j, i, o, data);
						itemp = (int[]) o;
						for (int k = 0; k < numDataElems; k++) {
							idata[cnt++] = itemp[k];
						}
					}
				}

				obj = idata;
				break;
			}
			case DataBuffer.TYPE_SHORT : {

				short[] sdata;
				short[] stemp;

				if (obj == null) {
					sdata = new short[numDataElems * w * h];
				} else {
					sdata = (short[]) obj;
				}

				for (int i = y; i < y + h; i++) {
					for (int j = x; j < x + w; j++) {
						o = getDataElements(j, i, o, data);
						stemp = (short[]) o;
						for (int k = 0; k < numDataElems; k++) {
							sdata[cnt++] = stemp[k];
						}
					}
				}

				obj = sdata;
				break;
			}
			case DataBuffer.TYPE_FLOAT : {

				float[] fdata;
				float[] ftemp;

				if (obj == null) {
					fdata = new float[numDataElems * w * h];
				} else {
					fdata = (float[]) obj;
				}

				for (int i = y; i < y + h; i++) {
					for (int j = x; j < x + w; j++) {
						o = getDataElements(j, i, o, data);
						ftemp = (float[]) o;
						for (int k = 0; k < numDataElems; k++) {
							fdata[cnt++] = ftemp[k];
						}
					}
				}

				obj = fdata;
				break;
			}
			case DataBuffer.TYPE_DOUBLE : {

				double[] ddata;
				double[] dtemp;

				if (obj == null) {
					ddata = new double[numDataElems * w * h];
				} else {
					ddata = (double[]) obj;
				}

				for (int i = y; i < y + h; i++) {
					for (int j = x; j < x + w; j++) {
						o = getDataElements(j, i, o, data);
						dtemp = (double[]) o;
						for (int k = 0; k < numDataElems; k++) {
							ddata[cnt++] = dtemp[k];
						}
					}
				}

				obj = ddata;
				break;
			}
		}

		return obj;
	}

	/**
	 * Sets the data for a single pixel in the specified DataBuffer from a
	 * primitive array of type TransferType. For a ComponentSampleModel, this
	 * will be the same as the data type, and samples are transferred one per
	 * array element.
	 * <p>
	 * The following code illustrates transferring data for one pixel from
	 * DataBuffer <code>db1</code>, whose storage layout is described by
	 * ComponentSampleModel <code>csm1</code>, to DataBuffer <code>db2</code>,
	 * whose storage layout is described by ComponentSampleModel
	 * <code>csm2</code>. The transfer will generally be more efficient than
	 * using getPixel/setPixel.
	 * 
	 * <pre>
	 * ComponentSampleModel csm1, csm2;
	 * DataBufferInt db1, db2;
	 * csm2.setDataElements(x, y, csm1.getDataElements(x, y, null, db1), db2);
	 * </pre>
	 * 
	 * Using getDataElements/setDataElements to transfer between two
	 * DataBuffer/SampleModel pairs is legitimate if the SampleModels have the
	 * same number of bands, corresponding bands have the same number of bits
	 * per sample, and the TransferTypes are the same.
	 * <p>
	 * obj must be a primitive array of type TransferType. Otherwise, a
	 * ClassCastException is thrown. An ArrayIndexOutOfBoundsException may be
	 * thrown if the coordinates are not in bounds, or if obj is not large
	 * enough to hold the pixel data.
	 * 
	 * @param x
	 *            The X coordinate of the pixel location.
	 * @param y
	 *            The Y coordinate of the pixel location.
	 * @param obj
	 *            A primitive array containing pixel data.
	 * @param data
	 *            The DataBuffer containing the image data.
	 */

	@Override
	public void setDataElements(int x, int y, Object obj, DataBuffer data) {

		int type = getTransferType();
		int numDataElems = getNumDataElements();
		int pixelOffset = y * scanlineStride + x * pixelStride;

		switch (type) {

			case DataBuffer.TYPE_BYTE :

				byte[] barray = (byte[]) obj;

				for (int i = 0; i < numDataElems; i++) {
					data.setElem(bankIndices[i], pixelOffset + bandOffsets[i], barray[i] & 0xff);
				}
				break;

			case DataBuffer.TYPE_USHORT :

				short[] usarray = (short[]) obj;

				for (int i = 0; i < numDataElems; i++) {
					data.setElem(bankIndices[i], pixelOffset + bandOffsets[i], usarray[i] & 0xffff);
				}
				break;

			case DataBuffer.TYPE_INT :

				int[] iarray = (int[]) obj;

				for (int i = 0; i < numDataElems; i++) {
					data.setElem(bankIndices[i], pixelOffset + bandOffsets[i], iarray[i]);
				}
				break;

			case DataBuffer.TYPE_SHORT :

				short[] sarray = (short[]) obj;

				for (int i = 0; i < numDataElems; i++) {
					data.setElem(bankIndices[i], pixelOffset + bandOffsets[i], sarray[i]);
				}
				break;

			case DataBuffer.TYPE_FLOAT :

				float[] farray = (float[]) obj;

				for (int i = 0; i < numDataElems; i++) {
					data.setElemFloat(bankIndices[i], pixelOffset + bandOffsets[i], farray[i]);
				}
				break;

			case DataBuffer.TYPE_DOUBLE :

				double[] darray = (double[]) obj;

				for (int i = 0; i < numDataElems; i++) {
					data.setElemDouble(bankIndices[i], pixelOffset + bandOffsets[i], darray[i]);
				}
				break;

		}
	}

	/**
	 * Sets the data for a rectangle of pixels in the specified DataBuffer from
	 * a primitive array of type TransferType. For image data supported by the
	 * Java 2D API, this will be one of the dataTypes supported by
	 * java.awt.image.DataBuffer. Data in the array may be in a packed format,
	 * thus increasing efficiency for data transfers.
	 * <p>
	 * The following code illustrates transferring data for a rectangular region
	 * of pixels from DataBuffer <code>db1</code>, whose storage layout is
	 * described by SampleModel <code>sm1</code>, to DataBuffer <code>db2</code>
	 * , whose storage layout is described by SampleModel <code>sm2</code>. The
	 * transfer will generally be more efficient than using getPixels/setPixels.
	 * 
	 * <pre>
	 * SampleModel sm1, sm2;
	 * DataBuffer db1, db2;
	 * sm2.setDataElements(x, y, w, h, sm1.getDataElements(x, y, w, h, null, db1), db2);
	 * </pre>
	 * 
	 * Using getDataElements/setDataElements to transfer between two
	 * DataBuffer/SampleModel pairs is legitimate if the SampleModels have the
	 * same number of bands, corresponding bands have the same number of bits
	 * per sample, and the TransferTypes are the same.
	 * <p>
	 * obj must be a primitive array of type TransferType. Otherwise, a
	 * ClassCastException is thrown. An ArrayIndexOutOfBoundsException may be
	 * thrown if the coordinates are not in bounds, or if obj is not large
	 * enough to hold the pixel data.
	 * 
	 * @param x
	 *            The minimum X coordinate of the pixel rectangle.
	 * @param y
	 *            The minimum Y coordinate of the pixel rectangle.
	 * @param w
	 *            The width of the pixel rectangle.
	 * @param h
	 *            The height of the pixel rectangle.
	 * @param obj
	 *            A primitive array containing pixel data.
	 * @param data
	 *            The DataBuffer containing the image data.
	 * @see #getNumDataElements
	 * @see #getTransferType
	 * @see java.awt.image.DataBuffer
	 */

	@Override
	public void setDataElements(int x, int y, int w, int h, Object obj, DataBuffer data) {
		int cnt = 0;
		Object o = null;
		int type = getTransferType();
		int numDataElems = getNumDataElements();

		switch (type) {

			case DataBuffer.TYPE_BYTE : {

				byte[] barray = (byte[]) obj;
				byte[] btemp = new byte[numDataElems];

				for (int i = y; i < y + h; i++) {
					for (int j = x; j < x + w; j++) {
						for (int k = 0; k < numDataElems; k++) {
							btemp[k] = barray[cnt++];
						}

						setDataElements(j, i, btemp, data);
					}
				}
				break;
			}
			case DataBuffer.TYPE_USHORT : {

				short[] usarray = (short[]) obj;
				short[] ustemp = new short[numDataElems];

				for (int i = y; i < y + h; i++) {
					for (int j = x; j < x + w; j++) {
						for (int k = 0; k < numDataElems; k++) {
							ustemp[k] = usarray[cnt++];
						}
						setDataElements(j, i, ustemp, data);
					}
				}
				break;
			}
			case DataBuffer.TYPE_INT : {

				int[] iArray = (int[]) obj;
				int[] itemp = new int[numDataElems];

				for (int i = y; i < y + h; i++) {
					for (int j = x; j < x + w; j++) {
						for (int k = 0; k < numDataElems; k++) {
							itemp[k] = iArray[cnt++];
						}

						setDataElements(j, i, itemp, data);
					}
				}
				break;
			}

			case DataBuffer.TYPE_SHORT : {

				short[] sArray = (short[]) obj;
				short[] stemp = new short[numDataElems];

				for (int i = y; i < y + h; i++) {
					for (int j = x; j < x + w; j++) {
						for (int k = 0; k < numDataElems; k++) {
							stemp[k] = sArray[cnt++];
						}

						setDataElements(j, i, stemp, data);
					}
				}
				break;
			}
			case DataBuffer.TYPE_FLOAT : {

				float[] fArray = (float[]) obj;
				float[] ftemp = new float[numDataElems];

				for (int i = y; i < y + h; i++) {
					for (int j = x; j < x + w; j++) {
						for (int k = 0; k < numDataElems; k++) {
							ftemp[k] = fArray[cnt++];
						}

						setDataElements(j, i, ftemp, data);
					}
				}
				break;
			}
			case DataBuffer.TYPE_DOUBLE : {

				double[] dArray = (double[]) obj;
				double[] dtemp = new double[numDataElems];

				for (int i = y; i < y + h; i++) {
					for (int j = x; j < x + w; j++) {
						for (int k = 0; k < numDataElems; k++) {
							dtemp[k] = dArray[cnt++];
						}

						setDataElements(j, i, dtemp, data);
					}
				}
				break;
			}
		}
	}

	/**
	 * Sets a sample in the specified band for the pixel located at (x,y) in the
	 * DataBuffer using a float for input. ArrayIndexOutOfBoundsException may be
	 * thrown if the coordinates are not in bounds.
	 * 
	 * @param x
	 *            The X coordinate of the pixel location.
	 * @param y
	 *            The Y coordinate of the pixel location.
	 * @param b
	 *            The band to set.
	 * @param s
	 *            The input sample as a float.
	 * @param data
	 *            The DataBuffer containing the image data.
	 * 
	 * @throws ArrayIndexOutOfBoundsException
	 *             if coordinates are not in bounds
	 */

	@Override
	public void setSample(int x, int y, int b, float s, DataBuffer data) {
		data.setElemFloat(bankIndices[b], y * scanlineStride + x * pixelStride + bandOffsets[b], s);
	}

	/**
	 * Returns the sample in a specified band for the pixel located at (x,y) as
	 * a float. ArrayIndexOutOfBoundsException may be thrown if the coordinates
	 * are not in bounds.
	 * 
	 * @param x
	 *            The X coordinate of the pixel location.
	 * @param y
	 *            The Y coordinate of the pixel location.
	 * @param b
	 *            The band to return.
	 * @param data
	 *            The DataBuffer containing the image data.
	 * @return sample The floating point sample value
	 */

	@Override
	public float getSampleFloat(int x, int y, int b, DataBuffer data) {
		float sample = data.getElemFloat(bankIndices[b], y * scanlineStride + x * pixelStride + bandOffsets[b]);
		return sample;
	}

	/**
	 * Sets a sample in the specified band for the pixel located at (x,y) in the
	 * DataBuffer using a double for input. ArrayIndexOutOfBoundsException may
	 * be thrown if the coordinates are not in bounds.
	 * 
	 * @param x
	 *            The X coordinate of the pixel location.
	 * @param y
	 *            The Y coordinate of the pixel location.
	 * @param b
	 *            The band to set.
	 * @param s
	 *            The input sample as a double.
	 * @param data
	 *            The DataBuffer containing the image data.
	 * 
	 * @throws ArrayIndexOutOfBoundsException
	 *             if coordinates are not in bounds
	 */

	@Override
	public void setSample(int x, int y, int b, double s, DataBuffer data) {
		data.setElemDouble(bankIndices[b], y * scanlineStride + x * pixelStride + bandOffsets[b], s);
	}

	/**
	 * Returns the sample in a specified band for a pixel located at (x,y) as a
	 * double. ArrayIndexOutOfBoundsException may be thrown if the coordinates
	 * are not in bounds.
	 * 
	 * @param x
	 *            The X coordinate of the pixel location.
	 * @param y
	 *            The Y coordinate of the pixel location.
	 * @param b
	 *            The band to return.
	 * @param data
	 *            The DataBuffer containing the image data.
	 * @return sample The double sample value
	 */

	@Override
	public double getSampleDouble(int x, int y, int b, DataBuffer data) {
		double sample = data.getElemDouble(bankIndices[b], y * scanlineStride + x * pixelStride + bandOffsets[b]);
		return sample;
	}

	/**
	 * Returns all samples for a rectangle of pixels in a double array, one
	 * sample per array element. ArrayIndexOutOfBoundsException may be thrown if
	 * the coordinates are not in bounds.
	 * 
	 * @param x
	 *            The X coordinate of the upper left pixel location.
	 * @param y
	 *            The Y coordinate of the upper left pixel location.
	 * @param w
	 *            The width of the pixel rectangle.
	 * @param h
	 *            The height of the pixel rectangle.
	 * @param dArray
	 *            If non-null, returns the samples in this array.
	 * @param data
	 *            The DataBuffer containing the image data.
	 */

	@Override
	public double[] getPixels(int x, int y, int w, int h, double dArray[], DataBuffer data) {
		double pixels[];
		int Offset = 0;

		if (dArray != null) {
			pixels = dArray;
		} else {
			pixels = new double[numBands * w * h];
		}

		for (int i = y; i < h + y; i++) {
			for (int j = x; j < w + x; j++) {
				for (int k = 0; k < numBands; k++) {
					pixels[Offset++] = getSampleDouble(j, i, k, data);
				}
			}
		}

		return pixels;
	}

	/** Returns a String containing the values of all valid fields. */

	@Override
	public String toString() {
		String ret = "ComponentSampleModelJAI: " + "  dataType=" + getDataType() + "  numBands=" + getNumBands() + "  width=" + getWidth() + "  height=" + getHeight() + "  bandOffsets=[ ";
		for (int i = 0; i < numBands; i++) {
			ret += getBandOffsets()[i] + " ";
		}
		ret += "]";
		return ret;
	}
}
