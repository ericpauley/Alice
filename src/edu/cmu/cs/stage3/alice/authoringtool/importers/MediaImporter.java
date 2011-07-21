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

package edu.cmu.cs.stage3.alice.authoringtool.importers;

/**
 * @author Jason Pratt
 */
public class MediaImporter extends edu.cmu.cs.stage3.alice.authoringtool.AbstractImporter {
	
	public java.util.Map getExtensionMap() {
//		java.util.HashMap knownCodecPrettyNames = new java.util.HashMap();
//		knownCodecPrettyNames.put( "AUDIO.XMP3", "Mpeg layer 3" );
//		knownCodecPrettyNames.put( "AUDIO.XWAV", "Windows sound" );
//		knownCodecPrettyNames.put( "MP3", "Mpeg layer 3" );
//		knownCodecPrettyNames.put( "WAV", "Windows sound" );
//
//		java.util.HashMap map = new java.util.HashMap();
//
//		String[] contentTypes = edu.cmu.cs.stage3.media.Manager.getContentTypes();
//		for( int i = 0; i < contentTypes.length; i++ ) {
//			String prettyName = (String)knownCodecPrettyNames.get( contentTypes[i].toUpperCase() );
//			if( prettyName == null ) {
//				prettyName = contentTypes[i];
//			}
//			String[] extensions = edu.cmu.cs.stage3.media.Manager.getExtensionsForContentType( contentTypes[i] );
//			if( extensions != null ) {
//				for( int j = 0; j < extensions.length; j++ ) {
//					map.put( extensions[j].toUpperCase(), prettyName );
//				}
//			}
//		}
//
//		return map;
		java.util.HashMap map = new java.util.HashMap();
		map.put( "WAV", "Windows sound" );
		map.put( "MP3", "Mpeg layer 3" );
		return map;
	}

	
	protected edu.cmu.cs.stage3.alice.core.Element load( java.io.InputStream istream, String ext ) throws java.io.IOException {
		edu.cmu.cs.stage3.media.DataSource dataSource = edu.cmu.cs.stage3.media.Manager.createDataSource( istream, ext );
		edu.cmu.cs.stage3.alice.core.Sound sound = new edu.cmu.cs.stage3.alice.core.Sound();
		sound.name.set( plainName );
		sound.dataSource.set( dataSource );
		return sound;
	}
}