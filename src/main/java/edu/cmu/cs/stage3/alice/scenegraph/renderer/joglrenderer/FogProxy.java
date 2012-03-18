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

abstract class FogProxy extends AffectorProxy {
    private float[] m_color = new float[ 4 ];
    private java.nio.FloatBuffer m_colorBuffer = java.nio.FloatBuffer.wrap( m_color );
	
	protected void changed( edu.cmu.cs.stage3.alice.scenegraph.Property property, Object value ) {
		if( property == edu.cmu.cs.stage3.alice.scenegraph.Fog.COLOR_PROPERTY ) {
			copy( m_color, (edu.cmu.cs.stage3.alice.scenegraph.Color)value );
		} else {
			super.changed( property, value );
		}
	}
    
	public void setup( RenderContext context ) {
        context.setIsFogEnabled( true );
        context.gl.glFogfv( javax.media.opengl.GL.GL_FOG_COLOR, m_colorBuffer );
    }
}
