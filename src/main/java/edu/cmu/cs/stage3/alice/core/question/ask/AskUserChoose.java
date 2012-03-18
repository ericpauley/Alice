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

public class AskUserChoose extends edu.cmu.cs.stage3.alice.core.question.list.ListObjectQuestion {
    public final StringProperty title = new StringProperty( this, "title", "Question" );
    public final StringProperty question = new StringProperty(this, "question", "Pick an Item:" );

	private edu.cmu.cs.stage3.alice.core.Clock m_clock;

    
	public Object getValue( edu.cmu.cs.stage3.alice.core.List listValue ) {
		if( m_clock != null ) {
			m_clock.pause();
		}
		try {
			//todo:
			//javax.swing.JList message = new javax.swing.JList();
			//Object = edu.cmu.cs.stage3.swing.DialogManager.showInputDialog( question.getStringValue(), message, title.getStringValue(), javax.swing.JOptionPane.QUESTION_MESSAGE, icon, selectionValues, initialSelectionValue );
			return null;
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