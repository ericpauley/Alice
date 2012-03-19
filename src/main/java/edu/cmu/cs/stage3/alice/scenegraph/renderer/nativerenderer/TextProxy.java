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

package edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer;

public abstract class TextProxy extends GeometryProxy {
	protected abstract void onTextChange(java.lang.String value);
	protected abstract void onFontChange(String name, int style, int size);
	protected abstract void onExtrusionChange(double x, double y, double z);

	@Override
	protected void changed(edu.cmu.cs.stage3.alice.scenegraph.Property property, Object value) {
		if (property == edu.cmu.cs.stage3.alice.scenegraph.Text.TEXT_PROPERTY) {
			onTextChange((java.lang.String) value);
		} else if (property == edu.cmu.cs.stage3.alice.scenegraph.Text.FONT_PROPERTY) {
			java.awt.Font font = (java.awt.Font) value;
			onFontChange(font.getName(), font.getStyle(), font.getSize());
		} else if (property == edu.cmu.cs.stage3.alice.scenegraph.Text.EXTRUSION_PROPERTY) {
			javax.vecmath.Vector3d v = (javax.vecmath.Vector3d) value;
			onExtrusionChange(v.x, v.y, v.z);
		} else {
			super.changed(property, value);
		}
	}
}
