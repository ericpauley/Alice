package edu.cmu.cs.stage3.media;

public interface DataSource {
	public final boolean USE_HINT_IF_NECESSARY = true;
	public final boolean DO_NOT_USE_HINT = false;
	
	public byte[] getData();
	public String getExtension();

	public double getDuration( boolean useHintIfNecessary );
	
	public double getDurationHint();
	public void setDurationHint( double durationHint );

	public Player acquirePlayer();

	public int waitForRealizedPlayerCount( int playerCount, long timeout );

	public boolean isCompressionWorthwhile();

	public void addDataSourceListener( edu.cmu.cs.stage3.media.event.DataSourceListener l );
	public void removeDataSourceListener( edu.cmu.cs.stage3.media.event.DataSourceListener l );
	public edu.cmu.cs.stage3.media.event.DataSourceListener[] getDataSourceListeners();
}
