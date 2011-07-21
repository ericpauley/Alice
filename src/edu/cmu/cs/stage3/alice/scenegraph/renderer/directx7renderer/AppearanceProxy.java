package edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer;

class AppearanceProxy extends edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.AppearanceProxy {
    //from ElementProxy
    
	protected native void createNativeInstance();
    
	protected native void releaseNativeInstance();
    //from AppearanceProxy
	
	protected native void onAmbientColorChange( double r, double g, double b, double a );
	
	protected native void onDiffuseColorChange( double r, double g, double b, double a );
	
	protected native void onFillingStyleChange( int value );
	
	protected native void onShadingStyleChange( int value );
	
	protected native void onOpacityChange( double value );
	
	protected native void onSpecularHighlightColorChange( double r, double g, double b, double a );
	
	protected native void onSpecularHighlightExponentChange( double value );
	
	protected native void onEmissiveColorChange( double r, double g, double b, double a );
	
	protected native void onDiffuseColorMapChange( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextureMapProxy value );
	
	protected native void onOpacityMapChange( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextureMapProxy value );
	
	protected native void onEmissiveColorMapChange( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextureMapProxy value );
	
	protected native void onSpecularHighlightColorMapChange( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextureMapProxy value );
	
	protected native void onBumpMapChange( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextureMapProxy value );
	
	protected native void onDetailMapChange( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextureMapProxy value );
}
