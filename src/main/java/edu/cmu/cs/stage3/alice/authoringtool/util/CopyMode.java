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

import java.awt.event.InputEvent;

public class CopyMode extends DefaultMoveMode {
	protected Class[] classesToShare = {
	// edu.cmu.cs.stage3.alice.core.Sound.class,
	// edu.cmu.cs.stage3.alice.core.TextureMap.class
	};

	public CopyMode() {
		super();
	}

	public CopyMode(edu.cmu.cs.stage3.alice.authoringtool.MainUndoRedoStack undoRedoStack, edu.cmu.cs.stage3.alice.core.Scheduler scheduler) {
		super(undoRedoStack, scheduler);
	}

	private boolean hasBeenDragged;
	private boolean isButton1(java.awt.event.MouseEvent ev) {
		return (ev.getModifiers() & InputEvent.BUTTON1_MASK) != 0;
	}

	@Override
	public void mousePressed(java.awt.event.MouseEvent ev, edu.cmu.cs.stage3.alice.core.Transformable pickedTransformable, edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo pickInfo) {
		if (isButton1(ev)) {
			undoRedoStack.startCompound();
			if (pickedTransformable != null) {
				String name = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getNameForNewChild(pickedTransformable.name.getStringValue(), pickedTransformable.getParent());
				int index = pickedTransformable.getParent().getIndexOfChild(pickedTransformable) + 1;

				// if( undoRedoStack != null ) {
				// undoRedoStack.setIsListening( true ); // make the copy get
				// automatically picked up by the undoRedoStack
				// }
				pickedTransformable = (edu.cmu.cs.stage3.alice.core.Transformable) pickedTransformable.HACK_createCopy(name, pickedTransformable.getParent(), index, classesToShare, null);
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.addElementToAppropriateProperty(pickedTransformable, pickedTransformable.getParent());
				// undoRedoStack.push( new
				// edu.cmu.cs.stage3.alice.authoringtool.util.PropertyUndoableRedoable(
				// pickedTransformable.vehicle, null,
				// pickedTransformable.vehicle.get() ) );
				// if( undoRedoStack != null ) {
				// undoRedoStack.setIsListening( false );
				// }
			}
			super.mousePressed(ev, pickedTransformable, pickInfo);
			hasBeenDragged = false;
		}
	}

	@Override
	public void mouseReleased(java.awt.event.MouseEvent ev) {
		if (isButton1(ev)) {
			// super.mouseReleased( ev );
			if (hasBeenDragged) {
				// pass
			} else {
				edu.cmu.cs.stage3.alice.core.response.MoveAnimation moveAnimation = new edu.cmu.cs.stage3.alice.core.response.MoveAnimation();
				moveAnimation.subject.set(pickedTransformable);
				moveAnimation.direction.set(edu.cmu.cs.stage3.alice.core.Direction.FORWARD);
				moveAnimation.amount.set(new Double(1));
				moveAnimation.isScaledBySize.set(Boolean.TRUE);

				edu.cmu.cs.stage3.alice.core.response.MoveAnimation undoAnimation = new edu.cmu.cs.stage3.alice.core.response.MoveAnimation();
				undoAnimation.subject.set(pickedTransformable);
				undoAnimation.direction.set(edu.cmu.cs.stage3.alice.core.Direction.FORWARD);
				undoAnimation.amount.set(new Double(-1));
				undoAnimation.isScaledBySize.set(Boolean.TRUE);

				edu.cmu.cs.stage3.alice.core.Property[] affectedProperties = new edu.cmu.cs.stage3.alice.core.Property[]{pickedTransformable.localTransformation};

				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().performOneShot(moveAnimation, undoAnimation, affectedProperties);
			}
			undoRedoStack.stopCompound();
		}
	}

	@Override
	public void mouseDragged(java.awt.event.MouseEvent ev, int dx, int dy) {
		if (isButton1(ev)) {
			super.mouseDragged(ev, dx, dy);
			hasBeenDragged = true;
		}
	}
}