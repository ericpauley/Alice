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
/** @deprecated */
public class Vertex extends Vertex3d {
	public static final int POSITION_XYZ = FORMAT_POSITION;
	public static final int NORMAL_IJK = FORMAT_NORMAL;
	public static final int DIFFUSE_RGBA = FORMAT_DIFFUSE_COLOR;
	public static final int SPECULAR_RGBA = FORMAT_SPECULAR_HIGHLIGHT_COLOR;
	public static final int TEXTURE_COORDINATE_UV0 = FORMAT_TEXTURE_COORDINATE_0;

	public Vertex( int format ) {
		super( format );
	}

	public static Vertex createXYZVertex( double x, double y, double z ) {
		Vertex vertex = new Vertex( POSITION_XYZ );
		vertex.setX( x );
		vertex.setY( y );
		vertex.setZ( z );
		return vertex;
	}
	public static Vertex createXYZDiffuseVertex( double x, double y, double z, Color color ) {
		Vertex vertex = new Vertex( POSITION_XYZ|DIFFUSE_RGBA );
		vertex.setX( x );
		vertex.setY( y );
		vertex.setZ( z );
		vertex.setR( color.getRed() );
		vertex.setG( color.getGreen() );
		vertex.setB( color.getBlue() );
		vertex.setA( color.getAlpha() );
		return vertex;
	}
	public static Vertex createXYZIJKUVVertex( double x, double y, double z, double i, double j, double k, double u, double v ) {
		Vertex vertex = new Vertex( POSITION_XYZ|NORMAL_IJK|TEXTURE_COORDINATE_UV0 );
		vertex.setX( x );
		vertex.setY( y );
		vertex.setZ( z );
		vertex.setI( i );
		vertex.setJ( j );
		vertex.setK( k );
		vertex.setU( u );
		vertex.setV( v );
		return vertex;
	}
	public double[] getArray() {
		byte xyzOffset = Byte.MIN_VALUE;
		byte ijkOffset = Byte.MIN_VALUE;
		byte uvOffset = Byte.MIN_VALUE;
		byte rgbaOffset = Byte.MIN_VALUE;
		byte size = 0;
		int format = getFormat();
		if( (format&POSITION_XYZ) != 0 ) {
			xyzOffset = size;
			size += 3;
		}
		if( (format&NORMAL_IJK) != 0 ) {
			ijkOffset = size;
			size += 3;
		}
		if( (format&DIFFUSE_RGBA) != 0 ) {
			rgbaOffset = size;
			size += 4;
		}
		if( (format&SPECULAR_RGBA) != 0 ) {
			rgbaOffset = size;
			size += 4;
		}
		if( (format&TEXTURE_COORDINATE_UV0) != 0 ) {
			uvOffset = size;
			size += 2;
		}
		double[] array = new double[ size ];
		if( (format&POSITION_XYZ) != 0 ) {
			array[ xyzOffset ] = position.x;
			array[ xyzOffset+1 ] = position.y;
			array[ xyzOffset+2 ] = position.z;
		}
		if( (format&NORMAL_IJK) != 0 ) {
			array[ xyzOffset ] = normal.x;
			array[ xyzOffset+1 ] = normal.y;
			array[ xyzOffset+2 ] = normal.z;
		}
		if( (format&DIFFUSE_RGBA) != 0 ) {
			array[ xyzOffset ] = diffuseColor.red;
			array[ xyzOffset+1 ] = diffuseColor.green;
			array[ xyzOffset+2 ] = diffuseColor.blue;
			array[ xyzOffset+3 ] = diffuseColor.alpha;
		}
		if( (format&SPECULAR_RGBA) != 0 ) {
			array[ xyzOffset ] = specularHighlightColor.red;
			array[ xyzOffset+1 ] = specularHighlightColor.green;
			array[ xyzOffset+2 ] = specularHighlightColor.blue;
			array[ xyzOffset+3 ] = specularHighlightColor.alpha;
		}
		if( (format&TEXTURE_COORDINATE_UV0) != 0 ) {
			array[ xyzOffset ] = textureCoordinate0.x;
			array[ xyzOffset+1 ] = textureCoordinate0.y;
		}
		return array;
	}
	public edu.cmu.cs.stage3.math.Vector3 getXYZ() {
		return new edu.cmu.cs.stage3.math.Vector3( getX(), getY(), getZ() );
	}
	public void setXYZ( edu.cmu.cs.stage3.math.Vector3 v ) {
		setX( v.x );
		setY( v.y );
		setZ( v.z );
	}
	public edu.cmu.cs.stage3.math.Vector3 getIJK() {
		return new edu.cmu.cs.stage3.math.Vector3( getI(), getJ(), getK() );
	}
	public void setIJK(  edu.cmu.cs.stage3.math.Vector3 v ) {
		setI( v.x );
		setJ( v.y );
		setK( v.z );
	}
	public double getX() {
		if( (getFormat()&POSITION_XYZ)!=0 ) {
			return position.x;
		} else {
			return Double.NaN;
		}
	}
	public double getY() {
		if( (getFormat()&POSITION_XYZ)!=0 ) {
			return position.y;
		} else {
			return Double.NaN;
		}
	}
	public double getZ() {
		if( (getFormat()&POSITION_XYZ)!=0 ) {
			return position.z;
		} else {
			return Double.NaN;
		}
	}
	public double getI() {
		if( (getFormat()&NORMAL_IJK)!=0 ) {
			return normal.x;
		} else {
			return Double.NaN;
		}
	}
	public double getJ() {
		if( (getFormat()&NORMAL_IJK)!=0 ) {
			return normal.y;
		} else {
			return Double.NaN;
		}
	}
	public double getK() {
		if( (getFormat()&NORMAL_IJK)!=0 ) {
			return normal.z;
		} else {
			return Double.NaN;
		}
	}
	public double getU() {
		if( (getFormat()&TEXTURE_COORDINATE_UV0)!=0 ) {
			return textureCoordinate0.x;
		} else {
			return Double.NaN;
		}
	}
	public double getV() {
		if( (getFormat()&TEXTURE_COORDINATE_UV0)!=0 ) {
			return textureCoordinate0.y;
		} else {
			return Double.NaN;
		}
	}
	public double getR() {
		if( (getFormat()&DIFFUSE_RGBA)!=0 ) {
			return diffuseColor.red;
		} else {
			return Double.NaN;
		}
	}
	public double getG() {
		if( (getFormat()&DIFFUSE_RGBA)!=0 ) {
			return diffuseColor.green;
		} else {
			return Double.NaN;
		}
	}
	public double getB() {
		if( (getFormat()&DIFFUSE_RGBA)!=0 ) {
			return diffuseColor.blue;
		} else {
			return Double.NaN;
		}
	}
	public double getA() {
		if( (getFormat()&DIFFUSE_RGBA)!=0 ) {
			return diffuseColor.alpha;
		} else {
			return Double.NaN;
		}
	}
	public void setX( double x ) {
		if( (getFormat()&POSITION_XYZ)!=0 ) {
			position.x = x;
		} else {
			if( !Double.isNaN( x ) ) {
				throw new RuntimeException();
			}
		}
	}
	public void setY( double y ) {
		if( (getFormat()&POSITION_XYZ)!=0 ) {
			position.y = y;
		} else {
			if( !Double.isNaN( y ) ) {
				throw new RuntimeException();
			}
		}
	}
	public void setZ( double z ) {
		if( (getFormat()&POSITION_XYZ)!=0 ) {
			position.z = z;
		} else {
			if( !Double.isNaN( z ) ) {
				throw new RuntimeException();
			}
		}
	}
	public void setI( double i ) {
		if( (getFormat()&NORMAL_IJK)!=0 ) {
			normal.x = i;
		} else {
			if( !Double.isNaN( i ) ) {
				throw new RuntimeException();
			}
		}
	}
	public void setJ( double j ) {
		if( (getFormat()&NORMAL_IJK)!=0 ) {
			normal.y = j;
		} else {
			if( !Double.isNaN( j ) ) {
				throw new RuntimeException();
			}
		}
	}
	public void setK( double k ) {
		if( (getFormat()&NORMAL_IJK)!=0 ) {
			normal.z = k;
		} else {
			if( !Double.isNaN( k ) ) {
				throw new RuntimeException();
			}
		}
	}
	public void setU( double u ) {
		if( (getFormat()&TEXTURE_COORDINATE_UV0)!=0 ) {
			textureCoordinate0.x = (float)u;
		} else {
			if( !Double.isNaN( u ) ) {
				throw new RuntimeException();
			}
		}
	}
	public void setV( double v ) {
		if( (getFormat()&TEXTURE_COORDINATE_UV0)!=0 ) {
			textureCoordinate0.y = (float)v;
		} else {
			if( !Double.isNaN( v ) ) {
				throw new RuntimeException();
			}
		}
	}
	public void setR( double r ) {
		if( (getFormat()&DIFFUSE_RGBA)!=0 ) {
			diffuseColor.red = (float)r;
		} else {
			if( !Double.isNaN( r ) ) {
				throw new RuntimeException();
			}
		}
	}
	public void setG( double g ) {
		if( (getFormat()&DIFFUSE_RGBA)!=0 ) {
			diffuseColor.green = (float)g;
		} else {
			if( !Double.isNaN( g ) ) {
				throw new RuntimeException();
			}
		}
	}
	public void setB( double b ) {
		if( (getFormat()&DIFFUSE_RGBA)!=0 ) {
			diffuseColor.blue = (float)b;
		} else {
			if( !Double.isNaN( b ) ) {
				throw new RuntimeException();
			}
		}
	}
	public void setA( double a ) {
		if( (getFormat()&DIFFUSE_RGBA)!=0 ) {
			diffuseColor.alpha = (float)a;
		} else {
			if( !Double.isNaN( a ) ) {
				throw new RuntimeException();
			}
		}
	}
	
