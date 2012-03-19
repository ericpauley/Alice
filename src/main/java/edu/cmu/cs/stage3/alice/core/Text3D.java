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

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Stage3</p>
 * @author Ben Buchwald
 * @version 1.0
 */

import edu.cmu.cs.stage3.alice.core.property.FontProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;

public class Text3D extends Model {
	public final StringProperty text = new StringProperty(this, "text", null);
	public final FontProperty font = new FontProperty(this, "font", null);
	public final NumberProperty extrusion = new NumberProperty(this, "extrusion", new Double(0.25));
	public final NumberProperty curvature = new NumberProperty(this, "curvature", new Integer(2));

	public void create3DTextGeometry() {
		edu.cmu.cs.stage3.alice.core.geometry.Text3D geom = new edu.cmu.cs.stage3.alice.core.geometry.Text3D();
		geom.curvature.set(curvature.get());
		geom.font.set(font.get());
		geom.text.set(text.get());
		addChild(geom);
		geometry.set(geom);
	}

	@Override
	protected void propertyChanged(edu.cmu.cs.stage3.alice.core.Property property, Object value) {
		if (geometry.getGeometryValue() != null && geometry.getGeometryValue() instanceof edu.cmu.cs.stage3.alice.core.geometry.Text3D) {
			if (property == text) {
				((edu.cmu.cs.stage3.alice.core.geometry.Text3D) geometry.getGeometryValue()).text.set(value);
			} else if (property == font) {
				((edu.cmu.cs.stage3.alice.core.geometry.Text3D) geometry.getGeometryValue()).font.set(value);
			} else if (property == extrusion) {
				((edu.cmu.cs.stage3.alice.core.geometry.Text3D) geometry.getGeometryValue()).extrusion.set(value);
			} else if (property == curvature) {
				((edu.cmu.cs.stage3.alice.core.geometry.Text3D) geometry.getGeometryValue()).curvature.set(value);
			} else {
				super.propertyChanged(property, value);
			}
		} else {
			super.propertyChanged(property, value);
		}
	}

	@Override
	protected void propertyChanging(edu.cmu.cs.stage3.alice.core.Property property, Object value) {
		if (property == geometry && value != null && !(value instanceof edu.cmu.cs.stage3.alice.core.geometry.Text3D)) {
			throw new java.lang.ClassCastException("A 3D text model's geometry must be a 3D text geometry");
		} else {
			super.propertyChanging(property, value);
		}
	}
}