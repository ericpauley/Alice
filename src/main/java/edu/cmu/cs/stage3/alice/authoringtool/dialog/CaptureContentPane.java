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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import movieMaker.FrameSequencer;
import movieMaker.MovieCapturer;
import movieMaker.MovieWriter;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.Sandbox;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;

public class CaptureContentPane extends edu.cmu.cs.stage3.swing.ContentPane {
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

	// Part of the new toolbar capture buttons
	protected javax.swing.JPanel captureBar = new javax.swing.JPanel(new java.awt.GridBagLayout());
	protected javax.swing.JPanel exportBar = new javax.swing.JPanel(new java.awt.GridBagLayout());
	protected javax.swing.JFrame statusFrame;
	protected javax.swing.JLabel recordLabel;

	// Capture Bar
	protected javax.swing.JButton startCaptureButton;
	protected javax.swing.JButton stopCaptureButton;
	protected javax.swing.JButton clearButton;
	protected javax.swing.JLabel timeLabel;

	// Encode Bar
	protected javax.swing.JButton encodeButton;
	protected javax.swing.JTextField fileName;
	protected javax.swing.JComboBox fileType;
	protected javax.swing.JLabel statusLabel;
	protected movieMaker.StartMovieCapture movieCapture = null;

	// Three Threads
	// Capture -> captures and used to encode
	// Pulse -> uses to pulse status Label
	// timerThread -> used to time the capturing
	private Thread capturing, pulse, timerThread, writing, compressing;
	private boolean pulsing = false;
	private boolean running = false;
	private boolean endCapturing = false;

