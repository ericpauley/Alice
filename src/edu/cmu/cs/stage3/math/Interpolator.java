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

public class Interpolator {
	public static int interpolate( int a, int b, double portion ) {
		return a+(int)((b-a)*portion);
	}
	public static double interpolate( double a, double b, double portion ) {
		return a+(b-a)*portion;
	}
	public static Number interpolate( Number a, Number b, double portion ) {
		return new Double( interpolate( a.doubleValue(), b.doubleValue(), portion ) );
	}
	public static double[] interpolate( double[] a, double[] b, double portion ) {
		if( a.length!=b.length ) return null;
		double[] v = new double[a.length];
		for( int i=0; i<a.length; i++ ) {
			v[i] = interpolate( a[i], b[i], portion );
		}
		return v;
	}
	public static double[][] interpolate( double[][] a, double[][] b, double portion ) {
		if( a.length!=b.length ) return null;
		double[][] m = new double[a.length][];
		for( int i=0; i<a.length; i++ ) {
			if( a[i].length!=b[i].length ) return null;
			m[i] = new double[a[i].length];
			for( int j=0; j<a[i].length; j++ ) {
				m[i][j] = interpolate( a[i][j], b[i][j], portion );
			}
		}
		return m;
	}
	public static java.awt.Color interpolate( java.awt.Color a, java.awt.Color b, double portion ) {
		return new java.awt.Color(
			Math.max( 0, Math.min( interpolate( a.getRed(), b.getRed(), portion ), 255 ) ),
			Math.max( 0, Math.min( interpolate( a.getGreen(), b.getGreen(), portion ), 255) ),
			Math.max( 0, Math.min( interpolate( a.getBlue(), b.getBlue(), portion ), 255) )
			);
	}
	public static Object interpolate( Object a, Object b, double portion ) {
		if( a instanceof javax.vecmath.Matrix4d ) {
			Interpolable ai;
			if( a instanceof Matrix44 ) {
				ai = (Matrix44)a;
			} else {
				ai = new Matrix44( (javax.vecmath.Matrix4d)a );
			}
			Interpolable bi;
			if( b instanceof Matrix44 ) {
				bi = (Matrix44)b;
			} else {
				bi = new Matrix44( (javax.vecmath.Matrix4d)b );
			}
			return ai.interpolate( bi, portion );
		} else if( a instanceof Interpolable ) {
			return ((Interpolable)a).interpolate( (Interpolable)b, portion );
		} else if( a instanceof Number ) {
			return interpolate( (Number)a, (Number)b, portion);
		} else if( a instanceof java.awt.Color ) {
			return interpolate( (java.awt.Color)a, (java.awt.Color)b, portion);
		} else if( a instanceof double[] ) {
			return interpolate( (double[])a, (double[])b, portion);
		} else if( a instanceof double[][] ) {
			return interpolate( (double[][])a, (double[][])b, portion);
		} else {
			return b;
		}
	}
}
