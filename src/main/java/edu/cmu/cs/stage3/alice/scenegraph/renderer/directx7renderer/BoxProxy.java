package edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer;

class BoxProxy extends edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.BoxProxy {
	// from ElementProxy

	@Override
	protected native void createNativeInstance();

	@Override
	protected native void releaseNativeInstance();
	// from GeometryProxy

	@Override
	protected native void onBoundChange(double x, double y, double z, double radius);
	// from BoxProxy

	@Override
	protected native void onWidthChange(double value);

	@Override
	protected native void onHeightChange(double value);

	@Override
	protected native void onDepthChange(double value);
}
