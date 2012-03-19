package edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer;

class SceneProxy extends edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.SceneProxy {
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
	// from SceneProxy

	@Override
	protected native void onBackgroundChange(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.BackgroundProxy value);
}
