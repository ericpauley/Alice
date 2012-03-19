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

import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.ReferenceFrameProperty;

public abstract class AbstractMoveInDirectionOfAnimation extends TransformAnimation {
	public final ReferenceFrameProperty target = new ReferenceFrameProperty(this, "target", null);
	public final NumberProperty amount = new NumberProperty(this, "amount", new Double(1));

	public abstract class RuntimeAbstractMoveInDirectionOfAnimationAnimation extends RuntimeTransformAnimation {
		private edu.cmu.cs.stage3.alice.core.ReferenceFrame m_target;
		private javax.vecmath.Vector3d m_vector;
		private javax.vecmath.Vector3d m_vectorPrev;
		protected abstract double getActualAmountValue();
		protected javax.vecmath.Vector3d getVector() {
			double amountValue = getActualAmountValue();
			if (!Double.isNaN(amountValue)) {
				javax.vecmath.Vector3d v = m_target.getPosition(m_subject);
				double length = edu.cmu.cs.stage3.math.MathUtilities.getLength(v);
				if (length > 0) {
					v.scale(amountValue / length);
				} else {
					v.set(0, 0, amountValue);
				}
				return v;
			} else {
				return new javax.vecmath.Vector3d();
			}
		}

		@Override
		public void prologue(double t) {
			super.prologue(t);
			m_target = target.getReferenceFrameValue();
			if (m_target == null) {
				throw new edu.cmu.cs.stage3.alice.core.SimulationPropertyException("target must not be null.", getCurrentStack(), target);
			}

			m_vectorPrev = new javax.vecmath.Vector3d();
			m_vector = getVector();
		}

		@Override
		public void update(double t) {
			super.update(t);
			javax.vecmath.Vector3d delta = edu.cmu.cs.stage3.math.MathUtilities.subtract(edu.cmu.cs.stage3.math.MathUtilities.multiply(m_vector, getPortion(t)), m_vectorPrev);
			m_subject.moveRightNow(delta, m_asSeenBy);
			m_vectorPrev.add(delta);
		}

		// public void epilogue( double t ) {
		// edu.cmu.cs.stage3.math.Vector3 delta =
		// edu.cmu.cs.stage3.math.Vector3.subtract( m_vector, m_vectorPrev );
		// m_subject.moveRightNow( delta, m_asSeenBy );
		// m_vectorPrev.add( delta );
		// }
	}
}
