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

package edu.cmu.cs.stage3.alice.scenegraph;

import edu.cmu.cs.stage3.alice.scenegraph.event.ChildrenEvent;
import edu.cmu.cs.stage3.alice.scenegraph.event.ChildrenListener;

/**
 * @author Dennis Cosgrove
 */
public abstract class Container extends Component {
	private java.util.Vector m_children = new java.util.Vector();
	private Component[] m_childArray = null;
	private java.util.Vector m_childrenListeners = new java.util.Vector();
	private ChildrenListener[] m_childrenListenerArray = null;

	public boolean isAncestorOf( Component component ) {
		if( component == null ) {
			return false;
		} else {
			return component.isDescendantOf( this );
		}
	}

	
	protected void releasePass1() {
		Component[] children = getChildren();
		for( int i=0; i<children.length; i++ ) {
			warnln( "WARNING: released container " + this + " still has child " + children[ i ] + "." );
			children[ i ].setParent( null );
		}
		super.releasePass1();
	}
	
	protected void releasePass2() {
		m_children = null;
		m_childArray = null;
		super.releasePass2();
	}
	
	protected void releasePass3() {
		java.util.Enumeration enum0 = m_childrenListeners.elements();
		while( enum0.hasMoreElements() ) {
			ChildrenListener childrenListener = (ChildrenListener)enum0.nextElement();
			warnln( "WARNING: released container " + this + " still has childrenListener " + childrenListener + "." );
		}
		m_childrenListeners = null;
		m_childrenListenerArray = null;
		super.releasePass3();
	}
	protected void onAddChild( Component child ) {
		if( isReleased() ) {
			warnln( "WARNING: scenegraph addChild " + child + " on already released " + this + "." );
		} else {
			if( child.isReleased() ) {
				warnln( "WARNING: scenegraph addChild from " + this + " on already released child " + child + "." );
			} else {
				m_children.addElement( child );
				m_childArray = null;
				ChildrenEvent childrenEvent = new ChildrenEvent( this, ChildrenEvent.CHILD_ADDED, child );
				ChildrenListener[] childrenListeners = getChildrenListeners();
				for( int i=0; i<childrenListeners.length; i++ ) {
					childrenListeners[ i ].childAdded( childrenEvent );
				}
			}
		}
	}
	protected void onRemoveChild( Component child ) {
		if( isReleased() ) {
			warnln( "WARNING: scenegraph removeChild " + child + " on already released " + this + "." );
		} else {
			if( child.isReleased() ) {
				warnln( "WARNING: scenegraph removeChild from " + this + " on already released child " + child + "." );
			} else {
				m_children.removeElement( child );
				m_childArray = null;
				ChildrenEvent childrenEvent = new ChildrenEvent( this, ChildrenEvent.CHILD_REMOVED, child );
				ChildrenListener[] childrenListeners = getChildrenListeners();
				for( int i=0; i<childrenListeners.length; i++ ) {
					childrenListeners[ i ].childRemoved( childrenEvent );
				}
			}
		}
	}
	public Component[] getChildren() {
		if( m_childArray == null ) {
			m_childArray = new Component[ m_children.size() ];
			m_children.copyInto( m_childArray );
		}
		return m_childArray;
	}
	public int getChildCount() {
		return m_children.size();
	}
	public Component getChildAt( int i ) {
		return (Component)m_children.elementAt( i );
	}
	public void addChildrenListener( ChildrenListener childrenListener ) {
		m_childrenListeners.addElement( childrenListener );
		m_childrenListenerArray = null;
	}
	public void removeChildrenListener( ChildrenListener childrenListener ) {
		m_childrenListeners.removeElement( childrenListener );
		m_childrenListenerArray = null;
	}
	public ChildrenListener[] getChildrenListeners() {
		if( m_childrenListenerArray == null ) {
			m_childrenListenerArray = new ChildrenListener[ m_childrenListeners.size() ];
			m_childrenListeners.copyInto( m_childrenListenerArray );
		}
		return m_childrenListenerArray;
	}
	
	protected void onAbsoluteTransformationChange() {
		super.onAbsoluteTransformationChange();
		Component[] children = getChildren();
		for( int i=0; i<children.length; i++ ) {
			children[ i ].onAbsoluteTransformationChange();
		}
	}
	
	protected void onHierarchyChange() {
		super.onHierarchyChange();
		Component[] children = getChildren();
		for( int i=0; i<children.length; i++ ) {
			children[ i ].onHierarchyChange();
		}
	}
}
