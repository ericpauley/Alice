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

public class ChoiceResponse extends CompositeResponse {
	/*
	public class RuntimeChoiceResponse extends RuntimeCompositeResponse {
		private int m_index;
		public void prologue( double t ) {
			super.prologue( t );
			m_index = 0;
			for( int i=0; i<m_runtimeResponses.length; i++ ) {
				if( m_runtimeResponses[i] instanceof ConditionalResponse.RuntimeConditionalResponse ) {
					ConditionalResponse.RuntimeConditionalResponse conditionalRuntimeResponse = (ConditionalResponse.RuntimeConditionalResponse)m_runtimeResponses[i];
					if( conditionalRuntimeResponse.testFirstTime( t ) ) {
						m_index = i;
						break;
					}
				}
			}
			if( m_index<m_runtimeResponses.length ) {
				m_runtimeResponses[m_index].prologue( t );
			}
		}
		public void update( double t ) {
			if( m_index<m_runtimeResponses.length ) {
				m_runtimeResponses[m_index].update( t );
			}
		}
		public void epilogue( double t ) {
			super.epilogue( t );
			if( m_index<m_runtimeResponses.length ) {
				m_runtimeResponses[m_index].epilogue( t );
			}
		}
		public double getTimeRemaining( double t ) {
			if( m_index<m_runtimeResponses.length ) {
				return m_runtimeResponses[m_index].getTimeRemaining( t );
			} else {
				return -getTimeElapsed( t );
			}
		}
	}
	*/
}
