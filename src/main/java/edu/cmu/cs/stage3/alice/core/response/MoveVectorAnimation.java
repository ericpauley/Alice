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

import edu.cmu.cs.stage3.alice.core.property.Vector3Property;

public class MoveVectorAnimation extends TransformAnimation {
	public final Vector3Property vector = new Vector3Property(this, "vector", null);
	public class RuntimeMoveVectorAnimation extends RuntimeTransformAnimation {
		private javax.vecmath.Vector3d m_vector;
		private javax.vecmath.Vector3d m_vectorPrev;

		@Override
		public void prologue(double t) {
			super.prologue(t);
			m_vectorPrev = new javax.vecmath.Vector3d();
			m_vector = vector.getVector3dValue();
			if (m_vector == null) {
				throw new edu.cmu.cs.stage3.alice.core.SimulationPropertyException("vector value must not be null.", null, vector);
			}
		}

		@Override
		public void update(double t) {
			super.update(t);
			javax.vecmath.Vector3d delta = new javax.vecmath.Vector3d();
			delta.scale(getPortion(t), m_vector);
			delta.sub(m_vectorPrev);
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
