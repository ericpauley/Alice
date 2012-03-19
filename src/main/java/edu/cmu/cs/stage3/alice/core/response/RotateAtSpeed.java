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

public abstract class RotateAtSpeed extends DirectionSpeedTransformResponse {

	@Override
	protected Direction getDefaultDirection() {
		return Direction.LEFT;
	}
	public abstract class RuntimeRotateAtSpeed extends RuntimeDirectionSpeedTransformResponse {
		private javax.vecmath.Vector3d m_axis;
		protected abstract javax.vecmath.Vector3d getAxis(edu.cmu.cs.stage3.alice.core.Direction direction);

		@Override
		public void prologue(double t) {
			super.prologue(t);
			Direction directionValue = RotateAtSpeed.this.direction.getDirectionValue();
			m_axis = getAxis(directionValue);
			if (m_axis == null) {
				StringBuffer sb = new StringBuffer("direction value must not be ");
				if (directionValue != null) {
					sb.append(directionValue.getRepr());
				} else {
					sb.append("null");
				}
				sb.append('.');
				throw new edu.cmu.cs.stage3.alice.core.SimulationPropertyException(sb.toString(), null, RotateAtSpeed.this.direction);
			}
		}

		@Override
		public void update(double t) {
			super.update(t);
			m_subject.rotateRightNow(m_axis, getSpeed() * getDT(), m_asSeenBy);
		}
	}
}
