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

public class TokenBlock {
	public String tokenName = null;
	public String tokenArgs = null;
	public String tokenContents = null;
	public int tokenEndIndex = 0;

	public TokenBlock() {}
	public TokenBlock( String tokenName, String tokenContents, int tokenEndIndex ) {
		this.tokenName = tokenName;
		this.tokenContents = tokenContents;
		this.tokenEndIndex = tokenEndIndex;
		this.tokenArgs = new String();
	}

	public TokenBlock( String tokenName, String tokenArgs, String tokenContents, int tokenEndIndex ) {
		this.tokenName = tokenName;
		this.tokenContents = tokenContents;
		this.tokenEndIndex = tokenEndIndex;
		this.tokenArgs = tokenArgs;
	}

	public static TokenBlock getTokenBlock( int beginIndex, String content ) {
		int openTokenStart = content.indexOf( '<', beginIndex );
		if( openTokenStart == -1 ) {
			return new TokenBlock();
		}
		int openTokenStop = content.indexOf( '>', openTokenStart );
		if( openTokenStop == -1 ) {
			System.out.println( "'<' found with no closing '>'." );
			return null;
		}

		String tokenName = content.substring( openTokenStart + 1, openTokenStop ).trim();
		String tokenArgs = new String();
		/* modification to allow arguments in token name tags */
		int whiteSpace = tokenName.indexOf(" ");
		if ( whiteSpace != -1 ) {
		    tokenArgs = tokenName.substring(whiteSpace + 1, tokenName.length()).trim();
		    tokenName = tokenName.substring(0, whiteSpace);
		}
		int closeTokenStart = content.indexOf( "</" + tokenName + ">", openTokenStop );
		if( closeTokenStart == -1 ) {
			System.out.println( "No closing token (</" + tokenName + ">) found." );
			return null;
		}

		String blockContents = content.substring( openTokenStop + 1, closeTokenStart );
		int endIndex = closeTokenStart + tokenName.length() + 3;

		return new TokenBlock( tokenName, tokenArgs, blockContents, endIndex );
	}
}
