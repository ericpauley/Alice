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

package edu.cmu.cs.stage3.alice.core.clock;

public class DefaultClock implements edu.cmu.cs.stage3.alice.core.Clock {
	private edu.cmu.cs.stage3.alice.core.World m_world;
	private double m_speed = 1.0;
	private double m_time;
	private long m_whenPrev;
	private int m_pauseCount;

	public edu.cmu.cs.stage3.alice.core.World getWorld() {
		return m_world;
	}
	public void setWorld( edu.cmu.cs.stage3.alice.core.World world ) {
		m_world = world;
	}

	public double getSpeed() {
		return m_speed;
	}
	public void setSpeed( double speed ) {
		m_speed = speed;
	}
	
	public void start() {
		m_pauseCount = 0;
		m_whenPrev = System.currentTimeMillis();
		if( m_world != null ) {
			m_world.start();
		}
	}
	public void stop() {
		m_pauseCount = 0;
		m_whenPrev = -1;
		if( m_world != null ) {
			m_world.stop();
		}
	}
	public void pause() {
		m_pauseCount++;
	}
	public void resume() {
		m_pauseCount--;
		if( m_pauseCount == 0 ) {
			m_whenPrev = System.currentTimeMillis();
		}
	}
	private void updateTime() {
		long whenCurr = System.currentTimeMillis();
		long whenDelta = whenCurr - m_whenPrev;
		if( whenDelta > 0 ) {
			double dt = whenDelta * 0.001;
			dt *= m_speed;
			if( m_world != null ) {
				dt *= m_world.speedMultiplier.doubleValue();
			}
			m_time += dt;
		}
		m_whenPrev = whenCurr;
	}

	public double getTime() {
		return m_time;
	}
	public double getTimeElapsed() {
		return getTime();
	}
	public boolean isPaused()
	{
		return m_pauseCount==0? true:  false ;	
	}
	
	public void schedule() {
		if( m_pauseCount == 0 ) {
			if( m_world != null ) {
				updateTime();
				m_world.schedule();
			}
		}
	}
}

