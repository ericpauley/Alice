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

package edu.cmu.cs.stage3.alice.core.event;

public class ObjectArrayPropertyEvent extends java.util.EventObject {
	public static final int ITEM_INSERTED = 1;
	public static final int ITEM_SHIFTED = 2;
	public static final int ITEM_REMOVED = 3;
	//todo?
	//public static final int ITEM_SET = 4;
	protected Object m_item;
	protected int m_changeType;
	protected int m_oldIndex;
	protected int m_newIndex;
	public ObjectArrayPropertyEvent( edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty source, Object item, int changeType, int oldIndex, int newIndex ) {
		super( source );
		m_item = item;
		m_changeType = changeType;
		m_oldIndex = oldIndex;
		m_newIndex = newIndex;
	}
	/** @deprecated */
	public edu.cmu.cs.stage3.alice.core.Property getProperty() {
		return (edu.cmu.cs.stage3.alice.core.Property)getSource();
	}
	public edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty getObjectArrayProperty() {
		return (edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty)getSource();
	}
	public Object getItem() {
		return m_item;
	}
	public int getChangeType() {
		return m_changeType;
	}
	public int getOldIndex() {
		return m_oldIndex;
	}
	public int getNewIndex() {
		return m_newIndex;
	}
}
