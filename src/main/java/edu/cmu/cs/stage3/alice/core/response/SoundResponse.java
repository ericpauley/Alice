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

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.core.media.SoundMarker;
import edu.cmu.cs.stage3.alice.core.property.ElementProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.ReferenceFrameProperty;
import edu.cmu.cs.stage3.alice.core.property.SoundProperty;

public class SoundResponse extends edu.cmu.cs.stage3.alice.core.Response {
	public final ReferenceFrameProperty subject = new ReferenceFrameProperty(this, "subject", null);
	public final SoundProperty sound = new SoundProperty(this, "sound", null);
	public final ElementProperty fromMarker = new ElementProperty(this, "fromMarker", null, SoundMarker.class);
	public final ElementProperty toMarker = new ElementProperty(this, "toMarker", null, SoundMarker.class);
	public final NumberProperty volumeLevel = new NumberProperty(this, "volumeLevel", new Double(1));
	public final NumberProperty rate = new NumberProperty(this, "rate", new Double(1));
	public final NumberProperty pan = new NumberProperty(this, "pan", new Double(0));
	private java.util.Vector soundListeners = new java.util.Vector();
	private edu.cmu.cs.stage3.alice.core.event.SoundListener[] soundListenerArray = null;

	@Override
	protected Number getDefaultDuration() {
		return null;
	}
	public void addSoundListener(edu.cmu.cs.stage3.alice.core.event.SoundListener soundListener) {
		if (soundListeners.contains(soundListener)) {
			// edu.cmu.cs.stage3.alice.core.Element.warnln( "WARNING: " + this +
			// " already has propertyListener " + propertyListener +
			// "(class="+propertyListener.getClass()+").  NOT added again." );
		} else {
			soundListeners.addElement(soundListener);
			soundListenerArray = null;
		}
	}
	public void removeSoundListener(edu.cmu.cs.stage3.alice.core.event.SoundListener soundListener) {
		soundListeners.removeElement(soundListener);
		soundListenerArray = null;
	}
	public edu.cmu.cs.stage3.alice.core.event.SoundListener[] getSoundListeners() {
		if (soundListenerArray == null) {
			soundListenerArray = new edu.cmu.cs.stage3.alice.core.event.SoundListener[soundListeners.size()];
			soundListeners.copyInto(soundListenerArray);
		}
		return soundListenerArray;
	}

	private void fireSoundListeners(double time, double duration, edu.cmu.cs.stage3.media.DataSource ds) {
		edu.cmu.cs.stage3.alice.core.event.SoundEvent e = new edu.cmu.cs.stage3.alice.core.event.SoundEvent(this, new Double(time), ds, new Double(duration));
		for (int i = 0; i < soundListeners.size(); i++) {
			edu.cmu.cs.stage3.alice.core.event.SoundListener l = (edu.cmu.cs.stage3.alice.core.event.SoundListener) soundListeners.elementAt(i);
			l.SoundStarted(e);
		}
	}

	public class RuntimeSoundResponse extends RuntimeResponse {
		private edu.cmu.cs.stage3.media.Player m_player = null;
		private edu.cmu.cs.stage3.alice.core.Expression m_volumeLevelExpression = null;
		private edu.cmu.cs.stage3.alice.core.Expression m_rateExpression = null;
		private edu.cmu.cs.stage3.alice.core.Expression m_panExpression = null;
		private edu.cmu.cs.stage3.alice.core.event.ExpressionListener m_volumeLevelExpressionListener = new edu.cmu.cs.stage3.alice.core.event.ExpressionListener() {
			@Override
			public void expressionChanged(edu.cmu.cs.stage3.alice.core.event.ExpressionEvent e) {
				updateVolumeLevel(e.getExpression());
			}
		};
		private edu.cmu.cs.stage3.alice.core.event.ExpressionListener m_rateExpressionListener = new edu.cmu.cs.stage3.alice.core.event.ExpressionListener() {
			@Override
			public void expressionChanged(edu.cmu.cs.stage3.alice.core.event.ExpressionEvent e) {
				updateRate(e.getExpression());
			}
		};
		private double m_prevMediaTime;
		private double m_timeout;

