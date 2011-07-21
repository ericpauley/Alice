package edu.cmu.cs.stage3.io;

public class FileUtilities {
	private static boolean s_successfullyLoadedLibrary;
	
	public static final int DIRECTORY_DOES_NOT_EXIST = -1;
	public static final int DIRECTORY_IS_NOT_WRITABLE = -2;
	public static final int DIRECTORY_IS_WRITABLE = 1;
	public static final int BAD_DIRECTORY_INPUT = -3;
	
	static {
		try {
			System.loadLibrary( "jni_fileutilities" );
			s_successfullyLoadedLibrary = true;
			//} catch( UnsatisfiedLinkError ule ) {
		} catch( Throwable t ) {
			s_successfullyLoadedLibrary = false;
		}
	}
	public static boolean isFileCopySupported() {
		return s_successfullyLoadedLibrary;
	}
	
	private static native boolean copy( String srcPath, String dstPath, boolean overwriteIfNecessary, edu.cmu.cs.stage3.progress.ProgressObserver progressObserver ) throws edu.cmu.cs.stage3.progress.ProgressCancelException;
	public static boolean copy( java.io.File src, java.io.File dst, boolean overwriteIfNecessary, edu.cmu.cs.stage3.progress.ProgressObserver progressObserver ) throws edu.cmu.cs.stage3.progress.ProgressCancelException {
		if( isFileCopySupported() ) {
			dst.getParentFile().mkdirs();
			if( progressObserver != null ) {
				progressObserver.progressBegin( edu.cmu.cs.stage3.progress.ProgressObserver.UNKNOWN_TOTAL );
			}
			try {
				return copy( src.getAbsolutePath(), dst.getAbsolutePath(), overwriteIfNecessary, progressObserver );
			} finally {
				if( progressObserver != null ) {
					progressObserver.progressEnd();
				}
			}
		} else {
			throw new RuntimeException( "file copy not supported" );
		}
	}
	public static void copy( java.io.File src, java.io.File dst, boolean overwriteIfNecessary ) {
		try {
			copy( src, dst, overwriteIfNecessary, null );
		} catch( edu.cmu.cs.stage3.progress.ProgressCancelException pce ) {
			throw new Error( "caught ProgressCancelException without ProgressObserver" );
		}
	}

	public static String getExtension( String filename ) {
		String extension = null;
		if( filename != null ) {
			int index = filename.lastIndexOf( '.' );
			if( index != -1 ) {
				extension = filename.substring( index+1 );
			}
		}
		return extension;
	}
	public static String getExtension( java.io.File file ) {
		if( file != null ) {
			return getExtension( file.getName() );
		} else {
			return null;
		}
	}

	public static String getBaseName( String filename ) {
		String basename = null;
		if( filename != null ) {
			int index = filename.lastIndexOf( '.' );
			if( index != -1 ) {
				basename = filename.substring( 0, index );
			} else {
				basename = filename;
			}
		}
		return basename;
	}
	public static String getBaseName( java.io.File file ) {
		if( file != null ) {
			return getBaseName( file.getName() );
		} else {
			return null;
		}
	}
	
