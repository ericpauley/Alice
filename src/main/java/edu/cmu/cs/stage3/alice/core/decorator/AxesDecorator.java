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

package edu.cmu.cs.stage3.alice.core.decorator;

import edu.cmu.cs.stage3.alice.core.Decorator;
import edu.cmu.cs.stage3.alice.core.ReferenceFrame;

public class AxesDecorator extends Decorator {
	private edu.cmu.cs.stage3.alice.scenegraph.LineArray m_sgLineArray = null;
	private ReferenceFrame m_referenceFrame = null;
	private double m_scale = 1.0;

	public double getScale() {
		return m_scale;
	}
	public void setScale(double scale) {
		m_scale = scale;
		markDirty();
	}

	@Override
	public ReferenceFrame getReferenceFrame() {
		return m_referenceFrame;
	}
	public void setReferenceFrame(ReferenceFrame referenceFrame) {
		if (referenceFrame != m_referenceFrame) {
			m_referenceFrame = referenceFrame;
			markDirty();
			updateIfShowing();
		}
	}

	@Override
	public void internalRelease(int pass) {
		switch (pass) {
			case 2 :
				if (m_sgLineArray != null) {
					m_sgLineArray.release();
					m_sgLineArray = null;
				}
				break;
		}
		super.internalRelease(pass);
	}

	@Override
	protected void update() {
		super.update();
		edu.cmu.cs.stage3.math.Box box = m_referenceFrame.getBoundingBox();
		if (box == null || box.getMinimum() == null || box.getMaximum() == null) {
			return;
		}
		boolean requiresVerticesToBeUpdated = isDirty();
		if (m_sgLineArray == null) {
			m_sgLineArray = new edu.cmu.cs.stage3.alice.scenegraph.LineArray();
			m_sgVisual.setGeometry(m_sgLineArray);
			m_sgLineArray.setBonus(getReferenceFrame());
			requiresVerticesToBeUpdated = true;
		}
		if (requiresVerticesToBeUpdated) {
			edu.cmu.cs.stage3.alice.scenegraph.Color color = edu.cmu.cs.stage3.alice.scenegraph.Color.YELLOW;
			edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[] vertices = new edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[6];
			javax.vecmath.Vector3d min = box.getMinimum();
			javax.vecmath.Vector3d max = box.getMaximum();
			javax.vecmath.Vector3d center = edu.cmu.cs.stage3.math.MathUtilities.add(min, max);
			center.scale(0.5);
			if (m_scale != 0) {
				javax.vecmath.Vector3d dmin = edu.cmu.cs.stage3.math.MathUtilities.subtract(min, center);
				javax.vecmath.Vector3d dmax = edu.cmu.cs.stage3.math.MathUtilities.subtract(max, center);
				dmin.scale(m_scale);
				dmax.scale(m_scale);
				min.add(dmin);
				max.add(dmax);
			}
			vertices[0] = new edu.cmu.cs.stage3.alice.scenegraph.Vertex3d(new javax.vecmath.Point3d(min.x, center.y, center.z), null, edu.cmu.cs.stage3.alice.scenegraph.Color.RED, null, null);
			vertices[1] = new edu.cmu.cs.stage3.alice.scenegraph.Vertex3d(new javax.vecmath.Point3d(max.x, center.y, center.z), null, edu.cmu.cs.stage3.alice.scenegraph.Color.RED, null, null);
			vertices[2] = new edu.cmu.cs.stage3.alice.scenegraph.Vertex3d(new javax.vecmath.Point3d(center.x, min.y, center.z), null, edu.cmu.cs.stage3.alice.scenegraph.Color.GREEN, null, null);
			vertices[3] = new edu.cmu.cs.stage3.alice.scenegraph.Vertex3d(new javax.vecmath.Point3d(center.x, max.y, center.z), null, edu.cmu.cs.stage3.alice.scenegraph.Color.GREEN, null, null);
			vertices[4] = new edu.cmu.cs.stage3.alice.scenegraph.Vertex3d(new javax.vecmath.Point3d(center.x, center.y, min.z), null, edu.cmu.cs.stage3.alice.scenegraph.Color.BLUE, null, null);
			vertices[5] = new edu.cmu.cs.stage3.alice.scenegraph.Vertex3d(new javax.vecmath.Point3d(center.x, center.y, max.z), null, edu.cmu.cs.stage3.alice.scenegraph.Color.BLUE, null, null);
			m_sgLineArray.setVertices(vertices);
		}
		setIsDirty(false);
	}
}
