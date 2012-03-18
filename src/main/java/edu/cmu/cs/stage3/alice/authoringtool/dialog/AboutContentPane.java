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

public class AboutContentPane extends edu.cmu.cs.stage3.swing.ContentPane {
	private javax.swing.JButton m_okButton = new javax.swing.JButton( "OK" );
	
	public AboutContentPane()	{
		javax.swing.JLabel imageLabel = new javax.swing.JLabel();
		imageLabel.setIcon( new javax.swing.ImageIcon( edu.cmu.cs.stage3.alice.authoringtool.JAlice.class.getResource( "images/aboutAlice.png" ) ) );

		setBackground(new java.awt.Color(173,202,234));
		setPreferredSize( new java.awt.Dimension(520, 410) );
		
		setLayout( new java.awt.GridBagLayout() );
		java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		add( imageLabel, gbc );
		add( m_okButton, gbc );

//		javax.swing.JLabel messageLabel = new javax.swing.JLabel();
//		//		String messageText = "<html><b>" +
//		//					   "<h3>Alice 2, version: " + JAlice.getVersion() + "</h3>" +
//		//					   "<h2>For more information about Alice, visit us on the web: </h2>" +
//		//					   "<h1>www.alice.org</h1>"+
//		//					   "<h4>Alice is made freely available and open source as a public service.<br>"+
//		//					   "Alice v2.0 1999-2003, Carnegie Mellon University. All rights reserved.<br>"+
//		//					   "We gratefully acknowledge the University of Virginia, where the Alice project originated.<br>"+
//		//					   "We gratefully acknowledge the financial support of DARPA, Intel, Microsoft, NSF, and ONR.</h4>"+
//		//					   "</b></html>";
//		//		add(messageLabel,   new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0
//		//			,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(4, 16, 4, 16), 0, 0));
	}
	
	
	public String getTitle() {
		return "About Alice verison "+edu.cmu.cs.stage3.alice.authoringtool.JAlice.getVersion();
	}

//	public void preDialogShow() {
////		edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().showErrorDialog("Testing", new Throwable("eh?"));
//	}
//	
//	public void postDialogShow() {
////		System.out.println("HOOHA");
////		Thread.dumpStack();
//	}

	
	public void addOKActionListener( java.awt.event.ActionListener l ) {
		m_okButton.addActionListener(l);
	}
	
	public void removeOKActionListener( java.awt.event.ActionListener l ) {
		m_okButton.removeActionListener(l);
	}
}