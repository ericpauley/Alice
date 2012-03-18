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

import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.Behavior;
import edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse;
import edu.cmu.cs.stage3.alice.core.property.UserDefinedResponseProperty;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;

public class CallToUserDefinedResponse extends Response {
	public final UserDefinedResponseProperty userDefinedResponse = new UserDefinedResponseProperty( this, "userDefinedResponse", null );
	public final ElementArrayProperty requiredActualParameters = new ElementArrayProperty( this, "requiredActualParameters", null, Variable[].class );
	public final ElementArrayProperty keywordActualParameters = new ElementArrayProperty( this, "keywordActualParameters", null, Variable[].class );
	public class RuntimeCallToUserDefinedResponse extends RuntimeResponse {
		private UserDefinedResponse.RuntimeUserDefinedResponse m_actual = null;
        private Behavior m_currentBehavior = null;
		
		public double getTimeRemaining( double t ) {
			if( m_actual!=null ) {
				return m_actual.getTimeRemaining( t );
			} else {
				return super.getTimeRemaining( t );
			}
		}
		
		public void prologue( double t ) {
			super.prologue( t );
			UserDefinedResponse userDefinedResponseValue = userDefinedResponse.getUserDefinedResponseValue();
			if( userDefinedResponseValue!=null ) {
				m_actual = (UserDefinedResponse.RuntimeUserDefinedResponse)userDefinedResponseValue.manufactureRuntimeResponse();
                m_currentBehavior = CallToUserDefinedResponse.this.getWorld().getCurrentSandbox().getCurrentBehavior();
                Variable[] rap = (Variable[])CallToUserDefinedResponse.this.requiredActualParameters.getArrayValue();
                for( int i=0; i<rap.length; i++ ) {
                    Variable runtime = m_currentBehavior.stackLookup( rap[ i ] );
                    if( runtime != null ) {
                        rap[ i ] = runtime;
                    }
                }
                Variable[] kap = (Variable[])CallToUserDefinedResponse.this.keywordActualParameters.getArrayValue();
                for( int i=0; i<kap.length; i++ ) {
                    Variable runtime = m_currentBehavior.stackLookup( kap[ i ] );
                    if( runtime != null ) {
                        kap[ i ] = runtime;
                    }
                }
                try {
                    m_currentBehavior.pushStack(
                        rap,
                        kap,
                        (Variable[])userDefinedResponseValue.requiredFormalParameters.getArrayValue(),
                        (Variable[])userDefinedResponseValue.keywordFormalParameters.getArrayValue(),
                        (Variable[])userDefinedResponseValue.localVariables.getArrayValue(),
                        true
                    );
                } catch( ArrayIndexOutOfBoundsException aioobe ) {
                    System.err.println( CallToUserDefinedResponse.this );
                    throw aioobe;
                }
                m_actual.prologue( t );
			}
		}
		
		public void update( double t ) {
			super.update( t );
			if( m_actual!=null ) {
				m_actual.update( t );
			} else {
				//warnln( "no actual response update" );
			}
		}
		
		public void epilogue( double t ) {
			super.epilogue( t );
			if( m_actual!=null ) {
				m_actual.epilogue( t );
                m_currentBehavior.popStack();
                m_currentBehavior = null;
				m_actual = null;
			} else {
				//warnln( "no actual response epilogue" );
			}
		}
	}
}