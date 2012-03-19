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

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Question;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.ElementProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;

public class ChildNamed extends Question {
	public final ElementProperty parent = new ElementProperty(this, "parent", null);
	public final StringProperty name = new StringProperty(this, "name", "");
	public final BooleanProperty ignoreCase = new BooleanProperty(this, "ignoreCase", Boolean.TRUE);

	@Override
	public Object getValue() {
		Element parentValue = parent.getElementValue();
		String nameValue = name.getStringValue();
		if (ignoreCase.booleanValue()) {
			return parentValue.getChildNamed(nameValue);
		} else {
			return parentValue.getChildNamedIgnoreCase(nameValue);
		}
	}

	@Override
	public Class getValueClass() {
		return Element.class;
	}
}