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

package edu.cmu.cs.stage3.alice.authoringtool.util;

/**
 * @author Jason Pratt
 */
public class InAppropriateObjectArrayPropertyCriterion implements edu.cmu.cs.stage3.util.Criterion {
	public boolean accept( Object object ) {
		if( object instanceof edu.cmu.cs.stage3.alice.core.Element ) {
			edu.cmu.cs.stage3.alice.core.Element element = (edu.cmu.cs.stage3.alice.core.Element)object;
			edu.cmu.cs.stage3.alice.core.Element parent = element.getParent();

			if( parent != null ) {
				edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty oap = null;
				if( element instanceof edu.cmu.cs.stage3.alice.core.Transformable ) {
					if( parent instanceof edu.cmu.cs.stage3.alice.core.World ) {
						oap = ((edu.cmu.cs.stage3.alice.core.World)parent).sandboxes;
					} else if( parent instanceof edu.cmu.cs.stage3.alice.core.Transformable ) {
						oap = ((edu.cmu.cs.stage3.alice.core.Transformable)parent).parts;
					} else if( parent instanceof edu.cmu.cs.stage3.alice.core.Group ) {
						oap = ((edu.cmu.cs.stage3.alice.core.Group)parent).values;
					}
				} else if( element instanceof edu.cmu.cs.stage3.alice.core.Response ) {
					if( parent instanceof edu.cmu.cs.stage3.alice.core.Sandbox ) {
						oap = ((edu.cmu.cs.stage3.alice.core.Sandbox)parent).responses;
					}
				} else if( element instanceof edu.cmu.cs.stage3.alice.core.Behavior ) {
					if( parent instanceof edu.cmu.cs.stage3.alice.core.Sandbox ) {
						oap = ((edu.cmu.cs.stage3.alice.core.Sandbox)parent).behaviors;
					}
				} else if( element instanceof edu.cmu.cs.stage3.alice.core.Variable ) {
					if( parent instanceof edu.cmu.cs.stage3.alice.core.Sandbox ) {
						oap = ((edu.cmu.cs.stage3.alice.core.Sandbox)parent).variables;
					}
				} else if( element instanceof edu.cmu.cs.stage3.alice.core.Question ) {
					if( parent instanceof edu.cmu.cs.stage3.alice.core.Sandbox ) {
						oap = ((edu.cmu.cs.stage3.alice.core.Sandbox)parent).questions;
					}
				} else if( element instanceof edu.cmu.cs.stage3.alice.core.Sound ) {
					if( parent instanceof edu.cmu.cs.stage3.alice.core.Sandbox ) {
						oap = ((edu.cmu.cs.stage3.alice.core.Sandbox)parent).sounds;
					}
				} else if( element instanceof edu.cmu.cs.stage3.alice.core.TextureMap ) {
					if( parent instanceof edu.cmu.cs.stage3.alice.core.Sandbox ) {
						oap = ((edu.cmu.cs.stage3.alice.core.Sandbox)parent).textureMaps;
					}
				} else if( element instanceof edu.cmu.cs.stage3.alice.core.Pose ) {
					if( parent instanceof edu.cmu.cs.stage3.alice.core.Transformable ) {
						oap = ((edu.cmu.cs.stage3.alice.core.Transformable)parent).poses;
					}
				} else {
					if( parent instanceof edu.cmu.cs.stage3.alice.core.Sandbox ) {
						oap = ((edu.cmu.cs.stage3.alice.core.Sandbox)parent).misc;
					}
				}
				if( oap != null ) {
					return oap.contains( element );
				}
			}
		}

		return true;
	}
}