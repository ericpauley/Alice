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

public abstract class BackgroundProxy extends ElementProxy {
	protected abstract void onColorChange(double r, double g, double b, double a);
	protected abstract void onTextureMapChange(TextureMapProxy value);
	protected abstract void onTextureMapSourceRectangleChange(int x, int y, int width, int height);

	@Override
	protected void changed(edu.cmu.cs.stage3.alice.scenegraph.Property property, Object value) {
		if (property == edu.cmu.cs.stage3.alice.scenegraph.Background.COLOR_PROPERTY) {
			edu.cmu.cs.stage3.alice.scenegraph.Color color = (edu.cmu.cs.stage3.alice.scenegraph.Color) value;
			if (color != null) {
				onColorChange(color.red, color.green, color.blue, color.alpha);
			} else {
				onColorChange(0, 0, 0, 0);
			}
		} else if (property == edu.cmu.cs.stage3.alice.scenegraph.Background.TEXTURE_MAP_PROPERTY) {
			onTextureMapChange((TextureMapProxy) getProxyFor((edu.cmu.cs.stage3.alice.scenegraph.TextureMap) value));
		} else if (property == edu.cmu.cs.stage3.alice.scenegraph.Background.TEXTURE_MAP_SOURCE_RECTANGLE_PROPERTY) {
			java.awt.Rectangle rect = (java.awt.Rectangle) value;
			if (rect != null) {
				onTextureMapSourceRectangleChange(rect.x, rect.y, rect.width, rect.height);
			} else {
				onTextureMapSourceRectangleChange(0, 0, 1, 1);
			}
		} else {
			super.changed(property, value);
		}
	}
}
