package edu.cmu.cs.stage3.media;

import edu.cmu.cs.stage3.media.event.DataSourceListener;

public abstract class AbstractDataSource implements DataSource {
	private String m_extension;
	private double m_duration = Double.NaN;
	private double m_durationHint = Double.NaN;
	private boolean m_isCompressionWorthwhile;
	private java.util.Vector m_dataSourceListeners = new java.util.Vector();
	private edu.cmu.cs.stage3.media.event.DataSourceListener[] m_dataSourceListenerArray;
	private java.util.Vector m_players = new java.util.Vector();

	protected abstract Player createPlayer();

	protected AbstractDataSource(String extension) {
		m_extension = extension;
		m_isCompressionWorthwhile = !m_extension.equalsIgnoreCase("mp3");
	}
	@Override
	public String getExtension() {
		return m_extension;
	}

	@Override
	public void addDataSourceListener(edu.cmu.cs.stage3.media.event.DataSourceListener l) {
		m_dataSourceListeners.addElement(l);
		m_dataSourceListenerArray = null;
	}
	@Override
	public void removeDataSourceListener(edu.cmu.cs.stage3.media.event.DataSourceListener l) {
		m_dataSourceListeners.removeElement(l);
		m_dataSourceListenerArray = null;
	}
	@Override
	public edu.cmu.cs.stage3.media.event.DataSourceListener[] getDataSourceListeners() {
		if (m_dataSourceListenerArray == null) {
			m_dataSourceListenerArray = new edu.cmu.cs.stage3.media.event.DataSourceListener[m_dataSourceListeners.size()];
			m_dataSourceListeners.copyInto(m_dataSourceListenerArray);
		}
		return m_dataSourceListenerArray;
	}

	// protected Player getPlayerAt( int index ) {
	// return (Player)m_players.elementAt( index );
	// }
	private Player addNewPlayer() {
		Player player = createPlayer();
		m_players.addElement(player);
		return player;
	}
	private int getRealizedPlayerCount() {
		int realizedPlayerCount = 0;
		java.util.Enumeration enum0 = m_players.elements();
		while (enum0.hasMoreElements()) {
			Player player = (Player) enum0.nextElement();
			if (player.getState() >= Player.STATE_REALIZED) {
				realizedPlayerCount++;
			}
		}
		return realizedPlayerCount;
	}

	// todo: handle timeout
	@Override
	public int waitForRealizedPlayerCount(int playerCount, long timeout) {
		while (m_players.size() < playerCount) {
			addNewPlayer();
		}
		java.util.Enumeration enum0 = m_players.elements();
		while (enum0.hasMoreElements()) {
			Player player = (Player) enum0.nextElement();
			player.realize();
		}
		return getRealizedPlayerCount();
	}

	@Override
	public Player acquirePlayer() {
		Player availablePlayer = null;
		java.util.Enumeration enum0 = m_players.elements();
		while (enum0.hasMoreElements()) {
			Player player = (Player) enum0.nextElement();
			if (player.isAvailable()) {
				availablePlayer = player;
				break;
			}
		}
		if (availablePlayer == null) {
			availablePlayer = addNewPlayer();
		}
		availablePlayer.setIsAvailable(false);
		return availablePlayer;
	}

	@Override
	public double getDuration(boolean useHintIfNecessary) {
		if (useHintIfNecessary) {
			if (Double.isNaN(m_duration)) {
				return m_durationHint;
			} else {
				return m_duration;
			}
		} else {
			return m_duration;
		}
	}

	@Override
	public double getDurationHint() {
		return m_durationHint;
	}

	@Override
	public void setDurationHint(double durationHint) {
		m_durationHint = durationHint;
	}

	@Override
	public boolean isCompressionWorthwhile() {
		return m_isCompressionWorthwhile;
	}

	protected void fireDurationUpdatedIfNecessary(double duration) {
		if (duration != m_duration) {
			m_duration = duration;
			edu.cmu.cs.stage3.media.event.DataSourceEvent e = new edu.cmu.cs.stage3.media.event.DataSourceEvent(this);
			edu.cmu.cs.stage3.media.event.DataSourceListener[] dataSourceListeners = getDataSourceListeners();
			for (DataSourceListener dataSourceListener : dataSourceListeners) {
				dataSourceListener.durationUpdated(e);
			}
		}
	}
}