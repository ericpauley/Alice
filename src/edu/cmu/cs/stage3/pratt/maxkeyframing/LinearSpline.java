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
public class LinearSpline extends Spline {
	public boolean addKey( SimpleKey key ) {
		return super.addKey( key );
	}

	public boolean removeKey( SimpleKey key ) {
		return super.removeKey( key );
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

				double[] prevComponents = boundingKeys[0].getValueComponents();
				double[] nextComponents = boundingKeys[1].getValueComponents();

				double[] components = new double[prevComponents.length];
				for( int j = 0; j < prevComponents.length; j++ ) {
					components[j] = prevComponents[j] + (nextComponents[j] - prevComponents[j])*portion;
				}

				return boundingKeys[0].createSample( components );
			}
		}

		return null;
	}
}
