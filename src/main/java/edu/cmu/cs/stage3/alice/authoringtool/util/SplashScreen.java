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
public class SplashScreen extends java.awt.Frame {
	protected java.awt.Image image;
	protected java.awt.Dimension size;
	protected java.awt.Window splashWindow;

	public SplashScreen(java.awt.Image image) {
		this.image = image;

		java.awt.MediaTracker tracker = new java.awt.MediaTracker(this);
		tracker.addImage(image, 0);
		try {
			tracker.waitForID(0);
		} catch (InterruptedException e) {}

		size = new java.awt.Dimension(image.getWidth(this), image.getHeight(this));
		if (size.width < 1 || size.height < 1) {
			size = new java.awt.Dimension(256, 256);
		}

		splashWindow = new java.awt.Window(this) {

			@Override
			public void paint(java.awt.Graphics g) {
				g.drawImage(SplashScreen.this.image, 0, 0, this);
				// g.setColor( java.awt.Color.yellow );
				g.setColor(java.awt.Color.white);
				String versionString = "version: " + edu.cmu.cs.stage3.alice.authoringtool.JAlice.getVersion();
				int stringWidth = g.getFontMetrics().stringWidth(versionString);
				// g.drawString( versionString, 6, size.height - 6 ); //TODO:
				// this makes the Splash Screen unnecessarily specialized. the
				// functionality should be abstracted out.
				g.drawString(versionString, size.width - 6 - stringWidth, size.height - 6); // TODO:
																							// this
																							// makes
																							// the
																							// Splash
																							// Screen
																							// unnecessarily
																							// specialized.
																							// the
																							// functionality
																							// should
																							// be
																							// abstracted
																							// out.
				if (hasNewVersion()) {
					g.drawString("Loading...                      New Alice 2.2 update available ", 10, size.height - 6);
				} else {
					g.drawString("Loading...", 10, size.height - 6);
				}
			}
		};
		splashWindow.setSize(size);

		this.setSize(size);
	}

	public boolean hasNewVersion() {
		if (System.getProperty("os.name") != null && System.getProperty("os.name").startsWith("Windows")) {
			try {
				StringBuffer sb = new StringBuffer("http://alicedownloads.org/alice.jar");
				java.net.URL url = new java.net.URL(sb.toString());
				java.net.URLConnection urlc = url.openConnection();
				long i = urlc.getLastModified();
				java.util.Date d = new java.util.Date(i);
				java.util.regex.Pattern p = java.util.regex.Pattern.compile("\\D");
				String oldVersion[] = p.split(edu.cmu.cs.stage3.alice.authoringtool.JAlice.getVersion());
				if (Integer.valueOf(oldVersion[3]).compareTo(Integer.valueOf(d.getMonth() + 1)) < 0 && Integer.valueOf(oldVersion[5]).compareTo(Integer.valueOf(d.getYear() + 1900)) <= 0) {
					return true;
				}
			} catch (Exception e) {

			}
		}
		return false;
	}

	public void showSplash() {
		java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screenSize.width - size.width) / 2;
		int y = (screenSize.height - size.height) / 2;
		splashWindow.setLocation(x, y);
		this.setLocation(x, y);
		splashWindow.setVisible(true);
	}

	public void hideSplash() {
		// splashWindow.setVisible( false );
		splashWindow.dispose();
	}
}