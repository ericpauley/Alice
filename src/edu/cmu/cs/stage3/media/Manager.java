package edu.cmu.cs.stage3.media;

public class Manager {
	private static boolean s_isJMFAvailable;
	static {
		try {
			Class.forName( "javax.media.Manager" );
			s_isJMFAvailable = true;
		} catch( ClassNotFoundException cnfe ) {
			s_isJMFAvailable = false;
		}
	}
	public static edu.cmu.cs.stage3.media.DataSource createDataSource( byte[] data, String extension ) {
		if( s_isJMFAvailable ) {
			return new edu.cmu.cs.stage3.media.jmfmedia.DataSource( data, extension );
		} else {
			return new edu.cmu.cs.stage3.media.nullmedia.DataSource( data, extension );
		}
	}
	public static edu.cmu.cs.stage3.media.DataSource createDataSource( java.io.InputStream is, String extension ) throws java.io.IOException {
		java.io.BufferedInputStream bis;
		if( is instanceof java.io.BufferedInputStream ) {
			bis = (java.io.BufferedInputStream)is;
		} else {
			bis = new java.io.BufferedInputStream( is );
		}
		int byteCount = bis.available();
		byte[] data = new byte[ byteCount ];
		bis.read( data );
		return createDataSource( data, extension );
	}
	public static edu.cmu.cs.stage3.media.DataSource createDataSource( java.io.File file ) throws java.io.IOException {
		java.io.FileInputStream fis = new java.io.FileInputStream( file );
		return createDataSource( fis, edu.cmu.cs.stage3.io.FileUtilities.getExtension( file ) );
	}
//	public static void main( String[] args ) {
//		try {
//			java.io.File file = new java.io.File( args[ 0 ] );
//			edu.cmu.cs.stage3.media.DataSource dataSource = edu.cmu.cs.stage3.media.Manager.createDataSource( file );
//			dataSource.addDataSourceListener( new edu.cmu.cs.stage3.media.event.DataSourceListener() {
//				public void durationUpdated( edu.cmu.cs.stage3.media.event.DataSourceEvent e )  {
//					System.err.println( "durationUpdated: " + e );
//				}
//			} );
//
//			//dataSource.setDurationHint( 2 );
//			dataSource.waitForRealizedPlayerCount( 1, 0 );
//			edu.cmu.cs.stage3.media.Player player = dataSource.acquirePlayer();
//			player.addPlayerListener( new edu.cmu.cs.stage3.media.event.PlayerListener() {
//				public void stateChanged( edu.cmu.cs.stage3.media.event.PlayerEvent e )  {
//					System.err.println( "stateChanged: " + e );
//				}
//				public void endReached( edu.cmu.cs.stage3.media.event.PlayerEvent e )  {
//					System.err.println( "endReached: " + e );
//					e.getPlayer().setIsAvailable( true );
//				} 
//			} );
//			player.startFromBeginning();
//		} catch( Throwable t ) {
//			t.printStackTrace();
//		}
//	}
}