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

class SpotLightProxy extends PointLightProxy {
    private float m_inner;
    private float m_outer;
    private float m_falloff;

    private static final float RADIANS_TO_DEGREES_FACTOR = (float)(180/Math.PI);
    
	protected float[] getSpotDirection( float[] rv ) {
       
    	double[] absolute = getAbsoluteTransformation();
        rv[ 0 ] = (float)absolute[ 8 ];
        rv[ 1 ] = (float)absolute[ 9 ];
        rv[ 2 ] = (float)absolute[ 10 ];
        return rv;
    }
    //todo?
    //protected float getSpotExponent() {
    //    return ???;
    //}
    
	protected float getSpotCutoff() {
        return m_outer*RADIANS_TO_DEGREES_FACTOR;
    }

    
	protected void changed( edu.cmu.cs.stage3.alice.scenegraph.Property property, Object value ) {
		if( property == edu.cmu.cs.stage3.alice.scenegraph.SpotLight.INNER_BEAM_ANGLE_PROPERTY ) {
			m_inner = ((Number)value).floatValue();
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.SpotLight.OUTER_BEAM_ANGLE_PROPERTY ) {
			m_outer = ((Number)value).floatValue();
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.SpotLight.FALLOFF_PROPERTY ) {
			m_falloff = ((Number)value).floatValue();
		} else {
			super.changed( property, value );
		}
	}
}
