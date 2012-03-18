package edu.cmu.cs.stage3.media;

public abstract class AbstractPlayer implements Player {
	private boolean m_isAvailable = true;
	private AbstractDataSource m_dataSource;
	private java.util.Vector m_playerListeners = new java.util.Vector();
	private edu.cmu.cs.stage3.media.event.PlayerListener[] m_playerListenerArray;
	private double m_beginTime = 0;
	private double m_endTime = Double.NaN;
	protected AbstractPlayer( AbstractDataSource dataSource ) {
		m_dataSource = dataSource;
	}
	public DataSource getDataSource() {
		return m_dataSource;
	}
	public boolean isAvailable() {
		return m_isAvailable;
	}
	public void setIsAvailable( boolean isAvailable ) {
		m_isAvailable = isAvailable;
	}
	public double getBeginTime() {
		return m_beginTime;
	}
	public void setBeginTime( double beginTime ) {
		m_beginTime = beginTime;
	}
	public double getEndTime() {
		return m_endTime;
	}
	public void setEndTime( double endTime ) {
		m_endTime = endTime;
	}
	public void startFromBeginning() {
		setCurrentTime( getBeginTime() );
		start();
	}
	public void addPlayerListener( edu.cmu.cs.stage3.media.event.PlayerListener l ) {
		m_playerListeners.addElement( l );
		m_playerListenerArray = null;
	}
	public void removePlayerListener( edu.cmu.cs.stage3.media.event.PlayerListener l ) {
		m_playerListeners.removeElement( l );
		m_playerListenerArray = null;
	}
	public edu.cmu.cs.stage3.media.event.PlayerListener[] getPlayerListeners() {
		if( m_playerListenerArray == null ) {
			m_playerListenerArray = new edu.cmu.cs.stage3.media.event.PlayerListener[ m_playerListeners.size() ];
			m_playerListeners.copyInto( m_playerListenerArray );
		}
		return m_playerListenerArray;
	}
	protected void fireDurationUpdated() {
		m_dataSource.fireDurationUpdatedIfNecessary( getDuration() );
	}
	protected void fireEndReached() {
		edu.cmu.cs.stage3.media.event.PlayerEvent e = new edu.cmu.cs.stage3.media.event.PlayerEvent( this );
		edu.cmu.cs.stage3.media.event.PlayerListener[] playerListeners = getPlayerListeners();
		for( int i=0; i<playerListeners.length; i++ ) {
			playerListeners[ i ].endReached( e );
		}
	}
	protected void fireStateChanged() {
		edu.cmu.cs.stage3.media.event.PlayerEvent e = new edu.cmu.cs.stage3.media.event.PlayerEvent( this );
		edu.cmu.cs.stage3.media.event.PlayerListener[] playerListeners = getPlayerListeners();
		for( int i=0; i<playerListeners.length; i++ ) {
			playerListeners[ i ].stateChanged( e );
		}
	}
}