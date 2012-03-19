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

package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Expression;
import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;

public class UserDefinedResponse extends DoInOrder {
	public final ElementArrayProperty requiredFormalParameters = new ElementArrayProperty(this, "requiredFormalParameters", null, Variable[].class);
	public final ElementArrayProperty keywordFormalParameters = new ElementArrayProperty(this, "keywordFormalParameters", null, Variable[].class);
	public final ElementArrayProperty localVariables = new ElementArrayProperty(this, "localVariables", null, Variable[].class);

	@Override
	protected void internalFindAccessibleExpressions(Class cls, java.util.Vector v) {
		for (int i = 0; i < requiredFormalParameters.size(); i++) {
			internalAddExpressionIfAssignableTo((Expression) requiredFormalParameters.get(i), cls, v);
		}
		for (int i = 0; i < keywordFormalParameters.size(); i++) {
			internalAddExpressionIfAssignableTo((Expression) keywordFormalParameters.get(i), cls, v);
		}
		for (int i = 0; i < localVariables.size(); i++) {
			internalAddExpressionIfAssignableTo((Expression) localVariables.get(i), cls, v);
		}
		super.internalFindAccessibleExpressions(cls, v);
	}
	private static Class[] s_supportedCoercionClasses = {};

	@Override
	public Class[] getSupportedCoercionClasses() {
		return s_supportedCoercionClasses;
	}
	public class RuntimeUserDefinedResponse extends RuntimeDoInOrder {
		/*
		 * public void configure( Variable[] requiredActualParameters,
		 * Variable[] keywordActualParameters ) { configure(); if(
		 * requiredActualParameters != null ) { for( int i=0;
		 * i<requiredFormalParameters.size(); i++ ) { Variable formal =
		 * (Variable)requiredFormalParameters.get( i ); for( int j=0;
		 * j<requiredActualParameters.length; j++ ) { Variable actual =
		 * (Variable)requiredActualParameters[ j ]; if(
		 * formal.name.getStringValue().equals( actual.name.getStringValue() ) )
		 * { add( formal, actual ); break; } else if(
		 * j==requiredActualParameters.length-1 ) { throw new RuntimeException(
		 * "missing required parameter: " + formal.name.getStringValue() ); } }
		 * } } if( keywordActualParameters != null ) { for( int i=0;
		 * i<keywordFormalParameters.size(); i++ ) { } } for( int i=0;
		 * i<localVariables.size(); i++ ) { Variable localVariable =
		 * (Variable)localVariables.get( i ); Variable runtime = new Variable();
		 * runtime.valueClass.set( localVariable.valueClass.get() );
		 * runtime.value.set( localVariable.value.get() ); add( localVariable,
		 * runtime ); } }
		 */
	}
}
