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

import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.property.ScriptProperty;

public class ScriptDefinedResponse extends Response {
	public final ScriptProperty script = new ScriptProperty( this, "script", "" );
	public class RuntimeScriptDefinedResponse extends RuntimeResponse {
		RuntimeResponse m_actual = null;
		
		public double getTimeRemaining( double t ) {
			if( m_actual!=null ) {
				return m_actual.getTimeRemaining( t );
			} else {
				return super.getTimeRemaining( t );
			}
		}
		
		public void prologue( double t ) {
			super.prologue( t );
			m_actual = null;
			Object o = ScriptDefinedResponse.this.eval( ScriptDefinedResponse.this.script.getCode( edu.cmu.cs.stage3.alice.scripting.CompileType.EVAL ) );
			if( o instanceof edu.cmu.cs.stage3.alice.core.Response ) {
				m_actual = ((edu.cmu.cs.stage3.alice.core.Response)o).manufactureRuntimeResponse();
				if( m_actual != null ) {
					m_actual.prologue( t );
				} else {
					//warnln( "no actual response prologue" );
				}
			} else {
				throw new RuntimeException( script.getStringValue() + " does not evaluate to a response." );
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
				m_actual = null;
			} else {
				//warnln( "no actual response epilogue" );
			}
		}
	}
}