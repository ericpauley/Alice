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

public class Direction extends edu.cmu.cs.stage3.util.Enumerable {
	public static final Direction LEFT = new Direction(MathUtilities.createNegativeXAxis(), MathUtilities.createNegativeYAxis(), MathUtilities.createZAxis());
	public static final Direction RIGHT = new Direction(MathUtilities.createXAxis(), MathUtilities.createYAxis(), MathUtilities.createNegativeZAxis());
	public static final Direction UP = new Direction(MathUtilities.createYAxis(), null, null);
	public static final Direction DOWN = new Direction(MathUtilities.createNegativeYAxis(), null, null);
	public static final Direction FORWARD = new Direction(MathUtilities.createZAxis(), MathUtilities.createXAxis(), null);
	public static final Direction BACKWARD = new Direction(MathUtilities.createNegativeZAxis(), MathUtilities.createNegativeXAxis(), null);
	private javax.vecmath.Vector3d m_moveAxis;
	private javax.vecmath.Vector3d m_turnAxis;
	private javax.vecmath.Vector3d m_rollAxis;
	public Direction(javax.vecmath.Vector3d moveAxis, javax.vecmath.Vector3d turnAxis, javax.vecmath.Vector3d rollAxis) {
		m_moveAxis = moveAxis;
		m_turnAxis = turnAxis;
		m_rollAxis = rollAxis;
	}
	public javax.vecmath.Vector3d getMoveAxis() {
		return m_moveAxis;
	}
	public javax.vecmath.Vector3d getTurnAxis() {
		return m_turnAxis;
	}
	public javax.vecmath.Vector3d getRollAxis() {
		return m_rollAxis;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o != null && o instanceof Direction) {
			Direction direction = (Direction) o;
			if (m_moveAxis != null) {
				if (!m_moveAxis.equals(direction.m_moveAxis)) {
					return false;
				}
			} else {
				if (direction.m_moveAxis != null) {
					return false;
				}
			}
			if (m_turnAxis != null) {
				if (!m_turnAxis.equals(direction.m_turnAxis)) {
					return false;
				}
			} else {
				if (direction.m_turnAxis != null) {
					return false;
				}
			}
			if (m_rollAxis != null) {
				if (!m_rollAxis.equals(direction.m_rollAxis)) {
					return false;
				}
			} else {
				if (direction.m_rollAxis != null) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}
	public static Direction valueOf(String s) {
		return (Direction) edu.cmu.cs.stage3.util.Enumerable.valueOf(s, Direction.class);
	}
}
