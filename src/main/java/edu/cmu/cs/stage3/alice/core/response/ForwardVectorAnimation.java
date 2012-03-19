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

public class ForwardVectorAnimation extends OrientationAnimation {
	public final Vector3Property forward = new Vector3Property(this, "forward", new edu.cmu.cs.stage3.math.Vector3(0, 0, 1));
	public final Vector3Property upGuide = new Vector3Property(this, "upGuide", null);
	public class RuntimeForwardVectorAnimation extends RuntimeOrientationAnimation {
		private javax.vecmath.Vector3d m_forward;
		private javax.vecmath.Vector3d m_upGuide;

		@Override
		public void prologue(double t) {
			super.prologue(t);
			m_forward = forward.getVector3Value();
			m_upGuide = upGuide.getVector3Value();
		}

		@Override
		protected edu.cmu.cs.stage3.math.Quaternion getTargetQuaternion() {
			return edu.cmu.cs.stage3.alice.core.Transformable.calculateOrientation(m_forward, m_upGuide).getQuaternion();
		}
	}
}
