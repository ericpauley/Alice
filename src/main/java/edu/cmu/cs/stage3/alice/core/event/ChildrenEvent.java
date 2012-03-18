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

import edu.cmu.cs.stage3.alice.core.Element;

public class ChildrenEvent extends java.util.EventObject {
	public static final int CHILD_INSERTED = 1;
	public static final int CHILD_SHIFTED = 2;
	public static final int CHILD_REMOVED = 3;
	protected Element m_child;
	protected int m_changeType;
	protected int m_oldIndex;
	protected int m_newIndex;
	public ChildrenEvent( Element source, Element child, int changeType, int oldIndex, int newIndex ) {
		super( source );
		m_child = child;
		m_changeType = changeType;
		m_oldIndex = oldIndex;
		m_newIndex = newIndex;
	}
	public Element getParent() {
		return (Element)getSource();
	}
	public Element getChild() {
		return m_child;
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
