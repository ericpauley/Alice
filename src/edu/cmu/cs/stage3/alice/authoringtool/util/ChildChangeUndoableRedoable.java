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

public class ChildChangeUndoableRedoable implements ContextAssignableUndoableRedoable {
	protected edu.cmu.cs.stage3.alice.core.Element parent;
	protected edu.cmu.cs.stage3.alice.core.Element child;
	protected int changeType;
	protected int oldIndex;
	protected int newIndex;
	protected Object context;

	public ChildChangeUndoableRedoable( edu.cmu.cs.stage3.alice.core.event.ChildrenEvent childrenEvent ) {
		this.parent = (edu.cmu.cs.stage3.alice.core.Element)childrenEvent.getSource();
		this.child = childrenEvent.getChild();
		this.changeType = childrenEvent.getChangeType();
		this.oldIndex = childrenEvent.getOldIndex();
		this.newIndex = childrenEvent.getNewIndex();
	}

	/**
	 * @deprecated  use other constructor
	 */
	public ChildChangeUndoableRedoable( edu.cmu.cs.stage3.alice.core.event.ChildrenEvent childrenEvent, Object context ) {
		this.parent = (edu.cmu.cs.stage3.alice.core.Element)childrenEvent.getSource();
		this.child = childrenEvent.getChild();
		this.changeType = childrenEvent.getChangeType();
		this.oldIndex = childrenEvent.getOldIndex();
		this.newIndex = childrenEvent.getNewIndex();
	}

	public void setContext( Object context ) {
		this.context = context;
	}

	public void undo() {
		if( changeType == edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_INSERTED ) {
			child.removeFromParent();
		} else if( changeType == edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_REMOVED ) {
			parent.insertChildAt( child, oldIndex );
		} else if( changeType == edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_SHIFTED ) {
			parent.insertChildAt( child, oldIndex );
		}
	}

	public void redo() {
		if( changeType == edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_INSERTED ) {
			parent.insertChildAt( child, newIndex );
		} else if( changeType == edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_REMOVED ) {
			parent.removeChild( child );
		} else if( changeType == edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_SHIFTED ) {
			parent.insertChildAt( child, newIndex );
		}
	}

	public Object getAffectedObject() {
		return child;
	}

	public Object getContext() {
		return context;
	}
}