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

package edu.cmu.cs.stage3.alice.scenegraph.renderer.nullrenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Camera;
import edu.cmu.cs.stage3.alice.scenegraph.OrthographicCamera;
import edu.cmu.cs.stage3.alice.scenegraph.PerspectiveCamera;
import edu.cmu.cs.stage3.alice.scenegraph.SymmetricPerspectiveCamera;
import edu.cmu.cs.stage3.alice.scenegraph.TextureMap;

public abstract class RenderTarget extends edu.cmu.cs.stage3.alice.scenegraph.renderer.AbstractRenderTarget {
	RenderTarget( Renderer renderer ) {
		super( renderer );
	}
	
	public void markDirty() {
	}
	public boolean updateIsRequired() {
		return false;
	}
	public javax.vecmath.Matrix4d getProjectionMatrix( Camera camera ) {
		return null;
	}
	public double[] getActualPlane( OrthographicCamera sgOrthographicCamera ) {
		return null;
	}
	public double[] getActualPlane( PerspectiveCamera sgPerspectiveCamera ) {
		return null;
	}
	public double getActualHorizontalViewingAngle( SymmetricPerspectiveCamera sgSymmetricPerspectiveCamera ) {
		return Double.NaN;
	}
	public double getActualVerticalViewingAngle( SymmetricPerspectiveCamera sgSymmetricPerspectiveCamera ) {
		return Double.NaN;
	}
	public java.awt.Rectangle getActualViewport( Camera sgCamera ) {
		return new java.awt.Rectangle( getSize() );
	}
	public java.awt.Rectangle getViewport( Camera sgCamera ) {
		return null;
	}
	public void setViewport( Camera sgCamera, java.awt.Rectangle viewport ) {
	}
	public boolean isLetterboxedAsOpposedToDistorted( edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera ) {
        return true;
    }
	public void setIsLetterboxedAsOpposedToDistorted( edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera, boolean isLetterboxedAsOpposedToDistorted ) {
    }

	public java.awt.Image getOffscreenImage() {
		return null;
	}
    public java.awt.Image getZBufferImage() {
        return null;
    }
	public java.awt.Image getImage( edu.cmu.cs.stage3.alice.scenegraph.TextureMap textureMap ) {
		return null;
	}
	public java.awt.Graphics getGraphics( TextureMap textureMap ) {
		return null;
	}
	public void copyOffscreenImageToTextureMap( TextureMap textureMap ) {
	}

	public void clearAndRenderOffscreen() {
	}
	public boolean rendersOnEdgeTrianglesAsLines( edu.cmu.cs.stage3.alice.scenegraph.OrthographicCamera orthographicCamera ) {
        return false;
    }
	public void setRendersOnEdgeTrianglesAsLines( edu.cmu.cs.stage3.alice.scenegraph.OrthographicCamera orthographicCamera, boolean rendersOnEdgeTrianglesAsLines ) {
    }
	public java.awt.Graphics getOffscreenGraphics() {
		return null;
	}

	public edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo pick( int x, int y, boolean isSubElementRequired, boolean isOnlyFrontMostRequired ) {
		return null;
	}

	private double m_silhouetteThickness = 0.0;
	public void setSilhouetteThickness( double silhouetteThickness ) {
		m_silhouetteThickness = silhouetteThickness;
	}
	public double getSilhouetteThickness() {
		return m_silhouetteThickness;
	}
}
