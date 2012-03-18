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
 * <code>DirectoryTreeLoader</code> defines an interface for mechanisms that can load
 * hierarchical trees of files, either from a filesystem, a network resource, an archive file
 * (i.e. .tar), a compressed archive file (i.e. .zip), or some other storage medium.
 *
 * @author Jason Pratt
 */
public interface DirectoryTreeLoader {
	/**
	 * prepares the loader to read a new directory tree from <code>pathname</code>.
	 * <code>pathname</code> may be a directory, filename, URL, or something else,
	 * depending on the implementation.
	 *
	 * @throws IllegalArgumentException if <code>pathname</code> is of an incorrect type
	 * @throws java.io.IOException
	 */
	public void open( Object pathname ) throws IllegalArgumentException, java.io.IOException;

	/**
	 * performs any clean-up necessary for closing the currently open directory tree.
	 */
	public void close() throws java.io.IOException;

	/**
	 * sets the current directory.
	 * if <code>pathname</code> begins with '/', the operation is relative to the root of this tree,
	 * otherwise the operation is relative to the current directory.
	 *
	 * @throws IllegalArgumentException if <code>pathname</code> is ill-formed or doesn't exist
	 */
	public void setCurrentDirectory( String pathname ) throws IllegalArgumentException;

	/**
	 * @returns the current directory, relative to the root of this tree
	 */
	public String getCurrentDirectory();

	/**
	 * returns an array of Strings which represent the files in the current directory
	 */
	public String [] getFilesInCurrentDirectory();

	/**
	 * returns an array of Strings which represent the directory entries in the current directory
	 */
	public String [] getDirectoriesInCurrentDirectory();

	/**
	 * opens a "file" with the given <code>filename</code>.
	 * the path will be determined by the current directory,
	 * and should not be specified in <code>filename</code>.
	 *
	 * @returns a stream which can be used to read from the file.
	 * if <code>readFile</code> is called twice before <code>closeCurrentFile</code>
	 * is called, <code>closeCurrentFile</code> will be called automatically,
	 * and the stream will become invalid.
	 *
	 * @throws IllegalArgumentException if <code>filename</code> is ill-formed
	 */
	public java.io.InputStream readFile( String filename ) throws IllegalArgumentException, java.io.IOException;

	/**
	 * closes the current file, performing any necessary clean-up operations, depending on the implementation.
	 */
	public void closeCurrentFile() throws java.io.IOException;

	/**
	 * if true, then this DirectoryTreeLoader supports the <code>getKeepKey()</code> method, which can be used
	 * with <code>getKeepKey()</code> and <code>keepFile()</code> on DirectoryTreeStorer
	 *
	 * @see #keepFile
	 */
	public boolean isKeepFileSupported();

	/**
	 * useful in avoiding the unnecessary storing of unchanged data to the same file location as previously loaded or stored.
	 *
	 * @throws KeepFileNotSupportedException if <code>getKeepKey()</code> cannot be used with this Loader
	 *
	 * @returns a unique string identifier for the given filename in the current directory
	 *
	 * @see #isKeepFileSupported
	 * @see #edu.cmu.cs.stage3.io.DirectoryTreeStorer.getKeepKey
	 * @see #edu.cmu.cs.stage3.io.DirectoryTreeStorer.keepFile
	 * @see #edu.cmu.cs.stage3.io.DirectoryTreeStorer.isKeepFileSupported
	 */
	public Object getKeepKey( String filename ) throws KeepFileNotSupportedException;
}
