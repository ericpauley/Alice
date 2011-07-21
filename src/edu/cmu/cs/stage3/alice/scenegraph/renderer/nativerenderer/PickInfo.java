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

public class PickInfo implements  edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo {
	private edu.cmu.cs.stage3.alice.scenegraph.Component m_source = null;
	private javax.vecmath.Matrix4d m_projection;
	private edu.cmu.cs.stage3.alice.scenegraph.Visual[] m_visuals;
	private boolean[] m_isFrontFacings;
	private edu.cmu.cs.stage3.alice.scenegraph.Geometry[] m_geometries;
	private int[] m_subElements;
	private double[] m_zs;

	public PickInfo( edu.cmu.cs.stage3.alice.scenegraph.Component component, javax.vecmath.Matrix4d projection, edu.cmu.cs.stage3.alice.scenegraph.Visual[] visuals, boolean[] isFrontFacings, edu.cmu.cs.stage3.alice.scenegraph.Geometry[] geometries, int[] subElements, double[] zs ) {
		m_source = component;
		m_projection = projection;
		m_visuals = visuals;
		m_isFrontFacings = isFrontFacings;
		m_geometries = geometries;
		m_subElements = subElements;
		m_zs = zs;
	}

	public edu.cmu.cs.stage3.alice.scenegraph.Component getSource() {
		return m_source;
	}
	public edu.cmu.cs.stage3.alice.scenegraph.Visual[] getVisuals() {
		return m_visuals;
	}
	public edu.cmu.cs.stage3.alice.scenegraph.Geometry[] getGeometries() {
		return m_geometries;
	}
	public boolean[] isFrontFacings() {
		return m_isFrontFacings;
	}
	public int[] getSubElements() {
		return m_subElements;
	}
	public double[] getZs() {
		return m_zs;
	}

	public int getCount() {
		if( m_visuals!=null ) {
			return m_visuals.length;
		} else {
			return 0;
		}
	}
	public edu.cmu.cs.stage3.alice.scenegraph.Visual getVisualAt( int index ) {
		return m_visuals[index];
	}
	public boolean isFrontFacingAt( int index ) {
		return m_isFrontFacings[ index ];
	}
	public edu.cmu.cs.stage3.alice.scenegraph.Geometry getGeometryAt( int index ) {
		return m_geometries[index];
	}
	public int getSubElementAt( int index ) {
		return m_subElements[index];
	}
	public double getZAt( int index ) {
		return m_zs[index];
	}


	/** @deprecated */
	private RenderTarget m_renderTarget = null;
	/** @deprecated */
	private edu.cmu.cs.stage3.alice.scenegraph.Camera m_camera = null;
	/** @deprecated */
	private int m_x = -1;
	/** @deprecated */
	private int m_y = -1;
	//todo: deprecate
	public PickInfo( RenderTarget renderTarget, edu.cmu.cs.stage3.alice.scenegraph.Camera camera, int x, int y, edu.cmu.cs.stage3.alice.scenegraph.Visual[] visuals, boolean[] isFrontFacings, edu.cmu.cs.stage3.alice.scenegraph.Geometry[] geometries, int[] subElements ) {
		m_renderTarget = renderTarget;
		m_camera = camera;
		m_source = camera;
		m_x = x;
		m_y = y;
		m_visuals = visuals;
		m_isFrontFacings = isFrontFacings;
		m_geometries = geometries;
		m_subElements = subElements;
	}

