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

package edu.cmu.cs.stage3.alice.core.camera;

import edu.cmu.cs.stage3.alice.core.property.NumberProperty;

public class PerspectiveCamera extends edu.cmu.cs.stage3.alice.core.Camera {
	public final NumberProperty minimumX = new NumberProperty(this, "minimumX", null);
	public final NumberProperty minimumY = new NumberProperty(this, "minimumY", null);
	public final NumberProperty maximumX = new NumberProperty(this, "maximumX", null);
	public final NumberProperty maximumY = new NumberProperty(this, "maximumY", null);
	public PerspectiveCamera() {
		super(new edu.cmu.cs.stage3.alice.scenegraph.PerspectiveCamera());
	}
	public edu.cmu.cs.stage3.alice.scenegraph.PerspectiveCamera getSceneGraphPerspectiveCamera() {
		return (edu.cmu.cs.stage3.alice.scenegraph.PerspectiveCamera) getSceneGraphCamera();
	}

	@Override
	protected edu.cmu.cs.stage3.alice.core.decorator.ViewVolumeDecorator createViewVolumeDecorator() {
		edu.cmu.cs.stage3.alice.core.decorator.PerspectiveViewVolumeDecorator perspectiveViewVolumeDecorator = new edu.cmu.cs.stage3.alice.core.decorator.PerspectiveViewVolumeDecorator();
		perspectiveViewVolumeDecorator.setPerspectiveCamera(this);
		return perspectiveViewVolumeDecorator;
	}
	private void minimumXValueChanged(Number value) {
		double[] plane = getSceneGraphPerspectiveCamera().getPlane().clone();
		plane[0] = Double.NaN;
		if (value != null) {
			plane[0] = value.doubleValue();
		}
		getSceneGraphPerspectiveCamera().setPlane(plane);
	}
	private void minimumYValueChanged(Number value) {
		double[] plane = getSceneGraphPerspectiveCamera().getPlane().clone();
		plane[1] = Double.NaN;
		if (value != null) {
			plane[1] = value.doubleValue();
		}
		getSceneGraphPerspectiveCamera().setPlane(plane);
	}
	private void maximumXValueChanged(Number value) {
		double[] plane = getSceneGraphPerspectiveCamera().getPlane().clone();
		plane[2] = Double.NaN;
		if (value != null) {
			plane[2] = value.doubleValue();
		}
		getSceneGraphPerspectiveCamera().setPlane(plane);
	}
	private void maximumYValueChanged(Number value) {
		double[] plane = getSceneGraphPerspectiveCamera().getPlane().clone();
		plane[3] = Double.NaN;
		if (value != null) {
			plane[3] = value.doubleValue();
		}
		getSceneGraphPerspectiveCamera().setPlane(plane);
	}

	@Override
	protected void propertyChanged(edu.cmu.cs.stage3.alice.core.Property property, Object value) {
		if (property == minimumX) {
			minimumXValueChanged((Number) value);
		} else if (property == minimumY) {
			minimumYValueChanged((Number) value);
		} else if (property == maximumX) {
			maximumXValueChanged((Number) value);
		} else if (property == maximumY) {
			maximumYValueChanged((Number) value);
		} else {
			super.propertyChanged(property, value);
		}
	}
}