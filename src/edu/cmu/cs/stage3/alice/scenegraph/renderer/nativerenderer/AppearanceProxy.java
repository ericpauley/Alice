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

public abstract class AppearanceProxy extends ElementProxy {
	protected abstract void onAmbientColorChange( double r, double g, double b, double a );
	protected abstract void onDiffuseColorChange( double r, double g, double b, double a );
	protected abstract void onFillingStyleChange( int value );
	protected abstract void onShadingStyleChange( int value );
	protected abstract void onOpacityChange( double value );
	protected abstract void onSpecularHighlightColorChange( double r, double g, double b, double a );
	protected abstract void onSpecularHighlightExponentChange( double value );
	protected abstract void onEmissiveColorChange( double r, double g, double b, double a );
	protected abstract void onDiffuseColorMapChange( TextureMapProxy value );
	protected abstract void onOpacityMapChange( TextureMapProxy value );
	protected abstract void onEmissiveColorMapChange( TextureMapProxy value );
	protected abstract void onSpecularHighlightColorMapChange( TextureMapProxy value );
	protected abstract void onBumpMapChange( TextureMapProxy value );
	protected abstract void onDetailMapChange( TextureMapProxy value );
	
	protected void changed( edu.cmu.cs.stage3.alice.scenegraph.Property property, Object value ) {
		if( property == edu.cmu.cs.stage3.alice.scenegraph.Appearance.AMBIENT_COLOR_PROPERTY ) {
			edu.cmu.cs.stage3.alice.scenegraph.Color color = (edu.cmu.cs.stage3.alice.scenegraph.Color)value;
			if( color==null ) {
				color = ((edu.cmu.cs.stage3.alice.scenegraph.Appearance)getSceneGraphElement()).getDiffuseColor();
			}
			if( color!=null ) {
				onAmbientColorChange( color.red, color.green, color.blue, color.alpha );
			} else {
				onAmbientColorChange( 0,0,0,0 );
			}
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Appearance.DIFFUSE_COLOR_PROPERTY ) {
			edu.cmu.cs.stage3.alice.scenegraph.Color color = (edu.cmu.cs.stage3.alice.scenegraph.Color)value;
			if( color!=null ) {
				onDiffuseColorChange( color.red, color.green, color.blue, color.alpha );
			} else {
				onDiffuseColorChange( 0,0,0,0 );
			}
			if( ((edu.cmu.cs.stage3.alice.scenegraph.Appearance)getSceneGraphElement()).getAmbientColor() == null ) {
				changed( edu.cmu.cs.stage3.alice.scenegraph.Appearance.AMBIENT_COLOR_PROPERTY, color );
			}
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Appearance.FILLING_STYLE_PROPERTY ) {
			int i;
			if( value.equals( edu.cmu.cs.stage3.alice.scenegraph.FillingStyle.SOLID ) ) {
				i = 4;
			} else if( value.equals( edu.cmu.cs.stage3.alice.scenegraph.FillingStyle.WIREFRAME ) ) {
				i = 2;
			} else if( value.equals( edu.cmu.cs.stage3.alice.scenegraph.FillingStyle.POINTS ) ) {
				i = 1;
			} else {
				throw new RuntimeException();
			}
			onFillingStyleChange( i );
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Appearance.SHADING_STYLE_PROPERTY ) {
			int i;
			if( value==null || value.equals( edu.cmu.cs.stage3.alice.scenegraph.ShadingStyle.NONE ) ) {
				i = 0;
			} else if( value.equals( edu.cmu.cs.stage3.alice.scenegraph.ShadingStyle.FLAT ) ) {
				i = 1;
			} else if( value.equals( edu.cmu.cs.stage3.alice.scenegraph.ShadingStyle.SMOOTH ) ) {
				i = 2;
			} else {
				throw new RuntimeException();
			}
			onShadingStyleChange( i );
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Appearance.OPACITY_PROPERTY ) {
			onOpacityChange( ((Double)value).doubleValue() );
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Appearance.SPECULAR_HIGHLIGHT_COLOR_PROPERTY ) {
			edu.cmu.cs.stage3.alice.scenegraph.Color color = (edu.cmu.cs.stage3.alice.scenegraph.Color)value;
			if( color!=null ) {
				onSpecularHighlightColorChange( color.red, color.green, color.blue, color.alpha );
			} else {
				onSpecularHighlightColorChange( 0,0,0,0 );
			}
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Appearance.SPECULAR_HIGHLIGHT_EXPONENT_PROPERTY ) {
			onSpecularHighlightExponentChange( ((Double)value).doubleValue() );
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Appearance.EMISSIVE_COLOR_PROPERTY ) {
			edu.cmu.cs.stage3.alice.scenegraph.Color color = (edu.cmu.cs.stage3.alice.scenegraph.Color)value;
			if( color!=null ) {
				onEmissiveColorChange( color.red, color.green, color.blue, color.alpha );
			} else {
				onEmissiveColorChange( 0,0,0,0 );
			}
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Appearance.DIFFUSE_COLOR_MAP_PROPERTY ) {
			onDiffuseColorMapChange( (TextureMapProxy)getProxyFor( (edu.cmu.cs.stage3.alice.scenegraph.TextureMap)value ) );
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Appearance.OPACITY_MAP_PROPERTY ) {
			onOpacityMapChange( (TextureMapProxy)getProxyFor( (edu.cmu.cs.stage3.alice.scenegraph.TextureMap)value ) );
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Appearance.EMISSIVE_COLOR_MAP_PROPERTY ) {
			onEmissiveColorMapChange( (TextureMapProxy)getProxyFor( (edu.cmu.cs.stage3.alice.scenegraph.TextureMap)value ) );
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Appearance.SPECULAR_HIGHLIGHT_COLOR_MAP_PROPERTY ) {
			onSpecularHighlightColorMapChange( (TextureMapProxy)getProxyFor( (edu.cmu.cs.stage3.alice.scenegraph.TextureMap)value ) );
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Appearance.BUMP_MAP_PROPERTY ) {
			onBumpMapChange( (TextureMapProxy)getProxyFor( (edu.cmu.cs.stage3.alice.scenegraph.TextureMap)value ) );
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Appearance.DETAIL_MAP_PROPERTY ) {
			onDetailMapChange( (TextureMapProxy)getProxyFor( (edu.cmu.cs.stage3.alice.scenegraph.TextureMap)value ) );
		} else {
			super.changed( property, value );
		}
	}
}
