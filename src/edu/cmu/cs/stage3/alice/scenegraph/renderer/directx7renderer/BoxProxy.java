package edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer;

class BoxProxy extends edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.BoxProxy {
    //from ElementProxy
    
	protected native void createNativeInstance();
    
	protected native void releaseNativeInstance();
    //from GeometryProxy
    
	protected native void onBoundChange( double x, double y, double z, double radius );
    //from BoxProxy
	
	protected native void onWidthChange( double value );
	
	protected native void onHeightChange( double value );
	
	protected native void onDepthChange( double value );
}
