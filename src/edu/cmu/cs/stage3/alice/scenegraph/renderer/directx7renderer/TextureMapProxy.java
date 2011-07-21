package edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer;

class TextureMapProxy extends edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextureMapProxy {
    //from ElementProxy
    
	protected native void createNativeInstance();
    
	protected native void releaseNativeInstance();
    //from TextureMapProxy
	
	protected native void onImageChange( int[] pixels, int width, int height );
	
	protected native void onFormatChange( int value );
}
