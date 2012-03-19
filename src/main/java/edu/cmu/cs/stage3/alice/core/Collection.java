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

package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.alice.core.property.ClassProperty;
import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;

public abstract class Collection extends Element {
	public final ObjectArrayProperty values = new ObjectArrayProperty(this, "values", null, Object[].class);
	public final ClassProperty valueClass = new ClassProperty(this, "valueClass", null);

	@Override
	protected void propertyChanged(Property property, Object value) {
		if (property == valueClass) {
			values.setComponentType((Class) value);
		} else {
			super.propertyChanged(property, value);
		}
	}

	// for jython
	/** @depracated */
	public void append(Object o) {
		values.add(o);
	}
	// for jython
	/** @depracated */
	public void insert(Number index, Object o) {
		values.add(index.intValue(), o);
	}

}