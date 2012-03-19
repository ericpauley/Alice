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

package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.alice.core.property.ObjectProperty;

public class RenderTarget extends Element {
	/** @deprecated */
	@Deprecated
	public final ObjectProperty requiredCapabilities = new ObjectProperty(this, "requiredCapabilities", null, Long.class);
	private edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget m_onscreenRenderTarget = null;
	private java.awt.Component m_awtComponent;
	private java.util.Vector m_cameras = new java.util.Vector();
	private Camera[] m_cameraArray = null;
	private edu.cmu.cs.stage3.alice.scenegraph.renderer.RenderTargetFactory m_renderTargetFactory;
	private static java.util.Dictionary s_componentMap = new java.util.Hashtable();
	private static java.util.Dictionary s_eventMap = new java.util.Hashtable();
	public RenderTarget() {
		requiredCapabilities.deprecate();
	}

	@Override
	protected void finalize() throws java.lang.Throwable {
		if (m_awtComponent != null) {
			s_componentMap.remove(m_awtComponent);
		}
		super.finalize();
	}

	@Override
	protected void internalRelease(int pass) {
		switch (pass) {
			case 1 :
				java.util.Enumeration enum0 = m_cameras.elements();
				while (enum0.hasMoreElements()) {
					Camera camera = (Camera) enum0.nextElement();
					if (m_onscreenRenderTarget != null) {
						m_onscreenRenderTarget.removeCamera(camera.getSceneGraphCamera());
					}
				}
				break;
			case 2 :
				if (m_onscreenRenderTarget != null) {
					if (m_renderTargetFactory != null) {
						m_renderTargetFactory.releaseOnscreenRenderTarget(m_onscreenRenderTarget);
					}
					m_onscreenRenderTarget = null;
				}
				break;
		}
		super.internalRelease(pass);
	}

	public edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo pick(int x, int y, boolean isSubElementRequired, boolean isOnlyFrontMostRequired) {
		if (m_onscreenRenderTarget != null) {
			return m_onscreenRenderTarget.pick(x, y, isSubElementRequired, isOnlyFrontMostRequired);
		} else {
			throw new NullPointerException("internal m_onscreenRenderTarget is null");
		}
	}
	public static edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo pick(java.awt.event.MouseEvent mouseEvent) {
		edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo pickInfo = (edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo) s_eventMap.get(mouseEvent);
		if (pickInfo == null) {
			RenderTarget renderTarget = (RenderTarget) s_componentMap.get(mouseEvent.getComponent());
			pickInfo = renderTarget.pick(mouseEvent.getX(), mouseEvent.getY(), false, true);
			if (pickInfo != null) {
				s_eventMap.put(mouseEvent, pickInfo);
			}
		}
		return pickInfo;
	}
	public void commit(edu.cmu.cs.stage3.alice.scenegraph.renderer.RenderTargetFactory renderTargetFactory) {
		m_renderTargetFactory = renderTargetFactory;
		m_onscreenRenderTarget = renderTargetFactory.createOnscreenRenderTarget();
		m_awtComponent = m_onscreenRenderTarget.getAWTComponent();
		s_componentMap.put(m_awtComponent, this);
		java.util.Enumeration enum0 = m_cameras.elements();
		while (enum0.hasMoreElements()) {
			Camera camera = (Camera) enum0.nextElement();
			m_onscreenRenderTarget.addCamera(camera.getSceneGraphCamera());
			m_onscreenRenderTarget.setIsLetterboxedAsOpposedToDistorted(camera.getSceneGraphCamera(), camera.isLetterboxedAsOpposedToDistorted.booleanValue());
		}
	}
	public edu.cmu.cs.stage3.alice.scenegraph.renderer.Renderer getRenderer() {
		return m_onscreenRenderTarget.getRenderer();
	}
	public edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget getInternal() {
		return m_onscreenRenderTarget;
	}
	public void addCamera(Camera camera) {
		if (m_cameras.contains(camera)) {} else {
			m_cameras.addElement(camera);
			m_cameraArray = null;
			if (m_onscreenRenderTarget != null) {
				m_onscreenRenderTarget.addCamera(camera.getSceneGraphCamera());
			}
		}
	}
	public void removeCamera(Camera camera) {
		m_cameras.removeElement(camera);
		m_cameraArray = null;
		if (m_onscreenRenderTarget != null) {
			m_onscreenRenderTarget.removeCamera(camera.getSceneGraphCamera());
		}
	}
	public Camera[] getCameras() {
		if (m_cameraArray == null) {
			m_cameraArray = new Camera[m_cameras.size()];
			m_cameras.copyInto(m_cameraArray);
		}
		return m_cameraArray;
	}

	public double[] getActualPlane(edu.cmu.cs.stage3.alice.core.camera.OrthographicCamera orthographicCamera) {
		if (m_onscreenRenderTarget != null) {
			return m_onscreenRenderTarget.getActualPlane(orthographicCamera.getSceneGraphOrthographicCamera());
		} else {
			return null;
		}
	}
	public double[] getActualPlane(edu.cmu.cs.stage3.alice.core.camera.PerspectiveCamera perspectiveCamera) {
		if (m_onscreenRenderTarget != null) {
			return m_onscreenRenderTarget.getActualPlane(perspectiveCamera.getSceneGraphPerspectiveCamera());
		} else {
			return null;
		}
	}
	public double getActualHorizontalViewingAngle(edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera symmetricPerspectiveCamera) {
		if (m_onscreenRenderTarget != null) {
			return m_onscreenRenderTarget.getActualVerticalViewingAngle(symmetricPerspectiveCamera.getSceneGraphSymmetricPerspectiveCamera());
		} else {
			return Double.NaN;
		}
	}
	public double getActualVerticalViewingAngle(edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera symmetricPerspectiveCamera) {
		if (m_onscreenRenderTarget != null) {
			return m_onscreenRenderTarget.getActualVerticalViewingAngle(symmetricPerspectiveCamera.getSceneGraphSymmetricPerspectiveCamera());
		} else {
			return Double.NaN;
		}
	}

