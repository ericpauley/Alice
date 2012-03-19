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

import edu.cmu.cs.stage3.alice.core.property.VertexArrayProperty;

public class VertexGeometry extends edu.cmu.cs.stage3.alice.core.Geometry {
	public final VertexArrayProperty vertices = new VertexArrayProperty(this, "vertices", null);
	public VertexGeometry(edu.cmu.cs.stage3.alice.scenegraph.VertexGeometry sgVertexGeometry) {
		super(sgVertexGeometry);
	}
	public edu.cmu.cs.stage3.alice.scenegraph.VertexGeometry getSceneGraphVertexGeometry() {
		return (edu.cmu.cs.stage3.alice.scenegraph.VertexGeometry) getSceneGraphGeometry();
	}

	@Override
	protected void propertyChanged(edu.cmu.cs.stage3.alice.core.Property property, Object value) {
		if (property == vertices) {
			getSceneGraphVertexGeometry().setVertices((edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[]) value);
		} else {
			super.propertyChanged(property, value);
		}
	}

	public int getVertexCount() {
		return getSceneGraphVertexGeometry().getVertexCount();
	}
	public void setNormals(javax.vecmath.Vector3d[] normals) {
		edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[] sgVertices = getSceneGraphVertexGeometry().getVertices();
		if (sgVertices != null) {
			for (int i = 0; i < sgVertices.length; i++) {
				sgVertices[i].normal = normals[i];
			}
		}
		// todo: need a better way to force update
		vertices.set(null);
		vertices.set(sgVertices);
	}
}
