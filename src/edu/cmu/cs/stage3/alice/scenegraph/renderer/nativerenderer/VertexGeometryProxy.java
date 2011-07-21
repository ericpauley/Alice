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

public abstract class VertexGeometryProxy extends GeometryProxy {
	protected abstract void onVerticesFormatAndLengthChange( int format, int length );
	protected abstract void onVerticesVertexPositionChange( int index, double x, double y, double z );
	protected abstract void onVerticesVertexNormalChange( int index, double i, double j, double k );
	protected abstract void onVerticesVertexDiffuseColorChange( int index, float r, float g, float b, float a );
	protected abstract void onVerticesVertexSpecularHighlightColorChange( int index, float r, float g, float b, float a );
	protected abstract void onVerticesVertexTextureCoordinate0Change( int index, float u, float v );
	protected abstract void onVerticesBeginChange();
	protected abstract void onVerticesEndChange();
	protected abstract void onVertexLowerBoundChange( int value );
	protected abstract void onVertexUpperBoundChange( int value );
	private void onVerticesChange( edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[] vertices ) {
		if( vertices!=null ) {
			if( vertices.length>0 ) {
                if( vertices[0] != null ) {
                    int format = vertices[0].getFormat();
                    onVerticesFormatAndLengthChange( format, vertices.length );
                    onVerticesBeginChange();
                    for( int i=0; i<vertices.length; i++ ) {
                        edu.cmu.cs.stage3.alice.scenegraph.Vertex3d vertex = vertices[i];
                        if( vertex.position != null ) {
                            onVerticesVertexPositionChange( i, vertex.position.x, vertex.position.y, vertex.position.z );
                        }
                        if( vertex.normal != null ) {
                            onVerticesVertexNormalChange( i, vertex.normal.x, vertex.normal.y, vertex.normal.z );
                        }
                        if( vertex.diffuseColor != null ) {
                            onVerticesVertexDiffuseColorChange( i, vertex.diffuseColor.red, vertex.diffuseColor.green, vertex.diffuseColor.blue, vertex.diffuseColor.alpha );
                        }
                        if( vertex.specularHighlightColor != null ) {
                            onVerticesVertexSpecularHighlightColorChange( i, vertex.specularHighlightColor.red, vertex.specularHighlightColor.green, vertex.specularHighlightColor.blue, vertex.specularHighlightColor.alpha );
                        }
                        if( vertex.textureCoordinate0 != null ) {
                            onVerticesVertexTextureCoordinate0Change( i, vertex.textureCoordinate0.x, vertex.textureCoordinate0.y );
                        }
                    }
                    onVerticesEndChange();
                }
			} else {
				onVerticesFormatAndLengthChange( 0, 0 );
				onVerticesBeginChange();
				onVerticesEndChange();
			}
		} else {
			onVerticesFormatAndLengthChange( 0, 0 );
			onVerticesBeginChange();
			onVerticesEndChange();
		}
	}
	
	protected void changed( edu.cmu.cs.stage3.alice.scenegraph.Property property, Object value ) {
		if( property == edu.cmu.cs.stage3.alice.scenegraph.VertexGeometry.VERTICES_PROPERTY ) {
			onVerticesChange( (edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[])value );
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.VertexGeometry.VERTEX_LOWER_BOUND_PROPERTY ) {
			onVertexLowerBoundChange( ((Integer)value).intValue() );
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.VertexGeometry.VERTEX_UPPER_BOUND_PROPERTY ) {
			onVertexUpperBoundChange( ((Integer)value).intValue() );
		} else {
			super.changed( property, value );
		}
	}
}
