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

public class ElementNamedCriterion implements edu.cmu.cs.stage3.util.Criterion {
	private String m_name;
	private boolean m_ignoreCase;
	public ElementNamedCriterion( String name ) {
		this( name, true );
	}
	public ElementNamedCriterion( String name, boolean ignoreCase ) {
		m_name = name;
		m_ignoreCase = ignoreCase;
	}
	public String getName() {
		return m_name;
	}
	public boolean getIgnoreCase() {
		return m_ignoreCase;
	}
	public boolean accept( Object o ) {
		if( o instanceof edu.cmu.cs.stage3.alice.core.Element ) {
			String nameValue = ((edu.cmu.cs.stage3.alice.core.Element)o).name.getStringValue();
			if( m_name==null ) {
				return nameValue==null;
			} else {
				if( m_ignoreCase ) {
					return m_name.equalsIgnoreCase( nameValue );
				} else {
					return m_name.equals( nameValue );
				}
			}
		} else {
			return false;
		}
	}
}