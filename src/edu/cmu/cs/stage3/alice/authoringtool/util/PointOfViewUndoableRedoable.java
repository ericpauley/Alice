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
public class PointOfViewUndoableRedoable extends OneShotUndoableRedoable {
	public PointOfViewUndoableRedoable( edu.cmu.cs.stage3.alice.core.Transformable transformable, edu.cmu.cs.stage3.math.Matrix44 oldTransformation, edu.cmu.cs.stage3.math.Matrix44 newTransformation, edu.cmu.cs.stage3.alice.core.Scheduler scheduler ) {
		super( new edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation(), new edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation(), new edu.cmu.cs.stage3.alice.authoringtool.util.OneShotSimpleBehavior(), scheduler );

		edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation redoResponse = (edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation)getRedoResponse();
		edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation undoResponse = (edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation)getUndoResponse();
		edu.cmu.cs.stage3.alice.authoringtool.util.OneShotSimpleBehavior oneShotBehavior = getOneShotBehavior();

		redoResponse.subject.set( transformable );
		redoResponse.pointOfView.set( newTransformation );
		undoResponse.subject.set( transformable );
		undoResponse.pointOfView.set( oldTransformation );

		oneShotBehavior.setAffectedProperties( new edu.cmu.cs.stage3.alice.core.Property[] { transformable.localTransformation } );
	}
}