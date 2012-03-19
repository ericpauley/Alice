package edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer;

class IndexedTriangleArrayProxy extends edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.IndexedTriangleArrayProxy {
	// from ElementProxy

	@Override
	protected native void createNativeInstance();

	@Override
	protected native void releaseNativeInstance();
	// from GeometryProxy

	@Override
	protected native void onBoundChange(double x, double y, double z, double radius);
	// from VertexGeometryProxy

	@Override
	protected native void onVerticesFormatAndLengthChange(int format, int length);

	@Override
	protected native void onVerticesVertexPositionChange(int index, double x, double y, double z);

	@Override
	protected native void onVerticesVertexNormalChange(int index, double i, double j, double k);

	@Override
	protected native void onVerticesVertexDiffuseColorChange(int index, float r, float g, float b, float a);

	@Override
	protected native void onVerticesVertexSpecularHighlightColorChange(int index, float r, float g, float b, float a);

	@Override
	protected native void onVerticesVertexTextureCoordinate0Change(int index, float u, float v);

	@Override
	protected native void onVerticesBeginChange();

	@Override
	protected native void onVerticesEndChange();

	@Override
	protected native void onVertexLowerBoundChange(int value);

	@Override
	protected native void onVertexUpperBoundChange(int value);
	// from IndexedTriangleArrayProxy

	@Override
	protected native void onIndicesChange(int[] value);

	@Override
	protected native void onIndexLowerBoundChange(int value);

	@Override
	protected native void onIndexUpperBoundChange(int value);
}
