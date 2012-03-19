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

public class DoInOrder extends CompositeResponse {
	private static Class[] s_supportedCoercionClasses = {DoTogether.class};

	@Override
	public Class[] getSupportedCoercionClasses() {
		return s_supportedCoercionClasses;
	}
	public class RuntimeDoInOrder extends RuntimeCompositeResponse {
		private int m_index = 0;
		private double m_timeRemaining;
		protected boolean preLoopTest(double t) {
			return true;
		}
		protected boolean postLoopTest(double t) {
			return false;
		}

		protected int getChildCount() {
			RuntimeResponse[] runtimeResponses = getRuntimeResponses();
			return runtimeResponses.length;
		}
		protected double getChildTimeRemaining(int index, double t) {
			RuntimeResponse[] runtimeResponses = getRuntimeResponses();
			return runtimeResponses[index].getTimeRemaining(t);
		}
		protected void childPrologueIfNecessary(int index, double t) {
			RuntimeResponse[] runtimeResponses = getRuntimeResponses();
			if (!runtimeResponses[index].isActive()) {
				runtimeResponses[index].prologue(t);
			}
		}
		protected void childUpdate(int index, double t) {
			RuntimeResponse[] runtimeResponses = getRuntimeResponses();
			runtimeResponses[index].update(t);
		}
		protected void childEpilogue(int index, double t) {
			RuntimeResponse[] runtimeResponses = getRuntimeResponses();
			runtimeResponses[index].epilogue(t);
		}

		protected boolean isCullable() {
			return getChildCount() == 0;
		}

		@Override
		public void prologue(double t) {
			super.prologue(t);
			m_index = -1;
		}

		@Override
		public void update(double t) {
			super.update(t);
			double timeRemaining;
			if (isCullable()) {
				m_timeRemaining = 0;
			} else {
				m_timeRemaining = Double.POSITIVE_INFINITY;
				do {
					if (m_index == -1) {
						if (preLoopTest(t)) {
							if (getChildCount() == 0) {
								if (postLoopTest(t)) {
									m_index = -1;
								} else {
									m_timeRemaining = 0;
								}
								break;
							} else {
								m_index = 0;
							}
						} else {
							m_timeRemaining = 0;
							break;
						}
					}

					childPrologueIfNecessary(m_index, t);
					childUpdate(m_index, t);

					timeRemaining = getChildTimeRemaining(m_index, t);

					if (timeRemaining <= 0) {
						childEpilogue(m_index, t);
						m_index++;
					}

					if (m_index == getChildCount()) {
						if (postLoopTest(t)) {
							m_index = -1;
						} else {
							m_timeRemaining = 0;
							break;
						}
					}
				} while (timeRemaining < 0);
			}
		}

		@Override
		public double getTimeRemaining(double t) {
			return m_timeRemaining;
		}
	}
}
