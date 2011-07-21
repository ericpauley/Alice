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

public class ObjectArrayPropertyUndoableRedoable implements ContextAssignableUndoableRedoable {
	protected edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty property;
	protected int changeType;
	protected int oldIndex;
	protected int newIndex;
	protected Object value;
	protected Object context;

	public ObjectArrayPropertyUndoableRedoable( edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty property, int changeType, int oldIndex, int newIndex, Object value ) {
		this.property = property;
		this.changeType = changeType;
		this.oldIndex = oldIndex;
		this.newIndex = newIndex;
		this.value = value;
	}

	public void setContext( Object context ) {
		this.context = context;
	}

	public void undo() {
		if( changeType == edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent.ITEM_INSERTED ) {
			property.remove( value );
		} else if( changeType == edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent.ITEM_REMOVED ) {
			property.add( oldIndex, value );
		} else if( changeType == edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent.ITEM_SHIFTED ) {
			property.shift( newIndex, oldIndex );
		}
	}

	public void redo() {
		if( changeType == edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent.ITEM_INSERTED ) {
			property.add( newIndex, value );
		} else if( changeType == edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent.ITEM_REMOVED ) {
			property.remove( value );
		} else if( changeType == edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent.ITEM_SHIFTED ) {
			property.shift( oldIndex, newIndex );
		}
	}

	public Object getAffectedObject() {
		return property.getOwner();
	}

	public Object getContext() {
		return context;
	}

	
	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append( "edu.cmu.cs.stage3.alice.authoringtool.util.ObjectArrayPropertyUndoableRedoable[ " );
		s.append( "property=" + property + "; " );
		s.append( "changeType=" + changeType + "; " );
		s.append( "oldIndex=" + oldIndex + "; " );
		s.append( "newIndex=" + newIndex + "; " );
		s.append( "value=" + value + "; " );
		if( (context != this) && !(context instanceof DefaultUndoRedoStack) ) {  // watch out for infinite loops
			s.append( "context=" + context + "; " );
		} else {
			s.append( "context=" + context.getClass() + "; " );
		}
		s.append( " ]" );
		return s.toString();
	}
}