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

package edu.cmu.cs.stage3.alice.core.util;

public class TextureMapCounter implements edu.cmu.cs.stage3.util.VisitListener {
	int m_textureMapCount = 0;
	int m_textureMapMemoryCount = 0;
	public void visited( Object o ) {
		if( o instanceof edu.cmu.cs.stage3.alice.core.TextureMap ) {
			edu.cmu.cs.stage3.alice.core.TextureMap textureMap = (edu.cmu.cs.stage3.alice.core.TextureMap)o;
			m_textureMapCount++;
			java.awt.Image image = textureMap.image.getImageValue();
			try {
				int width = edu.cmu.cs.stage3.image.ImageUtilities.getWidth( image );
				int height = edu.cmu.cs.stage3.image.ImageUtilities.getHeight( image );
				int depth = 4; //todo
				if( width!=-1 || height!=-1 ) {
					m_textureMapMemoryCount += width*height*depth;
				}
			} catch( InterruptedException ie ) {
				//pass
			}
		}
	}
	public int getTextureMapCount() {
		return m_textureMapCount;
	}
	public int getTextureMapMemoryCount() {
		return m_textureMapMemoryCount;
	}
}