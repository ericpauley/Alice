package edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer;

class LinearFogProxy extends edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.LinearFogProxy {
    //from ElementProxy
    
	protected native void createNativeInstance();
    
	protected native void releaseNativeInstance();
    //from ComponentProxy
    
	protected native void onAbsoluteTransformationChange( javax.vecmath.Matrix4d m );
    
	protected native void addToScene( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.SceneProxy scene );
    
	protected native void removeFromScene( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.SceneProxy scene );
    //from FogProxy
    
	protected native void onColorChange( double r, double g, double b, double a );
    //from LinearFogProxy
	
	protected native void onNearDistanceChange( double value );
	
	protected native void onFarDistanceChange( double value );
}
