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

import edu.cmu.cs.stage3.math.MathUtilities;

import edu.cmu.cs.stage3.math.Matrix33;
import edu.cmu.cs.stage3.math.Matrix44;
import edu.cmu.cs.stage3.math.Quaternion;
import edu.cmu.cs.stage3.math.Vector3;
import edu.cmu.cs.stage3.math.Vector4;

/**
 * @author Dennis Cosgrove
 */
public abstract class ReferenceFrame extends Container {
	private static Transformable s_getTransformationHelperOffset = new Transformable();
	static {
		s_getTransformationHelperOffset.setName( "s_getTransformationHelperOffset" );
		s_getTransformationHelperOffset.setIsHelper( true );
	}
	public Matrix44 getTransformation( javax.vecmath.Vector3d offset, ReferenceFrame asSeenBy ) {
		synchronized( s_getTransformationHelperOffset ) {
			ReferenceFrame actual;
			if( offset != null ) {
				s_getTransformationHelperOffset.setParent( this );
				Matrix44 m = new Matrix44();
				m.m30 = offset.x;
				m.m31 = offset.y;
				m.m32 = offset.z;
				s_getTransformationHelperOffset.setLocalTransformation( m );
				actual = s_getTransformationHelperOffset;
			} else {
				actual = this;
			}
			Matrix44 m;
			if( asSeenBy != null ) {
				m = Matrix44.multiply( actual.getAbsoluteTransformation(), asSeenBy.getInverseAbsoluteTransformation() );
			} else {
				m = new Matrix44( actual.getAbsoluteTransformation() );
			}
			if( offset != null ) {
				s_getTransformationHelperOffset.setParent( null );
			}
			return m;
		}
	}
	public Matrix44 getTransformation( ReferenceFrame asSeenBy ) {
		return getTransformation( null, asSeenBy );
	}
	public Vector3 getPosition( javax.vecmath.Vector3d offset, ReferenceFrame asSeenBy ) {
		return getTransformation( asSeenBy ).getPosition();
	}
	public Vector3 getPosition( ReferenceFrame asSeenBy ) {
		return getPosition( null, asSeenBy );
	}
	public Matrix33 getAxes( ReferenceFrame asSeenBy ) {
		return getTransformation( asSeenBy ).getAxes();
	}
	public Vector3[] getOrientation( ReferenceFrame asSeenBy ) {
		Matrix33 axes = getAxes( asSeenBy );
		Vector3[] array = { axes.getRow( 2 ), axes.getRow( 1 ) };
		return array;
	}
	public Quaternion getQuaternion( ReferenceFrame asSeenBy ) {
		return getAxes( asSeenBy ).getQuaternion();
	}

	public javax.vecmath.Vector4d transformTo( javax.vecmath.Vector4d xyzw, ReferenceFrame to ) {
		return MathUtilities.multiply( xyzw, getTransformation( to ) );
	}
	public javax.vecmath.Vector3d transformTo( javax.vecmath.Vector3d xyz, ReferenceFrame to ) {
		return new Vector3( transformTo( new Vector4( xyz, 1 ), to ) );
	}
}
