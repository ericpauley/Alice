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

class AppearanceProxy extends ElementProxy {
	private boolean m_isShaded;
	private boolean m_isAmbientLinkedToDiffuse;
	private float[] m_ambient = new float[4];
	private float[] m_diffuse = new float[4];
	private float[] m_specular = new float[4];
	private float[] m_emissive = new float[4];
	private float m_opacity = 1.0f;
	private float m_shininess;
	private int m_polygonMode;
	private TextureMapProxy m_diffuseColorMapProxy;

	private java.nio.FloatBuffer m_ambientBuffer = java.nio.FloatBuffer.wrap(m_ambient);
	private java.nio.FloatBuffer m_diffuseBuffer = java.nio.FloatBuffer.wrap(m_diffuse);
	private java.nio.FloatBuffer m_specularBuffer = java.nio.FloatBuffer.wrap(m_specular);
	private java.nio.FloatBuffer m_emissiveBuffer = java.nio.FloatBuffer.wrap(m_emissive);

	public void setPipelineState(RenderContext context, int face) {
		context.setIsShadingEnabled(m_isShaded);
		m_diffuse[3] = m_opacity;
		if (m_isShaded) {
			if (m_isAmbientLinkedToDiffuse) {
				context.gl.glMaterialfv(face, GL.GL_AMBIENT_AND_DIFFUSE, m_diffuseBuffer);
			} else {
				context.gl.glMaterialfv(face, GL.GL_AMBIENT, m_ambientBuffer);
				context.gl.glMaterialfv(face, GL.GL_DIFFUSE, m_diffuseBuffer);
			}
			context.gl.glMaterialfv(face, GL.GL_SPECULAR, m_specularBuffer);
			context.gl.glMaterialfv(face, GL.GL_EMISSION, m_emissiveBuffer);
			context.gl.glMaterialf(face, GL.GL_SHININESS, m_shininess);
		} else {
			context.gl.glDisable(GL.GL_LIGHTING);
			// todo: color?
		}
		context.gl.glColor4f(m_diffuse[0], m_diffuse[1], m_diffuse[2], m_diffuse[3]);
		context.gl.glPolygonMode(face, m_polygonMode);

		context.setTextureMapProxy(m_diffuseColorMapProxy);
	}

	@Override
	protected void changed(edu.cmu.cs.stage3.alice.scenegraph.Property property, Object value) {
		if (property == edu.cmu.cs.stage3.alice.scenegraph.Appearance.AMBIENT_COLOR_PROPERTY) {
			m_isAmbientLinkedToDiffuse = value == null;
			if (m_isAmbientLinkedToDiffuse) {
				// pass
			} else {
				copy(m_ambient, (edu.cmu.cs.stage3.alice.scenegraph.Color) value);
			}
		} else if (property == edu.cmu.cs.stage3.alice.scenegraph.Appearance.DIFFUSE_COLOR_PROPERTY) {
			copy(m_diffuse, (edu.cmu.cs.stage3.alice.scenegraph.Color) value);
		} else if (property == edu.cmu.cs.stage3.alice.scenegraph.Appearance.FILLING_STYLE_PROPERTY) {
			if (value.equals(edu.cmu.cs.stage3.alice.scenegraph.FillingStyle.SOLID)) {
				m_polygonMode = GL.GL_FILL;
			} else if (value.equals(edu.cmu.cs.stage3.alice.scenegraph.FillingStyle.WIREFRAME)) {
				m_polygonMode = GL.GL_LINE;
			} else if (value.equals(edu.cmu.cs.stage3.alice.scenegraph.FillingStyle.POINTS)) {
				m_polygonMode = GL.GL_POINT;
			} else {
				throw new RuntimeException();
			}
		} else if (property == edu.cmu.cs.stage3.alice.scenegraph.Appearance.SHADING_STYLE_PROPERTY) {
			if (value == null || value.equals(edu.cmu.cs.stage3.alice.scenegraph.ShadingStyle.NONE)) {
				m_isShaded = false;
			} else if (value.equals(edu.cmu.cs.stage3.alice.scenegraph.ShadingStyle.FLAT)) {
				m_isShaded = true;
				// todo
			} else if (value.equals(edu.cmu.cs.stage3.alice.scenegraph.ShadingStyle.SMOOTH)) {
				m_isShaded = true;
				// todo
			} else {
				throw new RuntimeException();
			}
		} else if (property == edu.cmu.cs.stage3.alice.scenegraph.Appearance.OPACITY_PROPERTY) {
			m_opacity = ((Number) value).floatValue();
		} else if (property == edu.cmu.cs.stage3.alice.scenegraph.Appearance.SPECULAR_HIGHLIGHT_COLOR_PROPERTY) {
			copy(m_specular, (edu.cmu.cs.stage3.alice.scenegraph.Color) value);
		} else if (property == edu.cmu.cs.stage3.alice.scenegraph.Appearance.SPECULAR_HIGHLIGHT_EXPONENT_PROPERTY) {
			m_shininess = ((Number) value).floatValue();
		} else if (property == edu.cmu.cs.stage3.alice.scenegraph.Appearance.EMISSIVE_COLOR_PROPERTY) {
			copy(m_emissive, (edu.cmu.cs.stage3.alice.scenegraph.Color) value);
		} else if (property == edu.cmu.cs.stage3.alice.scenegraph.Appearance.DIFFUSE_COLOR_MAP_PROPERTY) {
			m_diffuseColorMapProxy = (TextureMapProxy) getProxyFor((edu.cmu.cs.stage3.alice.scenegraph.TextureMap) value);
		} else if (property == edu.cmu.cs.stage3.alice.scenegraph.Appearance.OPACITY_MAP_PROPERTY) {
			// todo
		} else if (property == edu.cmu.cs.stage3.alice.scenegraph.Appearance.EMISSIVE_COLOR_MAP_PROPERTY) {
			// todo
		} else if (property == edu.cmu.cs.stage3.alice.scenegraph.Appearance.SPECULAR_HIGHLIGHT_COLOR_MAP_PROPERTY) {
			// todo
		} else if (property == edu.cmu.cs.stage3.alice.scenegraph.Appearance.BUMP_MAP_PROPERTY) {
			// todo
		} else if (property == edu.cmu.cs.stage3.alice.scenegraph.Appearance.DETAIL_MAP_PROPERTY) {
			// todo
		} else {
			super.changed(property, value);
		}
	}
	// todo
	public double Showing() {
		return m_opacity;
	}
}
