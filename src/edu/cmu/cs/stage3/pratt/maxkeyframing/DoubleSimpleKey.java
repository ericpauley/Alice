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

package edu.cmu.cs.stage3.pratt.maxkeyframing;

/**
 * @author Jason Pratt
 */
public class DoubleSimpleKey extends SimpleKey {
	public DoubleSimpleKey( double time, double value ) {
		super( time, new double[] { value } );
	}

	
	public Object createSample( double[] components ) {
		return new Double( components[0] );
	}

	public static DoubleSimpleKey valueOf( String s ) {
		java.util.StringTokenizer st = new java.util.StringTokenizer( s, " \t,[]" );

		String className = st.nextToken(); // unused
		double time = Double.parseDouble( st.nextToken() );
		double value = Double.parseDouble( st.nextToken() );

		return new DoubleSimpleKey( time, value );
	}
}
