package edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer;

class SpotLightProxy extends edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.SpotLightProxy {
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
    //from PointLightProxy
    
	protected native void onConstantAttenuationChange( double value );
    
	protected native void onLinearAttenuationChange( double value );
    
	protected native void onQuadraticAttenuationChange( double value );
    //from SpotLightProxy
	
	protected native void onInnerBeamAngleChange( double value );
	
	protected native void onOuterBeamAngleChange( double value );
	
	protected native void onFalloffChange( double value );
}
