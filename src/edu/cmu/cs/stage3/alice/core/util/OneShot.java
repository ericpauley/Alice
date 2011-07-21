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

package edu.cmu.cs.stage3.alice.core.util;

import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.Scheduler;

public class OneShot implements edu.cmu.cs.stage3.alice.core.event.ScheduleListener {
	private Response m_response;
	private Response.RuntimeResponse m_runtimeResponse;
	private Scheduler m_scheduler;

	public Response getResponse() {
		return m_response;
	}
	public void setResponse( Response response ) {
		m_response = response;
	}
	public void stopRunningResponse( double time ) {
		if( m_runtimeResponse != null ) {
			m_runtimeResponse.stop( time );
			m_runtimeResponse = null;
		}
	}
	public void scheduled( edu.cmu.cs.stage3.alice.core.event.ScheduleEvent scheduleEvent ) {
		double t = scheduleEvent.getTime();
		boolean done;
		if( m_response == null || m_response.isCommentedOut.booleanValue() ) {
			done = true;
		} else {
			if( m_runtimeResponse==null ) {
				m_runtimeResponse = m_response.manufactureRuntimeResponse();
				m_runtimeResponse.prologue( t );
			}
			m_runtimeResponse.update( t );
			double timeRemaining = m_runtimeResponse.getTimeRemaining( t );
			done = timeRemaining<=0;
			if( done ) {
				m_runtimeResponse.epilogue( t );
				m_runtimeResponse = null;
			}
		}
		if( done ) {
			if( m_scheduler != null ){
				m_scheduler.removeScheduleListener( this );
			}
		}
	}
	public void start( Scheduler scheduler ) {
		m_scheduler = scheduler;
		m_scheduler.addScheduleListener( this );
	}
}
