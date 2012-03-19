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

package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.core.event.PropertyListener;

/**
 * @author Jason Pratt
 */
public class ObjectArrayPropertyItem extends edu.cmu.cs.stage3.alice.core.Property {
	protected edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty objectArrayProperty;
	protected int index;
	protected Class type;

	public ObjectArrayPropertyItem(edu.cmu.cs.stage3.alice.core.Element element, edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty objectArrayProperty, int index, Class type) {
		super(element, "item " + index, null, objectArrayProperty.getComponentType());
		// this.setIsAcceptingOfNull( true );
		this.objectArrayProperty = objectArrayProperty;
		this.index = index;
		this.type = type;
	}

	public edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty getObjectArrayProperty() {
		return objectArrayProperty;
	}

	public int getIndex() {
		return index;
	}

	@Override
	public Object get() {
		return objectArrayProperty.get(index);
	}

	@Override
	public Object getValue() {
		return objectArrayProperty.get(index);
	}

	@Override
	public Class getValueClass() {
		return type;
	}

	@Override
	public void set(Object value) throws IllegalArgumentException {
		objectArrayProperty.set(index, value);
		// super.set( value );
	}

	public void dispose() {
		edu.cmu.cs.stage3.alice.core.event.PropertyListener[] listeners = getPropertyListeners();
		for (PropertyListener listener : listeners) {
			removePropertyListener(listener);
		}
		objectArrayProperty = null;
	}

}