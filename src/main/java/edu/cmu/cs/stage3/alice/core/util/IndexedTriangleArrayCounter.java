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

package edu.cmu.cs.stage3.alice.core.util;

public class IndexedTriangleArrayCounter implements edu.cmu.cs.stage3.util.VisitListener {
	private int m_indexedTriangleArrayCount = 0;
	private int m_shownIndexedTriangleArrayCount = 0;
	private int m_vertexCount = 0;
	private int m_shownVertexCount = 0;
	private int m_indexCount = 0;
	private int m_shownIndexCount = 0;
	public void visited( Object o ) {
		if( o instanceof edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray ) {
			edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray ita = (edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray)o;
			m_indexedTriangleArrayCount++;
			m_vertexCount += ita.vertices.size();
			m_indexCount += ita.indices.size();
		} else if( o instanceof edu.cmu.cs.stage3.alice.core.Model ) {
			edu.cmu.cs.stage3.alice.core.Model model = (edu.cmu.cs.stage3.alice.core.Model)o;
			if( model.isShowing.booleanValue() ) {
				edu.cmu.cs.stage3.alice.core.Geometry geometry = model.geometry.getGeometryValue();
				if( geometry instanceof edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray ) {
					edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray ita = (edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray)geometry;
					m_shownIndexedTriangleArrayCount++;
					m_shownVertexCount += ita.vertices.size();
					m_shownIndexCount += ita.indices.size();
				}
			}
		}
	}
	public int getIndexedTriangleArrayCount() {
		return m_indexedTriangleArrayCount;
	}
	public int getShownIndexedTriangleArrayCount() {
		return m_shownIndexedTriangleArrayCount;
	}
	public int getVertexCount() {
		return m_vertexCount;
	}
	public int getShownVertexCount() {
		return m_shownVertexCount;
	}
	public int getIndexCount() {
		return m_indexCount;
	}
	public int getShownIndexCount() {
		return m_shownIndexCount;
	}
}