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

import edu.cmu.cs.stage3.alice.core.property.IntArrayProperty;

public class IndexedTriangleArray extends VertexGeometry {
	public final IntArrayProperty indices = new IntArrayProperty(this, "indices", null);
	public IndexedTriangleArray() {
		super(new edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray());
	}
	public edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray getSceneGraphIndexedTriangleArray() {
		return (edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray) getSceneGraphGeometry();
	}

	@Override
	protected void propertyChanged(edu.cmu.cs.stage3.alice.core.Property property, Object value) {
		if (property == indices) {
			getSceneGraphIndexedTriangleArray().setIndices((int[]) value);
		} else {
			super.propertyChanged(property, value);
		}
	}

	public int getIndexCount() {
		return getSceneGraphIndexedTriangleArray().getIndexCount();
	}

	/*
	 * //todo: private void updateVertices() { Object o =
	 * getSceneGraphIndexedTriangleArray().getVertices(); vertices.set( null );
	 * vertices.set( o ); } //todo: private void updateIndices() { Object o =
	 * getSceneGraphIndexedTriangleArray().getIndices(); indices.set( null );
	 * indices.set( o ); }
	 * 
	 * public int getProgressUpdateTotalForReverseNormals() { return
	 * getSceneGraphIndexedTriangleArray
	 * ().getProgressUpdateTotalForReverseNormals(); } public void
	 * reverseNormals( edu.cmu.cs.stage3.util.ProgressObserver progressObserver
	 * ) { getSceneGraphIndexedTriangleArray().reverseNormals( progressObserver
	 * ); updateVertices(); } public int
	 * getProgressUpdateTotalForReverseIndexOrder() { return
	 * getSceneGraphIndexedTriangleArray
	 * ().getProgressUpdateTotalForReverseIndexOrder(); } public void
	 * reverseIndexOrder( edu.cmu.cs.stage3.util.ProgressObserver
	 * progressObserver ) {
	 * getSceneGraphIndexedTriangleArray().reverseIndexOrder( progressObserver
	 * ); updateIndices(); } public int
	 * getProgressUpdateTotalForUnshareVertices() { return
	 * getSceneGraphIndexedTriangleArray
	 * ().getProgressUpdateTotalForUnshareVertices(); } public void
	 * unshareVertices( edu.cmu.cs.stage3.util.ProgressObserver progressObserver
	 * ) { getSceneGraphIndexedTriangleArray().unshareVertices( progressObserver
	 * ); updateVertices(); updateIndices(); } public int
	 * getProgressUpdateTotalForShareVertices() { return
	 * getSceneGraphIndexedTriangleArray
	 * ().getProgressUpdateTotalForShareVertices(); } public void shareVertices(
	 * edu.cmu.cs.stage3.util.ProgressObserver progressObserver ) {
	 * getSceneGraphIndexedTriangleArray().unshareVertices( progressObserver );
	 * updateVertices(); } public int
	 * getProgressUpdateTotalForCalculateNormals() { return
	 * getSceneGraphIndexedTriangleArray
	 * ().getProgressUpdateTotalForCalculateNormals(); } public void
	 * calculateNormals( edu.cmu.cs.stage3.util.ProgressObserver
	 * progressObserver ) {
	 * getSceneGraphIndexedTriangleArray().calculateNormals( progressObserver );
	 * updateVertices(); } public int getProgressUpdateTotalForSmoothNormals() {
	 * return
	 * getSceneGraphIndexedTriangleArray().getProgressUpdateTotalForSmoothNormals
	 * (); } public void smoothNormals( double threshold,
	 * edu.cmu.cs.stage3.util.ProgressObserver progressObserver ) {
	 * getSceneGraphIndexedTriangleArray().smoothNormals( threshold,
	 * progressObserver ); updateVertices(); }
	 */
}
