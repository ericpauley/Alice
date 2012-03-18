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

import edu.cmu.cs.stage3.alice.core.property.ListProperty;
import edu.cmu.cs.stage3.alice.core.property.VariableProperty;

public class ForEach extends Composite {
	public final VariableProperty each = new VariableProperty( this, "each", null );

    //todo: change to collection property
	public final ListProperty list = new ListProperty( this, "list", null );

	
	protected void internalFindAccessibleExpressions( Class cls, java.util.Vector v ) {
		internalAddExpressionIfAssignableTo( (edu.cmu.cs.stage3.alice.core.Expression)each.get(), cls, v );
		super.internalFindAccessibleExpressions( cls, v );
	}

    
	public Object[] execute() {
		edu.cmu.cs.stage3.alice.core.World world = getWorld();
		if( world != null ) {
			edu.cmu.cs.stage3.alice.core.Sandbox sandbox = world.getCurrentSandbox();
			if( sandbox != null ) {
				edu.cmu.cs.stage3.alice.core.Behavior currentBehavior = sandbox.getCurrentBehavior();
				if( currentBehavior != null ) {
					edu.cmu.cs.stage3.alice.core.List listValue = list.getListValue();
					if( listValue.size()>0 ) {
						edu.cmu.cs.stage3.alice.core.Variable eachVariable = each.getVariableValue();
						edu.cmu.cs.stage3.alice.core.Variable eachRuntimeVariable = new edu.cmu.cs.stage3.alice.core.Variable();
						eachRuntimeVariable.valueClass.set( eachVariable.valueClass.get() );
						for( int i=0; i<listValue.size(); i++ ) {
							eachRuntimeVariable.value.set( listValue.values.get( i ) );
							currentBehavior.pushEach( eachVariable, eachRuntimeVariable );
							Object[] returnValue = super.execute();
							currentBehavior.popStack();
							if( returnValue != null ) {
								return returnValue;
							}
						}
					}
				}
            }
        }
        return null;
    }
}
