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

package edu.cmu.cs.stage3.alice.core.geometry;

import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.TexCoord2f;

import edu.cmu.cs.stage3.alice.core.Geometry;
import edu.cmu.cs.stage3.alice.core.property.FontProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray;
import edu.cmu.cs.stage3.alice.scenegraph.Vertex3d;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: Stage3
 * </p>
 * 
 * @author Ben Buchwald
 * @version 1.0
 */

public class Text3D extends Geometry {
	public final StringProperty text = new StringProperty(this, "text", null);
	public final FontProperty font = new FontProperty(this, "font", null);
	public final NumberProperty extrusion = new NumberProperty(this, "extrusion", new Double(.25));
	public final NumberProperty curvature = new NumberProperty(this, "curvature", new Integer(2));

	// private java.util.Hashtable charCache;

	private java.awt.Font m_font = null;
	private int m_curvature = 0;

	public Text3D() {
		super(new IndexedTriangleArray());
		// charCache = new java.util.Hastable();
		updateGeometry();
	}

	@Override
	protected void propertyChanged(edu.cmu.cs.stage3.alice.core.Property property, Object value) {
		if (property == text) {
			updateGeometry();
		} else if (property == font || property == curvature) {
			if (m_curvature != curvature.intValue() || !m_font.equals(font.getFontValue())) {
				// charCache.clear();
				updateGeometry();
			}
		} else if (property == extrusion) {
			updateExtrusion();
		} else {
			super.propertyChanged(property, value);
		}
	}

	protected void updateExtrusion() {
		double extz = extrusion.doubleValue() / 2;
		Vertex3d[] verts = ((IndexedTriangleArray) getSceneGraphGeometry()).getVertices();

		for (int i = 0; i < verts.length; i++) {
			verts[i].position.z = extz * (verts[i].position.z > 0 ? 1 : -1);
		}

		((IndexedTriangleArray) getSceneGraphGeometry()).setVertices(verts);
	}

	protected void updateGeometry() {
		if (font.getFontValue() == null || text.getStringValue() == null || extrusion.getNumberValue() == null || curvature.getNumberValue() == null) {
			return;
		}
		m_font = font.getFontValue();
		m_curvature = curvature.intValue();

		PolygonGroup pg = new PolygonGroup();

		Point2d shiftOffset = new Point2d(0, 0);
		Point2d offset;

		int loc = 0;
		int lineCount = 1;
		String m_text = text.getStringValue();
		while (m_text.indexOf('\n', loc) != -1) {
			lineCount++;
			GlyphVector gv = m_font.createGlyphVector(new FontRenderContext(new AffineTransform(), false, true), m_text.substring(loc, m_text.indexOf('\n', loc)));
			shiftOffset.x = gv.getVisualBounds().getWidth() / 2;
			for (int i = 0; i < gv.getNumGlyphs(); i++) {
				offset = new Point2d(-gv.getGlyphPosition(i).getX() + shiftOffset.x, -gv.getGlyphPosition(i).getY() + shiftOffset.y);
				buildGlyph(pg, gv.getGlyphOutline(i), offset, curvature.intValue());
			}
			shiftOffset.y -= gv.getVisualBounds().getHeight();
			loc = m_text.indexOf('\n', loc) + 1;
		}
		if (loc < m_text.length()) {
			GlyphVector gv = m_font.createGlyphVector(new FontRenderContext(new AffineTransform(), false, true), m_text.substring(loc));
			shiftOffset.x = gv.getVisualBounds().getWidth() / 2;
			for (int i = 0; i < gv.getNumGlyphs(); i++) {
				// changed: dividing width by 10 to make less wide, maybe change
				// shiftOffset as well
				offset = new Point2d(-gv.getGlyphPosition(i).getX() / 10 + shiftOffset.x, -gv.getGlyphPosition(i).getY() + shiftOffset.y);
				buildGlyph(pg, gv.getGlyphOutline(i), offset, curvature.intValue());
			}
		}

		pg.triangulate(extrusion.doubleValue());
		((IndexedTriangleArray) getSceneGraphGeometry()).setVertices(pg.getVertices());
		((IndexedTriangleArray) getSceneGraphGeometry()).setIndices(pg.getIndices());

		if (pg.getVertices() != null) {
			Vertex3d[] verts = new Vertex3d[pg.getVertices().length];
			double height = getSceneGraphGeometry().getBoundingBox().getHeight() / lineCount;
			for (int i = 0; i < verts.length; i++) {
				verts[i] = new Vertex3d((Point3d) pg.getVertices()[i].position.clone(), pg.getVertices()[i].normal, null, null, new TexCoord2f());
				verts[i].scale(1.0 / height, 1.0 / height, 1);
			}
			((IndexedTriangleArray) getSceneGraphGeometry()).setVertices(verts);
		}
	}

	protected void buildGlyph(PolygonGroup pg, Shape outline, Point2d offset, int curvature) {
		PathIterator pi = outline.getPathIterator(new AffineTransform());
		pg.parsePathIterator(pi, offset, curvature);
	}

}