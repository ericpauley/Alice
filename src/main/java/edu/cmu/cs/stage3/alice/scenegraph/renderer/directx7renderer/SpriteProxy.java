package edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer;

class SpriteProxy extends edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.SpriteProxy {
	// from ElementProxy

	@Override
	protected native void createNativeInstance();

	@Override
	protected native void releaseNativeInstance();
	// from GeometryProxy

	@Override
	protected native void onBoundChange(double x, double y, double z, double radius);
	// from SpriteProxy

	@Override
	protected native void onRadiusChange(double value);
}
