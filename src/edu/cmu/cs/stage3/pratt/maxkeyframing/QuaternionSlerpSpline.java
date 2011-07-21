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
public class QuaternionSlerpSpline extends Spline {
	public boolean addKey( QuaternionKey key ) {
		return super.addKey( key );
	}

	public boolean removeKey( QuaternionKey key ) {
		return super.removeKey( key );
	}

	public void correctForMAXRelativeKeys() {
		edu.cmu.cs.stage3.math.Matrix33 lastRot = null;
		QuaternionKey[] keys = (QuaternionKey[])getKeyArray( new QuaternionKey[0] );
		for( int i = 0; i < keys.length; i++ ) {
			edu.cmu.cs.stage3.math.Quaternion thisQ = (edu.cmu.cs.stage3.math.Quaternion)keys[i].createSample( keys[i].getValueComponents() );
			if( i > 0 ) {
				edu.cmu.cs.stage3.math.Quaternion realQ = edu.cmu.cs.stage3.math.Matrix33.multiply( lastRot, thisQ.getMatrix33() ).getQuaternion();
				QuaternionKey realKey = new QuaternionKey( keys[i].getTime(), realQ );
				this.removeKey( keys[i] );
				this.addKey( realKey );
				lastRot = realQ.getMatrix33();
			} else {
				lastRot = thisQ.getMatrix33();
			}
		}
	}

	
	public Object getSample( double t ) {
		if( t <= 0.0 ) {
			Key key = getFirstKey();
			if( key != null ) {
				return key.createSample( key.getValueComponents() );
			}
		} else if( t >= getDuration() ) {
			Key key = getLastKey();
			if( key != null ) {
				return key.createSample( key.getValueComponents() );
			}
		} else {
			Key[] boundingKeys = getBoundingKeys( t );
			if( boundingKeys != null ) {
				double timeSpan = boundingKeys[1].getTime() - boundingKeys[0].getTime();
				double portion = (t - boundingKeys[0].getTime())/timeSpan;

				return edu.cmu.cs.stage3.math.Quaternion.interpolate( ((QuaternionKey)boundingKeys[0]).getQuaternion(), ((QuaternionKey)boundingKeys[1]).getQuaternion(), portion );
			}
		}
		return null;
	}

	// until Matrix33 actually has this...
//	public static edu.cmu.cs.stage3.math.Matrix33 multiply( edu.cmu.cs.stage3.math.Matrix33 a, edu.cmu.cs.stage3.math.Matrix33 b ) {
//		edu.cmu.cs.stage3.math.Matrix33 m = new edu.cmu.cs.stage3.math.Matrix33();
//		m.rc00 = a.rc00 * b.rc00 + a.rc01 * b.rc10 + a.rc02 * b.rc20;
//		m.rc01 = a.rc00 * b.rc01 + a.rc01 * b.rc11 + a.rc02 * b.rc21;
//		m.rc02 = a.rc00 * b.rc02 + a.rc01 * b.rc12 + a.rc02 * b.rc22;
//
//		m.rc10 = a.rc10 * b.rc00 + a.rc11 * b.rc10 + a.rc12 * b.rc20;
//		m.rc11 = a.rc10 * b.rc01 + a.rc11 * b.rc11 + a.rc12 * b.rc21;
//		m.rc12 = a.rc10 * b.rc02 + a.rc11 * b.rc12 + a.rc12 * b.rc22;
//
//		m.rc20 = a.rc20 * b.rc00 + a.rc21 * b.rc10 + a.rc22 * b.rc20;
//		m.rc21 = a.rc20 * b.rc01 + a.rc21 * b.rc11 + a.rc22 * b.rc21;
//		m.rc22 = a.rc20 * b.rc02 + a.rc21 * b.rc12 + a.rc22 * b.rc22;
//		return m;
//	}
}
