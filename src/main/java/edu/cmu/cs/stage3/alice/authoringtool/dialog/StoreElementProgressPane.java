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

package edu.cmu.cs.stage3.alice.authoringtool.dialog;

public class StoreElementProgressPane extends edu.cmu.cs.stage3.progress.ProgressPane {
	private edu.cmu.cs.stage3.alice.core.Element m_element;
	private java.io.File m_file;
	private java.util.Dictionary m_filnameToByteArrayMap;
	private boolean m_wasSuccessful = false;
	public StoreElementProgressPane( String title, String preDescription ) {
		super( title, preDescription );
	}
	
	public boolean wasSuccessful() {
		return m_wasSuccessful;
	}
	
	protected void construct() throws edu.cmu.cs.stage3.progress.ProgressCancelException {
		m_wasSuccessful = false;
		try {
			m_element.store( m_file, this, m_filnameToByteArrayMap );
			m_wasSuccessful = true;
		} catch( edu.cmu.cs.stage3.progress.ProgressCancelException pce ) {
			throw pce;
		} catch( Throwable t ) {
			StringBuffer sb = new StringBuffer();
			sb.append( "An error has occurred while attempting to save your world.\n" );
			sb.append( "This is a critical situation that needs to be dealt with immediately.\n\n" );
			if( t instanceof java.io.IOException ) {
				sb.append( "This may be the result of not having enough space on the target drive.\n" );			
				sb.append( "If possible,\n    attempt to save your world to a different drive, or\n    free up some space and \"Save As\" to a different file.\n\n" );
				sb.append( "NOTE: If unsuccessful, please" );
			} else {
				sb.append( "NOTE: Please" );
			}
			sb.append( " check for a directory co-located with \nyour world named \"Backups of <YourWorldNameHere>\" which \nshould contain previously saved versions of your world.\n" );

			sb.append( "\nWe at the Alice Team apologize for any work you have lost\n" );
			sb.append( "\nPlease accept our sincerest apologies.  The Alice Team." );
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( sb.toString(), t );
		}
	}
	public void setElement( edu.cmu.cs.stage3.alice.core.Element element ) {
		m_element = element;
	}
	public void setFile( java.io.File file ) {
		m_file = file;
	}
	public void setFilnameToByteArrayMap( java.util.Dictionary filnameToByteArrayMap ) {
		m_filnameToByteArrayMap = filnameToByteArrayMap;
	}
}
