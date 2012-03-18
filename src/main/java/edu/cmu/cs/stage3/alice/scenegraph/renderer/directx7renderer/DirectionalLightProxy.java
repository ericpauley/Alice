package edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer;

class DirectionalLightProxy extends edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.DirectionalLightProxy {
    //from ElementProxy
    
	protected native void createNativeInstance();
    
	protected native void releaseNativeInstance();
    //from ComponentProxy
    
	protected native void onAbsoluteTransformationChange( javax.vecmath.Matrix4d m );
    
	protected native void addToScene( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.SceneProxy scene );
    
	protected native void removeFromScene( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.SceneProxy scene );
    //from LightProxy
    
	protected native void onColorChange( double r, double g, double b, double a );
    
	protected native void onBrightnessChange( double value );
    
	protected native void onRangeChange( double value );
}
