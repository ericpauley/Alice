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

package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.alice.core.event.ScheduleEvent;
import edu.cmu.cs.stage3.alice.core.event.ScheduleListener;

public class Scheduler implements Runnable {
	private static long s_startTime = System.currentTimeMillis();
	private java.util.Vector m_scheduleListeners = new java.util.Vector();
	private ScheduleListener[] m_scheduleListenerArray = null;
	private double m_falseDT = Double.NaN;
	private double m_prevTime = Double.NaN;
	private double m_time = Double.NaN;

	public void addScheduleListener( ScheduleListener scheduleListener ) {
		if( !m_scheduleListeners.contains( scheduleListener ) ) {
			m_scheduleListeners.addElement( scheduleListener );
			m_scheduleListenerArray = null;
		}
	}
	public void removeScheduleListener( ScheduleListener scheduleListener ) {
		m_scheduleListeners.removeElement( scheduleListener );
		m_scheduleListenerArray = null;
	}
	public ScheduleListener[] getScheduleListeners() {
		if( m_scheduleListenerArray==null ) {
			m_scheduleListenerArray = new ScheduleListener[m_scheduleListeners.size()];
			m_scheduleListeners.copyInto( m_scheduleListenerArray );
		}
		return m_scheduleListenerArray;
	}
	public double getFalseDT() {
		return m_falseDT;
	}
	public void setFalseDT( double falseDT ) {
		m_falseDT = falseDT;
	}
	public double getTime() {
		return m_time;
	}
	public double getDT() {
		if( Double.isNaN( m_time ) || Double.isNaN( m_prevTime ) ) {
			return 0;
		} else {
			return m_time-m_prevTime;
		}
	}

	protected void schedule( ScheduleListener scheduleListener, ScheduleEvent scheduleEvent ) {
		scheduleListener.scheduled( scheduleEvent );
	}

	private void updateTime() {
		m_time = (System.currentTimeMillis()-s_startTime)*0.001;
		if( !Double.isNaN( m_falseDT ) ) {
			if( !Double.isNaN( m_prevTime ) ) {
				m_time = m_prevTime+m_falseDT;
			}
		}
	}

	// todo: remove
	// the world needs to update the time before the script is executed
	public void HACK_updateTime() {
		updateTime();
		m_prevTime = m_time;
	}

    private ScheduleEvent m_scheduleEvent = new ScheduleEvent( this, 0 );
	public synchronized void run() {
		updateTime();
        m_scheduleEvent.setTime( m_time );
        ScheduleListener[] sls = getScheduleListeners();
        for( int i=0; i<sls.length; i++ ) {
            try {
    			schedule( sls[ i ], m_scheduleEvent );
            } catch( RuntimeException re ) {
                removeScheduleListener( sls[ i ] );
                throw re;
            }
        }
		m_prevTime = m_time;
	}
}
