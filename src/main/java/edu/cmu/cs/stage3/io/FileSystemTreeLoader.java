/*
 * Copyright (c) 1999-2003, Carnegie Mellon University. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 
 * 3. Products derived from the software may not be called "Alice",
 *    nor may "Alice" appear in their name, without prior written
 *    permission of Carnegie Mellon University.
 * 
 * 4. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *    "This product includes software developed by Carnegie Mellon University"
 */

package edu.cmu.cs.stage3.io;

/**
 * @author Jason Pratt
 */
public class FileSystemTreeLoader implements DirectoryTreeLoader {
	protected java.io.File root = null;
	protected java.io.File currentDirectory = null;
	protected java.io.InputStream currentlyOpenStream = null;

	/**
	 * pathname can be a String or java.io.File
	 */
	//public void open( Object pathname ) throws IllegalArgumentException, java.io.IOException {
	public void open( Object pathname ) throws IllegalArgumentException, java.io.FileNotFoundException, java.io.IOException {
		if( root != null ) {
			close();
		}

		if( pathname instanceof String ) {
			root = new java.io.File( (String)pathname );
		}
		else if( pathname instanceof java.io.File ) {
			root = (java.io.File)pathname;
		}
		else {
			throw new IllegalArgumentException( "pathname must be an instance of String or java.io.File" );
		}

		if( root.exists() ) {
			if( ! root.canRead() ) {
				throw new java.io.IOException( "cannot read " + root );
			}
		}
		else {
			//throw new IllegalArgumentException( root + " does not exist" );
			throw new java.io.FileNotFoundException( root + " does not exist" );
		}

		currentDirectory = root;
	}

	public void close() throws java.io.IOException {
		closeCurrentFile();
		root = null;
		currentDirectory = null;
	}

	public void setCurrentDirectory( String pathname ) throws IllegalArgumentException {
		java.io.File newCurrentDirectory;
		if( (pathname.length() == 0) || (pathname.charAt( 0 ) == '/') || (pathname.charAt( 0 ) == '\\') ) {
			newCurrentDirectory = new java.io.File( root.getAbsolutePath() + pathname );
		}
		else {
			newCurrentDirectory = new java.io.File( currentDirectory.getAbsolutePath() + "/" + pathname );
		}

		if( ! newCurrentDirectory.exists() ) {
			throw new IllegalArgumentException( newCurrentDirectory + " doesn't exist" );
		}
		if( ! newCurrentDirectory.isDirectory() ) {
			throw new IllegalArgumentException( newCurrentDirectory + " isn't a directory" );
		}

		currentDirectory = newCurrentDirectory;
	}

	public String getCurrentDirectory() {
		return getRelativePathname( currentDirectory );
	}

	//public java.io.InputStream readFile( String filename ) throws IllegalArgumentException, java.io.IOException {
	public java.io.InputStream readFile( String filename ) throws java.io.FileNotFoundException, java.io.IOException {
		closeCurrentFile();

		java.io.File file = new java.io.File( currentDirectory, filename );
		if( ! file.exists() ) {
			//throw new IllegalArgumentException( file + " does not exist" );
			throw new java.io.FileNotFoundException( file + " does not exist" );
		}
		if( ! file.canRead() ) {
			throw new java.io.IOException( "cannot read " + file );
		}

		currentlyOpenStream = new java.io.FileInputStream( file );
		return currentlyOpenStream;
	}

	public void closeCurrentFile() throws java.io.IOException {
		if( currentlyOpenStream != null ) {
			currentlyOpenStream.close();
			currentlyOpenStream = null;
		}
	}

	public String [] getFilesInCurrentDirectory() {
		java.io.File [] files = currentDirectory.listFiles(
			new java.io.FileFilter() {
				public boolean accept( java.io.File f ) {
					return f.isFile();
				}
			}
		);

		String [] filenames = new String [files.length];
		for( int i = 0; i < files.length; i++ ) {
			filenames[i] = getRelativePathname( files[i] );
		}

		return filenames;
	}

	public String [] getDirectoriesInCurrentDirectory() {
		java.io.File [] files = currentDirectory.listFiles(
			new java.io.FileFilter() {
				public boolean accept( java.io.File f ) {
					return f.isDirectory();
				}
			}
		);

		String [] filenames = new String [files.length];
		for( int i = 0; i < files.length; i++ ) {
			filenames[i] = getRelativePathname( files[i] );
		}

		return filenames;
	}

	protected String getRelativePathname( java.io.File file ) {
		StringBuffer dir = new StringBuffer( file.getAbsolutePath() );
		dir.delete( 0, root.getAbsolutePath().length() );
		return dir.toString();
	}

	public boolean isKeepFileSupported() {
		return true;
	}

	static Object getKeepKey( java.io.File currentDirectory, String filename ) {
		return new java.io.File( currentDirectory, filename ).getAbsolutePath();
	}

	public Object getKeepKey( String filename ) throws KeepFileNotSupportedException {
		return getKeepKey( currentDirectory, filename );
	}
}