	public javax.vecmath.Vector3d getLocalPositionAt( int index ) {
		if( m_source != null && m_zs != null ) {
			javax.vecmath.Matrix4d componentInverseAbsolute = m_source.getInverseAbsoluteTransformation();
			javax.vecmath.Matrix4d visualAbsolute = getVisualAt( index ).getAbsoluteTransformation();
			javax.vecmath.Vector4d xyzw = new javax.vecmath.Vector4d( 0, 0, m_zs[ index ], 1 );
			m_projection.transform( xyzw );
			componentInverseAbsolute.transform( xyzw );
			visualAbsolute.transform( xyzw );
			return new javax.vecmath.Vector3d( xyzw.x/xyzw.w, xyzw.y/xyzw.w, xyzw.z/xyzw.w );
		} else {
			edu.cmu.cs.stage3.alice.scenegraph.Visual visual = getVisualAt( index );
			edu.cmu.cs.stage3.alice.scenegraph.Geometry geometry = getGeometryAt( index );
			int subElement = getSubElementAt( index );
			edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray ita = (edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray)geometry;
			edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[] vertices = ita.getVertices();
			int[] indices = ita.getIndices();
			int i0 = subElement*3;
			javax.vecmath.Vector3d v0 = new javax.vecmath.Vector3d( vertices[indices[i0]].position );
			javax.vecmath.Vector3d v1 = new javax.vecmath.Vector3d( vertices[indices[i0+1]].position );
			javax.vecmath.Vector3d v2 = new javax.vecmath.Vector3d( vertices[indices[i0+2]].position );

			javax.vecmath.Matrix3d scale = visual.getScale();
			if( scale != null ) {
				v0 = edu.cmu.cs.stage3.math.MathUtilities.multiply( scale, v0 );
				v1 = edu.cmu.cs.stage3.math.MathUtilities.multiply( scale, v1 );
				v2 = edu.cmu.cs.stage3.math.MathUtilities.multiply( scale, v2 );
			}
			edu.cmu.cs.stage3.math.Plane plane = new edu.cmu.cs.stage3.math.Plane( v0, v1, v2 );

//			javax.vecmath.Matrix4d inverseProjection = m_renderTarget.getProjectionMatrix( m_camera );
//			inverseProjection.invert();
//			javax.vecmath.Vector3d origin = new javax.vecmath.Vector3d( inverseProjection.m20, inverseProjection.m21, inverseProjection.m22 );
//			origin.scale( 1/inverseProjection.m23 );
//
//			java.awt.Rectangle viewport = m_renderTarget.getActualViewport( m_camera );
//			double halfWidth = viewport.width/2.0;
//			double halfHeight = viewport.height/2.0;
//			double x = (m_x+0.5-halfWidth)/halfWidth;
//			double y = -(m_y+0.5-halfHeight)/halfHeight;
//
//			javax.vecmath.Vector4d qs = new javax.vecmath.Vector4d( x, y, 0, 1 );
//			javax.vecmath.Vector4d qw = edu.cmu.cs.stage3.math.MathUtilities.multiply( qs, inverseProjection );
//
//			javax.vecmath.Vector3d direction = new javax.vecmath.Vector3d( qw.x*inverseProjection.m23 - qw.w*inverseProjection.m20, qw.y*inverseProjection.m23 - qw.w*inverseProjection.m21, qw.z*inverseProjection.m23 - qw.w*inverseProjection.m22 );
//			direction.normalize();
//
//			edu.cmu.cs.stage3.math.Ray ray = new edu.cmu.cs.stage3.math.Ray( origin, direction );
			edu.cmu.cs.stage3.math.Ray ray = m_renderTarget.getRayAtPixel( m_camera, m_x, m_y );

			edu.cmu.cs.stage3.alice.scenegraph.ReferenceFrame cameraFrame = (edu.cmu.cs.stage3.alice.scenegraph.ReferenceFrame)m_camera.getParent();
			ray.transform( cameraFrame.getAbsoluteTransformation() );
			edu.cmu.cs.stage3.alice.scenegraph.ReferenceFrame visualFrame = (edu.cmu.cs.stage3.alice.scenegraph.ReferenceFrame)visual.getParent();
			javax.vecmath.Matrix4d inverseVisual = visualFrame.getInverseAbsoluteTransformation();
			ray.transform( inverseVisual );
			double t = plane.intersect( ray );
			return ray.getPoint( t );
		}
	}
}
