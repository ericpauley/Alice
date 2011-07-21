package edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer;

public class RenderTargetAdapter extends edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.RenderTargetAdapter {
    private edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.RenderTarget m_renderTarget;
    public RenderTargetAdapter( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.RenderTarget renderTarget ) {
        m_renderTarget = renderTarget;
        createNativeInstance( (Renderer)renderTarget.getRenderer() );
    }
    
	public void release() {
        releaseNativeInstance();
    }

    
	public java.awt.Graphics getOffscreenGraphics() {
        return new Graphics( m_renderTarget );
    }
    
	public java.awt.Graphics getGraphics( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextureMapProxy textureMapProxy ) {
        if( textureMapProxy!=null ) {
            return new Graphics( m_renderTarget, textureMapProxy );
        } else {
            return null;
        }
    }

    private int m_nativeInstance;
    protected native void createNativeInstance( Renderer renderer );
    protected native void releaseNativeInstance();
	
	public native void reset();

	
	public native void clear( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.CameraProxy cameraProxy, boolean all );
	
	public native void render( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.CameraProxy cameraProxy );
	
	public native void pick( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.CameraProxy cameraProxy, int x, int y, boolean isSubElementRequired, boolean isOnlyFrontMostRequired, int[] atVisual, boolean[] atIsFrontFacing, int[] atSubElement, double[] atZ );
	
	public native void blt( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextureMapProxy textureMapProxy );

	
	public native void onViewportChange( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.CameraProxy cameraProxy, int x, int y, int width, int height );

	
	public native double[] getActualPlane( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.OrthographicCameraProxy orthographicCameraProxy );
	
	public native double[] getActualPlane( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.PerspectiveCameraProxy perspectiveCameraProxy );
	
	public native double getActualHorizontalViewingAngle( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.SymmetricPerspectiveCameraProxy symmetricPerspectiveCameraProxy );
	
	public native double getActualVerticalViewingAngle( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.SymmetricPerspectiveCameraProxy symmetricPerspectiveCameraProxy );
	
	public native java.awt.Rectangle getActualViewport( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.CameraProxy cameraProxy );
	
	public native javax.vecmath.Matrix4d getProjectionMatrix( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.CameraProxy cameraProxy );
	
	public native boolean isLetterboxedAsOpposedToDistorted( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.CameraProxy cameraProxy );
	
	public native void setIsLetterboxedAsOpposedToDistorted( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.CameraProxy cameraProxy, boolean isLetterboxedAsOpposedToDistorted );
	
	public native boolean rendersOnEdgeTrianglesAsLines( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.OrthographicCameraProxy orthographicCameraProxy );
	
	public native void setRendersOnEdgeTrianglesAsLines( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.OrthographicCameraProxy orthographicCameraProxy, boolean rendersOnEdgeTrianglesAsLines );

	
	public native void setDesiredSize( int width, int height );

	
	public native int getWidth();
	
	public native int getHeight();
    
	public native int getPitch();
    
	public native int getBitCount();
    
	public native int getRedBitMask();
    
	public native int getGreenBitMask();
    
	public native int getBlueBitMask();
    
	public native int getAlphaBitMask();
    
	public native void getPixels( int x, int y, int width, int height, int pitch, int bitCount, int redBitMask, int greenBitMask, int blueBitMask, int alphaBitMask, int[] pixels );

	
	public native int getWidth( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextureMapProxy textureMapProxy );
	
	public native int getHeight( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextureMapProxy textureMapProxy );
	
	public native int getPitch( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextureMapProxy textureMapProxy );
	
	public native int getBitCount( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextureMapProxy textureMapProxy );
	
	public native int getRedBitMask( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextureMapProxy textureMapProxy );
	
	public native int getGreenBitMask( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextureMapProxy textureMapProxy );
	
	public native int getBlueBitMask( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextureMapProxy textureMapProxy );
	
	public native int getAlphaBitMask( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextureMapProxy textureMapProxy );
	
	public native void getPixels(  edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextureMapProxy textureMapProxy, int x, int y, int width, int height, int pitch, int bitCount, int redBitMask, int greenBitMask, int blueBitMask, int alphaBitMask, int[] pixels );

    
	public native int getZBufferPitch();
    
	public native int getZBufferBitCount();
    
	public native void getZBufferPixels( int x, int y, int width, int height, int zPitch, int zBitCount, int[] zPixels );

	
	public native void setSilhouetteThickness( double silhouetteThickness );
	
	public native double getSilhouetteThickness();
}