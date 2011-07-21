package edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer;

class TriangleStripProxy extends edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TriangleStripProxy {
    //from ElementProxy
    
	protected native void createNativeInstance();
    
	protected native void releaseNativeInstance();
    //from GeometryProxy
    
	protected native void onBoundChange( double x, double y, double z, double radius );
    //from VertexGeometryProxy
    
	protected native void onVerticesFormatAndLengthChange( int format, int length );
    
	protected native void onVerticesVertexPositionChange( int index, double x, double y, double z );
    
	protected native void onVerticesVertexNormalChange( int index, double i, double j, double k );
    
	protected native void onVerticesVertexDiffuseColorChange( int index, float r, float g, float b, float a );
    
	protected native void onVerticesVertexSpecularHighlightColorChange( int index, float r, float g, float b, float a );
    
	protected native void onVerticesVertexTextureCoordinate0Change( int index, float u, float v );
    
	protected native void onVerticesBeginChange();
    
	protected native void onVerticesEndChange();
    
	protected native void onVertexLowerBoundChange( int value );
    
	protected native void onVertexUpperBoundChange( int value );
}