	public static int isWritableDirectory( java.io.File directory ){
		if (directory == null || !directory.isDirectory()){
			return BAD_DIRECTORY_INPUT;
		}
		java.io.File testFile = new java.io.File(directory, "test.test");
		boolean writable;
		if ( testFile.exists() ) {
			writable = testFile.canWrite();
		} else {
			try{
				boolean success = testFile.createNewFile();
				writable = success;
			} catch (Throwable t){
				writable = false;
			} finally{
				testFile.delete();
			}
		}		
		if (!writable){
			return DIRECTORY_IS_NOT_WRITABLE;
		}
		if( (!directory.exists()) || (!directory.canRead())){
			return DIRECTORY_DOES_NOT_EXIST;
		}
		return DIRECTORY_IS_WRITABLE;
	}

/*	public static void main( String[] args ) {
		edu.cmu.cs.stage3.progress.ProgressObserver progressObserver = new edu.cmu.cs.stage3.progress.ProgressObserver() {
			public void progressBegin( int total ) {
				System.err.println( "progressBegin: " + total );
			}
			public void progressUpdateTotal( int total ) {
				System.err.println( "progressUpdateTotal: " + total );
			}
			public void progressUpdate( int current, String description ) throws edu.cmu.cs.stage3.progress.ProgressCancelException {
				if( current > 1000000 ) {
					throw new edu.cmu.cs.stage3.progress.ProgressCancelException();
				}
				System.err.println( "progressUpdate: " + current + " " + description );
			}
			public void progressEnd() {
				System.err.println( "progressEnd" );
			}
		};
		try {
			copy( src, dst, true, progressObserver );
		} catch( edu.cmu.cs.stage3.progress.ProgressCancelException pce ) {
			pce.printStackTrace();
		}

		final java.io.File src = new java.io.File( "E:\\estrian\\Desktop\\wizard1.mp2" );
		StringBuffer sb = new StringBuffer( new java.util.Date().toLocaleString() );
		for( int i=0; i<sb.length(); i++ ) {
			if( Character.isLetterOrDigit( sb.charAt( i ) ) ) {
				//pass
			} else {
				sb.setCharAt( i, '_' );
			}
		}
		final java.io.File dst = new java.io.File( "E:\\estrian\\Desktop\\Backup\\" + sb.toString() + "_wizard1.mp2" );

		java.awt.Frame frame = new java.awt.Frame();
		edu.cmu.cs.stage3.swing.DialogManager.initialize( frame );
		edu.cmu.cs.stage3.progress.ProgressPane progressPane = new edu.cmu.cs.stage3.progress.ProgressPane( "Backing up: " + src.getPath(), dst.getPath() ) {
			protected void construct() throws edu.cmu.cs.stage3.progress.ProgressCancelException {
				copy( src, dst, true, this );
			}
		};
		edu.cmu.cs.stage3.swing.DialogManager.showDialog( progressPane );
		frame.dispose();

		StringBuffer sb = new StringBuffer();
		java.util.Calendar calendar = java.util.Calendar.getInstance();
		sb.append( " on " );
		switch( calendar.get( java.util.Calendar.MONTH ) ) {
		case java.util.Calendar.JANUARY:
			sb.append( "Jan " );
			break;
		case java.util.Calendar.FEBRUARY:
			sb.append( "Feb " );
			break;
		case java.util.Calendar.MARCH:
			sb.append( "Mar " );
			break;
		case java.util.Calendar.APRIL:
			sb.append( "Apr " );
			break;
		case java.util.Calendar.MAY:
			sb.append( "May " );
			break;
		case java.util.Calendar.JUNE:
			sb.append( "Jun " );
			break;
		case java.util.Calendar.JULY:
			sb.append( "Jul " );
			break;
		case java.util.Calendar.AUGUST:
			sb.append( "Aug " );
			break;
		case java.util.Calendar.SEPTEMBER:
			sb.append( "Sep " );
			break;
		case java.util.Calendar.OCTOBER:
			sb.append( "Oct " );
			break;
		case java.util.Calendar.NOVEMBER:
			sb.append( "Noc " );
			break;
		case java.util.Calendar.DECEMBER:
			sb.append( "Dec " );
			break;
		}
		sb.append( calendar.get( java.util.Calendar.DAY_OF_MONTH ) );
		sb.append( " " );
		sb.append( calendar.get( java.util.Calendar.YEAR ) );
		sb.append( " at " );
		sb.append( calendar.get( java.util.Calendar.HOUR ) );
		sb.append( " " );
		sb.append( calendar.get( java.util.Calendar.MINUTE ) );
		sb.append( " " );
		sb.append( calendar.get( java.util.Calendar.SECOND ) );
		switch( calendar.get( java.util.Calendar.AM_PM ) ) {
		case java.util.Calendar.AM:
			sb.append( " AM" );
			break;
		case java.util.Calendar.PM:
			sb.append( " PM" );
			break;
		}
		System.out.println( sb.toString() );
	}*/
}