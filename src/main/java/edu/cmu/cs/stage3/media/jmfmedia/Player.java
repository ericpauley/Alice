package edu.cmu.cs.stage3.media.jmfmedia;

import javax.media.Controller;

public class Player extends edu.cmu.cs.stage3.media.AbstractPlayer {
	private javax.media.Player m_jmfPlayer;
	private javax.media.Time m_pendingMediaTime;
	private Float m_pendingVolumeLevel;
	private Float m_pendingRate;
	public Player(DataSource dataSource) {
		super(dataSource);
		try {
			m_jmfPlayer = javax.media.Manager.createPlayer(dataSource.getJMFDataSource());
			m_jmfPlayer.addControllerListener(new javax.media.ControllerListener() {
				@Override
				public void controllerUpdate(javax.media.ControllerEvent e) {
					if (e instanceof javax.media.TransitionEvent) {
						javax.media.TransitionEvent te = (javax.media.TransitionEvent) e;
						switch (te.getCurrentState()) {
							case javax.media.Controller.Realized :
								if (m_pendingMediaTime != null) {
									m_jmfPlayer.setMediaTime(m_pendingMediaTime);
									m_pendingMediaTime = null;
								}
								if (m_pendingVolumeLevel != null) {
									updateVolumeLevel(m_pendingVolumeLevel.floatValue());
									m_pendingVolumeLevel = null;
								}
								if (m_pendingRate != null) {
									updateRate(m_pendingRate.floatValue());
									m_pendingRate = null;
								}
								break;
						}
						fireStateChanged();
					}
					if (e instanceof javax.media.EndOfMediaEvent) {
						fireEndReached();
					}
					if (e instanceof javax.media.DurationUpdateEvent) {
						fireDurationUpdated();
					}
				}
			});
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	@Override
	public int getState() {
		return m_jmfPlayer.getState();
	}

	@Override
	public double getDuration() {
		javax.media.Time t = m_jmfPlayer.getDuration();
		if (t != null) {
			long nsec = t.getNanoseconds();
			if (nsec == Long.MAX_VALUE - 1) {
				return Double.NaN;
			} else if (nsec == Long.MAX_VALUE - 1) {
				return Double.POSITIVE_INFINITY;
			} else {
				return nsec * 1.0E-9;
			}
		} else {
			return Double.NaN;
		}
	}
	@Override
	public double getCurrentTime() {
		javax.media.Time t;
		if (m_pendingMediaTime != null) {
			t = m_pendingMediaTime;
		} else {
			t = m_jmfPlayer.getMediaTime();
		}
		if (t != null) {
			long nsec = t.getNanoseconds();
			if (nsec == Long.MAX_VALUE - 1) {
				return Double.NaN;
			} else if (nsec == Long.MAX_VALUE - 1) {
				return Double.POSITIVE_INFINITY;
			} else {
				return nsec * 1.0E-9;
			}
		} else {
			return Double.NaN;
		}
	}
	private boolean isAtLeastRealized() {
		int state = getState();
		return state != STATE_UNREALIZED && state != STATE_REALIZING;
	}
	@Override
	public void setCurrentTime(double currentTime) {
		javax.media.Time t = new javax.media.Time(currentTime);
		if (isAtLeastRealized()) {
			m_jmfPlayer.setMediaTime(t);
		} else {
			m_pendingMediaTime = t;
		}
	}

	private void updateVolumeLevel(float volumeLevel) {
		javax.media.GainControl gainConrol = m_jmfPlayer.getGainControl();
		if (gainConrol != null) {
			gainConrol.setLevel(volumeLevel);
		} else {
			// todo: throw exception?
		}
	}
	@Override
	public void setVolumeLevel(float volumeLevel) {
		if (isAtLeastRealized()) {
			updateVolumeLevel(volumeLevel);
		} else {
			m_pendingVolumeLevel = new Float(volumeLevel);
		}
	}

	private void updateRate(float rate) {
		float actualRate = m_jmfPlayer.setRate(rate);
		if (actualRate != rate) {
			// todo: throw exception?
		}
	}

	@Override
	public void setRate(float rate) {
		if (isAtLeastRealized()) {
			updateRate(rate);
		} else {
			m_pendingRate = new Float(rate);
		}
	}

	@Override
	public void realize() {
		try {
			m_jmfPlayer.realize();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	@Override
	public void prefetch() {
		try {
			m_jmfPlayer.prefetch();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	@Override
	public void start() {
		try {
			m_jmfPlayer.start();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	@Override
	public void stop() {
		try {
			if (m_jmfPlayer.getState() == Controller.Started) {
				m_jmfPlayer.stop();
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
