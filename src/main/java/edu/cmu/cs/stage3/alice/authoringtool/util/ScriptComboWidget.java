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
public class ScriptComboWidget extends javax.swing.JPanel {
	protected edu.cmu.cs.stage3.alice.core.Sandbox sandbox;
	protected edu.cmu.cs.stage3.alice.authoringtool.util.OneShotScheduler oneShotScheduler = new edu.cmu.cs.stage3.alice.authoringtool.util.OneShotScheduler();

	public final javax.swing.AbstractAction runAction = new javax.swing.AbstractAction() {
		public void actionPerformed( java.awt.event.ActionEvent ev ) {
			ScriptComboWidget.this.runScript();
		}
	};

	public ScriptComboWidget() {
		actionInit();
		guiInit();
	}

	private void actionInit() {
		//runAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.Event.CTRL_MASK) );
		runAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "go" );
		//runAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'G' ) );
		runAction.putValue( javax.swing.Action.NAME, "Go" );
		runAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, "Execute the given script" );
		//runAction.putValue( javax.swing.Action.SMALL_ICON, newWorldIcon );
	}

	public void setSandbox( edu.cmu.cs.stage3.alice.core.Sandbox sandbox ) {
		this.sandbox = sandbox;
	}

	public void runScript() {
		Object item = comboBox.getEditor().getItem();
		if( item instanceof String ) {
			String script = ((String)item).trim();
			if( script.length() != 0 ) {
				try {
					edu.cmu.cs.stage3.alice.scripting.Code code = sandbox.compile( script, "<Run Line>", edu.cmu.cs.stage3.alice.scripting.CompileType.EXEC_SINGLE );
					sandbox.exec( code );

					for( int i = 0; i < comboBox.getItemCount(); i++ ) { //this is not thread safe
						if( script.equals( comboBox.getItemAt( i ) ) ) {
							comboBox.removeItemAt( i );
							break;
						}
					}
					comboBox.insertItemAt( script, 0 );
					comboBox.setSelectedItem( script );
				} catch( org.python.core.PyException e ) {
					org.python.core.Py.printException( e, null, edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getPyStdErr() );
				} catch( Throwable t ) {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Error running jython code.", t );
				}
			}
		} else {
			comboBox.removeItem( item );
		}
	}

	/////////////////
	// GUI
	/////////////////

	private javax.swing.JComboBox comboBox = new javax.swing.JComboBox();
	private javax.swing.JButton runButton = new javax.swing.JButton( runAction );

	private void guiInit() {
		this.setLayout( new java.awt.BorderLayout() );
		comboBox.setEditable( true );
		comboBox.getEditor().addActionListener( runAction );

		add( comboBox, java.awt.BorderLayout.CENTER );
		add( runButton, java.awt.BorderLayout.EAST );
	}
}
