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

public class GetAGoodLookAtAnimation extends OrientationAnimation {
	public final ReferenceFrameProperty target = new ReferenceFrameProperty(this, "target", null);
	public class RuntimeGetAGoodLookAtAnimation extends RuntimeOrientationAnimation {
		private edu.cmu.cs.stage3.alice.core.ReferenceFrame m_target;
		private edu.cmu.cs.stage3.math.Matrix44 m_transformationBegin;
		private edu.cmu.cs.stage3.math.Matrix44 m_transformationEnd;

		@Override
		public void prologue(double t) {
			super.prologue(t);
			m_target = target.getReferenceFrameValue();

			if (m_target == null) {
				throw new edu.cmu.cs.stage3.alice.core.SimulationPropertyException("target value must not be null.", null, target);
			}
			if (m_target == m_subject) {
				throw new edu.cmu.cs.stage3.alice.core.SimulationPropertyException("target value must not be equal to the subject value.", getCurrentStack(), target);
			}

			m_transformationBegin = m_subject.getTransformation(m_asSeenBy);
			m_transformationEnd = new edu.cmu.cs.stage3.math.Matrix44(m_subject.calculateGoodLookAt(m_target, m_asSeenBy));
		}

		@Override
		protected boolean affectQuaternion() {
			return true;
		}

		@Override
		protected edu.cmu.cs.stage3.math.Quaternion getTargetQuaternion() {
			return m_transformationEnd.getAxes().getQuaternion();
		}

		@Override
		public void update(double t) {
			double portion = getPortion(t);
			double x;
			double y;
			double z;
			// if( m_followHermiteCubic ) {
			// x = m_xHermite.evaluate( portion );
			// y = m_yHermite.evaluate( portion );
			// z = m_zHermite.evaluate( portion );
			// } else {
			x = edu.cmu.cs.stage3.math.Interpolator.interpolate(m_transformationBegin.m30, m_transformationEnd.m30, portion);
			y = edu.cmu.cs.stage3.math.Interpolator.interpolate(m_transformationBegin.m31, m_transformationEnd.m31, portion);
			z = edu.cmu.cs.stage3.math.Interpolator.interpolate(m_transformationBegin.m32, m_transformationEnd.m32, portion);
			// }
			m_subject.setPositionRightNow(x, y, z, m_asSeenBy);
			super.update(t);
		}

		@Override
		public void epilogue(double t) {
			if (m_subject != null && m_transformationEnd != null) {
				m_subject.setPositionRightNow(m_transformationEnd.m30, m_transformationEnd.m31, m_transformationEnd.m32, m_asSeenBy);
			}
			super.epilogue(t);
		}
	}
}
