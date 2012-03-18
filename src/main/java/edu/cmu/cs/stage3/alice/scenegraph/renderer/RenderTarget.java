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

public interface RenderTarget {
	public String getName();
	public void setName( String name );

	public void release();

	public Renderer getRenderer();

	public void addCamera( edu.cmu.cs.stage3.alice.scenegraph.Camera camera );
	public void removeCamera( edu.cmu.cs.stage3.alice.scenegraph.Camera camera );
	public edu.cmu.cs.stage3.alice.scenegraph.Camera[] getCameras();

	public void markDirty();
	public void clearAndRenderOffscreen();

	public boolean rendersOnEdgeTrianglesAsLines( edu.cmu.cs.stage3.alice.scenegraph.OrthographicCamera orthographicCamera );
	public void setRendersOnEdgeTrianglesAsLines( edu.cmu.cs.stage3.alice.scenegraph.OrthographicCamera orthographicCamera, boolean rendersOnEdgeTrianglesAsLines );

	public static final boolean SUB_ELEMENT_IS_REQUIRED = true;
	public static final boolean SUB_ELEMENT_IS_NOT_REQUIRED = false;
	public static final boolean ONLY_FRONT_MOST_VISUAL_IS_REQUIRED = true;
	public static final boolean ALL_VISUALS_ARE_REQUIRED = false;
	public PickInfo pick( int x, int y, boolean isSubElementRequired, boolean isOnlyFrontMostRequired );

	public javax.vecmath.Vector4d transformFromViewportToProjection( javax.vecmath.Vector3d xyz, edu.cmu.cs.stage3.alice.scenegraph.Camera camera );
	public javax.vecmath.Vector3d transformFromProjectionToCamera( javax.vecmath.Vector4d xyzw, edu.cmu.cs.stage3.alice.scenegraph.Camera camera );
	public javax.vecmath.Vector3d transformFromViewportToCamera( javax.vecmath.Vector3d xyz, edu.cmu.cs.stage3.alice.scenegraph.Camera camera );

	public javax.vecmath.Vector4d transformFromCameraToProjection( javax.vecmath.Vector3d xyz, edu.cmu.cs.stage3.alice.scenegraph.Camera camera );
	public javax.vecmath.Vector3d transformFromProjectionToViewport( javax.vecmath.Vector4d xyzw, edu.cmu.cs.stage3.alice.scenegraph.Camera camera );
	public javax.vecmath.Vector3d transformFromCameraToViewport( javax.vecmath.Vector3d xyz, edu.cmu.cs.stage3.alice.scenegraph.Camera camera );

	public java.awt.Image getOffscreenImage();
	public java.awt.Graphics getOffscreenGraphics();

    public java.awt.Image getZBufferImage();
	public java.awt.Image getImage( edu.cmu.cs.stage3.alice.scenegraph.TextureMap textureMap );

	public java.awt.Graphics getGraphics( edu.cmu.cs.stage3.alice.scenegraph.TextureMap textureMap );
	public void copyOffscreenImageToTextureMap( edu.cmu.cs.stage3.alice.scenegraph.TextureMap textureMap );

	public java.awt.Dimension getSize();
	public java.awt.Dimension getSize( java.awt.Dimension rv );

	public double[] getActualPlane( edu.cmu.cs.stage3.alice.scenegraph.OrthographicCamera orthographicCamera );
	public double[] getActualPlane( edu.cmu.cs.stage3.alice.scenegraph.PerspectiveCamera perspectiveCamera );
	public double getActualHorizontalViewingAngle( edu.cmu.cs.stage3.alice.scenegraph.SymmetricPerspectiveCamera symmetricPerspectiveCamera );
	public double getActualVerticalViewingAngle( edu.cmu.cs.stage3.alice.scenegraph.SymmetricPerspectiveCamera symmetricPerspectiveCamera );
	public java.awt.Rectangle getActualViewport( edu.cmu.cs.stage3.alice.scenegraph.Camera camera );

	public javax.vecmath.Matrix4d getProjectionMatrix( edu.cmu.cs.stage3.alice.scenegraph.Camera camera );

	public edu.cmu.cs.stage3.math.Ray getRayAtPixel( edu.cmu.cs.stage3.alice.scenegraph.Camera camera, int pixelX, int pixelY );

	public java.awt.Rectangle getViewport( edu.cmu.cs.stage3.alice.scenegraph.Camera camera );
	public void setViewport( edu.cmu.cs.stage3.alice.scenegraph.Camera camera, java.awt.Rectangle viewport );

	public boolean isLetterboxedAsOpposedToDistorted( edu.cmu.cs.stage3.alice.scenegraph.Camera camera );
	public void setIsLetterboxedAsOpposedToDistorted( edu.cmu.cs.stage3.alice.scenegraph.Camera camera, boolean isLetterboxedAsOpposedToDistorted );

	public void addRenderTargetListener( edu.cmu.cs.stage3.alice.scenegraph.renderer.event.RenderTargetListener renderTargetListener );
	public void removeRenderTargetListener( edu.cmu.cs.stage3.alice.scenegraph.renderer.event.RenderTargetListener renderTargetListener );
	public edu.cmu.cs.stage3.alice.scenegraph.renderer.event.RenderTargetListener[] getRenderTargetListeners();
}
