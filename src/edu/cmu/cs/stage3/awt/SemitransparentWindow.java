/*
 * Copyright (c) 1999-2003, Carnegie Mellon University. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 
 * 3. Products derived from the software may not be called "Alice",
 *    nor may "Alice" appear in their name, without prior written
 *    permission of Carnegie Mellon University.
 * 
 * 4. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *    "This product includes software developed by Carnegie Mellon University"
 */

package edu.cmu.cs.stage3.awt;

public class SemitransparentWindow {
	private static boolean s_successfullyLoadedLibrary;
	static {
		try {
			System.loadLibrary( "jni_semitransparent" );
			s_successfullyLoadedLibrary = true;
			//} catch( UnsatisfiedLinkError ule ) {
		} catch( Throwable t ) {
			s_successfullyLoadedLibrary = false;
		}
	}

	private static native boolean isSupportedNative();
	public static boolean isSupported() {
		if( s_successfullyLoadedLibrary ) {
			return isSupportedNative();
		} else {
			return false;
		}
	}

	private int m_nativeData = 0;

	private native void createNative();
	private native void destroyNative();
	private native void showNative();
	private native void hideNative();
	private native void setLocationOnScreenNative( int x, int y );
	private native void setOpacityNative( double opacity );
	private native void setImageNative( int width, int height, int[] pixels );

	public SemitransparentWindow() {
		if( SemitransparentWindow.isSupported() ) {
			createNative();
		}
	}
	
	protected void finalize() throws Throwable {
		if( SemitransparentWindow.isSupported() ) {
			destroyNative();
		}
		super.finalize();
	}
	public void setImage( java.awt.Image image ) throws InterruptedException {
		if( SemitransparentWindow.isSupported() ) {
			int width = edu.cmu.cs.stage3.image.ImageUtilities.getWidth( image );
			int height = edu.cmu.cs.stage3.image.ImageUtilities.getHeight( image );
			int[] pixels = edu.cmu.cs.stage3.image.ImageUtilities.getPixels( image, width, height );
			setImageNative( width, height, pixels );
		}
	}

	public void show() {
		if( SemitransparentWindow.isSupported() ) {
			showNative();
		}
	}
	public void hide() {
		if( SemitransparentWindow.isSupported() ) {
			hideNative();
		}
	}
	public void setLocationOnScreen( int x, int y ) {
		if( SemitransparentWindow.isSupported() ) {
			setLocationOnScreenNative( x, y );
		}
	}
	public void setOpacity( double opacity ) {
		if( SemitransparentWindow.isSupported() ) {
			setOpacityNative( opacity );
		}
	}
}
