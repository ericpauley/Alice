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
public class SizeUndoableRedoable extends OneShotUndoableRedoable {
	public SizeUndoableRedoable( edu.cmu.cs.stage3.alice.core.Transformable transformable, javax.vecmath.Vector3d oldSize, javax.vecmath.Vector3d newSize, edu.cmu.cs.stage3.alice.core.Scheduler scheduler ) {
		super( new edu.cmu.cs.stage3.alice.core.response.SizeAnimation(), new edu.cmu.cs.stage3.alice.core.response.SizeAnimation(), new edu.cmu.cs.stage3.alice.authoringtool.util.OneShotSimpleBehavior(), scheduler );

		edu.cmu.cs.stage3.alice.core.response.SizeAnimation redoResponse = (edu.cmu.cs.stage3.alice.core.response.SizeAnimation)getRedoResponse();
		edu.cmu.cs.stage3.alice.core.response.SizeAnimation undoResponse = (edu.cmu.cs.stage3.alice.core.response.SizeAnimation)getUndoResponse();
		edu.cmu.cs.stage3.alice.authoringtool.util.OneShotSimpleBehavior oneShotBehavior = getOneShotBehavior();

		redoResponse.subject.set( transformable );
		redoResponse.size.set( newSize );
		undoResponse.subject.set( transformable );
		undoResponse.size.set( oldSize );

		java.util.ArrayList affectedProperties = new java.util.ArrayList();
		edu.cmu.cs.stage3.alice.core.Transformable[] transformables = (edu.cmu.cs.stage3.alice.core.Transformable[])transformable.getDescendants( edu.cmu.cs.stage3.alice.core.Transformable.class, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS );
		for( int i = 0; i < transformables.length; i++ ) {
			affectedProperties.add( transformables[i].localTransformation );
			if( transformables[i] instanceof edu.cmu.cs.stage3.alice.core.Model ) {
				affectedProperties.add( ((edu.cmu.cs.stage3.alice.core.Model)transformables[i]).visualScale );
			}
		}

		oneShotBehavior.setAffectedProperties( (edu.cmu.cs.stage3.alice.core.Property[])affectedProperties.toArray( new edu.cmu.cs.stage3.alice.core.Property[0] ) );
	}
}