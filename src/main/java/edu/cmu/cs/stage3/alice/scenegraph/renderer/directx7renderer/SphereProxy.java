package edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer;

class SphereProxy extends edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.SphereProxy {
	// from ElementProxy

	@Override
	protected native void createNativeInstance();

	@Override
	protected native void releaseNativeInstance();
	// from GeometryProxy

	@Override
	protected native void onBoundChange(double x, double y, double z, double radius);
	// from SphereProxy

	@Override
	protected native void onRadiusChange(double value);
}