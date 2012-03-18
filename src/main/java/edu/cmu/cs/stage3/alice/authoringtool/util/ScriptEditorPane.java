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
public class ScriptEditorPane extends javax.swing.JEditorPane {
	protected edu.cmu.cs.stage3.alice.core.Sandbox sandbox;
	protected javax.swing.Action[] actions;
	protected javax.swing.JPopupMenu popup;

	// Find/Replace state variables
	protected String findString = "";
	protected String replaceWithString = "";
	protected boolean matchCase = false;
	protected boolean findFromStart = true;

	//////////////////////////////////////
	// Actions
	//////////////////////////////////////

	//TODO: should probably cache compiled code.  shouldn't be a issue in the editor, though.
	public final javax.swing.Action performAllAction = new javax.swing.AbstractAction() {
		public void actionPerformed( java.awt.event.ActionEvent ev ) {
			if( sandbox != null ) {
				String script = ScriptEditorPane.this.getText();
				try {
					edu.cmu.cs.stage3.alice.scripting.Code code = sandbox.compile( script, "<ScriptEditorPane>", edu.cmu.cs.stage3.alice.scripting.CompileType.EXEC_MULTIPLE );
					sandbox.exec( code );
				} catch( org.python.core.PyException e ) {
					if( org.python.core.Py.matchException( e, org.python.core.Py.SystemExit ) ) {
						//TODO
					} else {
						org.python.core.Py.printException( e, null, edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getPyStdErr() );
					}
				}
				//DEBUG System.out.println( "performedAll:\n" + script );
			}
		}
	};

	public final javax.swing.Action performSelectedAction = new javax.swing.AbstractAction() {
		public void actionPerformed( java.awt.event.ActionEvent ev ) {
			if( sandbox != null ) {
				try {
					int selectionStart = ScriptEditorPane.this.getLineStartOffset( ScriptEditorPane.this.getLineOfOffset( ScriptEditorPane.this.getSelectionStart() ) );
					int selectionEnd = ScriptEditorPane.this.getLineEndOffset( ScriptEditorPane.this.getLineOfOffset( ScriptEditorPane.this.getSelectionEnd() ) );
					String script = ScriptEditorPane.this.getText( selectionStart, selectionEnd-selectionStart );
					try {
						edu.cmu.cs.stage3.alice.scripting.Code code = sandbox.compile( script, "<ScriptEditorPane>", edu.cmu.cs.stage3.alice.scripting.CompileType.EXEC_MULTIPLE );
						sandbox.exec( code );
					} catch( org.python.core.PyException e ) {
						if( org.python.core.Py.matchException( e, org.python.core.Py.SystemExit ) ) {
							//TODO
						} else {
							org.python.core.Py.printException( e, null, edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getPyStdErr() );
						}
					}
					//DEBUG System.out.println( "performedSelected:\n" + script );
				} catch( javax.swing.text.BadLocationException ble ) {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Error getting selected code.", ble );
				}
			}
		}
	};

	public ScriptEditorPane() {
		actionInit();
		popupInit();
		setDefaultKeyBindingsEnabled( true );
		setFont( new java.awt.Font( "Monospaced", 0, 12 ) );
		getDocument().addUndoableEditListener( undoHandler );
		addMouseListener( editorPaneMouseListener );
		setSize( new java.awt.Dimension( 10000, 100 ) ); // this avoids line-wrapping
	}

