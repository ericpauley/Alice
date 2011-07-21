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

package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Dimension;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.ObjectProperty;
import edu.cmu.cs.stage3.alice.core.property.DimensionProperty;
import edu.cmu.cs.stage3.alice.core.Transformable;

public class ResizeAnimation extends TransformAnimation {
	public final DimensionProperty dimension = new DimensionProperty( this, "dimension", Dimension.ALL );
	public final NumberProperty amount = new NumberProperty( this, "amount", new Double( 2 ) );
	public final BooleanProperty likeRubber = new BooleanProperty( this, "likeRubber", Boolean.FALSE );
	public final ObjectProperty howMuch = new ObjectProperty( this, "howMuch", edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_PARTS, edu.cmu.cs.stage3.util.HowMuch.class );
	private static final javax.vecmath.Vector3d VECTOR_111 = new javax.vecmath.Vector3d( 1, 1, 1 );
	public class RuntimeResizeAnimation extends RuntimeTransformAnimation {
		private javax.vecmath.Vector3d m_vector;
		private javax.vecmath.Vector3d m_vectorPrev;
		private edu.cmu.cs.stage3.util.HowMuch m_howMuch;
		
		public void prologue( double t ) {
			super.prologue( t );
			m_vectorPrev = new javax.vecmath.Vector3d( 1, 1, 1 );
			Dimension dimensionValue = ResizeAnimation.this.dimension.getDimensionValue();
			double amountValue = ResizeAnimation.this.amount.doubleValue();
			m_vector = Transformable.calculateResizeScale( dimensionValue, amountValue, likeRubber.booleanValue() );
            m_howMuch = (edu.cmu.cs.stage3.util.HowMuch)ResizeAnimation.this.howMuch.getValue();
		}
		
		public void update( double t ) {
			super.update( t );
			javax.vecmath.Vector3d vectorCurrent = edu.cmu.cs.stage3.math.MathUtilities.interpolate( VECTOR_111, m_vector, getPortion( t ) );
			m_subject.resizeRightNow( edu.cmu.cs.stage3.math.MathUtilities.divide( vectorCurrent, m_vectorPrev ), m_asSeenBy, m_howMuch );
			m_vectorPrev = vectorCurrent;
		}
	}
}
