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

package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.ObjectProperty;
import edu.cmu.cs.stage3.alice.core.property.TransformableProperty;

//todo handle arguments
public class TextureMapMovie extends edu.cmu.cs.stage3.alice.core.Response {
	public final TransformableProperty subject = new TransformableProperty(this, "subject", null);
	public final ElementArrayProperty textureMaps = new ElementArrayProperty(this, "textureMaps", null, edu.cmu.cs.stage3.alice.core.TextureMap[].class);
	public final NumberProperty framesPerSecond = new NumberProperty(this, "framesPerSecond", new Integer(24));
	public final BooleanProperty setDiffuseColorMap = new BooleanProperty(this, "setDiffuseColorMap", Boolean.TRUE);
	public final BooleanProperty setOpacityMap = new BooleanProperty(this, "setOpacityMap", Boolean.FALSE);
	public final ObjectProperty howMuch = new ObjectProperty(this, "howMuch", edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_PARTS, edu.cmu.cs.stage3.util.HowMuch.class);
	public class TextureMapMovieResponse extends RuntimeResponse {
		private edu.cmu.cs.stage3.alice.core.Transformable m_transformable;
		private edu.cmu.cs.stage3.alice.core.TextureMap[] m_textureMaps;
		private double m_framesPerSecond;
		private boolean m_setDiffuseColorMap;
		private boolean m_setOpacityMap;
		private edu.cmu.cs.stage3.util.HowMuch m_howMuch;

		@Override
		public void prologue(double t) {
			super.prologue(t);
			m_transformable = subject.getTransformableValue();
			m_textureMaps = (edu.cmu.cs.stage3.alice.core.TextureMap[]) textureMaps.getValue();
			m_framesPerSecond = framesPerSecond.doubleValue();
			m_setDiffuseColorMap = setDiffuseColorMap.booleanValue();
			m_setOpacityMap = setOpacityMap.booleanValue();
			m_howMuch = (edu.cmu.cs.stage3.util.HowMuch) howMuch.getValue();
		}

		@Override
		public void update(double t) {
			super.update(t);
			if (m_textureMaps.length > 0) {
				int index = (int) (getTimeElapsed(t) * m_framesPerSecond);
				index %= m_textureMaps.length;
				edu.cmu.cs.stage3.alice.core.TextureMap textureMap = m_textureMaps[index];
				if (m_setDiffuseColorMap) {
					m_transformable.setPropertyNamed("diffuseColorMap", textureMap, m_howMuch);
				}
				if (m_setOpacityMap) {
					m_transformable.setPropertyNamed("opacityMap", textureMap, m_howMuch);
				}
			}
		}
	}
}