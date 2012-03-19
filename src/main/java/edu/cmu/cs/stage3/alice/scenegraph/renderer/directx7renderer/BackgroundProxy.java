package edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer;

class BackgroundProxy extends edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.BackgroundProxy {
	// from ElementProxy

	@Override
	protected native void createNativeInstance();

	@Override
	protected native void releaseNativeInstance();
	// from BackgroundProxy

	@Override
	protected native void onColorChange(double r, double g, double b, double a);

	@Override
	protected native void onTextureMapChange(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextureMapProxy value);

	@Override
	protected native void onTextureMapSourceRectangleChange(int x, int y, int width, int height);
}
