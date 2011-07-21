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

package edu.cmu.cs.stage3.util;

public abstract class Enumerable implements java.io.Serializable {
	private String m_repr = null;
	public static Enumerable[] getItems( Class cls ) {
		java.util.Vector v = new java.util.Vector();
		java.lang.reflect.Field[] fields = cls.getFields();
		for( int i=0; i<fields.length; i++ ) {
			int modifiers = fields[i].getModifiers();
			if( java.lang.reflect.Modifier.isPublic( modifiers ) && java.lang.reflect.Modifier.isFinal( modifiers ) && java.lang.reflect.Modifier.isStatic( modifiers ) ) {
				try {
					v.addElement( fields[i].get( null ) );
				} catch( IllegalAccessException iae ) {
					iae.printStackTrace();
				}
			}
		}
		Enumerable[] array = new Enumerable[ v.size() ];
		v.copyInto( array );
		return array;
	}
	public String getRepr() {
		if( m_repr==null ) {
			java.lang.reflect.Field[] fields = getClass().getFields();
			for( int i=0; i<fields.length; i++ ) {
				int modifiers = fields[i].getModifiers();
				if( java.lang.reflect.Modifier.isPublic( modifiers ) && java.lang.reflect.Modifier.isFinal( modifiers ) && java.lang.reflect.Modifier.isStatic( modifiers ) ) {
					try {
						if( this.equals( fields[i].get( null ) ) ) {
							m_repr = fields[i].getName();
							return m_repr;
						}
					} catch( IllegalAccessException iae ) {
						iae.printStackTrace();
					}
				}
			}
			return "unknown";
		} else {
			return m_repr;
		}
	}
	
	public String toString() {
		return getClass().getName()+"["+getRepr()+"]";
	}
	protected static Enumerable valueOf( String s, Class cls ) {
		String[] markers = { cls.getName()+"[", "]" };
		int begin = s.indexOf( markers[0] ) + markers[0].length();
		int end = s.indexOf( markers[1] );
		String fieldName = s.substring( begin, end );
		java.lang.reflect.Field[] fields = cls.getFields();
		for( int i=0; i<fields.length; i++ ) {
			int modifiers = fields[i].getModifiers();
			if( java.lang.reflect.Modifier.isPublic( modifiers ) && java.lang.reflect.Modifier.isFinal( modifiers ) && java.lang.reflect.Modifier.isStatic( modifiers ) ) {
				if( fieldName.equals( fields[i].getName() ) ) {
					try {
						return (Enumerable)fields[i].get( null );
					} catch( IllegalAccessException iae ) {
						iae.printStackTrace();
					}
				}
			}
		}
		return null;
	}
}
