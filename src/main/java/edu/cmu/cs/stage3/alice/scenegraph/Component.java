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

import edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationEvent;
import edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationListener;
import edu.cmu.cs.stage3.alice.scenegraph.event.HierarchyEvent;
import edu.cmu.cs.stage3.alice.scenegraph.event.HierarchyListener;

/**
 * @author Dennis Cosgrove
 */
public abstract class Component extends Element {
	public static final Property PARENT_PROPERTY = new Property(Component.class, "PARENT");
	private Container m_parent = null;
	private java.util.Vector m_absoluteTransformationListeners = new java.util.Vector();
	private java.util.Vector m_hierarchyListeners = new java.util.Vector();

	@Override
	protected void releasePass1() {
		if (m_parent != null) {
			warnln("WARNING: released component " + this + " still has parent " + m_parent + ".");
			setParent(null);
		}
		super.releasePass1();
	}

	@Override
	protected void releasePass3() {
		java.util.Enumeration enum0;
		enum0 = m_absoluteTransformationListeners.elements();
		while (enum0.hasMoreElements()) {
			AbsoluteTransformationListener absoluteTransformationListener = (AbsoluteTransformationListener) enum0.nextElement();
			warnln("WARNING: released component " + this + " still has absoluteTransformationListener " + absoluteTransformationListener + ".");
		}
		// todo
		// m_absoluteTransformationListeners = null;
		// m_absoluteTransformationListenerArray = null;

		enum0 = m_hierarchyListeners.elements();
		while (enum0.hasMoreElements()) {
			HierarchyListener hierarchyListener = (HierarchyListener) enum0.nextElement();
			warnln("WARNING: released component " + this + " still has hierarchyListener " + hierarchyListener + ".");
		}
		// todo
		// m_hierarchyListeners = null;
		// m_hierarchyListenerArray = null;
	}
	public Container getRoot() {
		if (m_parent != null) {
			return m_parent.getRoot();
		} else {
			return null;
		}
	}
	public javax.vecmath.Matrix4d getAbsoluteTransformation() {
		if (m_parent != null) {
			return m_parent.getAbsoluteTransformation();
		} else {
			javax.vecmath.Matrix4d m = new javax.vecmath.Matrix4d();
			m.setIdentity();
			return m;
		}
	}
	public javax.vecmath.Matrix4d getInverseAbsoluteTransformation() {
		if (m_parent != null) {
			return m_parent.getInverseAbsoluteTransformation();
		} else {
			javax.vecmath.Matrix4d m = new javax.vecmath.Matrix4d();
			m.setIdentity();
			return m;
		}
	}
	public Container getParent() {
		return m_parent;
	}
	public void setParent(Container parent) {
		if (m_parent != parent) {
			if (m_parent != null) {
				m_parent.onRemoveChild(this);
			}
			m_parent = parent;
			if (m_parent != null) {
				m_parent.onAddChild(this);
			}
			onPropertyChange(PARENT_PROPERTY);
			onAbsoluteTransformationChange();
			onHierarchyChange();
		}
	}

	public boolean isDescendantOf(Container container) {
		if (container == null) {
			return false;
		}
		if (m_parent == container) {
			return true;
		} else {
			if (m_parent == null) {
				return false;
			} else {
				return m_parent.isDescendantOf(container);
			}
		}
	}

	public void addAbsoluteTransformationListener(AbsoluteTransformationListener absoluteTransformationListener) {
		m_absoluteTransformationListeners.addElement(absoluteTransformationListener);
	}
	public void removeAbsoluteTransformationListener(AbsoluteTransformationListener absoluteTransformationListener) {
		m_absoluteTransformationListeners.removeElement(absoluteTransformationListener);
	}
	public AbsoluteTransformationListener[] getAbsoluteTransformationListeners() {
		AbsoluteTransformationListener[] array = new AbsoluteTransformationListener[m_absoluteTransformationListeners.size()];
		m_absoluteTransformationListeners.copyInto(array);
		return array;
	}
	private void onAbsoluteTransformationChange(AbsoluteTransformationEvent absoluteTransformationEvent) {
		java.util.Enumeration enum0 = m_absoluteTransformationListeners.elements();
		while (enum0.hasMoreElements()) {
			((AbsoluteTransformationListener) enum0.nextElement()).absoluteTransformationChanged(absoluteTransformationEvent);
		}
	}
	protected void onAbsoluteTransformationChange() {
		if (isReleased()) {
			warnln("WARNING: absolute transformation change on already released " + this + ".");
		} else {
			onAbsoluteTransformationChange(new AbsoluteTransformationEvent(this));
		}
	}

	public void addHierarchyListener(HierarchyListener hierarchyListener) {
		m_hierarchyListeners.addElement(hierarchyListener);
	}
	public void removeHierarchyListener(HierarchyListener hierarchyListener) {
		m_hierarchyListeners.removeElement(hierarchyListener);
	}
	public HierarchyListener[] getHierarchyListeners() {
		// todo
		HierarchyListener[] array = new HierarchyListener[m_hierarchyListeners.size()];
		m_hierarchyListeners.copyInto(array);
		return array;
	}
	private void onHierarchyChange(HierarchyEvent hierarchyEvent) {
		java.util.Enumeration enum0 = m_hierarchyListeners.elements();
		while (enum0.hasMoreElements()) {
			((HierarchyListener) enum0.nextElement()).hierarchyChanged(hierarchyEvent);
		}
	}
	protected void onHierarchyChange() {
		if (isReleased()) {
			warnln("WARNING: scenegraph heirarchy change on already released " + this + ".");
		} else {
			// if( this instanceof Camera ) {
			// Thread.dumpStack();
			// }
			onHierarchyChange(new HierarchyEvent(this));
		}
	}
}
