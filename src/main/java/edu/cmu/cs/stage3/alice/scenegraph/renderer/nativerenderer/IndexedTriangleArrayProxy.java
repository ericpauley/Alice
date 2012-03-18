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

public abstract class IndexedTriangleArrayProxy extends VertexGeometryProxy {
	protected abstract void onIndicesChange( int[] value );
	protected abstract void onIndexLowerBoundChange( int value );
	protected abstract void onIndexUpperBoundChange( int value );
	
	protected void changed( edu.cmu.cs.stage3.alice.scenegraph.Property property, Object value ) {
		if( property == edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray.INDICES_PROPERTY ) {
			onIndicesChange( (int[])value );
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray.INDEX_LOWER_BOUND_PROPERTY ) {
			onIndexLowerBoundChange( ((Integer)value).intValue() );
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray.INDEX_UPPER_BOUND_PROPERTY ) {
			onIndexUpperBoundChange( ((Integer)value).intValue() );
		} else {
			super.changed( property, value );
		}
	}
}
