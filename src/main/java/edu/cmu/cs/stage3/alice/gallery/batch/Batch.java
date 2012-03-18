package edu.cmu.cs.stage3.alice.gallery.batch;

public abstract class Batch {
	private edu.cmu.cs.stage3.alice.core.World m_world;
	public Batch() {
		m_world = new edu.cmu.cs.stage3.alice.core.World();
		m_world.name.set( "World" );

		initialize( m_world );
	}
	public edu.cmu.cs.stage3.alice.core.World getWorld() {
		return m_world;
	}
	protected void initialize( edu.cmu.cs.stage3.alice.core.World world ) {
	}
	public void forEachElement( java.io.File root, ElementHandler elementHandler ) {
		java.io.File[] directories = root.listFiles( new java.io.FileFilter() {
			public boolean accept( java.io.File file ) {
				return file.isDirectory();
			}
		} );
		for( int i=0; i<directories.length; i++ ) {
			forEachElement( directories[ i ], elementHandler );
		}

		java.io.File[] files = root.listFiles( new java.io.FilenameFilter() {
			public boolean accept( java.io.File dir, String name ) {
				return name.endsWith( ".a2c" );
			}
		} );
		for( int i=0; i<files.length; i++ ) {
			java.io.File fileI = files[ i ];
			try {
				edu.cmu.cs.stage3.alice.core.Element element = edu.cmu.cs.stage3.alice.core.Element.load( fileI, m_world );
				elementHandler.handleElement( element, fileI );
				element.release();
			} catch( edu.cmu.cs.stage3.alice.core.UnresolvablePropertyReferencesException upre ) {
				System.err.println( fileI );
				edu.cmu.cs.stage3.alice.core.reference.PropertyReference[] propertyReferences = upre.getPropertyReferences();
				for( int j=0; j<propertyReferences.length; j++ ) {
					System.err.println( "\t" + propertyReferences[ j ] );
				}
				upre.printStackTrace();
			} catch( Throwable t ) {
				System.err.println( fileI );
				t.printStackTrace();
			}
		}
	}
}
