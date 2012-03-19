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

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.event.ChildrenListener;

/**
 * @author Jason Pratt
 */
public abstract class PropertyViewController extends edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel implements edu.cmu.cs.stage3.alice.authoringtool.util.GUIElement, edu.cmu.cs.stage3.alice.authoringtool.util.Releasable {
	protected static RefreshThread refreshThread = new RefreshThread();
	protected static java.util.Set propertyViewControllersToRefresh = new java.util.HashSet();

	public static int created = 0;
	public static int released = 0;

	static {
		refreshThread.start();
	}

	public static RefreshThread getRefreshThread() {
		return refreshThread;
	}

	protected edu.cmu.cs.stage3.alice.core.Property property;
	protected boolean allowExpressions;
	protected boolean includeDefaults;
	protected boolean includeOther;
	protected boolean omitPropertyName;
	protected static boolean handlingQuestionAlready = false;
	/**
	 * used both for popup actions and gui-initiated property sets. should take
	 * a target value and return a Runnable.
	 */
	protected edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory factory;
	private boolean popupEnabled = false;
	protected boolean editingEnabled = true;
	protected boolean sleeping = false;
	protected java.util.Vector popupStructure;
	protected javax.swing.JButton popupButton = new javax.swing.JButton(new javax.swing.ImageIcon(edu.cmu.cs.stage3.alice.authoringtool.JAlice.class.getResource("images/popupArrow.gif")));
	protected edu.cmu.cs.stage3.alice.core.event.PropertyListener propertyListener = new edu.cmu.cs.stage3.alice.core.event.PropertyListener() {
		protected long lastTime = System.currentTimeMillis();
		@Override
		public void propertyChanging(edu.cmu.cs.stage3.alice.core.event.PropertyEvent ev) {
			// stop listening to name changes on the value of this property
			if (ev.getProperty() == property) {
				if (ev.getValue() instanceof edu.cmu.cs.stage3.alice.core.Element) {
					((edu.cmu.cs.stage3.alice.core.Element) ev.getValue()).name.removePropertyListener(this);
				}
			}
		}
		@Override
		public void propertyChanged(edu.cmu.cs.stage3.alice.core.event.PropertyEvent ev) {
			// start listening to name changes on the value of this property
			if (ev.getProperty() == property) {
				if (ev.getValue() instanceof edu.cmu.cs.stage3.alice.core.Element) {
					((edu.cmu.cs.stage3.alice.core.Element) ev.getValue()).name.addPropertyListener(this);
				}
			}

			// mark dirty
			synchronized (propertyViewControllersToRefresh) {
				PropertyViewController.propertyViewControllersToRefresh.add(PropertyViewController.this);
			}

			// PropertyViewController.this.refreshGUI();

			// PropertyViewController.this.refreshThread.markDirty();
			// PropertyViewController.this.cleanOutPropertyValueQuestions();

			// HACK
			// if( ev.getValue() instanceof
			// edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion
			// ) {
			// edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion
			// call =
			// (edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion)ev.getValue();
			// if( call.getParent() != ev.getProperty().getOwner() ) {
			// ev.getProperty().getOwner().addChild( call );
			// call.data.put( "PropertyViewController_propertyOwner",
			// ev.getProperty().getName() );
			// }
			// } else if( ev.getValue() instanceof
			// edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse )
			// {
			// edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse
			// call =
			// (edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse)ev.getValue();
			// if( call.getParent() != ev.getProperty().getOwner() ) {
			// ev.getProperty().getOwner().addChild( call );
			// call.data.put( "PropertyViewController_propertyOwner",
			// ev.getProperty().getName() );
			// }
			// }
		}
	};
	protected QuestionDeletionListener questionDeletionListener = new QuestionDeletionListener();
	protected javax.swing.JLabel expressionLabel = new javax.swing.JLabel();
	protected javax.swing.JComponent questionViewController;
	protected boolean beingDroppedOn = false;
	protected java.awt.Color dndHighlightColor = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("dndHighlight");
	protected java.awt.Color dndHighlightColor2 = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("dndHighlight2");
	protected boolean paintDropPotential = false;
	protected DropPotentialFeedbackListener dropPotentialFeedbackListener = new DropPotentialFeedbackListener();
	protected javax.swing.JLabel unitLabel = new javax.swing.JLabel();
	// protected edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel
	// rightPanel = new
	// edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel();
	protected javax.swing.JPanel rightPanel = new edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel() {

		@Override
		public void release() {
			// do nothing
		}
	};

