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

package edu.cmu.cs.stage3.alice.scenegraph;

/**
 * @author Dennis Cosgrove
 */

public class Color implements Cloneable, java.io.Serializable, edu.cmu.cs.stage3.math.Interpolable {
	public static final Color RED = new Color( java.awt.Color.red );
	public static final Color PINK = new Color( java.awt.Color.pink );
	public static final Color ORANGE = new Color( new java.awt.Color( 255, 165, 0 ) );
	public static final Color YELLOW = new Color( java.awt.Color.yellow );
	public static final Color GREEN = new Color( java.awt.Color.green );
	public static final Color BLUE = new Color( java.awt.Color.blue );
	public static final Color PURPLE = new Color( new java.awt.Color( 128, 0, 128 ) );
	public static final Color BROWN = new Color( new java.awt.Color( 162, 42, 42 ) );
	public static final Color WHITE = new Color( java.awt.Color.white );
	public static final Color LIGHT_GRAY = new Color( java.awt.Color.lightGray );
	public static final Color GRAY = new Color( java.awt.Color.gray );
	public static final Color DARK_GRAY = new Color( java.awt.Color.darkGray );
	public static final Color BLACK = new Color( java.awt.Color.black );
	public static final Color CYAN = new Color( java.awt.Color.cyan );
	public static final Color MAGENTA = new Color( java.awt.Color.magenta );

	public float red;
	public float green;
	public float blue;
	public float alpha;

	public Color() {
		this( 0, 0, 0, 1 );
	}
	public Color( float red, float green, float blue ) {
		this( red, green, blue, 1 );
	}
	public Color( float red, float green, float blue, float alpha ) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}
	public Color( float[] array ) {
		alpha = 1;
		switch( array.length ) {
		case 4:
			alpha = array[ 3 ];
		case 3:
			blue = array[ 2 ];
			green = array[ 1 ];
			red = array[ 0 ];
			break;
		default:
			throw new RuntimeException();
		}
	}
	public Color( double red, double green, double blue ) {
		this( (float)red, (float)green, (float)blue );
	}
	public Color( double red, double green, double blue, double alpha ) {
		this( (float)red, (float)green, (float)blue, (float)alpha );
	}
	public Color( double[] array ) {
		alpha = 1;
		switch( array.length ) {
		case 4:
			alpha = (float)array[ 3 ];
		case 3:
			blue = (float)array[ 2 ];
			green = (float)array[ 1 ];
			red = (float)array[ 0 ];
			break;
		default:
			throw new RuntimeException();
		}
	}
	public Color( Color color ) {
		this( color.red, color.green, color.blue, color.alpha );
	}
	public Color( javax.vecmath.Color3f color ) {
		this( color.x, color.y, color.z );
	}
	public Color( javax.vecmath.Color4f color ) {
		this( color.x, color.y, color.z, color.w );
	}
	public Color( java.awt.Color color ) {
		this( color.getRed()/255.0f, color.getGreen()/255.0f, color.getBlue()/255.0f, 1 );
	}

    
	public boolean equals( Object o ) {
        if( o==this ) return true;
        if( o!=null && o instanceof Color ) {
            Color c = (Color)o;
            return red==c.red && green==c.green && blue==c.blue && alpha==c.alpha;
        } else {
            return false;
        }
    }

	public java.awt.Color createAWTColor() {
		float r = (float)Math.max( Math.min( red, 1.0 ), 0.0 );
		float g = (float)Math.max( Math.min( green, 1.0 ), 0.0 );
		float b = (float)Math.max( Math.min( blue, 1.0 ), 0.0 );
		return new java.awt.Color( r, g, b );
	}
	public javax.vecmath.Color3f createVecmathColor3f() {
		return new javax.vecmath.Color3f( red, green, blue );
	}
	public javax.vecmath.Color4f createVecmathColor4f() {
		return new javax.vecmath.Color4f( red, green, blue, alpha );
	}

	public float getRed() {
		return red;
	}
	public float getGreen() {
		return green;
	}
	public float getBlue() {
		return blue;
	}
	public float getAlpha() {
		return alpha;
	}

	public void setRed( float red ) {
		this.red = red;
	}
	public void setGreen( float green ) {
		this.green = green;
	}
	public void setBlue( float blue ) {
		this.blue = blue;
	}
	public void setAlpha( float alpha ) {
		this.alpha = alpha;
	}

	
	public synchronized Object clone() {
		try {
			return super.clone();
		} catch( CloneNotSupportedException e ) {
			throw new InternalError();
		}
	}
	public static Color interpolate( Color a, Color b, double portion ) {
		return new Color( a.red+(b.red-a.red)*(float)portion, a.green+(b.green-a.green)*(float)portion, a.blue+(b.blue-a.blue)*(float)portion, a.alpha+(b.alpha-a.alpha)*(float)portion );
	}
	public edu.cmu.cs.stage3.math.Interpolable interpolate( edu.cmu.cs.stage3.math.Interpolable b, double portion ) {
		return interpolate( this, (Color)b, portion );
	}
	
	public String toString() {
		return "edu.cmu.cs.stage3.alice.scenegraph.Color[r="+red+",g="+green+",b="+blue+",a="+alpha+"]";
	}
	public static Color valueOf( String s ) {
		String[] markers = { "edu.cmu.cs.stage3.alice.scenegraph.Color[r=", ",g=", ",b=", ",a=", "]" };
		float[] values = new float[markers.length-1];
		for( int i=0; i<values.length; i++ ) {
			int begin = s.indexOf( markers[i] ) + markers[i].length();
			int end = s.indexOf( markers[i+1] );
			values[i] = Float.valueOf( s.substring( begin, end ) ).floatValue();
		}
		return new Color( values );
	}
}
