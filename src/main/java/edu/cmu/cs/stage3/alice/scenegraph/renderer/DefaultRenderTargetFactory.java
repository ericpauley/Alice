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

import java.util.ArrayList;
import java.util.List;

public class DefaultRenderTargetFactory implements RenderTargetFactory {

	public static List<Class<? extends edu.cmu.cs.stage3.alice.scenegraph.renderer.AbstractRenderer>> getPotentialRendererClasses() {
		List<Class<? extends edu.cmu.cs.stage3.alice.scenegraph.renderer.AbstractRenderer>> renderers = new ArrayList<Class<? extends edu.cmu.cs.stage3.alice.scenegraph.renderer.AbstractRenderer>>();

		if (System.getProperty("os.name").startsWith("Win")) {
			try {
				if (edu.cmu.cs.stage3.alice.scenegraph.renderer.util.DirectXVersion.getVersion() >= 7.0) {
					renderers.add(edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer.Renderer.class);
				}
			} catch (Throwable t) {
				// pass
			}
		}

		try {
			renderers.add(edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer.Renderer.class);
		} catch (Throwable t) {
			// pass
		}

		try {
			renderers.add(edu.cmu.cs.stage3.alice.scenegraph.renderer.nullrenderer.Renderer.class);
		} catch (Throwable t) {
			// pass
		}
		return renderers;
	}
	private Class<?> m_rendererClass;
	private Renderer m_renderer;
	public DefaultRenderTargetFactory() {
		this(null);
	}
	public DefaultRenderTargetFactory(Class<?> rendererClass) {
		if (rendererClass == null) {
			rendererClass = getPotentialRendererClasses().get(0);
		}
		m_rendererClass = rendererClass;
	}
	@Override
	public boolean isSoftwareEmulationForced() {
		return getRenderer().isSoftwareEmulationForced();
	}
	@Override
	public void setIsSoftwareEmulationForced(boolean isSoftwareEmulationForced) {
		getRenderer().setIsSoftwareEmulationForced(isSoftwareEmulationForced);
	}

	private Renderer getRenderer() {
		if (m_renderer == null) {
			try {
				m_renderer = (Renderer) m_rendererClass.newInstance();
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		return m_renderer;
	}

	@Override
	public OffscreenRenderTarget createOffscreenRenderTarget() {
		return getRenderer().createOffscreenRenderTarget();
	}
	@Override
	public OnscreenRenderTarget createOnscreenRenderTarget() {
		return getRenderer().createOnscreenRenderTarget();
	}

	@Override
	public void releaseOffscreenRenderTarget(OffscreenRenderTarget offscreenRenderTarget) {
		offscreenRenderTarget.release();
	}
	@Override
	public void releaseOnscreenRenderTarget(OnscreenRenderTarget onscreenRenderTarget) {
		onscreenRenderTarget.release();
	}

	@Override
	public void release() {
		// todo
	}
}
