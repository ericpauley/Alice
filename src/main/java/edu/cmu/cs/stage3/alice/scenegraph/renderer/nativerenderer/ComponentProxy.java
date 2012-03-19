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

package edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer;

public abstract class ComponentProxy extends ElementProxy {
	protected abstract void onAbsoluteTransformationChange(javax.vecmath.Matrix4d m);
	protected abstract void addToScene(SceneProxy scene);
	protected abstract void removeFromScene(SceneProxy scene);

	private SceneProxy m_sceneProxy = null;

	private static java.util.Vector s_changed = new java.util.Vector(); // todo?
																		// initialCapacity
	protected boolean listenToHierarchyAndAbsoluteTransformationChanges() {
		return ((Renderer) getRenderer()).requiresHierarchyAndAbsoluteTransformationListening();
	}
	static void updateAbsoluteTransformationChanges() {
		synchronized (s_changed) {
			if (s_changed.size() > 0) {
				java.util.Enumeration enum0 = s_changed.elements();
				while (enum0.hasMoreElements()) {
					ComponentProxy componentProxy = (ComponentProxy) enum0.nextElement();
					edu.cmu.cs.stage3.alice.scenegraph.Component sgComponent = componentProxy.getSceneGraphComponent();
					if (sgComponent != null) {
						javax.vecmath.Matrix4d m = sgComponent.getAbsoluteTransformation();
						componentProxy.onAbsoluteTransformationChange(m);
					}
				}
				s_changed.removeAllElements();
			}
		}
	}

	private edu.cmu.cs.stage3.alice.scenegraph.Component getSceneGraphComponent() {
		return (edu.cmu.cs.stage3.alice.scenegraph.Component) getSceneGraphElement();
	}

	@Override
	public void initialize(edu.cmu.cs.stage3.alice.scenegraph.Element sgElement, edu.cmu.cs.stage3.alice.scenegraph.renderer.AbstractProxyRenderer renderer) {
		super.initialize(sgElement, renderer);
		if (listenToHierarchyAndAbsoluteTransformationChanges()) {
			onAbsoluteTransformationChange();
			onHierarchyChange();
		}
	}
	public void onAbsoluteTransformationChange() {
		synchronized (s_changed) {
			if (s_changed.contains(this)) {
				// pass
			} else {
				s_changed.addElement(this);
			}
		}
	}
	public void onHierarchyChange() {
		ContainerProxy rootProxy = (ContainerProxy) getProxyFor(getSceneGraphComponent().getRoot());
		if (rootProxy != m_sceneProxy) {
			if (m_sceneProxy != null) {
				removeFromScene(m_sceneProxy);
			}
			if (rootProxy instanceof SceneProxy) {
				m_sceneProxy = (SceneProxy) rootProxy;
			} else {
				m_sceneProxy = null;
			}
			if (m_sceneProxy != null) {
				addToScene(m_sceneProxy);
			}
		}
	}

	@Override
	protected void changed(edu.cmu.cs.stage3.alice.scenegraph.Property property, Object value) {
		if (property == edu.cmu.cs.stage3.alice.scenegraph.Component.PARENT_PROPERTY) {
			// pass
		} else {
			super.changed(property, value);
		}
	}
}
