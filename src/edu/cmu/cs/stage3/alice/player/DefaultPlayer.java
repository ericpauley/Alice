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

public class DefaultPlayer extends AbstractPlayer {
	public DefaultPlayer(Class rendererClass) {
		super( rendererClass );
	}
	private java.util.Vector m_frames = new java.util.Vector();
	
	protected boolean isPreserveAndRestoreRequired() {
		return false;
	}
	
	protected int getDesiredFrameWidth() {
		return 320;
	}
	protected int getDesiredFrameHeight() {
		return 240;
	}
	
	
	protected void handleRenderTarget(edu.cmu.cs.stage3.alice.core.RenderTarget renderTarget) {
		java.awt.Frame frame = new java.awt.Frame();
		frame.setSize(getDesiredFrameWidth(), getDesiredFrameHeight());
		frame.setLayout(new java.awt.BorderLayout());
		frame.add(renderTarget.getAWTComponent(), java.awt.BorderLayout.CENTER);
		frame.setVisible( true );
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			
			public void windowClosing(java.awt.event.WindowEvent ev) {
				m_frames.removeElement(ev.getSource());
				if( m_frames.size() == 0 ) {
					System.exit(0);
				}
			}
		});
		m_frames.add(frame);
		edu.cmu.cs.stage3.swing.DialogManager.initialize( frame );
	}
	/*
	private static java.io.File getFileFromArgs(String[] args, int startFrom) {
		java.io.File file = null;
		String path = "";
		int i = startFrom;
		while (i < args.length) {
			path += args[i];
			i++;
			file = new java.io.File(path);
			if( file.exists() ) {
				break;
			} else {
				path += " ";
				file = null;
			}
		}
		return file;
	}

	public static void main(String[] args) {
		Class rendererClass = null;
		java.io.File file = null;
		if( args.length > 0 ) {
			int startFrom = 1;
			if (args[0].equals("-directx")) {
				rendererClass = edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer.Renderer.class;
			//} else if( args[ 0 ].equals("-opengl" ) ) {
			//	renderer = new edu.cmu.cs.stage3.alice.scenegraph.renderer.openglrenderer.Renderer();
			//} else if( args[ 0 ].equals("-java3d" ) ) {
			//	rendererClass = edu.cmu.cs.stage3.alice.scenegraph.renderer.java3drenderer.Renderer.class;
			} else if( args[ 0 ].equals("-jogl" ) ) {
			    rendererClass = edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer.Renderer.class;
			} else if( args[ 0 ].equals("-null" ) ) {
				rendererClass = edu.cmu.cs.stage3.alice.scenegraph.renderer.nullrenderer.Renderer.class;
			} else {
				System.err.println(args[0]);
				startFrom = 0;
			}
			file = getFileFromArgs(args, startFrom);
		}
		if( file == null ) {
			java.awt.Frame frame = new java.awt.Frame();
			java.awt.FileDialog fd = new java.awt.FileDialog(frame);
			fd.setVisible( true );
			String filename = fd.getFile();
			if (fd.getDirectory() != null && fd.getFile() != null) {
				file = new java.io.File(fd.getDirectory() + fd.getFile());
			}
			frame.dispose();
		}
		DefaultPlayer player = new DefaultPlayer(rendererClass);
		try {
			player.loadWorld( file, new edu.cmu.cs.stage3.progress.ProgressObserver() {
				private int i = 0;
				private int n = 80;
				private int m_total = edu.cmu.cs.stage3.progress.ProgressObserver.UNKNOWN_TOTAL;
				public void progressBegin(int total) {
					progressUpdateTotal(total);
				}
				public void progressUpdateTotal(int total) {
					m_total = total;
				}
				public void progressUpdate(int current, String description) throws edu.cmu.cs.stage3.progress.ProgressCancelException {
					if (m_total == edu.cmu.cs.stage3.progress.ProgressObserver.UNKNOWN_TOTAL) {
						System.out.print("?");
					} else {
						if (i < (int) ((current / (double) m_total) * n)) {
							System.out.print(".");
							i++;
						}
					}
				}
				public void progressEnd() {
				}
			} );
			player.startWorld();
		} catch( java.io.IOException ioe ) {
			ioe.printStackTrace();
		}
	}*/
}
