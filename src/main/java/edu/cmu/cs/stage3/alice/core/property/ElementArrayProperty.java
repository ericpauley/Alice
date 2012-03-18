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

public class ElementArrayProperty extends ObjectArrayProperty {
	public ElementArrayProperty( Element owner, String name, Object[] defaultValue, Class valueClass ) {
		super( owner, name, defaultValue, valueClass );
	}
	public Element[] getElementArrayValue() {
		return (Element[])getArrayValue();
	}
    private void sortByName( final boolean ignoreCase ) {
        Element[] src = getElementArrayValue();
        Element[] dst = (Element[])java.lang.reflect.Array.newInstance( getComponentType(), src.length );
        System.arraycopy( src, 0, dst, 0, dst.length );
        java.util.Arrays.sort( dst, new java.util.Comparator() {
            public int compare( Object o1, Object o2 ) {
                Element e1 = (Element)o1;
                Element e2 = (Element)o2;
                String n1 = e1.name.getStringValue();
                String n2 = e2.name.getStringValue();
                if( n1 != null ) {
                    if( n2 != null ) {
                        if( ignoreCase ) {
                            return n1.compareTo( n2 );
                        } else {
                            return n1.compareToIgnoreCase( n2 );
                        }
                    } else {
                        return -1;
                    }
                } else {
                    if( n2 != null ) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            }
            
			public boolean equals( Object obj ) {
                return super.equals( obj );

            }
        } );
        set( dst );
    }

    public void sortByName() {
        sortByName( false );
    }
    public void sortByNameIgnoreCase() {
        sortByName( true );
    }
}
