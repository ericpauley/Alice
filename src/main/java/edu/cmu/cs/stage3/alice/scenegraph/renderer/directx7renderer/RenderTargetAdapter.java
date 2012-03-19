package edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer;

public class RenderTargetAdapter extends edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.RenderTargetAdapter {
	private edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.RenderTarget m_renderTarget;
	public RenderTargetAdapter(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.RenderTarget renderTarget) {
		m_renderTarget = renderTarget;
		createNativeInstance((Renderer) renderTarget.getRenderer());
	}

	@Override
	public void release() {
		releaseNativeInstance();
	}

	@Override
	public java.awt.Graphics getOffscreenGraphics() {
		return new Graphics(m_renderTarget);
	}

	@Override
	public java.awt.Graphics getGraphics(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextureMapProxy textureMapProxy) {
		if (textureMapProxy != null) {
			return new Graphics(m_renderTarget, textureMapProxy);
		} else {
			return null;
		}
	}

	private int m_nativeInstance;
	protected native void createNativeInstance(Renderer renderer);
	protected native void releaseNativeInstance();

	@Override
	public native void reset();

	@Override
	public native void clear(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.CameraProxy cameraProxy, boolean all);

	@Override
	public native void render(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.CameraProxy cameraProxy);

	@Override
	public native void pick(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.CameraProxy cameraProxy, int x, int y, boolean isSubElementRequired, boolean isOnlyFrontMostRequired, int[] atVisual, boolean[] atIsFrontFacing, int[] atSubElement, double[] atZ);

	@Override
	public native void blt(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextureMapProxy textureMapProxy);

	@Override
	public native void onViewportChange(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.CameraProxy cameraProxy, int x, int y, int width, int height);

	@Override
	public native double[] getActualPlane(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.OrthographicCameraProxy orthographicCameraProxy);

	@Override
	public native double[] getActualPlane(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.PerspectiveCameraProxy perspectiveCameraProxy);

	@Override
	public native double getActualHorizontalViewingAngle(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.SymmetricPerspectiveCameraProxy symmetricPerspectiveCameraProxy);

	@Override
	public native double getActualVerticalViewingAngle(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.SymmetricPerspectiveCameraProxy symmetricPerspectiveCameraProxy);

	@Override
	public native java.awt.Rectangle getActualViewport(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.CameraProxy cameraProxy);

	@Override
	public native javax.vecmath.Matrix4d getProjectionMatrix(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.CameraProxy cameraProxy);

	@Override
	public native boolean isLetterboxedAsOpposedToDistorted(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.CameraProxy cameraProxy);

	@Override
	public native void setIsLetterboxedAsOpposedToDistorted(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.CameraProxy cameraProxy, boolean isLetterboxedAsOpposedToDistorted);

	@Override
	public native boolean rendersOnEdgeTrianglesAsLines(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.OrthographicCameraProxy orthographicCameraProxy);

	@Override
	public native void setRendersOnEdgeTrianglesAsLines(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.OrthographicCameraProxy orthographicCameraProxy, boolean rendersOnEdgeTrianglesAsLines);

	@Override
	public native void setDesiredSize(int width, int height);

	@Override
	public native int getWidth();

	@Override
	public native int getHeight();

	@Override
	public native int getPitch();

	@Override
	public native int getBitCount();

	@Override
	public native int getRedBitMask();

	@Override
	public native int getGreenBitMask();

	@Override
	public native int getBlueBitMask();

	@Override
	public native int getAlphaBitMask();

	@Override
	public native void getPixels(int x, int y, int width, int height, int pitch, int bitCount, int redBitMask, int greenBitMask, int blueBitMask, int alphaBitMask, int[] pixels);

	@Override
	public native int getWidth(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextureMapProxy textureMapProxy);

	@Override
	public native int getHeight(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextureMapProxy textureMapProxy);

	@Override
	public native int getPitch(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextureMapProxy textureMapProxy);

	@Override
	public native int getBitCount(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextureMapProxy textureMapProxy);

	@Override
	public native int getRedBitMask(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextureMapProxy textureMapProxy);

	@Override
	public native int getGreenBitMask(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextureMapProxy textureMapProxy);

	@Override
	public native int getBlueBitMask(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextureMapProxy textureMapProxy);

	@Override
	public native int getAlphaBitMask(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextureMapProxy textureMapProxy);

	@Override
	public native void getPixels(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextureMapProxy textureMapProxy, int x, int y, int width, int height, int pitch, int bitCount, int redBitMask, int greenBitMask, int blueBitMask, int alphaBitMask, int[] pixels);

	@Override
	public native int getZBufferPitch();

	@Override
	public native int getZBufferBitCount();

	@Override
	public native void getZBufferPixels(int x, int y, int width, int height, int zPitch, int zBitCount, int[] zPixels);

	@Override
	public native void setSilhouetteThickness(double silhouetteThickness);

	@Override
	public native double getSilhouetteThickness();
}