package edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer;

class ProjectionCameraProxy extends edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.ProjectionCameraProxy {
    //from ElementProxy
    
	protected native void createNativeInstance();
    
	protected native void releaseNativeInstance();
    //from ComponentProxy
    
	protected native void onAbsoluteTransformationChange( javax.vecmath.Matrix4d m );
    
	protected native void addToScene( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.SceneProxy scene );
    
	protected native void removeFromScene( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.SceneProxy scene );
    //from CameraProxy
    
	protected native void onNearClippingPlaneDistanceChange( double value );
    
	protected native void onFarClippingPlaneDistanceChange( double value );
    
	protected native void onBackgroundChange( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.BackgroundProxy value );
    //from ProjectionCameraProxy
	
	protected native void onProjectionChange( javax.vecmath.Matrix4d m );
}
