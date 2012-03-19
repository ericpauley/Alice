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

package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.alice.core.property.ImageProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;

public class TextureMap extends Element {
	public final ImageProperty image = new ImageProperty(this, "image", null);
	public final NumberProperty format = new NumberProperty(this, "format", new Integer(0));
	private edu.cmu.cs.stage3.alice.scenegraph.TextureMap m_sgTextureMap;
	public TextureMap() {
		super();
		m_sgTextureMap = new edu.cmu.cs.stage3.alice.scenegraph.TextureMap();
		m_sgTextureMap.setBonus(this);
	}

	@Override
	protected void internalRelease(int pass) {
		switch (pass) {
			case 2 :
				m_sgTextureMap.release();
				m_sgTextureMap = null;
				break;
		}
		super.internalRelease(pass);
	}
	public edu.cmu.cs.stage3.alice.scenegraph.TextureMap getSceneGraphTextureMap() {
		return m_sgTextureMap;
	}

	@Override
	protected void nameValueChanged(String value) {
		super.nameValueChanged(value);
		String s = null;
		if (value != null) {
			s = value + ".m_sgTextureMap";
		}
		m_sgTextureMap.setName(s);
	}
	private void imageValueChanged(java.awt.Image value) {
		m_sgTextureMap.setImage(value);
	}
	private void formatValueChanged(Number value) {
		if (value != null) {
			m_sgTextureMap.setFormat(value.intValue());
		} else {
			m_sgTextureMap.setFormat(0);
		}
	}

	@Override
	protected void propertyChanged(Property property, Object value) {
		if (property == image) {
			imageValueChanged((java.awt.Image) value);
		} else if (property == format) {
			formatValueChanged((Number) value);
		} else {
			super.propertyChanged(property, value);
		}
	}

	public void touchImage() {
		m_sgTextureMap.touchImage();
	}
}
