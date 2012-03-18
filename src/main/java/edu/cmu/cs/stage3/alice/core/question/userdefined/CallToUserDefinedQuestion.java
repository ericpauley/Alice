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

import edu.cmu.cs.stage3.alice.core.Behavior;
import edu.cmu.cs.stage3.alice.core.Question;
import edu.cmu.cs.stage3.alice.core.Sandbox;
import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.UserDefinedQuestionProperty;

public class CallToUserDefinedQuestion extends Question {
	public final UserDefinedQuestionProperty userDefinedQuestion = new UserDefinedQuestionProperty( this, "userDefinedQuestion", null );
	public final ElementArrayProperty requiredActualParameters = new ElementArrayProperty( this, "requiredActualParameters", null, Variable[].class );
	public final ElementArrayProperty keywordActualParameters = new ElementArrayProperty( this, "keywordActualParameters", null, Variable[].class );
    
	public Object getValue() {
        UserDefinedQuestion userDefinedQuestionValue = userDefinedQuestion.getUserDefinedQuestionValue();
        Behavior currentBehavior = null;
        World world = getWorld();
        if( world != null ) {
            Sandbox sandbox = world.getCurrentSandbox();
            if( sandbox!=null ) {
                currentBehavior = sandbox.getCurrentBehavior();
            } else {
                //System.err.println( "current sandbox is null" );
            }
        } else {
            //System.err.println( "world is null" );
        }
		if( currentBehavior != null ) {
			currentBehavior.pushStack(
				(Variable[])CallToUserDefinedQuestion.this.requiredActualParameters.getArrayValue(),
				(Variable[])CallToUserDefinedQuestion.this.keywordActualParameters.getArrayValue(),
				(Variable[])userDefinedQuestionValue.requiredFormalParameters.getArrayValue(),
				(Variable[])userDefinedQuestionValue.keywordFormalParameters.getArrayValue(),
				(Variable[])userDefinedQuestionValue.localVariables.getArrayValue(),
				true
			);
			Object returnValue = userDefinedQuestionValue.getValue();
			currentBehavior.popStack();
			return returnValue;
		} else {
			return null;
		}
    }
    
	public Class getValueClass() {
        UserDefinedQuestion userDefinedQuestionValue = userDefinedQuestion.getUserDefinedQuestionValue();
        if( userDefinedQuestionValue != null ) {
            return userDefinedQuestionValue.getValueClass();
        } else {
            return null;
        }
    }
}
