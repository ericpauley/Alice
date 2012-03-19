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

import edu.cmu.cs.stage3.alice.core.property.ReferenceFrameProperty;
import edu.cmu.cs.stage3.alice.core.property.Vector3Property;

public class AbstractPointAtConstraint extends TransformResponse {
	public final ReferenceFrameProperty target = new ReferenceFrameProperty(this, "target", null);
	public final Vector3Property offset = new Vector3Property(this, "offset", null);
	public final Vector3Property upGuide = new Vector3Property(this, "upGuide", null);

	public abstract class RuntimeAbstractPointAtConstraint extends RuntimeTransformResponse {
		private javax.vecmath.Vector3d m_upGuide;
		private javax.vecmath.Vector3d m_offset;
		private edu.cmu.cs.stage3.alice.core.ReferenceFrame m_target;
		private boolean m_onlyAffectYaw;
		protected abstract boolean onlyAffectYaw();

		@Override
		public void prologue(double t) {
			super.prologue(t);
			m_target = target.getReferenceFrameValue();
			m_offset = offset.getVector3Value();
			m_upGuide = upGuide.getVector3Value();
			m_onlyAffectYaw = onlyAffectYaw();
			if (m_target == null) {
				throw new edu.cmu.cs.stage3.alice.core.SimulationPropertyException("target value must not be null.", null, target);
			}
			if (m_target == m_subject) {
				throw new edu.cmu.cs.stage3.alice.core.SimulationPropertyException("target value must not be equal to the subject value.", getCurrentStack(), target);
			}
		}
		// todo: rework this hack added from TurnAwayFromConstraint
		protected boolean isTurnAroundNecessary() {
			return false;
		}

		@Override
		public void update(double t) {
			super.update(t);
			m_subject.pointAtRightNow(m_target, m_offset, m_upGuide, m_asSeenBy, m_onlyAffectYaw);
			if (isTurnAroundNecessary()) {
				m_subject.rotateRightNow(edu.cmu.cs.stage3.math.MathUtilities.getYAxis(), 0.5);
			}
		}
	}
}
