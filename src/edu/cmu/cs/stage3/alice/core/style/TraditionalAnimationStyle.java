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

package edu.cmu.cs.stage3.alice.core.style;

public class TraditionalAnimationStyle extends edu.cmu.cs.stage3.util.Enumerable implements edu.cmu.cs.stage3.alice.core.Style {
	/** @deprecated */
	public static final TraditionalAnimationStyle LINEAR = new TraditionalAnimationStyle( false, false, false, false );
	/** @deprecated */
	public static final TraditionalAnimationStyle SLOW_IN = new TraditionalAnimationStyle( true, false, false, false );
	/** @deprecated */
	public static final TraditionalAnimationStyle SLOW_OUT = new TraditionalAnimationStyle( false, true, false, false );
	/** @deprecated */
	public static final TraditionalAnimationStyle SLOW_IN_OUT = new TraditionalAnimationStyle( true, true, false, false );

	public static final TraditionalAnimationStyle BEGIN_AND_END_GENTLY = new TraditionalAnimationStyle( true, true, false, false );
	public static final TraditionalAnimationStyle BEGIN_GENTLY_AND_END_ABRUPTLY = new TraditionalAnimationStyle( true, false, false, false );
	public static final TraditionalAnimationStyle BEGIN_ABRUPTLY_AND_END_GENTLY = new TraditionalAnimationStyle( false, true, false, false );
	public static final TraditionalAnimationStyle BEGIN_AND_END_ABRUPTLY = new TraditionalAnimationStyle( false, false, false, false );

	private boolean m_beginGently;
	private boolean m_endGently;
	private boolean m_withAnticipation;
	private boolean m_withOvershoot;
	//todo
	public TraditionalAnimationStyle( boolean beginGently, boolean endGently, boolean withAnticipation, boolean withOvershoot ) {
		m_beginGently = beginGently;
		m_endGently = endGently;
		m_withAnticipation = withAnticipation;
		m_withOvershoot = withOvershoot;
	}

	//todo
	private static double gently (double x, double A, double B) {
		double y, a3, b3, c3, m, b2;
		if (x < A) {
			y = ((B - 1)/(A *  (B * B - A * B + A - 1))) * x * x;
		} else if (x > B) {
			a3 = 1 / (B * B - A * B +  A - 1);
			b3 = -2 * a3;
			c3 = 1 + a3;
			y  = a3 * x * x + b3 * x + c3;
		} else {
			m  = 2 * (B - 1) / (B * B - A * B + A - 1);
			b2 = -m * A / 2;
			y  = m * x + b2;
		}
		return y;
	}

	public double getPortion( double current, double total ) {
		//todo:
		if( total!=0 ) {
			double portion = current/total;
			if( m_beginGently ) {
				if( m_endGently ) {
					return gently( portion, 0.3, 0.8 );
				} else {
					return gently( portion, 0.99, 0.999 );
				}
			} else {
				if( m_endGently ) {
					return gently( portion, 0.001, 0.01 );
				} else {
					return portion;
				}
			}
		}
		else {
			return 1;
		}
	}
	public static TraditionalAnimationStyle valueOf( String s ) {
		// todo: remove this hack
		TraditionalAnimationStyle tas = (TraditionalAnimationStyle)edu.cmu.cs.stage3.util.Enumerable.valueOf( s, TraditionalAnimationStyle.class );
		if( tas==LINEAR ) {
			tas = BEGIN_AND_END_ABRUPTLY;
		} else if( tas==SLOW_IN ) {
			// slow in used to be mislabeled
			tas = BEGIN_GENTLY_AND_END_ABRUPTLY;
		} else if( tas==SLOW_OUT ) {
			// slow out used to be mislabeled
			tas = BEGIN_ABRUPTLY_AND_END_GENTLY;
		} else if( tas==SLOW_IN_OUT ) {
			tas = BEGIN_AND_END_GENTLY;
		}
		return tas;
	}
}