package edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer;

class SymmetricPerspectiveCameraProxy extends edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.SymmetricPerspectiveCameraProxy {
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
    //from SymmetricPerspectiveCameraProxy
	
	protected native void onVerticalViewingAngleChange( double value );
	
	protected native void onHorizontalViewingAngleChange( double value );
}
