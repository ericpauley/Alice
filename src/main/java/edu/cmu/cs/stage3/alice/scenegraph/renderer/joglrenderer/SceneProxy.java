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

package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

class SceneProxy extends ReferenceFrameProxy {
	private BackgroundProxy m_backgroundProxy = null;
	public BackgroundProxy getBackgroundProxy() {
		return m_backgroundProxy;
	}

	@Override
	public void setup(RenderContext context) {
		context.beginAffectorSetup();
		super.setup(context);
		context.endAffectorSetup();
	}

	@Override
	protected void changed(edu.cmu.cs.stage3.alice.scenegraph.Property property, Object value) {
		if (property == edu.cmu.cs.stage3.alice.scenegraph.Scene.BACKGROUND_PROPERTY) {
			m_backgroundProxy = (BackgroundProxy) getProxyFor((edu.cmu.cs.stage3.alice.scenegraph.Background) value);
		} else {
			super.changed(property, value);
		}
	}
}
