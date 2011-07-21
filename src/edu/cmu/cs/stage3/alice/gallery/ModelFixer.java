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

package edu.cmu.cs.stage3.alice.gallery;

public class ModelFixer {
	private static javax.vecmath.Vector3d getDirection( edu.cmu.cs.stage3.alice.scenegraph.Vertex3d vertexA, edu.cmu.cs.stage3.alice.scenegraph.Vertex3d vertexB, edu.cmu.cs.stage3.alice.scenegraph.Vertex3d vertexC ) {
		javax.vecmath.Vector3d ba = new javax.vecmath.Vector3d();
		javax.vecmath.Vector3d bc = new javax.vecmath.Vector3d();
		ba.sub( vertexA.position, vertexB.position );
		bc.sub( vertexC.position, vertexB.position );
		ba.normalize();
		bc.normalize();
		javax.vecmath.Vector3d result = new javax.vecmath.Vector3d();
		result.cross( bc, ba );
		return result;
	}

	private static javax.vecmath.Vector3d[] getCalculatedNormals( edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray indexedTriangleArray, edu.cmu.cs.stage3.progress.ProgressObserver progressObserver ) throws edu.cmu.cs.stage3.progress.ProgressCancelException {
		javax.vecmath.Vector3d[] result = null;
		int triangleCount = indexedTriangleArray.getTriangleCount();
		if( triangleCount>0 ) {
			edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[] vertices = indexedTriangleArray.getVertices();
			result = new javax.vecmath.Vector3d[ vertices.length ];
			int[] counts = new int[ vertices.length ];
			for( int i=0; i<counts.length; i++ ) {
				counts[ i ] = 0;
				result[ i ] = new javax.vecmath.Vector3d();
			}
			int[] indices = indexedTriangleArray.getIndices();
			if( progressObserver!=null ) {
				progressObserver.progressBegin( triangleCount );
			}
			int index = 0;
			for( int t=0; t<triangleCount; t++ ) {
				int a = indices[index++];
				int b = indices[index++];
				int c = indices[index++];

				javax.vecmath.Vector3d normal = getDirection( vertices[a], vertices[b], vertices[c] );
				normal.normalize();

				result[a].add( normal );
				result[b].add( normal );
				result[c].add( normal );
				counts[a]++;
				counts[b]++;
				counts[c]++;
				if( progressObserver != null ) {
					progressObserver.progressUpdate( t, null );
				}
			}
			for( int i=0; i<result.length; i++ ) {
				result[ i ].scale( 1.0/counts[ i ] );
			}
		}
		return result;
	}

	public static void calculateNormals( edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray ita, edu.cmu.cs.stage3.progress.ProgressObserver progressObserver ) throws edu.cmu.cs.stage3.progress.ProgressCancelException {
		ita.setNormals( getCalculatedNormals( ita.getSceneGraphIndexedTriangleArray(), progressObserver ) );
	}

