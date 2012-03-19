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

package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;

public class BooleanProperty extends ObjectProperty {
	public BooleanProperty(Element owner, String name, Boolean defaultValue) {
		super(owner, name, defaultValue, Boolean.class);
	}

	@Override
	public Object getValue() {
		Object value = super.getValue();
		if (value instanceof Number) {
			Number number = (Number) value;
			if (number.byteValue() != 0) {
				value = Boolean.TRUE;
			} else {
				value = Boolean.FALSE;
			}
		}
		return value;
	}
	public Boolean getBooleanValue() {
		return (Boolean) getValue();
	}
	public boolean booleanValue(boolean valueIfNull) {
		Boolean b = getBooleanValue();
		if (b != null) {
			return b.booleanValue();
		} else {
			return valueIfNull;
		}
	}
	public boolean booleanValue() {
		return booleanValue(false);
	}

	public void set(boolean value) throws IllegalArgumentException {
		if (value) {
			set(Boolean.TRUE);
		} else {
			set(Boolean.FALSE);
		}
	}
	public void set(boolean value, edu.cmu.cs.stage3.util.HowMuch howMuch) throws IllegalArgumentException {
		if (value) {
			set(Boolean.TRUE, howMuch);
		} else {
			set(Boolean.FALSE, howMuch);
		}
	}

	@Override
	protected void decodeObject(org.w3c.dom.Element node, edu.cmu.cs.stage3.io.DirectoryTreeLoader loader, java.util.Vector referencesToBeResolved, double version) throws java.io.IOException {
		set(Boolean.valueOf(getNodeText(node)));
	}

	@Override
	protected void encodeObject(org.w3c.dom.Document document, org.w3c.dom.Element node, edu.cmu.cs.stage3.io.DirectoryTreeStorer storer, edu.cmu.cs.stage3.alice.core.ReferenceGenerator referenceGenerator) throws java.io.IOException {
		node.appendChild(createNodeForString(document, getBooleanValue().toString()));
	}
}
