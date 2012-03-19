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

public class SizeAnimation extends TransformAnimation {
	public final Vector3Property size = new Vector3Property(this, "size", new javax.vecmath.Vector3d(1, 1, 1));
	public class RuntimePositionAnimation extends RuntimeTransformAnimation {
		private javax.vecmath.Vector3d m_sizeBegin;
		private javax.vecmath.Vector3d m_sizeEnd;

		@Override
		public void prologue(double t) {
			super.prologue(t);
			m_sizeBegin = m_subject.getSize();
			m_sizeEnd = size.getVector3Value();
		}

		@Override
		public void update(double t) {
			super.update(t);
			m_subject.setSizeRightNow(edu.cmu.cs.stage3.math.MathUtilities.interpolate(m_sizeBegin, m_sizeEnd, getPortion(t)), m_asSeenBy);
		}

		@Override
		public void epilogue(double t) {
			super.epilogue(t);
			m_subject.setSizeRightNow(m_sizeEnd, m_asSeenBy);
		}
	}
}
