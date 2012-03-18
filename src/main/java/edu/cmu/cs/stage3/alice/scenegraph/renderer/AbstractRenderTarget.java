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

package edu.cmu.cs.stage3.alice.scenegraph.renderer;

public abstract class AbstractRenderTarget implements edu.cmu.cs.stage3.alice.scenegraph.renderer.RenderTarget {
	private AbstractRenderer m_abstractRenderer;
	private java.util.Vector m_sgCameras = new java.util.Vector();
	private edu.cmu.cs.stage3.alice.scenegraph.Camera[] m_sgCameraArray = null;
	private java.util.Vector m_renderTargetListeners = new java.util.Vector();
	private edu.cmu.cs.stage3.alice.scenegraph.renderer.event.RenderTargetListener[] m_renderTargetListenerArray = null;
	private String m_name = null;

	protected AbstractRenderTarget( AbstractRenderer abstractRenderer ) {
		m_abstractRenderer = abstractRenderer;
		m_abstractRenderer.addRenderTarget( this );
	}
	public void release() {
		m_abstractRenderer.removeRenderTarget( this );
	}
	
	protected void finalize() throws Throwable {
		release();
		super.finalize();
	}
	public edu.cmu.cs.stage3.alice.scenegraph.renderer.Renderer getRenderer() {
		return m_abstractRenderer;
	}

	public void markDirty() {
	}
	public java.awt.Dimension getSize() {
		java.awt.Dimension size = new java.awt.Dimension();
		return getSize( size );
	}
	public String getName() {
		return m_name;
	}
	public void setName( String name ) {
		m_name = name;
	}
	public void addCamera( edu.cmu.cs.stage3.alice.scenegraph.Camera camera ) {
		if( m_sgCameras.contains( camera ) ) {
			//pass
		} else {
			m_sgCameras.addElement( camera );
			m_sgCameraArray = null;
			//if( m_sgCameras.size()>1 ) {
			//	Element.warnln( "what the monkey???  more than one camera in renderTarget: " + this );
			//	Element.warnln( "editors- please remember to remove cameras when opening new worlds." );
			//	for( int i=0; i<m_sgCameras.size(); i++ ) {
			//		Element.warnln( "\t" + i  + m_sgCameras.elementAt( i ) );
			//	}
			//}
		}
		markDirty();
	}
	public void removeCamera( edu.cmu.cs.stage3.alice.scenegraph.Camera camera ) {
		m_sgCameras.removeElement( camera );
		m_sgCameraArray = null;
		markDirty();
	}
	public edu.cmu.cs.stage3.alice.scenegraph.Camera[] getCameras() {
		if( m_sgCameraArray == null ) {
			m_sgCameraArray = new edu.cmu.cs.stage3.alice.scenegraph.Camera[ m_sgCameras.size() ];
			m_sgCameras.copyInto( m_sgCameraArray );
		}
		return m_sgCameraArray;
	}
	public void clearCameras() {
		m_sgCameras.removeAllElements();
		markDirty();
	}

	public void addRenderTargetListener( edu.cmu.cs.stage3.alice.scenegraph.renderer.event.RenderTargetListener renderTargetListener ) {
		m_renderTargetListeners.addElement( renderTargetListener );
		m_renderTargetListenerArray = null;
	}
	public void removeRenderTargetListener( edu.cmu.cs.stage3.alice.scenegraph.renderer.event.RenderTargetListener renderTargetListener ) {
		m_renderTargetListeners.removeElement( renderTargetListener );
		m_renderTargetListenerArray = null;
	}
	public edu.cmu.cs.stage3.alice.scenegraph.renderer.event.RenderTargetListener[] getRenderTargetListeners() {
		if( m_renderTargetListenerArray == null ) {
			m_renderTargetListenerArray = new edu.cmu.cs.stage3.alice.scenegraph.renderer.event.RenderTargetListener[ m_renderTargetListeners.size() ];
			m_renderTargetListeners.copyInto( m_renderTargetListenerArray );
		}
		return m_renderTargetListenerArray;
	}

	public javax.vecmath.Vector4d transformFromViewportToProjection( javax.vecmath.Vector3d xyz, edu.cmu.cs.stage3.alice.scenegraph.Camera camera ) {
		java.awt.Rectangle viewport = getActualViewport( camera );
		double halfWidth = viewport.width / 2.0;
		double halfHeight = viewport.height / 2.0;
		double x = (xyz.x - halfWidth) / halfWidth;
		double y = -(xyz.y - halfHeight) / halfHeight;
		return new javax.vecmath.Vector4d( x, y, xyz.z, 1 );
	}
	public javax.vecmath.Vector3d transformFromProjectionToCamera( javax.vecmath.Vector4d xyzw, edu.cmu.cs.stage3.alice.scenegraph.Camera camera ) {
		javax.vecmath.Matrix4d inverseProjectionMatrix = getProjectionMatrix( camera );
		inverseProjectionMatrix.invert();
		return edu.cmu.cs.stage3.math.MathUtilities.createVector3d( edu.cmu.cs.stage3.math.MathUtilities.multiply( xyzw, inverseProjectionMatrix ) );
	}
	public javax.vecmath.Vector3d transformFromViewportToCamera( javax.vecmath.Vector3d xyz, edu.cmu.cs.stage3.alice.scenegraph.Camera camera ) {
		return transformFromProjectionToCamera( transformFromViewportToProjection( xyz, camera ), camera );
	}

