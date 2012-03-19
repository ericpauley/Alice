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

public class PropertyUndoableRedoable implements ContextAssignableUndoableRedoable {
	protected edu.cmu.cs.stage3.alice.core.Property property;
	protected Object oldValue;
	protected Object newValue;
	protected Object context;

	/**
	 * @deprecated please use other constructor
	 */
	@Deprecated
	public PropertyUndoableRedoable(edu.cmu.cs.stage3.alice.core.Property property, Object oldValue, Object newValue, Object context) {
		this.property = property;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public PropertyUndoableRedoable(edu.cmu.cs.stage3.alice.core.Property property, Object oldValue, Object newValue) {
		this.property = property;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	@Override
	public void setContext(Object context) {
		this.context = context;
	}

	@Override
	public void undo() {
		property.set(oldValue);
	}

	@Override
	public void redo() {
		property.set(newValue);
	}

	@Override
	public Object getAffectedObject() {
		return property.getOwner();
	}

	@Override
	public Object getContext() {
		return context;
	}

	@Override
	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append("edu.cmu.cs.stage3.alice.authoringtool.util.PropertyUndoableRedoable[ ");
		s.append("property=" + property + "; ");
		s.append("oldValue=" + oldValue + "; ");
		s.append("newValue=" + newValue + "; ");
		if (context != this && !(context instanceof DefaultUndoRedoStack)) { // watch
																				// out
																				// for
																				// infinite
																				// loops
			s.append("context=" + context + "; ");
		} else {
			s.append("context=" + context.getClass() + "; ");
		}
		s.append(" ]");
		return s.toString();
	}
}