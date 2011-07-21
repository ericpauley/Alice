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
public class ExtensionGroupFileFilter extends javax.swing.filechooser.FileFilter implements Comparable {
	private java.util.ArrayList extensions;
	private String description = "";
	private String baseDescription = "";

	public ExtensionGroupFileFilter( String baseDescription ) {
		this.extensions = new java.util.ArrayList();
		this.baseDescription = baseDescription;
	}

	public ExtensionGroupFileFilter( java.util.ArrayList extensions, String baseDescription ) {
		this.extensions = extensions;
		this.baseDescription = baseDescription;
	}

	public void addExtensionFileFilter( ExtensionFileFilter ext ) {
		extensions.add( ext );
	}

	private void recalculateDescription() {
		StringBuffer d = new StringBuffer( baseDescription );
		d.append( " (" );

		java.util.Iterator iter = extensions.iterator();
		if( iter.hasNext() ) {
			ExtensionFileFilter ext = (ExtensionFileFilter)iter.next();
			d.append( ext.getExtension() );
		}
		while( iter.hasNext() ) {
			ExtensionFileFilter ext = (ExtensionFileFilter)iter.next();
			d.append( ";" + ext.getExtension() );
		}

		d.append( ")" );

		description = d.toString();
	}

	
	public boolean accept( java.io.File f ) {
		for( java.util.Iterator iter = extensions.iterator(); iter.hasNext(); ) {
			ExtensionFileFilter ext = (ExtensionFileFilter)iter.next();
			if( ext.accept( f ) ) {
				return true;
			}
		}

		return false;
	}

	
	public String getDescription() {
		recalculateDescription();
		return description;
	}

	public int compareTo( Object o ) {
		//account for change in java 1.5 VM
		if( o instanceof String ) {
			return description.compareTo( (String)o );
		} else {
			return -1;
		}
	}
}
