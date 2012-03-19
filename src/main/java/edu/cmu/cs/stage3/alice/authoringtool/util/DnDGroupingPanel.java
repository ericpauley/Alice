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
public class DnDGroupingPanel extends GroupingPanel {
	protected java.awt.datatransfer.Transferable transferable;
	protected DnDGrip grip = new DnDGrip();
	protected java.awt.dnd.DragSource dragSource = new java.awt.dnd.DragSource();
	protected java.awt.Point hotSpot = new java.awt.Point(0, 0);
	protected java.awt.dnd.DragSourceListener dndManagerListener = DnDManager.getInternalListener();
	protected edu.cmu.cs.stage3.alice.authoringtool.util.DragWindow dragWindow;
	protected edu.cmu.cs.stage3.awt.SemitransparentWindow dragWindow2;
	protected java.awt.Point dragOffset;
	protected int arcWidth = 12;
	protected int arcHeight = 10;
	protected GroupingPanelDragGestureListener dragGestureListener = new GroupingPanelDragGestureListener();
	protected java.util.LinkedList dragGestureRecognizers = new java.util.LinkedList(); // MEMFIX
	protected boolean dragEnabled = true;
	protected boolean drawFaded = false;
	protected java.awt.Composite defaultComposite = java.awt.AlphaComposite.SrcOver;
	protected java.awt.AlphaComposite alphaComposite = java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, .5f);
	protected boolean isSystemDefined = false;

	protected static edu.cmu.cs.stage3.alice.authoringtool.util.Configuration authoringToolConfig = edu.cmu.cs.stage3.alice.authoringtool.util.Configuration.getLocalConfiguration(edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.class.getPackage());

	public DnDGroupingPanel() {
		setLayout(new java.awt.BorderLayout(2, 2));
		// outerBorder = new
		// edu.cmu.cs.stage3.alice.authoringtool.border.PartialLineBorder(
		// java.awt.Color.lightGray, 1, true, true, true, true );
		// border = javax.swing.BorderFactory.createCompoundBorder( outerBorder,
		// innerBorder );
		// setBorder( border );
		setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
		setOpaque(false);

		add(grip, java.awt.BorderLayout.WEST);
		addDragSourceComponent(grip);
		addDragSourceComponent(this);
	}

	public java.awt.datatransfer.Transferable getTransferable() {
		return transferable;
	}

	public void setTransferable(java.awt.datatransfer.Transferable transferable) {
		this.transferable = transferable;
		if (transferable != null) {
			if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.PropertyReferenceTransferable.propertyReferenceFlavor)) {
				isSystemDefined = true;
			} else if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ResponsePrototypeReferenceTransferable.responsePrototypeReferenceFlavor) && !edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedResponsePrototypeReferenceTransferable.callToUserDefinedResponsePrototypeReferenceFlavor)) {
				isSystemDefined = true;
			} else if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.QuestionPrototypeReferenceTransferable.questionPrototypeReferenceFlavor) && !edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedQuestionPrototypeReferenceTransferable.callToUserDefinedQuestionPrototypeReferenceFlavor)) {
				isSystemDefined = true;
			} else if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CommonMathQuestionsTransferable.commonMathQuestionsFlavor)) {
				isSystemDefined = true;
			} else {
				isSystemDefined = false;
			}
		}
	}

	public boolean isDragEnabled() {
		return dragEnabled;
	}

	public void setDragEnabled(boolean b) {
		dragEnabled = b;
	}

	public void addDragSourceComponent(java.awt.Component component) {
		for (java.util.Iterator iter = dragGestureRecognizers.iterator(); iter.hasNext();) {
			java.awt.dnd.DragGestureRecognizer dgr = (java.awt.dnd.DragGestureRecognizer) iter.next();
			if (dgr.getComponent() == component) {
				return; // HACK
			}
		}
		if (dragSource != null) {
			dragGestureRecognizers.add(dragSource.createDefaultDragGestureRecognizer(component, java.awt.dnd.DnDConstants.ACTION_COPY_OR_MOVE | java.awt.dnd.DnDConstants.ACTION_LINK, dragGestureListener));
		} else {
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("dragSource is null", null);
		}
	}

	public void removeDragSourceComponent(java.awt.Component component) {
		for (java.util.ListIterator iter = dragGestureRecognizers.listIterator(); iter.hasNext();) {
			java.awt.dnd.DragGestureRecognizer dgr = (java.awt.dnd.DragGestureRecognizer) iter.next();
			if (dgr.getComponent() == component) {
				dgr.removeDragGestureListener(dragGestureListener);
				dgr.setComponent(null);
				iter.remove();
				break;
			}
		}
	}

	public java.awt.dnd.DragSource getDragSource() {
		return dragSource;
	}

	public void reset() {
		add(grip, java.awt.BorderLayout.WEST);
		addDragSourceComponent(grip);
		addDragSourceComponent(this);
	}

	@Override
	public void release() {
		super.release();
		// if( grip != null ) { //MEMFIX
		// remove( grip );
		// }
		// grip = null;
		// dragSource = null;
		// if( dragWindow != null ) {
		// dragWindow.setVisible( false );
		// dragWindow.dispose();
		// }
		// dragWindow = null;
		// if( dragWindow2 != null ) {
		// dragWindow2.hide();
		// }
		// dragWindow2 = null;
		for (java.util.Iterator iter = dragGestureRecognizers.listIterator(); iter.hasNext();) { // MEMFIX
			java.awt.dnd.DragGestureRecognizer dgr = (java.awt.dnd.DragGestureRecognizer) iter.next();
			if (dragGestureListener != null) {
				dgr.removeDragGestureListener(dragGestureListener);
				iter.remove();
			} else {
				// System.err.println( "dragGestureListener unexpectedly null"
				// );
				// Thread.dumpStack();
			}
			dgr.setComponent(null);
		}
		// dragGestureListener = null;
	}

	public java.awt.Image getImage() {
		java.awt.Rectangle bounds = getBounds();
		java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(bounds.width, bounds.height, java.awt.image.BufferedImage.TYPE_INT_ARGB);
		java.awt.Graphics2D g = image.createGraphics();
		paintAll(g);
		return image;
	}

	@Override
	public void paintComponent(java.awt.Graphics g) {
		// if( g instanceof java.awt.Graphics2D ) {
		// if( drawFaded ) {
		// ((java.awt.Graphics2D)g).setComposite( alphaComposite );
		// }
		// }

		super.paintComponent(g);
		Object oldAntialiasing = null;
		if (g instanceof java.awt.Graphics2D) {
			oldAntialiasing = ((java.awt.Graphics2D) g).getRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING);
			((java.awt.Graphics2D) g).addRenderingHints(new java.awt.RenderingHints(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON));
		}
		java.awt.Rectangle bounds = getBounds();

		g.setColor(getBackground());
		g.fillRoundRect(0, 0, bounds.width, bounds.height, arcWidth, arcHeight);
		g.setColor(java.awt.Color.lightGray);
		g.drawRoundRect(0, 0, bounds.width - 1, bounds.height - 1, arcWidth, arcHeight);

		if (g instanceof java.awt.Graphics2D) {
			((java.awt.Graphics2D) g).addRenderingHints(new java.awt.RenderingHints(java.awt.RenderingHints.KEY_ANTIALIASING, oldAntialiasing));
		}
	}

	@Override
	public void paintForeground(java.awt.Graphics g) {
		super.paintForeground(g);

		/*
		 * if( isSystemDefined && false ) { java.awt.Rectangle bounds =
		 * getBounds();
		 * 
		 * Object oldAntialiasing = null; if( g instanceof java.awt.Graphics2D )
		 * { oldAntialiasing = ((java.awt.Graphics2D)g).getRenderingHint(
		 * java.awt.RenderingHints.KEY_ANTIALIASING );
		 * ((java.awt.Graphics2D)g).addRenderingHints( new
		 * java.awt.RenderingHints( java.awt.RenderingHints.KEY_ANTIALIASING,
		 * java.awt.RenderingHints.VALUE_ANTIALIAS_ON ) ); }
		 * 
		 * int w = bounds.width; int h = bounds.height; int s = 7; g.setColor(
		 * new java.awt.Color( 220, 220, 220 ) ); g.fillPolygon( new int[] { 0,
		 * 0, s }, new int[] { s, 0, 0 }, 3 ); // upper left g.fillPolygon( new
		 * int[] { w - s, w, w }, new int[] { 0, 0, s }, 3 ); // upper right
		 * g.fillPolygon( new int[] { 0, 0, s }, new int[] { h - s, h, h }, 3 );
		 * // lower left g.fillPolygon( new int[] { w, w, w - s }, new int[] { h
		 * - s, h, h }, 3 ); // lower right w--; h--; s--; g.setColor( new
		 * java.awt.Color( 160, 160, 160 ) ); g.drawPolygon( new int[] { 0, 0, s
		 * }, new int[] { s, 0, 0 }, 3 ); // upper left g.drawPolygon( new int[]
		 * { w - s, w, w }, new int[] { 0, 0, s }, 3 ); // upper right
		 * g.drawPolygon( new int[] { 0, 0, s }, new int[] { h - s, h, h }, 3 );
		 * // lower left g.drawPolygon( new int[] { w, w, w - s }, new int[] { h
		 * - s, h, h }, 3 ); // lower right
		 * 
		 * if( g instanceof java.awt.Graphics2D ) {
		 * ((java.awt.Graphics2D)g).addRenderingHints( new
		 * java.awt.RenderingHints( java.awt.RenderingHints.KEY_ANTIALIASING,
		 * oldAntialiasing ) ); } }
		 */
	}

	// public void paintChildren( java.awt.Graphics g ) {
	// super.paintChildren( g );
	// if( g instanceof java.awt.Graphics2D ) {
	// ((java.awt.Graphics2D)g).setComposite( defaultComposite );
	// }
	// }

	@Override
	public void printComponent(java.awt.Graphics g) {
		if (!authoringToolConfig.getValue("printing.fillBackground").equalsIgnoreCase("true")) {
			Object oldAntialiasing = null;
			if (g instanceof java.awt.Graphics2D) {
				oldAntialiasing = ((java.awt.Graphics2D) g).getRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING);
				((java.awt.Graphics2D) g).addRenderingHints(new java.awt.RenderingHints(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON));
			}
			java.awt.Rectangle bounds = getBounds();

			g.setColor(java.awt.Color.lightGray);
			g.drawRoundRect(0, 0, bounds.width - 1, bounds.height - 1, arcWidth, arcHeight);

			if (g instanceof java.awt.Graphics2D) {
				((java.awt.Graphics2D) g).addRenderingHints(new java.awt.RenderingHints(java.awt.RenderingHints.KEY_ANTIALIASING, oldAntialiasing));
			}
		} else {
			super.printComponent(g);
		}
	}

	public class GroupingPanelDragGestureListener implements java.awt.dnd.DragGestureListener {
		protected class DragListener extends java.awt.event.MouseAdapter implements java.awt.event.MouseMotionListener {

			@Override
			public void mouseReleased(java.awt.event.MouseEvent ev) {
				drawFaded = false;
				DnDGroupingPanel.this.repaint();
				if (authoringToolConfig.getValue("gui.useAlphaTiles").equalsIgnoreCase("true") && dragWindow2 != null) {
					dragWindow2.hide();
					edu.cmu.cs.stage3.awt.AWTUtilities.pumpMessageQueue();
				} else if (dragWindow != null) {
					dragWindow.setVisible(false);
				}
				edu.cmu.cs.stage3.awt.AWTUtilities.removeMouseListener(this);
				edu.cmu.cs.stage3.awt.AWTUtilities.removeMouseMotionListener(this);
				DnDManager.removeListener(dragSourceListener);
			}

			@Override
			public void mouseDragged(java.awt.event.MouseEvent ev) {
				java.awt.Point p = ev.getPoint();
				p.x += 5; // -= dragOffset.x; Aik Min commented this. Mouse
							// cursor (arrow) is now at the top left of a tile.
				p.y += 5; // -= dragOffset.y;
				if (authoringToolConfig.getValue("gui.useAlphaTiles").equalsIgnoreCase("true") && dragWindow2 != null) {
					if (authoringToolConfig.getValue("gui.pickUpTiles").equalsIgnoreCase("false")) {
						dragWindow2.hide();
					} else {
						dragWindow2.show();
					}
					dragWindow2.setLocationOnScreen(p.x, p.y);
					edu.cmu.cs.stage3.awt.AWTUtilities.pumpMessageQueue();
				} else if (dragWindow != null) {
					if (authoringToolConfig.getValue("gui.pickUpTiles").equalsIgnoreCase("false")) {
						dragWindow.setVisible(false);
					} else {
						dragWindow.setVisible(true);
					}
					dragWindow.setLocation(p);
				}
			}

			@Override
			public void mouseMoved(java.awt.event.MouseEvent ev) {
			}
		}
		protected DragListener dragListener = new DragListener();

		protected class DragSourceListener implements edu.cmu.cs.stage3.alice.authoringtool.util.event.DnDManagerListener {
			@Override
			public void dragEnter(java.awt.dnd.DragSourceDragEvent ev) {
				updateImages(true);
			}

			@Override
			public void dragExit(java.awt.dnd.DragSourceEvent ev) {
				updateImages(false);
			}

			@Override
			public void dragDropEnd(java.awt.dnd.DragSourceDropEvent ev) {
			}
			@Override
			public void dragOver(java.awt.dnd.DragSourceDragEvent ev) {
			}
			@Override
			public void dropActionChanged(java.awt.dnd.DragSourceDragEvent ev) {
			}
			@Override
			public void dragGestureRecognized(java.awt.dnd.DragGestureEvent ev) {
			}
			@Override
			public void dragStarted() {
			}
		}
		protected DragSourceListener dragSourceListener = new DragSourceListener();

		@Override
		public void dragGestureRecognized(java.awt.dnd.DragGestureEvent dge) {
			if (transferable != null) {
				DnDManager.fireDragGestureRecognized(dge);
				try {
					if (dragEnabled) {
						dge.startDrag(java.awt.dnd.DragSource.DefaultCopyDrop, transferable, DnDManager.getInternalListener());
						DnDManager.fireDragStarted(transferable, DnDGroupingPanel.this);

						if (authoringToolConfig.getValue("gui.pickUpTiles").equalsIgnoreCase("true") && edu.cmu.cs.stage3.awt.AWTUtilities.mouseListenersAreSupported() && edu.cmu.cs.stage3.awt.AWTUtilities.mouseMotionListenersAreSupported()) {
							if (authoringToolConfig.getValue("gui.useAlphaTiles").equalsIgnoreCase("true") && edu.cmu.cs.stage3.awt.SemitransparentWindow.isSupported()) {
								if (dragWindow2 == null) {
									dragWindow2 = new edu.cmu.cs.stage3.awt.SemitransparentWindow();
								}
								dragWindow2.show();
							} else {
								if (dragWindow == null) {
									dragWindow = new edu.cmu.cs.stage3.alice.authoringtool.util.DragWindow(edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getJAliceFrame());
								}
								dragWindow.setLocation(-10000, -10000); // hack
								dragWindow.setVisible(true);
							}

							boolean scaledAndCropped = updateImages(false);

							if (scaledAndCropped) {
								dragOffset = new java.awt.Point(3, 3);
							} else {
								dragOffset = dge.getDragOrigin();
								dragOffset = javax.swing.SwingUtilities.convertPoint(dge.getComponent(), dragOffset, DnDGroupingPanel.this);
							}
							edu.cmu.cs.stage3.awt.AWTUtilities.addMouseListener(dragListener);
							edu.cmu.cs.stage3.awt.AWTUtilities.addMouseMotionListener(dragListener);
							DnDManager.addListener(dragSourceListener);
						}

						drawFaded = true;
						DnDGroupingPanel.this.repaint();
					}
				} catch (Throwable t) {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("Error initiating drag of tile.", t);
				}
			}
		}

		private boolean updateImages(boolean valid) {
			java.awt.Image tileImage = getImage();
			boolean scaledAndCropped = false;

			if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.responseReferenceFlavor) || edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReferenceFlavorForClass(edu.cmu.cs.stage3.alice.core.question.userdefined.Component.class))) {
				int width = tileImage.getWidth(GUIEffects.sizeObserver);
				int height = tileImage.getHeight(GUIEffects.sizeObserver);

				if (width > 64 || height > 64) {
					double scaleFactor = 1.0;
					java.awt.Rectangle cropRect = new java.awt.Rectangle(0, 0, 64, 64);
					if (width > 128 && height > 128) { // if both dimensions are
														// > 128, scale by 0.5
						scaleFactor = 0.5;
					} else if (height < 32) { // if it looks like a one-liner,
												// crop at 128
						cropRect = new java.awt.Rectangle(0, 0, 128, height);
					} else if (width > 128 || height > 128) { // if only one
																// dimension is
																// > 128, scale
																// smaller
																// dimension
																// down to 64,
																// but ignore if
																// smaller
																// dimension is
																// < 64
						scaleFactor = Math.min(1.0, 64.0 / Math.min(width, height));
					} else { // if both are < 128, let it slide...
						cropRect = new java.awt.Rectangle(0, 0, width, height);
					}
					tileImage = GUIEffects.getImageScaledAndCropped(tileImage, scaleFactor, cropRect);
					scaledAndCropped = true;
				}
			}

			if (valid) {
				tileImage = GUIEffects.getImageWithColoredBorder(tileImage, edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("dndHighlight2"));
			} else {
				tileImage = GUIEffects.getImageWithColoredBorder(tileImage, edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("dndHighlight3"));
			}

			if (authoringToolConfig.getValue("gui.useAlphaTiles").equalsIgnoreCase("true") && dragWindow2 != null) {
				java.awt.Image image = GUIEffects.getImageWithDropShadow(tileImage, 8, 8, arcWidth, arcHeight);
				try {
					dragWindow2.setImage(image);
				} catch (InterruptedException ie) {
					throw new RuntimeException();
				}
			} else if (dragWindow != null) {
				dragWindow.setImage(tileImage);
			}

			return scaledAndCropped;
		}
	}

	public class DnDGrip extends javax.swing.JComponent {
		protected java.awt.Color highlightColor = javax.swing.plaf.metal.MetalLookAndFeel.getControlHighlight();
		protected java.awt.Color shadowColor = javax.swing.plaf.metal.MetalLookAndFeel.getControlDarkShadow();

		public DnDGrip() {
			setMinimumSize(new java.awt.Dimension(6, 0));
			setMaximumSize(new java.awt.Dimension(6, Integer.MAX_VALUE));
			setPreferredSize(new java.awt.Dimension(6, 0));
		}

		@Override
		protected void printComponent(java.awt.Graphics g) {
			// do nothing
		}

		@Override
		protected void paintComponent(java.awt.Graphics g) {
			java.awt.Dimension size = getSize();

			g.setColor(highlightColor);
			for (int x = 0; x < size.width; x += 4) {
				for (int y = 0; y < size.height; y += 4) {
					g.drawLine(x, y, x, y);
					g.drawLine(x + 2, y + 2, x + 2, y + 2);
				}
			}

			g.setColor(shadowColor);
			for (int x = 0; x < size.width; x += 4) {
				for (int y = 0; y < size.height; y += 4) {
					g.drawLine(x + 1, y + 1, x + 1, y + 1);
					g.drawLine(x + 3, y + 3, x + 3, y + 3);
				}
			}
		}
	}

}