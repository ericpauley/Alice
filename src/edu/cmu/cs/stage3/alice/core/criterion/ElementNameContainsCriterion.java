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

package edu.cmu.cs.stage3.alice.core.criterion;

public class ElementNameContainsCriterion implements edu.cmu.cs.stage3.util.Criterion {
	private String m_contains;
	private boolean m_ignoreCase;
	public ElementNameContainsCriterion( String contains ) {
		this( contains, true );
	}
	public ElementNameContainsCriterion( String contains, boolean ignoreCase ) {
		m_contains = contains;
		m_ignoreCase = ignoreCase;
	}
	public String getContains() {
		return m_contains;
	}
	public boolean getIgnoreCase() {
		return m_ignoreCase;
	}
	public boolean accept( Object o ) {
		if( o instanceof edu.cmu.cs.stage3.alice.core.Element ) {
			String name = ((edu.cmu.cs.stage3.alice.core.Element)o).name.getStringValue();
			if ( m_contains==null )  {
				return name==null;
			} else if (name == null) {
				return false;
			} else {
				if( m_ignoreCase ) {
					return name.toLowerCase().indexOf(m_contains.toLowerCase())!=-1;
				} else {
					return name.indexOf(m_contains)!=-1;
				}
			}
		} else {
			return false;
		}
	}
	
	public String toString() {
		return getClass().getName()+"["+m_contains+"]";
	}

	protected static ElementNameContainsCriterion valueOf( String s, Class cls ) {
		String beginMarker = cls.getName()+"[";
		String endMarker = "]";
		int begin = s.indexOf( beginMarker ) + beginMarker.length();
		int end = s.lastIndexOf( endMarker );
		try {
			Class[] types = { String.class };
			Object[] values = { s.substring( begin, end ) };
			java.lang.reflect.Constructor constructor = cls.getConstructor( types );
			return (ElementNameContainsCriterion)constructor.newInstance( values );
		} catch( Throwable t ) {
			throw( new RuntimeException() );
		}
	}

	public static ElementNameContainsCriterion valueOf( String s ) {
		return valueOf( s, edu.cmu.cs.stage3.alice.core.criterion.ElementNameContainsCriterion.class );
	}
}