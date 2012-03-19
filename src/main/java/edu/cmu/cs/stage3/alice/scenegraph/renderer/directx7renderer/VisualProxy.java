package edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer;

class VisualProxy extends edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.VisualProxy {
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
	// from VisualProxy

	@Override
	protected native void onFrontFacingAppearanceChange(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.AppearanceProxy value);

	@Override
	protected native void onBackFacingAppearanceChange(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.AppearanceProxy value);

	@Override
	protected native void onGeometryChange(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.GeometryProxy value);

	@Override
	protected native void onScaleChange(javax.vecmath.Matrix3d value);

	@Override
	protected native void onIsShowingChange(boolean isShowing);

	@Override
	protected native void onDisabledAffectorsChange(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.AffectorProxy[] affectors);
}
