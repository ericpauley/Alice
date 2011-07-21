package edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer;

class BackgroundProxy extends edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.BackgroundProxy {
    //from ElementProxy
    
	protected native void createNativeInstance();
    
	protected native void releaseNativeInstance();
    //from BackgroundProxy
	
	protected native void onColorChange( double r, double g, double b, double a );
	
	protected native void onTextureMapChange( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextureMapProxy value );
	
	protected native void onTextureMapSourceRectangleChange( int x, int y, int width, int height );
}
