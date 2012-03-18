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

package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;

public class ScriptProperty extends StringProperty {
	private edu.cmu.cs.stage3.alice.scripting.Code m_code = null;
	public ScriptProperty( Element owner, String name, String defaultValue ) {
		super( owner, name, defaultValue );
	}
	
	protected void onSet( Object value ) {
		super.onSet( value );
		m_code = null;
	}
	public edu.cmu.cs.stage3.alice.scripting.Code getCode( edu.cmu.cs.stage3.alice.scripting.CompileType compileType ) {
		String script = getStringValue();
		if( script!=null && script.length()>0 ) {
			if( m_code==null ) {
   				m_code = getOwner().compile( script, this, compileType );
			}
		} else {
			m_code = null;
		}
		return m_code;
	}
	private String loadScript( java.io.InputStream is ) throws java.io.IOException {
		java.io.BufferedReader br = new java.io.BufferedReader( new java.io.InputStreamReader ( new java.io.BufferedInputStream( is ) ) );
		StringBuffer sb = new StringBuffer();
		while( true ) {
			String s = br.readLine();
			if( s!=null ) {
				sb.append( s );
				sb.append( '\n' );
			} else {
				break;
			}
		}
		if( sb.length()>0 ) {
			return sb.substring( 0, sb.length()-1 );
		} else {
			return "";
		}
	}
	private void storeScript( java.io.OutputStream os ) throws java.io.IOException {
		java.io.BufferedOutputStream bos = new java.io.BufferedOutputStream ( os );
		String script = getStringValue();
		if ( script!=null ) {
			bos.write( script.getBytes() );
		}
		bos.flush();
	}
    
	protected void decodeObject( org.w3c.dom.Element node, edu.cmu.cs.stage3.io.DirectoryTreeLoader loader, java.util.Vector referencesToBeResolved, double version ) throws java.io.IOException {
		m_associatedFileKey = null;
        String filename = getFilename( getNodeText( node ) );
        java.io.InputStream is = loader.readFile( filename );
        set( loadScript( is ) );
        loader.closeCurrentFile();
        try {
            m_associatedFileKey = loader.getKeepKey( filename );
        } catch( edu.cmu.cs.stage3.io.KeepFileNotSupportedException kfnse ) {
            m_associatedFileKey = null;
        }
    }
    
	protected void encodeObject( org.w3c.dom.Document document, org.w3c.dom.Element node, edu.cmu.cs.stage3.io.DirectoryTreeStorer storer, edu.cmu.cs.stage3.alice.core.ReferenceGenerator referenceGenerator ) throws java.io.IOException {
        String filename = getName()+".py";
        Object associatedFileKey;
        try {
            associatedFileKey = storer.getKeepKey( filename );
        } catch( edu.cmu.cs.stage3.io.KeepFileNotSupportedException kfnse ) {
            associatedFileKey = null;
        }
        if( m_associatedFileKey==null || !m_associatedFileKey.equals( associatedFileKey ) ) {
            m_associatedFileKey = null;
            java.io.OutputStream os = storer.createFile( filename, true );
            storeScript( os );
            storer.closeCurrentFile();
            try {
                m_associatedFileKey = storer.getKeepKey( filename );
            } catch( edu.cmu.cs.stage3.io.KeepFileNotSupportedException kfnse ) {
                m_associatedFileKey = null;
            }
        } else {
            if( storer.isKeepFileSupported() ) {
                try {
                    storer.keepFile( filename );
                } catch( edu.cmu.cs.stage3.io.KeepFileNotSupportedException kfnse ) {
                    throw new Error( storer + " returns true for isKeepFileSupported(), but then throws " + kfnse );
                } catch( edu.cmu.cs.stage3.io.KeepFileDoesNotExistException kfdne ) {
                    throw new edu.cmu.cs.stage3.alice.core.ExceptionWrapper( kfdne, filename );
                }
            }
        }
        node.appendChild( createNodeForString( document, "java.io.File["+filename+"]" ) );
    }
    
	public void keepAnyAssociatedFiles( edu.cmu.cs.stage3.io.DirectoryTreeStorer storer ) throws edu.cmu.cs.stage3.io.KeepFileNotSupportedException, edu.cmu.cs.stage3.io.KeepFileDoesNotExistException {
        super.keepAnyAssociatedFiles( storer );
        String filename = getName()+".py";
        storer.keepFile( filename );
    }
}
