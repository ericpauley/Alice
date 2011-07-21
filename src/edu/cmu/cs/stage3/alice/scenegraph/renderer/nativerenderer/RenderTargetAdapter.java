package edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer;

public abstract class RenderTargetAdapter {
	public abstract void reset();
    public abstract void release();

    public abstract java.awt.Graphics getOffscreenGraphics();
    public abstract java.awt.Graphics getGraphics( TextureMapProxy textureMapProxy );

	public abstract void clear( CameraProxy cameraProxy, boolean all );
	public abstract void render( CameraProxy cameraProxy );
	public abstract void pick( CameraProxy cameraProxy, int x, int y, boolean isSubElementRequired, boolean isOnlyFrontMostRequired, int[] atVisual, boolean[] atIsFrontFacing, int[] atSubElement, double[] atZ );
	public abstract void blt( TextureMapProxy textureMapProxy );

	public abstract void onViewportChange( CameraProxy cameraProxy, int x, int y, int width, int height );

	public abstract double[] getActualPlane( OrthographicCameraProxy orthographicCameraProxy );
	public abstract double[] getActualPlane( PerspectiveCameraProxy perspectiveCameraProxy );
	public abstract double getActualHorizontalViewingAngle( SymmetricPerspectiveCameraProxy symmetricPerspectiveCameraProxy );
	public abstract double getActualVerticalViewingAngle( SymmetricPerspectiveCameraProxy symmetricPerspectiveCameraProxy );
	public abstract java.awt.Rectangle getActualViewport( CameraProxy cameraProxy );
	public abstract javax.vecmath.Matrix4d getProjectionMatrix( CameraProxy cameraProxy );
	public abstract boolean isLetterboxedAsOpposedToDistorted( CameraProxy cameraProxy );
	public abstract void setIsLetterboxedAsOpposedToDistorted( CameraProxy cameraProxy, boolean isLetterboxedAsOpposedToDistorted );
	public abstract boolean rendersOnEdgeTrianglesAsLines( OrthographicCameraProxy orthographicCameraProxy );
	public abstract void setRendersOnEdgeTrianglesAsLines( OrthographicCameraProxy orthographicCameraProxy, boolean rendersOnEdgeTrianglesAsLines );

	public abstract void setDesiredSize( int width, int height );

	public abstract int getWidth();
	public abstract int getHeight();
    public abstract int getPitch();
    public abstract int getBitCount();
    public abstract int getRedBitMask();
    public abstract int getGreenBitMask();
    public abstract int getBlueBitMask();
    public abstract int getAlphaBitMask();
    public abstract void getPixels( int x, int y, int width, int height, int pitch, int bitCount, int redBitMask, int greenBitMask, int blueBitMask, int alphaBitMask, int[] pixels );

    public abstract int getZBufferPitch();
    public abstract int getZBufferBitCount();
    public abstract void getZBufferPixels( int x, int y, int width, int height, int zPitch, int zBitCount, int[] zPixels );

	public abstract int getWidth( TextureMapProxy textureMapProxy );
	public abstract int getHeight( TextureMapProxy textureMapProxy );
	public abstract int getPitch( TextureMapProxy textureMapProxy );
	public abstract int getBitCount( TextureMapProxy textureMapProxy );
	public abstract int getRedBitMask( TextureMapProxy textureMapProxy );
	public abstract int getGreenBitMask( TextureMapProxy textureMapProxy );
	public abstract int getBlueBitMask( TextureMapProxy textureMapProxy );
	public abstract int getAlphaBitMask( TextureMapProxy textureMapProxy );
	public abstract void getPixels( TextureMapProxy textureMapProxy, int x, int y, int width, int height, int pitch, int bitCount, int redBitMask, int greenBitMask, int blueBitMask, int alphaBitMask, int[] pixels );

	public abstract void setSilhouetteThickness( double silhouetteThickness );
	public abstract double getSilhouetteThickness();
}