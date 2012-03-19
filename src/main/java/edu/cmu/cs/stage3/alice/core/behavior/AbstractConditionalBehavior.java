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
import edu.cmu.cs.stage3.alice.core.property.ResponseProperty;

public abstract class AbstractConditionalBehavior extends Behavior {
	public final ResponseProperty beginResponse = new ResponseProperty(this, "beginResponse", null);
	public final ResponseProperty duringResponse = new ResponseProperty(this, "duringResponse", null);
	public final ResponseProperty endResponse = new ResponseProperty(this, "endResponse", null);

	private static final int RUNTIME_STATE_CHECKING_FOR_TRUE = 0;
	private static final int RUNTIME_STATE_BEGINNING = 1;
	private static final int RUNTIME_STATE_CHECKING_FOR_FALSE = 2;
	private static final int RUNTIME_STATE_ENDING = 3;

	private int m_runtimeState = 0;
	private boolean m_booleanValue;
	private Response.RuntimeResponse m_runtimeBeginResponse;
	private Response.RuntimeResponse m_runtimeDuringResponse;
	private Response.RuntimeResponse m_runtimeEndResponse;

	protected boolean invokeEndOnStop() {
		return false;
	}

	@Override
	public void stopAllRuntimeResponses(double time) {
		if (m_runtimeBeginResponse != null) {
			if (m_runtimeBeginResponse.isActive()) {
				m_runtimeBeginResponse.stop(time);
			}
		}
		if (m_runtimeDuringResponse != null) {
			if (m_runtimeDuringResponse.isActive()) {
				m_runtimeDuringResponse.stop(time);
			}
		}
		if (m_runtimeEndResponse != null) {
			if (m_runtimeEndResponse.isActive()) {
				m_runtimeEndResponse.stop(time);
			} else {
				if (invokeEndOnStop()) {
					m_runtimeEndResponse.prologue(time);
					m_runtimeEndResponse.update(time);
					m_runtimeEndResponse.epilogue(time);
				}
			}
		}
	}

	protected void set(boolean booleanValue) {
		m_booleanValue = booleanValue;
	}

	@Override
	protected void internalSchedule(double t, double dt) {
		double timeRemaining = 0;
		if (m_runtimeState == RUNTIME_STATE_CHECKING_FOR_TRUE) {
			if (m_booleanValue) {
				if (m_runtimeBeginResponse != null) {
					m_runtimeState = RUNTIME_STATE_BEGINNING;
					m_runtimeBeginResponse.prologue(t);
				} else {
					m_runtimeState = RUNTIME_STATE_CHECKING_FOR_FALSE;
				}
			}
		}
		if (m_runtimeState == RUNTIME_STATE_BEGINNING) {
			m_runtimeBeginResponse.update(t);
			timeRemaining = m_runtimeBeginResponse.getTimeRemaining(t);
			if (timeRemaining <= 0) {
				m_runtimeBeginResponse.epilogue(t);
				m_runtimeState = RUNTIME_STATE_CHECKING_FOR_FALSE;
			}
		}
		if (m_runtimeState == RUNTIME_STATE_CHECKING_FOR_FALSE) {
			if (m_booleanValue) {
				if (m_runtimeDuringResponse != null) {
					if (!m_runtimeDuringResponse.isActive()) {
						m_runtimeDuringResponse.prologue(t + timeRemaining);
					}
					m_runtimeDuringResponse.update(t);
					timeRemaining = m_runtimeDuringResponse.getTimeRemaining(t);
					if (timeRemaining <= 0) {
						m_runtimeDuringResponse.epilogue(t + timeRemaining);
						m_runtimeDuringResponse.prologue(t + timeRemaining);
					}
				}
			} else {
				if (m_runtimeDuringResponse != null) {
					m_runtimeDuringResponse.epilogue(t);
				}
				if (m_runtimeEndResponse != null) {
					m_runtimeState = RUNTIME_STATE_ENDING;
					m_runtimeEndResponse.prologue(t + timeRemaining);
				} else {
					m_runtimeState = RUNTIME_STATE_CHECKING_FOR_TRUE;
				}
			}
		}
		if (m_runtimeState == RUNTIME_STATE_ENDING) {
			m_runtimeEndResponse.update(t);
			timeRemaining = m_runtimeEndResponse.getTimeRemaining(t);
			if (timeRemaining <= 0) {
				m_runtimeEndResponse.epilogue(t);
				m_runtimeState = RUNTIME_STATE_CHECKING_FOR_TRUE;
			}
		}
	}

	@Override
	protected void started(edu.cmu.cs.stage3.alice.core.World world, double time) {
		super.started(world, time);
		// todo: delay this binding
		Response beginResponseValue = beginResponse.getResponseValue();
		Response duringResponseValue = duringResponse.getResponseValue();
		Response endResponseValue = endResponse.getResponseValue();
		if (beginResponseValue != null && beginResponseValue.isCommentedOut.booleanValue()) {
			beginResponseValue = null;
		}
		if (duringResponseValue != null && duringResponseValue.isCommentedOut.booleanValue()) {
			duringResponseValue = null;
		}
		if (endResponseValue != null && endResponseValue.isCommentedOut.booleanValue()) {
			endResponseValue = null;
		}
		if (beginResponseValue != null) {
			m_runtimeBeginResponse = beginResponseValue.manufactureRuntimeResponse();
		} else {
			m_runtimeBeginResponse = null;
		}
		if (duringResponseValue != null) {
			m_runtimeDuringResponse = duringResponseValue.manufactureRuntimeResponse();
		} else {
			m_runtimeDuringResponse = null;
		}
		if (endResponseValue != null) {
			m_runtimeEndResponse = endResponseValue.manufactureRuntimeResponse();
		} else {
			m_runtimeEndResponse = null;
		}
		m_runtimeState = RUNTIME_STATE_CHECKING_FOR_TRUE;
	}
}
