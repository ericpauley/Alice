/*
 * Copyright (c) 1999-2003, Carnegie Mellon University. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * yes
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

package edu.cmu.cs.stage3.alice.authoringtool.dialog;

import java.awt.BorderLayout;

/**
 * @author Jason Pratt, David Culyba
 */

public class StdErrOutContentPane extends edu.cmu.cs.stage3.alice.authoringtool.dialog.AliceAlertContentPane {
	public final static int HISTORY_MODE = 2;

	protected edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;
	protected OutputComponent errOutputComponent;
	protected OutputComponent stdOutputComponent;

	protected String lastError;
	protected String titleString;

	protected boolean isShowing = false;
	protected boolean errorContentAdded = false;
	protected boolean textContentAdded = false;
	protected boolean shouldListenToErrors = true;
	protected boolean shouldListenToPrint = true;

	protected class ErrOutputDocumentListener implements javax.swing.event.DocumentListener {
		@Override
		public void insertUpdate(final javax.swing.event.DocumentEvent ev) {
			try {
				lastError = ev.getDocument().getText(ev.getOffset(), ev.getLength());
				if (lastError.startsWith("  Unable to handle format") == true) {
					lastError = "\n\nYour sound file cannot be played in Alice.\n" + "Please find an audio editor to convert the file to one with a PCM encoding.\n" + "See the tutorial on converting sound files at our Alice website.\n" + "Right click to clear the messages here.\n\n" + lastError;
				}
				detailTextPane.getDocument().insertString(detailTextPane.getDocument().getLength(), lastError, detailTextPane.stdErrStyle);
			} catch (Exception e) {}
			errorContentAdded = true;
			update();
		}
		@Override
		public void removeUpdate(javax.swing.event.DocumentEvent ev) {
			update();
		}
		@Override
		public void changedUpdate(javax.swing.event.DocumentEvent ev) {
			update();
		}

		private void update() {
			if (shouldListenToErrors) {
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						if (!isShowing) {
							isShowing = true;
							int result = edu.cmu.cs.stage3.swing.DialogManager.showDialog(StdErrOutContentPane.this);
						}
					}
				});
			}

		}
	}

	protected class StdOutputDocumentListener implements javax.swing.event.DocumentListener {
		@Override
		public void insertUpdate(final javax.swing.event.DocumentEvent ev) {
			try {
				lastError = ev.getDocument().getText(ev.getOffset(), ev.getLength());
				detailTextPane.getDocument().insertString(detailTextPane.getDocument().getLength(), lastError, detailTextPane.stdOutStyle);
			} catch (Exception e) {}
			textContentAdded = true;
			update();
		}
		@Override
		public void removeUpdate(javax.swing.event.DocumentEvent ev) {
			update();
		}
		@Override
		public void changedUpdate(javax.swing.event.DocumentEvent ev) {
			update();
		}

		private void update() {
			if (shouldListenToPrint) {
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						if (!isShowing) {
							isShowing = true;
							int result = edu.cmu.cs.stage3.swing.DialogManager.showDialog(StdErrOutContentPane.this);
						}
					}
				});
			}

		}
	}

	public StdErrOutContentPane(edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool) {
		super();
		titleString = "Alice - Error Console";
		this.authoringTool = authoringTool;
		errOutputComponent = authoringTool.getStdErrOutputComponent();
		stdOutputComponent = authoringTool.getStdOutOutputComponent();
		writeGenericAliceHeaderToTextPane();
		errOutputComponent.getTextPane().getDocument().addDocumentListener(new ErrOutputDocumentListener());
		stdOutputComponent.getTextPane().getDocument().addDocumentListener(new StdOutputDocumentListener());
	}

	protected void writeGenericAliceHeaderToTextPane() {
		detailTextPane.setText("");
		detailStream.println("Alice version: " + edu.cmu.cs.stage3.alice.authoringtool.JAlice.getVersion());
		// String[] systemProperties = { "os.name", "os.version", "os.arch",
		// "java.vm.name", "java.vm.version", "user.dir" };
		// for( int i = 0; i < systemProperties.length; i++ ) {
		// detailStream.println( systemProperties[i] + ": " +
		// System.getProperty( systemProperties[i] ) );
		// }
		detailStream.println();
	}

	@Override
	public void preDialogShow(javax.swing.JDialog parentDialog) {
		super.preDialogShow(parentDialog);
	}

	public void stopReactingToPrint() {
		shouldListenToPrint = false;
	}

	public void startReactingToPrint() {
		stdOutputComponent.stdErrStream.flush();
		stdOutputComponent.stdOutStream.flush();
		shouldListenToPrint = true;
	}

	public void stopReactingToError() {
		shouldListenToErrors = false;
	}

	public void startReactingToError() {
		errOutputComponent.stdErrStream.flush();
		errOutputComponent.stdOutStream.flush();
		shouldListenToErrors = true;
	}

	@Override
	public void postDialogShow(javax.swing.JDialog parentDialog) {
		isShowing = false;
		setMode(LESS_DETAIL_MODE);
		super.postDialogShow(parentDialog);
	}

	public int showStdErrOutDialog() {
		if (!isShowing) {
			isShowing = true;
			return edu.cmu.cs.stage3.swing.DialogManager.showDialog(this);
		} else {
			return -1;
		}
	}

	@Override
	public String getTitle() {
		return titleString;
	}

	protected void setHistoryDetail() {
		// detailPanel.add(detailScrollPane, BorderLayout.CENTER);
		this.add(detailScrollPane, BorderLayout.CENTER);
		buttonPanel.removeAll();
		buttonConstraints.gridx = 0;
		buttonPanel.add(cancelButton, buttonConstraints);
		// buttonConstraints.gridx++;
		// buttonPanel.add(submitBugButton, buttonConstraints);
		buttonConstraints.gridx++;
		buttonPanel.add(copyButton, buttonConstraints);
		buttonConstraints.gridx++;
		buttonPanel.add(cancelButton, buttonConstraints);
		buttonConstraints.gridx++;
		glueConstraints.gridx = buttonConstraints.gridx;
		buttonPanel.add(buttonGlue, glueConstraints);

		if (errorContentAdded) {
			messageLabel.setText("Something bad has occurred.");
		} else if (textContentAdded) {
			messageLabel.setText("Nothing bad has occurred.");
		} else {
			messageLabel.setText("Nothing bad has occurred.");
		}
	}

	@Override
	protected void setLessDetail() {
		super.setLessDetail();
		messageLabel.setText("An unknown error has occurred.");
	}

	@Override
	protected void setMoreDetail() {
		super.setMoreDetail();
		messageLabel.setText("An unknown error has occurred.");
	}

	@Override
	protected void handleModeSwitch(int mode) {
		if (mode == LESS_DETAIL_MODE) {
			setLessDetail();
		} else if (mode == MORE_DETAIL_MODE) {
			setMoreDetail();
		} else if (mode == HISTORY_MODE) {
			setHistoryDetail();
		} else {
			throw new IllegalArgumentException("Illegal mode: " + mode);
		}
		packDialog();
	}

}