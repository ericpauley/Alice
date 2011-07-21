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

public class Sphere implements Cloneable {
	protected double m_radius;
	protected javax.vecmath.Vector3d m_center;
	public Sphere() {
		this( null, Double.NaN );
	}
	public Sphere( javax.vecmath.Vector3d center, double radius ) {
		setCenter( center );
		setRadius( radius );
	}
	public Sphere( double x, double y, double z, double radius ) {
		setCenter( new javax.vecmath.Vector3d( x, y, z ) );
		setRadius( radius );
	}
	
	public synchronized Object clone() {
		try {
			Sphere sphere = (Sphere)super.clone();
			sphere.setCenter( m_center );
			return sphere;
		} catch( CloneNotSupportedException e ) {
			throw new InternalError();
		}
	}
	
	public boolean equals( Object o ) {
		if( o==this ) return true;
		if( o!=null && o instanceof Sphere ) {
			Sphere s = (Sphere)o;

			//todo handle null
			return m_center.equals( s.m_center ) && m_radius==s.m_radius;
		} else {
			return false;
		}
	}
	public double getRadius() {
		return m_radius;
	}
	public void setRadius( double radius ) {
		m_radius = radius;
	}
	public javax.vecmath.Vector3d getCenter() {
		if( m_center!=null ) {
			return new javax.vecmath.Vector3d( m_center );
		} else {
			return null;
		}
	}
	public void setCenter( javax.vecmath.Vector3d center ) {
        if( center != null ) {
    		m_center = new javax.vecmath.Vector3d( center );
        } else {
            m_center = null;
        }
	}

	public void union( Sphere s ) {
		if( s!=null && s.m_center!=null ) {
			if( m_center!=null ) {
				javax.vecmath.Vector3d diagonal = new javax.vecmath.Vector3d( m_center );
				diagonal.sub( s.m_center );
				diagonal.normalize();
				javax.vecmath.Vector3d[] points = new javax.vecmath.Vector3d[4];
				points[0] = MathUtilities.add( m_center, MathUtilities.multiply( diagonal, m_radius ) );
				points[1] = MathUtilities.subtract( m_center, MathUtilities.multiply( diagonal, m_radius ) );
				points[2] = MathUtilities.add( s.m_center, MathUtilities.multiply( diagonal, s.m_radius ) );
				points[3] = MathUtilities.subtract( s.m_center, MathUtilities.multiply( diagonal, s.m_radius ) );
				double maxDistanceSquared = 0;
				int maxDistanceI = 0;
				int maxDistanceJ = 1;
				for( int i=0; i<4; i++ ) {
					for( int j=i+1; j<4; j++ ) {
						double d2 = MathUtilities.subtract( points[i], points[j] ).lengthSquared();
						if( d2>maxDistanceSquared ) {
							maxDistanceSquared = d2;
							maxDistanceI = i;
							maxDistanceJ = j;
						}
					}
				}
				m_center = MathUtilities.divide( MathUtilities.add( points[maxDistanceI], points[maxDistanceJ] ), 2 );
				m_radius = Math.sqrt( maxDistanceSquared )/2.0;
			} else {
				m_center = s.getCenter();
				m_radius = s.getRadius();
			}
		}
	}
	public void transform( javax.vecmath.Matrix4d m ) {
		if( m_center!=null && !Double.isNaN( m_radius ) ) {
			//todo... account for scale
			m_center.add( new Vector3( m.m30, m.m31, m.m32 ) );
		}
	}

	public void scale( javax.vecmath.Matrix3d s ) {
		if( s!=null ) {
			if( m_center!=null ) {
				m_center = MathUtilities.multiply( s, m_center );
			}
			//Vector3 v = s.getScaledSpace();
			//double scale = Math.max( Math.max( v.x, v.y ), v.z );
			//m_radius *= scale;
			m_radius *= s.getScale();
		}
	}

	
	public String toString() {
		String s = "edu.cmu.cs.stage3.math.Sphere[radius="+m_radius+",center=";
		if( m_center!=null ) {
			s += m_center + "]";
		} else {
			s += "null]";
		}
		return s;
	}
}
