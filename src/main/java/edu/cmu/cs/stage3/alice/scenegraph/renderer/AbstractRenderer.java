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

package edu.cmu.cs.stage3.alice.scenegraph.renderer;

import java.util.EventObject;
import java.util.Vector;

import edu.cmu.cs.stage3.alice.scenegraph.event.ReleaseEvent;

public abstract class AbstractRenderer implements edu.cmu.cs.stage3.alice.scenegraph.renderer.Renderer, edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationListener, edu.cmu.cs.stage3.alice.scenegraph.event.BoundListener, edu.cmu.cs.stage3.alice.scenegraph.event.ChildrenListener, edu.cmu.cs.stage3.alice.scenegraph.event.HierarchyListener, edu.cmu.cs.stage3.alice.scenegraph.event.PropertyListener, edu.cmu.cs.stage3.alice.scenegraph.event.ReleaseListener {

	private boolean m_isSoftwareEmulationForced;
	private Vector<RenderTarget> m_onscreenRenderTargets = new Vector<RenderTarget>();
	private Vector<RenderTarget> m_offscreenRenderTargets = new Vector<RenderTarget>();
	private OnscreenRenderTarget[] m_onscreenRenderTargetArray = null;
	private OffscreenRenderTarget[] m_offscreenRenderTargetArray = null;

	@SuppressWarnings("unused")
	private Vector<?> m_rendererListeners = new Vector<Object>();

	private Vector<EventObject> m_pendingAbsoluteTransformationChanges = new Vector<EventObject>();
	private Vector<EventObject> m_pendingBoundChanges = new Vector<EventObject>();
	private Vector<EventObject> m_pendingChildChanges = new Vector<EventObject>();
	private Vector<EventObject> m_pendingHeirarchyChanges = new Vector<EventObject>();
	private Vector<EventObject> m_pendingPropertyChanges = new Vector<EventObject>();
	private Vector<ReleaseEvent> m_pendingReleases = new Vector<ReleaseEvent>();

	protected abstract void dispatchAbsoluteTransformationChange(edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationEvent absoluteTransformationEvent);
	protected abstract void dispatchBoundChange(edu.cmu.cs.stage3.alice.scenegraph.event.BoundEvent boundEvent);
	protected abstract void dispatchChildAdd(edu.cmu.cs.stage3.alice.scenegraph.event.ChildrenEvent childrenEvent);
	protected abstract void dispatchChildRemove(edu.cmu.cs.stage3.alice.scenegraph.event.ChildrenEvent childrenEvent);
	protected abstract void dispatchHierarchyChange(edu.cmu.cs.stage3.alice.scenegraph.event.HierarchyEvent hierarchyEvent);
	protected abstract void dispatchPropertyChange(edu.cmu.cs.stage3.alice.scenegraph.event.PropertyEvent propertyEvent);
	protected abstract void dispatchRelease(edu.cmu.cs.stage3.alice.scenegraph.event.ReleaseEvent releaseEvent);

	protected abstract boolean requiresHierarchyAndAbsoluteTransformationListening();
	protected abstract boolean requiresBoundListening();

	private int m_ignoreCount = 0;
	public void enterIgnore() {
		m_ignoreCount++;
	}
	public void leaveIgnore() {
		m_ignoreCount--;
	}
	@SuppressWarnings("unused")
	private boolean ignore() {
		return m_ignoreCount > 0;
	}
	public void markAllRenderTargetsDirty() {
		if (m_ignoreCount > 0) {
			// pass
		} else {
			RenderTarget[] renderTargets;
			renderTargets = getOffscreenRenderTargets();
			for (RenderTarget renderTarget : renderTargets) {
				renderTarget.markDirty();
			}
			renderTargets = getOnscreenRenderTargets();
			for (RenderTarget renderTarget : renderTargets) {
				renderTarget.markDirty();
			}
		}
	}

