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

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;



/**
 * @author Jason Pratt, David Culyba
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class AliceAlertContentPane extends edu.cmu.cs.stage3.swing.ContentPane {
	
	public final static int LESS_DETAIL_MODE = 0;
	public final static int MORE_DETAIL_MODE = 1;
	
	protected java.awt.Dimension smallSize;
	protected java.awt.Dimension largeSize;

	protected int mode = -1;
	protected edu.cmu.cs.stage3.alice.authoringtool.util.ImagePanel errorIconPanel = new edu.cmu.cs.stage3.alice.authoringtool.util.ImagePanel();
	protected edu.cmu.cs.stage3.alice.authoringtool.util.StyledStreamTextPane detailTextPane = new edu.cmu.cs.stage3.alice.authoringtool.util.StyledStreamTextPane();
//		protected edu.cmu.cs.stage3.alice.authoringtool.util.StyledStreamTextPane.StyleStream detailStream;
	protected edu.cmu.cs.stage3.alice.authoringtool.util.StyleStream detailStream;


	public AliceAlertContentPane() {
		init();
	}

	
	public void preDialogShow( javax.swing.JDialog dialog ) {
		super.preDialogShow( dialog );
	}

	
	public void postDialogShow( javax.swing.JDialog dialog ) {
		super.postDialogShow( dialog );
	}	

	
	public String getTitle() {
		return "Alice - Alert";
	}

	
	public void addOKActionListener( java.awt.event.ActionListener l ) {
		cancelButton.addActionListener(l);
	}

	protected void init() {
		jbInit();
		smallSize = new java.awt.Dimension(700, 130);
		largeSize = new java.awt.Dimension(700, 350);
		setMode( LESS_DETAIL_MODE );
		addComponentListener( new java.awt.event.ComponentAdapter() {
			
			public void componentResized( java.awt.event.ComponentEvent e ) {
				if (mode == LESS_DETAIL_MODE){
					smallSize = AliceAlertContentPane.this.getSize();
				} else if (mode == MORE_DETAIL_MODE){
					largeSize = AliceAlertContentPane.this.getSize();
				}
			}
		} );
	}

	/*public void setSubmitBugButtonEnabled( boolean b ) {
		if( b != buttonPanel.isAncestorOf( submitBugButton ) ) {
			if( b ) {
				buttonPanel.add( submitBugButton, new java.awt.GridBagConstraints( 1, 0, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets( 8, 0, 8, 0 ), 0, 0 ) );
			} else {
				buttonPanel.remove( submitBugButton );
			}
		}
	}*/

	public void setMessage( String message ) {
		messageLabel.setText( message );
	}


	
	public java.awt.Dimension getPreferredSize() {
		java.awt.Dimension preferredSize = super.getPreferredSize();
//		if( mode == LESS_DETAIL_MODE) {
//			return smallSize; 
//		} else if ( mode == MORE_DETAIL_MODE){
//			return largeSize;
//		} else{
			return preferredSize;
//		}
	}

	protected void setLessDetail(){
		detailButton.setText( "More Detail >>" );
		this.remove(detailScrollPane);
		this.add(detailPanel, BorderLayout.CENTER);
		
		buttonPanel.removeAll();
		buttonConstraints.gridx = 0;
		//buttonPanel.add(submitBugButton, buttonConstraints);
		buttonConstraints.gridx++;
		buttonPanel.add(cancelButton, buttonConstraints );
		buttonConstraints.gridx++;
		glueConstraints.gridx = buttonConstraints.gridx;
		buttonPanel.add(buttonGlue, glueConstraints);
		buttonConstraints.gridx++;
		buttonPanel.add(detailButton, buttonConstraints);
	}
	protected void setMoreDetail(){
		detailButton.setText( "Less Detail <<" );
		this.remove(detailPanel);
		this.add(detailScrollPane, BorderLayout.CENTER);
		
		buttonPanel.removeAll();
		buttonConstraints.gridx = 0;
		//buttonPanel.add(submitBugButton, buttonConstraints);
		buttonConstraints.gridx++;		
		buttonPanel.add(copyButton, buttonConstraints);
		buttonConstraints.gridx++;
		buttonPanel.add(cancelButton, buttonConstraints );
		buttonConstraints.gridx++;
		glueConstraints.gridx = buttonConstraints.gridx;
		buttonPanel.add(buttonGlue, glueConstraints);
		buttonConstraints.gridx++;	
		buttonPanel.add(detailButton, buttonConstraints);
	}
	
	protected void handleModeSwitch(int mode){
		if( mode == LESS_DETAIL_MODE ) {
			setLessDetail();
		} else if( mode == MORE_DETAIL_MODE ) {
			setMoreDetail();
		} else {
			throw new IllegalArgumentException( "Illegal mode: " + mode );
		}
	}
	
	public void setMode( int mode ) {
		if( this.mode != mode ) {
			this.mode = mode;
			handleModeSwitch(mode);
			packDialog();
		}
	
	}
	

	public void toggleMode() {
		if( mode == LESS_DETAIL_MODE ) {
			setMode( MORE_DETAIL_MODE );
		} else {
			setMode( LESS_DETAIL_MODE );
		}
	}

	protected void writeAliceHeaderToTextPane() {
		detailTextPane.setText( "" );
		detailStream.println( messageLabel.getText() + "\n" );
		detailStream.println();

		detailStream.println( "Alice version: " + edu.cmu.cs.stage3.alice.authoringtool.JAlice.getVersion() );
		//String[] systemProperties = { "os.name", "os.version", "os.arch", "java.vm.name", "java.vm.version", "user.dir" };
		//for( int i = 0; i < systemProperties.length; i++ ) {
		//	detailStream.println( systemProperties[i] + ": " + System.getProperty( systemProperties[i] ) );
		//}
		detailStream.println();
	}

	/*protected void submitBug() {
		String stacktraceID = postStacktrace();
		String urlString = "http://alice.org/bugreport/submit.php";

		try {
//				String version = java.net.URLEncoder.encode( edu.cmu.cs.stage3.alice.authoringtool.JAlice.getVersion(), "UTF-8" );
			String version = java.net.URLEncoder.encode( edu.cmu.cs.stage3.alice.authoringtool.JAlice.getVersion() );
//				String stacktrace = java.net.URLEncoder.encode( detailTextPane.getText(), "UTF-8" );
//				stacktrace = stacktrace.replaceAll( "%0D", "" );
//				edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.openURL( urlString + "?version=" + version + "&stacktrace=" + stacktrace );
//				edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.openURL( urlString + "?version=" + version );
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.openURL( urlString + "?version=" + version + "&pasteStacktrace=" + stacktraceID );
		} catch( Throwable t ) {
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Unable to open bug report web page.", t );
		}
	}*/

	protected String postStacktrace() {
		try {
			java.net.URL url = new java.net.URL( "http://www.alice.org/bugreport/stacktrace.php" );
			java.net.URLConnection connection = url.openConnection();
			connection.setDoInput( true );
			connection.setDoOutput( true );
			connection.setUseCaches( false );
			connection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded" );

			String stacktrace = java.net.URLEncoder.encode( detailTextPane.getText() );
			while( stacktrace.indexOf( "%0D" ) > -1 ) { // delete the carriage returns
				int i = stacktrace.indexOf( "%0D" );
				stacktrace = stacktrace.substring( 0, i ) + stacktrace.substring( i + 3 );
			}
			String content = "stacktrace=" + stacktrace;
			java.io.DataOutputStream output = new java.io.DataOutputStream( new java.io.BufferedOutputStream( connection.getOutputStream() ) );
			output.writeBytes( content );
			output.flush();
			output.close();

			// must open input stream after output stream is closed in order for the data to be posted
			java.io.BufferedReader input = new java.io.BufferedReader( new java.io.InputStreamReader( connection.getInputStream() ) );
			String stacktraceIDString = input.readLine();
			input.close();
			return stacktraceIDString;
		} catch( Throwable t ) {
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Error posting stacktrace to bug database.", t );
			return "0";
		}
	}

	protected void copyDetailText() {
		String detailText = detailTextPane.getText();
		java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents( new java.awt.datatransfer.StringSelection( detailText ), edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack() );
	}


	///////////////
	// Callbacks
	///////////////

	

	void detailButton_actionPerformed(ActionEvent e) {
		toggleMode();
	}

	/*void submitBugButton_actionPerformed(ActionEvent e) {
		submitBug();
	}*/

	void copyButton_actionPerformed(ActionEvent e) {
		copyDetailText();
	}

	/////////////////////
	// Autogenerated
	/////////////////////

	protected BorderLayout borderLayout1 = new BorderLayout();
	protected JPanel buttonPanel = new JPanel();
	protected JPanel messagePanel = new JPanel();
	protected JPanel detailPanel = new JPanel();
	protected JScrollPane detailScrollPane = new JScrollPane();
	protected JTextArea messageLabel = new JTextArea();
	//protected JButton submitBugButton = new JButton();
	protected JButton copyButton = new JButton();
	protected JButton cancelButton = new JButton();
	protected JButton detailButton = new JButton();
	
	protected GridBagConstraints buttonConstraints = new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0 ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0);
	protected GridBagConstraints glueConstraints = new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0
		,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
	protected Component buttonGlue;

	private void jbInit() {
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
this.setLocation(1, 1);

		buttonGlue = Box.createHorizontalGlue();
		
		messageLabel.setEditable(false);
		messageLabel.setLineWrap(true);
		messageLabel.setOpaque(false);
		messageLabel.setPreferredSize(new java.awt.Dimension(402, 1));
		messageLabel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		messageLabel.setText("An unknown error has occurred.");
				
		cancelButton.setText("OK");
		
		detailButton.setText("More Detail >>");
		detailButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				detailButton_actionPerformed(e);
			}
		});
		/*submitBugButton.setText("Submit Bug");
		submitBugButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				submitBugButton_actionPerformed(e);
			}
		});*/
		copyButton.setText("Copy Error to Clipboard");
		copyButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				copyButton_actionPerformed(e);
			}
		});
		
		messagePanel.setLayout(new BorderLayout());
		messagePanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		detailPanel.setLayout(new BorderLayout());
		detailPanel.setBorder(null);
		detailPanel.setPreferredSize(new java.awt.Dimension(492, 0));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(6,0,0,0));
		buttonPanel.setLayout(new GridBagLayout());
		
		
		
		java.net.URL errorImageResources = edu.cmu.cs.stage3.alice.authoringtool.JAlice.class.getResource( "images/errorDialogueIcon.png" );
		errorIconPanel.setImage( java.awt.Toolkit.getDefaultToolkit().createImage( errorImageResources ) );
		messagePanel.add( errorIconPanel, java.awt.BorderLayout.WEST );
		messagePanel.add(messageLabel, BorderLayout.CENTER);
		
		detailTextPane.setEditable( false );
		detailStream = detailTextPane.getNewStyleStream( detailTextPane.stdErrStyle );
		detailScrollPane.setViewportView( detailTextPane );
		detailScrollPane.setPreferredSize(new java.awt.Dimension(492, 300));

		add( messagePanel, BorderLayout.NORTH );
		add(detailPanel, BorderLayout.CENTER);
		add( buttonPanel, BorderLayout.SOUTH );
		
		
	}
}

