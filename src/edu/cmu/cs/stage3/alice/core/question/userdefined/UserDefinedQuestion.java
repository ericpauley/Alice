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

package edu.cmu.cs.stage3.alice.core.question.userdefined;

import edu.cmu.cs.stage3.alice.core.Expression;
import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.ClassProperty;

public class UserDefinedQuestion extends edu.cmu.cs.stage3.alice.core.Question {
	public final ClassProperty valueClass = new ClassProperty( this, "valueClass", null );
	public final ElementArrayProperty components = new ElementArrayProperty( this, "components", null, Component[].class );
	public final ElementArrayProperty requiredFormalParameters = new ElementArrayProperty( this, "requiredFormalParameters", null, Variable[].class );
	public final ElementArrayProperty keywordFormalParameters = new ElementArrayProperty( this, "keywordFormalParameters", null, Variable[].class );
	public final ElementArrayProperty localVariables = new ElementArrayProperty( this, "localVariables", null, Variable[].class );

	
	protected void internalFindAccessibleExpressions( Class cls, java.util.Vector v ) {
		for( int i=0; i<requiredFormalParameters.size(); i++ ) {
			internalAddExpressionIfAssignableTo( (Expression)requiredFormalParameters.get( i ), cls, v );
		}
		for( int i=0; i<keywordFormalParameters.size(); i++ ) {
			internalAddExpressionIfAssignableTo( (Expression)keywordFormalParameters.get( i ), cls, v );
		}
		for( int i=0; i<localVariables.size(); i++ ) {
			internalAddExpressionIfAssignableTo( (Expression)localVariables.get( i ), cls, v );
		}
		super.internalFindAccessibleExpressions( cls, v );
	}

	
	public Object getValue() {
        for( int i=0; i<components.size(); i++ ) {
            Component component = (Component)components.get( i );
            Object[] value = component.execute();
            if( value != null ) {
                return value[ 0 ];
            }
        }
        return null;
	}
	
	public Class getValueClass() {
		return valueClass.getClassValue();
	}
}
