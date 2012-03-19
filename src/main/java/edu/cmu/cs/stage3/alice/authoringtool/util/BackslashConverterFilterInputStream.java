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

package edu.cmu.cs.stage3.alice.authoringtool.util;

public class BackslashConverterFilterInputStream extends java.io.FilterInputStream {
	int newChar;

	// creates a FilterInputStream that converts '\\' characters to '/'
	// characters
	public BackslashConverterFilterInputStream(java.io.InputStream is) {
		super(is);
		newChar = '/';
	}

	// creates a FilterInputStream that converts '\\' characters to
	// <code>newChar</code> characters
	public BackslashConverterFilterInputStream(java.io.InputStream is, int newChar) {
		super(is);
		this.newChar = newChar;
	}

	@Override
	public int read() throws java.io.IOException {
		int c = super.read();
		if (c == '\\') {
			c = newChar;
		}
		return c;
	}

	@Override
	public int read(byte[] b, int off, int len) throws java.io.IOException {
		int result = super.read(b, off, len);
		for (int i = 0; i < result; i++) {
			if (b[off + i] == '\\') {
				b[off + i] = (byte) newChar;
			}
		}
		return result;
	}
}