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

import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.Scheduler;
import edu.cmu.cs.stage3.alice.core.property.ResponseProperty;

/**
 * @deprecated
 * please use edu.cmu.cs.stage3.alice.core.util.OneShot
*/
public class SimpleBehavior extends edu.cmu.cs.stage3.alice.core.Behavior implements edu.cmu.cs.stage3.alice.core.event.ScheduleListener {
	public final ResponseProperty response = new ResponseProperty( this, "response", null );
	private Response.RuntimeResponse m_runtimeResponse;
	private Scheduler m_scheduler;
	public Response.RuntimeResponse[] getAllRunningResponses() {
		if( m_runtimeResponse != null ) {
			return new Response.RuntimeResponse[] { m_runtimeResponse };
		} else {
			return null;
		}
	}
	
	public void stopAllRuntimeResponses( double time ) {
		if( m_runtimeResponse != null ) {
			m_runtimeResponse.stop( time );
		}
		m_runtimeResponse = null;
	}
	
	protected void internalSchedule( double t, double dt ) {
	}
	public void scheduled( edu.cmu.cs.stage3.alice.core.event.ScheduleEvent scheduleEvent ) {
		double t = scheduleEvent.getTime();
		Response responseValue = response.getResponseValue();
		if( responseValue != null && responseValue.isCommentedOut.booleanValue() ) {
			responseValue = null;
		}
		if( responseValue != null ) {
			if( m_runtimeResponse==null ) {
				m_runtimeResponse = responseValue.manufactureRuntimeResponse();
				m_runtimeResponse.prologue( t );
			}
			m_runtimeResponse.update( t );
			double timeRemaining = m_runtimeResponse.getTimeRemaining( t );
			if( timeRemaining<=0 ) {
				m_runtimeResponse.epilogue( t );
				if( m_scheduler != null ){
					m_scheduler.removeScheduleListener( this );
				}
				m_runtimeResponse = null;
			}
		}
	}
	public void start( Scheduler scheduler ) {
		m_scheduler = scheduler;
		m_scheduler.addScheduleListener( this );
	}
}

/*
import edu.cmu.cs.stage3.alice.core.Behavior;
import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.Expression;
import edu.cmu.cs.stage3.alice.core.Sandbox;
import edu.cmu.cs.stage3.alice.core.property.ResponseProperty;

public class SimpleBehavior extends Behavior {
	private Response m_response = null;
	private Expression[] m_requiredParameters = null;
	private Expression[] m_keywordParameters = null;
	private Response.RuntimeResponse[] m_runtimeResponses = null;
	public SimpleBehavior( Response response, Expression[] requiredParameters, Expression[] keywordParameters ) {
		m_response = response;
		m_requiredParameters = requiredParameters;
		m_keywordParameters = keywordParameters;
	}
	public edu.cmu.cs.stage3.alice.core.Response.RuntimeResponse[] getAllRunningResponses() {
		return m_runtimeResponses;
	}
	protected void scheduled( Sandbox sandbox, double time ) {
		if( m_runtimeResponses == null ) {
			if( m_response != null ) {
				if( !m_response.isCommentedOut.booleanValue() ) {
					m_runtimeResponses = new Response.RuntimeResponse[ 1 ];
					m_runtimeResponses[ 0 ] = responseValue.manufactureRuntimeResponse();
					m_runtimeResponses[ 0 ].configure();
					m_runtimeResponses[ 0 ].prologue( time );
				}
			}
		}
		if( m_runtimeResponse != null ) {
			m_runtimeResponse.update( t );
			double timeRemaining = m_runtimeResponse.getTimeRemaining( t );
			if( timeRemaining<=0 ) {
				m_runtimeResponse.epilogue( t );
				m_runtimeResponses = null;
				m_response = null;
				m_requiredParameters = null;
				m_keywordParameters = null;
			}
		}
	}
}
*/