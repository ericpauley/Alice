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

public abstract class RenderTarget extends edu.cmu.cs.stage3.alice.scenegraph.renderer.AbstractProxyRenderTarget {
    private RenderTargetAdapter m_adapter;
    protected RenderTargetAdapter getAdapter() {
        return m_adapter;
    }

    private java.util.Dictionary m_cameraViewportMap = new java.util.Hashtable();

	RenderTarget( Renderer renderer ) {
		super( renderer );
		m_adapter = renderer.createRenderTargetAdapter( this );
	}
	
	public void release() {
		super.release();
        m_adapter.release();
	}

    public java.awt.Graphics getOffscreenGraphics() {
        return m_adapter.getOffscreenGraphics();
    }
    public java.awt.Graphics getGraphics( edu.cmu.cs.stage3.alice.scenegraph.TextureMap textureMap ) {
        return m_adapter.getGraphics( (TextureMapProxy)getProxyFor( textureMap ) );
    }

	public javax.vecmath.Matrix4d getProjectionMatrix( edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera ) {
		return m_adapter.getProjectionMatrix( (CameraProxy)getProxyFor( sgCamera ) );
	}
	public double[] getActualPlane( edu.cmu.cs.stage3.alice.scenegraph.OrthographicCamera sgOrthographicCamera ) {
		return m_adapter.getActualPlane( (OrthographicCameraProxy)getProxyFor( sgOrthographicCamera ) );
	}
	public double[] getActualPlane( edu.cmu.cs.stage3.alice.scenegraph.PerspectiveCamera sgPerspectiveCamera ) {
		return m_adapter.getActualPlane( (PerspectiveCameraProxy)getProxyFor( sgPerspectiveCamera ) );
	}
	public double getActualHorizontalViewingAngle( edu.cmu.cs.stage3.alice.scenegraph.SymmetricPerspectiveCamera sgSymmetricPerspectiveCamera ) {
		return m_adapter.getActualHorizontalViewingAngle( (SymmetricPerspectiveCameraProxy)getProxyFor( sgSymmetricPerspectiveCamera ) );
	}
	public double getActualVerticalViewingAngle( edu.cmu.cs.stage3.alice.scenegraph.SymmetricPerspectiveCamera sgSymmetricPerspectiveCamera ) {
		return m_adapter.getActualVerticalViewingAngle( (SymmetricPerspectiveCameraProxy)getProxyFor( sgSymmetricPerspectiveCamera ) );
	}
	public java.awt.Rectangle getActualViewport( edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera ) {
		return m_adapter.getActualViewport( (CameraProxy)getProxyFor( sgCamera ) );
	}
	public java.awt.Rectangle getViewport( edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera ) {
		return (java.awt.Rectangle)m_cameraViewportMap.get( sgCamera );
	}
	public void setViewport( edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera, java.awt.Rectangle viewport ) {
		CameraProxy cameraProxy = (CameraProxy)getProxyFor( sgCamera );
		if( viewport==null ) {
			m_adapter.onViewportChange( cameraProxy, 0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE );
			m_cameraViewportMap.remove( sgCamera );
		} else {
			m_adapter.onViewportChange( cameraProxy, viewport.x, viewport.y, viewport.width, viewport.height );
			m_cameraViewportMap.put( sgCamera, viewport );
		}
	}
	public boolean rendersOnEdgeTrianglesAsLines( edu.cmu.cs.stage3.alice.scenegraph.OrthographicCamera sgOrthographicCamera ) {
		return m_adapter.rendersOnEdgeTrianglesAsLines( (OrthographicCameraProxy)getProxyFor( sgOrthographicCamera ) );
    }
	public void setRendersOnEdgeTrianglesAsLines( edu.cmu.cs.stage3.alice.scenegraph.OrthographicCamera sgOrthographicCamera, boolean rendersOnEdgeTrianglesAsLines ) {
		m_adapter.setRendersOnEdgeTrianglesAsLines( (OrthographicCameraProxy)getProxyFor( sgOrthographicCamera ), rendersOnEdgeTrianglesAsLines );
    }

	public boolean isLetterboxedAsOpposedToDistorted( edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera ) {
        return m_adapter.isLetterboxedAsOpposedToDistorted( (CameraProxy)getProxyFor( sgCamera ) );
    }
	public void setIsLetterboxedAsOpposedToDistorted( edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera, boolean isLetterboxedAsOpposedToDistorted ) {
        m_adapter.setIsLetterboxedAsOpposedToDistorted( (CameraProxy)getProxyFor( sgCamera ), isLetterboxedAsOpposedToDistorted );
    }

