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
public class IndexedTriangleArray extends VertexGeometry {
	public static final Property INDICES_PROPERTY = new Property( IndexedTriangleArray.class, "INDICES" );
	public static final Property INDEX_LOWER_BOUND_PROPERTY = new Property( IndexedTriangleArray.class, "INDEX_LOWER_BOUND" );
	public static final Property INDEX_UPPER_BOUND_PROPERTY = new Property( IndexedTriangleArray.class, "INDEX_UPPER_BOUND" );

	//public static final Property EDGES_PROPERTY = new Property( IndexedTriangleArray.class, "EDGES" );
	//public static final Property SUBDIVISION_REFINEMENT_LEVEL_PROPERTY = new Property( IndexedTriangleArray.class, "SUBDIVISION_REFINEMENT_LEVEL" );

	private int[] m_indices = null;
	private int m_indexLowerBound = 0;
	private int m_indexUpperBound = -1;

	private int[] m_edges = null;
	private int m_subdivisionRefinementLevel = 0;

	public int[] getIndices() {
		return m_indices;
	}
	public void setIndices( int[] indices ) {
		m_indices = indices;
		onPropertyChange( INDICES_PROPERTY );
	}
	public int getIndexLowerBound() {
		return m_indexLowerBound;
	}
	public void setIndexLowerBound( int indexLowerBound ) {
		if( m_indexLowerBound != indexLowerBound ) {
			m_indexLowerBound = indexLowerBound;
			onPropertyChange( INDEX_LOWER_BOUND_PROPERTY );
		}
	}
	public int getIndexUpperBound() {
		return m_indexUpperBound;
	}
	public void setIndexUpperBound( int indexUpperBound ) {
		if( m_indexUpperBound != indexUpperBound ) {
			m_indexUpperBound = indexUpperBound;
			onPropertyChange( INDEX_UPPER_BOUND_PROPERTY );
		}
	}

	/*
	public int[] getEdges() {
		return m_edges;
	}
	public void setEdges( int[] edges ) {
		m_edges = edges;
		onPropertyChange( EDGES_PROPERTY );
	}
	public int getSubdivisionRefinementLevel() {
		return m_subdivisionRefinementLevel;
	}
	public void setSubdivisionRefinementLevel( int subdivisionRefinementLevel ) {
		if( m_subdivisionRefinementLevel != subdivisionRefinementLevel ) {
			m_subdivisionRefinementLevel = subdivisionRefinementLevel;
			onPropertyChange( SUBDIVISION_REFINEMENT_LEVEL_PROPERTY );
		}
	}
	*/

	public int getIndexCount() {
		if( m_indices!=null ) {
			return m_indices.length;
		} else {
			return 0;
		}
	}
	public int getTriangleCount() {
		if( m_indices!=null ) {
			return m_indices.length/3;
		} else {
			return 0;
		}
	}

	public static void storeIndices( int[] indices, java.io.OutputStream os ) throws java.io.IOException {
		java.io.BufferedOutputStream bos = new java.io.BufferedOutputStream ( os );
		java.io.DataOutputStream dos = new java.io.DataOutputStream ( bos );
		dos.writeInt( 2 );
		dos.writeInt( indices.length );
		for (int i=0; i<indices.length; i++) {
			dos.writeInt( indices[i] );
		}
		dos.flush();
	}
	public static int[] loadIndices( java.io.InputStream is ) throws java.io.IOException {
		int[] indices = null;
		java.io.BufferedInputStream bis = new java.io.BufferedInputStream ( is );
		java.io.DataInputStream dis = new java.io.DataInputStream ( bis );
		int version = dis.readInt ();
		if( version==1 ) {
			int faceCount = dis.readInt();
			int verticesPerFace = dis.readInt();
			indices = new int[faceCount*3];
			for( int i=0; i<indices.length; i++ ) {
				indices[i] = dis.readInt();
			}
		} else if( version==2 ) {
			int indicesCount = dis.readInt();
			indices = new int[indicesCount];
			for( int i=0; i<indices.length; i++ ) {
				indices[i] = dis.readInt();
			}
		} else {
			throw new RuntimeException( "invalid file version: " + version );
		}
		return indices;
	}
}