	// Capture Objects
	private MovieCapturer videoHandler;
	private String exportDirectory;
	private FrameSequencer frameSequencer;

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
				CaptureContentPane.this.saveRenderBounds();
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
			if (shouldConstrainAspectOnResize() && !doNotListenToResize && !endCapturing) {
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

				// Put in
				captureBar.setPreferredSize(new java.awt.Dimension(newBounds.width, captureBar.getHeight()));
				exportBar.setPreferredSize(new java.awt.Dimension(newBounds.width, exportBar.getHeight()));

				packDialog();
				doNotListenToResize = false;
			}
		}
	}

	protected TextOutputDocumentListener textListener = new TextOutputDocumentListener();

	protected RenderComponentListener renderResizeListener = new RenderComponentListener();

	public CaptureContentPane(edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool) {
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
				renderPanel.invalidate();
				parentDialog.pack();
			}
		}
	}

	@Override
	public void postDialogShow(javax.swing.JDialog parentDialog) {
		super.postDialogShow(parentDialog);

		authoringTool.stopWorld();
		statusFrame.setVisible(false);
		timeLabel.setBackground(java.awt.Color.GREEN);
		setClear(false);
		setButtonsCapturing(false);

		endCapturing = false;
		running = false;
		pulsing = false;

		try {
			if (timerThread != null) {
				timerThread.join();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		timeLabel.setText("0:00  ");
		try {
			if (capturing != null) {
				capturing.join();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
		restartButton = new javax.swing.JButton(authoringTool.getActions().restartStopWorldAction);
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

		// Capture GUI section
		stopCaptureButton = new JButton("Stop Recording");
		stopCaptureButton.setToolTipText("Click here to stop the video capture");

		startCaptureButton = new JButton("Record");
		startCaptureButton.setToolTipText("Click here to start the video capture");

		encodeButton = new JButton("Export Video");
		encodeButton.setToolTipText("Click here to stop filming and encode the video");

		clearButton = new JButton("Clear");
		clearButton.setToolTipText("Click here to clear captured video");
		clearButton.setMargin(new java.awt.Insets(3, 2, 3, 2));

		stopCaptureButton.setMargin(new java.awt.Insets(3, 2, 3, 2));
		startCaptureButton.setMargin(new java.awt.Insets(3, 2, 3, 2));
		encodeButton.setMargin(new java.awt.Insets(3, 2, 3, 2));

		String[] types = {"MOV"};
		fileType = new javax.swing.JComboBox(types);

		fileName = new javax.swing.JTextField("MyVideo");
		fileName.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, (int) (12 * fontSize / 12.0)));
		fileName.setPreferredSize(new java.awt.Dimension(124, 24));
		fileName.setMinimumSize(new java.awt.Dimension(60, 24));
		fileName.setMaximumSize(new java.awt.Dimension(124, 24));

		statusLabel = new javax.swing.JLabel("Ready");
		timeLabel = new javax.swing.JLabel("0:00  ");

		captureBar = new javax.swing.JPanel();
		statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
		captureBar.setLayout(new java.awt.GridBagLayout());
		exportBar = new javax.swing.JPanel();
		exportBar.setLayout(new java.awt.GridBagLayout());
		recordLabel = new JLabel();

		captureBar.add(startCaptureButton, new java.awt.GridBagConstraints(1, 0, 1, 2, 0.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(2, 2, 2, 2), 0, 0));
		captureBar.add(stopCaptureButton, new java.awt.GridBagConstraints(2, 0, 1, 2, 0.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(2, 2, 2, 2), 0, 0));
		captureBar.add(clearButton, new java.awt.GridBagConstraints(3, 0, 1, 2, 0.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(2, 2, 2, 2), 0, 0));
		captureBar.add(new JLabel(), new java.awt.GridBagConstraints(4, 0, 1, 2, 0.5, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(2, 2, 2, 2), 0, 0));
		captureBar.add(recordLabel, new java.awt.GridBagConstraints(5, 0, 1, 2, 0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(2, 2, 2, 2), 0, 0));
		captureBar.add(timeLabel, new java.awt.GridBagConstraints(6, 0, 1, 2, 0.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(2, 2, 2, 2), 0, 0));

		exportBar.add(new JLabel("   File name: "), new java.awt.GridBagConstraints(1, 0, 1, 2, 0.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(2, 2, 2, 2), 0, 0));
		exportBar.add(fileName, new java.awt.GridBagConstraints(2, 0, 1, 2, 0.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(2, 2, 2, 2), 0, 0));
		exportBar.add(fileType, new java.awt.GridBagConstraints(3, 0, 1, 2, 0.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(2, 2, 2, 2), 0, 0));
		exportBar.add(new JLabel(), new java.awt.GridBagConstraints(4, 0, 1, 2, 0.5, 0.0, java.awt.GridBagConstraints.EAST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(2, 2, 2, 2), 0, 0));
		exportBar.add(encodeButton, new java.awt.GridBagConstraints(5, 0, 1, 2, 0.0, 0.0, java.awt.GridBagConstraints.EAST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(2, 2, 2, 2), 0, 0));

		timeLabel.setBackground(java.awt.Color.GREEN);

		setButtonsCapturing(false);
		setClear(false);

		captureActionListeners();

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

		// create Panel with two levels each capture button Panel
		javax.swing.JPanel menu = new javax.swing.JPanel();
		menu.setLayout(new java.awt.GridLayout(3, 1));
		menu.add(captureBar);
		menu.add(exportBar);
		menu.add(buttonPanel);
		add(menu, java.awt.BorderLayout.NORTH);
		add(stdOutSplitPane, java.awt.BorderLayout.CENTER);

		createStatusFrame();

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
		return !showStdOut // ||
							// authoringTool.getWatcherPanel().isThereSomethingToWatch())
				&& config.getValue("rendering.constrainRenderDialogAspectRatio").equalsIgnoreCase("true");
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
				if (CaptureContentPane.this.shouldConstrainAspectOnResize()) {
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

		// Aik Min removed this

		/*
		 * if (authoringTool.getWatcherPanel().isThereSomethingToWatch()) {
		 * watcherScrollPane.setPreferredSize(new java.awt.Dimension(
		 * watcherWidth, renderHeight));
		 * watcherSplitPane.setRightComponent(watcherScrollPane);
		 * watcherSplitPane.setDividerSize(dividerSize); } else {
		 * watcherSplitPane.setRightComponent( null );
		 * watcherSplitPane.setDividerLocation(0);
		 * watcherSplitPane.setDividerSize(0); }
		 */
		watcherSplitPane.setRightComponent(null);
		watcherSplitPane.setDividerLocation(0);
		watcherSplitPane.setDividerSize(0);

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

	public void traverseTree() {
		World w = authoringTool.getWorld();

		findSounds(w);
	}

	public void setButtonsCapturing(boolean choice) {
		stopCaptureButton.setEnabled(choice);
		startCaptureButton.setEnabled(!choice);
		encodeButton.setEnabled(!choice);
		resumeButton.setEnabled(!choice);
		stopButton.setEnabled(!choice);
		restartButton.setEnabled(!choice);
	}

	public void setClear(boolean choice) {
		clearButton.setEnabled(choice);
		encodeButton.setEnabled(choice);
	}

	// checks all response files for sounds to add listeners to
	public void findSoundsfromResponse(Response s) {

		if (s instanceof edu.cmu.cs.stage3.alice.core.response.SoundResponse) {
			((edu.cmu.cs.stage3.alice.core.response.SoundResponse) s).addSoundListener(new movieMaker.SoundHandler(authoringTool.getSoundStorage(), authoringTool));
			// System.err.println(s.toString());
		}
		if (s.getChildCount() > 0) {
			Element[] children = s.getChildren(Sandbox.class);
			for (Element element : children) {
				findSounds((Sandbox) element);
			}

			children = s.getChildren(edu.cmu.cs.stage3.alice.core.Response.class);
			for (Element element : children) {
				findSoundsfromResponse((edu.cmu.cs.stage3.alice.core.Response) element);
			}

		}
	}

	// traverses the scenegraph looking for sounds to add listeneres to
	public void findSounds(Sandbox sbox) {
		ElementArrayProperty p = sbox.responses;
		Object[] objs = p.getArrayValue();

		// System.err.println(sbox.name.get());

		for (Object obj : objs) {
			findSoundsfromResponse((edu.cmu.cs.stage3.alice.core.Response) obj);
		}

		Element[] t = sbox.getChildren(edu.cmu.cs.stage3.alice.core.Sandbox.class);

		for (Element element : t) {
			findSounds((Sandbox) element);
		}

	}

	// setup where the frames folder will be
	public void setExportDirectory(String directory) {
		exportDirectory = directory;
	}

	// set up for capturing
	public void captureInit() {
		videoHandler = new MovieCapturer(exportDirectory + "/frames/");
		frameSequencer = videoHandler.getFrameSequencer();
		authoringTool.pause();

		// adds sound listeners
		traverseTree();
		authoringTool.getSoundStorage().frameList = new java.util.ArrayList();
	}

	// Used to delete frame files after encoding
	public void removeFiles(String dir) {
		java.io.File f = new java.io.File(dir);
		String[] files = f.list(new java.io.FilenameFilter() {
			@Override
			public boolean accept(java.io.File dir, String name) {
				if (name.startsWith("frame") || name.endsWith(".wav") || name.endsWith(".mp3") || name.endsWith(".mov") || name.endsWith(".MP3") || name.endsWith(".WAV") || name.endsWith(".MOV")) {
					return true;
				}
				return false;
			}
		});

		if (files == null) {
			return;
		}
		String x;
		for (String file : files) {
			x = file;
			java.io.File empty = new java.io.File(dir + x);
			empty.delete();
		}
	}

	// is the world running?
	public boolean getRunning() {
		return running;
	}

	// have we finished capturing?
	public boolean getEnd() {
		return endCapturing;
	}

	public JLabel getStatusLabel() {
		return statusLabel;
	}

	public javax.swing.JPanel getButtonPanel() {
		return buttonPanel;
	}

	// creates status window for pulsing when encoding
	public void createStatusFrame() {
		statusFrame = new javax.swing.JFrame();
		statusFrame.setSize(200, 100);
		statusFrame.setVisible(false);
		statusFrame.getContentPane().add(statusLabel);
		statusFrame.pack();
	}

	@Override
	public void disable() {
		startCaptureButton.setEnabled(false);
		stopCaptureButton.setEnabled(false);
		clearButton.setEnabled(false);
		encodeButton.setEnabled(false);

		resumeButton.setEnabled(false);
		pauseButton.setEnabled(false);
		restartButton.setEnabled(false);
		stopButton.setEnabled(false);

		speedSlider.setEnabled(false);
		fileName.setEnabled(false);
		fileType.setEnabled(false);
		takePictureButton.setEnabled(false);
	}

	@Override
	public void enable() {
		startCaptureButton.setEnabled(true);
		clearButton.setEnabled(true);
		encodeButton.setEnabled(true);

		resumeButton.setEnabled(true);
		restartButton.setEnabled(true);
		stopButton.setEnabled(true);

		speedSlider.setEnabled(true);
		fileName.setEnabled(true);
		fileType.setEnabled(true);
		takePictureButton.setEnabled(true);
	}

	// shows the pulsing window
	public void showStatus() {
		disable();
		statusFrame.setSize(200, 100);
		statusFrame.setVisible(true);
		try {
			if (System.getProperty("os.name").startsWith("Window")) {
				statusFrame.setAlwaysOnTop(true);
				java.awt.Point location = captureBar.getLocationOnScreen();
				statusFrame.setLocation(new java.awt.Point(location.x, (int) (location.y + renderPanel.getHeight() / 2.0)));
			} else {
				statusFrame.setLocation(0, 0);
			}
		} catch (Exception e) {};
	}

	// happens when capturing stops
	public void stopCaptureAction() {

		running = false;
		if (authoringTool.getWorldClock().isPaused() == true) {
			authoringTool.pause();
		}

		authoringTool.getSoundStorage().setListening(false, System.currentTimeMillis() / 1000.0);

		recordLabel.setIcon(null);
		setButtonsCapturing(false);
		setClear(true);

		try {
			writing.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// happens when clear button is hit. Removes all files and sets up for
	// capturing again
	public void clearAction() {
		// statusLabel.setText("Cleared.");

		endCapturing = false;// kills previous thread of Capturing
		running = false;
		pulsing = false;

		setClear(false);

		removeFiles(exportDirectory + "/frames/");

		try {
			if (timerThread != null) {
				timerThread.join();
			}
		} catch (InterruptedException e1) {}

		// todo: reset Storage
		timeLabel.setText("0:00  ");

		authoringTool.setSoundStorage(new movieMaker.SoundStorage());
		videoHandler = new MovieCapturer(exportDirectory + "/frames/");
		authoringTool.getSoundStorage().frameList = new java.util.ArrayList();
		frameSequencer = videoHandler.getFrameSequencer();
		traverseTree();
	}

	// start capturing the momvie
	public void startCaptureAction() {
		setClear(false);

		// captureBar.setBackground(java.awt.Color.RED);
		timeLabel.setBackground(java.awt.Color.RED);
		// If a new capture sequence
		setButtonsCapturing(true);
		// restartButton.setEnabled(true);

		recordLabel.setIcon(new javax.swing.ImageIcon("etc/redDot.PNG"));

		recordLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		if (movieCapture == null || capturing == null || !capturing.isAlive()) {

			endCapturing = true;

			movieCapture = new movieMaker.StartMovieCapture(authoringTool, CaptureContentPane.this, frameSequencer, videoHandler.getFramesPerSecond());

			running = true;

			timerThread = new Thread(new StartTimer(timeLabel));
			capturing = new Thread(movieCapture);
			writing = new Thread(new WriteFrames(exportDirectory + "/frames", frameSequencer, frameSequencer.getFrameNumber()));

			capturing.start();
			writing.start();

			timerThread.start();

		} else {
			running = true;
			writing = new Thread(new WriteFrames(exportDirectory + "/frames", frameSequencer, frameSequencer.getFrameNumber()));
			writing.start();
		}

		if (!authoringTool.getWorldClock().isPaused()) {
			authoringTool.resume();
		}
		authoringTool.getSoundStorage().setListening(true, System.currentTimeMillis() / 1000.0);

	}

	public javax.swing.JPanel getCapturePanel() {
		return captureBar;
	}

	public javax.swing.JPanel getExportPanel() {
		return exportBar;
	}

	// Encoding the Movie
	public void encodeAction() {

		setClear(false);
		setButtonsCapturing(false);
		encodeButton.setEnabled(false);

		endCapturing = false;// kills previous thread of Capturing
		pulsing = true;

		timeLabel.setText("0:00  ");

		try {
			capturing.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Start Pulsing status
		pulse = new Thread(new StartStatusPulsing(statusLabel, "Encoding Video"));
		pulse.start();

		// Show Pulsing Status
		showStatus();

		// movieCapture = null;

		// start Compression
		compressing = new Thread(new StartMovieCompression());
		compressing.start();
	}

	// set up all the button action listeners

	public void captureActionListeners() {
		stopCaptureButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stopCaptureAction();
			}
		});

		clearButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearAction();
			}
		});

		startCaptureButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startCaptureAction();
			}
		});

		encodeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				encodeAction();
			}
		});

	}

	// used to find which area should be captured on screen
	public java.awt.Rectangle getRenderPanelLocation() {
		java.awt.Rectangle area = new java.awt.Rectangle(0, 0, 0, 0);

		if (stdOutSplitPane != null && stdOutSplitPane.getLocationOnScreen() != null) {
			area.x = stdOutSplitPane.getLocationOnScreen().x;
			area.y = stdOutSplitPane.getLocationOnScreen().y;
		}

		if (showStdOut) {
			area.height = stdOutHeight;
		}
		return area;
	}

	// Movie Compression Thread
	private class StartMovieCompression implements Runnable {

		@Override
		public void run() {

			// authoringTool.stdErrOutContentPane.stopReactingToError();

			// create the movie with no sound
			MovieWriter writer = new MovieWriter(videoHandler.getFramesPerSecond(), exportDirectory + "/frames/", fileName.getText() + "_NoSound", exportDirectory + "/frames/");
			if (writer == null) {
				afterEncoding(true);
				return;
			}

			// both options go to quicktime (avi never really worked)
			boolean result;
			if (fileType.getSelectedIndex() == 0) {
				result = writer.writeQuicktime();
			} else {
				result = writer.writeQuicktime();
			}

			pulsing = false;
			try {
				pulse.join();
			} catch (InterruptedException e) {}
			writer = null;

			if (result == false) {
				statusLabel.setText("Video failed to encrypt");
				afterEncoding(true);
				return;
			}

			if (authoringTool.getSoundStorage() == null) {
				System.err.print("No Sound Storage");
				afterEncoding(true);
				return;
			}

			movieMaker.Merge m = new movieMaker.Merge(authoringTool.getSoundStorage().createURL(exportDirectory + "/" + fileName.getText() + ".mov"));
			if (m == null) {
				afterEncoding(true);
				return;
			}

			java.util.Vector sources = new java.util.Vector();

			pulsing = true;
			pulse = new Thread(new StartStatusPulsing(statusLabel, "Encoding Sound"));
			pulse.start();

			// get all the sound files
			if (frameSequencer != null) {
				sources = authoringTool.getSoundStorage().encodeFiles((frameSequencer.getFrameNumber() + 1) / 16.0, exportDirectory + "/frames/" + authoringTool.numEncoded);
			} else {
				System.err.print("No Sequencer");
				afterEncoding(true);
				return;
			}

			pulsing = false;
			try {
				pulse.join();
			} catch (InterruptedException e) {}

			pulsing = true;

			pulse = new Thread(new StartStatusPulsing(statusLabel, "Merging Sound and Audio"));
			pulse.start();

			if (sources == null) {
				afterEncoding(true);
				return;
			}

			sources.add(authoringTool.getSoundStorage().createURL(exportDirectory + "/frames/" + fileName.getText() + "_NoSound.mov"));

			// merge the sounds and silent movie together if sounds exist
			if (sources.size() > 0) {
				m.doMerge(sources);
			}

			m = null;
			sources = null;
			pulsing = false;
			try {
				pulse.join();
			} catch (InterruptedException e) {}
			// clean up after encoding
			afterEncoding(false);
		}

	}

	// cleaning up after encoding (checks for error and does things differently
	// accordingly)
	void afterEncoding(boolean error) {
		// if there was an error don't delete files
		authoringTool.stdErrOutContentPane.startReactingToError();
		if (error) {
			System.err.print("Error in making video.");
		}

		running = false;
		pulsing = false;
		statusFrame.setVisible(false);
		authoringTool.setSoundStorage(new movieMaker.SoundStorage());
		videoHandler = new MovieCapturer(exportDirectory + "/frames/");
		authoringTool.getSoundStorage().frameList = new java.util.ArrayList();
		frameSequencer = videoHandler.getFrameSequencer();
		traverseTree();
		authoringTool.numEncoded++;
		enable();
	}

	// Pulses the status label
	private class StartStatusPulsing implements Runnable {
		private JLabel label;

		private String text;

		public StartStatusPulsing(JLabel l, String s) {
			label = l;
			text = s;
		}

		@Override
		public void run() {

			long time = 300;
			while (pulsing) {
				label.setText(text + ".");
				try {
					Thread.sleep(time);
				} catch (InterruptedException e) {}
				label.setText(text + "..");
				try {
					Thread.sleep(time);
				} catch (InterruptedException e) {}
			}
		}

	}

	private class WriteFrames implements Runnable {

		private int frameNumber;
		// access to frameSequence
		String fileLocation;
		private NumberFormat numberFormat = NumberFormat.getIntegerInstance();

		private movieMaker.FrameSequencer myFrameSeq;

		public WriteFrames(String dirPath, movieMaker.FrameSequencer fs, int num) {
			fileLocation = fs.getDirectory();
			numberFormat.setMinimumIntegerDigits(6);
			numberFormat.setGroupingUsed(false);
			myFrameSeq = fs;
			frameNumber = num;
		}

		@Override
		public void run() {
			while (running == true || myFrameSeq.getNumFrames() > 0) {
				movieMaker.Picture picture = myFrameSeq.removeFrame();

				if (picture == null) {
					continue;
				}
				String fileName = fileLocation + "frame" + numberFormat.format(frameNumber) + ".jpg";

				// set the file name
				picture.setFileName(fileName);

				// write out this frame
				picture.write(fileName);

				// increment count
				frameNumber++;
			}
		}

	}

	private class StartTimer implements Runnable {
		private JLabel label;

		private int timer = 0;

		public StartTimer(JLabel l) {
			label = l;
		}

		@Override
		public void run() {
			java.text.DecimalFormat det = new java.text.DecimalFormat("00");
			while (endCapturing != false) {
				if (getRunning()) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					timer++;
					int seconds = timer % 60;
					int minutes = timer / 60;
					label.setText(minutes + ":" + det.format(seconds) + "  ");
				}

			}
		}

	}

}