	private void actionInit() {
		performAllAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_F4, java.awt.Event.CTRL_MASK ) );
		performAllAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "performAll" );
		performAllAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'A' ) );
		performAllAction.putValue( javax.swing.Action.NAME, "Perform All (Ctrl-F4)" );
		performAllAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, "Performs the entire script." );

		performSelectedAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_F4, 0 ) );
		performSelectedAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "performSelected" );
		performSelectedAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'S' ) );
		performSelectedAction.putValue( javax.swing.Action.NAME, "Perform Selected (F4)" );
		performSelectedAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, "Performs the selected lines of the script, or the line the cursor is on if there is no selection." );

		undoAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_Z, java.awt.Event.CTRL_MASK ) );
		undoAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "undoEdit" );
		undoAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'U' ) );
		undoAction.putValue( javax.swing.Action.NAME, "Undo (Ctrl-Z)" );
		undoAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, "Undo last edit" );

		redoAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_Y, java.awt.Event.CTRL_MASK ) );
		redoAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "redoEdit" );
		redoAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'R' ) );
		redoAction.putValue( javax.swing.Action.NAME, "Redo (Ctrl-Y)" );
		redoAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, "Redo last undo" );

		actions = new javax.swing.Action[] { performAllAction, performSelectedAction, undoAction, redoAction };
	}

	private void popupInit() {
		popup = new javax.swing.JPopupMenu( "" );
		if( performAllAction.isEnabled() ) {
			popup.add( performAllAction );
		}
		if( performSelectedAction.isEnabled() ) {
			popup.add( performSelectedAction );
		}
		if( performAllAction.isEnabled() || performSelectedAction.isEnabled() ) {
			popup.addSeparator();
		}
		popup.add( undoAction );
		popup.add( redoAction );
	}

	public boolean findNext( String stringToFind ) {
		int startFrom;
		if( getSelectionStart() != getSelectionEnd() ) {
			startFrom = getSelectionEnd();
		} else {
			startFrom = getCaretPosition();
		}
		String script = getText(); //TODO: could be more efficient...
		int startSelection;
		if( matchCase ) {
			startSelection = script.indexOf( stringToFind, startFrom );
		} else {
			startSelection = script.toLowerCase().indexOf( stringToFind.toLowerCase(), startFrom );
		}
		int endSelection = startSelection + stringToFind.length();
		if( (startSelection >= 0) && (endSelection <= script.length()) ) {
			select( startSelection, endSelection );
			return true;
		} else {
			return false;
		}
	}

	public void replaceCurrent( String stringToReplaceWith ) {
		replaceSelection( stringToReplaceWith );
	}

	public void replaceAllRemaining( String stringToFind, String stringToReplaceWith ) {
		int currentPosition = getCaretPosition();

		if( findNext( findString ) ) {
			replaceCurrent( stringToReplaceWith );
			while( findNext( stringToFind ) ) {
				replaceCurrent( stringToReplaceWith );
			}
		} else {
			edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog( "String '" + findString + "' not found.", "String not found", javax.swing.JOptionPane.INFORMATION_MESSAGE );
		}
		setCaretPosition( currentPosition );
	}

	
	public void setDocument( javax.swing.text.Document doc ) {
		super.setDocument( doc );
		getDocument().addUndoableEditListener( undoHandler );
	}

	public edu.cmu.cs.stage3.alice.core.Element getSandbox() {
		return sandbox;
	}

	public void setSandbox( edu.cmu.cs.stage3.alice.core.Sandbox sandbox ) {
		this.sandbox = sandbox;
	}

	public void setDefaultKeyBindingsEnabled( boolean b ) {
		setDefaultKeyBindingsEnabled( b, actions );
	}

	public void setDefaultKeyBindingsEnabled( boolean b, javax.swing.Action[] whichActions ) {
		javax.swing.text.Keymap keymap = getKeymap();
		if( b ) {
			for( int i = 0; i < whichActions.length; i++ ) {
				getInputMap().put( (javax.swing.KeyStroke)whichActions[i].getValue( javax.swing.Action.ACCELERATOR_KEY ), whichActions[i].getValue( javax.swing.Action.ACTION_COMMAND_KEY ) );
				getActionMap().put( whichActions[i].getValue( javax.swing.Action.ACTION_COMMAND_KEY ), whichActions[i] );
			}
		} else {
			for( int i = 0; i < whichActions.length; i++ ) {
				this.getInputMap().remove( (javax.swing.KeyStroke)whichActions[i].getValue( javax.swing.Action.ACCELERATOR_KEY ) );
				getActionMap().remove( whichActions[i].getValue( javax.swing.Action.ACTION_COMMAND_KEY ) );
			}
		}
	}

	public int getCurrentLineNumber() {
		try {
			return getLineOfOffset( getCaretPosition() );
		} catch( javax.swing.text.BadLocationException e ) {
			return -1;
		}
	}

	//////////////////////////////////
	// Adapted from JTextArea
	//////////////////////////////////

	public int getLineOfOffset( int offset ) throws javax.swing.text.BadLocationException {
		javax.swing.text.Document doc = getDocument();
		if( doc instanceof javax.swing.text.PlainDocument ) {
			if( offset < 0 ) {
				throw new javax.swing.text.BadLocationException("Can't translate offset to line", -1);
			} else if( offset > doc.getLength() ) {
				throw new javax.swing.text.BadLocationException("Can't translate offset to line", doc.getLength()+1);
			} else {
				javax.swing.text.Element map = getDocument().getDefaultRootElement();
				return map.getElementIndex( offset );
			}
		} else {
			throw new java.lang.UnsupportedOperationException( "Cannot find line number; only PlainDocuments supported at this time." );
		}
	}

	public int getLineStartOffset( int line ) throws javax.swing.text.BadLocationException {
		javax.swing.text.Document doc = getDocument();
		if( doc instanceof javax.swing.text.PlainDocument ) {
			javax.swing.text.Element map = doc.getDefaultRootElement();
			if( line < 0 ) {
				throw new javax.swing.text.BadLocationException( "Negative line", -1 );
			} else if (line >= map.getElementCount()) {
				throw new javax.swing.text.BadLocationException( "No such line", getDocument().getLength() + 1 );
			} else {
				javax.swing.text.Element lineElem = map.getElement( line );
				return lineElem.getStartOffset();
			}
		} else {
			throw new java.lang.UnsupportedOperationException( "Cannot find line start offset; only PlainDocuments supported at this time." );
		}
	}

	public int getLineEndOffset(int line) throws javax.swing.text.BadLocationException {
		javax.swing.text.Document doc = getDocument();
		if( doc instanceof javax.swing.text.PlainDocument ) {
			javax.swing.text.Element map = doc.getDefaultRootElement();
			if( line < 0 ) {
				throw new javax.swing.text.BadLocationException("Negative line", -1);
			} else if( line >= map.getElementCount() ) {
				throw new javax.swing.text.BadLocationException("No such line", getDocument().getLength()+1);
			} else {
				javax.swing.text.Element lineElem = map.getElement(line);
				return lineElem.getEndOffset();
			}
		} else {
			throw new java.lang.UnsupportedOperationException( "Cannot find line end offset; only PlainDocuments supported at this time." );
		}
	}

	///////////////////////
	// Mouse Listener
	///////////////////////

	protected final java.awt.event.MouseListener editorPaneMouseListener = new edu.cmu.cs.stage3.alice.authoringtool.util.CustomMouseAdapter() {
		
		protected void popupResponse( java.awt.event.MouseEvent e ) {
			ScriptEditorPane.this.popupInit();
			popup.show( e.getComponent(), e.getX(), e.getY() );
			PopupMenuUtilities.ensurePopupIsOnScreen( popup );
		}
	};

	///////////////////////
	// Undo / Redo
	///////////////////////

	public final UndoAction undoAction = new UndoAction();
	public final RedoAction redoAction = new RedoAction();
	protected javax.swing.event.UndoableEditListener undoHandler = new UndoHandler();
	protected javax.swing.undo.UndoManager undoManager = new javax.swing.undo.UndoManager();

	public void resetUndoManager() {
		undoManager.discardAllEdits();
		undoAction.update();
		redoAction.update();
	}

	class UndoHandler implements javax.swing.event.UndoableEditListener {
		public void undoableEditHappened( javax.swing.event.UndoableEditEvent ev ) {
			undoManager.addEdit( ev.getEdit() );
			undoAction.update();
			redoAction.update();
		}
	}

	class UndoAction extends javax.swing.AbstractAction {
		public UndoAction() {
			super( "Undo" );
			setEnabled( false );
		}

		public void actionPerformed( java.awt.event.ActionEvent ev ) {
			try {
				undoManager.undo();
			} catch( javax.swing.undo.CannotUndoException e ) {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Error: unable to undo.", e );
			}
			update();
			redoAction.update();
		}

		protected void update() {
			if( undoManager.canUndo() ) {
				setEnabled( true );
				putValue( javax.swing.Action.NAME, undoManager.getUndoPresentationName() );
			} else {
				setEnabled( false );
				putValue( javax.swing.Action.NAME, "Undo" );
			}
		}
	}

	class RedoAction extends javax.swing.AbstractAction {
		public RedoAction() {
			super( "Redo" );
			setEnabled( false );
		}

		public void actionPerformed( java.awt.event.ActionEvent ev ) {
			try {
				undoManager.redo();
			} catch( javax.swing.undo.CannotRedoException e ) {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Error: unable to redo.", e );
			}
			update();
			undoAction.update();
		}

		protected void update() {
			if( undoManager.canRedo() ) {
				setEnabled( true );
				putValue( javax.swing.Action.NAME, undoManager.getRedoPresentationName() );
			} else {
				setEnabled( false );
				putValue( javax.swing.Action.NAME, "Redo" );
			}
		}
	}
}