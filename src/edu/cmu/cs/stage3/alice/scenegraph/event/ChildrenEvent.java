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

package edu.cmu.cs.stage3.alice.scenegraph.event;

public class ChildrenEvent extends java.util.EventObject {
	public static final int CHILD_ADDED = 1;
	public static final int CHILD_REMOVED = 2;
	private edu.cmu.cs.stage3.alice.scenegraph.Component m_child;
	private int m_id;
	public ChildrenEvent( edu.cmu.cs.stage3.alice.scenegraph.Container source, int id, edu.cmu.cs.stage3.alice.scenegraph.Component child ) {
		super( source );
		m_id = id;
		m_child = child;
	}
	public int getID() {
		return m_id;
	}
	public edu.cmu.cs.stage3.alice.scenegraph.Component getChild() {
		return m_child;
	}
	private String getIDText() {
		switch( m_id ) {
		case CHILD_ADDED:
			return "CHILD_ADDED";
		case CHILD_REMOVED:
			return "CHILD_REMOVED";
		}
		return "UNKNOWN";
	}
	
	public String toString() {
		return getClass().getName() + "[source=" + source + ",id=" + getIDText()+ ",child=" + m_child + "]";
	}
}
