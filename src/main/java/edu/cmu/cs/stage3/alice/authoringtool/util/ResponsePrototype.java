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

package edu.cmu.cs.stage3.alice.authoringtool.util;

/**
 * @author Jason Pratt
 */
public class ResponsePrototype extends ElementPrototype {
	public ResponsePrototype( Class responseClass, edu.cmu.cs.stage3.util.StringObjectPair[] knownPropertyValues, String[] desiredProperties ) {
		super( responseClass, knownPropertyValues, desiredProperties );
	}

	public edu.cmu.cs.stage3.alice.core.Response createNewResponse() {
		return (edu.cmu.cs.stage3.alice.core.Response)createNewElement();
	}

	public Class getResponseClass() {
		return super.getElementClass();
	}

	// a rather inelegant solution for creating copies of the correct type.
	// subclasses should override this method and call their own constructor
	
	protected ElementPrototype createInstance( Class elementClass, edu.cmu.cs.stage3.util.StringObjectPair[] knownPropertyValues, String[] desiredProperties ) {
		return new ResponsePrototype( elementClass, knownPropertyValues, desiredProperties );
	}
}

