package edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer;

class Graphics extends edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.Graphics {
	public Graphics(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.RenderTarget renderTarget) {
		super(renderTarget);
	}
	public Graphics(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.RenderTarget renderTarget, edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextureMapProxy textureMapProxy) {
		super(renderTarget, textureMapProxy);
	}

	@Override
	protected native void createNativeInstance(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.RenderTargetAdapter renderTargetAdapter);

	@Override
	protected native void createNativeInstance(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.RenderTargetAdapter renderTargetAdapter, edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextureMapProxy textureMapProxy);

	@Override
	protected native void releaseNativeInstance();

	@Override
	protected native void setFont(String family, String name, boolean isBold, boolean isItalic, int size);

	@Override
	public native void translate(int x, int y);

	@Override
	public native java.awt.Color getColor();

	@Override
	public native void setColor(java.awt.Color c);

	@Override
	public native void setPaintMode();

	@Override
	public native void setXORMode(java.awt.Color c1);

	@Override
	public native void copyArea(int x, int y, int width, int height, int dx, int dy);

	@Override
	public native void drawLine(int x1, int y1, int x2, int y2);

	@Override
	public native void fillRect(int x, int y, int width, int height);

	@Override
	public native void clearRect(int x, int y, int width, int height);

	@Override
	public native void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight);

	@Override
	public native void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight);

	@Override
	public native void drawOval(int x, int y, int width, int height);

	@Override
	public native void fillOval(int x, int y, int width, int height);

	@Override
	public native void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle);

	@Override
	public native void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle);

	@Override
	public native void drawPolyline(int xPoints[], int yPoints[], int nPoints);

	@Override
	public native void drawPolygon(int xPoints[], int yPoints[], int nPoints);

	@Override
	public native void fillPolygon(int xPoints[], int yPoints[], int nPoints);

	@Override
	public native void drawString(String str, int x, int y);

	@Override
	public native void drawChars(char[] data, int offset, int length, int x, int y);

	@Override
	public native void drawBytes(byte[] data, int offset, int length, int x, int y);

	@Override
	public String toString() {
		return "edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer.Graphics[" + hashCode() + "]";
	}
}
