package edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer;

class CylinderProxy extends edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.CylinderProxy {
    //from ElementProxy
    
	protected native void createNativeInstance();
    
	protected native void releaseNativeInstance();
    //from GeometryProxy
    
	protected native void onBoundChange( double x, double y, double z, double radius );
    //from CylinderProxy
	
	protected native void onBaseRadiusChange( double value );
	
	protected native void onTopRadiusChange( double value );
	
	protected native void onHeightChange( double value );
}