package edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer;

class AppearanceProxy extends edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.AppearanceProxy {
	// from ElementProxy

	@Override
	protected native void createNativeInstance();

	@Override
	protected native void releaseNativeInstance();
	// from AppearanceProxy

	@Override
	protected native void onAmbientColorChange(double r, double g, double b, double a);

	@Override
	protected native void onDiffuseColorChange(double r, double g, double b, double a);

	@Override
	protected native void onFillingStyleChange(int value);

	@Override
	protected native void onShadingStyleChange(int value);

	@Override
	protected native void onOpacityChange(double value);

	@Override
	protected native void onSpecularHighlightColorChange(double r, double g, double b, double a);

	@Override
	protected native void onSpecularHighlightExponentChange(double value);

	@Override
	protected native void onEmissiveColorChange(double r, double g, double b, double a);

	@Override
	protected native void onDiffuseColorMapChange(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextureMapProxy value);

	@Override
	protected native void onOpacityMapChange(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextureMapProxy value);

	@Override
	protected native void onEmissiveColorMapChange(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextureMapProxy value);

	@Override
	protected native void onSpecularHighlightColorMapChange(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextureMapProxy value);

	@Override
	protected native void onBumpMapChange(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextureMapProxy value);

	@Override
	protected native void onDetailMapChange(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextureMapProxy value);
}
