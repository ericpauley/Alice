package edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer;

class RenderCanvas extends edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.RenderCanvas {

	@Override
	protected native void createNativeInstance(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.RenderTargetAdapter renderTargetAdapter);

	@Override
	protected native void releaseNativeInstance();

	@Override
	protected synchronized native boolean acquireDrawingSurface();

	@Override
	protected synchronized native void releaseDrawingSurface();

	@Override
	protected synchronized native void swapBuffers();

	RenderCanvas(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.OnscreenRenderTarget onscreenRenderTarget) {
		super(onscreenRenderTarget);
	}
}
