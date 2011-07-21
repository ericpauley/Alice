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

package edu.cmu.cs.stage3.alice.scenegraph.renderer.nullrenderer;

public class OnscreenRenderTarget extends RenderTarget implements edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget {
	private java.awt.Canvas m_canvas = new java.awt.Canvas();
	OnscreenRenderTarget( Renderer renderer ) {
		super( renderer );
	}
	public java.awt.Dimension getSize( java.awt.Dimension rv ) {
        java.awt.Component awtComponent = getAWTComponent();
		if( awtComponent != null ) {
			awtComponent.getSize( rv );
		} else {
			rv.width = 0;
            rv.height = 0;
		}
        return rv;
    }
	public java.awt.Component getAWTComponent() {
		return m_canvas;
	}
}