	public String toString() {
		return "edu.cmu.cs.stage3.alice.scenegraph.Vertex[format="+getFormat()+",x="+getX()+",y="+getY()+",z="+getZ()+",i="+getI()+",j="+getJ()+",k="+getK()+",u="+getU()+",v="+getV()+",r="+getR()+",g="+getG()+",b="+getB()+",a="+getA()+"]";
	}
	public static Vertex valueOf( String s ) {
		String[] markers = { "edu.cmu.cs.stage3.alice.scenegraph.Vertex[format=", ",x=", ",y=", ",z=", ",z=", ",i=", ",j=", ",k=", ",u=", ",v=", ",r=", ",g=", ",b=", ",a=", "]" };
		double[] values = new double[markers.length-1];
		for( int i=0; i<values.length; i++ ) {
			int begin = s.indexOf( markers[i] ) + markers[i].length();
			int end = s.indexOf( markers[i+1] );
			values[i] = Double.valueOf( s.substring( begin, end ) ).doubleValue();
		}
		Vertex v = new Vertex( (int)values[0] );
		v.setX( values[1] );
		v.setY( values[2] );
		v.setZ( values[3] );
		v.setI( values[4] );
		v.setJ( values[5] );
		v.setK( values[6] );
		v.setU( values[7] );
		v.setV( values[8] );
		v.setR( values[9] );
		v.setG( values[10] );
		v.setB( values[11] );
		v.setA( values[12] );
		return v;
	}
}
