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

package edu.cmu.cs.stage3.alice.authoringtool.dialog;

import javax.swing.ScrollPaneConstants;

/**
 * @author Jason Pratt
 */
public class OutputComponent extends javax.swing.JPanel {
	protected javax.swing.JScrollPane scrollPane;
	protected edu.cmu.cs.stage3.alice.authoringtool.util.StyledStreamTextPane textPane;
//	protected edu.cmu.cs.stage3.alice.authoringtool.util.StyledStreamTextPane.StyleStream stdOutStream;
//	protected edu.cmu.cs.stage3.alice.authoringtool.util.StyledStreamTextPane.StyleStream stdErrStream;
	protected edu.cmu.cs.stage3.alice.authoringtool.util.StyleStream stdOutStream;
	protected edu.cmu.cs.stage3.alice.authoringtool.util.StyleStream stdErrStream;

	public OutputComponent() {
		guiInit();
		miscInit();
	}

	private void guiInit() {
		setLayout( new java.awt.BorderLayout() );

		textPane = new edu.cmu.cs.stage3.alice.authoringtool.util.StyledStreamTextPane();
		scrollPane = new javax.swing.JScrollPane( textPane, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED );

		add( scrollPane, java.awt.BorderLayout.CENTER );
	}

	private void miscInit() {
		stdOutStream = textPane.getNewStyleStream( textPane.stdOutStyle );
		stdErrStream = textPane.getNewStyleStream( textPane.stdErrStyle );

		// scroll to the end of the output window when new output arrives
		scrollPane.getVerticalScrollBar().getModel().addChangeListener(
			new javax.swing.event.ChangeListener() {
				private int max = 0;
				private javax.swing.JScrollBar scrollBar = scrollPane.getVerticalScrollBar();

				public void stateChanged( javax.swing.event.ChangeEvent e ) {
					final int newMax = ((javax.swing.BoundedRangeModel)e.getSource()).getMaximum();
					if( newMax != max ) {
						javax.swing.SwingUtilities.invokeLater(
							new Runnable() {
								public void run() {
									scrollBar.setValue( newMax );
									max = newMax;
								}
							}
						);
					}
				}
			}
		);
	}

	public edu.cmu.cs.stage3.alice.authoringtool.util.StyledStreamTextPane getTextPane() {
		return textPane;
	}

	public edu.cmu.cs.stage3.alice.authoringtool.util.StyleStream getStdOutStream() {
		return stdOutStream;
	}

	public edu.cmu.cs.stage3.alice.authoringtool.util.StyleStream getStdErrStream() {
		return stdErrStream;
	}

	public void clear() {
		textPane.setText( "" );
	}
}