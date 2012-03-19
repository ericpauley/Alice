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

public abstract class TextureMapProxy extends ElementProxy {
	protected abstract void onImageChange(int[] pixels, int width, int height);
	protected abstract void onFormatChange(int value);
	private void onImageChange(java.awt.Image image) {
		if (image != null) {
			try {
				int width = edu.cmu.cs.stage3.image.ImageUtilities.getWidth(image);
				int height = edu.cmu.cs.stage3.image.ImageUtilities.getHeight(image);
				int[] pixels = edu.cmu.cs.stage3.image.ImageUtilities.getPixels(image, width, height);
				onImageChange(pixels, width, height);
			} catch (InterruptedException ie) {
				throw new RuntimeException("interrupted waiting for size");
			}
		} else {
			onImageChange(null, 0, 0);
		}
	}

	@Override
	protected void changed(edu.cmu.cs.stage3.alice.scenegraph.Property property, Object value) {
		if (property == edu.cmu.cs.stage3.alice.scenegraph.TextureMap.IMAGE_PROPERTY) {
			onImageChange((java.awt.Image) value);
		} else if (property == edu.cmu.cs.stage3.alice.scenegraph.TextureMap.FORMAT_PROPERTY) {
			onFormatChange(((Integer) value).intValue());
		} else {
			super.changed(property, value);
		}
	}

	/* package protected */void setRenderTargetWithLatestImage(RenderTarget renderTarget) {
		((edu.cmu.cs.stage3.alice.scenegraph.TextureMap) getSceneGraphElement()).setRenderTargetWithLatestImage(renderTarget);
	}
}
