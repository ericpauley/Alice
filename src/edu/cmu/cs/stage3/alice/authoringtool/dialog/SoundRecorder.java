package edu.cmu.cs.stage3.alice.authoringtool.dialog;

import javax.media.Controller;
import javax.media.Format;

/**
 * @author Ben Buchwald, Dennis Cosgrove
 */
public class SoundRecorder extends edu.cmu.cs.stage3.swing.ContentPane {
	private static byte[] s_wavHeader = new byte[ 44 ];
	static {
		System.arraycopy( "RIFF????WAVEfmt ".getBytes(), 0, s_wavHeader, 0, 16 );
		s_wavHeader[16] = 0x10;
		s_wavHeader[17] = 0;
		s_wavHeader[18] = 0;
		s_wavHeader[19] = 0;
		s_wavHeader[20] = 1;
		s_wavHeader[21] = 0;
		s_wavHeader[22] = 2;
		s_wavHeader[23] = 0;
		s_wavHeader[24] = (byte) 0x44; //(44100 & 0xFF);
		s_wavHeader[25] = (byte) 0xAC; //(44100 & 0xFF00)>>8;
		s_wavHeader[26] = 0;
		s_wavHeader[27] = 0;
		s_wavHeader[28] = (byte) 0x10; //((44100*4) & 0xFF);
		s_wavHeader[29] = (byte) 0xB1; //((44100*4) & 0xFF00)>>8;
		s_wavHeader[30] = (byte) 0x2; //((44100*4) & 0xFF0000)>>16;
		s_wavHeader[31] = 0;
		s_wavHeader[32] = 4;
		s_wavHeader[33] = 0;
		s_wavHeader[34] = 16;
		s_wavHeader[35] = 0;
		System.arraycopy( "data????".getBytes(), 0, s_wavHeader, 36, 8 );
	}

	private static final int IDLE = 0;
	private static final int RECORDING = 1;
	private static final int PLAYING = 2;

	private int m_state = IDLE;

	private edu.cmu.cs.stage3.alice.core.Element m_parentToCheckForNameValidity;

	private edu.cmu.cs.stage3.alice.core.Sound m_sound;

	private javax.swing.JTextField m_nameTextField;
	private javax.swing.JLabel m_durationLabel;
	private javax.swing.JButton m_recordButton;
	private javax.swing.JButton m_playButton;
	private javax.swing.JButton m_okButton;
	private javax.swing.JButton m_cancelButton;

	private javax.media.protocol.DataSource m_jmfDataSource;
	private javax.media.Processor m_jmfProcessor;
	private edu.cmu.cs.stage3.alice.authoringtool.util.CaptureRenderer m_jmfRenderer;

	private edu.cmu.cs.stage3.media.DataSource m_dataSource;
	private edu.cmu.cs.stage3.media.Player m_player;

    private javax.swing.Timer m_durationUpdateTimer = new javax.swing.Timer( 100, new java.awt.event.ActionListener(){
        public void actionPerformed( java.awt.event.ActionEvent e ) {
        	SoundRecorder.this.onDurationUpdate();
        }
    } );
	private long m_durationT0;

