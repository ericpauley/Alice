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
public class FormatTokenizer {
	protected java.util.Iterator tokenIterator;

	public FormatTokenizer( String inputString ) {
		if( inputString != null ) {
			java.util.LinkedList tokens = new java.util.LinkedList();
			while( inputString.length() > 0 ) {
				if( inputString.startsWith( "<<<" ) ) {
					if( inputString.indexOf( ">>>" ) > 0 ) {
						tokens.add( inputString.substring( 0, inputString.indexOf( ">>>" ) + 3 ) );
						inputString = inputString.substring( inputString.indexOf( ">>>" ) + 3 );
					} else {
						tokens.add( inputString );
						inputString = "";
					}
				} else if( inputString.startsWith( "<<" ) ) {
					if( inputString.indexOf( ">>" ) > 0 ) {
						tokens.add( inputString.substring( 0, inputString.indexOf( ">>" ) + 2 ) );
						inputString = inputString.substring( inputString.indexOf( ">>" ) + 2 );
					} else {
						tokens.add( inputString );
						inputString = "";
					}
				} else if( inputString.startsWith( "<" ) ) {
					if( inputString.indexOf( ">" ) > 0 ) {
						tokens.add( inputString.substring( 0, inputString.indexOf( ">" ) + 1 ) );
						inputString = inputString.substring( inputString.indexOf( ">" ) + 1 );
					} else {
						tokens.add( inputString );
						inputString = "";
					}
				} else {
					if( inputString.indexOf( '<' ) > 0 ) {
						tokens.add( inputString.substring( 0, inputString.indexOf( '<' ) ) );
						inputString = inputString.substring( inputString.indexOf( '<' ) );
					} else {
						tokens.add( inputString );
						inputString = "";
					}
				}
			}

			tokenIterator = tokens.iterator();
		}
	}

	public boolean hasMoreTokens() {
		if( tokenIterator != null ) {
			return tokenIterator.hasNext();
		} else {
			return false;
		}
	}

	public String nextToken() {
		if( tokenIterator != null ) {
			if (tokenIterator.hasNext()){
				return (String)tokenIterator.next();
			}
			else{
				return null;
			}
		} else {
			return null;
		}
	}
}