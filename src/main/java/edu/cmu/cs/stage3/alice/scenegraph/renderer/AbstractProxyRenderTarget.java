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

public abstract class AbstractProxyRenderTarget extends AbstractRenderTarget {
	protected AbstractProxyRenderer m_abstractProxyRenderer;
	protected AbstractProxyRenderTarget(AbstractProxyRenderer abstractProxyRenderer) {
		super(abstractProxyRenderer);
		m_abstractProxyRenderer = abstractProxyRenderer;
	}
	protected AbstractProxy getProxyFor(edu.cmu.cs.stage3.alice.scenegraph.Element sgElement) {
		return m_abstractProxyRenderer.getProxyFor(sgElement);
	}
	protected AbstractProxy[] getProxiesFor(edu.cmu.cs.stage3.alice.scenegraph.Element[] sgElements, Class componentType) {
		return m_abstractProxyRenderer.getProxiesFor(sgElements, componentType);
	}

	@Override
	public void addCamera(edu.cmu.cs.stage3.alice.scenegraph.Camera camera) {
		m_abstractProxyRenderer.createNecessaryProxies(camera.getRoot());
		super.addCamera(camera);
	}
}
