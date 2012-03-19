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

public abstract class RotateAnimation extends DirectionAmountTransformAnimation {

	@Override
	protected Direction getDefaultDirection() {
		return Direction.LEFT;
	}
	public abstract class RuntimeRotateAnimation extends RuntimeDirectionAmountTransformAnimation {
		private javax.vecmath.Vector3d m_axis;
		private double m_amount;
		private double m_amountPrev;
		protected abstract javax.vecmath.Vector3d getAxis(edu.cmu.cs.stage3.alice.core.Direction direction);

		@Override
		public void prologue(double t) {
			super.prologue(t);
			Direction directionValue = RotateAnimation.this.direction.getDirectionValue();
			m_amount = RotateAnimation.this.amount.doubleValue();
			m_axis = getAxis(directionValue);
			if (m_axis == null) {
				StringBuffer sb = new StringBuffer("direction value must not be ");
				if (directionValue != null) {
					sb.append(directionValue.getRepr());
				} else {
					sb.append("null");
				}
				sb.append('.');
				throw new edu.cmu.cs.stage3.alice.core.SimulationPropertyException(sb.toString(), null, RotateAnimation.this.direction);
			}
			m_amountPrev = 0;
		}

		@Override
		public void update(double t) {
			super.update(t);
			double delta = m_amount * getPortion(t) - m_amountPrev;
			m_subject.rotateRightNow(m_axis, delta, m_asSeenBy);
			m_amountPrev += delta;
		}
	}
}
