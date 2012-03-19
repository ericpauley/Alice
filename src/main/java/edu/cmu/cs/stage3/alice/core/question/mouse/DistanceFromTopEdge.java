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

package edu.cmu.cs.stage3.alice.core.question.mouse;

import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;

public class DistanceFromTopEdge extends edu.cmu.cs.stage3.alice.core.question.NumberQuestion {
	public final BooleanProperty relativeToRenderTarget = new BooleanProperty(this, "relativeToRenderTarget", Boolean.TRUE);
	private static Class[] s_supportedCoercionClasses = {DistanceFromLeftEdge.class};
	private edu.cmu.cs.stage3.alice.core.RenderTarget[] m_renderTargets = null;

	@Override
	public Class[] getSupportedCoercionClasses() {
		return s_supportedCoercionClasses;
	}

	@Override
	public Object getValue() {
		java.awt.Point p = edu.cmu.cs.stage3.awt.AWTUtilities.getCursorLocation();
		if (p != null) {
			int y = p.y;
			if (relativeToRenderTarget.booleanValue()) {
				if (m_renderTargets != null) {
					if (m_renderTargets.length > 0) {
						y -= m_renderTargets[0].getAWTComponent().getLocationOnScreen().y;
					}
				}
			}
			return new Double(y);
		} else {
			return null;
		}
	}

	@Override
	protected void started(edu.cmu.cs.stage3.alice.core.World world, double time) {
		super.started(world, time);
		m_renderTargets = (edu.cmu.cs.stage3.alice.core.RenderTarget[]) world.getDescendants(edu.cmu.cs.stage3.alice.core.RenderTarget.class);
	}

	@Override
	protected void stopped(edu.cmu.cs.stage3.alice.core.World world, double time) {
		super.stopped(world, time);
		m_renderTargets = null;
	}
}