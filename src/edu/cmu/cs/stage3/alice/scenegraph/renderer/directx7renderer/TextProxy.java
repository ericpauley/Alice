package edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer;

class TextProxy extends edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextProxy {
    //from ElementProxy
    
	protected native void createNativeInstance();
    
	protected native void releaseNativeInstance();
    //from GeometryProxy
    
	protected native void onBoundChange( double x, double y, double z, double radius );
    //from TextProxy
	
	protected native void onTextChange( java.lang.String value );
	
	protected native void onFontChange( String name, int style, int size );
	
	protected native void onExtrusionChange( double x, double y, double z );
}
