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
public class SetPropertyImmediatelyFactory implements PopupItemFactory {
	protected edu.cmu.cs.stage3.alice.core.Property property;

	public SetPropertyImmediatelyFactory( edu.cmu.cs.stage3.alice.core.Property property ) {
		this.property = property;
	}

	public Object createItem( final Object value ) {
		return new Runnable() {
			public void run() {
				SetPropertyImmediatelyFactory.this.run( value );
			}
		};
	}

	protected void run( Object value ) {
		if( value instanceof edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype ) {
			value = ((edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype)value).createNewElement();
		}
		// HACK; is this too aggressive?
		if( value instanceof edu.cmu.cs.stage3.alice.core.Element ) {
			edu.cmu.cs.stage3.alice.core.Element element = (edu.cmu.cs.stage3.alice.core.Element)value;
			if( (element.getParent() == null) && (! (element instanceof edu.cmu.cs.stage3.alice.core.World)) ) {
				property.getOwner().addChild( element );
				element.data.put( "associatedProperty", property.getName() );
			}
		}
		// END HACK
		SetPropertyImmediatelyFactory.this.property.set( value );
	}
}
