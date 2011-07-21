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

package edu.cmu.cs.stage3.alice.scenegraph.event;

import edu.cmu.cs.stage3.alice.scenegraph.Property;

public class PropertyEvent extends java.util.EventObject {
	protected Property m_property;
	protected Object m_previousValue;
	protected boolean m_isPreviousValueValid;
	public PropertyEvent( edu.cmu.cs.stage3.alice.scenegraph.Element source, Property property ) {
		super( source );
		m_property = property;
		m_isPreviousValueValid = false;
	}
	public PropertyEvent( edu.cmu.cs.stage3.alice.scenegraph.Element source, Property property, Object previousValue ) {
		super( source );
		m_property = property;
		m_previousValue = previousValue;
		m_isPreviousValueValid = true;
	}
	public Property getProperty() {
		return m_property;
	}
	public boolean isPreviousValueValid() {
		return m_isPreviousValueValid;
	}
	public Object getPreviousValue() {
		if( m_isPreviousValueValid ) {
			return m_previousValue;
		} else {
			throw new RuntimeException( "previous value in not valid" );
		}
	}
}
