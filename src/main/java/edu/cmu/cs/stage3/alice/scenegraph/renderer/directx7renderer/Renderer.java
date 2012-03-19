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

package edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer;

public class Renderer extends edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.Renderer {
	static {
		System.loadLibrary("jni_directx7renderer");
	}

	@Override
	protected native void createNativeInstance();

	@Override
	protected native void releaseNativeInstance();

	@Override
	protected native void pick(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.ComponentProxy componentProxy, double x, double y, double z, double planeMinX, double planeMinY, double planeMaxX, double planeMaxY, double nearClippingPlaneDistance, double farClippingPlaneDistance, boolean isSubElementRequired, boolean isOnlyFrontMostRequired, int[] atVisual, boolean[] atIsFrontFacing, int[] atSubElement, double[] atZ);

	@Override
	protected native void internalSetIsSoftwareEmulationForced(boolean isSoftwareEmulationForced);

	@Override
	protected boolean requiresHierarchyAndAbsoluteTransformationListening() {
		return true;
	}

	@Override
	protected boolean requiresBoundListening() {
		return true;
	}

	@Override
	protected edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.RenderTargetAdapter createRenderTargetAdapter(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.RenderTarget renderTarget) {
		return new RenderTargetAdapter(renderTarget);
	}

	@Override
	protected edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.RenderCanvas createRenderCanvas(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.OnscreenRenderTarget onscreenRenderTarget) {
		return new RenderCanvas(onscreenRenderTarget);
	}

	private static final String RENDERER_PACKAGE_NAME = "edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer.";
	private static final String SCENEGRAPH_PACKAGE_NAME = "edu.cmu.cs.stage3.alice.scenegraph.";
	private static final int SCENEGRAPH_PACKAGE_NAME_COUNT = SCENEGRAPH_PACKAGE_NAME.length();

	@Override
	protected edu.cmu.cs.stage3.alice.scenegraph.renderer.AbstractProxy createProxyFor(edu.cmu.cs.stage3.alice.scenegraph.Element sgElement) {
		Class sgClass = sgElement.getClass();
		while (sgClass != null) {
			String className = sgClass.getName();
			if (className.startsWith(SCENEGRAPH_PACKAGE_NAME)) {
				break;
			} else {
				sgClass = sgClass.getSuperclass();
			}
		}
		try {
			Class proxyClass = Class.forName(RENDERER_PACKAGE_NAME + sgClass.getName().substring(SCENEGRAPH_PACKAGE_NAME_COUNT) + "Proxy");
			return (edu.cmu.cs.stage3.alice.scenegraph.renderer.AbstractProxy) proxyClass.newInstance();
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		} catch (IllegalAccessException iae) {
			iae.printStackTrace();
		} catch (InstantiationException ie) {
			ie.printStackTrace();
		}
		return null;
	}

}
