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

package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.property.OverridableObjectProperty;
import edu.cmu.cs.stage3.alice.core.property.ClassProperty;

public abstract class BinaryObjectResultingInBooleanQuestion extends BooleanQuestion {
	public final OverridableObjectProperty a = new OverridableObjectProperty( this, "a", null );
	public final OverridableObjectProperty b = new OverridableObjectProperty( this, "b", null );
    public final ClassProperty valueClass = new ClassProperty( this, "valueClass", null );
	protected abstract boolean getValue( Object aValue, Object bValue );
    
	protected void propertyChanged( edu.cmu.cs.stage3.alice.core.Property property, Object value ) {
        if( property == valueClass ) {
            Class overrideValueClass = valueClass.getClassValue();
            a.setOverrideValueClass( overrideValueClass );
            b.setOverrideValueClass( overrideValueClass );
        } else {
            super.propertyChanged( property, value );
        }
    }
	
	public Object getValue() {
		if( getValue( a.getValue(), b.getValue() ) ) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}
}