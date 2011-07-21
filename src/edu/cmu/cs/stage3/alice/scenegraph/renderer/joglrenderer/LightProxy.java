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

abstract class LightProxy extends AffectorProxy {
    private float[] m_colorTimesBrightness = new float[ 4 ];
    private float[] m_color = new float[ 4 ];
    private float m_brightness;
    private float m_range;
    
    private float[] reuse_position = new float[ 4 ];
    private float[] reuse_spotDirection = new float[ 3 ];

    private java.nio.FloatBuffer reuse_positionBuffer = java.nio.FloatBuffer.wrap( reuse_position );
    private java.nio.FloatBuffer reuse_spotDirectionBuffer = java.nio.FloatBuffer.wrap( reuse_spotDirection );
    
    private java.nio.FloatBuffer m_colorTimesBrightnessBuffer = java.nio.FloatBuffer.wrap( m_colorTimesBrightness );

    protected float[] getPosition( float[] rv ) {
        rv[ 0 ] = 0;
        rv[ 1 ] = 0;
        rv[ 2 ] = -1;
        rv[ 3 ] = 0;
        return rv;
    }
    protected float[] getSpotDirection( float[] rv ) {
        rv[ 0 ] = 0;
        rv[ 1 ] = 0;
        rv[ 2 ] = 1;
        return rv;
    }
    protected float getSpotExponent() {
        return 0;
    }
    protected float getSpotCutoff() {
        return 180;
    }
    protected float getConstantAttenuation() {
        return 1;
    }
    protected float getLinearAttenuation() {
        return 0;
    }
    protected float getQuadraticAttenuation() {
        return 0;
    }

    protected void setup( RenderContext context, int id ) {
        context.gl.glEnable( id );

        //there should never be a need to set GL_AMBIENT
        //context.gl.glLightfv( id, GL.GL_AMBIENT, { 0, 0, 0, 1 } );
        
        context.gl.glLightfv( id, GL.GL_DIFFUSE, m_colorTimesBrightnessBuffer );
        
        //todo: should lights' diffuse and specular colors be separated in the scenegraph?
        context.gl.glLightfv( id, GL.GL_SPECULAR, m_colorTimesBrightnessBuffer );
        
        getPosition( reuse_position );
        context.gl.glLightfv( id, GL.GL_POSITION, reuse_positionBuffer );
        
        getSpotDirection( reuse_spotDirection );
        context.gl.glLightfv( id, GL.GL_SPOT_DIRECTION, reuse_spotDirectionBuffer );

        context.gl.glLightf( id, GL.GL_SPOT_EXPONENT, getSpotExponent() );
        context.gl.glLightf( id, GL.GL_SPOT_CUTOFF, getSpotCutoff() );
        context.gl.glLightf( id, GL.GL_CONSTANT_ATTENUATION, getConstantAttenuation() );
        context.gl.glLightf( id, GL.GL_LINEAR_ATTENUATION, getLinearAttenuation() );
        context.gl.glLightf( id, GL.GL_QUADRATIC_ATTENUATION, getQuadraticAttenuation() );
    }
    
	public void setup( RenderContext context ) {
        if( this instanceof AmbientLightProxy ) {
            context.addAmbient( m_colorTimesBrightness );
        } else {
            int id = context.getNextLightID();
            setup( context, id );
        }
    }
    
    private void updateColorTimesBrightness() {
        m_colorTimesBrightness[ 0 ] = m_color[ 0 ] * m_brightness;
        m_colorTimesBrightness[ 1 ] = m_color[ 1 ] * m_brightness;
        m_colorTimesBrightness[ 2 ] = m_color[ 2 ] * m_brightness;
        m_colorTimesBrightness[ 3 ] = 1;
    }

    
	protected void changed( edu.cmu.cs.stage3.alice.scenegraph.Property property, Object value ) {
    	if( property == edu.cmu.cs.stage3.alice.scenegraph.Light.COLOR_PROPERTY ) {
			copy( m_color, (edu.cmu.cs.stage3.alice.scenegraph.Color)value );
			updateColorTimesBrightness();
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Light.BRIGHTNESS_PROPERTY ) {
		    m_brightness = ((Number)value).floatValue();
			updateColorTimesBrightness();
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Light.RANGE_PROPERTY ) {
		    m_range = ((Number)value).floatValue();
		} else {
			super.changed( property, value );
		}
	}
}
