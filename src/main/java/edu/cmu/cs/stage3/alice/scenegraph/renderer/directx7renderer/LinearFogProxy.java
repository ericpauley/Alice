package edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer;

class LinearFogProxy extends edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.LinearFogProxy {
	// from ElementProxy

	@Override
	protected native void createNativeInstance();

	@Override
	protected native void releaseNativeInstance();
	// from ComponentProxy

	@Override
	protected native void onAbsoluteTransformationChange(javax.vecmath.Matrix4d m);

	@Override
	protected native void addToScene(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.SceneProxy scene);

	@Override
	protected native void removeFromScene(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.SceneProxy scene);
	// from FogProxy

	@Override
	protected native void onColorChange(double r, double g, double b, double a);
	// from LinearFogProxy

	@Override
	protected native void onNearDistanceChange(double value);

	@Override
	protected native void onFarDistanceChange(double value);
}