		private void updateVolumeLevel(Number n) {
			if (m_player != null) {
				m_player.setVolumeLevel(n.floatValue());
			}
		}
		private void updateVolumeLevel(edu.cmu.cs.stage3.alice.core.Expression expression) {
			if (expression != null) {
				Object value = expression.getValue();
				if (value instanceof Number) {
					updateVolumeLevel((Number) value);
				} else {
					// todo: throw exception?
				}
			} else {
				// todo: throw exception?
			}
		}

		private void updateRate(Number n) {
			if (m_player != null) {
				m_player.setRate(n.floatValue());
			}
		}
		private void updateRate(edu.cmu.cs.stage3.alice.core.Expression expression) {
			if (expression != null) {
				Object value = expression.getValue();
				if (value instanceof Number) {
					updateRate((Number) value);
				} else {
					// todo: throw exception?
				}
			} else {
				// todo: throw exception?
			}
		}

		@Override
		public void prologue(double t) {
			super.prologue(t);
			edu.cmu.cs.stage3.alice.core.Sound soundValue = sound.getSoundValue();
			if (soundValue != null) {
				edu.cmu.cs.stage3.media.DataSource dataSourceValue = soundValue.dataSource.getDataSourceValue();
				if (dataSourceValue != null) {
					m_player = dataSourceValue.acquirePlayer();
					// SoundResponse.this.fromMarker.getSoundMarker();
					// m_player.setBeginTime( )
					// m_player.setStopTime( )
					fireSoundListeners(t, m_player.getDuration(), dataSourceValue);
					AuthoringTool.pauseSound(m_player);
					// m_player.startFromBeginning();
				}
			}

			Object o;

			m_volumeLevelExpression = null;
			o = volumeLevel.get();
			if (o instanceof edu.cmu.cs.stage3.alice.core.Expression) {
				m_volumeLevelExpression = (edu.cmu.cs.stage3.alice.core.Expression) o;
				m_volumeLevelExpression.addExpressionListener(m_volumeLevelExpressionListener);
				updateVolumeLevel(m_volumeLevelExpression);
			} else {
				updateVolumeLevel((Number) o);
			}

			m_rateExpression = null;
			o = rate.get();
			if (o instanceof edu.cmu.cs.stage3.alice.core.Expression) {
				m_rateExpression = (edu.cmu.cs.stage3.alice.core.Expression) o;
				m_rateExpression.addExpressionListener(m_rateExpressionListener);
				updateRate(m_rateExpression);
			} else {
				updateRate((Number) o);
			}
			m_prevMediaTime = 0;
			m_timeout = Double.NaN;
		}

		@Override
		public void epilogue(double t) {
			if (m_volumeLevelExpression != null) {
				m_volumeLevelExpression.removeExpressionListener(m_volumeLevelExpressionListener);
			}
			if (m_rateExpression != null) {
				m_rateExpression.removeExpressionListener(m_rateExpressionListener);
			}
			edu.cmu.cs.stage3.alice.core.Sound soundValue = sound.getSoundValue();
			if (m_player != null) {
				m_player.stop();
				m_player.setIsAvailable(true);
				m_player = null;
			}
			super.epilogue(t);
		}

		@Override
		public double getTimeRemaining(double t) {
			if (m_player != null) {
				double currMediaTime = m_player.getCurrentTime();
				double duration = SoundResponse.this.duration.doubleValue();
				double endMediaTime;
				if (Double.isNaN(duration)) {
					endMediaTime = m_player.getEndTime();
					if (Double.isNaN(endMediaTime)) {
						endMediaTime = m_player.getDuration();
					}
				} else {
					endMediaTime = duration;
				}
				double mediaTimeRemaining = endMediaTime - currMediaTime;
				double elapsedTime = getTimeElapsed(t);
				if (m_prevMediaTime != currMediaTime) {
					m_timeout = elapsedTime + mediaTimeRemaining;
					m_prevMediaTime = currMediaTime;
				}
				if (Double.isNaN(m_timeout)) {
					// pass
				} else {
					if (elapsedTime > m_timeout) {
						mediaTimeRemaining = 0;
					}
				}
				return mediaTimeRemaining;
			} else {
				return 0;
			}
		}
		// todo: remove?

		@Override
		public double getDuration() {
			double dur = super.getDuration();
			if (dur == 0) {
				return Double.NaN;
			} else {
				return dur;
			}
		}
	}
}