    public SoundRecorder() {
		m_durationLabel = new javax.swing.JLabel();
		onDurationUpdate();

		m_recordButton = new javax.swing.JButton( "Record" );
		m_recordButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				onRecord();
			}
		});

		m_playButton = new javax.swing.JButton( "Play" );
		m_playButton.setEnabled(false);
		m_playButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				onPlay();
			}
		});

		m_nameTextField = new javax.swing.JTextField();
		m_nameTextField.setText( "unnamedSound" );
		m_nameTextField.getDocument().addDocumentListener(
			new javax.swing.event.DocumentListener() {
				public void changedUpdate( javax.swing.event.DocumentEvent e ) { SoundRecorder.this.checkNameForValidity(); }
				public void insertUpdate( javax.swing.event.DocumentEvent e ) { SoundRecorder.this.checkNameForValidity(); }
				public void removeUpdate( javax.swing.event.DocumentEvent e ) { SoundRecorder.this.checkNameForValidity(); }
			}
		);

		m_okButton = new javax.swing.JButton( "OK" );
		m_okButton.setEnabled( false );
		m_okButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				onOK();
			}
		});

		m_cancelButton = new javax.swing.JButton( "Cancel" );
		m_cancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				onCancel();
			}
		});

		checkNameForValidity();

		java.awt.GridBagConstraints gbc;

		javax.swing.JPanel namePanel = new javax.swing.JPanel();
		namePanel.setLayout( new java.awt.GridBagLayout() );
		gbc = new java.awt.GridBagConstraints();
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = java.awt.GridBagConstraints.RELATIVE;
		namePanel.add( new javax.swing.JLabel( "Name: " ), gbc );
		gbc.weightx = 1.0;
		gbc.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		namePanel.add( m_nameTextField, gbc );
		

		javax.swing.JPanel controlPanel = new javax.swing.JPanel();
		controlPanel.setLayout( new java.awt.GridBagLayout() );
		gbc = new java.awt.GridBagConstraints();
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = 1;
		gbc.weightx = 1.0;
		controlPanel.add( m_recordButton, gbc );
		controlPanel.add( m_playButton, gbc );


		javax.swing.JPanel okCancelPanel = new javax.swing.JPanel();
		okCancelPanel.setLayout( new java.awt.GridBagLayout() );
		gbc = new java.awt.GridBagConstraints();
		gbc.weightx = 1.0;
		okCancelPanel.add( new javax.swing.JLabel(), gbc );
		gbc.weightx = 0.0;
		okCancelPanel.add( m_okButton, gbc );
		okCancelPanel.add( m_cancelButton, gbc );

		final int opad = 16;
		setLayout( new java.awt.GridBagLayout() );

		gbc = new java.awt.GridBagConstraints();
		gbc.anchor = java.awt.GridBagConstraints.NORTHWEST;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets.top = opad;
		gbc.insets.left = opad;
		gbc.insets.right = opad;
		gbc.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;

		add( namePanel, gbc );

		gbc.insets.top = 0;

		javax.swing.JLabel spacer1 = new javax.swing.JLabel();
		spacer1.setPreferredSize( new java.awt.Dimension( 480, 16 ) );
		gbc.weighty = 1.0;
		add( spacer1, gbc );
		gbc.weighty = 0.0;

		gbc.anchor = java.awt.GridBagConstraints.WEST;
		add( m_durationLabel, gbc );
		add( controlPanel, gbc );

		javax.swing.JLabel spacer2 = new javax.swing.JLabel();
		spacer2.setPreferredSize( new java.awt.Dimension( 480, 16 ) );
		gbc.weighty = 1.0;
		add( spacer2, gbc );
		gbc.weighty = 0.0;

		gbc.insets.bottom = opad;

		gbc.anchor = java.awt.GridBagConstraints.SOUTHEAST;
		add( okCancelPanel, gbc );
    }
    
	
	public void handleDispose() {
		onCancel();
		super.handleDispose();
	}
	
	public void addOKActionListener( java.awt.event.ActionListener l ) {
		m_okButton.addActionListener( l );
	}
	
	public void removeOKActionListener( java.awt.event.ActionListener l ) {
		m_okButton.removeActionListener( l );
	}
	
	public void addCancelActionListener( java.awt.event.ActionListener l ) {
		m_cancelButton.addActionListener( l );
	}
	
	public void removeCancelActionListener( java.awt.event.ActionListener l ) {
		m_cancelButton.removeActionListener( l );
	}

	public edu.cmu.cs.stage3.alice.core.Element getParentToCheckForNameValidity() {
		return m_parentToCheckForNameValidity;
	}
	public void setParentToCheckForNameValidity( edu.cmu.cs.stage3.alice.core.Element parentToCheckForNameValidity ) {
		m_parentToCheckForNameValidity = parentToCheckForNameValidity;
		m_nameTextField.setText( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getNameForNewChild( "unnamedSound", m_parentToCheckForNameValidity ) );
	}

	public edu.cmu.cs.stage3.alice.core.Sound getSound() {
		return m_sound;
	}
	public void setSound( edu.cmu.cs.stage3.alice.core.Sound sound ) {
		m_sound = sound;
	}
		
	private void checkNameForValidity() {
		java.awt.Color color = java.awt.Color.black;
		if( m_parentToCheckForNameValidity != null ) {
			if( m_parentToCheckForNameValidity.getChildNamedIgnoreCase( m_nameTextField.getText() ) != null ) {
				color = java.awt.Color.red;
			}
		}
		m_nameTextField.setForeground( color );
		updateOKButtonEnabled();
	}

	private void updateOKButtonEnabled() {
		m_okButton.setEnabled( m_dataSource != null && m_nameTextField.getForeground().equals( java.awt.Color.black ) );
	}

	private void onDurationUpdate() {
		double t = 0;
		switch( m_state ) {
		case RECORDING:
			long dt = System.currentTimeMillis() - m_durationT0;
			t = dt*0.001;
			break;
		case PLAYING:
			if( m_player != null ) {
				t = m_player.getDuration();
			}
			break;
		}
		m_durationLabel.setText( "Duration: " + edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.formatTime( t ) );
	}

	private void onStop() {
		m_state = IDLE;
		m_playButton.setText( "Play" );
		m_playButton.setEnabled( true );
		m_recordButton.setText( "Record" );
		m_recordButton.setEnabled( true );
		if( m_jmfProcessor != null ) {
			m_jmfProcessor.stop();
			m_jmfProcessor = null;
		}
		if( m_jmfRenderer != null ) {
			m_jmfRenderer.stop();
			m_jmfRenderer.close();
			m_jmfRenderer = null;
		}
		if( m_jmfDataSource != null ) {
			try {
				m_jmfDataSource.stop();
			} catch( java.io.IOException ioe ) {
				ioe.printStackTrace();
			}
			m_jmfDataSource.disconnect();
			m_jmfDataSource = null;
		}
		if( m_player != null ) {
			m_player.stop();
			m_player.setIsAvailable( true );
		}
		m_durationUpdateTimer.stop();
	}
	
