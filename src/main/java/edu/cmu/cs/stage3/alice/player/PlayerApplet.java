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

package edu.cmu.cs.stage3.alice.player;

import java.awt.Color;

import javax.vecmath.Matrix4d;

public class PlayerApplet extends java.applet.Applet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private AbstractPlayer m_player = new AbstractPlayer() {
		
		protected boolean isPreserveAndRestoreRequired() {
			return true;
		}
		
		protected void handleRenderTarget(edu.cmu.cs.stage3.alice.core.RenderTarget renderTarget) {
			PlayerApplet.this.handleRenderTarget( renderTarget );
		}
	};
	
	private javax.swing.JButton m_pauseButton = new javax.swing.JButton( "pause" );
	private javax.swing.JButton m_resumeButton = new javax.swing.JButton( "resume" );
	private javax.swing.JButton m_startButton = new javax.swing.JButton( "restart" );
	private javax.swing.JButton m_stopButton = new javax.swing.JButton( "stop" );
	
	private java.awt.Color decodeColorParam( String name, java.awt.Color valueIfNull ) {
		//String value = getParameter( name );
		//if( value != null ) {
		//	return java.awt.Color.decode( value );
		//} else {
			return valueIfNull;
		//}
	}

	private class ProgressPanel extends java.awt.Panel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private String m_worldName = getParameter( "world" );

		private javax.swing.JProgressBar m_downloadProgressBar;
		private javax.swing.JProgressBar m_extractProgressBar;

		public ProgressPanel() {
			setLayout( null );
			setBackground( decodeColorParam( "boxfgcolor", java.awt.Color.black ) );
			setBackground( decodeColorParam( "boxbgcolor", java.awt.Color.white ) );
			@SuppressWarnings("unused")
			Color progressColor = decodeColorParam( "progresscolor", java.awt.Color.blue );

			String s = getParameter( "WIDTH" );
			int width;
			if( s != null ) {
				width = Integer.parseInt( s );
			} else {
				width = 320;
			}

			m_downloadProgressBar = new javax.swing.JProgressBar();
			m_downloadProgressBar.setLocation( 40, 80 );
			m_downloadProgressBar.setSize( width-80, 32 );
			m_downloadProgressBar.setString( "downloading..." );
			m_downloadProgressBar.setStringPainted( true );
			add( m_downloadProgressBar );

			m_extractProgressBar = new javax.swing.JProgressBar();
			m_extractProgressBar.setLocation( 40, 120 );
			m_extractProgressBar.setSize( width-80, 32 );
			m_extractProgressBar.setString( "extracting..." );
			m_extractProgressBar.setStringPainted( true );
			add( m_extractProgressBar );
		}

		public void setDownloadCurrent( int downloadCurrent ) {
			m_downloadProgressBar.setValue( downloadCurrent );
		}
		public void setDownloadTotal( int downloadTotal ) {
			m_downloadProgressBar.setMaximum( downloadTotal );
		}

		public void setExtractCurrent( int extractCurrent ) {
			m_extractProgressBar.setValue( extractCurrent );
		}
		public void setExtractTotal( int extractTotal ) {
			m_extractProgressBar.setMaximum( extractTotal );
		}

		
		public void paint( java.awt.Graphics g ) {
			super.paint( g );
			g.drawString( "Alice world: " + m_worldName, 20, 40 );
		}
	}
	private ProgressPanel m_progressPanel;

	private void startWorld() {
		m_player.stopWorldIfNecessary();
		m_startButton.setText( "restart" );
		m_pauseButton.setEnabled( true );
		m_stopButton.setEnabled( true );
		m_resumeButton.setEnabled( false );
		m_player.startWorld();
	}
	private void stopWorld() {
		m_startButton.setText( "start" );
		m_pauseButton.setEnabled( false );
		m_stopButton.setEnabled( false );
		m_resumeButton.setEnabled( false );
		m_player.stopWorld();
	}
	private void pauseWorld() {
		m_pauseButton.setEnabled( false );
		m_resumeButton.setEnabled( true );
		m_player.pauseWorld();
	}
	private void resumeWorld() {
		m_pauseButton.setEnabled( true );
		m_resumeButton.setEnabled( false );
		m_player.resumeWorld();
	}

	
	public void init() {
		super.init();
		setLayout( new java.awt.BorderLayout() );

		javax.swing.JPanel panel = new javax.swing.JPanel();
		panel.setLayout( new java.awt.GridLayout( 1, 4 ) );

		m_pauseButton.setEnabled( false );
		m_resumeButton.setEnabled( false );
		m_startButton.setEnabled( false );
		m_stopButton.setEnabled( false );

		panel.add( m_pauseButton );
		panel.add( m_resumeButton );
		panel.add( m_startButton );
		panel.add( m_stopButton );

		add( panel, java.awt.BorderLayout.NORTH );

		m_progressPanel = new ProgressPanel();
		add( m_progressPanel, java.awt.BorderLayout.CENTER );


		m_pauseButton.addActionListener( new java.awt.event.ActionListener() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				pauseWorld();
			}
		} );

		m_resumeButton.addActionListener( new java.awt.event.ActionListener() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				resumeWorld();
			}
		} );

		m_stopButton.addActionListener( new java.awt.event.ActionListener() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				stopWorld();
			}
		} );

		m_startButton.addActionListener( new java.awt.event.ActionListener() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				startWorld();
			}
		} );
		
	}

	private Runnable m_loadRunnable = new Runnable() {
		public void run() {
			try {
				@SuppressWarnings("unused")
				Class<Matrix4d> c = Matrix4d.class;
			} catch( Throwable t ) {
				remove( m_progressPanel );
				String initErrorMessage = getParameter( "initializationErrorMessage" );
				initErrorMessage = null;
				if( initErrorMessage == null ) {
					initErrorMessage = "Alice is unable to initialize.\n\nPlease click the link below.";
				}
				javax.swing.JTextArea errorTextArea = new javax.swing.JTextArea();
				errorTextArea.setText( initErrorMessage );
				add( errorTextArea );
				return;
			}
			try {
				java.net.URL url = new java.net.URL( getCodeBase(), getParameter( "world" ) );
				java.net.URLConnection urlConnection = url.openConnection();
				java.io.InputStream is = urlConnection.getInputStream();
				int contentLength = urlConnection.getContentLength();
				m_progressPanel.setDownloadTotal( contentLength );
				final int bufferLength = 2048;
				byte[] content;
				if( contentLength != -1 ) {
					int offset = 0;
					content = new byte[ contentLength ];
					while( offset < contentLength ) {
						int actual = is.read( content, offset, Math.min( bufferLength, contentLength-offset ) );
						offset += actual;
						m_progressPanel.setDownloadCurrent( offset );
					}
				} else {
					byte[] buffer = new byte[ bufferLength ];
					java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream( bufferLength );
					while( true ) {
						int actual = is.read( buffer, 0, bufferLength );
						if( actual != -1 ) {
							baos.write( buffer, 0, actual );
						} else {
							break;
						}
					}
					content = baos.toByteArray();
				}
				is.close();
				urlConnection = null;

				java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream( content );

				m_player.loadWorld( bais, new edu.cmu.cs.stage3.progress.ProgressObserver() {
					public void progressBegin( int total ) {
						progressUpdateTotal( total );
					}
					public void progressUpdateTotal( int total ) {
						m_progressPanel.setExtractTotal( total );
					}
					public void progressUpdate( int current, String description ) throws edu.cmu.cs.stage3.progress.ProgressCancelException {
						m_progressPanel.setExtractCurrent( current );
					}
					public void progressEnd() {
					}
				} );

				m_startButton.setEnabled( true );
				startWorld();
			} catch( java.net.MalformedURLException murle ) {
				murle.printStackTrace();
			} catch( java.io.IOException ioe ) {
				ioe.printStackTrace();
			}
		}
	};
	
	public void start() {
		super.start();
		new Thread( m_loadRunnable ).start();
	}
	
	public void stop() {
		m_player.stopWorldIfNecessary();
		m_player.unloadWorld();
		super.stop();
	}

	protected void handleRenderTarget( edu.cmu.cs.stage3.alice.core.RenderTarget renderTarget ) {
		remove( m_progressPanel );
		add( renderTarget.getInternal().getAWTComponent(), java.awt.BorderLayout.CENTER );
		doLayout();
		invalidate();
		repaint();
	}
}