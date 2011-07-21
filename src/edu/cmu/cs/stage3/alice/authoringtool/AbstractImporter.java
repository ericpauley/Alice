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

package edu.cmu.cs.stage3.alice.authoringtool;

public abstract class AbstractImporter implements edu.cmu.cs.stage3.alice.authoringtool.Importer {
	private Object location = null;
	protected String plainName = null;

	public abstract java.util.Map getExtensionMap();

	public edu.cmu.cs.stage3.alice.core.Element load( String filename ) throws java.io.IOException {
		location = new java.io.File( filename ).getParentFile();
		String fullName = new java.io.File( filename ).getName();
		plainName = fullName.substring( 0, fullName.indexOf( '.' ) );
		if( edu.cmu.cs.stage3.alice.core.Element.isPotentialNameValid( plainName ) ) {
			//pass
		} else {
			plainName = edu.cmu.cs.stage3.alice.core.Element.generateValidName( plainName );
		}
		java.io.FileInputStream fis = new java.io.FileInputStream( filename );
		edu.cmu.cs.stage3.alice.core.Element castMember = load( fis, getExtension( filename ) );
		fis.close();
		plainName = null;
		return castMember;
	}

	public edu.cmu.cs.stage3.alice.core.Element load( java.io.File file ) throws java.io.IOException {
		location = file.getParentFile();
		String fullName = file.getName();
		plainName = fullName.substring( 0, fullName.indexOf( '.' ) );
		if( edu.cmu.cs.stage3.alice.core.Element.isPotentialNameValid( plainName ) ) {
			//pass
		} else {
			plainName = edu.cmu.cs.stage3.alice.core.Element.generateValidName( plainName );
		}
		java.io.FileInputStream fis = new java.io.FileInputStream( file );
		edu.cmu.cs.stage3.alice.core.Element castMember = load( fis, getExtension( file.getName() ) );
		fis.close();
		plainName = null;
		return castMember;
	}

	public edu.cmu.cs.stage3.alice.core.Element load( java.net.URL url ) throws java.io.IOException {
		String externalForm = url.toExternalForm();
		location = new java.net.URL( externalForm.substring( 0, externalForm.lastIndexOf( '/' ) + 1 ) );
		String fullName = externalForm.substring( externalForm.lastIndexOf( '/' ) + 1 );
		plainName = fullName.substring( 0, fullName.lastIndexOf( '.' ) );
		if( edu.cmu.cs.stage3.alice.core.Element.isPotentialNameValid( plainName ) ) {
			//pass
		} else {
			plainName = edu.cmu.cs.stage3.alice.core.Element.generateValidName( plainName );
		}
		edu.cmu.cs.stage3.alice.core.Element castMember = load( url.openStream(), getExtension( url.getFile() ) );
		plainName = null;
		return castMember;
	}

	protected abstract edu.cmu.cs.stage3.alice.core.Element load( java.io.InputStream istream, String ext ) throws java.io.IOException;

	public Object getLocation() {
		return location;
	}

	private String getExtension( String filename ) {
		if( filename == null ) {
			throw new IllegalArgumentException( "null filename encountered" );
		}
		filename.trim();
		int i = filename.lastIndexOf( "." );
		if( i == -1 ) {
			throw new IllegalArgumentException( "unable to determine the extension of " + filename );
		}
		String ext = filename.substring( i + 1 );
		if( ext.length() < 1 ) {
			throw new IllegalArgumentException( "unable to determine the extension of " + filename );
		}
		ext = ext.toUpperCase();
		if( getExtensionMap().get( ext ) == null ) {
			throw new IllegalArgumentException( ext + " files are not supported by this Importer" );
		}
		return ext;
	}
}