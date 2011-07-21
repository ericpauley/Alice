package edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer;

class SpriteProxy extends edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.SpriteProxy {
    //from ElementProxy
    
	protected native void createNativeInstance();
    
	protected native void releaseNativeInstance();
    //from GeometryProxy
    
	protected native void onBoundChange( double x, double y, double z, double radius );
    //from SpriteProxy
    
	protected native void onRadiusChange( double value );
}
