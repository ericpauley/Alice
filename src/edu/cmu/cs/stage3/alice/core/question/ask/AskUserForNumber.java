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

package edu.cmu.cs.stage3.alice.core.question.ask;

import edu.cmu.cs.stage3.alice.core.property.StringProperty;

/**
 * @author Ben Buchwald, Dennis Cosgrove
 */

public class AskUserForNumber extends edu.cmu.cs.stage3.alice.core.question.NumberQuestion {
    public final StringProperty title = new StringProperty( this, "title", "Question");
    public final StringProperty question = new StringProperty( this, "question", "Enter a Number:" );

	private edu.cmu.cs.stage3.alice.core.Clock m_clock;

    
	public Object getValue() {
		if( m_clock != null ) {
			m_clock.pause();
		}
		try {
			String string = edu.cmu.cs.stage3.swing.DialogManager.showInputDialog( question.getStringValue(), title.getStringValue(), javax.swing.JOptionPane.QUESTION_MESSAGE );
			if (string != null && ( string.matches( "-?\\d+" ) || string.matches( "-?\\d+.\\d+" ) ))
				return new Double( string );
			return new Double( 0 );
		} finally {
			if( m_clock != null ) {
				m_clock.resume();
			}
		}
    }

	
	protected void started( edu.cmu.cs.stage3.alice.core.World world, double time ) {
		super.started( world, time );
		if( world != null ) {
			m_clock = world.getClock();
		} 
	}
	
	protected void stopped( edu.cmu.cs.stage3.alice.core.World world, double time ) {
		m_clock = null;
		super.stopped( world, time );
	}
}