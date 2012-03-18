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

public class EulerAngles implements Interpolable, Cloneable {
	public double pitch = 0;
	public double yaw = 0;
	public double roll = 0;
	public EulerAngles() {
	}
	public EulerAngles( double pitch, double yaw, double roll ) {
		this.pitch = pitch;
		this.yaw = yaw;
		this.roll = roll;
	}
	public EulerAngles( double[] a ) {
		this( a[0], a[1], a[2] );
	}
	public EulerAngles( Matrix33 m ) {
		setMatrix33( m );
	}
	public EulerAngles( AxisAngle aa ) {
		setAxisAngle( aa );
	}
	public EulerAngles( Quaternion q ) {
		setQuaternion( q );
	}
	
	public synchronized Object clone() {
		try {
			return super.clone();
		} catch( CloneNotSupportedException e ) {
			throw new InternalError();
		}
	}
	
	public boolean equals( Object o ) {
		if( o==this ) return true;
		if( o!=null && o instanceof EulerAngles ) {
			EulerAngles ea = (EulerAngles)o;
			return yaw==ea.yaw && pitch==ea.pitch && roll==ea.roll;
		} else {
			return false;
		}
	}
	public Matrix33 getMatrix33() {
		return new Matrix33( this );
	}
	public void setMatrix33( Matrix33 m ) {
		javax.vecmath.Vector3d row0 = MathUtilities.getRow( m, 0 );
		javax.vecmath.Vector3d row1 = MathUtilities.getRow( m, 1 );
		javax.vecmath.Vector3d row2 = MathUtilities.getRow( m, 2 );
		javax.vecmath.Vector3d scale = new javax.vecmath.Vector3d();
		Shear shear = new Shear();

		scale.x = row0.length();
		row0.normalize();
		shear.xy = MathUtilities.dotProduct( row0, row1 );
		row1 = MathUtilities.combine( row1, row0, 1, -shear.xy );

		scale.y = row1.length();
		row1.normalize();
		shear.xy /= scale.y;
		shear.xz = MathUtilities.dotProduct( row0, row2 );
		row2 = MathUtilities.combine( row2, row0, 1, -shear.xz );

		shear.yz = MathUtilities.dotProduct( row1, row2 );
		row2 = MathUtilities.combine( row2, row1, 1, -shear.yz );

		scale.z = row2.length();
		row2.normalize();
		shear.xz /= scale.z;
		shear.yz /= scale.z;

		double determinate = MathUtilities.dotProduct( row0, MathUtilities.crossProduct( row1, row2 ) );;
		if( determinate < 0 ) {
			row0.negate();
			row1.negate();
			row2.negate();
			scale.scale( -1 );
		}
		yaw = Math.asin( -row0.z );
		if( Math.cos( yaw ) != 0 ) {
			pitch = Math.atan2( row1.z, row2.z );
			roll = Math.atan2( row0.y, row0.x );
		} else {
			pitch = Math.atan2( row1.x, row1.y );
			roll = 0;
		}
	}
	public AxisAngle getAxisAngle() {
		return new AxisAngle( this );
	}
	public void setAxisAngle( AxisAngle aa ) {
		//todo: optimize?
		setMatrix33( aa.getMatrix33() );
	}
	public Quaternion getQuaternion() {
		return new Quaternion( this );
	}
	public void setQuaternion( Quaternion q ) {
		//todo: optimize?
		setMatrix33( q.getMatrix33() );
	}
	public static EulerAngles interpolate( EulerAngles a, EulerAngles b, double portion ) {
		Quaternion q = Quaternion.interpolate( a.getQuaternion(), b.getQuaternion(), portion );
		return new EulerAngles( q );
	}
	public Interpolable interpolate( Interpolable b, double portion ) {
		return interpolate( this, (EulerAngles)b, portion );
	}
	
	public String toString() {
		return "edu.cmu.cs.stage3.math.EulerAngles[pitch="+pitch+",yaw="+yaw+",roll="+roll+"]";
	}
	public static EulerAngles revolutionsToRadians( EulerAngles ea ) {
		return new EulerAngles( ea.pitch/Angle.RADIANS, ea.yaw/Angle.RADIANS, ea.roll/Angle.RADIANS );
	}
	public static EulerAngles radiansToRevolutions( EulerAngles ea ) {
		return new EulerAngles( ea.pitch*Angle.RADIANS, ea.yaw*Angle.RADIANS, ea.roll*Angle.RADIANS );
	}

	public static EulerAngles valueOf( String s ) {
		String[] markers = { "edu.cmu.cs.stage3.math.EulerAngles[pitch=", ",yaw=", ",roll=", "]" };
		double[] values = new double[markers.length-1];
		for( int i=0; i<values.length; i++ ) {
			int begin = s.indexOf( markers[i] ) + markers[i].length();
			int end = s.indexOf( markers[i+1] );
			values[i] = Double.valueOf( s.substring( begin, end ) ).doubleValue();
		}
		return new EulerAngles( values );
	}
}
