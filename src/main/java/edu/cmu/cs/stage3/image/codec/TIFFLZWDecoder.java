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
 * A class for performing LZW decoding.
 */
public class TIFFLZWDecoder {

    byte stringTable[][];
    byte data[] = null, uncompData[];
    int tableIndex, bitsToGet = 9;
    int bytePointer, bitPointer;
    int dstIndex;
    int w, h;
    int predictor, samplesPerPixel;
    int nextData = 0;
    int nextBits = 0;

    int andTable[] = {
	511,
	1023,
	2047,
	4095
    };

    public TIFFLZWDecoder(int w, int predictor, int samplesPerPixel) {
	this.w = w;
	this.predictor = predictor;
	this.samplesPerPixel = samplesPerPixel;
    }

    /**
     * Method to decode LZW compressed data.
     *
     * @param data            The compressed data.
     * @param uncompData      Array to return the uncompressed data in.
     * @param h               The number of rows the compressed data contains.
     */
    public byte[] decode(byte data[], byte uncompData[], int h) {

	initializeStringTable();

	this.data = data;
	this.h = h;
	this.uncompData = uncompData;

	// Initialize pointers
	bytePointer = 0;
	bitPointer = 0;
	dstIndex = 0;


	nextData = 0;
	nextBits = 0;

	int code, oldCode = 0;
	byte string[];

	while ( ((code = getNextCode()) != 257) &&
		dstIndex != uncompData.length) {

	    if (code == 256) {

		initializeStringTable();
		code = getNextCode();

		if (code == 257) {
		    break;
		}

		writeString(stringTable[code]);
		oldCode = code;

	    } else {

		if (code < tableIndex) {

		    string = stringTable[code];

		    writeString(string);
		    addStringToTable(stringTable[oldCode], string[0]);
		    oldCode = code;

		} else {

		    string = stringTable[oldCode];
		    string = composeString(string, string[0]);
		    writeString(string);
		    addStringToTable(string);
		    oldCode = code;
		}

	    }

	}

	// Horizontal Differencing Predictor
	if (predictor == 2) {

	    int count;
	    for (int j = 0; j < h; j++) {

		count = samplesPerPixel * (j * w + 1);

		for (int i = samplesPerPixel; i < w * samplesPerPixel; i++) {

		    uncompData[count] += uncompData[count - samplesPerPixel];
		    count++;
		}
	    }
	}

	return uncompData;
    }


    /**
     * Initialize the string table.
     */
    public void initializeStringTable() {

	stringTable = new byte[4096][];

	for (int i=0; i<256; i++) {
	    stringTable[i] = new byte[1];
	    stringTable[i][0] = (byte)i;
	}

	tableIndex = 258;
	bitsToGet = 9;
    }

    /**
     * Write out the string just uncompressed.
     */
    public void writeString(byte string[]) {

	for (int i=0; i<string.length; i++) {
	    uncompData[dstIndex++] = string[i];
	}
    }

    /**
     * Add a new string to the string table.
     */
    public void addStringToTable(byte oldString[], byte newString) {
	int length = oldString.length;
	byte string[] = new byte[length + 1];
	System.arraycopy(oldString, 0, string, 0, length);
	string[length] = newString;

	// Add this new String to the table
	stringTable[tableIndex++] = string;

	if (tableIndex == 511) {
	    bitsToGet = 10;
	} else if (tableIndex == 1023) {
	    bitsToGet = 11;
	} else if (tableIndex == 2047) {
	    bitsToGet = 12;
	}
    }

    /**
     * Add a new string to the string table.
     */
    public void addStringToTable(byte string[]) {

	// Add this new String to the table
	stringTable[tableIndex++] = string;

	if (tableIndex == 511) {
	    bitsToGet = 10;
	} else if (tableIndex == 1023) {
	    bitsToGet = 11;
	} else if (tableIndex == 2047) {
	    bitsToGet = 12;
	}
    }

    /**
     * Append <code>newString</code> to the end of <code>oldString</code>.
     */
    public byte[] composeString(byte oldString[], byte newString) {
	int length = oldString.length;
	byte string[] = new byte[length + 1];
	System.arraycopy(oldString, 0, string, 0, length);
	string[length] = newString;

	return string;
    }

    // Returns the next 9, 10, 11 or 12 bits
    public int getNextCode() {

	nextData = (nextData << 8) | (data[bytePointer++] & 0xff);
	nextBits += 8;

	if (nextBits < bitsToGet) {

	    nextData = (nextData << 8) | (data[bytePointer++] & 0xff);
	    nextBits += 8;
	}

	int code = (nextData >> (nextBits - bitsToGet)) & andTable[bitsToGet-9];
	nextBits -= bitsToGet;

	return code;
    }

}
