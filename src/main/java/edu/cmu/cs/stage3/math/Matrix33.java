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

public class Matrix33 extends javax.vecmath.Matrix3d implements Interpolable {
	public static final Matrix33 IDENTITY = new Matrix33();
	public Matrix33() {
		this( 1,0,0, 0,1,0, 0,0,1 );
	}
	public Matrix33( double rc00, double rc01, double rc02, double rc10, double rc11, double rc12, double rc20, double rc21, double rc22 ) {
		m00 = rc00; m01 = rc01; m02 = rc02;
		m10 = rc10; m11 = rc11; m12 = rc12;
		m20 = rc20; m21 = rc21; m22 = rc22;
	}
	public Matrix33( double[] row0, double[] row1, double[] row2 ) {
		this( row0[0], row0[1], row0[2], row1[0], row1[1], row1[2], row2[0], row2[1], row2[2] );
	}
	public Matrix33( javax.vecmath.Vector3d row0, javax.vecmath.Vector3d row1, javax.vecmath.Vector3d row2 ) {
		this( row0.x, row0.y, row0.z, row1.x, row1.y, row1.z, row2.x, row2.y, row2.z );
	}
	public Matrix33( double[] a ) {
		this( a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8] );
	}
	public Matrix33( double[][] m ) {
		this( m[0], m[1], m[2] );
	}
	public Matrix33( javax.vecmath.Matrix3d m ) {
		super( m );
	}
	public Matrix33( AxisAngle aa ) {
		setAxisAngle( aa );
	}
	public Matrix33( Quaternion q ) {
		setQuaternion( q );
	}
	public Matrix33( EulerAngles ea ) {
		setEulerAngles( ea );
	}
	
	public boolean equals( Object o ) {
		if( o==this ) return true;
		if( o!=null && o instanceof Matrix33 ) {
			Matrix33 m = (Matrix33)o;
			return  m00==m.m00 && m01==m.m01 && m02==m.m02 &&
					m10==m.m10 && m11==m.m11 && m12==m.m12 &&
					m20==m.m20 && m21==m.m21 && m22==m.m22;
		} else {
			return false;
		}
	}
	public double getItem( int i, int j ) {
		switch( i ) {
		case 0:
			switch( j ) {
			case 0:
				return m00;
			case 1:
				return m01;
			case 2:
				return m02;
			}
			break;
		case 1:
			switch( j ) {
			case 0:
				return m10;
			case 1:
				return m11;
			case 2:
				return m12;
			}
			break;
		case 2:
			switch( j ) {
			case 0:
				return m20;
			case 1:
				return m21;
			case 2:
				return m22;
			}
			break;
		}
		throw new IllegalArgumentException();
	}
	public void setItem( int i, int j, double v ) {
		switch( i ) {
		case 0:
			switch( j ) {
			case 0:
				m00 = v;
				return;
			case 1:
				m01 = v;
				return;
			case 2:
				m02 = v;
				return;
			}
			break;
		case 1:
			switch( j ) {
			case 0:
				m10 = v;
				return;
			case 1:
				m11 = v;
				return;
			case 2:
				m12 = v;
				return;
			}
			break;
		case 2:
			switch( j ) {
			case 0:
				m20 = v;
				return;
			case 1:
				m21 = v;
				return;
			case 2:
				m22 = v;
				return;
			}
			break;
		}
		throw new IllegalArgumentException();
	}


	public Vector3 getRow( int i ) {
		switch( i ) {
		case 0:
			return new Vector3( m00, m01, m02 );
		case 1:
			return new Vector3( m10, m11, m12 );
		case 2:
			return new Vector3( m20, m21, m22 );
		default:
			return null;
		}
	}
	public void setRow( int i, Vector3 v ) {
		switch( i ) {
		case 0:
			m00 = v.x;
			m01 = v.y;
			m02 = v.z;
			break;
		case 1:
			m10 = v.x;
			m11 = v.y;
			m12 = v.z;
			break;
		case 2:
			m20 = v.x;
			m21 = v.y;
			m22 = v.z;
			break;
		default:
			throw new IndexOutOfBoundsException();
		}
	}
	public Vector3 getColumn( int i ) {
		switch( i ) {
		case 0:
			return new Vector3( m00, m10, m20 );
		case 1:
			return new Vector3( m01, m11, m21 );
		case 2:
			return new Vector3( m02, m12, m22 );
		default:
			throw new IndexOutOfBoundsException();
		}
	}
	public void setColumn( int i, Vector3 v ) {
		switch( i ) {
		case 0:
			m00 = v.x;
			m10 = v.y;
			m20 = v.z;
			break;
		case 1:
			m01 = v.x;
			m11 = v.y;
			m21 = v.z;
			break;
		case 2:
			m02 = v.x;
			m12 = v.y;
			m22 = v.z;
			break;
		default:
			throw new IndexOutOfBoundsException();
		}
	}

	public double[] getArray( boolean rowMajor ) {
		if( rowMajor ) {
			double[] a = { m00, m01, m02, m10, m11, m12, m20, m21, m22 };
			return a;
		} else {
			double[] a = { m00, m10, m20, m01, m11, m21, m02, m12, m22 };
			return a;
		}
	}
	public void setArray( double[] a, boolean rowMajor ) {
		if( rowMajor ) {
			m00 = a[0]; m01 = a[1]; m02 = a[2];
			m10 = a[3]; m11 = a[4]; m12 = a[5];
			m20 = a[6]; m21 = a[7]; m22 = a[8];
		} else {
			m00 = a[0]; m01 = a[3]; m02 = a[6];
			m10 = a[1]; m11 = a[4]; m12 = a[7];
			m20 = a[2]; m21 = a[5]; m22 = a[8];
		}
	}

	public double[][] getMatrix() {
		double[][] m = { { m00, m01, m02 }, { m10, m11, m12 }, { m20, m21, m22 } };
		return m;
	}
	public void setMatrix( double[][] m ) {
		m00 = m[0][0]; m01 = m[0][1]; m02 = m[0][2];
		m10 = m[1][0]; m11 = m[1][1]; m12 = m[1][2];
		m20 = m[2][0]; m21 = m[2][1]; m22 = m[2][2];
	}
	public Quaternion getQuaternion() {
		return new Quaternion( this );
	}
	public void setQuaternion( Quaternion q ) {
		double xx = q.x * q.x;
		double xy = q.x * q.y;
		double xz = q.x * q.z;
		double yy = q.y * q.y;
		double yz = q.y * q.z;
		double zz = q.z * q.z;
		double wx = q.w * q.x;
		double wy = q.w * q.y;
		double wz = q.w * q.z;

		m00 = 1.0 - 2 * ( yy + zz );
		m01 =       2 * ( xy - wz );
		m02 =       2 * ( xz + wy );

		m10 =       2 * ( xy + wz );
		m11 = 1.0 - 2 * ( xx + zz );
		m12 =       2 * ( yz - wx );

		m20 =       2 * ( xz - wy );
		m21 =       2 * ( yz + wx );
		m22 = 1.0 - 2 * ( xx + yy );
	}
	public AxisAngle getAxisAngle() {
		return new AxisAngle( this );
	}
	public void setAxisAngle( AxisAngle aa ) {
		double theta = aa.getAngle();
		javax.vecmath.Vector3d axis = aa.getAxis();
		double cosTheta = Math.cos( theta );
		double sinTheta = Math.sin( theta );
		m00 = axis.x*axis.x+cosTheta*(1.0f-axis.x*axis.x);
		m01 = axis.x*axis.y*(1.0f-cosTheta)+axis.z*sinTheta;
		m02 = axis.z*axis.x*(1.0f-cosTheta)-axis.y*sinTheta;
		m10 = axis.x*axis.y*(1.0f-cosTheta)-axis.z*sinTheta;
		m11 = axis.y*axis.y+cosTheta*(1.0f-axis.y*axis.y);
		m12 = axis.y*axis.z*(1.0f-cosTheta)+axis.x*sinTheta;
		m20 = axis.z*axis.x*(1.0f-cosTheta)+axis.y*sinTheta;
		m21 = axis.y*axis.z*(1.0f-cosTheta)-axis.x*sinTheta;
		m22 = axis.z*axis.z+cosTheta*(1.0f-axis.z*axis.z);
	}

	public EulerAngles getEulerAngles() {
		return new EulerAngles( this );
	}
	public void setEulerAngles( EulerAngles ea ) {
		double c1 = Math.cos( ea.pitch );
		double s1 = Math.sin( ea.pitch );
		double c2 = Math.cos( ea.yaw );
		double s2 = Math.sin( ea.yaw );
		double c3 = Math.cos( ea.roll );
		double s3 = Math.sin( ea.roll );

		m00 = c2*c3;
		m01 = s2*s1*c3 - c1*s3;
		m02 = s2*c1*c3 + s1*s3;

		m10 = c2*s3;
		m11 = s2*s1*s3 + c1*c3;
		m12 = c2*s1;

		m20 = -s2;
		m21 = c2*s1;
		m22 = c2*c1;
		/*
		double cosPitch = Math.cos( ea.pitch );
		double sinPitch = Math.sin( ea.pitch );
		double cosYaw   = Math.cos( ea.yaw );
		double sinYaw   = Math.sin( ea.yaw );
		double cosRoll  = Math.cos( ea.roll );
		double sinRoll  = Math.sin( ea.roll );

		m00 = +cosRoll*cosYaw;
		m01 = +sinRoll*cosYaw;
		m02 = -sinYaw;
		m10 = +cosRoll*sinYaw*sinPitch - sinRoll*cosPitch;
		m11 = +cosRoll*cosPitch + sinRoll*sinYaw*sinPitch;
		m12 = +cosYaw*sinPitch;
		m20 = +cosRoll*sinYaw*cosPitch + sinRoll*sinPitch;
		m21 = +sinRoll*sinYaw*cosPitch - cosRoll*sinPitch;
		m22 = +cosYaw*cosPitch;
		*/
	}

	public javax.vecmath.Vector3d[] getRows() {
		return new javax.vecmath.Vector3d[] { getRow( 0 ), getRow( 1 ), getRow( 2 ) };
	}
	public void setRows( javax.vecmath.Vector3d row0, javax.vecmath.Vector3d row1, javax.vecmath.Vector3d row2 ) {
		m00 = row0.x;
		m01 = row0.y;
		m02 = row0.z;
		m10 = row1.x;
		m11 = row1.y;
		m12 = row1.z;
		m20 = row2.x;
		m21 = row2.y;
		m22 = row2.z;
	}
	public void setForwardUpGuide( javax.vecmath.Vector3d forward, javax.vecmath.Vector3d upGuide ) {
		Vector3 row2 = Vector3.normalizeV( forward );
		Vector3 normalizedUpGuide;
		if( upGuide != null ) {
			normalizedUpGuide = Vector3.normalizeV( upGuide );
		} else {
			normalizedUpGuide = new Vector3( 0,1,0 );
		}
		Vector3 row0 = Vector3.crossProduct( normalizedUpGuide, row2 );
		Vector3 row1 = Vector3.crossProduct( row2, row0 );
		setRows( row0, row1, row2 );
	}

	public void rotateX( double angle ) {
		double cosAngle = Math.cos( angle );
		double sinAngle = Math.sin( angle );
		for( int i=0; i<3; i++ ) {
			double tmp = getItem( i, 1 );
			setItem( i, 1, tmp*cosAngle - getItem( i, 2 )*sinAngle );
			setItem( i, 2, tmp*sinAngle + getItem( i, 2 )*cosAngle );
		}
	}
	public void rotateY( double angle ) {
		double cosAngle = Math.cos( angle );
		double sinAngle = Math.sin( angle );
		for( int i=0; i<3; i++ ) {
			double tmp = getItem( i, 0 );
			setItem( i, 0, tmp*cosAngle + getItem( i, 2 )*sinAngle );
			setItem( i, 2, -tmp*sinAngle + getItem( i, 2 )*cosAngle );
		}
	}
	public void rotateZ( double angle ) {
		double cosAngle = Math.cos( angle );
		double sinAngle = Math.sin( angle );
		for( int i=0; i<3; i++ ) {
			double tmp = getItem( i, 0 );
			setItem( i, 0, tmp*cosAngle - getItem( i, 1 )*sinAngle );
			setItem( i, 1, tmp*sinAngle + getItem( i, 1 )*cosAngle );
		}
	}

	public Vector3 getScaledSpace() {
		Vector3 row0 = getRow( 0 );
		Vector3 row1 = getRow( 1 );
		Vector3 row2 = getRow( 2 );
		Vector3 scale = new Vector3();
		Shear shear = new Shear();

		scale.x = row0.getLength();
		row0.normalize();
		shear.xy = Vector3.dotProduct( row0, row1 );
		row1 = Vector3.combine( row1, row0, 1, -shear.xy );

		scale.y = row1.getLength();
		row1.normalize();
		shear.xy /= scale.y;
		shear.xz = Vector3.dotProduct( row0, row2 );
		row2 = Vector3.combine( row2, row0, 1, -shear.xz );

		shear.yz = Vector3.dotProduct( row1, row2 );
		row2 = Vector3.combine( row2, row1, 1, -shear.yz );

		scale.z = row2.getLength();
		row2.normalize();
		shear.xz /= scale.z;
		shear.yz /= scale.z;

		double determinate = Vector3.dotProduct( row0, Vector3.crossProduct( row1, row2 ) );;
		if( determinate < 0 ) {
			row0.negate();
			row1.negate();
			row2.negate();
			scale.multiply( -1 );
		}
		return scale;
	}

	//public Vector3 getScale() {
	//	double x = Vector3.getLength( m00, m01, m02 );
	//	double y = Vector3.getLength( m10, m11, m12 );
	//	double z = Vector3.getLength( m20, m21, m22 );
	//	return new Vector3( x, y, z );
	//}
	public static edu.cmu.cs.stage3.math.Matrix33 multiply( edu.cmu.cs.stage3.math.Matrix33 a, edu.cmu.cs.stage3.math.Matrix33 b ) {
		edu.cmu.cs.stage3.math.Matrix33 m = new edu.cmu.cs.stage3.math.Matrix33();
		m.m00 = a.m00 * b.m00 + a.m01 * b.m10 + a.m02 * b.m20;
		m.m01 = a.m00 * b.m01 + a.m01 * b.m11 + a.m02 * b.m21;
		m.m02 = a.m00 * b.m02 + a.m01 * b.m12 + a.m02 * b.m22;

		m.m10 = a.m10 * b.m00 + a.m11 * b.m10 + a.m12 * b.m20;
		m.m11 = a.m10 * b.m01 + a.m11 * b.m11 + a.m12 * b.m21;
		m.m12 = a.m10 * b.m02 + a.m11 * b.m12 + a.m12 * b.m22;

		m.m20 = a.m20 * b.m00 + a.m21 * b.m10 + a.m22 * b.m20;
		m.m21 = a.m20 * b.m01 + a.m21 * b.m11 + a.m22 * b.m21;
		m.m22 = a.m20 * b.m02 + a.m21 * b.m12 + a.m22 * b.m22;
		return m;
	}

	public static Matrix33 interpolate( Matrix33 a, Matrix33 b, double portion ) {
		Quaternion q = Quaternion.interpolate( a.getQuaternion(), b.getQuaternion(), portion );
		return new Matrix33( q );
	}
	public Interpolable interpolate( Interpolable b, double portion ) {
		return interpolate( this, (Matrix33)b, portion );
	}
	
	public String toString() {
		return "edu.cmu.cs.stage3.math.Matrix33[rc00="+m00+",rc01="+m01+",rc02="+m02+",rc10="+m10+",rc11="+m11+",rc12="+m12+",rc20="+m20+",rc21="+m21+",rc22="+m22+"]";
	}
	public static Matrix33 valueOf( String s ) {
		String[] markers = { "edu.cmu.cs.stage3.math.Matrix33[rc00=", ",rc01=", ",rc02=", ",rc10=", ",rc11=", ",rc12=", ",rc20=", ",rc21=", ",rc22=", "]" };
		double[] values = new double[markers.length-1];
		for( int i=0; i<values.length; i++ ) {
			int begin = s.indexOf( markers[i] ) + markers[i].length();
			int end = s.indexOf( markers[i+1] );
			values[i] = Double.valueOf( s.substring( begin, end ) ).doubleValue();
		}
		return new Matrix33( values );
	}
}
