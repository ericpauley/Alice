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

public class PySandbox extends PyElement {
	public PySandbox( edu.cmu.cs.stage3.alice.core.Sandbox sandbox, Namespace namespace ) {
		super( sandbox, namespace );
	}
	private edu.cmu.cs.stage3.alice.core.Sandbox getSandbox() {
		return (edu.cmu.cs.stage3.alice.core.Sandbox)getElement();
	}

	
	public void __setattr__( String name, org.python.core.PyObject attr ) {
		//todo: this should be made thread safe
		for( int i=0; i<getSandbox().variables.size(); i++ ) {
			edu.cmu.cs.stage3.alice.core.Variable variable = (edu.cmu.cs.stage3.alice.core.Variable)getSandbox().variables.get( i );
			if( name.equalsIgnoreCase( variable.name.getStringValue() ) ) {
				//todo: handle boolean
				variable.value.set( attr.__tojava__( variable.getValueClass() ) );
				return;
			}
		}
		super.__setattr__( name, attr );
	}
}