	public void addListenersToSGElement(edu.cmu.cs.stage3.alice.scenegraph.Element sgElement) {
		if (sgElement instanceof edu.cmu.cs.stage3.alice.scenegraph.Geometry) {
			if (requiresBoundListening()) {
				((edu.cmu.cs.stage3.alice.scenegraph.Geometry) sgElement).addBoundListener(this);
			}
		} else if (sgElement instanceof edu.cmu.cs.stage3.alice.scenegraph.Component) {
			if (sgElement instanceof edu.cmu.cs.stage3.alice.scenegraph.Container) {
				((edu.cmu.cs.stage3.alice.scenegraph.Container) sgElement).addChildrenListener(this);
			}
			if (requiresHierarchyAndAbsoluteTransformationListening()) {
				((edu.cmu.cs.stage3.alice.scenegraph.Component) sgElement).addAbsoluteTransformationListener(this);
				((edu.cmu.cs.stage3.alice.scenegraph.Component) sgElement).addHierarchyListener(this);
			}
		}

		sgElement.addPropertyListener(this);
		sgElement.addReleaseListener(this);
	}
	public void removeListenersFromSGElement(edu.cmu.cs.stage3.alice.scenegraph.Element sgElement) {
		if (sgElement instanceof edu.cmu.cs.stage3.alice.scenegraph.Geometry) {
			if (requiresBoundListening()) {
				((edu.cmu.cs.stage3.alice.scenegraph.Geometry) sgElement).removeBoundListener(this);
			}
		} else if (sgElement instanceof edu.cmu.cs.stage3.alice.scenegraph.Component) {
			if (sgElement instanceof edu.cmu.cs.stage3.alice.scenegraph.Container) {
				((edu.cmu.cs.stage3.alice.scenegraph.Container) sgElement).removeChildrenListener(this);
			}
			if (requiresHierarchyAndAbsoluteTransformationListening()) {
				((edu.cmu.cs.stage3.alice.scenegraph.Component) sgElement).removeAbsoluteTransformationListener(this);
				((edu.cmu.cs.stage3.alice.scenegraph.Component) sgElement).removeHierarchyListener(this);
			}
		}
		sgElement.removePropertyListener(this);
		sgElement.removeReleaseListener(this);
	}
	public void commitAnyPendingChanges() {
		java.util.Enumeration<EventObject> enum0;
		if (m_pendingAbsoluteTransformationChanges.size() > 0) {
			synchronized (m_pendingAbsoluteTransformationChanges) {
				enum0 = m_pendingAbsoluteTransformationChanges.elements();
				while (enum0.hasMoreElements()) {
					dispatchAbsoluteTransformationChange((edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationEvent) enum0.nextElement());
				}
				m_pendingAbsoluteTransformationChanges.clear();
			}
		}
		if (m_pendingBoundChanges.size() > 0) {
			synchronized (m_pendingBoundChanges) {
				enum0 = m_pendingBoundChanges.elements();
				while (enum0.hasMoreElements()) {
					dispatchBoundChange((edu.cmu.cs.stage3.alice.scenegraph.event.BoundEvent) enum0.nextElement());
				}
				m_pendingBoundChanges.clear();
			}
		}
		if (m_pendingChildChanges.size() > 0) {
			synchronized (m_pendingChildChanges) {
				enum0 = m_pendingChildChanges.elements();
				while (enum0.hasMoreElements()) {
					edu.cmu.cs.stage3.alice.scenegraph.event.ChildrenEvent e = (edu.cmu.cs.stage3.alice.scenegraph.event.ChildrenEvent) enum0.nextElement();
					if (e.getID() == edu.cmu.cs.stage3.alice.scenegraph.event.ChildrenEvent.CHILD_ADDED) {
						dispatchChildAdd(e);
					} else {
						dispatchChildRemove(e);
					}
				}
				m_pendingChildChanges.clear();
			}
		}
		if (m_pendingHeirarchyChanges.size() > 0) {
			synchronized (m_pendingHeirarchyChanges) {
				enum0 = m_pendingHeirarchyChanges.elements();
				while (enum0.hasMoreElements()) {
					dispatchHierarchyChange((edu.cmu.cs.stage3.alice.scenegraph.event.HierarchyEvent) enum0.nextElement());
				}
				m_pendingHeirarchyChanges.clear();
			}
		}
		if (m_pendingPropertyChanges.size() > 0) {
			synchronized (m_pendingPropertyChanges) {
				enum0 = m_pendingPropertyChanges.elements();
				while (enum0.hasMoreElements()) {
					dispatchPropertyChange((edu.cmu.cs.stage3.alice.scenegraph.event.PropertyEvent) enum0.nextElement());
				}
				m_pendingPropertyChanges.clear();
			}
		}
	}

