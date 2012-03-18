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
public class ExpandablePanel extends javax.swing.JPanel {
	protected static java.util.HashSet collapsedSet = new java.util.HashSet();

	protected javax.swing.JPanel topPanel = new javax.swing.JPanel();
	protected javax.swing.JPanel mainPanel = new javax.swing.JPanel();
	protected javax.swing.JToggleButton expandButton = new javax.swing.JToggleButton();
	protected javax.swing.JLabel titleLabel = new javax.swing.JLabel();
	protected javax.swing.ImageIcon plusIcon;
	protected javax.swing.ImageIcon minusIcon;
	protected javax.swing.ImageIcon squareIcon;
	protected ExpandButtonListener expandButtonListener = new ExpandButtonListener();

	public ExpandablePanel() {
		guiInit();
	}

	private void guiInit() {
		setLayout( new java.awt.BorderLayout() );
		add( topPanel, java.awt.BorderLayout.NORTH );
		add( mainPanel, java.awt.BorderLayout.CENTER );
		setOpaque( false );
		
		topPanel.setLayout( new java.awt.BorderLayout() );
		topPanel.add( expandButton, java.awt.BorderLayout.WEST );
		topPanel.add( titleLabel, java.awt.BorderLayout.CENTER );
		titleLabel.setBorder( javax.swing.BorderFactory.createEmptyBorder( 0, 10, 0, 0 ) );
		topPanel.setOpaque( false );

		mainPanel.setLayout( new java.awt.BorderLayout() );
		mainPanel.setBorder( javax.swing.BorderFactory.createEmptyBorder( 0, 20, 0, 0 ) );
		mainPanel.setOpaque( false );

		plusIcon = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getIconForValue( "plus" );
		minusIcon = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getIconForValue( "minus" );
		squareIcon = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getIconForValue( "square" );
		expandButton.setBorder( javax.swing.BorderFactory.createEmptyBorder( 0, 3, 0, 0 ) );
		expandButton.setOpaque( false );
		expandButton.setIcon( plusIcon );
		expandButton.setSelectedIcon( minusIcon );
		expandButton.setPressedIcon( squareIcon );
		expandButton.setSelected( true );
		expandButton.setFocusPainted( false );
		expandButton.setContentAreaFilled( false );
		expandButton.addChangeListener( expandButtonListener );
		expandButton.setBorderPainted(false);
		titleLabel.setOpaque( false );
	}

	public void setTitle( String title ) {
		titleLabel.setText( title );
		if( collapsedSet.contains( title ) ) {
			expandButton.setSelected( false );
		}
	}

	public String getTitle() {
		return titleLabel.getText();
	}

	public void setContent( javax.swing.JComponent component ) {
		mainPanel.removeAll();
		mainPanel.add( component, java.awt.BorderLayout.CENTER );
	}

	public void setExpanded( boolean b ) {
		if( b ) {
			if( ! this.isAncestorOf( mainPanel ) ) {
				add( mainPanel, java.awt.BorderLayout.CENTER );
				collapsedSet.remove( titleLabel.getText() );
				if( ! expandButton.isSelected() ) {
					expandButton.setSelected( true );
				}
			}
		} else {
			if( this.isAncestorOf( mainPanel ) ) {
				remove( mainPanel );
				collapsedSet.add( titleLabel.getText() );
				if( expandButton.isSelected() ) {
					expandButton.setSelected( false );
				}
			}
		}
		revalidate();
		repaint();
	}

	protected class ExpandButtonListener implements javax.swing.event.ChangeListener {
		public void stateChanged( javax.swing.event.ChangeEvent ev ) {
			ExpandablePanel.this.setExpanded( ExpandablePanel.this.expandButton.isSelected() );
		}
	}
}