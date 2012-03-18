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
 * @author Dennis Cosgrove
 */

public class CaptureRenderer implements javax.media.Renderer {
	private javax.media.Format[] m_inputFormats =
		new javax.media.Format[] {
			 new javax.media.format.AudioFormat(
				javax.media.format.AudioFormat.LINEAR,
				javax.media.Format.NOT_SPECIFIED,
				16,
				2,
				javax.media.format.AudioFormat.LITTLE_ENDIAN,
				javax.media.Format.NOT_SPECIFIED,
				javax.media.Format.NOT_SPECIFIED,
				javax.media.Format.NOT_SPECIFIED,
				javax.media.Format.byteArray 
			) 
		};
	private javax.media.Format m_inputFormat;

	private java.util.Vector m_buffers = new java.util.Vector();
	

	public javax.media.Format[] getSupportedInputFormats() {
		return m_inputFormats;
	}

	public javax.media.Format setInputFormat( javax.media.Format inputFormat ) {
		m_inputFormat = inputFormat;
		return m_inputFormat;
	}

	public String getName() {
		return "CaptureRenderer";
	}

	public void open() {
	}

	public void close() {
	}

	public void reset() {
		m_buffers.clear();
	}

	public void start() {
	}

	public void stop() {
	}

	public Object[] getControls() {
		return new Object[ 0 ];
	}

	public Object getControl( String name ) {
		return null;
	}

	public int process( javax.media.Buffer buffer ) {
		m_buffers.addElement( buffer.clone() );
		return BUFFER_PROCESSED_OK;
	}
	
	public int getDataLength() {
		int size = 0;
		java.util.Enumeration enum0 = m_buffers.elements();
		while( enum0.hasMoreElements() ) {
			javax.media.Buffer buffer = (javax.media.Buffer)enum0.nextElement();
			size += buffer.getOffset();
			size += buffer.getLength();
		}
		return size;
	}
	
	public void getData( byte[] data, int offset, int length ) {
		int location = offset;
		java.util.Enumeration enum0 = m_buffers.elements();
		while( enum0.hasMoreElements() ) {
			javax.media.Buffer buffer = (javax.media.Buffer)enum0.nextElement();
			location += buffer.getOffset();
			if( ( location + buffer.getLength() ) <= data.length ) {
				System.arraycopy( buffer.getData(), 0, data, location, buffer.getLength() );
			} else {
				System.err.println( "out of range " + buffer + " " + buffer.getOffset() + " " + buffer.getLength() );
				System.err.println( data.length + " " + location );
			}
			location += buffer.getLength();
		}
	}
}
