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

public abstract class AbstractProxy {
	private edu.cmu.cs.stage3.alice.scenegraph.Element m_sgElement;
	private AbstractProxyRenderer m_abstractProxyRenderer;

	public void initialize(edu.cmu.cs.stage3.alice.scenegraph.Element sgElement, AbstractProxyRenderer renderer) {
		m_sgElement = sgElement;
		setRenderer(renderer);
		initializeProperties();
		renderer.addListenersToSGElement(sgElement);
	}

	public void release() {
		m_abstractProxyRenderer.removeListenersFromSGElement(m_sgElement);
		m_abstractProxyRenderer.forgetProxyFor(m_sgElement);
		m_sgElement = null;
	}

	protected void setRenderer(AbstractProxyRenderer renderer) {
		m_abstractProxyRenderer = renderer;
	}
	protected void initializeProperties() {
		java.util.Enumeration enum0 = edu.cmu.cs.stage3.alice.scenegraph.Property.getProperties(m_sgElement.getClass()).elements();
		while (enum0.hasMoreElements()) {
			edu.cmu.cs.stage3.alice.scenegraph.Property property = (edu.cmu.cs.stage3.alice.scenegraph.Property) enum0.nextElement();
			changed(property, property.get(m_sgElement));
		}
	}
	protected abstract void changed(edu.cmu.cs.stage3.alice.scenegraph.Property property, Object value);
	public edu.cmu.cs.stage3.alice.scenegraph.Element getSceneGraphElement() {
		return m_sgElement;
	}
	public AbstractProxyRenderer getRenderer() {
		return m_abstractProxyRenderer;
	}
	protected AbstractProxy getProxyFor(edu.cmu.cs.stage3.alice.scenegraph.Element sgElement) {
		return m_abstractProxyRenderer.getProxyFor(sgElement);
	}
	protected AbstractProxy[] getProxiesFor(edu.cmu.cs.stage3.alice.scenegraph.Element[] sgElements, Class componentType) {
		return m_abstractProxyRenderer.getProxiesFor(sgElements, componentType);
	}
	protected void createNecessaryProxies(edu.cmu.cs.stage3.alice.scenegraph.Element sgElement) {
		m_abstractProxyRenderer.createNecessaryProxies(sgElement);
	}
	protected void markAllRenderTargetsDirty() {
		m_abstractProxyRenderer.markAllRenderTargetsDirty();
	}

	@Override
	public String toString() {
		return getClass().getName().substring("edu.cmu.cs.stage3.alice.scenegraph.renderer.".length()) + "[" + m_sgElement.toString() + "]";
	}
}
