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

public class DefaultUndoRedoStack extends java.util.LinkedList implements UndoRedoStack {
	private int currentIndex = -1;

	@Override
	synchronized public void push(UndoableRedoable ur) {
		// System.out.println( "push" );
		if (currentIndex < size() - 1) { // is the current index the last item
											// added
			removeRange(currentIndex + 1, size());
		}
		addLast(ur);
		currentIndex = size() - 1;
	}

	@Override
	synchronized public UndoableRedoable pop_() {
		if (currentIndex == size() - 1) {
			currentIndex--;
		}
		return (UndoableRedoable) removeLast();
	}

	@Override
	synchronized public UndoableRedoable undo() {
		// DEBUG System.out.println( "undo" );
		if (currentIndex > -1) {
			UndoableRedoable ur = (UndoableRedoable) get(currentIndex);
			// System.out.println( "DefaultUndoRedoStack.undo( " + ur + " )" );
			ur.undo();
			currentIndex--;
			return ur;
		} else {
			return null;
		}
	}

	@Override
	synchronized public UndoableRedoable redo() {
		// System.out.println( "redo" );
		if (currentIndex < size() - 1) {
			currentIndex++;
			UndoableRedoable ur = (UndoableRedoable) get(currentIndex);
			// System.out.println( "DefaultUndoRedoStack.redo( " + ur + " )" );
			ur.redo();
			return ur;
		} else {
			return null;
		}
	}

	@Override
	synchronized public UndoableRedoable removeUndoable(int index) {
		if (index < 0 || index > size() - 1) {
			return null;
		}
		if (index > currentIndex) {
			return (UndoableRedoable) remove(index);
		}

		UndoableRedoable removedItem = null;
		java.util.ListIterator iter = listIterator();
		while (iter.nextIndex() <= currentIndex) {
			iter.next();
		}
		while (iter.previousIndex() >= index) {
			UndoableRedoable ur = (UndoableRedoable) iter.previous();
			ur.undo();
			removedItem = ur;
		}
		iter.remove();
		currentIndex--;
		while (iter.nextIndex() <= currentIndex) {
			UndoableRedoable ur = (UndoableRedoable) iter.next();
			ur.redo();
		}

		return removedItem;
	}

	@Override
	synchronized public int getCurrentUndoableRedoableIndex() {
		return currentIndex;
	}

	synchronized public UndoableRedoable getCurrentUndoableRedoable() {
		return (UndoableRedoable) get(currentIndex);
	}

	@Override
	synchronized public void clear() {
		super.clear();
		currentIndex = -1;
	}
}
