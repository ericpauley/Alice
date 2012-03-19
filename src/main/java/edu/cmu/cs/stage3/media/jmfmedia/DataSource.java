package edu.cmu.cs.stage3.media.jmfmedia;

class ByteArraySeekablePullSourceStream implements javax.media.protocol.PullSourceStream, javax.media.protocol.Seekable {
	private static final javax.media.protocol.ContentDescriptor RAW_CONTENT_DISCRIPTOR = new javax.media.protocol.ContentDescriptor(javax.media.protocol.ContentDescriptor.RAW);
	private byte[] m_data;
	private long m_location;
	private long m_size;
	public ByteArraySeekablePullSourceStream(byte[] data) {
		m_data = data;
		m_location = 0;
		m_size = data.length;
	}
	@Override
	public int read(byte[] buffer, int offset, int length) throws java.io.IOException {
		long bytesLeft = m_size - m_location;
		if (bytesLeft == 0) {
			return -1;
		}
		int intBytesLeft = (int) bytesLeft;
		int toRead = length;
		if (intBytesLeft < length) {
			toRead = intBytesLeft;
		}
		System.arraycopy(m_data, (int) m_location, buffer, offset, toRead);
		m_location = m_location + toRead;
		return toRead;
	}
	@Override
	public Object getControl(String controlType) {
		return null;
	}
	@Override
	public Object[] getControls() {
		return null;
	}
	@Override
	public javax.media.protocol.ContentDescriptor getContentDescriptor() {
		return RAW_CONTENT_DISCRIPTOR;
	}
	@Override
	public boolean endOfStream() {
		return m_location == m_size;
	}
	@Override
	public long getContentLength() {
		return m_size;
	}
	@Override
	public boolean willReadBlock() {
		return endOfStream();
	}
	@Override
	public boolean isRandomAccess() {
		return true;
	}
	@Override
	public long seek(long where) {
		if (where > m_size) {
			m_location = m_size;
		} else {
			m_location = where;
		}
		return m_location;
	}
	@Override
	public long tell() {
		return m_location;
	}
}

class ByteArrayDataSource extends javax.media.protocol.PullDataSource {
	private static java.util.Dictionary s_extensionToContentTypeMap;
	private byte[] m_data;
	private String m_contentType;
	public ByteArrayDataSource(byte[] data, String contentType) {
		m_data = data;
		m_contentType = contentType;
	}
	public byte[] getData() {
		return m_data;
	}

	@Override
	public String getContentType() {
		return m_contentType;
	}

	@Override
	public javax.media.Time getDuration() {
		return javax.media.Duration.DURATION_UNKNOWN;
	}

	@Override
	public void connect() throws java.io.IOException {
	}

	@Override
	public void start() throws java.io.IOException {
	}

	@Override
	public void disconnect() {
	}

	@Override
	public Object getControl(String parm1) {
		return null;
	}

	@Override
	public javax.media.protocol.PullSourceStream[] getStreams() {
		return new javax.media.protocol.PullSourceStream[]{new ByteArraySeekablePullSourceStream(m_data)};
	}

	@Override
	public void stop() throws java.io.IOException {
	}

	@Override
	public Object[] getControls() {
		return null;
	}
}

public class DataSource extends edu.cmu.cs.stage3.media.AbstractDataSource {
	private static java.util.Dictionary s_extensionToContentTypeMap = new java.util.Hashtable();
	static {
		s_extensionToContentTypeMap = new java.util.Hashtable();
		s_extensionToContentTypeMap.put("mp3", "audio.mpeg");
		s_extensionToContentTypeMap.put("wav", "audio.x_wav");
	}

	private ByteArrayDataSource m_jmfDataSource;
	public DataSource(byte[] data, String extension) {
		super(extension);
		String contentType = (String) s_extensionToContentTypeMap.get(extension.toLowerCase());
		try {
			m_jmfDataSource = new ByteArrayDataSource(data, contentType);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	@Override
	public byte[] getData() {
		return m_jmfDataSource.getData();
	}
	public javax.media.protocol.DataSource getJMFDataSource() {
		return m_jmfDataSource;
	}

	@Override
	protected edu.cmu.cs.stage3.media.Player createPlayer() {
		return new Player(this);
	}

	// public double waitForDuration( long timeout ) {
	// double durationHint = getDurationHint();
	// if( Double.isNaN( durationHint ) ) {
	// long t0 = System.currentTimeMillis();
	// waitForRealizedPlayerCount( 1, timeout );
	// long dt = System.currentTimeMillis() - t0;
	// timeout = Math.max( timeout - dt, 0 );
	// edu.cmu.cs.stage3.media.Player player = getPlayerAt( 0 );
	// return player.waitForDuration( timeout );
	// } else {
	// return durationHint;
	// }
	// }
}