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

public class OffscreenRenderTarget extends RenderTarget implements edu.cmu.cs.stage3.alice.scenegraph.renderer.OffscreenRenderTarget {
	private java.awt.Dimension m_size = new java.awt.Dimension();
	protected OffscreenRenderTarget(Renderer renderer) {
		super(renderer);
	}

	@Override
	public java.awt.Dimension getSize() {
		return m_size;
	}
	@Override
	public java.awt.Dimension getSize(java.awt.Dimension rv) {
		rv.width = m_size.width;
		rv.height = m_size.height;
		return rv;
	}
	@Override
	public void setSize(int width, int height) {
		m_size.width = width;
		m_size.height = height;
		getAdapter().setDesiredSize(width, height);
	}
	@Override
	public void setSize(java.awt.Dimension size) {
		setSize(size.width, size.height);
	}
}
