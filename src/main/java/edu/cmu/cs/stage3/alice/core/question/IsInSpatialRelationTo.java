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

package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.ReferenceFrame;
import edu.cmu.cs.stage3.alice.core.SpatialRelation;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.property.ReferenceFrameProperty;

public abstract class IsInSpatialRelationTo extends SubjectObjectQuestion {
	public final ReferenceFrameProperty asSeenBy = new ReferenceFrameProperty(this, "asSeenBy", null);
	protected abstract SpatialRelation getSpatialRelation();

	@Override
	public Class getValueClass() {
		return Boolean.class;
	}

	@Override
	protected Object getValue(Transformable subjectValue, Transformable objectValue) {
		ReferenceFrame asSeenByValue = asSeenBy.getReferenceFrameValue();
		if (subjectValue.isInSpatialRelationTo(getSpatialRelation(), objectValue, asSeenByValue)) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}
}