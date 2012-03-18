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
public class ResizeMode extends RenderTargetManipulatorMode {
	protected edu.cmu.cs.stage3.alice.core.Transformable pickedTransformable;
	protected edu.cmu.cs.stage3.alice.authoringtool.MainUndoRedoStack undoRedoStack;
	protected edu.cmu.cs.stage3.alice.core.Scheduler scheduler;
	protected javax.vecmath.Vector3d oldSize;

	public ResizeMode( edu.cmu.cs.stage3.alice.authoringtool.MainUndoRedoStack undoRedoStack, edu.cmu.cs.stage3.alice.core.Scheduler scheduler  ) {
		this.undoRedoStack = undoRedoStack;
		this.scheduler = scheduler;
	}

	
	public boolean requiresPickedObject() {
		return true;
	}

	
	public boolean hideCursorOnDrag() {
		return true;
	}

	
	public void mousePressed( java.awt.event.MouseEvent ev, edu.cmu.cs.stage3.alice.core.Transformable pickedTransformable, edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo pickInfo ) {
		this.pickedTransformable = pickedTransformable;
		if( pickedTransformable != null ) {
			oldSize = pickedTransformable.getSize();
		}
	}

	
	public void mouseReleased( java.awt.event.MouseEvent ev ) {
		if( (pickedTransformable != null) && (undoRedoStack != null)  ) {
			if( ! ev.isPopupTrigger() ) { // TODO: this is a hack.  this method should never be called if the popup is triggered
				undoRedoStack.push( new SizeUndoableRedoable( pickedTransformable, oldSize, pickedTransformable.getSize(), scheduler ) );
			}

			if( pickedTransformable.poses.size() > 0 ) {
				edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog( "Warning: resizing objects with poses may make those poses unusable.", "Pose warning", javax.swing.JOptionPane.WARNING_MESSAGE );
			}
		}
	}

	
	public void mouseDragged( java.awt.event.MouseEvent ev, int dx, int dy ) {
		javax.vecmath.Vector3d currentSize = pickedTransformable.getSize();
//		if ( (currentSize.x > 0 && currentSize.y > 0 ) || dy < 0 ) { //Aik Min
			if( (pickedTransformable != null) && (dy != 0) ) {
				double divisor = ev.isShiftDown() ? 1000.0 : 100.0;
				double scaleFactor = 1.0 - (dy)/divisor;
				pickedTransformable.resizeRightNow( scaleFactor );
			}
//		} 
	}
}