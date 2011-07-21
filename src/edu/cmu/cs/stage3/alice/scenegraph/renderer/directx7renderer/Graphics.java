package edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer;

class Graphics extends edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.Graphics {
    public Graphics( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.RenderTarget renderTarget ) {
        super( renderTarget );
    }
    public Graphics( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.RenderTarget renderTarget, edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextureMapProxy textureMapProxy ) {
        super( renderTarget, textureMapProxy );
    }

    
	protected native void createNativeInstance( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.RenderTargetAdapter renderTargetAdapter );
    
	protected native void createNativeInstance( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.RenderTargetAdapter renderTargetAdapter, edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextureMapProxy textureMapProxy );
    
	protected native void releaseNativeInstance();

	
	protected native void setFont( String family, String name, boolean isBold, boolean isItalic, int size );

	
	public native void translate( int x, int y );
	
	public native java.awt.Color getColor();
	
	public native void setColor( java.awt.Color c );
	
	public native void setPaintMode();
	
	public native void setXORMode( java.awt.Color c1 );
	
	public native void copyArea( int x, int y, int width, int height, int dx, int dy );
	
	public native void drawLine(int x1, int y1, int x2, int y2);
	
	public native void fillRect( int x, int y, int width, int height );
	
	public native void clearRect( int x, int y, int width, int height );
	
	public native void drawRoundRect( int x, int y, int width, int height, int arcWidth, int arcHeight );
	
	public native void fillRoundRect( int x, int y, int width, int height, int arcWidth, int arcHeight );
	
	public native void drawOval( int x, int y, int width, int height );
	
	public native void fillOval( int x, int y, int width, int height );
	
	public native void drawArc( int x, int y, int width, int height, int startAngle, int arcAngle );
	
	public native void fillArc( int x, int y, int width, int height, int startAngle, int arcAngle );
	
	public native void drawPolyline( int xPoints[], int yPoints[], int nPoints );
	
	public native void drawPolygon( int xPoints[], int yPoints[], int nPoints );
	
	public native void fillPolygon( int xPoints[], int yPoints[], int nPoints );
	
	public native void drawString( String str, int x, int y );
	
	public native void drawChars( char[] data, int offset, int length, int x, int y );
	
	public native void drawBytes( byte[] data, int offset, int length, int x, int y );
	
	public String toString() {
		return "edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer.Graphics["+hashCode()+"]";
	}
}
