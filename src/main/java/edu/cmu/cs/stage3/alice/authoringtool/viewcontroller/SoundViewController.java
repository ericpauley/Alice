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

package edu.cmu.cs.stage3.alice.authoringtool.viewcontroller;

/**
 * @author Jason Pratt, Dennis Cosgrove
 */
public class SoundViewController extends javax.swing.JPanel implements edu.cmu.cs.stage3.alice.authoringtool.util.GUIElement, edu.cmu.cs.stage3.alice.authoringtool.util.Releasable {
	private edu.cmu.cs.stage3.alice.core.Sound m_sound;
	private edu.cmu.cs.stage3.media.Player m_player;
	private SoundPlayStopToggleButton m_soundPlayStopToggleButton = new SoundPlayStopToggleButton();
	private SoundDurationLabel m_soundDurationLabel = new SoundDurationLabel();
	private ElementDnDPanel m_soundDnDPanel;

	private edu.cmu.cs.stage3.media.event.DataSourceListener m_dataSourceListener = new edu.cmu.cs.stage3.media.event.DataSourceListener() {
		public void durationUpdated( edu.cmu.cs.stage3.media.event.DataSourceEvent e ) {
			m_soundDurationLabel.updateComponent();
		}
	};
	private edu.cmu.cs.stage3.media.event.PlayerListener m_playerListener = new edu.cmu.cs.stage3.media.event.PlayerListener() {
		public void stateChanged( edu.cmu.cs.stage3.media.event.PlayerEvent e ) {
			m_soundPlayStopToggleButton.updateComponent();
		}
		public void endReached( edu.cmu.cs.stage3.media.event.PlayerEvent e ) {
		}
	};

	public SoundViewController() {
		setLayout( new java.awt.GridBagLayout() );
		add( m_soundPlayStopToggleButton, new java.awt.GridBagConstraints( 1, 0, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.NONE, new java.awt.Insets( 0, 4, 0, 0 ), 0, 0 ) );
		add( m_soundDurationLabel, new java.awt.GridBagConstraints( 2, 0, 1, 1, 1.0, 0.0, java.awt.GridBagConstraints.EAST, java.awt.GridBagConstraints.NONE, new java.awt.Insets( 0, 4, 0, 4 ), 0, 0 ) );
		setOpaque( false );
	}

	public void setSound( edu.cmu.cs.stage3.alice.core.Sound sound ) {
		if( m_sound != null ) {
			edu.cmu.cs.stage3.media.DataSource dataSourceValue = sound.dataSource.getDataSourceValue();
			if( dataSourceValue != null ) {
				dataSourceValue.removeDataSourceListener( m_dataSourceListener );
			}
			if( m_player != null ) {
				m_player.stop();
				m_player.removePlayerListener( m_playerListener );
				m_player = null;
			}
		}

		m_sound = sound;
		if( sound != null ) {
			m_soundDnDPanel = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getElementDnDPanel( sound );
			add( m_soundDnDPanel, new java.awt.GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.NONE, new java.awt.Insets( 0, 0, 0, 0 ), 0, 0 ) );
			
			m_soundDurationLabel.updateComponent();

			edu.cmu.cs.stage3.media.DataSource dataSourceValue = sound.dataSource.getDataSourceValue();
			if( dataSourceValue != null ) {
				dataSourceValue.addDataSourceListener( m_dataSourceListener );
				dataSourceValue.waitForRealizedPlayerCount( 1, 0 );
			}
		}
	}

	protected void startListening() {
		//TODO?
	}

	protected void stopListening() {
		//TODO?
	}

	public void goToSleep() {
		stopListening();
		if( m_soundDnDPanel != null ) {
			m_soundDnDPanel.goToSleep();
		}
	}

	public void wakeUp() {
		startListening();
		if( m_soundDnDPanel != null ) {
			m_soundDnDPanel.wakeUp();
		}
	}

	public void clean() {
		stopSound();
		stopListening();
		if( m_soundDnDPanel != null ) {
			remove( m_soundDnDPanel );
			m_soundDnDPanel = null;
		}
	}

	public void die() {
		clean();
	}

	public void release() {
		edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.releaseGUI( this );
		if( m_player != null ) {
			m_player.setIsAvailable( true );
			m_player = null;
		}
	}

	public double getSoundDuration() {
		double t = Double.NaN;
		edu.cmu.cs.stage3.media.DataSource dataSourceValue = m_sound.dataSource.getDataSourceValue();
		if( dataSourceValue != null ) {
			t = dataSourceValue.getDuration( edu.cmu.cs.stage3.media.DataSource.USE_HINT_IF_NECESSARY );
		}
		return t;
	}
	public void playSound() {
		if( m_player == null ) {
			if( m_sound != null ) {
				edu.cmu.cs.stage3.media.DataSource dataSourceValue = m_sound.dataSource.getDataSourceValue();
				if( dataSourceValue != null ) {
					m_player = dataSourceValue.acquirePlayer();
					m_player.addPlayerListener( m_playerListener );
				}
			}
		}
		if( m_player != null ) {
			m_player.startFromBeginning();
		}
	}

	public void stopSound() {
		if( m_player != null ) {
			m_player.stop();
		}
	}

	public int getSoundState() {
		if( m_player != null ) {
			return m_player.getState();
		} else {
			return 0;
		}
	}

	protected class SoundPlayStopToggleButton extends javax.swing.JButton {
		protected javax.swing.ImageIcon playIcon;
		protected javax.swing.ImageIcon stopIcon;
		protected java.awt.event.ActionListener buttonListener = new java.awt.event.ActionListener() {
			public void actionPerformed( java.awt.event.ActionEvent ev ) {
				if( SoundViewController.this.m_sound != null ) {
					if( SoundViewController.this.getSoundState() == edu.cmu.cs.stage3.media.Player.STATE_STARTED ) {
						SoundViewController.this.stopSound();
					} else {
						SoundViewController.this.playSound();
					}
				}
			}
		};

		public SoundPlayStopToggleButton() {
			playIcon = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getIconForValue( "playSound" );
			stopIcon = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getIconForValue( "stopSound" );
			addActionListener( buttonListener );
			setOpaque( false );
			setBorder( null );
			setContentAreaFilled( false );
			setFocusPainted( false );
			setIcon( playIcon );
		}

		public void updateComponent() {
			if( SoundViewController.this.getSoundState() == edu.cmu.cs.stage3.media.Player.STATE_STARTED ) {
				this.setIcon( stopIcon );
			} else {
				this.setIcon( playIcon );
			}
		}
	}

	protected class SoundDurationLabel extends javax.swing.JLabel {
		public void updateComponent() {
			setText( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.formatTime( SoundViewController.this.getSoundDuration() ) );
		}
	}
}