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

package edu.cmu.cs.stage3.alice.core.behavior;

import edu.cmu.cs.stage3.alice.core.Behavior;
import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.property.ObjectProperty;
import edu.cmu.cs.stage3.alice.core.property.ResponseProperty;

public class TriggerBehavior extends Behavior {
	public final ResponseProperty triggerResponse = new ResponseProperty( this, "triggerResponse", null );
	public final ObjectProperty multipleRuntimeResponsePolicy = new ObjectProperty( this, "multipleRuntimeResponsePolicy", MultipleRuntimeResponsePolicy.ENQUEUE_MULTIPLE, MultipleRuntimeResponsePolicy.class );
	private java.util.Vector m_runtimeResponses = new java.util.Vector();
	private Response.RuntimeResponse[] m_runtimeResponseArray = null;
    private Response.RuntimeResponse[] getRuntimeResponseArray() {
        if( m_runtimeResponseArray == null ) {
            m_runtimeResponseArray = new Response.RuntimeResponse[ m_runtimeResponses.size() ];
            m_runtimeResponses.copyInto( m_runtimeResponseArray );
        }
        return m_runtimeResponseArray;
    }
	public void trigger( double time ) {
		//debugln( "trigger: " + time );
		if( m_runtimeResponses.size() > 0 ) {
			if( multipleRuntimeResponsePolicy.getValue()==MultipleRuntimeResponsePolicy.IGNORE_MULTIPLE ) {
				return;
			}
		}
		Response response = triggerResponse.getResponseValue();
		if( response != null ) {
			Response.RuntimeResponse runtimeResponse = response.manufactureRuntimeResponse();
			m_runtimeResponses.addElement( runtimeResponse );
            m_runtimeResponseArray = null;
		}
	}
	public void trigger() {
		trigger( System.currentTimeMillis()*0.001 );
	}

	
	protected void internalSchedule( double time, double dt ) {
		MultipleRuntimeResponsePolicy mrrp = (MultipleRuntimeResponsePolicy)multipleRuntimeResponsePolicy.getValue();
        Response.RuntimeResponse[] rra = getRuntimeResponseArray();
		for( int i=0; i<rra.length; i++ ) {
			Response.RuntimeResponse runtimeResponse = rra[ i ];
			if( !runtimeResponse.isActive() ) {
				runtimeResponse.prologue( time );
			}
			runtimeResponse.update( time );
			double timeRemaining = runtimeResponse.getTimeRemaining( time );
			if( timeRemaining<=0 ) {
				runtimeResponse.epilogue( time );
                runtimeResponse.HACK_markForRemoval();
			} else {
				if( mrrp != MultipleRuntimeResponsePolicy.INTERLEAVE_MULTIPLE ) {
					break;
				}
			}
		}
        if( m_runtimeResponses.size()>0 ) {
            synchronized( m_runtimeResponses ) {
                java.util.Enumeration enum0 = m_runtimeResponses.elements();
                while( enum0.hasMoreElements() ) {
                    Response.RuntimeResponse runtimeResponse = (Response.RuntimeResponse)enum0.nextElement();
                    if( runtimeResponse.HACK_isMarkedForRemoval() ) {
                        m_runtimeResponses.removeElement( runtimeResponse );
                        m_runtimeResponseArray = null;
                    }
                }
            }
        }
	}
	
	public void stopAllRuntimeResponses( double time ) {
        Response.RuntimeResponse[] rra = getRuntimeResponseArray();
		for( int i=0; i<rra.length; i++ ) {
			Response.RuntimeResponse runtimeResponse = rra[ i ];
            runtimeResponse.stop( time );
		}
		m_runtimeResponses.removeAllElements();
        m_runtimeResponseArray = null;
	}
	
	protected void started( edu.cmu.cs.stage3.alice.core.World world, double time ) {
		super.started( world, time );
		m_runtimeResponses.removeAllElements();
        m_runtimeResponseArray = null;
	}
}
