package edu.cmu.cs.stage3.alice.scenegraph.renderer.util;

public class DirectXVersion {
	static {
		System.loadLibrary( "jni_directxversion" );
	}
	public static native double getVersion();
}