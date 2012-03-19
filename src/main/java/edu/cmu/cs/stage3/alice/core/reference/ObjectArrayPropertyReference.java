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

package edu.cmu.cs.stage3.alice.core.reference;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;
import edu.cmu.cs.stage3.util.Criterion;

public class ObjectArrayPropertyReference extends PropertyReference {
	private int m_index;
	private int m_precedingTotal;
	public ObjectArrayPropertyReference(ObjectArrayProperty objectArrayProperty, Criterion criterion, int index, int precedingTotal) {
		super(objectArrayProperty, criterion);
		m_index = index;
		m_precedingTotal = precedingTotal;
	}

	@Override
	public Element getReference() {
		return (Element) getObjectArrayProperty().get(m_index);
	}
	public ObjectArrayProperty getObjectArrayProperty() {
		return (ObjectArrayProperty) getProperty();
	}

	@Override
	public void resolve(edu.cmu.cs.stage3.alice.core.ReferenceResolver referenceResolver) throws edu.cmu.cs.stage3.alice.core.UnresolvableReferenceException {
		getObjectArrayProperty().set(m_index, resolveReference(referenceResolver));
	}
	public int getIndex() {
		return m_index;
	}
	public int getPrecedingTotal() {
		return m_precedingTotal;
	}

	@Override
	public String toString() {
		return "ObjectArrayPropertyReference[property=" + getObjectArrayProperty() + ",criterion=" + getCriterion() + ",index=" + getIndex() + "]";
	}
}
