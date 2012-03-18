package edu.cmu.cs.stage3.alice.scenegraph.renderer.util;

public class OpenGLVersion {
	static {
		System.loadLibrary( "jni_openglversion" );
	}
	public static native double getVersion();
}