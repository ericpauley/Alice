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

import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.Matrix44Property;
import edu.cmu.cs.stage3.math.HermiteCubic;
import edu.cmu.cs.stage3.math.Interpolator;
import edu.cmu.cs.stage3.math.Matrix44;
import edu.cmu.cs.stage3.math.Quaternion;

public class PointOfViewAnimation extends OrientationAnimation {
	public final Matrix44Property pointOfView = new Matrix44Property(this, "pointOfView", new Matrix44());
	public final BooleanProperty affectPosition = new BooleanProperty(this, "affectPosition", Boolean.TRUE);
	// todo: change to affectOrientation
	public final BooleanProperty affectQuaternion = new BooleanProperty(this, "affectQuaternion", Boolean.TRUE);
	public final BooleanProperty followHermiteCubic = new BooleanProperty(this, "followHermiteCubic", Boolean.FALSE);

	public class RuntimePointOfViewAnimation extends RuntimeOrientationAnimation {
		private Matrix44 m_transformationBegin;
		private Matrix44 m_transformationEnd;
		private boolean m_affectPosition;
		private boolean m_affectQuaternion;
		private boolean m_followHermiteCubic;
		private HermiteCubic m_xHermite;
		private HermiteCubic m_yHermite;
		private HermiteCubic m_zHermite;

		@Override
		public void prologue(double t) {
			m_affectPosition = affectPosition.booleanValue();
			m_affectQuaternion = affectQuaternion.booleanValue();
			super.prologue(t);
			if (m_affectPosition) {
				m_followHermiteCubic = followHermiteCubic.booleanValue();
				m_transformationBegin = m_subject.getTransformation(m_asSeenBy);
				m_transformationEnd = pointOfView.getMatrix44Value();
				if (m_followHermiteCubic) {
					double dx = m_transformationBegin.m30 - m_transformationEnd.m30;
					double dy = m_transformationBegin.m31 - m_transformationEnd.m31;
					double dz = m_transformationBegin.m32 - m_transformationEnd.m32;
					double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
					double s = distance / 2;
					m_xHermite = new HermiteCubic(m_transformationBegin.m30, m_transformationEnd.m30, m_transformationBegin.m20 * s, m_transformationEnd.m20 * s);
					m_yHermite = new HermiteCubic(m_transformationBegin.m31, m_transformationEnd.m31, m_transformationBegin.m21 * s, m_transformationEnd.m21 * s);
					m_zHermite = new HermiteCubic(m_transformationBegin.m32, m_transformationEnd.m32, m_transformationBegin.m22 * s, m_transformationEnd.m22 * s);
				}
			}
		}

		@Override
		protected boolean affectQuaternion() {
			return m_affectQuaternion;
		}

		@Override
		protected Quaternion getTargetQuaternion() {
			return m_transformationEnd.getAxes().getQuaternion();
		}

		@Override
		public void update(double t) {
			if (m_affectPosition) {
				double portion = getPortion(t);
				double x;
				double y;
				double z;
				if (m_followHermiteCubic) {
					x = m_xHermite.evaluate(portion);
					y = m_yHermite.evaluate(portion);
					z = m_zHermite.evaluate(portion);
				} else {
					x = Interpolator.interpolate(m_transformationBegin.m30, m_transformationEnd.m30, portion);
					y = Interpolator.interpolate(m_transformationBegin.m31, m_transformationEnd.m31, portion);
					z = Interpolator.interpolate(m_transformationBegin.m32, m_transformationEnd.m32, portion);
				}
				m_subject.setPositionRightNow(x, y, z, m_asSeenBy);
			}
			super.update(t);
		}

		@Override
		public void epilogue(double t) {
			if (m_affectPosition) {
				m_subject.setPositionRightNow(m_transformationEnd.m30, m_transformationEnd.m31, m_transformationEnd.m32, m_asSeenBy);
			}
			super.epilogue(t);
		}
	}
}
