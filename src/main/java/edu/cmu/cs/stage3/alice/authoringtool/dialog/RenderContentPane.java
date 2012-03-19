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

public class RenderContentPane extends edu.cmu.cs.stage3.swing.ContentPane {
	protected javax.swing.JPanel renderPanel = new javax.swing.JPanel(new java.awt.BorderLayout());
	protected javax.swing.JPanel buttonPanel = new javax.swing.JPanel(new java.awt.GridBagLayout());
	protected javax.swing.JSplitPane stdOutSplitPane = new javax.swing.JSplitPane(javax.swing.JSplitPane.VERTICAL_SPLIT);
	protected edu.cmu.cs.stage3.alice.authoringtool.util.Configuration config = edu.cmu.cs.stage3.alice.authoringtool.util.Configuration.getLocalConfiguration(edu.cmu.cs.stage3.alice.authoringtool.JAlice.class.getPackage());
	protected double aspectRatio;
	protected edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;
	protected boolean showStdOut = false;
	protected int stdOutHeight = 100;
	protected int watcherWidth = 200;
	protected OutputComponent stdOutOutputComponent;
	protected edu.cmu.cs.stage3.alice.authoringtool.util.WatcherPanel watcherPanel;
	protected javax.swing.JScrollPane watcherScrollPane;
	protected javax.swing.JSplitPane watcherSplitPane = new javax.swing.JSplitPane(javax.swing.JSplitPane.HORIZONTAL_SPLIT);
	protected javax.swing.JButton pauseButton;
	protected javax.swing.JButton resumeButton;
	protected javax.swing.JButton restartButton;
	protected javax.swing.JButton stopButton;
	protected javax.swing.JButton takePictureButton;
	protected javax.swing.JSlider speedSlider;
	protected javax.swing.JLabel speedLabel;
	protected RenderCanvasFocusListener renderCanvasFocusListener = new RenderCanvasFocusListener();
	protected boolean shiftIsDown = false;

	protected javax.swing.JScrollPane textScrollPane;
	protected edu.cmu.cs.stage3.alice.authoringtool.util.StyledStreamTextPane detailTextPane = new edu.cmu.cs.stage3.alice.authoringtool.util.StyledStreamTextPane();

	protected boolean doNotListenToSpeedSlider = false;
	protected boolean doNotListenToResize = false;
	protected final int dividerSize = 8;
	protected String title;

	protected java.awt.event.ActionListener okActionListener;

