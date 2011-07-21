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

import edu.cmu.cs.stage3.alice.core.Direction;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;

public class MoveAnimation extends DirectionAmountTransformAnimation {
	public final BooleanProperty isScaledBySize = new BooleanProperty( this, "isScaledBySize", Boolean.FALSE );
	
	protected Direction getDefaultDirection() {
		return Direction.FORWARD;
	}
	
	protected boolean acceptsDirection( edu.cmu.cs.stage3.alice.core.Direction direction ) {
		return direction.getMoveAxis()!=null;
	}
	public class RuntimeMoveAnimation extends RuntimeDirectionAmountTransformAnimation {
		private javax.vecmath.Vector3d m_vector;
		private javax.vecmath.Vector3d m_vectorPrev;
		protected javax.vecmath.Vector3d getVector() {
			Direction directionValue = MoveAnimation.this.direction.getDirectionValue();
			double amountValue = MoveAnimation.this.amount.doubleValue();
			if( directionValue!=null && !Double.isNaN( amountValue ) ) {
				javax.vecmath.Vector3d v = edu.cmu.cs.stage3.math.MathUtilities.multiply( directionValue.getMoveAxis(), amountValue );
				if( MoveAnimation.this.isScaledBySize.booleanValue() ) {
					javax.vecmath.Vector3d subjectSize = m_subject.getSize();
					v.x *= subjectSize.x;
					v.y *= subjectSize.y;
					v.z *= subjectSize.z;
				}
				return v;
			} else {
				return new javax.vecmath.Vector3d();
			}
		}
		
		public void prologue( double t ) {
			super.prologue( t );
			m_vectorPrev = new javax.vecmath.Vector3d();
			m_vector = getVector();
		}
		
		public void update( double t ) {
			super.update( t );
			javax.vecmath.Vector3d delta = edu.cmu.cs.stage3.math.MathUtilities.subtract( edu.cmu.cs.stage3.math.MathUtilities.multiply( m_vector, getPortion( t ) ), m_vectorPrev );
			m_subject.moveRightNow( delta, m_asSeenBy );
			m_vectorPrev.add( delta );
		}

		//public void epilogue( double t ) {
		//	edu.cmu.cs.stage3.math.Vector3 delta = edu.cmu.cs.stage3.math.Vector3.subtract( m_vector, m_vectorPrev );
		//	m_subject.moveRightNow( delta, m_asSeenBy );
		//	m_vectorPrev.add( delta );
		//}
	}
}
