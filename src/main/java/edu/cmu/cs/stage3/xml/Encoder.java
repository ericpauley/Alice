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

package edu.cmu.cs.stage3.xml;

public class Encoder {
	public static void write( org.w3c.dom.Document document, java.io.OutputStream os ) throws java.io.IOException {
		java.io.BufferedOutputStream bos;
		if( os instanceof java.io.BufferedOutputStream ) {
			bos = (java.io.BufferedOutputStream)os;
		} else {
			bos = new java.io.BufferedOutputStream( os );
		}
		org.apache.xml.serialize.OutputFormat outputFormat = new org.apache.xml.serialize.OutputFormat( document, "UTF-8", true );
		org.apache.xml.serialize.XMLSerializer xmlSerializer = new org.apache.xml.serialize.XMLSerializer( bos, outputFormat );
		xmlSerializer.serialize( document );
		bos.flush();
	}
	public static void write( org.w3c.dom.Document document, java.io.Writer w ) throws java.io.IOException {
		java.io.BufferedWriter bw;
		if( w instanceof java.io.BufferedWriter ) {
			bw = (java.io.BufferedWriter)w;
		} else {
			bw = new java.io.BufferedWriter( w );
		}
		org.apache.xml.serialize.OutputFormat outputFormat = new org.apache.xml.serialize.OutputFormat( document, "UTF-8", true );
		org.apache.xml.serialize.XMLSerializer xmlSerializer = new org.apache.xml.serialize.XMLSerializer( bw, outputFormat );
		xmlSerializer.serialize( document );
		bw.flush();
	}
}