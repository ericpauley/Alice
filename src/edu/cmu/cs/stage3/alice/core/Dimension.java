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

package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.math.MathUtilities;

public class Dimension extends edu.cmu.cs.stage3.util.Enumerable {
	public static final Dimension ALL = new Dimension( new javax.vecmath.Vector3d( 1, 1, 1 ) );
	public static final Dimension LEFT_TO_RIGHT = new Dimension( MathUtilities.createXAxis() );
	public static final Dimension TOP_TO_BOTTOM = new Dimension( MathUtilities.createYAxis() );
	public static final Dimension FRONT_TO_BACK = new Dimension( MathUtilities.createZAxis() );
	private javax.vecmath.Vector3d m_scaleAxis;
	public Dimension( javax.vecmath.Vector3d scaleAxis ) {
		m_scaleAxis = scaleAxis;
	}
	public javax.vecmath.Vector3d getScaleAxis() {
		return m_scaleAxis;
	}
	
	public boolean equals( Object o ) {
		if( o==this ) return true;
		if( o!=null && o instanceof Direction ) {
			Dimension dimension = (Dimension)o;
			if( m_scaleAxis == null ) {
				return dimension.m_scaleAxis==null;
			} else {
				return m_scaleAxis.equals( dimension.m_scaleAxis );
			}
		} else {
			return false;
		}
	}
	public static Dimension valueOf( String s ) {
		return (Dimension)edu.cmu.cs.stage3.util.Enumerable.valueOf( s, Dimension.class );
	}
}
