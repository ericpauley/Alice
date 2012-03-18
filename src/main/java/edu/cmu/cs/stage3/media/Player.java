package edu.cmu.cs.stage3.media;

public interface Player {
	public static final int STATE_UNREALIZED = 100;
	public static final int STATE_REALIZING = 200;
	public static final int STATE_REALIZED = 300;
	public static final int STATE_PREFETCHING = 400;
	public static final int STATE_PREFETCHED = 500;
	public static final int STATE_STARTED = 600;

	public DataSource getDataSource();

	public boolean isAvailable();
	public void setIsAvailable( boolean isAvailable );

	public double getBeginTime();
	public void setBeginTime( double beginTime );
	public double getEndTime();
	public void setEndTime( double endTime );

	public int getState();

	//public float getVolumeLevel();
	public void setVolumeLevel( float volumeLevel );
	//public float getRate();
	public void setRate( float rate );

	public void prefetch();
	public void realize();
	public void start();
	public void stop();
	
	public void startFromBeginning();

	public double getCurrentTime();
	public void setCurrentTime( double currentTime );
	public double getDuration();
	//public double waitForDuration( long timeout );

	public void addPlayerListener( edu.cmu.cs.stage3.media.event.PlayerListener l );
	public void removePlayerListener( edu.cmu.cs.stage3.media.event.PlayerListener l );
	public edu.cmu.cs.stage3.media.event.PlayerListener[] getPlayerListeners();
}