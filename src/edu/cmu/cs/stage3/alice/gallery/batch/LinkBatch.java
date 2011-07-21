package edu.cmu.cs.stage3.alice.gallery.batch;

public abstract class LinkBatch {
	public void forEachLink( java.io.File dir, LinkHandler linkHandler ) {
		java.io.File[] dirs = dir.listFiles( new java.io.FileFilter() {
			public boolean accept( java.io.File file ) {
				return file.isDirectory();
			}
		} );
		for( int i=0; i<dirs.length; i++ ) {
			forEachLink( dirs[ i ], linkHandler );
		}

		java.io.File[] files = dir.listFiles( new java.io.FilenameFilter() {
			public boolean accept( java.io.File dir, String name ) {
				return name.endsWith( ".link" );
			}
		} );
		for( int i=0; i<files.length; i++ ) {
			java.io.File fileI = files[ i ];
			try {
				java.io.BufferedReader r = new java.io.BufferedReader( new java.io.FileReader( files[ i ] ) );
				String s = r.readLine();
				linkHandler.handleLink( fileI, s );
			} catch( java.io.IOException ioe ) {
				ioe.printStackTrace();
			}
		}
	}
}
