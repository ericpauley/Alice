package edu.cmu.cs.stage3.media.nullmedia;

public class DataSource extends edu.cmu.cs.stage3.media.AbstractDataSource {
	private byte[] m_data;
	public DataSource( byte[] data, String extension ) {
		super( extension );
		m_data = data;
	}

	public byte[] getData() {
		return m_data;
	}
	
	protected edu.cmu.cs.stage3.media.Player createPlayer() {
		return new Player( this );
	}
	public double waitForDuration( long timeout ) {
		return getDurationHint();
	}
}