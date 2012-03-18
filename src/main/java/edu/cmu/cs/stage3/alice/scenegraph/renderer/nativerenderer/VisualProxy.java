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

public abstract class VisualProxy extends ComponentProxy {
	protected abstract void onFrontFacingAppearanceChange( AppearanceProxy value );
	protected abstract void onBackFacingAppearanceChange( AppearanceProxy value );
	protected abstract void onGeometryChange( GeometryProxy value );
	protected abstract void onScaleChange( javax.vecmath.Matrix3d value );
	protected abstract void onIsShowingChange( boolean isShowing );
	protected abstract void onDisabledAffectorsChange( AffectorProxy[] affectors );
    //todo: use Dictionary
	static java.util.Vector m_instances = new java.util.Vector();
	public VisualProxy() {
		super();
		m_instances.addElement( this );
	}
	
	public void release() {
		m_instances.removeElement( this );
		super.release();
	}

	static VisualProxy map( int nativeInstance ) {
		for( int i=0; i<m_instances.size(); i++ ) {
			VisualProxy visualProxy = (VisualProxy)m_instances.elementAt( i );
			if( visualProxy.getNativeInstance()==nativeInstance ) {
				return visualProxy;
			}
		}
		return null;
	}

	
	protected void changed( edu.cmu.cs.stage3.alice.scenegraph.Property property, Object value ) {
		if( property == edu.cmu.cs.stage3.alice.scenegraph.Visual.FRONT_FACING_APPEARANCE_PROPERTY ) {
			onFrontFacingAppearanceChange( (AppearanceProxy)getProxyFor( (edu.cmu.cs.stage3.alice.scenegraph.Appearance)value ) );
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Visual.BACK_FACING_APPEARANCE_PROPERTY ) {
			onBackFacingAppearanceChange( (AppearanceProxy)getProxyFor( (edu.cmu.cs.stage3.alice.scenegraph.Appearance)value ) );
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Visual.GEOMETRY_PROPERTY ) {
			onGeometryChange( (GeometryProxy)getProxyFor( (edu.cmu.cs.stage3.alice.scenegraph.Geometry)value ) );
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Visual.SCALE_PROPERTY ) {
			onScaleChange( (javax.vecmath.Matrix3d)value );
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Visual.IS_SHOWING_PROPERTY ) {
			onIsShowingChange( ((Boolean)value).booleanValue() );
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Visual.DISABLED_AFFECTORS_PROPERTY ) {
            onDisabledAffectorsChange( (AffectorProxy[])getProxiesFor( (edu.cmu.cs.stage3.alice.scenegraph.Affector[])value, AffectorProxy.class ) );
		} else {
			super.changed( property, value );
		}
	}
}
