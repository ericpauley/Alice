package edu.cmu.cs.stage3.media.nullmedia;

public class Player extends edu.cmu.cs.stage3.media.AbstractPlayer {
	private double m_currentTime = 0;
	private int m_state = STATE_REALIZED;
	public Player( DataSource dataSource ) {
		super( dataSource );
	}
	public double waitForTimeRemaining( long timeout ) {
		return m_currentTime - getActualEndTime();
	}
	public int getState() {
		return m_state;
	}
	private void setState( int state ) {
		if( m_state != state ) {
			m_state = state;
			fireStateChanged();			
		}
	}
	
	public double getCurrentTime() {
		return m_currentTime;
	}
	public void setCurrentTime( double currentTime ) {
		m_currentTime = currentTime;
	}

	public double getDuration() {
		return getDataSource().getDuration( edu.cmu.cs.stage3.media.DataSource.USE_HINT_IF_NECESSARY );
	}

	private double getActualEndTime() {
		double endTime = getEndTime();
		if( Double.isNaN( endTime ) ) {
			endTime = getDuration();
		}
		return endTime;
	}
	private void setCurrentTimeToEnd() {
		setCurrentTime( getActualEndTime() );
		setState( STATE_REALIZED );
		fireEndReached();
	}
	
	public void setVolumeLevel( float volumeLevel ) {
	}
	public void setRate( float rate ) {
	}
	public void setPan( float pan ) {
	}
	public void prefetch() {
	}
	public void realize() {
	}
	public void start() {
		setState( STATE_REALIZED );
		setState( STATE_STARTED );
		final double timeRemaining = waitForTimeRemaining( 0 );
		if( Double.isNaN( timeRemaining ) ) {
			setCurrentTimeToEnd();
		} else {
			new Thread() {
				
				public void run() {
					try {
						Thread.sleep( (long)(timeRemaining*1000) );
					} catch( InterruptedException ie ) {
						//pass
					} finally {
						setCurrentTimeToEnd();
					}
				}
			}.start();
		}
	}
	public void stop() {
		setState( STATE_REALIZED );
	}
} 