	protected class TextOutputDocumentListener implements javax.swing.event.DocumentListener {
		@Override
		public void insertUpdate(final javax.swing.event.DocumentEvent ev) {
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					try {
						String textUpdate = ev.getDocument().getText(ev.getOffset(), ev.getLength());
						detailTextPane.getDocument().insertString(detailTextPane.getDocument().getLength(), textUpdate, detailTextPane.stdOutStyle);
					} catch (Exception e) {}
					update();
				}
			});
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
			if (!showStdOut) {
				RenderContentPane.this.saveRenderBounds();
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						updateGUI();
						showStdOut = true;
						java.awt.Component renderCanvas = getRenderCanvas();
						if (renderCanvas != null) {
							renderCanvas.requestFocus();
						}
					}
				});
			}
		}
	}

	protected class RenderComponentListener extends java.awt.event.ComponentAdapter {

		@Override
		public void componentResized(java.awt.event.ComponentEvent ev) {
			if (shouldConstrainAspectOnResize() && !doNotListenToResize) {
				doNotListenToResize = true;
				java.awt.Rectangle oldBounds = getRenderBounds();
				java.awt.Dimension newSize = renderPanel.getSize();
				java.awt.Rectangle newBounds = new java.awt.Rectangle(oldBounds.getLocation(), newSize);

				int deltaWidth = Math.abs(oldBounds.width - newSize.width);
				int deltaHeight = Math.abs(oldBounds.height - newSize.height);

				constrainToAspectRatio(newBounds, deltaWidth < deltaHeight);

				config.setValue("rendering.renderWindowBounds", newBounds.x + ", " + newBounds.y + ", " + newBounds.width + ", " + newBounds.height);
				renderPanel.setPreferredSize(new java.awt.Dimension(newBounds.width, newBounds.height));
				buttonPanel.setPreferredSize(new java.awt.Dimension(newBounds.width, buttonPanel.getHeight()));
				packDialog();
				doNotListenToResize = false;
			}
		}
	}

	protected TextOutputDocumentListener textListener = new TextOutputDocumentListener();

	public RenderContentPane(edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool) {
		this.authoringTool = authoringTool;
		stdOutOutputComponent = authoringTool.getStdOutOutputComponent();
		watcherPanel = authoringTool.getWatcherPanel();
		guiInit();
	}

	public void setAspectRatio(double aspectRatio) {
		this.aspectRatio = aspectRatio;
	}

	@Override
	public String getTitle() {
		return title;
	}

	public void setTitle(String newTitle) {
		title = newTitle;
	}

	protected void setRenderWindowSizeBasedOnSavedBounds() {
		java.awt.Rectangle bounds = getRenderBounds();
		renderPanel.setPreferredSize(new java.awt.Dimension(bounds.width, bounds.height));
	}

	@Override
	public void preDialogShow(javax.swing.JDialog parentDialog) {
		super.preDialogShow(parentDialog);
		final java.awt.Component renderCanvas = getRenderCanvas();

		if (renderCanvas != null) {
			renderCanvas.addFocusListener(renderCanvasFocusListener);
			javax.swing.Timer focusTimer = new javax.swing.Timer(100, new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent ev) {
					renderCanvas.requestFocus();
				}
			});
			focusTimer.setRepeats(false);
			focusTimer.start();
		}
		stdOutOutputComponent.stdOutStream.println("*****Running World*****");
		stdOutOutputComponent.stdOutStream.flush();
		stdOutOutputComponent.getTextPane().getDocument().addDocumentListener(textListener);
		java.awt.Rectangle bounds = getRenderBounds();
		renderPanel.setPreferredSize(new java.awt.Dimension(bounds.width, bounds.height));
		parentDialog.setLocation(bounds.getLocation());
		showStdOut = false;
		keyMapInit();
		updateGUI();
		if (config.getValue("rendering.ensureRenderDialogIsOnScreen").equalsIgnoreCase("true")) {
			java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
			java.awt.Dimension dialogSize = parentDialog.getSize();
			java.awt.Point dialogLocation = parentDialog.getLocation();
			java.awt.Rectangle screenRect = new java.awt.Rectangle(0, 0, screenSize.width, screenSize.height);
			if (!javax.swing.SwingUtilities.isRectangleContainingRectangle(screenRect, parentDialog.getBounds())) {
				if (dialogLocation.x + dialogSize.width > screenSize.width) {
					dialogLocation.x = screenSize.width - dialogSize.width;
				}
				if (dialogLocation.x < 0) {
					dialogLocation.x = 0;
				}
				if (dialogLocation.y + dialogSize.height > screenSize.height) {
					dialogLocation.y = screenSize.height - dialogSize.height;
				}
				if (dialogLocation.y < 0) {
					dialogLocation.y = 0;
				}
				if (config.getValue("rendering.constrainRenderDialogAspectRatio").equalsIgnoreCase("false")) {
					if (dialogSize.width > screenSize.width) {
						java.awt.Dimension renderSize = renderPanel.getPreferredSize();
						renderSize.width = screenSize.width - 8;
						renderPanel.setPreferredSize(renderSize);
					}
					if (dialogSize.height > screenSize.height) {
						java.awt.Dimension renderSize = renderPanel.getPreferredSize();
						renderSize.height = screenSize.height - 27;
						renderPanel.setPreferredSize(renderSize);
					}
				} else {
					if (dialogSize.height > screenSize.height || dialogSize.width > screenSize.width) {
						double windowAspect = (double) screenSize.width / (double) screenSize.height;
						if (aspectRatio > windowAspect) { // constrain the width
							java.awt.Dimension renderSize = renderPanel.getPreferredSize();
							renderSize.width = screenSize.width - 8;
							renderSize.height = (int) Math.round(renderSize.width / aspectRatio);
							renderPanel.setPreferredSize(renderSize);
						} else { // constrain the height
							java.awt.Dimension renderSize = renderPanel.getPreferredSize();
							renderSize.height = screenSize.height - 27;
							renderSize.width = (int) Math.round(renderSize.height * aspectRatio);
							renderPanel.setPreferredSize(renderSize);
						}
					}
				}
				parentDialog.setLocation(dialogLocation);
				authoringTool.getJAliceFrame().sceneEditor.makeDirty();
				renderPanel.invalidate();
				parentDialog.pack();
			}
		}
	}

	@Override
	public void postDialogShow(javax.swing.JDialog parentDialog) {
		super.postDialogShow(parentDialog);
		authoringTool.worldStopRunning();
		// authoringTool.getWorld().speedMultiplier.set(new Double(1.0));
		speedSlider.setValue(0);
		saveRenderBounds();
		showStdOut = false;
		if (config.getValue("clearStdOutOnRun").equalsIgnoreCase("true")) {
			detailTextPane.setText("");
		}
		java.awt.Component renderCanvas = getRenderCanvas();
		if (renderCanvas != null) {
			renderCanvas.removeFocusListener(renderCanvasFocusListener);
		}
		stdOutOutputComponent.getTextPane().getDocument().removeDocumentListener(textListener);
		stdOutOutputComponent.stdOutStream.println("*****Stopping World*****");
		stdOutOutputComponent.stdOutStream.flush();

	}

	@Override
	public void addOKActionListener(java.awt.event.ActionListener l) {
		okActionListener = l;
		stopButton.addActionListener(l);
	}

	@Override
	public void removeOKActionListener(java.awt.event.ActionListener l) {
		okActionListener = null;
		stopButton.removeActionListener(l);
	}

	private void guiInit() {
		title = "Alice World";
		// on renderPanel resize, constrain to aspectRatio
		setRenderWindowSizeBasedOnSavedBounds();

		// renderPanel.addComponentListener(renderResizeListener);
		watcherScrollPane = new javax.swing.JScrollPane(watcherPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		textScrollPane = new javax.swing.JScrollPane(detailTextPane, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		detailTextPane.setEditable(false);

		// buttons
		pauseButton = new javax.swing.JButton(authoringTool.getActions().pauseWorldAction);
		resumeButton = new javax.swing.JButton(authoringTool.getActions().resumeWorldAction);
		restartButton = new javax.swing.JButton(authoringTool.getActions().restartWorldAction);
		stopButton = new javax.swing.JButton(authoringTool.getActions().stopWorldAction);
		takePictureButton = new javax.swing.JButton(authoringTool.getActions().takePictureAction);
		takePictureButton.setEnabled(true);

		speedLabel = new javax.swing.JLabel("Speed: 1x");
		int fontSize = Integer.parseInt(config.getValue("fontSize"));
		speedLabel.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, (int) (12 * fontSize / 12.0)));
		speedLabel.setPreferredSize(new java.awt.Dimension(80, 12));
		speedLabel.setMinimumSize(new java.awt.Dimension(20, 12));
		speedLabel.setMaximumSize(new java.awt.Dimension(80, 12));

		pauseButton.setMargin(new java.awt.Insets(3, 2, 3, 2));
		resumeButton.setMargin(new java.awt.Insets(3, 2, 3, 2));
		restartButton.setMargin(new java.awt.Insets(3, 2, 3, 2));
		stopButton.setMargin(new java.awt.Insets(3, 2, 3, 2));
		takePictureButton.setMargin(new java.awt.Insets(3, 2, 3, 2));

		speedSlider = new javax.swing.JSlider(0, 9, 0);

		speedSlider.setUI(new javax.swing.plaf.metal.MetalSliderUI() {

			@Override
			public void paintTrack(java.awt.Graphics g) {
				super.paintTrack(g);
			}
		});
		speedSlider.setPreferredSize(new java.awt.Dimension(100, 16));
		speedSlider.setMinimumSize(new java.awt.Dimension(40, 16));
		speedSlider.setMaximumSize(new java.awt.Dimension(100, 16));
		speedSlider.setSnapToTicks(true);
		speedSlider.addChangeListener(new javax.swing.event.ChangeListener() {
			@Override
			public void stateChanged(javax.swing.event.ChangeEvent ce) {
				javax.swing.JSlider s = (javax.swing.JSlider) ce.getSource();
				if (!doNotListenToSpeedSlider) {
					int value = s.getValue();
					if (value >= 0) {
						updateSpeed(value + 1.0);
					} else if (value < 0) {
						updateSpeed(1.0 / (1 + value * -1));
					}
				}
				java.awt.Component renderCanvas = getRenderCanvas();
				if (renderCanvas != null && renderCanvas.isShowing()) {
					renderCanvas.requestFocus();
				}
			}
		});

		buttonPanel.add(speedSlider, new java.awt.GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.HORIZONTAL, new java.awt.Insets(2, 2, 2, 2), 0, 0));
		buttonPanel.add(pauseButton, new java.awt.GridBagConstraints(2, 0, 1, 2, 0.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(2, 2, 2, 2), 0, 0));
		buttonPanel.add(speedLabel, new java.awt.GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 2, 0, 2), 0, 0));

		buttonPanel.add(resumeButton, new java.awt.GridBagConstraints(5, 0, 1, 2, 0.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(2, 2, 2, 2), 0, 0));
		buttonPanel.add(restartButton, new java.awt.GridBagConstraints(6, 0, 1, 2, 0.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(2, 2, 2, 2), 0, 0));
		buttonPanel.add(stopButton, new java.awt.GridBagConstraints(7, 0, 1, 2, 0.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(2, 2, 2, 2), 0, 0));
		buttonPanel.add(takePictureButton, new java.awt.GridBagConstraints(8, 0, 1, 2, 1.0, 0.0, java.awt.GridBagConstraints.EAST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(2, 2, 2, 2), 0, 0));

		watcherSplitPane = new javax.swing.JSplitPane(javax.swing.JSplitPane.HORIZONTAL_SPLIT);
		watcherSplitPane.setContinuousLayout(true);
		watcherSplitPane.setDividerSize(0);
		watcherSplitPane.setResizeWeight(1.0);
		watcherSplitPane.setLeftComponent(renderPanel);

		stdOutSplitPane = new javax.swing.JSplitPane(javax.swing.JSplitPane.VERTICAL_SPLIT);
		stdOutSplitPane.setContinuousLayout(true);
		stdOutSplitPane.setDividerSize(0);
		stdOutSplitPane.setResizeWeight(1.0);
		stdOutSplitPane.setTopComponent(watcherSplitPane);
		setLayout(new java.awt.BorderLayout());

		add(buttonPanel, java.awt.BorderLayout.NORTH);
		add(stdOutSplitPane, java.awt.BorderLayout.CENTER);

		updateGUI();
	}

	private void keyMapInit() {
		javax.swing.KeyStroke keyStroke;
		String commandKey;
		for (java.util.Iterator iter = authoringTool.getActions().renderActions.iterator(); iter.hasNext();) {
			javax.swing.Action action = (javax.swing.Action) iter.next();

			try {
				keyStroke = (javax.swing.KeyStroke) action.getValue(javax.swing.Action.ACCELERATOR_KEY);
				commandKey = (String) action.getValue(javax.swing.Action.ACTION_COMMAND_KEY);
			} catch (ClassCastException e) {
				continue;
			}
			if (keyStroke != null && commandKey != null) {
				java.awt.Component root = javax.swing.SwingUtilities.getRoot(this);
				if (root instanceof javax.swing.JDialog) {
					((javax.swing.JDialog) root).getRootPane().registerKeyboardAction(action, commandKey, keyStroke, javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW);
				}
				// getRootPane().getInputMap().put( keyStroke, commandKey );
				// getRootPane().getActionMap().put( commandKey, action );
			}
		}
	}

	public void saveRenderBounds() {
		java.awt.Point pos = new java.awt.Point(0, 0);
		java.awt.Dimension size = renderPanel.getSize();
		java.awt.Component root = javax.swing.SwingUtilities.getRoot(this);
		if (root instanceof javax.swing.JDialog) {
			javax.swing.SwingUtilities.convertPointToScreen(pos, root);
		} else {
			javax.swing.SwingUtilities.convertPointToScreen(pos, renderPanel);
		}
		config.setValue("rendering.renderWindowBounds", pos.x + ", " + pos.y + ", " + size.width + ", " + size.height);
	}

	public void saveRenderBounds(java.awt.Rectangle newBounds) {
		config.setValue("rendering.renderWindowBounds", newBounds.x + ", " + newBounds.y + ", " + newBounds.width + ", " + newBounds.height);
	}

	protected boolean shouldConstrainAspectOnResize() {
		return !(showStdOut || authoringTool.getWatcherPanel().isThereSomethingToWatch()) && config.getValue("rendering.constrainRenderDialogAspectRatio").equalsIgnoreCase("true");
	}

	public javax.swing.JPanel getRenderPanel() {
		return renderPanel;
	}

	public java.awt.Rectangle getRenderBounds() {
		String boundsString = config.getValue("rendering.renderWindowBounds");
		java.util.StringTokenizer st = new java.util.StringTokenizer(boundsString, " \t,");
		if (st.countTokens() == 4) {
			try {
				int x = Integer.parseInt(st.nextToken());
				int y = Integer.parseInt(st.nextToken());
				int width = Integer.parseInt(st.nextToken());
				int height = Integer.parseInt(st.nextToken());
				java.awt.Rectangle bounds = new java.awt.Rectangle(x, y, width, height);
				double currentAspectRatio = (double) width / height;
				if (RenderContentPane.this.shouldConstrainAspectOnResize()) {
					constrainToAspectRatio(bounds, currentAspectRatio > 1.0);
				}
				return bounds;
			} catch (NumberFormatException e) {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("Parse error in config value: rendering.renderWindowBounds", e);
			}
		} else {
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("Incorrect number of tokens in config value: rendering.renderWindowBounds", null);
		}

		return null;
	}

	public void constrainToAspectRatio(java.awt.Rectangle bounds, boolean stretchHorizontally) {
		if (aspectRatio > 0.0) {
			if (stretchHorizontally) {
				bounds.width = (int) Math.round(bounds.height * aspectRatio);
			} else {
				bounds.height = (int) Math.round(bounds.width / aspectRatio);
			}
		}
	}

	public void updateSpeed(double newSpeed) {
		authoringTool.setWorldSpeed(newSpeed);
		String speedText = java.text.NumberFormat.getInstance().format(newSpeed);
		if (newSpeed < 1) {
			if (newSpeed == .5) {
				speedText = "1/2";
			} else if (newSpeed == .25) {
				speedText = "1/4";
			} else if (newSpeed == .2) {
				speedText = "1/5";
			} else if (newSpeed > .3 && newSpeed < .34) {
				speedText = "1/3";
			} else if (newSpeed > .16 && newSpeed < .168) {
				speedText = "1/6";
			} else if (newSpeed > .14 && newSpeed < .143) {
				speedText = "1/7";
			}
		}
		speedLabel.setText("Speed: " + speedText + "x");
		speedLabel.repaint();
	}

	public void setSpeedSliderValue(int value) {
		speedSlider.setValue(value);
	}

	public void stopWorld() {
		if (okActionListener != null) {
			okActionListener.actionPerformed(null);
		}
	}

	public void updateGUI() {

		// scriptScratchPad.setSandbox( authoringTool.getWorld() );

		// getContentPane().removeAll();
		// getContentPane().add( buttonPanel, java.awt.BorderLayout.NORTH );

		int renderWidth = renderPanel.getWidth();
		int renderHeight = renderPanel.getHeight();

		// this looks much more complicated than it really is, although some of
		// the code is quite tempermental
		if (showStdOut) {
			textScrollPane.setPreferredSize(new java.awt.Dimension(renderWidth, stdOutHeight));
			stdOutSplitPane.setBottomComponent(textScrollPane);
			stdOutSplitPane.setDividerSize(dividerSize);
		} else {
			stdOutSplitPane.setBottomComponent(null);
			stdOutSplitPane.setDividerLocation(0);
			stdOutSplitPane.setDividerSize(0);
		}
		if (authoringTool.getWatcherPanel().isThereSomethingToWatch()) {
			watcherScrollPane.setPreferredSize(new java.awt.Dimension(watcherWidth, renderHeight));
			watcherSplitPane.setRightComponent(watcherScrollPane);
			watcherSplitPane.setDividerSize(dividerSize);
		} else {
			watcherSplitPane.setRightComponent(null);
			watcherSplitPane.setDividerLocation(0);
			watcherSplitPane.setDividerSize(0);
		}
		packDialog();
	}

	@Override
	public void addNotify() {
		super.addNotify();
		showStdOut = false;
		updateGUI();
	}

	public java.awt.Component getRenderCanvas() {
		java.awt.Component authoringToolRenderPanel = renderPanel.getComponent(0);
		if (authoringToolRenderPanel instanceof java.awt.Container) {
			return ((java.awt.Container) authoringToolRenderPanel).getComponent(0);
		}
		return null;
	}

	public class RenderCanvasFocusListener extends java.awt.event.FocusAdapter {
		public void focusLost() {
			java.awt.Component renderCanvas = getRenderCanvas();
			if (renderCanvas != null && renderCanvas.isShowing()) {
				renderCanvas.requestFocus();
			}
		}
	}
}