//	public void release() {
//		onStop();
//		m_player = null;
//	}
	
    private void onRecord() {
		if( m_state == RECORDING ) {
			m_jmfProcessor.stop();
			m_jmfRenderer.stop();
			m_jmfRenderer.close();
			
			int dataLength = m_jmfRenderer.getDataLength();
			byte[] data = new byte[ s_wavHeader.length + dataLength ];

			byte[] dataLengthInBytes = new byte[ 4 ];
			dataLengthInBytes[0] = (byte)((dataLength & 0x000000FF));
			dataLengthInBytes[1] = (byte)((dataLength & 0x0000FF00)>>8);
			dataLengthInBytes[2] = (byte)((dataLength & 0x00FF0000)>>16);
			dataLengthInBytes[3] = (byte)((dataLength & 0xFF000000)>>24);
		
			System.arraycopy( s_wavHeader, 0, data, 0, s_wavHeader.length );	
			System.arraycopy( dataLengthInBytes, 0, data, 4, dataLengthInBytes.length );	
			System.arraycopy( dataLengthInBytes, 0, data, 40, dataLengthInBytes.length );

			m_jmfRenderer.getData( data, s_wavHeader.length, dataLength );
			
			m_dataSource = edu.cmu.cs.stage3.media.Manager.createDataSource( data, "wav" );
			m_dataSource.waitForRealizedPlayerCount( 1, 0 );
			onStop();
			updateOKButtonEnabled();
		} else {
			m_state = RECORDING;
			m_recordButton.setText( "Stop" );
			m_playButton.setEnabled( false );
			if( m_jmfDataSource == null ) {			
				javax.media.format.AudioFormat format = new javax.media.format.AudioFormat( javax.media.format.AudioFormat.LINEAR, Format.NOT_SPECIFIED, 16, 1 );
				java.util.Vector captureDeviceList = javax.media.CaptureDeviceManager.getDeviceList( format );
				if( captureDeviceList.size() > 0 ) {
					javax.media.CaptureDeviceInfo captureDevice = (javax.media.CaptureDeviceInfo)captureDeviceList.firstElement();
					javax.media.MediaLocator locator = captureDevice.getLocator();
					try {
						m_jmfDataSource = javax.media.Manager.createDataSource( locator );
						m_jmfProcessor = javax.media.Manager.createProcessor( m_jmfDataSource );

						final Object configureLock = new Object();
						synchronized( configureLock ) {
							javax.media.ControllerListener configureControllerListener = new javax.media.ControllerListener() {
								public void controllerUpdate( javax.media.ControllerEvent e ) {
									if( e instanceof javax.media.TransitionEvent ) {
										javax.media.TransitionEvent te = (javax.media.TransitionEvent)e;
										if( te.getCurrentState() == javax.media.Processor.Configured ) {
											synchronized( configureLock ) {
												configureLock.notify();
											}
										}
									}
								}
							};
							m_jmfProcessor.addControllerListener( configureControllerListener );

							new Thread() {
								
								public void run() {
									try {
										sleep( 1000 );
									} catch( InterruptedException ie ) {
										ie.printStackTrace();
									} finally {
										synchronized( configureLock ) {
											configureLock.notify();
										}
									}
								}
							}.start();

							m_jmfProcessor.configure();
							try {
								configureLock.wait();
							} catch( InterruptedException ie ) {
								ie.printStackTrace();
							}
							m_jmfProcessor.removeControllerListener( configureControllerListener );
						}
						m_jmfProcessor.setContentDescriptor( null );

						m_jmfRenderer = new edu.cmu.cs.stage3.alice.authoringtool.util.CaptureRenderer();
						m_jmfProcessor.getTrackControls()[0].setRenderer( m_jmfRenderer );

						m_jmfProcessor.realize();
						final Object realizeLock = new Object();
						synchronized( realizeLock ) {
							javax.media.ControllerListener realizeControllerListener = new javax.media.ControllerListener() {
								public void controllerUpdate( javax.media.ControllerEvent e ) {
									if( e instanceof javax.media.TransitionEvent ) {
										javax.media.TransitionEvent te = (javax.media.TransitionEvent)e;
										if( te.getCurrentState() == Controller.Realized ) {
											synchronized( realizeLock ) {
												realizeLock.notify();
											}
										}
									}
								}
							};
							m_jmfProcessor.addControllerListener( realizeControllerListener );

							new Thread() {
								
								public void run() {
									try {
										sleep( 1000 );
									} catch( InterruptedException ie ) {
										ie.printStackTrace();
									} finally {
										synchronized( realizeLock ) {
											realizeLock.notify();
										}
									}
								}
							}.start();

							m_jmfProcessor.realize();
							try {
								realizeLock.wait();
							} catch( InterruptedException ie ) {
								ie.printStackTrace();
							}
							m_jmfProcessor.removeControllerListener( realizeControllerListener );
						}
						m_jmfProcessor.start();
						m_durationT0 = System.currentTimeMillis();
						m_durationUpdateTimer.start();
					} catch( javax.media.UnsupportedPlugInException upie ) {
						upie.printStackTrace();
					} catch( javax.media.NoProcessorException npe ) {
						npe.printStackTrace();
					} catch( javax.media.NoDataSourceException ndse ) {
						ndse.printStackTrace();
					} catch( java.io.IOException ioe ) {
						ioe.printStackTrace();
					}
				}
			}
		}
    }

    private void onPlay() {
		if( m_state == PLAYING ) {
			onStop();
			m_player.setIsAvailable( true );
		} else {
			m_state = PLAYING;
			m_playButton.setText( "Stop" );
			m_recordButton.setEnabled( false );
			m_player = m_dataSource.acquirePlayer();
			m_player.addPlayerListener( new edu.cmu.cs.stage3.media.event.PlayerListener() {
				public void endReached( edu.cmu.cs.stage3.media.event.PlayerEvent e ) {
					if( m_state == PLAYING ) {
						onPlay();
					}
				}
				public void stateChanged( edu.cmu.cs.stage3.media.event.PlayerEvent e ) {
				}
			} );
			m_player.startFromBeginning();
		}
    }

	private void onCancel() {
		onStop();
		setSound( null );
    }

	private void onOK() {
		onStop();
		edu.cmu.cs.stage3.alice.core.Sound sound = new edu.cmu.cs.stage3.alice.core.Sound();
		sound.name.set( m_nameTextField.getText() );
		sound.dataSource.set( m_dataSource );
		setSound( sound );
    }
}