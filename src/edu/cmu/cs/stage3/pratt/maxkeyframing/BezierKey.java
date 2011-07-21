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
public abstract class BezierKey extends Key {
	private double[] incomingControlComponents;
	private double[] outgoingControlComponents;

	protected BezierKey( double time, double[] components, double[] incomingControlComponents, double[] outgoingControlComponents ) {
		setTime( time );
		setValueComponents( components );
		this.incomingControlComponents = incomingControlComponents;
		this.outgoingControlComponents = outgoingControlComponents;
	}

	public double[] getIncomingControlComponents() {
		return incomingControlComponents;
	}

	public double[] getOutgoingControlComponents() {
		return outgoingControlComponents;
	}

	
	public String toString() {
		String className = this.getClass().getName();
		double[] components = getValueComponents();
		int numComponents = components.length;

		StringBuffer repr = new StringBuffer();
		repr.append( className );
		repr.append( "[" );
		repr.append( getTime() );
		repr.append( "," );
		for( int i = 0; i < numComponents; i++ ) {
			repr.append( components[i] );
			repr.append( "," );
		}
		for( int i = 0; i < numComponents; i++ ) {
			repr.append( incomingControlComponents[i] );
			repr.append( "," );
		}
		for( int i = 0; i < numComponents - 1; i++ ) {
			repr.append( outgoingControlComponents[i] );
			repr.append( "," );
		}
		repr.append( outgoingControlComponents[numComponents - 1] );
		repr.append( "]" );

		return repr.toString();
	}
}