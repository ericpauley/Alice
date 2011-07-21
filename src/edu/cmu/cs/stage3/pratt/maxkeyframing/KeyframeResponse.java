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

public abstract class KeyframeResponse extends edu.cmu.cs.stage3.alice.core.response.TransformResponse {
	public final edu.cmu.cs.stage3.alice.core.property.ObjectProperty spline = new edu.cmu.cs.stage3.alice.core.property.ObjectProperty( this, "spline", null, edu.cmu.cs.stage3.pratt.maxkeyframing.Spline.class );

	public KeyframeResponse() {
//		spline.setIsAcceptingOfNull( true );
	}

	public abstract class RuntimeKeyframeResponse extends Hack_RuntimeTransformResponse {
		protected edu.cmu.cs.stage3.pratt.maxkeyframing.Spline runtimeSpline = null;
		protected double splineDuration = 0.0;
		protected double timeFactor = 1.0;

		
		public void prologue( double t ) {
			super.prologue( t );
			runtimeSpline = (edu.cmu.cs.stage3.pratt.maxkeyframing.Spline)spline.getValue();
			if( runtimeSpline != null ) {
				splineDuration = runtimeSpline.getDuration();
			} else {
				splineDuration = 0.0;
			}
			timeFactor = 1.0;
			if( ! Double.isNaN( getDuration() ) ) {
				timeFactor = splineDuration/getDuration();
			} else {
				setDuration( splineDuration );
			}
		}

		
		public void epilogue( double t ) {
			super.epilogue( t );
			runtimeSpline = null;
		}
	}

	abstract class Hack_RuntimeTransformResponse extends RuntimeResponse {
		protected edu.cmu.cs.stage3.alice.core.Transformable m_transformable;
		protected edu.cmu.cs.stage3.alice.core.ReferenceFrame m_asSeenBy;
		
		public void prologue( double t ) {
			super.prologue( t );
			m_transformable = (edu.cmu.cs.stage3.alice.core.Transformable)subject.getValue();
			m_asSeenBy = (edu.cmu.cs.stage3.alice.core.ReferenceFrame)asSeenBy.getValue();
		}
	}
}
