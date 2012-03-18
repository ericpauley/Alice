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

public class QuaternionKeyframeResponse extends edu.cmu.cs.stage3.pratt.maxkeyframing.KeyframeResponse {
	public class RuntimeQuaternionKeyframeResponse extends RuntimeKeyframeResponse {
		
		public void update( double t ) {
			double timeElapsed = getTimeElapsed( t );
			double splineTimeElapsed = timeElapsed*timeFactor;

			try {
				edu.cmu.cs.stage3.math.Quaternion q = (edu.cmu.cs.stage3.math.Quaternion)runtimeSpline.getSample( splineTimeElapsed );
				if( q != null ) {
					m_transformable.setOrientationRightNow( q );
				}
			} catch( ClassCastException e ) {
				System.err.println( "Incorrect sample type from spline " + runtimeSpline + ".  Quaternion expected.  Found " + runtimeSpline.getSample( splineTimeElapsed ) );
			}
		}
	}
}
