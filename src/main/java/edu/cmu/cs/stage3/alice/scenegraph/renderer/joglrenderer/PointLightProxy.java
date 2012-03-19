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

class PointLightProxy extends LightProxy {
	private float m_constant;
	private float m_linear;
	private float m_quadratic;

	@Override
	protected float[] getPosition(float[] rv) {
		double[] absolute = getAbsoluteTransformation();
		rv[0] = (float) absolute[12];
		rv[1] = (float) absolute[13];
		rv[2] = (float) absolute[14];
		rv[3] = 1;
		return rv;
	}

	@Override
	protected float getConstantAttenuation() {
		return m_constant;
	}

	@Override
	protected float getLinearAttenuation() {
		return m_linear;
	}

	@Override
	protected float getQuadraticAttenuation() {
		return m_quadratic;
	}

	@Override
	protected void changed(edu.cmu.cs.stage3.alice.scenegraph.Property property, Object value) {
		if (property == edu.cmu.cs.stage3.alice.scenegraph.PointLight.CONSTANT_ATTENUATION_PROPERTY) {
			m_constant = ((Number) value).floatValue();
		} else if (property == edu.cmu.cs.stage3.alice.scenegraph.PointLight.LINEAR_ATTENUATION_PROPERTY) {
			m_linear = ((Number) value).floatValue();
		} else if (property == edu.cmu.cs.stage3.alice.scenegraph.PointLight.QUADRATIC_ATTENUATION_PROPERTY) {
			m_quadratic = ((Number) value).floatValue();
		} else {
			super.changed(property, value);
		}
	}
}
