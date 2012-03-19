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

public class FixedFrameRateClock implements edu.cmu.cs.stage3.alice.core.Clock {
	private edu.cmu.cs.stage3.alice.core.World m_world;
	private int m_frameRate;
	private double m_dt;
	private double m_time;

	public FixedFrameRateClock() {
		this(24);
	}
	public FixedFrameRateClock(int frameRate) {
		setFrameRate(frameRate);
	}
	@Override
	public edu.cmu.cs.stage3.alice.core.World getWorld() {
		return m_world;
	}
	@Override
	public void setWorld(edu.cmu.cs.stage3.alice.core.World world) {
		m_world = world;
	}

	public int getFrameRate() {
		return m_frameRate;
	}
	public void setFrameRate(int frameRate) {
		m_frameRate = frameRate;
		m_dt = 1.0 / m_frameRate;
	}

	@Override
	public void start() {
		m_time = -m_dt;
		if (m_world != null) {
			m_world.start();
		}
	}
	@Override
	public void stop() {
		if (m_world != null) {
			m_world.stop();
		}
	}
	@Override
	public void pause() {
	}
	@Override
	public void resume() {
	}

	@Override
	public double getTime() {
		return m_time;
	}
	@Override
	public double getTimeElapsed() {
		return getTime();
	}
	@Override
	public void schedule() {
		if (m_world != null) {
			m_time += m_dt;
			m_world.schedule();
		}
	}
}
