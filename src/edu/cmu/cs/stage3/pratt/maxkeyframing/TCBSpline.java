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
public class TCBSpline extends Spline {
	protected TCBKey[] keys;
	protected edu.cmu.cs.stage3.math.HermiteCubic[][] curves;
	protected java.util.HashMap curveMap = new java.util.HashMap();
	protected int numComponents;

	public boolean addKey( TCBKey key ) {
		boolean result = super.addKey( key );
		updateKeys();
		return result;
	}

	public boolean removeKey( TCBKey key ) {
		boolean result = super.removeKey( key );
		updateKeys();
		return result;
	}

	public void updateKeys() {
		this.keys = (TCBKey[])getKeyArray( new TCBKey[0] );
		this.curveMap.clear();

		if( this.keys != null ) {
			this.numComponents = keys[0].getValueComponents().length;
			this.curves = new edu.cmu.cs.stage3.math.HermiteCubic[keys.length - 1][numComponents];

			for( int i = 0; i < curves.length; i++ ) {
				TCBKey keyLast = this.keys[Math.max( i - 1, 0 )];
				TCBKey keyThis = this.keys[i];
				TCBKey keyNext = this.keys[i + 1];
				TCBKey keyNextNext = this.keys[Math.min( i + 2, keys.length - 1 )];
				this.curveMap.put( keyThis, new Integer( i ) );
				for( int j = 0; j < numComponents; j++ ) {
					double pLast = keyLast.getValueComponents()[j];
					double pThis = keyThis.getValueComponents()[j];
					double pNext = keyNext.getValueComponents()[j];
					double pNextNext = keyNextNext.getValueComponents()[j];

					double dThis = getTangentAtKey( keyThis, pLast, pThis, pNext );
					double dNext = getTangentAtKey( keyNext, pThis, pNext, pNextNext );

					this.curves[i][j] = new edu.cmu.cs.stage3.math.HermiteCubic( pThis, pNext, dThis, dNext );
				}
			}
		} else {
			this.curves = null;
		}
	}

	private double getTangentAtKey( TCBKey key, double pLast, double pThis, double pNext ) {
		double t = key.getTension();
		double c = key.getContinuity();
		double b = key.getBias();

		double ds = (((1.0 - t)*(1.0 - c)*(1.0 + b))/2.0)*(pThis - pLast) + (((1.0 - t)*(1.0 + c)*(1.0 - b))/2.0)*(pNext - pThis);
		double dd = (((1.0 - t)*(1.0 + c)*(1.0 + b))/2.0)*(pThis - pLast) + (((1.0 - t)*(1.0 - c)*(1.0 - b))/2.0)*(pNext - pThis);
		return (ds + dd)/2.0;
	}

	public void correctForMAXRelativeKeys() {
		edu.cmu.cs.stage3.math.Matrix33 lastRot = null;
		TCBKey[] keys = (TCBKey[])getKeyArray( new TCBKey[0] );
		for( int i = 0; i < keys.length; i++ ) {
			//System.out.println( i );
			edu.cmu.cs.stage3.math.Quaternion thisQ = (edu.cmu.cs.stage3.math.Quaternion)keys[i].createSample( keys[i].getValueComponents() );
			if( i > 0 ) {
				edu.cmu.cs.stage3.math.Quaternion realQ = edu.cmu.cs.stage3.math.Matrix33.multiply( lastRot, thisQ.getMatrix33() ).getQuaternion();
				QuaternionTCBKey realKey = new QuaternionTCBKey( keys[i].getTime(), realQ, keys[i].getTension(), keys[i].getContinuity(), keys[i].getBias() );
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

				Object o = curveMap.get( boundingKeys[0] );
				if( o instanceof Integer ) {
					int i = ((Integer)o).intValue();

					double[] components = new double[numComponents];
					for( int j = 0; j < numComponents; j++ ) {
						components[j] = curves[i][j].evaluate( portion );
					}

					return boundingKeys[0].createSample( components );
				}
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
