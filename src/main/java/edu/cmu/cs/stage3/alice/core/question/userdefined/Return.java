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

package edu.cmu.cs.stage3.alice.core.question.userdefined;

import edu.cmu.cs.stage3.alice.core.property.ClassProperty;
import edu.cmu.cs.stage3.alice.core.property.ValueProperty;

public class Return extends Component {
	public final ValueProperty value = new ValueProperty(this, "value", null);
	public final ClassProperty valueClass = new ClassProperty(this, "valueClass", null);

	@Override
	protected void propertyChanged(edu.cmu.cs.stage3.alice.core.Property property, Object o) {
		if (property == value) {
			// todo
		} else if (property == valueClass) {
			value.setOverrideValueClass((Class) o);
		} else {
			super.propertyChanged(property, o);
		}
	}

	@Override
	public Object[] execute() {
		Class valueClassValue = valueClass.getClassValue();
		Object[] returnArray = (Object[]) java.lang.reflect.Array.newInstance(valueClass.getClassValue(), 1);
		returnArray[0] = value.getValue();
		return returnArray;
	}
}
