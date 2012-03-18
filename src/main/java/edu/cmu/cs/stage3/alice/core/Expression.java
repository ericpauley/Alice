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

package edu.cmu.cs.stage3.alice.core;

public abstract class Expression extends Element {
	public abstract Object getValue();
	public abstract Class getValueClass();
	private java.util.Vector m_expressionListeners = new java.util.Vector();
	private edu.cmu.cs.stage3.alice.core.event.ExpressionListener[] m_expressionListenerArray = null;
	public void addExpressionListener( edu.cmu.cs.stage3.alice.core.event.ExpressionListener expressionListener ) {
		m_expressionListeners.addElement( expressionListener );
		m_expressionListenerArray = null;
	}
	public void removeExpressionListener( edu.cmu.cs.stage3.alice.core.event.ExpressionListener expressionListener ) {
		m_expressionListeners.removeElement( expressionListener );
		m_expressionListenerArray = null;
	}
	public edu.cmu.cs.stage3.alice.core.event.ExpressionListener[] getExpressionListeners() {
		if( m_expressionListenerArray==null ) {
			m_expressionListenerArray = new edu.cmu.cs.stage3.alice.core.event.ExpressionListener[m_expressionListeners.size()];
			m_expressionListeners.copyInto( m_expressionListenerArray );
		}
		return m_expressionListenerArray;
	}
	protected void onExpressionChange() {
		edu.cmu.cs.stage3.alice.core.event.ExpressionEvent expressionEvent = new edu.cmu.cs.stage3.alice.core.event.ExpressionEvent( this );
		java.util.Enumeration enum0 = m_expressionListeners.elements();
		while( enum0.hasMoreElements() ) {
			edu.cmu.cs.stage3.alice.core.event.ExpressionListener expressionListener = (edu.cmu.cs.stage3.alice.core.event.ExpressionListener)enum0.nextElement();
			expressionListener.expressionChanged( expressionEvent );
		}
	}
}
