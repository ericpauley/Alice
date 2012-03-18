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

public class DoTogether extends CompositeResponse {
	private static Class[] s_supportedCoercionClasses = { DoInOrder.class };
	
	public Class[] getSupportedCoercionClasses() {
		return s_supportedCoercionClasses;
	}
	public class RuntimeDoTogether extends RuntimeCompositeResponse {
		private double m_timeRemaining;
        private edu.cmu.cs.stage3.alice.core.Behavior m_currentBehavior = null;
		
		public void prologue( double t ) {
			super.prologue( t );
			RuntimeResponse[] runtimeResponses = getRuntimeResponses();
            if( runtimeResponses.length > 0 ) {
                edu.cmu.cs.stage3.alice.core.World world = DoTogether.this.getWorld();
                if( world != null ) {
                    edu.cmu.cs.stage3.alice.core.Sandbox sandbox = world.getCurrentSandbox();
                    if( sandbox != null ) {
                        m_currentBehavior = sandbox.getCurrentBehavior();
                        m_currentBehavior.openFork( this, runtimeResponses.length );
                    }
                }
            } else {
                m_currentBehavior = null;
            }
            for( int i=0; i<runtimeResponses.length; i++ ) {
                if( m_currentBehavior != null ) {
                    m_currentBehavior.setForkIndex( this, i );
                }
                runtimeResponses[ i ].prologue( t );
            }
            if( m_currentBehavior != null ) {
                m_currentBehavior.setForkIndex( this, -1 );
            }
		}
		
		public void update( double t ) {
			super.update( t );
			RuntimeResponse[] runtimeResponses = getRuntimeResponses();
			m_timeRemaining = -getTimeElapsed( t );
            for( int i=0; i<runtimeResponses.length; i++ ) {
                if( m_currentBehavior != null ) {
                    m_currentBehavior.setForkIndex( this, i );
                }
                if( runtimeResponses[ i ].isActive() ) {
                    runtimeResponses[ i ].update( t );
                    double timeRemaining = runtimeResponses[ i ].getTimeRemaining( t );
                    if( timeRemaining<=0 ) {
                        runtimeResponses[ i ].epilogue( t );
                    }
                    m_timeRemaining = Math.max( timeRemaining, m_timeRemaining );
                }
            }
            if( m_currentBehavior != null ) {
                m_currentBehavior.setForkIndex( this, -1 );
            }
		}
		
		public void epilogue( double t ) {
			super.epilogue( t );
            if( m_currentBehavior != null ) {
                //todo?
                m_currentBehavior.setForkIndex( this, 0 );
                m_currentBehavior.closeFork( this );
                m_currentBehavior = null;
            }
        }

		
		public double getTimeRemaining( double t ) {
			return m_timeRemaining;
		}
	}
}
