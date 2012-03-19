package edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer;

class TextureMapProxy extends edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextureMapProxy {
	// from ElementProxy

	@Override
	protected native void createNativeInstance();

	@Override
	protected native void releaseNativeInstance();
	// from TextureMapProxy

	@Override
	protected native void onImageChange(int[] pixels, int width, int height);

	@Override
	protected native void onFormatChange(int value);
}
