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

package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.alice.core.property.ColorProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;

public abstract class Light extends Affector {
	public final ColorProperty lightColor = new ColorProperty( this, "lightColor", null );
	public final NumberProperty brightness = new NumberProperty( this, "brightness", new Double( 1 ) );
	public final NumberProperty range = new NumberProperty( this, "range", new Double( 256 ) );
	private edu.cmu.cs.stage3.alice.scenegraph.Light m_sgLight;
	
	public edu.cmu.cs.stage3.alice.scenegraph.Affector getSceneGraphAffector() {
		return getSceneGraphLight();
	}
	public edu.cmu.cs.stage3.alice.scenegraph.Light getSceneGraphLight() {
		return m_sgLight;
	}
	protected Light( edu.cmu.cs.stage3.alice.scenegraph.Light sgLight ) {
		super();
		m_sgLight = sgLight;
		m_sgLight.setParent( getSceneGraphTransformable() );
		m_sgLight.setBonus( this );
		color.set( m_sgLight.getColor() );
		range.set( new Double( m_sgLight.getRange() ) );
	}
    
	protected void internalRelease( int pass ) {
        switch( pass ) {
        case 1:
    		m_sgLight.setParent( null );
            break;
        case 2:
            m_sgLight.release();
            m_sgLight = null;
            break;
        }
        super.internalRelease( pass );
    }

	
	protected void nameValueChanged( String value ) {
		super.nameValueChanged( value );
		String s = null;
		if( value!=null ) {
			s = value+".m_sgLight";
		}
		m_sgLight.setName( s );
	}
	
	protected void propertyChanged( Property property, Object value ) {
		if( property == color ) {
            if( lightColor.getColorValue() == null ) {
    			m_sgLight.setColor( (edu.cmu.cs.stage3.alice.scenegraph.Color)value );
            }
            super.propertyChanged( property, value );
		} else if( property == lightColor ) {
            if( value == null ) {
    			m_sgLight.setColor( color.getColorValue() );
            }
		} else if( property == range ) {
			double d = Double.NaN;
			if( value!=null ) {
				d = ((Number)value).doubleValue();
			}
			m_sgLight.setRange( d );
		} else if( property == brightness ) {
			double d = Double.NaN;
			if( value!=null ) {
				d = ((Number)value).doubleValue();
			}
			m_sgLight.setBrightness( d );
		} else {
			super.propertyChanged( property, value );
		}
	}
}
