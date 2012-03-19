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

package edu.cmu.cs.stage3.alice.scenegraph;

/**
 * @author Dennis Cosgrove
 */
public class Appearance extends Element {
	public static final Property AMBIENT_COLOR_PROPERTY = new Property(Appearance.class, "AMBIENT_COLOR");
	public static final Property DIFFUSE_COLOR_PROPERTY = new Property(Appearance.class, "DIFFUSE_COLOR");
	public static final Property FILLING_STYLE_PROPERTY = new Property(Appearance.class, "FILLING_STYLE");
	public static final Property SHADING_STYLE_PROPERTY = new Property(Appearance.class, "SHADING_STYLE");
	public static final Property OPACITY_PROPERTY = new Property(Appearance.class, "OPACITY");
	public static final Property SPECULAR_HIGHLIGHT_COLOR_PROPERTY = new Property(Appearance.class, "SPECULAR_HIGHLIGHT_COLOR");
	public static final Property SPECULAR_HIGHLIGHT_EXPONENT_PROPERTY = new Property(Appearance.class, "SPECULAR_HIGHLIGHT_EXPONENT");
	public static final Property EMISSIVE_COLOR_PROPERTY = new Property(Appearance.class, "EMISSIVE_COLOR");
	public static final Property DIFFUSE_COLOR_MAP_PROPERTY = new Property(Appearance.class, "DIFFUSE_COLOR_MAP");
	public static final Property OPACITY_MAP_PROPERTY = new Property(Appearance.class, "OPACITY_MAP");
	public static final Property EMISSIVE_COLOR_MAP_PROPERTY = new Property(Appearance.class, "EMISSIVE_COLOR_MAP");
	public static final Property SPECULAR_HIGHLIGHT_COLOR_MAP_PROPERTY = new Property(Appearance.class, "SPECULAR_HIGHLIGHT_COLOR_MAP");
	public static final Property BUMP_MAP_PROPERTY = new Property(Appearance.class, "BUMP_MAP");
	public static final Property DETAIL_MAP_PROPERTY = new Property(Appearance.class, "DETAIL_MAP");

	private edu.cmu.cs.stage3.alice.scenegraph.Color m_ambientColor = null;
	private edu.cmu.cs.stage3.alice.scenegraph.Color m_diffuseColor = new edu.cmu.cs.stage3.alice.scenegraph.Color(1, 1, 1, 1);
	private FillingStyle m_fillingStyle = FillingStyle.SOLID;
	private ShadingStyle m_shadingStyle = ShadingStyle.SMOOTH;
	private double m_opacity = 1;
	private edu.cmu.cs.stage3.alice.scenegraph.Color m_specularHighlightColor = new edu.cmu.cs.stage3.alice.scenegraph.Color(0, 0, 0, 1);
	private double m_specularHighlightExponent = 0;
	private edu.cmu.cs.stage3.alice.scenegraph.Color m_emissiveColor = new edu.cmu.cs.stage3.alice.scenegraph.Color(0, 0, 0, 1);
	private TextureMap m_diffuseColorMap = null;
	private TextureMap m_opacityMap = null;
	private TextureMap m_emissiveColorMap = null;
	private TextureMap m_specularHighlightColorMap = null;
	private TextureMap m_bumpMap = null;
	private TextureMap m_detailMap = null;

	@Override
	protected void releasePass1() {
		if (m_diffuseColorMap != null) {
			warnln("WARNING: released appearence " + this + " still has diffuse color map " + m_diffuseColorMap + ".");
			setDiffuseColorMap(null);
		}
		if (m_opacityMap != null) {
			warnln("WARNING: released appearence " + this + " still has opacity map " + m_opacityMap + ".");
			setOpacityMap(null);
		}
		if (m_emissiveColorMap != null) {
			warnln("WARNING: released appearence " + this + " still has emissive color map " + m_emissiveColorMap + ".");
			setEmissiveColorMap(null);
		}
		if (m_specularHighlightColorMap != null) {
			warnln("WARNING: released appearence " + this + " still has specular highlight color map " + m_specularHighlightColorMap + ".");
			setSpecularHighlightColorMap(null);
		}
		if (m_bumpMap != null) {
			warnln("WARNING: released appearence " + this + " still has bump map " + m_bumpMap + ".");
			setBumpMap(null);
		}
		if (m_detailMap != null) {
			warnln("WARNING: released appearence " + this + " still has detail map " + m_detailMap + ".");
			setDetailMap(null);
		}
		super.releasePass1();
	}

