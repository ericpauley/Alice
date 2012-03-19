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

import edu.cmu.cs.stage3.alice.scenegraph.VertexGeometry;

/**
 * @author Jason Pratt, Dennis Cosgrove
 */
public class Dummy extends Model {
	private static edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray s_sgITA = null;
	private static edu.cmu.cs.stage3.alice.scenegraph.TextureMap s_sgTexture = null;
	public Dummy() {
		isFirstClass.set(true);
		opacity.set(new Double(.5));
		isShowing.set(false);
		emissiveColor.set(edu.cmu.cs.stage3.alice.scenegraph.Color.LIGHT_GRAY);

		if (s_sgITA == null) {
			s_sgITA = new edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray();
			try {
				java.io.InputStream is = Dummy.class.getResourceAsStream("axesIndices.bin");
				s_sgITA.setIndices(edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray.loadIndices(is));
				is.close();
			} catch (java.io.IOException ioe) {
				throw new ExceptionWrapper(ioe, "failed to load axesIndices.bin resource");
			}
			try {
				java.io.InputStream is = Dummy.class.getResourceAsStream("axesVertices.bin");
				s_sgITA.setVertices(VertexGeometry.loadVertices(is));
				is.close();
			} catch (java.io.IOException ioe) {
				throw new ExceptionWrapper(ioe, "failed to load axesVertices.bin resource");
			}
		}
		if (s_sgTexture == null) {
			s_sgTexture = new edu.cmu.cs.stage3.alice.scenegraph.TextureMap();
			try {
				java.io.InputStream is = Dummy.class.getResourceAsStream("axesImage.png");
				java.awt.Image image = edu.cmu.cs.stage3.image.ImageIO.load("png", is);
				is.close();
				s_sgTexture.setImage(image);
			} catch (java.io.IOException ioe) {
				throw new ExceptionWrapper(ioe, "failed to load axesImage.png resource");
			}
		}
		getSceneGraphVisual().setGeometry(s_sgITA);
		getSceneGraphAppearance().setDiffuseColorMap(s_sgTexture);
	}
	// override to prevent core property values (null) from triggering
	// scenegraph changes

	@Override
	protected void propertyChanged(edu.cmu.cs.stage3.alice.core.Property property, Object value) {
		if (property == diffuseColorMap) {
			// pass
		} else if (property == geometry) {
			// pass
		} else {
			super.propertyChanged(property, value);
		}
	}
}