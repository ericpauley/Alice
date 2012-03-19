package edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer;

class CylinderProxy extends edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.CylinderProxy {
	// from ElementProxy

	@Override
	protected native void createNativeInstance();

	@Override
	protected native void releaseNativeInstance();
	// from GeometryProxy

	@Override
	protected native void onBoundChange(double x, double y, double z, double radius);
	// from CylinderProxy

	@Override
	protected native void onBaseRadiusChange(double value);

	@Override
	protected native void onTopRadiusChange(double value);

	@Override
	protected native void onHeightChange(double value);
}