	public edu.cmu.cs.stage3.alice.scenegraph.Color getAmbientColor() {
		return m_ambientColor;
	}
	public void setAmbientColor(edu.cmu.cs.stage3.alice.scenegraph.Color ambientColor) {
		if (notequal(m_ambientColor, ambientColor)) {
			m_ambientColor = ambientColor;
			onPropertyChange(AMBIENT_COLOR_PROPERTY);
		}
	}
	public edu.cmu.cs.stage3.alice.scenegraph.Color getDiffuseColor() {
		return m_diffuseColor;
	}
	public void setDiffuseColor(edu.cmu.cs.stage3.alice.scenegraph.Color diffuseColor) {
		if (notequal(m_diffuseColor, diffuseColor)) {
			m_diffuseColor = diffuseColor;
			onPropertyChange(DIFFUSE_COLOR_PROPERTY);
		}
	}
	public double getOpacity() {
		return m_opacity;
	}
	public void setOpacity(double opacity) {
		if (m_opacity != opacity) {
			m_opacity = opacity;
			onPropertyChange(OPACITY_PROPERTY);
		}
	}

	public double getSpecularHighlightExponent() {
		return m_specularHighlightExponent;
	}
	public void setSpecularHighlightExponent(double specularHighlightExponent) {
		if (m_specularHighlightExponent != specularHighlightExponent) {
			m_specularHighlightExponent = specularHighlightExponent;
			onPropertyChange(SPECULAR_HIGHLIGHT_EXPONENT_PROPERTY);
		}
	}

	public edu.cmu.cs.stage3.alice.scenegraph.Color getSpecularHighlightColor() {
		return m_specularHighlightColor;
	}
	public void setSpecularHighlightColor(edu.cmu.cs.stage3.alice.scenegraph.Color specularHighlightColor) {
		if (notequal(m_specularHighlightColor, specularHighlightColor)) {
			m_specularHighlightColor = specularHighlightColor;
			onPropertyChange(SPECULAR_HIGHLIGHT_COLOR_PROPERTY);
		}
	}

	public edu.cmu.cs.stage3.alice.scenegraph.Color getEmissiveColor() {
		return m_emissiveColor;
	}
	public void setEmissiveColor(edu.cmu.cs.stage3.alice.scenegraph.Color emissiveColor) {
		if (notequal(m_emissiveColor, emissiveColor)) {
			m_emissiveColor = emissiveColor;
			onPropertyChange(EMISSIVE_COLOR_PROPERTY);
		}
	}

	public FillingStyle getFillingStyle() {
		return m_fillingStyle;
	}
	public void setFillingStyle(FillingStyle fillingStyle) {
		if (m_fillingStyle != fillingStyle) {
			m_fillingStyle = fillingStyle;
			onPropertyChange(FILLING_STYLE_PROPERTY);
		}
	}
	public ShadingStyle getShadingStyle() {
		return m_shadingStyle;
	}
	public void setShadingStyle(ShadingStyle shadingStyle) {
		if (m_shadingStyle != shadingStyle) {
			m_shadingStyle = shadingStyle;
			onPropertyChange(SHADING_STYLE_PROPERTY);
		}
	}

	public TextureMap getDiffuseColorMap() {
		return m_diffuseColorMap;
	}
	public void setDiffuseColorMap(TextureMap diffuseColorMap) {
		if (notequal(m_diffuseColorMap, diffuseColorMap)) {
			m_diffuseColorMap = diffuseColorMap;
			onPropertyChange(DIFFUSE_COLOR_MAP_PROPERTY);
		}
	}
	public TextureMap getOpacityMap() {
		return m_opacityMap;
	}
	public void setOpacityMap(TextureMap opacityMap) {
		if (notequal(m_opacityMap, opacityMap)) {
			m_opacityMap = opacityMap;
			onPropertyChange(OPACITY_MAP_PROPERTY);
		}
	}
	public TextureMap getEmissiveColorMap() {
		return m_emissiveColorMap;
	}
	public void setEmissiveColorMap(TextureMap emissiveColorMap) {
		if (notequal(m_emissiveColorMap, emissiveColorMap)) {
			m_emissiveColorMap = emissiveColorMap;
			onPropertyChange(EMISSIVE_COLOR_MAP_PROPERTY);
		}
	}
	public TextureMap getSpecularHighlightColorMap() {
		return m_emissiveColorMap;
	}
	public void setSpecularHighlightColorMap(TextureMap specularHighlightColorMap) {
		if (notequal(m_specularHighlightColorMap, specularHighlightColorMap)) {
			m_specularHighlightColorMap = specularHighlightColorMap;
			onPropertyChange(SPECULAR_HIGHLIGHT_COLOR_MAP_PROPERTY);
		}
	}
	public TextureMap getBumpMap() {
		return m_bumpMap;
	}
	public void setBumpMap(TextureMap bumpMap) {
		if (notequal(m_bumpMap, bumpMap)) {
			m_bumpMap = bumpMap;
			onPropertyChange(BUMP_MAP_PROPERTY);
		}
	}
	public TextureMap getDetailMap() {
		return m_detailMap;
	}
	public void setDetailMap(TextureMap detailMap) {
		if (notequal(m_detailMap, detailMap)) {
			m_detailMap = detailMap;
			onPropertyChange(DETAIL_MAP_PROPERTY);
		}
	}
}