	public void commitAnyPendingChanges() {
		((Renderer)getRenderer()).commitAnyPendingChanges();
	}
	public void clearAndRenderOffscreen() {
		commitAnyPendingChanges();
		edu.cmu.cs.stage3.alice.scenegraph.Camera[] sgCameras = getCameras();
		if( sgCameras.length>0 ) {
			for( int i=0; i<sgCameras.length; i++ ) {
				CameraProxy cameraProxy = (CameraProxy)getProxyFor( sgCameras[i] );
				m_adapter.clear( cameraProxy, i==0 );
				onClear();
                ComponentProxy.updateAbsoluteTransformationChanges();
                GeometryProxy.updateBoundChanges();
                m_adapter.render( cameraProxy );
				onRender();
			}
        }
	}

	private CameraProxy getCameraProxy( int x, int y ) {
		if( x>=0 && y>=0 && x<m_adapter.getWidth() && y<m_adapter.getHeight() ) {
			edu.cmu.cs.stage3.alice.scenegraph.Camera[] sgCameras = getCameras();
			if( sgCameras!=null ) {
				for( int i=0; i<sgCameras.length; i++ ) {
					CameraProxy cameraProxy = (CameraProxy)getProxyFor( sgCameras[ i ] );
					java.awt.Rectangle viewport = m_adapter.getActualViewport( cameraProxy );
					if( viewport!=null ) {
						if( viewport.contains( x, y ) ) {
							return cameraProxy;
						}
					} else {
						return cameraProxy;
					}
				}
			}
		}
		return null;
	}

	public edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo pick( int x, int y, boolean isSubElementRequired, boolean isOnlyFrontMostRequired ) {
		commitAnyPendingChanges();
		CameraProxy cameraProxy = getCameraProxy( x, y );
		if( cameraProxy != null ) {
			int[] atVisual = { 0 };
			boolean[] atIsFrontFacing = { true };
			int[] atSubElement = { -1 };
			double[] atZ = { Double.NaN };

			m_adapter.pick( cameraProxy, x, y, isSubElementRequired, isOnlyFrontMostRequired, atVisual, atIsFrontFacing, atSubElement, atZ );

			edu.cmu.cs.stage3.alice.scenegraph.Visual[] sgVisuals = null;
			edu.cmu.cs.stage3.alice.scenegraph.Geometry[] sgGeometries = null;
			int[] subElements = null;
			boolean[] isFrontFacings = null;
			VisualProxy visualProxy = VisualProxy.map( atVisual[ 0 ] );
			if( visualProxy!=null ) {
				sgVisuals = new edu.cmu.cs.stage3.alice.scenegraph.Visual[ 1 ];
				sgVisuals[0] = (edu.cmu.cs.stage3.alice.scenegraph.Visual)visualProxy.getSceneGraphElement();
				sgGeometries = new edu.cmu.cs.stage3.alice.scenegraph.Geometry[ 1 ];
				sgGeometries[0] = sgVisuals[0].getGeometry();
                subElements = new int[ 1 ];
                subElements[ 0 ] = atSubElement[ 0 ];
                isFrontFacings = new boolean[ 1 ];
                isFrontFacings[ 0 ] = atIsFrontFacing[ 0 ];
			}
			return new PickInfo( this, (edu.cmu.cs.stage3.alice.scenegraph.Camera)cameraProxy.getSceneGraphElement(), x, y, sgVisuals, isFrontFacings, sgGeometries, subElements );
		} else {
			return null;
		}
	}
    /*
    private int fixEndian( int i ) {
        return ((i & 0x000000ff) << 24) |
               ((i & 0x0000ff00) << 8) |
               ((i & 0x00ff0000) >>> 8) |
               ((i & 0xff000000) >>> 24);
    }
    */
	public java.awt.Image getOffscreenImage() {
		int width = m_adapter.getWidth();
        int height = m_adapter.getHeight();
        int pitch = m_adapter.getPitch();
        int bitCount = m_adapter.getBitCount();
        int redBitMask = m_adapter.getRedBitMask();
        int greenBitMask = m_adapter.getGreenBitMask();
        int blueBitMask = m_adapter.getBlueBitMask();
        int alphaBitMask = m_adapter.getAlphaBitMask();

        int[] pixels = new int[ width * height ];
        m_adapter.getPixels( 0, 0, width, height, pitch, bitCount, redBitMask, greenBitMask, blueBitMask, alphaBitMask, pixels );
        java.awt.image.BufferedImage bufferedImage = new java.awt.image.BufferedImage( width, height, java.awt.image.BufferedImage.TYPE_INT_RGB );
        bufferedImage.setRGB( 0, 0, width, height, pixels, 0, width );
        return bufferedImage;
        /*
        java.awt.image.BufferedImage bufferedImage;
        switch( rgbBitCount ) {
        case 32:
        case 24:
            int[] pixels4 = new int[ width * height ];
            m_adapter.getPixels( 0, 0, width, height, pitch, rgbBitCount, redBitMask, greenBitMask, blueBitMask, alphaBitMask, pixels4 );
            bufferedImage = new java.awt.image.BufferedImage( width, height, java.awt.image.BufferedImage.TYPE_INT_RGB );
            bufferedImage.setRGB( 0, 0, width, height, pixels4, 0, width );
            break;
        case 16:
            short[] pixels2 = new short[ width * height ];

            m_adapter.getPixels( 0, 0, width, height, pitch, rgbBitCount, redBitMask, greenBitMask, blueBitMask, alphaBitMask, pixels2 );

            System.err.println( "red " + Integer.toBinaryString( redBitMask ) );
            System.err.println( "green " + Integer.toBinaryString( greenBitMask ) );
            System.err.println( "blue " + Integer.toBinaryString( blueBitMask ) );
            System.err.println( "alpha " + Integer.toBinaryString( alphaBitMask ) );

            bufferedImage = new java.awt.image.BufferedImage( width, height, java.awt.image.BufferedImage.TYPE_USHORT_565_RGB );
            bufferedImage.setRGB( 0, 0, width, height, pixels2, 0, width );

            break;
        }
        return bufferedImage;
        */

        //pitch = width * 4;
        //java.awt.image.DirectColorModel colorModel = new java.awt.image.DirectColorModel( rgbBitCount, redBitMask, greenBitMask, blueBitMask, alphaBitMask );
        //java.awt.image.ImageProducer imageProducer = new java.awt.image.MemoryImageSource( width, height, colorModel, pixels, 0, pitch );
        //return java.awt.Toolkit.getDefaultToolkit().createImage( imageProducer );
	}

