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

import java.awt.image.DataBuffer;

/**
 * An extension of <code>DataBuffer</code> that stores data internally in
 * <code>double</code> form.
 * 
 * @see DataBuffer
 */
public class DataBufferDouble extends DataBuffer {

	/** The array of data banks. */
	protected double bankdata[][];

	/** A reference to the default data bank. */
	protected double data[];

	/**
	 * Constructs a <code>double</code>-based <code>DataBuffer</code> with a
	 * specified size.
	 * 
	 * @param size
	 *            The number of elements in the <code>DataBuffer</code>.
	 */
	public DataBufferDouble(int size) {
		super(TYPE_DOUBLE, size);
		data = new double[size];
		bankdata = new double[1][];
		bankdata[0] = data;
	}

	/**
	 * Constructs a <code>double</code>-based <code>DataBuffer</code> with a
	 * specified number of banks, all of which are of a specified size.
	 * 
	 * @param size
	 *            The number of elements in each bank of the
	 *            <code>DataBuffer</code>.
	 * @param numBanks
	 *            The number of banks in the <code>DataBuffer</code>.
	 */
	public DataBufferDouble(int size, int numBanks) {
		super(TYPE_DOUBLE, size, numBanks);
		bankdata = new double[numBanks][];
		for (int i = 0; i < numBanks; i++) {
			bankdata[i] = new double[size];
		}
		data = bankdata[0];
	}

	/**
	 * Constructs a <code>double</code>-based <code>DataBuffer</code> with the
	 * specified data array. Only the first <code>size</code> elements are
	 * available for use by this <code>DataBuffer</code>. The array must be
	 * large enough to hold <code>size</code> elements.
	 * 
	 * @param dataArray
	 *            An array of <code>double</code>s to be used as the first and
	 *            only bank of this <code>DataBuffer</code>.
	 * @param size
	 *            The number of elements of the array to be used.
	 */
	public DataBufferDouble(double dataArray[], int size) {
		super(TYPE_DOUBLE, size);
		data = dataArray;
		bankdata = new double[1][];
		bankdata[0] = data;
	}

	/**
	 * Constructs a <code>double</code>-based <code>DataBuffer</code> with the
	 * specified data array. Only the elements between <code>offset</code> and
	 * <code>offset + size - 1</code> are available for use by this
	 * <code>DataBuffer</code>. The array must be large enough to hold
	 * <code>offset + size</code> elements.
	 * 
	 * @param dataArray
	 *            An array of <code>double</code>s to be used as the first and
	 *            only bank of this <code>DataBuffer</code>.
	 * @param size
	 *            The number of elements of the array to be used.
	 * @param offset
	 *            The offset of the first element of the array that will be
	 *            used.
	 */
	public DataBufferDouble(double dataArray[], int size, int offset) {
		super(TYPE_DOUBLE, size, 1, offset);
		data = dataArray;
		bankdata = new double[1][];
		bankdata[0] = data;
	}

	/**
	 * Constructs a <code>double</code>-based <code>DataBuffer</code> with the
	 * specified data arrays. Only the first <code>size</code> elements of each
	 * array are available for use by this <code>DataBuffer</code>. The number
	 * of banks will be equal <code>to dataArray.length</code>.
	 * 
	 * @param dataArray
	 *            An array of arrays of <code>double</code>s to be used as the
	 *            banks of this <code>DataBuffer</code>.
	 * @param size
	 *            The number of elements of each array to be used.
	 */
	public DataBufferDouble(double dataArray[][], int size) {
		super(TYPE_DOUBLE, size, dataArray.length);
		bankdata = dataArray;
		data = bankdata[0];
	}

	/**
	 * Constructs a <code>double</code>-based <code>DataBuffer</code> with the
	 * specified data arrays, size, and per-bank offsets. The number of banks is
	 * equal to dataArray.length. Each array must be at least as large as
	 * <code>size plus the corresponding
	 * offset.  There must be an entry in the <code>offsets</code> array for
	 * each data array.
	 * 
	 * @param dataArray
	 *            An array of arrays of <code>double</code>s to be used as the
	 *            banks of this <code>DataBuffer</code>.
	 * @param size
	 *            The number of elements of each array to be used.
	 * @param offsets
	 *            An array of integer offsets, one for each bank.
	 */
	public DataBufferDouble(double dataArray[][], int size, int offsets[]) {
		super(TYPE_DOUBLE, size, dataArray.length, offsets);
		bankdata = dataArray;
		data = bankdata[0];
	}