	public PropertyViewController() {
		PropertyViewController.created++;

		setLayout(new java.awt.BorderLayout(0, 0));
		setBackground(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("propertyViewControllerBackground"));
		setBorder(javax.swing.BorderFactory.createCompoundBorder(getBorder(), javax.swing.BorderFactory.createEmptyBorder(0, 2, 0, 0)));
		popupButton.setContentAreaFilled(false);
		popupButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
		popupButton.setFocusPainted(false);
		popupButton.setBorderPainted(false);
		popupButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent ev) {
				if (editingEnabled) {
					PropertyViewController.this.updatePopupStructure();
					PropertyViewController.this.triggerPopup(ev);
				}
			}
		});
		addMouseListener(getMouseListener());
		// refreshThread.start();
		// refreshThread.setPriority( Thread.NORM_PRIORITY - 1 );

		// unit stuff is a big hack right now
		unitLabel.setFont(unitLabel.getFont().deriveFont(java.awt.Font.PLAIN));
		rightPanel.setOpaque(false);
		rightPanel.setLayout(new java.awt.BorderLayout());
		rightPanel.setBorder(null);
		rightPanel.removeContainerListener(edu.cmu.cs.stage3.alice.authoringtool.util.GUIElementContainerListener.getStaticListener());
		// rightPanel.addContainerListener(
		// new java.awt.event.ContainerAdapter() {
		// public void componentAdded( java.awt.event.ContainerEvent ev ) {
		// if( ev.getChild().getDropTarget() == null ) { // is this
		// heavy-handed?
		// ev.getChild().setDropTarget( new java.awt.dnd.DropTarget(
		// ev.getChild(), rightPanel ) );
		// }
		// }
		// public void componentRemoved( java.awt.event.ContainerEvent ev ) {
		// //MEMFIX
		// if( ev.getChild().getDropTarget() != null ) { // is this
		// heavy-handed?
		// ev.getChild().getDropTarget().setActive( false );
		// ev.getChild().setDropTarget( null );
		// }
		// }
		// }
		// );
	}

	protected javax.swing.JLabel nameLabel = new javax.swing.JLabel();

	public void set(edu.cmu.cs.stage3.alice.core.Property property, boolean includeDefaults, boolean allowExpressions, boolean includeOther, boolean omitPropertyName, edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory factory) {
		clean();
		this.property = property;
		this.includeDefaults = includeDefaults;
		this.allowExpressions = allowExpressions;
		this.includeOther = includeOther;
		this.omitPropertyName = omitPropertyName;
		this.factory = factory;
		if (!omitPropertyName) {
			nameLabel = new javax.swing.JLabel(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(property, false) + " = ");
			int fontSize = Integer.parseInt(authoringToolConfig.getValue("fontSize"));
			nameLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.ITALIC | java.awt.Font.BOLD, (int) (12 * fontSize / 12.0)));
			add(nameLabel, java.awt.BorderLayout.WEST);
		}

		setPopupEnabled(true);

		if (!sleeping) {
			startListening();
			if (property.get() instanceof edu.cmu.cs.stage3.alice.core.Element) {
				((edu.cmu.cs.stage3.alice.core.Element) property.get()).name.addPropertyListener(propertyListener);
			}
		}
	}

	public edu.cmu.cs.stage3.alice.core.Property getProperty() {
		return property;
	}

	protected void getLabels(java.awt.Component c, java.util.Vector v) {
		if (c instanceof javax.swing.JLabel) {
			if (c.isVisible()) {
				if (!(c == nameLabel && omitPropertyName)) {
					// System.out.println("label: "+((javax.swing.JLabel)c).getText()+", "+(c==nameLabel)+", "+omitPropertyName);
					v.add(c);
				}
			}
		} else if (c instanceof java.awt.Container) {
			java.awt.Container containerC = (java.awt.Container) c;
			for (int i = 0; i < containerC.getComponentCount(); i++) {
				getLabels(containerC.getComponent(i), v);
			}
		}
	}

	protected String getHTMLColorString(java.awt.Color color) {
		String r = Integer.toHexString(color.getRed());
		String g = Integer.toHexString(color.getGreen());
		String b = Integer.toHexString(color.getBlue());

		if (r.length() == 1) {
			r = "0" + r;
		}
		if (g.length() == 1) {
			g = "0" + g;
		}
		if (b.length() == 1) {
			b = "0" + b;
		}
		return new String("#" + r + g + b);
	}

	public void getHTML(StringBuffer toWriteTo) {
		String tempString = "";

		// ((java.awt.BorderLayout )this.getLayout()).
		// this.doLayout();
		boolean isNativeComponent = true;
		for (int i = 0; i < getComponentCount(); i++) {
			if (getComponent(i) == getNativeComponent()) {
				break;
			}
			if (getComponent(i) == expressionLabel) {
				isNativeComponent = false;
				break;
			}
			if (getComponent(i) instanceof edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel) {
				toWriteTo.append("( " + edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getHTMLStringForComponent(getComponent(i)) + " )");
				return;
			}
		}
		tempString += "<span style=\"background-color: " + getHTMLColorString(getBackground()) + "\">";
		if (!omitPropertyName) {
			tempString += edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(property, false) + " = ";
		}
		if (isNativeComponent) {
			tempString += edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getHTMLStringForComponent(getNativeComponent());
		} else {
			tempString += edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getHTMLStringForComponent(expressionLabel);
		}
		if (unitLabel.getParent() != null) {
			tempString += " " + unitLabel.getText();
		}
		tempString += "</span>";
		// java.util.Vector labels = new java.util.Vector();
		// getLabels(this, labels);
		// java.util.Collections.sort(labels, new java.util.Comparator(){
		// public int compare(Object a, Object b){
		// javax.swing.JLabel labelA = (javax.swing.JLabel)a;
		// javax.swing.JLabel labelB = (javax.swing.JLabel)b;
		// System.out.println("a: "+labelA.getX()+", b: "+labelB.getX());
		// if (labelA.getX() == labelB.getX()){
		// return 0;
		// }
		// if (labelA.getX() < labelB.getX()){
		// return -1;
		// } else{
		// return 1;
		// }
		//
		// }
		//
		// public boolean equals(Object a){
		// return a == this;
		// }
		// });
		// for (int i=0; i<labels.size(); i++){
		// javax.swing.JLabel current = (javax.swing.JLabel)labels.get(i);
		// tempString += current.getText()+" ";
		// }
		// tempString =
		// edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.cleanHTMLString(tempString);
		toWriteTo.append(tempString);
	}

	@Override
	public void goToSleep() {
		stopListening();
		// refreshThread.pause();
		sleeping = true;
	}

	@Override
	public void wakeUp() {
		// refreshThread.unpause();
		startListening();
		sleeping = false;
	}

	@Override
	public void die() {
		clean();
		// refreshThread.halt();
	}

	@Override
	public void release() {
		// if( getParent() != null ) {
		// getParent().remove( this );
		// }
		edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.releaseGUI(this);
		PropertyViewController.released++;
	}

	public boolean isPopupEnabled() {
		return popupEnabled;
	}

	public void setPopupEnabled(boolean popupEnabled) {
		if (popupEnabled != this.popupEnabled) {
			this.popupEnabled = popupEnabled;
			if (popupEnabled) {
				if (editingEnabled) {
					// this.add( popupButton, java.awt.BorderLayout.EAST );
					rightPanel.add(popupButton, java.awt.BorderLayout.EAST); // unit
																				// hack
				}
			} else {
				// this.remove( popupButton );
				rightPanel.remove(popupButton); // unit hack
			}
		}
	}

	public void setEditingEnabled(boolean editingEnabled) {
		if (this.editingEnabled != editingEnabled) {
			this.editingEnabled = editingEnabled;
			if (!editingEnabled) {
				if (popupEnabled) {
					// this.remove( popupButton );
					rightPanel.remove(popupButton); // unit hack
				}
			} else {
				if (popupEnabled) {
					// this.add( popupButton, java.awt.BorderLayout.EAST );
					rightPanel.add(popupButton, java.awt.BorderLayout.EAST); // unit
																				// hack
				}
			}
		}
	}

	public boolean isEditingEnabled() {
		return editingEnabled;
	}

	protected void startListening() {
		if (property != null) {
			property.addPropertyListener(propertyListener);
			property.getOwner().addChildrenListener(questionDeletionListener);
		}
		edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager.addListener(dropPotentialFeedbackListener);
	}

	protected void stopListening() {
		if (property != null) {
			property.removePropertyListener(propertyListener);
			property.getOwner().removeChildrenListener(questionDeletionListener);
		}
		edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager.removeListener(dropPotentialFeedbackListener);
	}

	@Override
	public void clean() {
		stopListening();
		cleanOutPropertyValueQuestions();
		removeAll();
		popupEnabled = false;
	}

	protected java.awt.event.MouseListener getMouseListener() {
		return new java.awt.event.MouseAdapter() {

			@Override
			public void mouseReleased(java.awt.event.MouseEvent ev) {
				if (ev.getX() >= 0 && ev.getX() < ev.getComponent().getWidth() && ev.getY() >= 0 && ev.getY() < ev.getComponent().getHeight()) {
					if (isEnabled()) {
						popupButton.doClick();
					}
				}
			}
		};
	}

	protected void refreshGUI() {
		add(rightPanel, java.awt.BorderLayout.EAST); // unit hack
		// expressionLabel.setForeground( new java.awt.Color( 99, 99, 156 ) );
		// // hack for red, unfinished look
		expressionLabel.setForeground(new java.awt.Color(0, 0, 0)); // Aik Min
																	// changed
																	// this to
																	// black.
																	// See bug
																	// report
																	// #13.
		Object value = property.get();
		if (value instanceof edu.cmu.cs.stage3.alice.core.question.AbstractScriptDefinedObject) {
			if (isAncestorOf(getNativeComponent())) {
				remove(getNativeComponent());
			} else if (questionViewController != null && isAncestorOf(questionViewController)) {
				remove(questionViewController);
			}
			expressionLabel.setText("`" + ((edu.cmu.cs.stage3.alice.core.question.AbstractScriptDefinedObject) value).evalScript.getStringValue() + "`");
			if (!isAncestorOf(expressionLabel)) {
				add(expressionLabel, java.awt.BorderLayout.CENTER);
			}
		} else if (value instanceof edu.cmu.cs.stage3.alice.core.question.PropertyValue) {
			if (isAncestorOf(getNativeComponent())) {
				remove(getNativeComponent());
			} else if (questionViewController != null && isAncestorOf(questionViewController)) {
				remove(questionViewController);
			}
			// String propertyName =
			// ((edu.cmu.cs.stage3.alice.core.question.PropertyValue)value).propertyName.getStringValue();
			// edu.cmu.cs.stage3.alice.core.Element element =
			// ((edu.cmu.cs.stage3.alice.core.question.PropertyValue)value).element.getElementValue();
			// edu.cmu.cs.stage3.alice.core.Property p =
			// element.getPropertyNamed( propertyName );
			// expressionLabel.setText(
			// edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(
			// p, true ) );
			// expressionLabel.setText(
			// "BLAHAHA"+edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(
			// value, true ) );
			// if( ! this.isAncestorOf( expressionLabel ) ) {
			// add( expressionLabel, java.awt.BorderLayout.CENTER );
			// }
			questionViewController = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getGUI(value);
			add(questionViewController, java.awt.BorderLayout.CENTER);
		} else if (value instanceof edu.cmu.cs.stage3.alice.core.Question) {
			if (isAncestorOf(getNativeComponent())) {
				remove(getNativeComponent());
			} else if (questionViewController != null && isAncestorOf(questionViewController)) {
				remove(questionViewController);
			} else if (isAncestorOf(expressionLabel)) {
				remove(expressionLabel);
			}
			questionViewController = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getGUI(value);
			add(questionViewController, java.awt.BorderLayout.CENTER);
		} else if (value instanceof edu.cmu.cs.stage3.alice.core.Expression) {
			if (isAncestorOf(getNativeComponent())) {
				remove(getNativeComponent());
			} else if (questionViewController != null && isAncestorOf(questionViewController)) {
				remove(questionViewController);
			}
			expressionLabel.setText(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(value, property, property.getOwner().data));
			if (!isAncestorOf(expressionLabel)) {
				add(expressionLabel, java.awt.BorderLayout.CENTER);
			}
		} else if (value == null) {
			if (isAncestorOf(getNativeComponent())) {
				remove(getNativeComponent());
			} else if (questionViewController != null && isAncestorOf(questionViewController)) {
				remove(questionViewController);
			}
			// if( edu.cmu.cs.stage3.alice.core.Response.class.isAssignableFrom(
			// property.getValueClass() ) ) { // hack for red, unfinished look
			expressionLabel.setForeground(new java.awt.Color(200, 30, 30));
			// }
			expressionLabel.setText(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(null, property, property.getOwner().data));
			if (!isAncestorOf(expressionLabel)) {
				add(expressionLabel, java.awt.BorderLayout.CENTER);
			}
		} else if (getNativeClass().isAssignableFrom(value.getClass())) {
			if (isAncestorOf(expressionLabel)) {
				remove(expressionLabel);
			} else if (questionViewController != null && isAncestorOf(questionViewController)) {
				remove(questionViewController);
			}
			updateNativeComponent();
			if (!isAncestorOf(getNativeComponent())) {
				add(getNativeComponent(), java.awt.BorderLayout.CENTER);
			}
		} else {
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("Bad value: " + value, null);
		}

		// HACK; put units in PLAIN font style
		javax.swing.JLabel mainLabel = null;
		if (isAncestorOf(expressionLabel)) {
			mainLabel = expressionLabel;
		} else if (getNativeComponent() instanceof javax.swing.JLabel && isAncestorOf(getNativeComponent())) {
			mainLabel = (javax.swing.JLabel) getNativeComponent();
		}
		if (mainLabel != null) {
			String mainString = mainLabel.getText();
			if (mainString == null) {
				mainString = "";
			}
			// Aik Min - fix print and comment
			if (!(property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.response.Comment) && !(property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.response.Print)) {
				String unitString = null;
				java.util.Collection unitMapValues = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getUnitMapValues();
				for (java.util.Iterator iter = unitMapValues.iterator(); iter.hasNext();) {
					String s = (String) iter.next();
					if (mainString.endsWith(" " + s)) {
						unitString = s;
						break;
					}
				}
				if (unitString != null) {
					mainLabel.setText(mainString.substring(0, mainString.length() - unitString.length()));
					unitLabel.setText(unitString);
					if (!isAncestorOf(unitLabel)) {
						rightPanel.add(unitLabel, java.awt.BorderLayout.CENTER);
					}
				} else {
					if (isAncestorOf(unitLabel)) {
						rightPanel.remove(unitLabel);
					}
				}
			}
		} else {
			if (isAncestorOf(unitLabel)) {
				rightPanel.remove(unitLabel);
			}
		}

		// revalidate();
		// repaint();
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				revalidate();
				repaint();
			}
		});
	}

	protected abstract java.awt.Component getNativeComponent();
	protected abstract Class getNativeClass();
	protected abstract void updateNativeComponent();

	protected void updatePopupStructure() {
		popupStructure = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makePropertyStructure(property, factory, includeDefaults, allowExpressions, includeOther, null);
	}

	public void triggerPopup(java.awt.event.ActionEvent ev) {
		if (popupStructure != null) {
			if (isEnabled()) {
				edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.createAndShowPopupMenu(popupStructure, this, 0, getHeight());
			}
		}
	}

	@Override
	public void paintForeground(java.awt.Graphics g) {
		super.paintForeground(g);
		if (beingDroppedOn && editingEnabled) {
			java.awt.Dimension size = getSize();
			g.setColor(dndHighlightColor2);
			g.drawRect(0, 0, size.width - 1, size.height - 1);
			g.drawRect(1, 1, size.width - 3, size.height - 3);
		} else if (paintDropPotential && editingEnabled) {
			java.awt.Dimension size = getSize();
			g.setColor(dndHighlightColor);
			g.drawRect(0, 0, size.width - 1, size.height - 1);
			g.drawRect(1, 1, size.width - 3, size.height - 3);
		}
	}

	protected void cleanOutPropertyValueQuestions() {
		if (property != null) {
			edu.cmu.cs.stage3.alice.core.Element[] children = property.getOwner().getChildren();
			for (Element element : children) {
				if (element instanceof edu.cmu.cs.stage3.alice.core.question.PropertyValue) {
					edu.cmu.cs.stage3.alice.core.question.PropertyValue pv = (edu.cmu.cs.stage3.alice.core.question.PropertyValue) element;
					if (pv.data.get("createdByPropertyViewController") != null && pv.data.get("createdByPropertyViewController").equals("true")) {
						if (property.getOwner().getPropertyReferencesTo(pv, edu.cmu.cs.stage3.util.HowMuch.INSTANCE, false, false).length == 0) {
							property.getOwner().removeChild(pv);
						}
					}
				}
			}
		}
	}

	protected boolean checkTransferable(java.awt.datatransfer.Transferable transferable) {
		if (transferable != null) {
			Class desiredValueClass = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.getDesiredValueClass(property);
			try {
				/*
				 * if(
				 * edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources
				 * .safeIsDataFlavorSupported(transferable,
				 * edu.cmu.cs.stage3.alice
				 * .authoringtool.datatransfer.ElementReferenceTransferable
				 * .questionReferenceFlavor ) &&
				 * PropertyViewController.this.allowExpressions ) {
				 * edu.cmu.cs.stage3.alice.core.Question question =
				 * (edu.cmu.cs.stage3
				 * .alice.core.Question)transferable.getTransferData(
				 * edu.cmu.cs.stage3.alice.authoringtool.datatransfer.
				 * ElementReferenceTransferable.questionReferenceFlavor ); if(
				 * desiredValueClass.isAssignableFrom( question.getValueClass()
				 * ) ) { return true; } else if( Boolean.class.isAssignableFrom(
				 * desiredValueClass ) ) { return true; } else if(
				 * Number.class.isAssignableFrom( desiredValueClass ) &&
				 * javax.vecmath.Vector3d.class.isAssignableFrom(
				 * question.getValueClass() ) ) { return true; } else if(
				 * edu.cmu.cs.stage3.alice.core.Response.class.isAssignableFrom(
				 * desiredValueClass ) ) { return true; }
				 * 
				 * } else
				 */if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.variableReferenceFlavor) && PropertyViewController.this.allowExpressions) {
					java.util.List accessibleExpressions = new java.util.ArrayList(java.util.Arrays.asList(property.getOwner().findAccessibleExpressions(Object.class)));
					// System.out.println("owner: "+property.getOwner()+", root: "+property.getOwner().getRoot());
					// for (int i=0; i<accessibleExpressions.size(); i++ ){
					// System.out.println(accessibleExpressions.get(i));
					// }
					// System.out.println();
					// if (property.getOwner() !=
					// property.getOwner().getRoot()){
					// java.util.List worldExpressions =
					// java.util.Arrays.asList(
					// property.getOwner().getRoot().findAccessibleExpressions(
					// Object.class ) );
					// for (int i=0; i<worldExpressions.size(); i++ ){
					// System.out.println(worldExpressions.get(i));
					// }
					// if (worldExpressions != null){
					// accessibleExpressions.addAll(worldExpressions);
					// }
					// }
					// System.out.println("\n\n");
					edu.cmu.cs.stage3.alice.core.Variable variable = (edu.cmu.cs.stage3.alice.core.Variable) transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.variableReferenceFlavor);

					if (accessibleExpressions.contains(variable)) {
						java.util.Vector propertyValueStructure = new java.util.Vector();
						if (edu.cmu.cs.stage3.alice.core.Element.class.isAssignableFrom(variable.getValueClass())) {
							propertyValueStructure = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makePropertyValueStructure(variable, desiredValueClass, factory, property.getOwner());
						}

						if (edu.cmu.cs.stage3.alice.core.List.class.isAssignableFrom(variable.getValueClass())) {
							edu.cmu.cs.stage3.alice.core.List list = (edu.cmu.cs.stage3.alice.core.List) variable.getValue();
							edu.cmu.cs.stage3.alice.core.Element parent = property.getOwner().getParent();
							edu.cmu.cs.stage3.alice.core.reference.PropertyReference[] references = new edu.cmu.cs.stage3.alice.core.reference.PropertyReference[0];
							if (parent != null) {
								references = parent.getPropertyReferencesTo(property.getOwner(), edu.cmu.cs.stage3.util.HowMuch.INSTANCE, false);
							}

							// if this is a ListQuestion's list, only accept if
							// the list's valueClass matches the valueClass of
							// the property that the ListQuestion is within
							if (property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.question.list.ListObjectQuestion && references.length > 0) {
								final Class itemValueClass = references[0].getProperty().getValueClass();
								if (list != null && itemValueClass.isAssignableFrom(list.valueClass.getClassValue())) {
									return true;
								}
							} else {
								if (list != null && desiredValueClass.isAssignableFrom(list.valueClass.getClassValue())) {
									return true;
								} else if (desiredValueClass.isAssignableFrom(edu.cmu.cs.stage3.alice.core.List.class)) {
									return true;
								} else if (java.lang.Number.class.isAssignableFrom(desiredValueClass)) {
									return true;
								} else if (java.lang.Boolean.class.isAssignableFrom(desiredValueClass)) {
									return true;
								}
							}
						} else if (edu.cmu.cs.stage3.alice.core.Array.class.isAssignableFrom(variable.getValueClass())) {
							edu.cmu.cs.stage3.alice.core.Array array = (edu.cmu.cs.stage3.alice.core.Array) variable.getValue();
							edu.cmu.cs.stage3.alice.core.Element parent = property.getOwner().getParent();
							edu.cmu.cs.stage3.alice.core.reference.PropertyReference[] references = new edu.cmu.cs.stage3.alice.core.reference.PropertyReference[0];
							if (parent != null) {
								references = parent.getPropertyReferencesTo(property.getOwner(), edu.cmu.cs.stage3.util.HowMuch.INSTANCE, false);
							}

							// if this is an ArrayQuestion's list, only accept
							// if the array's valueClass matches the valueClass
							// of the property that the ArrayQuestion is within
							if (property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.question.array.ArrayObjectQuestion && references.length > 0) {
								final Class itemValueClass = references[0].getProperty().getValueClass();
								if (array != null && itemValueClass.isAssignableFrom(array.valueClass.getClassValue())) {
									return true;
								}
							} else {
								if (array != null && desiredValueClass.isAssignableFrom(array.valueClass.getClassValue())) {
									return true;
								} else if (desiredValueClass.isAssignableFrom(edu.cmu.cs.stage3.alice.core.Array.class)) {
									return true;
								} else if (java.lang.Number.class.isAssignableFrom(desiredValueClass)) {
									return true;
								}
							}
						} else if (desiredValueClass.isAssignableFrom(variable.getValueClass())) {
							return true;
						} else if (desiredValueClass.isAssignableFrom(variable.getClass())) {
							return true;
						} else if (Boolean.class.isAssignableFrom(desiredValueClass)) {
							return true;
						} else if (Number.class.isAssignableFrom(desiredValueClass) && javax.vecmath.Vector3d.class.isAssignableFrom(variable.getValueClass())) {
							return true;
						} else if (edu.cmu.cs.stage3.alice.core.Response.class.isAssignableFrom(desiredValueClass)) {
							return true;
						} else if (!propertyValueStructure.isEmpty()) {
							return true;
						}
					}
				} else if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.responseReferenceFlavor)) {
					if (property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.response.Print || property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Print) {
						return false;
					}
					if (desiredValueClass.isAssignableFrom(edu.cmu.cs.stage3.alice.core.Response.class)) {
						return true;
					}
				} else if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.elementReferenceFlavor)) {
					edu.cmu.cs.stage3.alice.core.Element element = (edu.cmu.cs.stage3.alice.core.Element) transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.elementReferenceFlavor);
					java.util.Vector propertyValueStructure = new java.util.Vector();
					if (!(element instanceof edu.cmu.cs.stage3.alice.core.Expression) && PropertyViewController.this.allowExpressions) {
						propertyValueStructure = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makePropertyValueStructure(element, desiredValueClass, factory, property.getOwner());
					}

					if (element instanceof edu.cmu.cs.stage3.alice.core.Sound) {
						return false;
					} else if (element instanceof edu.cmu.cs.stage3.alice.core.question.ask.AskUserForNumber) {
						return false;
					} else if (element instanceof edu.cmu.cs.stage3.alice.core.question.ask.AskUserYesNo) {
						return false;
					} else if (element instanceof edu.cmu.cs.stage3.alice.core.question.ask.AskUserForString) {
						return false;
					}

					if (desiredValueClass.isInstance(element)) {
						return true;
					} else if (element instanceof edu.cmu.cs.stage3.alice.core.Transformable && javax.vecmath.Matrix4d.class.isAssignableFrom(desiredValueClass)) {
						return true;
					} else if (element instanceof edu.cmu.cs.stage3.alice.core.Transformable && javax.vecmath.Vector3d.class.isAssignableFrom(desiredValueClass)) {
						return true;
					} else if (element instanceof edu.cmu.cs.stage3.alice.core.Transformable && edu.cmu.cs.stage3.math.Quaternion.class.isAssignableFrom(desiredValueClass)) {
						return true;
					} else if (Boolean.class.isAssignableFrom(desiredValueClass) && PropertyViewController.this.allowExpressions) {
						return true;
					} else if (Number.class.isAssignableFrom(desiredValueClass) && javax.vecmath.Vector3d.class.isAssignableFrom(element.getClass()) && PropertyViewController.this.allowExpressions) {
						return true;
					} else if (edu.cmu.cs.stage3.alice.core.Response.class.isAssignableFrom(desiredValueClass) && !(element instanceof edu.cmu.cs.stage3.alice.core.Behavior)) {
						if (property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.response.Print || property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Print) {
							return false;
						}
						return true;
					} else if (!propertyValueStructure.isEmpty() && !(element instanceof edu.cmu.cs.stage3.alice.core.Behavior)) {
						return true;
					}
				} else if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.PropertyReferenceTransferable.propertyReferenceFlavor)) {
					edu.cmu.cs.stage3.alice.core.Property p = (edu.cmu.cs.stage3.alice.core.Property) transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.PropertyReferenceTransferable.propertyReferenceFlavor);
					if (desiredValueClass.isAssignableFrom(p.getValueClass()) && PropertyViewController.this.allowExpressions) {
						return true;
					} else if (Boolean.class.isAssignableFrom(desiredValueClass) && PropertyViewController.this.allowExpressions) {
						return true;
					} else if (Number.class.isAssignableFrom(desiredValueClass) && javax.vecmath.Vector3d.class.isAssignableFrom(p.getValueClass()) && PropertyViewController.this.allowExpressions) {
						return true;
					} else if (edu.cmu.cs.stage3.alice.core.Response.class.isAssignableFrom(desiredValueClass)) {
						return true;
					}
				} else if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor)) {

					final edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype elementPrototype = (edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype) transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor);
					Class elementClass = elementPrototype.getElementClass();
					if ((property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.response.Print || property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Print) && edu.cmu.cs.stage3.alice.core.Response.class.isAssignableFrom(elementClass)) {
						return false;
					}
					boolean hookItUp = false;
					if (desiredValueClass.isAssignableFrom(elementClass)) {
						hookItUp = true;
					} else if (edu.cmu.cs.stage3.alice.core.Question.class.isAssignableFrom(elementClass) && PropertyViewController.this.allowExpressions) {
						// slight hack; i wish i didn't have to make a
						// throwaway...
						edu.cmu.cs.stage3.alice.core.Question testQuestion = (edu.cmu.cs.stage3.alice.core.Question) elementPrototype.createNewElement();
						if (desiredValueClass.isAssignableFrom(testQuestion.getValueClass())) {
							hookItUp = true;
						} else if (elementPrototype.getDesiredProperties().length == 0 && Number.class.isAssignableFrom(desiredValueClass) && javax.vecmath.Vector3d.class.isAssignableFrom(testQuestion.getValueClass())) {
							return true;
						}
					}
					return hookItUp;
				} else if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CommonMathQuestionsTransferable.commonMathQuestionsFlavor) && PropertyViewController.this.allowExpressions) {
					if (Number.class.isAssignableFrom(desiredValueClass)) {
						return true;
					}
				} else if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CopyFactoryTransferable.copyFactoryFlavor)) {
					edu.cmu.cs.stage3.alice.core.CopyFactory copyFactory = (edu.cmu.cs.stage3.alice.core.CopyFactory) transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CopyFactoryTransferable.copyFactoryFlavor);
					if ((property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.response.Print || property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Print) && edu.cmu.cs.stage3.alice.core.Response.class.isAssignableFrom(copyFactory.getValueClass())) {
						return false;
					}
					if (desiredValueClass.isAssignableFrom(copyFactory.getValueClass())) {
						return true;
					} else if (edu.cmu.cs.stage3.alice.core.Question.class.isAssignableFrom(copyFactory.getValueClass())) {
						if (desiredValueClass.isAssignableFrom(copyFactory.HACK_getExpressionValueClass())) {
							return true;
						}
					}
				}
			} catch (Throwable t) {
				return false;
				// edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog(
				// "Error while checking drag object.", t );
			}
		}
		return false;
	}

	protected boolean checkDrag(java.awt.dnd.DropTargetDragEvent dtde) {
		java.awt.datatransfer.Transferable transferable = edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager.getCurrentTransferable();
		return checkTransferable(transferable);
	}

	@Override
	public void dragEnter(java.awt.dnd.DropTargetDragEvent dtde) {
		if (checkDrag(dtde)) {
			beingDroppedOn = true;
			repaint();
		} else {
			super.dragEnter(dtde);
		}
	}

	@Override
	public void dragExit(java.awt.dnd.DropTargetEvent dte) {
		if (beingDroppedOn) {
			beingDroppedOn = false;
			repaint();
		} else {
			super.dragExit(dte);
		}
	}

	// not currently handling the case where a drag becomes valid after a
	// dragEnter...

	@Override
	public void dragOver(java.awt.dnd.DropTargetDragEvent dtde) {
		// System.out.println(property);
		if (beingDroppedOn) {
			if (!checkDrag(dtde)) {
				dtde.rejectDrag();
				beingDroppedOn = false;
				repaint();
			}
		} else {
			super.dragOver(dtde);
		}
	}

	@Override
	public void dropActionChanged(java.awt.dnd.DropTargetDragEvent dtde) {
		if (beingDroppedOn) {
			if (!checkDrag(dtde)) {
				dtde.rejectDrag();
				beingDroppedOn = false;
				repaint();
			}
		} else {
			super.dragOver(dtde);
		}
	}

	@Override
	public void drop(java.awt.dnd.DropTargetDropEvent dtde) {
		java.awt.datatransfer.Transferable transferable = edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager.getCurrentTransferable();
		Class desiredValueClass = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.getDesiredValueClass(property);
		try {
			// TODO: this whole question/variable dnd thing is really
			// dangerous... sometimes you want to move/copy, sometimes you want
			// to reference
			if (!checkTransferable(transferable)) {
				super.drop(dtde);
				return;
			}
			if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.questionReferenceFlavor) && PropertyViewController.this.allowExpressions) {
				edu.cmu.cs.stage3.alice.core.Question question = (edu.cmu.cs.stage3.alice.core.Question) transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.questionReferenceFlavor);
				if (desiredValueClass.isAssignableFrom(question.getValueClass())) {
					if ((dtde.getDropAction() & java.awt.dnd.DnDConstants.ACTION_COPY) > 0) {
						dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_COPY);
						edu.cmu.cs.stage3.alice.core.Element copy = question.createCopyNamed(question.name.getStringValue());
						property.getOwner().addChild(copy);
						// if we have a math question, replace "a" where
						// appropriate
						// if( copy instanceof
						// edu.cmu.cs.stage3.alice.core.question.BinaryBooleanResultingInBooleanQuestion
						// ) {
						// ((edu.cmu.cs.stage3.alice.core.question.BinaryBooleanResultingInBooleanQuestion)copy).a.set(
						// property.get() );
						// } else if( copy instanceof
						// edu.cmu.cs.stage3.alice.core.question.BinaryNumberResultingInNumberQuestion
						// ) {
						// ((edu.cmu.cs.stage3.alice.core.question.BinaryNumberResultingInNumberQuestion)copy).a.set(
						// property.get() );
						// } else if( copy instanceof
						// edu.cmu.cs.stage3.alice.core.question.UnaryBooleanResultingInBooleanQuestion
						// ) {
						// ((edu.cmu.cs.stage3.alice.core.question.UnaryBooleanResultingInBooleanQuestion)copy).a.set(
						// property.get() );
						// } else if( copy instanceof
						// edu.cmu.cs.stage3.alice.core.question.UnaryNumberResultingInNumberQuestion
						// ) {
						// ((edu.cmu.cs.stage3.alice.core.question.UnaryNumberResultingInNumberQuestion)copy).a.set(
						// property.get() );
						// }
						property.set(copy);
						dtde.dropComplete(true);
					} else if ((dtde.getDropAction() & java.awt.dnd.DnDConstants.ACTION_MOVE) > 0) {
						dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_MOVE);
						question.removeFromParent();
						property.getOwner().addChild(question);
						property.set(question);
						dtde.dropComplete(true);
					} else if ((dtde.getDropAction() & java.awt.dnd.DnDConstants.ACTION_LINK) > 0) {
						dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_LINK);
						property.set(question);
						dtde.dropComplete(true);
					} else {
						super.drop(dtde);
					}
				} else if (Boolean.class.isAssignableFrom(desiredValueClass)) {
					dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_LINK);
					java.util.Vector structure = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makeComparatorStructure(question, factory, property.getOwner());
					javax.swing.JPopupMenu popup = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makePopupMenu(structure);
					popup.show(this, (int) dtde.getLocation().getX(), (int) dtde.getLocation().getY());
					edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.ensurePopupIsOnScreen(popup);
					dtde.dropComplete(true);
				} else if (Number.class.isAssignableFrom(desiredValueClass) && javax.vecmath.Vector3d.class.isAssignableFrom(question.getValueClass())) {
					dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_LINK);
					java.util.Vector structure = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makePartsOfPositionStructure(question, factory, property.getOwner());
					javax.swing.JPopupMenu popup = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makePopupMenu(structure);
					popup.show(this, (int) dtde.getLocation().getX(), (int) dtde.getLocation().getY());
					edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.ensurePopupIsOnScreen(popup);
					dtde.dropComplete(true);
				} else if (edu.cmu.cs.stage3.alice.core.Response.class.isAssignableFrom(desiredValueClass)) {
					dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_LINK);
					java.util.Vector structure = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makeExpressionResponseStructure(question, factory, property.getOwner());
					javax.swing.JPopupMenu popup = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makePopupMenu(structure);
					popup.show(this, (int) dtde.getLocation().getX(), (int) dtde.getLocation().getY());
					edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.ensurePopupIsOnScreen(popup);
					dtde.dropComplete(true);
				} else {
					super.drop(dtde);
				}
			} else if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.variableReferenceFlavor) && PropertyViewController.this.allowExpressions) {
				edu.cmu.cs.stage3.alice.core.Variable variable = (edu.cmu.cs.stage3.alice.core.Variable) transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.variableReferenceFlavor);

				java.util.Vector propertyValueStructure = new java.util.Vector();
				if (edu.cmu.cs.stage3.alice.core.Element.class.isAssignableFrom(variable.getValueClass())) {
					propertyValueStructure = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makePropertyValueStructure(variable, desiredValueClass, factory, property.getOwner());
				}

				if (edu.cmu.cs.stage3.alice.core.List.class.isAssignableFrom(variable.getValueClass())) {
					edu.cmu.cs.stage3.alice.core.List list = (edu.cmu.cs.stage3.alice.core.List) variable.getValue();
					edu.cmu.cs.stage3.alice.core.Element parent = property.getOwner().getParent();
					edu.cmu.cs.stage3.alice.core.reference.PropertyReference[] references = new edu.cmu.cs.stage3.alice.core.reference.PropertyReference[0];
					if (parent != null) {
						references = parent.getPropertyReferencesTo(property.getOwner(), edu.cmu.cs.stage3.util.HowMuch.INSTANCE, false);
					}

					// if this is a ListQuestion's list, only accept if the
					// list's valueClass matches the valueClass of the property
					// that the ListQuestion is within
					if (property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.question.list.ListObjectQuestion && references.length > 0) {
						final Class itemValueClass = references[0].getProperty().getValueClass();
						if (list != null && itemValueClass.isAssignableFrom(list.valueClass.getClassValue())) {
							dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_LINK);
							property.set(variable);
							dtde.dropComplete(true);
						} else {
							super.drop(dtde);
						}
					} else {
						if (list != null && (desiredValueClass.isAssignableFrom(list.valueClass.getClassValue()) || java.lang.Boolean.class.isAssignableFrom(desiredValueClass) || java.lang.Number.class.isAssignableFrom(desiredValueClass))) {
							dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_LINK);
							edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory factory = new edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory() {
								@Override
								public Object createItem(final Object object) {
									return new Runnable() {
										@Override
										public void run() {
											edu.cmu.cs.stage3.alice.core.Question q = (edu.cmu.cs.stage3.alice.core.Question) object;
											property.getOwner().addChild(q);
											property.set(q);
											// q.data.put(
											// "PropertyViewController_propertyOwner",
											// PropertyViewController.this.property.getName()
											// );
										}
									};
								}
							};
							java.util.Vector structure = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makeListQuestionStructure(variable, factory, desiredValueClass, property.getOwner());
							if (desiredValueClass.isAssignableFrom(edu.cmu.cs.stage3.alice.core.List.class)) {
								String repr = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(variable, property);
								structure.add(0, new edu.cmu.cs.stage3.util.StringObjectPair("Separator", javax.swing.JSeparator.class));
								structure.add(0, new edu.cmu.cs.stage3.util.StringObjectPair(repr, new edu.cmu.cs.stage3.alice.authoringtool.util.SetPropertyImmediatelyFactory(property).createItem(variable)));
							}
							javax.swing.JPopupMenu popup = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makePopupMenu(structure);
							popup.show(this, (int) dtde.getLocation().getX(), (int) dtde.getLocation().getY());
							edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.ensurePopupIsOnScreen(popup);
							dtde.dropComplete(true);
						} else if (desiredValueClass.isAssignableFrom(edu.cmu.cs.stage3.alice.core.List.class)) {
							dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_LINK);
							property.set(variable);
							dtde.dropComplete(true);
						} else {
							super.drop(dtde);
						}
					}
				} else if (edu.cmu.cs.stage3.alice.core.Array.class.isAssignableFrom(variable.getValueClass())) {
					edu.cmu.cs.stage3.alice.core.Array array = (edu.cmu.cs.stage3.alice.core.Array) variable.getValue();
					edu.cmu.cs.stage3.alice.core.Element parent = property.getOwner().getParent();
					edu.cmu.cs.stage3.alice.core.reference.PropertyReference[] references = new edu.cmu.cs.stage3.alice.core.reference.PropertyReference[0];
					if (parent != null) {
						references = parent.getPropertyReferencesTo(property.getOwner(), edu.cmu.cs.stage3.util.HowMuch.INSTANCE, false);
					}

					// if this is a ArrayQuestion's array, only accept if the
					// array's valueClass matches the valueClass of the property
					// that the ArrayQuestion is within
					if (property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.question.array.ArrayObjectQuestion && references.length > 0) {
						final Class itemValueClass = references[0].getProperty().getValueClass();
						if (array != null && itemValueClass.isAssignableFrom(array.valueClass.getClassValue())) {
							dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_LINK);
							property.set(variable);
							dtde.dropComplete(true);
						} else {
							super.drop(dtde);
						}
					} else {
						if (array != null && desiredValueClass.isAssignableFrom(array.valueClass.getClassValue())) {
							dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_LINK);
							edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory factory = new edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory() {
								@Override
								public Object createItem(final Object object) {
									return new Runnable() {
										@Override
										public void run() {
											edu.cmu.cs.stage3.alice.core.Question q = (edu.cmu.cs.stage3.alice.core.Question) object;

											// property.getOwner().addChild( q
											// );
											q.setParent(property.getOwner());
											property.set(q);
											// q.data.put(
											// "PropertyViewController_propertyOwner",
											// PropertyViewController.this.property.getName()
											// );
										}
									};
								}
							};
							java.util.Vector structure = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makeArrayQuestionStructure(variable, factory, desiredValueClass, property.getOwner());
							javax.swing.JPopupMenu popup = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makePopupMenu(structure);
							popup.show(this, (int) dtde.getLocation().getX(), (int) dtde.getLocation().getY());
							edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.ensurePopupIsOnScreen(popup);
							dtde.dropComplete(true);
						} else if (desiredValueClass.isAssignableFrom(edu.cmu.cs.stage3.alice.core.Array.class)) {
							dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_LINK);
							property.set(variable);
							dtde.dropComplete(true);
						} else {
							super.drop(dtde);
						}
					}
				} else if (desiredValueClass.isAssignableFrom(variable.getValueClass())) {
					dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_LINK);
					if (!propertyValueStructure.isEmpty()) {
						java.util.Vector structure = new java.util.Vector();
						String variableRepr = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(variable, false);
						if (variable.equals(property.get())) {
							structure.addElement(new edu.cmu.cs.stage3.util.StringObjectPair(variableRepr, new edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemWithIcon(factory.createItem(variable), edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.currentValueIcon)));
						} else {
							structure.addElement(new edu.cmu.cs.stage3.util.StringObjectPair(variableRepr, factory.createItem(variable)));
						}
						structure.add(new edu.cmu.cs.stage3.util.StringObjectPair("Separator", javax.swing.JSeparator.class));
						structure.addAll(propertyValueStructure);
						javax.swing.JPopupMenu popup = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makePopupMenu(structure);
						popup.show(this, (int) dtde.getLocation().getX(), (int) dtde.getLocation().getY());
						edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.ensurePopupIsOnScreen(popup);
					} else {
						property.set(variable);
					}
					dtde.dropComplete(true);
				} else if (desiredValueClass.isAssignableFrom(variable.getClass())) {
					dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_LINK);
					property.set(variable);
					dtde.dropComplete(true);
				} else if (Boolean.class.isAssignableFrom(desiredValueClass)) {
					dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_LINK);
					java.util.Vector structure = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makeComparatorStructure(variable, factory, property.getOwner());
					if (!propertyValueStructure.isEmpty()) {
						structure.add(new edu.cmu.cs.stage3.util.StringObjectPair("Separator", javax.swing.JSeparator.class));
						structure.addAll(propertyValueStructure);
					}
					javax.swing.JPopupMenu popup = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makePopupMenu(structure);
					popup.show(this, (int) dtde.getLocation().getX(), (int) dtde.getLocation().getY());
					edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.ensurePopupIsOnScreen(popup); // here
					dtde.dropComplete(true);
				} else if (Number.class.isAssignableFrom(desiredValueClass) && javax.vecmath.Vector3d.class.isAssignableFrom(variable.getValueClass())) {
					dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_LINK);
					java.util.Vector structure = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makePartsOfPositionStructure(variable, factory, property.getOwner());
					if (!propertyValueStructure.isEmpty()) {
						structure.add(new edu.cmu.cs.stage3.util.StringObjectPair("Separator", javax.swing.JSeparator.class));
						structure.addAll(propertyValueStructure);
					}
					javax.swing.JPopupMenu popup = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makePopupMenu(structure);
					popup.show(this, (int) dtde.getLocation().getX(), (int) dtde.getLocation().getY());
					edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.ensurePopupIsOnScreen(popup);
					dtde.dropComplete(true);
				} else if (edu.cmu.cs.stage3.alice.core.Response.class.isAssignableFrom(desiredValueClass)) {
					dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_LINK);
					java.util.Vector structure = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makeExpressionResponseStructure(variable, factory, property.getOwner());
					javax.swing.JPopupMenu popup = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makePopupMenu(structure);
					popup.show(this, (int) dtde.getLocation().getX(), (int) dtde.getLocation().getY());
					edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.ensurePopupIsOnScreen(popup);
					dtde.dropComplete(true);
				} else if (!propertyValueStructure.isEmpty()) {
					dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_LINK);
					javax.swing.JPopupMenu popup = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makePopupMenu(propertyValueStructure);
					popup.show(this, (int) dtde.getLocation().getX(), (int) dtde.getLocation().getY());
					edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.ensurePopupIsOnScreen(popup);
					dtde.dropComplete(true);
				} else {
					super.drop(dtde);
				}
			} else if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.responseReferenceFlavor)) {
				edu.cmu.cs.stage3.alice.core.Response response = (edu.cmu.cs.stage3.alice.core.Response) transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.responseReferenceFlavor);
				if (desiredValueClass.isAssignableFrom(edu.cmu.cs.stage3.alice.core.Response.class)) {
					if ((dtde.getDropAction() & java.awt.dnd.DnDConstants.ACTION_COPY) > 0) {
						dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_COPY);
						edu.cmu.cs.stage3.alice.core.Element copy = response.createCopyNamed(response.name.getStringValue());
						property.getOwner().addChild(copy);
						property.set(copy);
						dtde.dropComplete(true);
					} else if ((dtde.getDropAction() & java.awt.dnd.DnDConstants.ACTION_MOVE) > 0) {
						dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_MOVE);
						response.removeFromParent();
						property.getOwner().addChild(response);
						property.set(response);
						dtde.dropComplete(true);
					} else if ((dtde.getDropAction() & java.awt.dnd.DnDConstants.ACTION_LINK) > 0) {
						dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_LINK);
						property.set(response);
						dtde.dropComplete(true);
					} else {
						super.drop(dtde);
					}
				} else {
					super.drop(dtde);
				}
			} else if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.elementReferenceFlavor)) {
				edu.cmu.cs.stage3.alice.core.Element element = (edu.cmu.cs.stage3.alice.core.Element) transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.elementReferenceFlavor);
				java.util.Vector propertyValueStructure = new java.util.Vector();
				if (PropertyViewController.this.allowExpressions) {
					propertyValueStructure = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makePropertyValueStructure(element, desiredValueClass, factory, property.getOwner());
				}

				if (desiredValueClass.isInstance(element)) {
					dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_MOVE);
					if (!propertyValueStructure.isEmpty()) {
						java.util.Vector structure = new java.util.Vector();
						String elementRepr = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(element, false);
						if (element.equals(property.get())) {
							structure.addElement(new edu.cmu.cs.stage3.util.StringObjectPair(elementRepr, new edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemWithIcon(factory.createItem(element), edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.currentValueIcon)));
						} else {
							structure.addElement(new edu.cmu.cs.stage3.util.StringObjectPair(elementRepr, factory.createItem(element)));
						}
						structure.add(new edu.cmu.cs.stage3.util.StringObjectPair("Separator", javax.swing.JSeparator.class));
						structure.addAll(propertyValueStructure);
						javax.swing.JPopupMenu popup = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makePopupMenu(structure);
						popup.show(this, (int) dtde.getLocation().getX(), (int) dtde.getLocation().getY());
						edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.ensurePopupIsOnScreen(popup);
					} else {
						property.set(element);
					}
					dtde.dropComplete(true);
				} else if (Boolean.class.isAssignableFrom(desiredValueClass)) {
					dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_LINK);
					java.util.Vector structure = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makeComparatorStructure(element, factory, property.getOwner());
					if (!propertyValueStructure.isEmpty()) {
						structure.add(new edu.cmu.cs.stage3.util.StringObjectPair("Separator", javax.swing.JSeparator.class));
						structure.addAll(propertyValueStructure);
					}
					javax.swing.JPopupMenu popup = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makePopupMenu(structure);
					popup.show(this, (int) dtde.getLocation().getX(), (int) dtde.getLocation().getY());
					edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.ensurePopupIsOnScreen(popup);
					dtde.dropComplete(true);
				} else if (Number.class.isAssignableFrom(desiredValueClass) && javax.vecmath.Vector3d.class.isAssignableFrom(element.getClass())) {
					dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_LINK);
					java.util.Vector structure = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makePartsOfPositionStructure(element, factory, property.getOwner());
					if (!propertyValueStructure.isEmpty()) {
						structure.add(new edu.cmu.cs.stage3.util.StringObjectPair("Separator", javax.swing.JSeparator.class));
						structure.addAll(propertyValueStructure);
					}
					javax.swing.JPopupMenu popup = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makePopupMenu(structure);
					popup.show(this, (int) dtde.getLocation().getX(), (int) dtde.getLocation().getY());
					edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.ensurePopupIsOnScreen(popup);
					dtde.dropComplete(true);
				} else if (element instanceof edu.cmu.cs.stage3.alice.core.Transformable && javax.vecmath.Matrix4d.class.isAssignableFrom(desiredValueClass)) {
					dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_LINK);
					// This if checks to see if the drop happened on the
					// PointOfView property of an object in the properties panel
					if (property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.Transformable && property == ((edu.cmu.cs.stage3.alice.core.Transformable) property.getOwner()).localTransformation) {
						edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation povAnim = new edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation();
						povAnim.subject.set(property.getOwner());
						povAnim.pointOfView.set(edu.cmu.cs.stage3.math.Matrix44.IDENTITY);
						povAnim.asSeenBy.set(element);
						edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation undoResponse = new edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation();
						undoResponse.subject.set(property.getOwner());
						undoResponse.pointOfView.set(((edu.cmu.cs.stage3.alice.core.Transformable) property.getOwner()).localTransformation.getMatrix4dValue());
						undoResponse.asSeenBy.set(((edu.cmu.cs.stage3.alice.core.Transformable) property.getOwner()).vehicle.getValue());
						edu.cmu.cs.stage3.alice.core.Property[] properties = new edu.cmu.cs.stage3.alice.core.Property[]{((edu.cmu.cs.stage3.alice.core.Transformable) property.getOwner()).localTransformation};
						edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().performOneShot(povAnim, undoResponse, properties);

					} else {
						edu.cmu.cs.stage3.alice.core.question.PointOfView POVQuestion = new edu.cmu.cs.stage3.alice.core.question.PointOfView();
						POVQuestion.subject.set(element);
						property.set(POVQuestion);
					}
					dtde.dropComplete(true);
				} else if (element instanceof edu.cmu.cs.stage3.alice.core.Transformable && javax.vecmath.Vector3d.class.isAssignableFrom(desiredValueClass)) {
					dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_LINK);
					property.set(((edu.cmu.cs.stage3.alice.core.Transformable) element).localTransformation.getMatrix44Value().getPosition());
					dtde.dropComplete(true);
				} else if (element instanceof edu.cmu.cs.stage3.alice.core.Transformable && edu.cmu.cs.stage3.math.Quaternion.class.isAssignableFrom(desiredValueClass)) {
					dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_LINK);
					property.set(((edu.cmu.cs.stage3.alice.core.Transformable) element).localTransformation.getMatrix44Value().getAxes().getQuaternion());
					dtde.dropComplete(true);
				} else if (edu.cmu.cs.stage3.alice.core.Response.class.isAssignableFrom(desiredValueClass)) {
					dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_LINK);
					java.util.Vector structure = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makeResponseStructure(element, factory, property.getOwner());
					javax.swing.JPopupMenu popup = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makePopupMenu(structure);
					if (popup != null) {
						popup.show(this, (int) dtde.getLocation().getX(), (int) dtde.getLocation().getY());
						edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.ensurePopupIsOnScreen(popup);
					}
					dtde.dropComplete(true);
				} else if (!propertyValueStructure.isEmpty()) {
					dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_LINK);
					javax.swing.JPopupMenu popup = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makePopupMenu(propertyValueStructure);
					popup.show(this, (int) dtde.getLocation().getX(), (int) dtde.getLocation().getY());
					edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.ensurePopupIsOnScreen(popup);
					dtde.dropComplete(true);
				} else {
					super.drop(dtde);
				}
			} else if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.PropertyReferenceTransferable.propertyReferenceFlavor)) {
				edu.cmu.cs.stage3.alice.core.Property p = (edu.cmu.cs.stage3.alice.core.Property) transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.PropertyReferenceTransferable.propertyReferenceFlavor);
				if (desiredValueClass.isAssignableFrom(p.getValueClass()) && PropertyViewController.this.allowExpressions) {
					dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_MOVE);
					edu.cmu.cs.stage3.alice.core.question.PropertyValue propertyValueQuestion = new edu.cmu.cs.stage3.alice.core.question.PropertyValue();
					propertyValueQuestion.element.set(p.getOwner());
					propertyValueQuestion.propertyName.set(p.getName());
					propertyValueQuestion.data.put("createdByPropertyViewController", "true");
					property.getOwner().addChild(propertyValueQuestion);
					property.set(propertyValueQuestion);
					dtde.dropComplete(true);
				} else if (Boolean.class.isAssignableFrom(desiredValueClass) && PropertyViewController.this.allowExpressions) {
					dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_LINK);
					edu.cmu.cs.stage3.alice.core.question.PropertyValue propertyValueQuestion = new edu.cmu.cs.stage3.alice.core.question.PropertyValue();
					propertyValueQuestion.element.set(p.getOwner());
					propertyValueQuestion.propertyName.set(p.getName());
					propertyValueQuestion.data.put("createdByPropertyViewController", "true");
					java.util.Vector structure = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makeComparatorStructure(propertyValueQuestion, factory, property.getOwner());
					javax.swing.JPopupMenu popup = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makePopupMenu(structure);
					popup.show(this, (int) dtde.getLocation().getX(), (int) dtde.getLocation().getY());
					edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.ensurePopupIsOnScreen(popup);
					dtde.dropComplete(true);
				} else if (Number.class.isAssignableFrom(desiredValueClass) && javax.vecmath.Vector3d.class.isAssignableFrom(p.getValueClass()) && PropertyViewController.this.allowExpressions) {
					dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_LINK);
					edu.cmu.cs.stage3.alice.core.question.PropertyValue propertyValueQuestion = new edu.cmu.cs.stage3.alice.core.question.PropertyValue();
					propertyValueQuestion.element.set(p.getOwner());
					propertyValueQuestion.propertyName.set(p.getName());
					propertyValueQuestion.data.put("createdByPropertyViewController", "true");
					java.util.Vector structure = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makePartsOfPositionStructure(propertyValueQuestion, factory, property.getOwner());
					javax.swing.JPopupMenu popup = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makePopupMenu(structure);
					popup.show(this, (int) dtde.getLocation().getX(), (int) dtde.getLocation().getY());
					edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.ensurePopupIsOnScreen(popup);
					dtde.dropComplete(true);
				} else if (edu.cmu.cs.stage3.alice.core.Response.class.isAssignableFrom(desiredValueClass)) {
					dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_LINK);

					edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype responsePrototype;
					if (p instanceof edu.cmu.cs.stage3.alice.core.property.VehicleProperty) {
						edu.cmu.cs.stage3.util.StringObjectPair[] knownPropertyValues = new edu.cmu.cs.stage3.util.StringObjectPair[]{new edu.cmu.cs.stage3.util.StringObjectPair("element", p.getOwner()), new edu.cmu.cs.stage3.util.StringObjectPair("propertyName", p.getName()), new edu.cmu.cs.stage3.util.StringObjectPair("duration", new Double(0.0)),};
						String[] desiredProperties = new String[]{"value"};
						responsePrototype = new edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype(edu.cmu.cs.stage3.alice.core.response.VehiclePropertyAnimation.class, knownPropertyValues, desiredProperties);
					} else {
						edu.cmu.cs.stage3.util.StringObjectPair[] knownPropertyValues = new edu.cmu.cs.stage3.util.StringObjectPair[]{new edu.cmu.cs.stage3.util.StringObjectPair("element", p.getOwner()), new edu.cmu.cs.stage3.util.StringObjectPair("propertyName", p.getName())};
						String[] desiredProperties = new String[]{"value"};
						responsePrototype = new edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype(edu.cmu.cs.stage3.alice.core.response.PropertyAnimation.class, knownPropertyValues, desiredProperties);
					}

					edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory prototypeFactory = new edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory() {
						@Override
						public Object createItem(final Object object) {
							return new Runnable() {
								@Override
								public void run() {
									edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype ep = (edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype) object;
									edu.cmu.cs.stage3.alice.core.Element element = ep.createNewElement();
									property.getOwner().addChild(element);
									property.set(element);
								}
							};
						}
					};

					java.util.Vector structure = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makePrototypeStructure(responsePrototype, prototypeFactory, property.getOwner());
					javax.swing.JPopupMenu popup = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makePopupMenu(structure);
					popup.show(this, (int) dtde.getLocation().getX(), (int) dtde.getLocation().getY());
					edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.ensurePopupIsOnScreen(popup);
					dtde.dropComplete(true);
				} else {
					super.drop(dtde);
				}
			} else if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor)) {
				edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype elementPrototype = (edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype) transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor);
				Class elementClass = elementPrototype.getElementClass();
				boolean hookItUp = false;
				if (desiredValueClass.isAssignableFrom(elementClass)) {
					hookItUp = true;
				} else if (edu.cmu.cs.stage3.alice.core.Question.class.isAssignableFrom(elementClass) && PropertyViewController.this.allowExpressions) {
					// slight hack; i wish i didn't have to make a throwaway...
					edu.cmu.cs.stage3.alice.core.Question testQuestion = (edu.cmu.cs.stage3.alice.core.Question) elementPrototype.createNewElement();
					if (desiredValueClass.isAssignableFrom(testQuestion.getValueClass())) {
						if (testQuestion instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion) {
							edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion userDefinedQuestion = ((edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion) testQuestion).userDefinedQuestion.getUserDefinedQuestionValue();
							if (userDefinedQuestion.isAncestorOf(property.getOwner())) {
								Object[] options = {"Yes, I understand what I am doing.", "No, I made this call accidentally."};
								int result = edu.cmu.cs.stage3.swing.DialogManager.showOptionDialog("The code you have just dropped in creates a recursive call. We recommend that you understand\nwhat recursion is before making a call like this.  Are you sure you want to do this?", "Recursion Warning", javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.WARNING_MESSAGE, null, options, options[1]);
								if (result == javax.swing.JOptionPane.YES_OPTION) {
									hookItUp = true;
								}
							} else {
								hookItUp = true;
							}
						} else if (property.get() != null) {
							hookItUp = true;
							if (testQuestion instanceof edu.cmu.cs.stage3.alice.core.question.BinaryBooleanResultingInBooleanQuestion) {
								elementPrototype = elementPrototype.createCopy(new edu.cmu.cs.stage3.util.StringObjectPair("a", property.get()));
							} else if (testQuestion instanceof edu.cmu.cs.stage3.alice.core.question.BinaryNumberResultingInNumberQuestion) {
								elementPrototype = elementPrototype.createCopy(new edu.cmu.cs.stage3.util.StringObjectPair("a", property.get()));
							} else if (testQuestion instanceof edu.cmu.cs.stage3.alice.core.question.UnaryBooleanResultingInBooleanQuestion) {
								elementPrototype = elementPrototype.createCopy(new edu.cmu.cs.stage3.util.StringObjectPair("a", property.get()));
							} else if (testQuestion instanceof edu.cmu.cs.stage3.alice.core.question.UnaryNumberResultingInNumberQuestion) {
								elementPrototype = elementPrototype.createCopy(new edu.cmu.cs.stage3.util.StringObjectPair("a", property.get()));
							} else if (testQuestion instanceof edu.cmu.cs.stage3.alice.core.question.StringConcatQuestion) {
								elementPrototype = elementPrototype.createCopy(new edu.cmu.cs.stage3.util.StringObjectPair("a", property.get()));
							}
						} else {
							hookItUp = true;
						}
					} else if (elementPrototype.getDesiredProperties().length == 0 && Number.class.isAssignableFrom(desiredValueClass) && javax.vecmath.Vector3d.class.isAssignableFrom(testQuestion.getValueClass())) {
						dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_LINK);
						edu.cmu.cs.stage3.alice.core.Element element = elementPrototype.createNewElement();
						property.getOwner().addChild(element);
						java.util.Vector structure = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makePartsOfPositionStructure(element, factory, property.getOwner());
						javax.swing.JPopupMenu popup = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makePopupMenu(structure);
						popup.show(this, (int) dtde.getLocation().getX(), (int) dtde.getLocation().getY());
						edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.ensurePopupIsOnScreen(popup);
						dtde.dropComplete(true);
					}
				}

				if (hookItUp) {
					dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_MOVE);
					edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory factory = new edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory() {
						@Override
						public Object createItem(final Object object) {
							return new Runnable() {
								@Override
								public void run() {
									edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype ep = (edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype) object;
									edu.cmu.cs.stage3.alice.core.Element element = ep.createNewElement();
									property.getOwner().addChild(element);
									property.set(element);
									// element.data.put(
									// "PropertyViewController_propertyOwner",
									// PropertyViewController.this.property.getName()
									// );
								}
							};
						}
					};
					if (elementPrototype.getDesiredProperties().length > 0 && elementPrototype.getDesiredProperties().length < 4) { // hack
																																	// to
																																	// prevent
																																	// menus
																																	// from
																																	// getting
																																	// too
																																	// big
						java.util.Vector structure = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makePrototypeStructure(elementPrototype, factory, property.getOwner());
						javax.swing.JPopupMenu popup = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makePopupMenu(structure);
						popup.show(this, (int) dtde.getLocation().getX(), (int) dtde.getLocation().getY());
						edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.ensurePopupIsOnScreen(popup);
					} else {
						((Runnable) factory.createItem(elementPrototype)).run();
					}
					dtde.dropComplete(true);
				} else {
					super.drop(dtde);
				}
			} else if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CommonMathQuestionsTransferable.commonMathQuestionsFlavor) && PropertyViewController.this.allowExpressions) {
				if (Number.class.isAssignableFrom(desiredValueClass)) {
					dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_LINK);
					edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory factory = new edu.cmu.cs.stage3.alice.authoringtool.util.SetPropertyImmediatelyFactory(property);
					java.util.Vector structure = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makeCommonMathQuestionStructure(property.get(), factory, property.getOwner());
					javax.swing.JPopupMenu popup = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makePopupMenu(structure);
					popup.show(this, (int) dtde.getLocation().getX(), (int) dtde.getLocation().getY());
					edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.ensurePopupIsOnScreen(popup);
					dtde.dropComplete(true);
				} else {
					super.drop(dtde);
				}
			} else if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CopyFactoryTransferable.copyFactoryFlavor)) {
				edu.cmu.cs.stage3.alice.core.CopyFactory copyFactory = (edu.cmu.cs.stage3.alice.core.CopyFactory) transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CopyFactoryTransferable.copyFactoryFlavor);
				if (desiredValueClass.isAssignableFrom(copyFactory.getValueClass())) {
					dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_LINK);
					edu.cmu.cs.stage3.alice.core.Element element = copyFactory.manufactureCopy(property.getOwner().getRoot());
					property.set(element);
					property.getOwner().addChild(element);
					dtde.dropComplete(true);
				} else if (edu.cmu.cs.stage3.alice.core.Question.class.isAssignableFrom(copyFactory.getValueClass())) {
					edu.cmu.cs.stage3.alice.core.Question question = (edu.cmu.cs.stage3.alice.core.Question) copyFactory.manufactureCopy(property.getOwner().getRoot());
					if (desiredValueClass.isAssignableFrom(question.getValueClass())) {
						dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_COPY);
						property.set(question);
						property.getOwner().addChild(question);
						dtde.dropComplete(true);
					} else {
						super.drop(dtde);
					}
				} else {
					super.drop(dtde);
				}
			} else {
				super.drop(dtde);
			}
		} catch (java.awt.datatransfer.UnsupportedFlavorException e) {
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("Drop didn't work: bad flavor", e);
		} catch (java.io.IOException e) {
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("Drop didn't work: IOException", e);
		} catch (Throwable t) {
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("Drop didn't work.", t);
		}
		beingDroppedOn = false;
		repaint();
	}

	protected class DropPotentialFeedbackListener implements edu.cmu.cs.stage3.alice.authoringtool.util.event.DnDManagerListener {
		private void doCheck() {
			java.awt.datatransfer.Transferable transferable = edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager.getCurrentTransferable();
			boolean transferableHasPotential = checkTransferable(transferable);
			if (paintDropPotential != transferableHasPotential) {
				paintDropPotential = transferableHasPotential;
				PropertyViewController.this.repaint();
			}
		}

		@Override
		public void dragGestureRecognized(java.awt.dnd.DragGestureEvent dge) {
			// do nothing for the gesture, wait until dragStarted
		}

		@Override
		public void dragStarted() {
			doCheck();
		}

		@Override
		public void dragEnter(java.awt.dnd.DragSourceDragEvent dsde) {
			doCheck();
		}

		@Override
		public void dragExit(java.awt.dnd.DragSourceEvent dse) {
			doCheck();
		}

		@Override
		public void dragOver(java.awt.dnd.DragSourceDragEvent dsde) {
			// don't check here
		}

		@Override
		public void dropActionChanged(java.awt.dnd.DragSourceDragEvent dsde) {
			doCheck();
		}

		@Override
		public void dragDropEnd(java.awt.dnd.DragSourceDropEvent dsde) {
			paintDropPotential = false;
			PropertyViewController.this.repaint();
		}
	}

	protected class QuestionDeletionListener implements edu.cmu.cs.stage3.alice.core.event.ChildrenListener {
		@Override
		public void childrenChanging(edu.cmu.cs.stage3.alice.core.event.ChildrenEvent ev) {
		}
		@Override
		public void childrenChanged(edu.cmu.cs.stage3.alice.core.event.ChildrenEvent ev) {
			if (ev.getChangeType() == edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_REMOVED) {
				if (ev.getChild() instanceof edu.cmu.cs.stage3.alice.core.Question /*
																					 * &&
																					 * !
																					 * handlingQuestionAlready
																					 */) {
					edu.cmu.cs.stage3.alice.core.Question question = (edu.cmu.cs.stage3.alice.core.Question) ev.getChild();
					if (question.data.get("associatedProperty") != null) {
						if (question.data.get("associatedProperty").equals(property.getName())) {
							Object newValue = null;
							handlingQuestionAlready = true;
							if (question instanceof edu.cmu.cs.stage3.alice.core.question.BinaryBooleanResultingInBooleanQuestion) {
								newValue = ((edu.cmu.cs.stage3.alice.core.question.BinaryBooleanResultingInBooleanQuestion) question).a.get();
							} else if (question instanceof edu.cmu.cs.stage3.alice.core.question.BinaryNumberResultingInNumberQuestion) {
								newValue = ((edu.cmu.cs.stage3.alice.core.question.BinaryNumberResultingInNumberQuestion) question).a.get();
							} else if (question instanceof edu.cmu.cs.stage3.alice.core.question.UnaryBooleanResultingInBooleanQuestion) {
								newValue = ((edu.cmu.cs.stage3.alice.core.question.UnaryBooleanResultingInBooleanQuestion) question).a.get();
							} else if (question instanceof edu.cmu.cs.stage3.alice.core.question.UnaryNumberResultingInNumberQuestion) {
								newValue = ((edu.cmu.cs.stage3.alice.core.question.UnaryNumberResultingInNumberQuestion) question).a.get();
							} else {
								newValue = property.getDefaultValue();
							}
							if (newValue instanceof edu.cmu.cs.stage3.alice.core.Element) {
								edu.cmu.cs.stage3.alice.core.Element element = (edu.cmu.cs.stage3.alice.core.Element) newValue;
								// Wow is this hacky. We need to remove the
								// value we're promoting from it's parent
								// but if we do that it's parent's
								// childrenChange listeners will act up and do
								// bad things
								// so we remove all the listeners, set the
								// parent to null, and then restore all the
								// listners
								// (if we don't restore the listeners then
								// undo/redo will be broke)
								if (element.getParent() == question) {
									edu.cmu.cs.stage3.alice.core.event.ChildrenListener[] childlisteners = question.getChildrenListeners();
									for (ChildrenListener childlistener : childlisteners) {
										question.removeChildrenListener(childlistener);
									}
									edu.cmu.cs.stage3.alice.core.Element parent = element.getParent();
									int oldIndex = parent.getIndexOfChild(element);
									element.setParent(null);
									edu.cmu.cs.stage3.alice.core.event.ChildrenEvent childChangedEvent = new edu.cmu.cs.stage3.alice.core.event.ChildrenEvent(parent, element, edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_REMOVED, oldIndex, -1);

									edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getUndoRedoStack().childrenChanged(childChangedEvent);
									for (ChildrenListener childlistener : childlisteners) {
										question.addChildrenListener(childlistener);
									}
								}
							}
							edu.cmu.cs.stage3.alice.authoringtool.util.SetPropertyImmediatelyFactory factory = new edu.cmu.cs.stage3.alice.authoringtool.util.SetPropertyImmediatelyFactory(property);
							((Runnable) factory.createItem(newValue)).run();
							handlingQuestionAlready = false;
						}
					}
				}
			}
		}
	}

	public static class RefreshThread extends Thread {
		protected boolean haltThread = false;
		protected boolean pauseThread = false;

		public RefreshThread() {
			setName("PropertyViewController.RefreshThread");
		}

		public void halt() {
			haltThread = true;
			synchronized (this) {
				notify();
			}
		}

		public void pause() {
			pauseThread = true;
		}

		public void unpause() {
			pauseThread = false;
			synchronized (this) {
				notify();
			}
		}

		@Override
		public void run() {
			while (!haltThread) {
				synchronized (PropertyViewController.propertyViewControllersToRefresh) {
					if (!PropertyViewController.propertyViewControllersToRefresh.isEmpty()) {
						for (java.util.Iterator iter = PropertyViewController.propertyViewControllersToRefresh.iterator(); iter.hasNext();) {
							final PropertyViewController pvc = (PropertyViewController) iter.next();
							javax.swing.SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									pvc.refreshGUI();
								}
							});
						}
						PropertyViewController.propertyViewControllersToRefresh.clear();
					}
				}
				try {
					Thread.sleep(100);
					if (pauseThread) {
						synchronized (this) {
							while (pauseThread) {
								wait();
							}
						}
					}
				} catch (InterruptedException e) {}
			}
		}
	}
}
