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

package edu.cmu.cs.stage3.alice.scripting.jython;

public class PyElement extends org.python.core.PyJavaInstance {
	private edu.cmu.cs.stage3.alice.core.Element m_element;
	private Namespace m_namespace;
	//private boolean m_explicit = true;
	public PyElement( edu.cmu.cs.stage3.alice.core.Element element, Namespace namespace ) {
		super( element );
		m_element = element;
		m_namespace = namespace;
		//__dict__ = new org.python.core.PyStringMap();
	}
	protected edu.cmu.cs.stage3.alice.core.Element getElement() {
		return m_element;
	}
	protected Namespace getNamespace() {
		return m_namespace;
	}
	//protected void noField( String s, org.python.core.PyObject pyO ) {
	//	if( m_explicit ) {
	//		super.noField( s, pyO );
	//	} else {
	//		__dict__.__setitem__( s, pyO );
	//	}
	//}
	
	public org.python.core.PyObject __findattr__( String name ) {
		edu.cmu.cs.stage3.alice.core.Element descendant = m_element.getChildNamedIgnoreCase( name );
		if( descendant == null ) {
			edu.cmu.cs.stage3.alice.core.Property property = m_element.getPropertyNamedIgnoreCase( name );
			if( property == null ) {
				if( name.startsWith( "_" ) ) {
					descendant = m_element.getChildNamed( name.substring( 1 ) );
					if( descendant != null ) {
						return m_namespace.getPyElement( descendant );
					} else {
						property = m_element.getPropertyNamedIgnoreCase( name.substring( 1 ) );
						if( property != null ) {
							return org.python.core.Py.java2py( property );
						}
					}
				}
				return super.__findattr__( name );
			} else {
				return m_namespace.java2py( property.get() );
			}
		} else {
			Object value;
			if( descendant instanceof edu.cmu.cs.stage3.alice.core.Expression ) {
				value = ((edu.cmu.cs.stage3.alice.core.Expression)descendant).getValue();
			} else {
				value = descendant;
			}
			return m_namespace.java2py( value );
		}
	}
	
	public void __setattr__( String name, org.python.core.PyObject attr ) {
		//if( name.equalsIgnoreCase( "__explicit__" ) ) {
		//	m_explicit = attr.__nonzero__();
		//} else {
			edu.cmu.cs.stage3.alice.core.Property property = m_element.getPropertyNamedIgnoreCase( name );
			if( property!=null ) {
				//todo: handle boolean
				property.set( attr.__tojava__( property.getValueClass() ) );
			} else {
				super.__setattr__( name, attr );
			}
		//}
	}

}