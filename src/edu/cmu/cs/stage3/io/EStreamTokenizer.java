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

package edu.cmu.cs.stage3.io;

/**
 * @author Ben Buchwald
 */

public class EStreamTokenizer extends java.io.StreamTokenizer {
	private String next_sval;
	private double next_nval;
	private int next_ttype;
	private boolean hold_back;

	public EStreamTokenizer(java.io.BufferedReader br) {
		super(br);
		hold_back = false;
	}

	
	public int nextToken() throws java.io.IOException {
		if (hold_back) {
			ttype = next_ttype;
			sval = next_sval;
			nval = next_nval;
			hold_back = false;
		} else {
			super.nextToken();
		}
		if (ttype == java.io.StreamTokenizer.TT_NUMBER) {
			double f = nval;
			super.nextToken();
			if (ttype == java.io.StreamTokenizer.TT_WORD) {
				if (sval.startsWith("e")) {
					int exponent = Integer.parseInt(sval.substring(1));
					ttype = java.io.StreamTokenizer.TT_NUMBER;
					nval = f * Math.pow(10, exponent);
					return ttype;
				}
			}
			hold_back = true;
			next_sval = sval;
			next_nval = nval;
			next_ttype = ttype;
			nval = f;
			ttype = java.io.StreamTokenizer.TT_NUMBER;
		}
		return ttype;
	}
}
