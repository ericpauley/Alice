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

package edu.cmu.cs.stage3.alice.core.event;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Transformable;

public class MessageEvent extends java.util.EventObject {
	private String m_message;
	private Transformable m_fromWho;
	private Transformable m_toWhom;
	private long m_when;

	public MessageEvent( Element source, String message, Transformable fromWho, Transformable toWhom, long when ) {
		super( source );
		m_message = message;
		m_fromWho = fromWho;
		m_toWhom = toWhom;
		m_when = when;
	}
	public String getMessage() {
		return m_message;
	}
	public Transformable getFromWho() {
		return m_fromWho;
	}
	public Transformable getToWhom() {
		return m_toWhom;
	}
	public long getWhen() {
		return m_when;
	}
}