	/** Returns the default (first) <code>double</code> data array. */
	public double[] getData() {
		return data;
	}

	/** Returns the data array for the specified bank. */
	public double[] getData(int bank) {
		return bankdata[bank];
	}

	/** Returns the data array for all banks. */
	public double[][] getBankData() {
		return bankdata;
	}

	/**
	 * Returns the requested data array element from the first (default) bank as
	 * an <code>int</code>.
	 * 
	 * @param i
	 *            The desired data array element.
	 * 
	 * @return The data entry as an <code>int</code>.
	 */

	@Override
	public int getElem(int i) {
		return (int) data[i + offset];
	}

	/**
	 * Returns the requested data array element from the specified bank as an
	 * <code>int</code>.
	 * 
	 * @param bank
	 *            The bank number.
	 * @param i
	 *            The desired data array element.
	 * 
	 * @return The data entry as an <code>int</code>.
	 */

	@Override
	public int getElem(int bank, int i) {
		return (int) bankdata[bank][i + offsets[bank]];
	}

	/**
	 * Sets the requested data array element in the first (default) bank to the
	 * given <code>int</code>.
	 * 
	 * @param i
	 *            The desired data array element.
	 * @param val
	 *            The value to be set.
	 */

	@Override
	public void setElem(int i, int val) {
		data[i + offset] = val;
	}

	/**
	 * Sets the requested data array element in the specified bank to the given
	 * <code>int</code>.
	 * 
	 * @param bank
	 *            The bank number.
	 * @param i
	 *            The desired data array element.
	 * @param val
	 *            The value to be set.
	 */

	@Override
	public void setElem(int bank, int i, int val) {
		bankdata[bank][i + offsets[bank]] = val;
	}

	/**
	 * Returns the requested data array element from the first (default) bank as
	 * a <code>float</code>.
	 * 
	 * @param i
	 *            The desired data array element.
	 * 
	 * @return The data entry as a <code>float</code>.
	 */

	@Override
	public float getElemFloat(int i) {
		return (float) data[i + offset];
	}

	/**
	 * Returns the requested data array element from the specified bank as a
	 * <code>float</code>.
	 * 
	 * @param bank
	 *            The bank number.
	 * @param i
	 *            The desired data array element.
	 * 
	 * @return The data entry as a <code>float</code>.
	 */

	@Override
	public float getElemFloat(int bank, int i) {
		return (float) bankdata[bank][i + offsets[bank]];
	}

	/**
	 * Sets the requested data array element in the first (default) bank to the
	 * given <code>float</code>.
	 * 
	 * @param i
	 *            The desired data array element.
	 * @param val
	 *            The value to be set.
	 */

	@Override
	public void setElemFloat(int i, float val) {
		data[i + offset] = val;
	}

	/**
	 * Sets the requested data array element in the specified bank to the given
	 * <code>float</code>.
	 * 
	 * @param bank
	 *            The bank number.
	 * @param i
	 *            The desired data array element.
	 * @param val
	 *            The value to be set.
	 */

	@Override
	public void setElemFloat(int bank, int i, float val) {
		bankdata[bank][i + offsets[bank]] = val;
	}

	/**
	 * Returns the requested data array element from the first (default) bank as
	 * a <code>double</code>.
	 * 
	 * @param i
	 *            The desired data array element.
	 * 
	 * @return The data entry as a <code>double</code>.
	 */

	@Override
	public double getElemDouble(int i) {
		return data[i + offset];
	}

	/**
	 * Returns the requested data array element from the specified bank as a
	 * <code>double</code>.
	 * 
	 * @param bank
	 *            The bank number.
	 * @param i
	 *            The desired data array element.
	 * 
	 * @return The data entry as a <code>double</code>.
	 */

	@Override
	public double getElemDouble(int bank, int i) {
		return bankdata[bank][i + offsets[bank]];
	}

	/**
	 * Sets the requested data array element in the first (default) bank to the
	 * given <code>double</code>.
	 * 
	 * @param i
	 *            The desired data array element.
	 * @param val
	 *            The value to be set.
	 */

	@Override
	public void setElemDouble(int i, double val) {
		data[i + offset] = val;
	}

	/**
	 * Sets the requested data array element in the specified bank to the given
	 * <code>double</code>.
	 * 
	 * @param bank
	 *            The bank number.
	 * @param i
	 *            The desired data array element.
	 * @param val
	 *            The value to be set.
	 */

	@Override
	public void setElemDouble(int bank, int i, double val) {
		bankdata[bank][i + offsets[bank]] = val;
	}
}
