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

public class PositionAnimation extends AbstractPositionAnimation {
	public final Vector3Property position = new Vector3Property(this, "position", new javax.vecmath.Vector3d(0, 0, 0));
	public class RuntimePositionAnimation extends RuntimeAbstractPositionAnimation {

		@Override
		protected javax.vecmath.Vector3d getPositionBegin() {
			return m_subject.getPosition(edu.cmu.cs.stage3.alice.core.ReferenceFrame.ABSOLUTE);
		}

		@Override
		protected javax.vecmath.Vector3d getPositionEnd() {
			return m_asSeenBy.getPosition(position.getVector3Value(), edu.cmu.cs.stage3.alice.core.ReferenceFrame.ABSOLUTE);
		}
	}
}
