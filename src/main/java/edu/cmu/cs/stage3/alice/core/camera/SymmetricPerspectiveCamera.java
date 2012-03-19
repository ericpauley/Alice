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

public class SymmetricPerspectiveCamera extends edu.cmu.cs.stage3.alice.core.Camera {
	public final NumberProperty verticalViewingAngle = new NumberProperty(this, "verticalViewingAngle", null);
	public final NumberProperty horizontalViewingAngle = new NumberProperty(this, "horizontalViewingAngle", null);
	public SymmetricPerspectiveCamera() {
		super(new edu.cmu.cs.stage3.alice.scenegraph.SymmetricPerspectiveCamera());
	}
	public edu.cmu.cs.stage3.alice.scenegraph.SymmetricPerspectiveCamera getSceneGraphSymmetricPerspectiveCamera() {
		return (edu.cmu.cs.stage3.alice.scenegraph.SymmetricPerspectiveCamera) getSceneGraphCamera();
	}

	@Override
	protected edu.cmu.cs.stage3.alice.core.decorator.ViewVolumeDecorator createViewVolumeDecorator() {
		edu.cmu.cs.stage3.alice.core.decorator.SymmetricPerspectiveViewVolumeDecorator symmetricPerspectiveViewVolumeDecorator = new edu.cmu.cs.stage3.alice.core.decorator.SymmetricPerspectiveViewVolumeDecorator();
		symmetricPerspectiveViewVolumeDecorator.setSymmetricPerspectiveCamera(this);
		return symmetricPerspectiveViewVolumeDecorator;
	}
	protected void verticalViewingAngleValueChanged(Number value) {
		double d = Double.NaN;
		if (value != null) {
			d = value.doubleValue();
		}
		getSceneGraphSymmetricPerspectiveCamera().setVerticalViewingAngle(d);
	}
	protected void horizontalViewingAngleValueChanged(Number value) {
		double d = Double.NaN;
		if (value != null) {
			d = value.doubleValue();
		}
		getSceneGraphSymmetricPerspectiveCamera().setHorizontalViewingAngle(d);
	}

	@Override
	protected void propertyChanged(edu.cmu.cs.stage3.alice.core.Property property, Object value) {
		if (property == verticalViewingAngle) {
			verticalViewingAngleValueChanged((Number) value);
		} else if (property == horizontalViewingAngle) {
			horizontalViewingAngleValueChanged((Number) value);
		} else {
			super.propertyChanged(property, value);
		}
	}

	@Override
	protected double getViewingAngleForGetAGoodLookAt() {
		double vva = verticalViewingAngle.doubleValue();
		double hva = horizontalViewingAngle.doubleValue();
		if (Double.isNaN(vva)) {
			if (Double.isNaN(hva)) {
				return super.getViewingAngleForGetAGoodLookAt();
			} else {
				return vva;
			}
		} else {
			if (Double.isNaN(hva)) {
				return vva;
			} else {
				return Math.min(vva, hva);
			}
		}
	}
}