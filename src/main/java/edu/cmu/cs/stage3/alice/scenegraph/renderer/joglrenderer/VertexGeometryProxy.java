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

abstract class VertexGeometryProxy extends GeometryProxy {
	private edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[] m_vertices;
	protected edu.cmu.cs.stage3.alice.scenegraph.Vertex3d getVertexAt(int index) {
		return m_vertices[index];
	}

	protected int getNumVertices() {

		return m_vertices.length;
	}

	@Override
	protected void changed(edu.cmu.cs.stage3.alice.scenegraph.Property property, Object value) {
		if (property == edu.cmu.cs.stage3.alice.scenegraph.VertexGeometry.VERTICES_PROPERTY) {
			m_vertices = (edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[]) value;
			setIsGeometryChanged(true);
		} else if (property == edu.cmu.cs.stage3.alice.scenegraph.VertexGeometry.VERTEX_LOWER_BOUND_PROPERTY) {
			// todo
		} else if (property == edu.cmu.cs.stage3.alice.scenegraph.VertexGeometry.VERTEX_UPPER_BOUND_PROPERTY) {
			// todo
		} else {
			super.changed(property, value);
		}
	}
}
