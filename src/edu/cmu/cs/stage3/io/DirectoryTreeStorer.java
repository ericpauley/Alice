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
 * <code>DirectoryTreeStorer</code> defines an interface for mechanisms that can store
 * hierarchical trees of files, either to a filesystem, a network resource, an archive file
 * (i.e. tar), a compressed archive file (i.e. zip), or some other storage medium.
 *
 * @author Jason Pratt
 */
public interface DirectoryTreeStorer {
	/**
	 * prepares the storer to write a new directory tree to <code>pathname</code>.
	 * <code>pathname</code> may be a directory, filename, URL, OutputStream, or something else,
	 * depending on the implementation.
	 *
	 * @throws IllegalArgumentException if <code>pathname</code> is of an incorrect type
	 * @throws java.io.IOException
	 */
	public void open( Object pathname ) throws IllegalArgumentException, java.io.IOException;

	/**
	 * performs any clean-up necessary for the currently open directory tree.
	 */
	public void close() throws java.io.IOException;

	/**
	 * should create a new directory with the given <code>pathname</code>,
	 * relative to the current directory.
	 *
	 * @throws IllegalArgumentException if <code>pathname</code> is ill-formed
	 * @throws java.io.IOException
	 */
	public void createDirectory( String pathname ) throws IllegalArgumentException, java.io.IOException;

	/**
	 * sets the current directory.
	 * if <code>pathname</code> begins with '/', the operation is relative to the root of this tree,
	 * otherwise the operation is relative to the current directory.
	 *
	 * new files and directories will be made relative to the current directory.
	 *
	 * @throws IllegalArgumentException if <code>pathname</code> is ill-formed or doesn't exist
	 */
	public void setCurrentDirectory( String pathname ) throws IllegalArgumentException;

	/**
	 * @returns the current directory, relative to the root of this tree
	 */
	public String getCurrentDirectory();

	/**
	 * creates a file with the given <code>filename</code>.
	 * the path will be determined by the current directory,
	 * and should not be specified in <code>filename</code>.
	 *
	 * if <code>compressItIfYouGotIt</code> is true, then the Storer
	 * should compress this data if it is capable of doing so.
	 * <code>compressItIfYouGotIt</code> should be false for formats that
	 * are already compressed, such as .png and .mp3.
	 *
	 * @returns a stream which can be used to write to the new file.
	 * if <code>createFile</code> is called twice before <code>closeCurrentFile</code>
	 * is called, <code>closeCurrentFile</code> will be called automatically,
	 * and the stream will become invalid.
	 *
	 * @throws IllegalArgumentException if <code>filename</code> is ill-formed
	 */
	public java.io.OutputStream createFile( String filename, boolean compressItIfYouGotIt ) throws IllegalArgumentException, java.io.IOException;

	/**
	 * closes the current file, performing any necessary clean-up operations, depending on the implementation.
	 */
	public void closeCurrentFile() throws java.io.IOException;

	/**
	 * useful in avoiding the unnecessary storing of unchanged data to the same file location as previously loaded or stored.
	 * return null if not applicable.
	 *
	 * @throws KeepFileNotSupportedException if <code>getKeepKey()</code> cannot be used with this Storer
	 *
	 * @returns a unique string identifier for the given filename in the current directory
	 *
	 * @see #edu.cmu.cs.stage3.io.DirectoryTreeLoader.getKey
	 */
	public Object getKeepKey( String filename ) throws KeepFileNotSupportedException;

	/**
	 * if true, then this DirectoryTreeStorer supports the <code>keepFile()</code> and <code>getKeepKey()</code> methods
	 *
	 * @see #keepFile
	 */
	public boolean isKeepFileSupported();

	/**
	 * indicates to the Storer that the given file should be retained in the store as is.  Storers that support
	 * <code>keepFile()</code> should make a cleanup pass on <code>close()</code> that removes any files that
	 * were not either written with <code>createFile()</code> or indicated as keepers with <code>keepFile()</code>.
	 *
	 * @throws KeepFileNotSupportedException if <code>keepFile()</code> cannot be used with this Storer
	 * @throws KeepFileDoesNotExistException if <code>keepFile()</code> is called for a file that does not exist in the store
	 */
	public void keepFile( String filename ) throws KeepFileNotSupportedException, KeepFileDoesNotExistException;
}
