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

import javax.media.opengl.GL;

class BackgroundProxy extends ElementProxy {
	private float[] m_color = new float[4];
	public void clear(RenderContext context) {
		context.gl.glClearColor(m_color[0], m_color[1], m_color[2], m_color[3]);
		context.gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
	}

	@Override
	protected void changed(edu.cmu.cs.stage3.alice.scenegraph.Property property, Object value) {
		if (property == edu.cmu.cs.stage3.alice.scenegraph.Background.COLOR_PROPERTY) {
			copy(m_color, (edu.cmu.cs.stage3.alice.scenegraph.Color) value);
		} else if (property == edu.cmu.cs.stage3.alice.scenegraph.Background.TEXTURE_MAP_PROPERTY) {
			// todo
		} else if (property == edu.cmu.cs.stage3.alice.scenegraph.Background.TEXTURE_MAP_SOURCE_RECTANGLE_PROPERTY) {
			// todo
		} else {
			super.changed(property, value);
		}
	}
}
