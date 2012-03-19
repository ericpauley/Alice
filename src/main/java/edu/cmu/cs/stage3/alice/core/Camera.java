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

import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.RenderTargetProperty;

public abstract class Camera extends Model /* Transformable */{
	public final BooleanProperty isLetterboxedAsOpposedToDistorted = new BooleanProperty(this, "isLetterboxedAsOpposedToDistorted", Boolean.TRUE);
	public final NumberProperty nearClippingPlaneDistance = new NumberProperty(this, "nearClippingPlaneDistance", new Double(0.1));
	public final NumberProperty farClippingPlaneDistance = new NumberProperty(this, "farClippingPlaneDistance", new Double(100.0));
	public final RenderTargetProperty renderTarget = new RenderTargetProperty(this, "renderTarget", null);
	public final BooleanProperty isViewVolumeShowing = new BooleanProperty(this, "isViewVolumeShowing", Boolean.FALSE);
	private edu.cmu.cs.stage3.alice.scenegraph.Camera m_sgCamera;
	private edu.cmu.cs.stage3.alice.core.decorator.ViewVolumeDecorator m_viewVolumeDecorator;
	protected Camera(edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera) {
		super();
		m_sgCamera = sgCamera;
		m_sgCamera.setParent(getSceneGraphTransformable());
		m_sgCamera.setBonus(this);
		nearClippingPlaneDistance.set(new Double(m_sgCamera.getNearClippingPlaneDistance()));
		farClippingPlaneDistance.set(new Double(m_sgCamera.getFarClippingPlaneDistance()));
		m_viewVolumeDecorator = createViewVolumeDecorator();
	}

	@Override
	protected void internalRelease(int pass) {
		switch (pass) {
			case 1 :
				m_sgCamera.setParent(null);
				m_viewVolumeDecorator.internalRelease(1);
				break;
			case 2 :
				m_sgCamera.release();
				m_sgCamera = null;
				m_viewVolumeDecorator.internalRelease(2);
				m_viewVolumeDecorator = null;
				break;
		}
		super.internalRelease(pass);
	}

	public edu.cmu.cs.stage3.alice.scenegraph.Camera getSceneGraphCamera() {
		return m_sgCamera;
	}
	protected abstract edu.cmu.cs.stage3.alice.core.decorator.ViewVolumeDecorator createViewVolumeDecorator();

	@Override
	protected void nameValueChanged(String value) {
		super.nameValueChanged(value);
		String s = null;
		if (value != null) {
			s = value + ".m_sgCamera";
		}
		m_sgCamera.setName(s);
	}
	private void nearClippingPlaneDistanceValueChanged(Number value) {
		double d = Double.NaN;
		if (value != null) {
			d = value.doubleValue();
		}
		m_sgCamera.setNearClippingPlaneDistance(d);
	}
	private void farClippingPlaneDistanceValueChanged(Number value) {
		double d = Double.NaN;
		if (value != null) {
			d = value.doubleValue();
		}
		m_sgCamera.setFarClippingPlaneDistance(d);
	}
	private void renderTargetValueChanging(RenderTarget renderTargetValueToBe) {
		RenderTarget renderTargetValue = (RenderTarget) renderTarget.getValue();
		if (renderTargetValue != null) {
			renderTargetValue.removeCamera(this);
		}
	}
	private void renderTargetValueChanged(RenderTarget renderTargetValue) {
		if (renderTargetValue != null) {
			renderTargetValue.addCamera(this);
		}
	}
	private void isViewVolumeShowingValueChanged(Boolean value) {
		if (m_viewVolumeDecorator != null) {
			m_viewVolumeDecorator.setIsShowing(value);
		}
	}

	@Override
	protected void propertyChanging(Property property, Object value) {
		if (property == nearClippingPlaneDistance) {
			// pass
		} else if (property == farClippingPlaneDistance) {
			// pass
		} else if (property == renderTarget) {
			renderTargetValueChanging((RenderTarget) value);
		} else if (property == isViewVolumeShowing) {
			// pass
		} else {
			super.propertyChanging(property, value);
		}
	}

	@Override
	protected void propertyChanged(Property property, Object value) {
		if (property == nearClippingPlaneDistance) {
			nearClippingPlaneDistanceValueChanged((Number) value);
		} else if (property == farClippingPlaneDistance) {
			farClippingPlaneDistanceValueChanged((Number) value);
		} else if (property == renderTarget) {
			renderTargetValueChanged((RenderTarget) value);
		} else if (property == isViewVolumeShowing) {
			isViewVolumeShowingValueChanged((Boolean) value);
		} else {
			super.propertyChanged(property, value);
		}
	}

	public edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo pick(int x, int y) {
		RenderTarget renderTargetValue = (RenderTarget) renderTarget.getValue();
		if (renderTargetValue != null) {
			return renderTargetValue.getInternal().pick(x, y, edu.cmu.cs.stage3.alice.scenegraph.renderer.RenderTarget.SUB_ELEMENT_IS_NOT_REQUIRED, edu.cmu.cs.stage3.alice.scenegraph.renderer.RenderTarget.ONLY_FRONT_MOST_VISUAL_IS_REQUIRED);
		} else {
			return null;
		}
	}
	public edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo pick(java.awt.Point p) {
		return pick(p.x, p.y);
	}

	public java.awt.Image takePicture(int width, int height) {
		java.awt.Image image = null;
		World world = getWorld();
		if (world != null) {
			edu.cmu.cs.stage3.alice.scenegraph.renderer.RenderTargetFactory renderTargetFactory = world.getRenderTargetFactory();
			if (renderTargetFactory != null) {
				edu.cmu.cs.stage3.alice.scenegraph.renderer.OffscreenRenderTarget offscreenRenderTarget = renderTargetFactory.createOffscreenRenderTarget();
				offscreenRenderTarget.setSize(width, height);
				offscreenRenderTarget.addCamera(getSceneGraphCamera());
				offscreenRenderTarget.clearAndRenderOffscreen();
				image = offscreenRenderTarget.getOffscreenImage();
				offscreenRenderTarget.removeCamera(getSceneGraphCamera());
				offscreenRenderTarget.release();
			}
		}
		return image;
	}

	public boolean canSee(Model model, boolean checkForOcclusion) {
		// if( checkForOcclusion ) {
		// todo
		// } else {
		if (model.getSceneGraphVisual().isInProjectionVolumeOf(getSceneGraphCamera())) {
			return true;
		}
		// }
		for (int i = 0; i < model.parts.size(); i++) {
			Object v = model.parts.get(i);
			if (v instanceof Model) {
				if (canSee((Model) v, checkForOcclusion)) {
					return true;
				}
			}
		}
		return false;
	}
}