	private boolean isThreadOK() {
		return javax.swing.SwingUtilities.isEventDispatchThread();
	}
	@Override
	public void absoluteTransformationChanged(edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationEvent absoluteTransformationEvent) {
		if (isThreadOK()) {
			dispatchAbsoluteTransformationChange(absoluteTransformationEvent);
			markAllRenderTargetsDirty();
		} else {
			m_pendingAbsoluteTransformationChanges.addElement(absoluteTransformationEvent);
		}
	}
	@Override
	public void boundChanged(edu.cmu.cs.stage3.alice.scenegraph.event.BoundEvent boundEvent) {
		if (isThreadOK()) {
			dispatchBoundChange(boundEvent);
			markAllRenderTargetsDirty();
		} else {
			m_pendingBoundChanges.addElement(boundEvent);
		}
	}
	@Override
	public void childAdded(edu.cmu.cs.stage3.alice.scenegraph.event.ChildrenEvent childrenEvent) {
		if (isThreadOK()) {
			dispatchChildAdd(childrenEvent);
			markAllRenderTargetsDirty();
		} else {
			m_pendingChildChanges.addElement(childrenEvent);
		}
	}
	@Override
	public void childRemoved(edu.cmu.cs.stage3.alice.scenegraph.event.ChildrenEvent childrenEvent) {
		if (isThreadOK()) {
			dispatchChildRemove(childrenEvent);
			markAllRenderTargetsDirty();
		} else {
			m_pendingChildChanges.addElement(childrenEvent);
		}
	}
	@Override
	public void hierarchyChanged(edu.cmu.cs.stage3.alice.scenegraph.event.HierarchyEvent hierarchyEvent) {
		if (isThreadOK()) {
			dispatchHierarchyChange(hierarchyEvent);
			markAllRenderTargetsDirty();
		} else {
			m_pendingHeirarchyChanges.addElement(hierarchyEvent);
		}
	}
	@Override
	public synchronized void changed(edu.cmu.cs.stage3.alice.scenegraph.event.PropertyEvent propertyEvent) {
		if (isThreadOK()) {
			dispatchPropertyChange(propertyEvent);
			markAllRenderTargetsDirty();
		} else {
			m_pendingPropertyChanges.addElement(propertyEvent);
		}
	}
	@Override
	public synchronized void releasing(edu.cmu.cs.stage3.alice.scenegraph.event.ReleaseEvent releaseEvent) {
	}
	@Override
	public synchronized void released(edu.cmu.cs.stage3.alice.scenegraph.event.ReleaseEvent releaseEvent) {
		if (isThreadOK()) {
			dispatchRelease(releaseEvent);
			markAllRenderTargetsDirty();
		} else {
			m_pendingReleases.addElement(releaseEvent);
		}
	}

	@Override
	protected void finalize() throws Throwable {
		release();
		super.finalize();
	}
	public boolean addRenderTarget(RenderTarget renderTarget) {
		if (renderTarget instanceof OnscreenRenderTarget) {
			m_onscreenRenderTargetArray = null;
			m_onscreenRenderTargets.addElement(renderTarget);
		} else if (renderTarget instanceof OffscreenRenderTarget) {
			m_offscreenRenderTargetArray = null;
			m_offscreenRenderTargets.addElement(renderTarget);
		} else {
			// todo?
		}
		return true;
	}
	public boolean removeRenderTarget(RenderTarget renderTarget) {
		if (renderTarget instanceof OnscreenRenderTarget) {
			m_onscreenRenderTargetArray = null;
			return m_onscreenRenderTargets.removeElement(renderTarget);
		} else if (renderTarget instanceof OffscreenRenderTarget) {
			m_offscreenRenderTargetArray = null;
			return m_offscreenRenderTargets.removeElement(renderTarget);
		} else {
			return true;
		}
	}

	@Override
	public OnscreenRenderTarget[] getOnscreenRenderTargets() {
		if (m_onscreenRenderTargetArray == null) {
			m_onscreenRenderTargetArray = new OnscreenRenderTarget[m_onscreenRenderTargets.size()];
			m_onscreenRenderTargets.copyInto(m_onscreenRenderTargetArray);
		}
		return m_onscreenRenderTargetArray;
	}
	@Override
	public OffscreenRenderTarget[] getOffscreenRenderTargets() {
		if (m_offscreenRenderTargetArray == null) {
			m_offscreenRenderTargetArray = new OffscreenRenderTarget[m_offscreenRenderTargets.size()];
			m_offscreenRenderTargets.copyInto(m_offscreenRenderTargetArray);
		}
		return m_offscreenRenderTargetArray;
	}

	@Override
	public synchronized void release() {
	}

	@Override
	public boolean isSoftwareEmulationForced() {
		return m_isSoftwareEmulationForced;
	}
	@Override
	public void setIsSoftwareEmulationForced(boolean isSoftwareEmulationForced) {
		m_isSoftwareEmulationForced = isSoftwareEmulationForced;
	}
}