	public javax.vecmath.Vector4d transformFromCameraToProjection( javax.vecmath.Vector3d xyz, edu.cmu.cs.stage3.alice.scenegraph.Camera camera ) {
		javax.vecmath.Matrix4d projectionMatrix = getProjectionMatrix( camera );
		return edu.cmu.cs.stage3.math.MathUtilities.multiply( xyz, 1, projectionMatrix );
	}
	public javax.vecmath.Vector3d transformFromProjectionToViewport( javax.vecmath.Vector4d xyzw, edu.cmu.cs.stage3.alice.scenegraph.Camera camera ) {
		java.awt.Rectangle viewport = getActualViewport( camera );
		double halfWidth = viewport.width / 2.0;
		double halfHeight = viewport.height / 2.0;
		javax.vecmath.Vector3d xyz = edu.cmu.cs.stage3.math.MathUtilities.createVector3d( xyzw );
		xyz.x = (xyz.x + 1) * halfWidth;
		xyz.y = viewport.height - ((xyz.y + 1) * halfHeight);
		return xyz;
	}
	public javax.vecmath.Vector3d transformFromCameraToViewport( javax.vecmath.Vector3d xyz, edu.cmu.cs.stage3.alice.scenegraph.Camera camera ) {
		return transformFromProjectionToViewport( transformFromCameraToProjection( xyz, camera ), camera );
	}

	public edu.cmu.cs.stage3.math.Ray getRayAtPixel( edu.cmu.cs.stage3.alice.scenegraph.Camera camera, int pixelX, int pixelY ) {
		javax.vecmath.Matrix4d inverseProjection = getProjectionMatrix( camera );
		inverseProjection.invert();

		javax.vecmath.Point3d origin = new javax.vecmath.Point3d( 
				inverseProjection.m20 / inverseProjection.m23, 
				inverseProjection.m21 / inverseProjection.m23, 
				inverseProjection.m22 / inverseProjection.m23 
		);

		java.awt.Rectangle viewport = getActualViewport( camera );
		double halfWidth = viewport.width / 2.0;
		double halfHeight = viewport.height / 2.0;
		double x = (pixelX + 0.5 - halfWidth) / halfWidth;
		double y = -(pixelY + 0.5 - halfHeight) / halfHeight;

		javax.vecmath.Vector4d qs = new javax.vecmath.Vector4d( x, y, 0, 1 );
		javax.vecmath.Vector4d qw = edu.cmu.cs.stage3.math.MathUtilities.multiply( qs, inverseProjection );

		javax.vecmath.Vector3d direction = new javax.vecmath.Vector3d( 
				qw.x * inverseProjection.m23 - qw.w * inverseProjection.m20, 
				qw.y * inverseProjection.m23 - qw.w * inverseProjection.m21, 
				qw.z * inverseProjection.m23 - qw.w * inverseProjection.m22 
		);
		direction.normalize();

		return new edu.cmu.cs.stage3.math.Ray( origin, direction );
	}

	protected void onClear() {
		m_abstractRenderer.enterIgnore();
		try {
			edu.cmu.cs.stage3.alice.scenegraph.renderer.event.RenderTargetEvent renderTargetEvent = new edu.cmu.cs.stage3.alice.scenegraph.renderer.event.RenderTargetEvent( this );
			edu.cmu.cs.stage3.alice.scenegraph.renderer.event.RenderTargetListener[] rtls = getRenderTargetListeners();
			for( int i = 0; i < rtls.length; i++ ) {
				rtls[ i ].cleared( renderTargetEvent );
			}
		} finally {
			m_abstractRenderer.leaveIgnore();
		}
	}
	protected void onRender() {
		m_abstractRenderer.enterIgnore();
		try {
			edu.cmu.cs.stage3.alice.scenegraph.renderer.event.RenderTargetEvent renderTargetEvent = new edu.cmu.cs.stage3.alice.scenegraph.renderer.event.RenderTargetEvent( this );
			edu.cmu.cs.stage3.alice.scenegraph.renderer.event.RenderTargetListener[] rtls = getRenderTargetListeners();
			for( int i = 0; i < rtls.length; i++ ) {
				rtls[ i ].rendered( renderTargetEvent );
			}
		} finally {
			m_abstractRenderer.leaveIgnore();
		}
	}
	
	public String toString() {
		return getClass().getName() + "[" + getName() + "]";
	}
}
