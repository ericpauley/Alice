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

package edu.cmu.cs.stage3.alice.authoringtool.util;

/**
 * @author Jason Pratt
 */
public class ExtensionFileFilter extends javax.swing.filechooser.FileFilter implements Comparable {
	private String extension;
	private String description;

	public ExtensionFileFilter( String extension, String description ) {
		this.extension = extension.toUpperCase();
		this.description = description;
	}

	
	public boolean accept( java.io.File f ) {
		return f.isDirectory() || getExtension( f ).equalsIgnoreCase( extension );
	}

	
	public String getDescription() {
		return description;
	}

	public String getExtension() {
		return extension;
	}

	private String getExtension( java.io.File f ) {
		String ext = "";
		String fullName = f.getName();
		int i = fullName.lastIndexOf( '.' );

		if( (i > 0) && (i < fullName.length() - 1) ) {
		   ext = fullName.substring( i+1 ).toUpperCase();
		}
		return ext;
	}

	public int compareTo( Object o ) {
		if( o instanceof ExtensionFileFilter ) {
			return description.compareTo( ((ExtensionFileFilter)o).getDescription() );
		}
		return description.compareTo( o.toString() );
	}
}