	public static void calculateNormals( edu.cmu.cs.stage3.alice.core.Element element, edu.cmu.cs.stage3.util.HowMuch howMuch, edu.cmu.cs.stage3.progress.ProgressObserver macroProgressObserver, edu.cmu.cs.stage3.progress.ProgressObserver microProgressObserver ) throws edu.cmu.cs.stage3.progress.ProgressCancelException {
		edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray[] itas = (edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray[])element.getDescendants( edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray.class, howMuch );
		if( itas != null && itas.length>0 ) {
			if( macroProgressObserver != null ) {
				macroProgressObserver.progressBegin( itas.length );
				javax.vecmath.Vector3d[][] normalsArray = new javax.vecmath.Vector3d[ itas.length ][];
				try {
					for( int i=0; i<itas.length; i++ ) {
						normalsArray[ i ] = getCalculatedNormals( itas[ i ].getSceneGraphIndexedTriangleArray(), microProgressObserver );
					}
				} finally {
					macroProgressObserver.progressEnd();
				}

				for( int i=0; i<itas.length; i++ ) {
					itas[ i ].setNormals( normalsArray[ i ] );
				}
			} else {
				for( int i=0; i<itas.length; i++ ) {
					calculateNormals( itas[ i ], microProgressObserver );
				}
			}
		}
	}
	public static void calculateNormals( edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray ita ) {
		try {
			calculateNormals( ita, (edu.cmu.cs.stage3.progress.ProgressObserver)null );
		} catch( edu.cmu.cs.stage3.progress.ProgressCancelException pce ) {
			throw new Error();
		}
	}
	public static void calculateNormals( edu.cmu.cs.stage3.alice.core.Element element, edu.cmu.cs.stage3.util.HowMuch howMuch ) {
		try {
			calculateNormals( element, howMuch, null, null );
		} catch( edu.cmu.cs.stage3.progress.ProgressCancelException pce ) {
			throw new Error();
		}
	}

//	public static void reverseNormals( Element element, HowMuch howMuch, ProgressObserver progressObserver, boolean observeMicro ) {
//		VertexGeometry[] vgs = (VertexGeometry[])element.getDescendants( VertexGeometry.class, howMuch );
//		int[] totals = new int[ vgs.length ];
//		int total = 0;
//		for( int i=0; i<vgs.length; i++ ) {
//			totals[ i ] = edu.cmu.cs.stage3.alice.scenegraph.util.VertexGeometryUtil.getProgressTotalForCalculateReversedNormals( vgs[ i ].getSceneGraphVertexGeometry() );
//			total += totals[ i ];
//		}
//		MacroProgressObserver macroProgressObserver = new MacroProgressObserver( progressObserver );
//		ProgressObserver internalProgressObserver;
//		if( observeMicro ) {
//			internalProgressObserver = macroProgressObserver;
//		} else {
//			internalProgressObserver = null;
//		}
//		macroProgressObserver.progressUpdateTotal( total );
//
//		javax.vecmath.Vector3d[][] normalsArray = new javax.vecmath.Vector3d[ vgs.length ][];
//		for( int i=0; i<vgs.length; i++ ) {
//			macroProgressObserver.setElement( vgs[ i ] );
//			normalsArray[ i ] = edu.cmu.cs.stage3.alice.scenegraph.util.VertexGeometryUtil.calculateReversedNormals( vgs[ i ].getSceneGraphVertexGeometry(), internalProgressObserver );
//			macroProgressObserver.incrementMacro( totals[ i ] );
//		}
//
//		for( int i=0; i<vgs.length; i++ ) {
//			vgs[ i ].setNormals( normalsArray[ i ] );
//		}
//	}
//
//	public static void reverseIndexOrder( Element element, HowMuch howMuch, ProgressObserver progressObserver, boolean observeMicro ) {
//		IndexedTriangleArray[] itas = (IndexedTriangleArray[])element.getDescendants( IndexedTriangleArray.class, howMuch );
//		int[] totals = new int[ itas.length ];
//		int total = 0;
//		for( int i=0; i<itas.length; i++ ) {
//			totals[ i ] = edu.cmu.cs.stage3.alice.scenegraph.util.IndexedTriangleArrayUtil.getProgressTotalForCalculateReversedIndexOrder( itas[ i ].getSceneGraphIndexedTriangleArray() );
//			total += totals[ i ];
//		}
//		MacroProgressObserver macroProgressObserver = new MacroProgressObserver( progressObserver );
//		ProgressObserver internalProgressObserver;
//		if( observeMicro ) {
//			internalProgressObserver = macroProgressObserver;
//		} else {
//			internalProgressObserver = null;
//		}
//		macroProgressObserver.progressUpdateTotal( total );
//
//		int[][] indicesArray = new int[ itas.length ][];
//		for( int i=0; i<itas.length; i++ ) {
//			macroProgressObserver.setElement( itas[ i ] );
//			indicesArray[ i ] = edu.cmu.cs.stage3.alice.scenegraph.util.IndexedTriangleArrayUtil.calculateReversedIndexOrder( itas[ i ].getSceneGraphIndexedTriangleArray(), internalProgressObserver );
//			macroProgressObserver.incrementMacro( totals[ i ] );
//		}
//
//		for( int i=0; i<itas.length; i++ ) {
//			itas[ i ].indices.set( indicesArray[ i ] );
//		}
//	}
//	public static void unshareVertices( Element element, HowMuch howMuch, ProgressObserver progressObserver, boolean observeMicro ) {
//		IndexedTriangleArray[] itas = (IndexedTriangleArray[])element.getDescendants( IndexedTriangleArray.class, howMuch );
//		int[] totals = new int[ itas.length ];
//		int total = 0;
//		for( int i=0; i<itas.length; i++ ) {
//			totals[ i ] = edu.cmu.cs.stage3.alice.scenegraph.util.IndexedTriangleArrayUtil.getProgressTotalForCalculateUnsharedVertices( itas[ i ].getSceneGraphIndexedTriangleArray() );
//			total += totals[ i ];
//		}
//		MacroProgressObserver macroProgressObserver = new MacroProgressObserver( progressObserver );
//		ProgressObserver internalProgressObserver;
//		if( observeMicro ) {
//			internalProgressObserver = macroProgressObserver;
//		} else {
//			internalProgressObserver = null;
//		}
//		macroProgressObserver.progressUpdateTotal( total );
//
//		edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[][] verticesArray = new edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[ itas.length ][];
//		int[][] indicesArray = new int[ itas.length ][];
//		for( int i=0; i<itas.length; i++ ) {
//			macroProgressObserver.setElement( itas[ i ] );
//			Object[] tuple = edu.cmu.cs.stage3.alice.scenegraph.util.IndexedTriangleArrayUtil.calculateUnsharedVertices( itas[ i ].getSceneGraphIndexedTriangleArray(), internalProgressObserver );
//			verticesArray[ i ] = (edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[])tuple[ 0 ];
//			indicesArray[ i ] = (int[])tuple[ 1 ];
//			macroProgressObserver.incrementMacro( totals[ i ] );
//		}
//
//		for( int i=0; i<itas.length; i++ ) {
//			itas[ i ].vertices.set( verticesArray[ i ] );
//			itas[ i ].indices.set( indicesArray[ i ] );
//		}
//	}
//	public static void shareVertices( Element element, HowMuch howMuch, ProgressObserver progressObserver, boolean observeMicro ) {
//		IndexedTriangleArray[] itas = (IndexedTriangleArray[])element.getDescendants( IndexedTriangleArray.class, howMuch );
//		int[] totals = new int[ itas.length ];
//		int total = 0;
//		for( int i=0; i<itas.length; i++ ) {
//			totals[ i ] = edu.cmu.cs.stage3.alice.scenegraph.util.IndexedTriangleArrayUtil.getProgressTotalForCalculateSharedVertices( itas[ i ].getSceneGraphIndexedTriangleArray() );
//			total += totals[ i ];
//		}
//		MacroProgressObserver macroProgressObserver = new MacroProgressObserver( progressObserver );
//		ProgressObserver internalProgressObserver;
//		if( observeMicro ) {
//			internalProgressObserver = macroProgressObserver;
//		} else {
//			internalProgressObserver = null;
//		}
//		macroProgressObserver.progressUpdateTotal( total );
//
//		edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[][] verticesArray = new edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[ itas.length ][];
//		int[][] indicesArray = new int[ itas.length ][];
//		for( int i=0; i<itas.length; i++ ) {
//			macroProgressObserver.setElement( itas[ i ] );
//			Object[] tuple = edu.cmu.cs.stage3.alice.scenegraph.util.IndexedTriangleArrayUtil.calculateSharedVertices( itas[ i ].getSceneGraphIndexedTriangleArray(), internalProgressObserver );
//			verticesArray[ i ] = (edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[])tuple[ 0 ];
//			indicesArray[ i ] = (int[])tuple[ 1 ];
//			macroProgressObserver.incrementMacro( totals[ i ] );
//		}
//
//		for( int i=0; i<itas.length; i++ ) {
//			itas[ i ].vertices.set( verticesArray[ i ] );
//			itas[ i ].indices.set( indicesArray[ i ] );
//		}
//	}
//	public static void calculateNormals( Element element, HowMuch howMuch, ProgressObserver progressObserver, boolean observeMicro ) {
//		IndexedTriangleArray[] itas = (IndexedTriangleArray[])element.getDescendants( IndexedTriangleArray.class, howMuch );
//		int[] totals = new int[ itas.length ];
//		int total = 0;
//		for( int i=0; i<itas.length; i++ ) {
//			totals[ i ] = edu.cmu.cs.stage3.alice.scenegraph.util.IndexedTriangleArrayUtil.getProgressTotalForCalculateNormals( itas[ i ].getSceneGraphIndexedTriangleArray() );
//			total += totals[ i ];
//		}
//		MacroProgressObserver macroProgressObserver = new MacroProgressObserver( progressObserver );
//		ProgressObserver internalProgressObserver;
//		if( observeMicro ) {
//			internalProgressObserver = macroProgressObserver;
//		} else {
//			internalProgressObserver = null;
//		}
//		macroProgressObserver.progressUpdateTotal( total );
//
//		javax.vecmath.Vector3d[][] normalsArray = new javax.vecmath.Vector3d[ itas.length ][];
//		for( int i=0; i<itas.length; i++ ) {
//			macroProgressObserver.setElement( itas[ i ] );
//			normalsArray[ i ] = edu.cmu.cs.stage3.alice.scenegraph.util.IndexedTriangleArrayUtil.calculateNormals( itas[ i ].getSceneGraphIndexedTriangleArray(), internalProgressObserver );
//			macroProgressObserver.incrementMacro( totals[ i ] );
//		}
//
//		for( int i=0; i<itas.length; i++ ) {
//			itas[ i ].setNormals( normalsArray[ i ] );
//		}
//	}
//	public static void smoothNormals( Element element, double threshold, HowMuch howMuch, ProgressObserver progressObserver, boolean observeMicro ) {
//		IndexedTriangleArray[] itas = (IndexedTriangleArray[])element.getDescendants( IndexedTriangleArray.class, howMuch );
//		int[] totals = new int[ itas.length ];
//		int total = 0;
//		for( int i=0; i<itas.length; i++ ) {
//			totals[ i ] = edu.cmu.cs.stage3.alice.scenegraph.util.IndexedTriangleArrayUtil.getProgressTotalForCalculateSmoothedNormals( itas[ i ].getSceneGraphIndexedTriangleArray() );
//			total += totals[ i ];
//		}
//		MacroProgressObserver macroProgressObserver = new MacroProgressObserver( progressObserver );
//		ProgressObserver internalProgressObserver;
//		if( observeMicro ) {
//			internalProgressObserver = macroProgressObserver;
//		} else {
//			internalProgressObserver = null;
//		}
//		macroProgressObserver.progressUpdateTotal( total );
//
//		javax.vecmath.Vector3d[][] normalsArray = new javax.vecmath.Vector3d[ itas.length ][];
//		for( int i=0; i<itas.length; i++ ) {
//			macroProgressObserver.setElement( itas[ i ] );
//			normalsArray[ i ] = edu.cmu.cs.stage3.alice.scenegraph.util.IndexedTriangleArrayUtil.calculateSmoothedNormals( itas[ i ].getSceneGraphIndexedTriangleArray(), threshold, internalProgressObserver );
//			macroProgressObserver.incrementMacro( totals[ i ] );
//		}
//
//		for( int i=0; i<itas.length; i++ ) {
//			itas[ i ].setNormals( normalsArray[ i ] );
//		}
//	}
}
