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

package edu.cmu.cs.stage3.alice.authoringtool.editors.scripteditor;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * @author Jason Pratt
 */
public class ScriptEditor extends javax.swing.JPanel implements edu.cmu.cs.stage3.alice.authoringtool.Editor {
	public String editorName = "Script Editor";

	protected edu.cmu.cs.stage3.alice.core.property.ScriptProperty scriptProperty;
	protected edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;
	protected edu.cmu.cs.stage3.alice.authoringtool.util.ScriptEditorPane scriptEditorPane = new edu.cmu.cs.stage3.alice.authoringtool.util.ScriptEditorPane();
	protected javax.swing.event.CaretListener caretListener = new javax.swing.event.CaretListener() {
		public void caretUpdate( javax.swing.event.CaretEvent e ) {
			ScriptEditor.this.updateLineNumber();
		}
	};
	protected javax.swing.event.DocumentListener documentListener = new javax.swing.event.DocumentListener() {
		//TODO: more efficient updating; this is going to be really costly when the script is large...
		public void changedUpdate( javax.swing.event.DocumentEvent e ) { scriptProperty.set( scriptEditorPane.getText() ); }
		public void insertUpdate( javax.swing.event.DocumentEvent e ) { scriptProperty.set( scriptEditorPane.getText() ); }
		public void removeUpdate( javax.swing.event.DocumentEvent e ) { scriptProperty.set( scriptEditorPane.getText() ); }
	};

	public ScriptEditor() {
		jbInit();
		guiInit();
	}

	private void guiInit() {
		scriptScrollPane.setViewportView( scriptEditorPane );
		scriptEditorPane.addCaretListener( caretListener );
		scriptEditorPane.performAllAction.setEnabled( false );
		scriptEditorPane.performSelectedAction.setEnabled( false );
	}

	public javax.swing.JComponent getJComponent() {
		return this;
	}

	public Object getObject() {
		return scriptProperty;
	}

	public void setObject( edu.cmu.cs.stage3.alice.core.property.ScriptProperty scriptProperty ) {
		scriptEditorPane.getDocument().removeDocumentListener( documentListener );
		this.scriptProperty = scriptProperty;

		if( this.scriptProperty != null ) {
			if( scriptProperty.getStringValue() == null ) {
				scriptProperty.set( "" );
			}
			scriptEditorPane.setText( scriptProperty.getStringValue() );

			scriptEditorPane.getDocument().addDocumentListener( documentListener );

			scriptEditorPane.resetUndoManager();
			scriptEditorPane.setSandbox( scriptProperty.getOwner().getSandbox() );
		} else {
			scriptEditorPane.resetUndoManager();
			scriptEditorPane.setSandbox( null );
		}
	}

	public void setAuthoringTool( edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool ) {
		this.authoringTool = authoringTool;
	}

	public void updateLineNumber() {
		//TODO: better formatting
		this.lineNumberLabel.setText( "  line number: " + (scriptEditorPane.getCurrentLineNumber() + 1) + "     " );
	}

	///////////////////////////////////////////////
	// AuthoringToolStateListener interface
	///////////////////////////////////////////////

	public void stateChanging( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldLoading( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldUnLoading( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldStarting( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldStopping( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldPausing( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldSaving( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}

	public void stateChanged( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldLoaded( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldUnLoaded( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldStarted( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldStopped( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldPaused( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldSaved( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}

	//////////////////////
	// Autogenerated
	//////////////////////

	BorderLayout borderLayout1 = new BorderLayout();
	JPanel southPanel = new JPanel();
	JScrollPane scriptScrollPane = new JScrollPane();
	JLabel lineNumberLabel = new JLabel();
	BoxLayout boxLayout1 = new BoxLayout( southPanel, BoxLayout.X_AXIS );
	Border border1;
	Border border2;
	Border border3;
	JPanel bogusPanel = new JPanel();
	Border border4;

	private void jbInit() {
		border1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED,Color.white,Color.lightGray,new Color(142, 142, 142),new Color(99, 99, 99));
		border2 = BorderFactory.createBevelBorder(BevelBorder.LOWERED,Color.white,Color.gray,new Color(142, 142, 142),new Color(99, 99, 99));
		border3 = BorderFactory.createEmptyBorder(1,1,1,1);
		border4 = BorderFactory.createBevelBorder(BevelBorder.LOWERED,Color.white,Color.lightGray,new Color(99, 99, 99),new Color(142, 142, 142));
		this.setLayout(borderLayout1);
		lineNumberLabel.setBorder(border1);
		lineNumberLabel.setText("  line number:     ");
		southPanel.setLayout(boxLayout1);
		southPanel.setBorder(border3);
		bogusPanel.setBorder(border4);
		this.add(southPanel, BorderLayout.SOUTH);
		southPanel.add(bogusPanel, null);
		southPanel.add(lineNumberLabel, null);
		this.add(scriptScrollPane, BorderLayout.CENTER);
	}
}