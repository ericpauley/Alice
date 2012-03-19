package edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer;

class TextProxy extends edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextProxy {
	// from ElementProxy

	@Override
	protected native void createNativeInstance();

	@Override
	protected native void releaseNativeInstance();
	// from GeometryProxy

	@Override
	protected native void onBoundChange(double x, double y, double z, double radius);
	// from TextProxy

	@Override
	protected native void onTextChange(java.lang.String value);

	@Override
	protected native void onFontChange(String name, int style, int size);

	@Override
	protected native void onExtrusionChange(double x, double y, double z);
}
