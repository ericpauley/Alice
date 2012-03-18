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

package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;

public class ValueProperty extends ObjectProperty {
	private Class m_overrideValueClass = null;
	public ValueProperty( Element owner, String name, Object defaultValue ) {
		super( owner, name, defaultValue, Object.class );
	}
	public void setOverrideValueClass( Class overrideValueClass ) {
		m_overrideValueClass = overrideValueClass;
	}
	
	public Class getValueClass() {
		if( m_overrideValueClass!=null ) {
			return m_overrideValueClass;
		} else {
			return super.getValueClass();
		}
	}
}