	public javax.vecmath.Matrix4d getProjectionMatrix(Camera camera) {
		if (m_onscreenRenderTarget != null) {
			return m_onscreenRenderTarget.getProjectionMatrix(camera.getSceneGraphCamera());
		} else {
			return null;
		}
	}

	public java.awt.Rectangle getActualViewport(Camera camera) {
		if (m_onscreenRenderTarget != null) {
			return m_onscreenRenderTarget.getActualViewport(camera.getSceneGraphCamera());
		} else {
			return null;
		}
	}
	public java.awt.Rectangle getViewport(Camera camera) {
		if (m_onscreenRenderTarget != null) {
			return m_onscreenRenderTarget.getViewport(camera.getSceneGraphCamera());
		} else {
			return null;
		}
	}
	public void setViewport(Camera camera, java.awt.Rectangle rectangle) {
		if (m_onscreenRenderTarget != null) {
			m_onscreenRenderTarget.setViewport(camera.getSceneGraphCamera(), rectangle);
		}
	}
	public edu.cmu.cs.stage3.math.Vector4 project(edu.cmu.cs.stage3.math.Vector3 point, Camera camera) {
		if (m_onscreenRenderTarget != null) {
			javax.vecmath.Matrix4d projection = m_onscreenRenderTarget.getProjectionMatrix(camera.getSceneGraphCamera());
			edu.cmu.cs.stage3.math.Vector4 xyzw = new edu.cmu.cs.stage3.math.Vector4(point.x, point.y, point.z, 1);
			return edu.cmu.cs.stage3.math.Vector4.multiply(xyzw, projection);
		} else {
			return null;
		}
	}
	public java.awt.Component getAWTComponent() {
		return m_awtComponent;
	}
	public java.awt.Image getOffscreenImage() {
		if (m_onscreenRenderTarget != null) {
			return m_onscreenRenderTarget.getOffscreenImage();
		} else {
			return null;
		}
	}
	public java.awt.Graphics getOffscreenGraphics() {
		if (m_onscreenRenderTarget != null) {
			return m_onscreenRenderTarget.getOffscreenGraphics();
		} else {
			return null;
		}
	}
	public void addRenderTargetListener(edu.cmu.cs.stage3.alice.scenegraph.renderer.event.RenderTargetListener renderTargetListener) {
		if (m_onscreenRenderTarget != null) {
			m_onscreenRenderTarget.addRenderTargetListener(renderTargetListener);
		} else {
			throw new NullPointerException("internal m_onscreenRenderTarget is null");
		}
	}
	public void removeRenderTargetListener(edu.cmu.cs.stage3.alice.scenegraph.renderer.event.RenderTargetListener renderTargetListener) {
		if (m_onscreenRenderTarget != null) {
			m_onscreenRenderTarget.removeRenderTargetListener(renderTargetListener);
		} else {
			throw new NullPointerException("internal m_onscreenRenderTarget is null");
		}
	}
	public edu.cmu.cs.stage3.alice.scenegraph.renderer.event.RenderTargetListener[] getRenderTargetListeners() {
		if (m_onscreenRenderTarget != null) {
			return m_onscreenRenderTarget.getRenderTargetListeners();
		} else {
			throw new NullPointerException("internal m_onscreenRenderTarget is null");
		}
	}

	public void addKeyListener(java.awt.event.KeyListener keyListener) {
		if (m_awtComponent != null) {
			m_awtComponent.addKeyListener(keyListener);
		} else {
			throw new NullPointerException("internal m_awtComponent is null");
		}
	}
	public void removeKeyListener(java.awt.event.KeyListener keyListener) {
		if (m_awtComponent != null) {
			m_awtComponent.removeKeyListener(keyListener);
		} else {
			throw new NullPointerException("internal m_awtComponent is null");
		}
	}
	public void addMouseListener(java.awt.event.MouseListener mouseListener) {
		if (m_awtComponent != null) {
			m_awtComponent.addMouseListener(mouseListener);
		} else {
			throw new NullPointerException("internal m_awtComponent is null");
		}
	}
	public void removeMouseListener(java.awt.event.MouseListener mouseListener) {
		if (m_awtComponent != null) {
			m_awtComponent.removeMouseListener(mouseListener);
		} else {
			throw new NullPointerException("internal m_awtComponent is null");
		}
	}
	public void addMouseMotionListener(java.awt.event.MouseMotionListener mouseMotionListener) {
		if (m_awtComponent != null) {
			m_awtComponent.addMouseMotionListener(mouseMotionListener);
		} else {
			throw new NullPointerException("internal m_awtComponent is null");
		}
	}
	public void removeMouseMotionListener(java.awt.event.MouseMotionListener mouseMotionListener) {
		if (m_awtComponent != null) {
			m_awtComponent.removeMouseMotionListener(mouseMotionListener);
		} else {
			throw new NullPointerException("internal m_awtComponent is null");
		}
	}

	@Override
	protected void started(World world, double time) {
		super.started(world, time);
		m_onscreenRenderTarget.addRenderTargetListener(world.getBubbleManager());
		java.awt.Component awtComponent = m_onscreenRenderTarget.getAWTComponent();
		if (awtComponent != null) {
			awtComponent.requestFocus();
		}
	}

	@Override
	protected void stopped(World world, double time) {
		super.stopped(world, time);
		m_onscreenRenderTarget.removeRenderTargetListener(world.getBubbleManager());
	}
}
