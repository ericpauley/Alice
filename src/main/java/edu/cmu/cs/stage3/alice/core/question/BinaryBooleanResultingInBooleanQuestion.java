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

import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;

public abstract class BinaryBooleanResultingInBooleanQuestion extends BooleanQuestion {
	public final BooleanProperty a = new BooleanProperty(this, "a", Boolean.TRUE);
	public final BooleanProperty b = new BooleanProperty(this, "b", Boolean.TRUE);
	protected abstract boolean getValue(boolean a, boolean b);
	protected abstract boolean isShortCircuitable(boolean a);

	@Override
	public Object getValue() {
		boolean aValue = a.booleanValue();
		boolean returnValue;
		if (this instanceof edu.cmu.cs.stage3.alice.core.question.And) {
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext = "both " + aValue + " and " + b.booleanValue() + " is ";
		} else if (this instanceof edu.cmu.cs.stage3.alice.core.question.Or) {
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext = "either " + aValue + " or " + b.booleanValue() + " is ";
		}
		if (isShortCircuitable(aValue)) {
			returnValue = aValue;
		} else {
			returnValue = getValue(aValue, b.booleanValue());
		}
		if (returnValue) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}
}