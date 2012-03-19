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

public class PointLight extends edu.cmu.cs.stage3.alice.core.Light {
	public final NumberProperty constantAttenuation = new NumberProperty(this, "constantAttenuation", new Double(1));
	public final NumberProperty linearAttenuation = new NumberProperty(this, "linearAttenuation", new Double(0));
	public final NumberProperty quadraticAttenuation = new NumberProperty(this, "quadraticAttenuation", new Double(0));
	protected PointLight(edu.cmu.cs.stage3.alice.scenegraph.PointLight sgLight) {
		super(sgLight);
		constantAttenuation.set(new Double(getSceneGraphPointLight().getConstantAttenuation()));
		linearAttenuation.set(new Double(getSceneGraphPointLight().getLinearAttenuation()));
		quadraticAttenuation.set(new Double(getSceneGraphPointLight().getQuadraticAttenuation()));
	}
	public PointLight() {
		this(new edu.cmu.cs.stage3.alice.scenegraph.PointLight());
	}
	public edu.cmu.cs.stage3.alice.scenegraph.PointLight getSceneGraphPointLight() {
		return (edu.cmu.cs.stage3.alice.scenegraph.PointLight) getSceneGraphLight();
	}
	private void constantAttenuationValueChanged(Number value) {
		double d = Double.NaN;
		if (value != null) {
			d = value.doubleValue();
		}
		getSceneGraphPointLight().setConstantAttenuation(d);
	}
	private void linearAttenuationValueChanged(Number value) {
		double d = Double.NaN;
		if (value != null) {
			d = value.doubleValue();
		}
		getSceneGraphPointLight().setLinearAttenuation(d);
	}
	private void quadraticAttenuationValueChanged(Number value) {
		double d = Double.NaN;
		if (value != null) {
			d = value.doubleValue();
		}
		getSceneGraphPointLight().setQuadraticAttenuation(d);
	}

	@Override
	protected void propertyChanged(edu.cmu.cs.stage3.alice.core.Property property, Object value) {
		if (property == constantAttenuation) {
			constantAttenuationValueChanged((Number) value);
		} else if (property == linearAttenuation) {
			linearAttenuationValueChanged((Number) value);
		} else if (property == quadraticAttenuation) {
			quadraticAttenuationValueChanged((Number) value);
		} else {
			super.propertyChanged(property, value);
		}
	}
}
