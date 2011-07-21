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

package edu.cmu.cs.stage3.math;

public class Shear {
	double xy = 1;
	double xz = 1;
	double yz = 1;
	public Shear() {
	}
	public Shear( double xy, double xz, double yz ) {
		this.xy = xy;
		this.xz = xz;
		this.yz = yz;
	}
	public Shear( double[] array ) {
		this( array[0], array[1], array[2] );
	}

	
	public String toString() {
		return "edu.cmu.cs.stage3.math.Shear[xy="+xy+",xz="+xz+",yz="+yz+"]";
	}
	public static Shear valueOf( String s ) {
		String[] markers = { "edu.cmu.cs.stage3.math.Shear[xy=", ",xz=", ",yz=", "]" };
		double[] values = new double[markers.length-1];
		for( int i=0; i<values.length; i++ ) {
			int begin = s.indexOf( markers[i] ) + markers[i].length();
			int end = s.indexOf( markers[i+1] );
			values[i] = Double.valueOf( s.substring( begin, end ) ).doubleValue();
		}
		return new Shear( values );
	}

}
