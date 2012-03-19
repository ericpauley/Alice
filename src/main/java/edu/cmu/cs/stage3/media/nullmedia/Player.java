package edu.cmu.cs.stage3.media.nullmedia;

public class Player extends edu.cmu.cs.stage3.media.AbstractPlayer {
	private double m_currentTime = 0;
	private int m_state = STATE_REALIZED;
	public Player(DataSource dataSource) {
		super(dataSource);
	}
	public double waitForTimeRemaining(long timeout) {
		return m_currentTime - getActualEndTime();
	}
	@Override
	public int getState() {
		return m_state;
	}
	private void setState(int state) {
		if (m_state != state) {
			m_state = state;
			fireStateChanged();
		}
	}

	@Override
	public double getCurrentTime() {
		return m_currentTime;
	}
	@Override
	public void setCurrentTime(double currentTime) {
		m_currentTime = currentTime;
	}

	@Override
	public double getDuration() {
		return getDataSource().getDuration(edu.cmu.cs.stage3.media.DataSource.USE_HINT_IF_NECESSARY);
	}

	private double getActualEndTime() {
		double endTime = getEndTime();
		if (Double.isNaN(endTime)) {
			endTime = getDuration();
		}
		return endTime;
	}
	private void setCurrentTimeToEnd() {
		setCurrentTime(getActualEndTime());
		setState(STATE_REALIZED);
		fireEndReached();
	}

	@Override
	public void setVolumeLevel(float volumeLevel) {
	}
	@Override
	public void setRate(float rate) {
	}
	public void setPan(float pan) {
	}
	@Override
	public void prefetch() {
	}
	@Override
	public void realize() {
	}
	@Override
	public void start() {
		setState(STATE_REALIZED);
		setState(STATE_STARTED);
		final double timeRemaining = waitForTimeRemaining(0);
		if (Double.isNaN(timeRemaining)) {
			setCurrentTimeToEnd();
		} else {
			new Thread() {

				@Override
				public void run() {
					try {
						Thread.sleep((long) (timeRemaining * 1000));
					} catch (InterruptedException ie) {
						// pass
					} finally {
						setCurrentTimeToEnd();
					}
				}
			}.start();
		}
	}
	@Override
	public void stop() {
		setState(STATE_REALIZED);
	}
}