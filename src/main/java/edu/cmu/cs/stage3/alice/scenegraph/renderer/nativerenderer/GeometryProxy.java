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

package edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer;

public abstract class GeometryProxy extends ElementProxy {
	protected abstract void onBoundChange(double x, double y, double z, double radius);
	private static java.util.Vector s_changed = new java.util.Vector(); // todo?
																		// initialCapacity
	private edu.cmu.cs.stage3.alice.scenegraph.Geometry getSceneGraphGeometry() {
		return (edu.cmu.cs.stage3.alice.scenegraph.Geometry) getSceneGraphElement();
	}
	protected boolean listenToBoundChanges() {
		return ((Renderer) getRenderer()).requiresBoundListening();
	}

	@Override
	public void initialize(edu.cmu.cs.stage3.alice.scenegraph.Element sgElement, edu.cmu.cs.stage3.alice.scenegraph.renderer.AbstractProxyRenderer renderer) {
		super.initialize(sgElement, renderer);
		if (listenToBoundChanges()) {
			onBoundChange();
		}
	}
	public void onBoundChange() {
		if (s_changed.contains(this)) {
			// pass
		} else {
			s_changed.addElement(this);
		}
	}
	static void updateBoundChanges() {
		if (s_changed.size() > 0) {
			java.util.Enumeration enum0 = s_changed.elements();
			while (enum0.hasMoreElements()) {
				GeometryProxy geometryProxy = (GeometryProxy) enum0.nextElement();
				edu.cmu.cs.stage3.math.Sphere sphere = geometryProxy.getSceneGraphGeometry().getBoundingSphere();
				double x = Double.NaN;
				double y = Double.NaN;
				double z = Double.NaN;
				double radius = Double.NaN;
				if (sphere != null) {
					javax.vecmath.Vector3d center = sphere.getCenter();
					if (center != null) {
						x = center.x;
						y = center.y;
						z = center.z;
					}
					radius = sphere.getRadius();
				}
				geometryProxy.onBoundChange(x, y, z, radius);
			}
			s_changed.removeAllElements();
		}
	}
}
