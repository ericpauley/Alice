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

public class ClassProperty extends ObjectProperty {
	public ClassProperty(Element owner, String name, Class defaultValue) {
		super(owner, name, defaultValue, Class.class);
	}
	public Class getClassValue() {
		return (Class) getValue();
	}

	@Override
	protected void decodeObject(org.w3c.dom.Element node, edu.cmu.cs.stage3.io.DirectoryTreeLoader loader, java.util.Vector referencesToBeResolved, double version) throws java.io.IOException {
		// String text = node.getFirstChild().toString(); //?
		String text = getNodeText(node);
		try {
			set(Class.forName(text));
		} catch (ClassNotFoundException cnfe) {
			throw new RuntimeException(text);
		}
	}

	@Override
	protected void encodeObject(org.w3c.dom.Document document, org.w3c.dom.Element node, edu.cmu.cs.stage3.io.DirectoryTreeStorer storer, edu.cmu.cs.stage3.alice.core.ReferenceGenerator referenceGenerator) throws java.io.IOException {
		node.appendChild(createNodeForString(document, getClassValue().getName()));
	}
}
