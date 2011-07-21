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

package edu.cmu.cs.stage3.alice.core.event;

public class PropertyEvent extends java.util.EventObject {
	private Object m_value;
	public PropertyEvent( edu.cmu.cs.stage3.alice.core.Property source, Object value ) {
		super( source );
		m_value = value;
	}
	public edu.cmu.cs.stage3.alice.core.Property getProperty() {
		return (edu.cmu.cs.stage3.alice.core.Property)getSource();
	}
	public Object getValue() {
		return m_value;
	}
	//todo deprecate?
	public boolean isSourceAlsoKnownAs( Class cls, String name ) {
		edu.cmu.cs.stage3.alice.core.Property property = getProperty();
		edu.cmu.cs.stage3.alice.core.Element element = property.getOwner();
		if( cls.isAssignableFrom( element.getClass() ) ) {
			try {
				java.lang.reflect.Field field = cls.getField( name );
				Object o = field.get( element );
				return o==property;
			} catch( NoSuchFieldException nsfe ) {
			} catch( IllegalAccessException nsfe ) {
			}
		}
		return false;
	}
}
