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

package edu.cmu.cs.stage3.alice.core.light;

import edu.cmu.cs.stage3.alice.core.property.NumberProperty;

public class SpotLight extends PointLight {
	public final NumberProperty innerBeamAngle = new NumberProperty(this, "innerBeamAngle", new Double(0.4));
	public final NumberProperty outerBeamAngle = new NumberProperty(this, "outerBeamAngle", new Double(0.5));
	public SpotLight() {
		super(new edu.cmu.cs.stage3.alice.scenegraph.SpotLight());
		innerBeamAngle.set(new Double(getSceneGraphSpotLight().getInnerBeamAngle()));
		outerBeamAngle.set(new Double(getSceneGraphSpotLight().getOuterBeamAngle()));
	}
	public edu.cmu.cs.stage3.alice.scenegraph.SpotLight getSceneGraphSpotLight() {
		return (edu.cmu.cs.stage3.alice.scenegraph.SpotLight) getSceneGraphPointLight();
	}

	private void innerBeamAngleValueChanged(Number value) {
		double d = Double.NaN;
		if (value != null) {
			d = value.doubleValue();
		}
		getSceneGraphSpotLight().setInnerBeamAngle(d);
	}
	private void outerBeamAngleValueChanged(Number value) {
		double d = Double.NaN;
		if (value != null) {
			d = value.doubleValue();
		}
		getSceneGraphSpotLight().setOuterBeamAngle(d);
	}

	@Override
	protected void propertyChanged(edu.cmu.cs.stage3.alice.core.Property property, Object value) {
		if (property == innerBeamAngle) {
			innerBeamAngleValueChanged((Number) value);
		} else if (property == outerBeamAngle) {
			outerBeamAngleValueChanged((Number) value);
		} else {
			super.propertyChanged(property, value);
		}
	}
}
