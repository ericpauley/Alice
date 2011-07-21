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

class SymmetricPerspectiveCameraProxy extends CameraProxy {
    private double m_vertical;
    private double m_horizontal;
    private static double radiansToDegrees( double radians ) { 
        return radians * 180.0 / Math.PI;
    }
	
	protected java.awt.Rectangle getActualLetterboxedViewport( int width, int height ) {
	    if( Double.isNaN( m_vertical ) || Double.isNaN( m_vertical ) ) {
		    return new java.awt.Rectangle( 0, 0, width, height );
	    } else {
	        double aspect = m_horizontal/m_vertical;
	        double pixelAspect = width/(double)height;
	        if( aspect > pixelAspect ) {
	            int letterBoxedHeight = (int)( ( width / aspect ) + 0.5 ); 
			    return new java.awt.Rectangle( 0, (height-letterBoxedHeight)/2, width, letterBoxedHeight );
	        } else if( aspect < pixelAspect ) {
	            int letterBoxedWidth = (int)( ( height * aspect ) + 0.5 ); 
			    return new java.awt.Rectangle( (width-letterBoxedWidth)/2, 0, letterBoxedWidth, height );
	        } else {
			    return new java.awt.Rectangle( 0, 0, width, height );
	        }
	    }
	}

	private static final double DEFAULT_ACTUAL_VERTICAL = 0.5;
	public double getActualHorizontalViewingAngle( int width, int height ) {
	    if( Double.isNaN( m_horizontal ) ) {
	        double aspect = width/(double)height;
		    if( Double.isNaN( m_vertical ) ) {
		        return DEFAULT_ACTUAL_VERTICAL * aspect;
		    } else {
		        return m_vertical * aspect;
		    }
	    } else {
	        return m_horizontal;
	    }
	}
	public double getActualVerticalViewingAngle( int width, int height ) {
	    if( Double.isNaN( m_vertical ) ) {
	        double aspect = width/(double)height;
		    if( Double.isNaN( m_horizontal ) ) {
		        return DEFAULT_ACTUAL_VERTICAL;
		    } else {
		        return m_horizontal / aspect;
		    }
	    } else {
	        return m_vertical;
	    }
	}

	
	protected double[] getActualNearPlane( double[] ret, int width, int height, double near ) {
	    double vertical = m_vertical;
	    double horizontal = m_horizontal;
	    if( Double.isNaN( horizontal ) ) {
		    if( Double.isNaN( vertical ) ) {
		        vertical = 0.5;
		    }
	        horizontal = ( vertical * width )/ height;
	    } else {
		    if( Double.isNaN( vertical ) ) {
		        vertical = ( horizontal * height )/ width;
		    }
	    }
	            
	    double y = Math.tan( vertical*0.5 ) * near;
	    double x = horizontal*y/vertical;
	    ret[ 0 ] = -x;
	    ret[ 1 ] = -y;
	    ret[ 2 ] = x;
	    ret[ 3 ] = y;
	    return ret;
	}
	private double[] reuse_actualNearPlane = new double[ 4 ];
    
	protected void projection( Context context, int width, int height, float near, float far ) {
        getActualNearPlane( reuse_actualNearPlane, width, height, near );
        context.gl.glFrustum( reuse_actualNearPlane[ 0 ], reuse_actualNearPlane[ 2 ], reuse_actualNearPlane[ 1 ], reuse_actualNearPlane[ 3 ], near, far );
        //context.glu.gluPerspective( radiansToDegrees( m_vertical ), width/(double)height, near, far );
    }

	
	protected void changed( edu.cmu.cs.stage3.alice.scenegraph.Property property, Object value ) {
		if( property == edu.cmu.cs.stage3.alice.scenegraph.SymmetricPerspectiveCamera.VERTICAL_VIEWING_ANGLE_PROPERTY ) {
		    m_vertical = ((Number)value).doubleValue();
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.SymmetricPerspectiveCamera.HORIZONTAL_VIEWING_ANGLE_PROPERTY ) {
		    m_horizontal = ((Number)value).doubleValue();
		} else {
			super.changed( property, value );
		}
	}
}
