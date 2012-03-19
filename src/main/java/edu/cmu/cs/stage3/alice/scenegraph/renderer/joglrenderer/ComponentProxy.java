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

package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

abstract class ComponentProxy extends ElementProxy {
	private double[] m_absolute = new double[16];
	private double[] m_inverseAbsolute = new double[16];
	private java.nio.DoubleBuffer m_absoluteBuffer = java.nio.DoubleBuffer.wrap(m_absolute);
	private java.nio.DoubleBuffer m_inverseAbsoluteBuffer = java.nio.DoubleBuffer.wrap(m_inverseAbsolute);
	ComponentProxy() {
		handleAbsoluteTransformationChange();
	}

	@Override
	protected void changed(edu.cmu.cs.stage3.alice.scenegraph.Property property, Object value) {
		if (property == edu.cmu.cs.stage3.alice.scenegraph.Component.PARENT_PROPERTY) {
			// pass
		} else {
			super.changed(property, value);
		}
	}
	public void handleAbsoluteTransformationChange() {
		m_absolute[0] = Double.NaN;
		m_inverseAbsolute[0] = Double.NaN;
	}
	private edu.cmu.cs.stage3.alice.scenegraph.Component getSceneGraphComponent() {
		return (edu.cmu.cs.stage3.alice.scenegraph.Component) getSceneGraphElement();
	}

	public SceneProxy getSceneProxy() {
		edu.cmu.cs.stage3.alice.scenegraph.Component sgComponent = getSceneGraphComponent();
		edu.cmu.cs.stage3.alice.scenegraph.Container sgRoot = sgComponent.getRoot();
		if (sgRoot instanceof edu.cmu.cs.stage3.alice.scenegraph.Scene) {
			return (SceneProxy) getProxyFor(sgRoot);
		} else {
			return null;
		}
	}

	public double[] getAbsoluteTransformation() {
		if (Double.isNaN(m_absolute[0])) {
			copy(m_absolute, getSceneGraphComponent().getAbsoluteTransformation());
		}
		return m_absolute;
	}
	public double[] getInverseAbsoluteTransformation() {
		if (Double.isNaN(m_inverseAbsolute[0])) {
			copy(m_inverseAbsolute, getSceneGraphComponent().getInverseAbsoluteTransformation());
		}
		return m_inverseAbsolute;
	}
	protected java.nio.DoubleBuffer getAbsoluteTransformationAsBuffer() {
		getAbsoluteTransformation();
		return m_absoluteBuffer;
	}
	protected java.nio.DoubleBuffer getInverseAbsoluteTransformationAsBuffer() {
		getInverseAbsoluteTransformation();
		return m_inverseAbsoluteBuffer;
	}

	public abstract void render(RenderContext context);
	public abstract void setup(RenderContext context);
	public abstract void pick(PickContext context, PickParameters pickParameters);

}