    public java.awt.Image getZBufferImage() {
        int width = m_adapter.getWidth();
        int height = m_adapter.getHeight();
        int zPitch = m_adapter.getZBufferPitch();
        int zBitCount = m_adapter.getZBufferBitCount();
        int[] zPixels = new int[ width * height ];
        m_adapter.getZBufferPixels( 0, 0, width, height, zPitch, zBitCount, zPixels );
        java.awt.image.BufferedImage bufferedImage = new java.awt.image.BufferedImage( width, height, java.awt.image.BufferedImage.TYPE_INT_RGB );
        bufferedImage.setRGB( 0, 0, width, height, zPixels, 0, width );
        return bufferedImage;
    }

	public java.awt.Image getImage( edu.cmu.cs.stage3.alice.scenegraph.TextureMap textureMap ) {
		TextureMapProxy textureMapProxy = (TextureMapProxy)getProxyFor( textureMap );
		int width = m_adapter.getWidth( textureMapProxy );
		int height = m_adapter.getHeight( textureMapProxy );
		int pitch = m_adapter.getPitch( textureMapProxy );
		int bitCount = m_adapter.getBitCount( textureMapProxy );
		int redBitMask = m_adapter.getRedBitMask( textureMapProxy );
		int greenBitMask = m_adapter.getGreenBitMask( textureMapProxy );
		int blueBitMask = m_adapter.getBlueBitMask( textureMapProxy );
		int alphaBitMask = m_adapter.getAlphaBitMask( textureMapProxy );

		int[] pixels = new int[ width * height ];
		m_adapter.getPixels( textureMapProxy, 0, 0, width, height, pitch, bitCount, redBitMask, greenBitMask, blueBitMask, alphaBitMask, pixels );

		java.awt.image.BufferedImage bufferedImage = new java.awt.image.BufferedImage( width, height, java.awt.image.BufferedImage.TYPE_INT_RGB );
		bufferedImage.setRGB( 0, 0, width, height, pixels, 0, width );
		return bufferedImage;
	}

	public void copyOffscreenImageToTextureMap( edu.cmu.cs.stage3.alice.scenegraph.TextureMap textureMap ) {
		TextureMapProxy textureMapProxy = (TextureMapProxy)getProxyFor( textureMap );
		m_adapter.blt( textureMapProxy );
		markDirty();
	}

    public void setSilhouetteThickness( double silhouetteThickness ) {
        m_adapter.setSilhouetteThickness( silhouetteThickness );
    }
    public double getSilhouetteThickness() {
        return m_adapter.getSilhouetteThickness();
    }
}