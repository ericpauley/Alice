package edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer;

class VisualProxy extends edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.VisualProxy {
    //from ElementProxy
    
	protected native void createNativeInstance();
    
	protected native void releaseNativeInstance();
    //from ComponentProxy
    
	protected native void onAbsoluteTransformationChange( javax.vecmath.Matrix4d m );
    
	protected native void addToScene( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.SceneProxy scene );
    
	protected native void removeFromScene( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.SceneProxy scene );
	//from VisualProxy
	
	protected native void onFrontFacingAppearanceChange( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.AppearanceProxy value );
	
	protected native void onBackFacingAppearanceChange( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.AppearanceProxy value );
	
	protected native void onGeometryChange( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.GeometryProxy value );
	
	protected native void onScaleChange( javax.vecmath.Matrix3d value );
	
	protected native void onIsShowingChange( boolean isShowing );
	
	protected native void onDisabledAffectorsChange( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.AffectorProxy[] affectors );
}
