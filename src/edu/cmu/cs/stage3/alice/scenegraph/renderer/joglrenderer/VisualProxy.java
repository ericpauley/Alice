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

class VisualProxy extends ComponentProxy {
    private AppearanceProxy m_frontFacingAppearanceProxy = null;
    private AppearanceProxy m_backFacingAppearanceProxy = null;
    private GeometryProxy m_geometryProxy = null;
    private boolean m_isShowing = false;
    private double[] m_scale = new double[ 16 ];

    private java.nio.DoubleBuffer m_scaleBuffer = java.nio.DoubleBuffer.wrap( m_scale );
    public edu.cmu.cs.stage3.alice.scenegraph.Visual getSceneGraphVisual() {
	    return (edu.cmu.cs.stage3.alice.scenegraph.Visual)getSceneGraphElement();
	}

    
	protected void changed( edu.cmu.cs.stage3.alice.scenegraph.Property property, Object value ) {
		if( property == edu.cmu.cs.stage3.alice.scenegraph.Visual.FRONT_FACING_APPEARANCE_PROPERTY ) {
            m_frontFacingAppearanceProxy = (AppearanceProxy)getProxyFor( (edu.cmu.cs.stage3.alice.scenegraph.Appearance)value );
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Visual.BACK_FACING_APPEARANCE_PROPERTY ) {
            m_backFacingAppearanceProxy = (AppearanceProxy)getProxyFor( (edu.cmu.cs.stage3.alice.scenegraph.Appearance)value );
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Visual.GEOMETRY_PROPERTY ) {
            m_geometryProxy = (GeometryProxy)getProxyFor( (edu.cmu.cs.stage3.alice.scenegraph.Geometry)value );
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Visual.SCALE_PROPERTY ) {
		    copy( m_scale, (javax.vecmath.Matrix3d)value );
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Visual.IS_SHOWING_PROPERTY ) {
            m_isShowing = value!=null && ((Boolean)value).booleanValue();
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Visual.DISABLED_AFFECTORS_PROPERTY ) {
            //todo
		} else {
			super.changed( property, value );
		}
	}
    
	public void setup( RenderContext context ) {
        //pass
    }
    
   
    
    private double opacity() {
        if( m_isShowing && m_geometryProxy != null ) {
            if( m_frontFacingAppearanceProxy != null ) {
               return m_frontFacingAppearanceProxy.Showing();
            }
            if( m_backFacingAppearanceProxy != null ) {
                return m_backFacingAppearanceProxy.Showing();
            }
        }
        return 0.0;
    }
    
	public void render( RenderContext context ) {
        if( opacity()>0.0) {
        	if(opacity()<1.0 && context.renderOpaque())
        		return;
         	else if(opacity()==1.0 && !context.renderOpaque())
          		return;
            if( m_frontFacingAppearanceProxy != null ) {
                if( m_backFacingAppearanceProxy != null ) {   	
                    context.gl.glDisable( GL.GL_CULL_FACE );
                } else {
                    context.gl.glEnable( GL.GL_CULL_FACE );
    			    context.gl.glCullFace( GL.GL_BACK );
                }
            } else {
                if( m_backFacingAppearanceProxy != null ) {
                    context.gl.glEnable( GL.GL_CULL_FACE );
    			    context.gl.glCullFace( GL.GL_FRONT );
                } else {
                    //should never reach here
                }
            }
            	
        	
            if( m_frontFacingAppearanceProxy == m_backFacingAppearanceProxy ) {
                if( m_frontFacingAppearanceProxy != null ) {
                    m_frontFacingAppearanceProxy.setPipelineState( context, GL.GL_FRONT_AND_BACK );
                } else {
                    //should never reach here
                }
            } else {
                if( m_frontFacingAppearanceProxy != null ) {
                    m_frontFacingAppearanceProxy.setPipelineState( context, GL.GL_FRONT );
                }
                if( m_backFacingAppearanceProxy != null ) {
                    m_backFacingAppearanceProxy.setPipelineState( context, GL.GL_BACK );
                }
            }
            
            
            context.gl.glPushMatrix();
            context.gl.glMultMatrixd( m_scaleBuffer );
            m_geometryProxy.render( context );
            context.gl.glPopMatrix();
          
            context.gl.glDepthMask(true);
        	
       }
    }
	
	public void pick( PickContext context, PickParameters pickParameters ) {
        if( opacity()>0.0 ) {
	        context.gl.glPushMatrix();
	        context.gl.glMultMatrixd( m_scaleBuffer );

	        context.gl.glPushName( context.getPickNameForVisualProxy( this ) );
		    context.gl.glEnable( GL.GL_CULL_FACE );
			if( m_backFacingAppearanceProxy != null ) {
			    context.gl.glCullFace( GL.GL_FRONT );
			    context.gl.glPushName( 0 );
		        m_geometryProxy.pick( context, pickParameters.isSubElementRequired() );
			    context.gl.glPopName();
			}
			if( m_frontFacingAppearanceProxy != null ) {
			    context.gl.glCullFace( GL.GL_BACK );
			    context.gl.glPushName( 1 );
		        m_geometryProxy.pick( context, pickParameters.isSubElementRequired() );
			    context.gl.glPopName();
			}
	        context.gl.glPopName();
	        context.gl.glPopMatrix();
        }
	}
}
