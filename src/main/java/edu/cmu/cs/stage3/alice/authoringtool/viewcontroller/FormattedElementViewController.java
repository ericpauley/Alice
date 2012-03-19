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

import java.awt.Component;

import javax.swing.SwingConstants;

import edu.cmu.cs.stage3.alice.core.Property;

/**
 * @author Jason Pratt
 */
public class FormattedElementViewController extends edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel implements edu.cmu.cs.stage3.alice.authoringtool.util.GUIElement {
	protected edu.cmu.cs.stage3.alice.core.Element element;
	protected java.util.List visibleProperties;
	protected javax.swing.JPanel subPanel = new edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel();
	protected String format;
	protected java.util.HashMap guiMap = new java.util.HashMap();
	// protected edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel
	// moreTile = new
	// edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel();
	protected javax.swing.JPanel moreTile = new javax.swing.JPanel();
	protected MouseListener mouseListener = new MouseListener();
	protected edu.cmu.cs.stage3.alice.core.event.PropertyListener commentedListener = new edu.cmu.cs.stage3.alice.core.event.PropertyListener() {
		@Override
		public void propertyChanging(edu.cmu.cs.stage3.alice.core.event.PropertyEvent ev) {
		}
		@Override
		public void propertyChanged(edu.cmu.cs.stage3.alice.core.event.PropertyEvent ev) {
			if (ev.getValue().equals(Boolean.TRUE)) {
				setEnabled(false);
			} else {
				setEnabled(true);
			}
			FormattedElementViewController.this.revalidate();
			FormattedElementViewController.this.repaint();
		}
	};
	protected edu.cmu.cs.stage3.alice.core.event.PropertyListener updateListener = new edu.cmu.cs.stage3.alice.core.event.PropertyListener() {
		@Override
		public void propertyChanging(edu.cmu.cs.stage3.alice.core.event.PropertyEvent ev) {
		}
		@Override
		public void propertyChanged(edu.cmu.cs.stage3.alice.core.event.PropertyEvent ev) {
			FormattedElementViewController.this.refreshGUI();
		}
	};
	protected edu.cmu.cs.stage3.alice.core.event.PropertyListener userDefinedListener = new edu.cmu.cs.stage3.alice.core.event.PropertyListener() {
		@Override
		public void propertyChanging(edu.cmu.cs.stage3.alice.core.event.PropertyEvent ev) {
			if (element instanceof edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse) {
				if (ev.getProperty().getValue() instanceof edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse) {
					((edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse) ev.getProperty().getValue()).name.removePropertyListener(updateListener);
				}
			} else if (element instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion) {
				if (ev.getProperty().getValue() instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion) {
					((edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion) ev.getProperty().getValue()).name.removePropertyListener(updateListener);
				}
			}
		}
		@Override
		public void propertyChanged(edu.cmu.cs.stage3.alice.core.event.PropertyEvent ev) {
			if (element instanceof edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse) {
				if (ev.getProperty().getValue() instanceof edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse) {
					((edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse) ev.getProperty().getValue()).name.addPropertyListener(updateListener);
				}
			} else if (element instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion) {
				if (ev.getProperty().getValue() instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion) {
					((edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion) ev.getProperty().getValue()).name.addPropertyListener(updateListener);
				}
			}
		}
	};
	protected boolean sleeping = false;

	public FormattedElementViewController() {
		setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 2, 3, 2));

		subPanel.setLayout(new java.awt.GridBagLayout());
		subPanel.setOpaque(false);
		subPanel.setBorder(null);

		moreTile.setLayout(new java.awt.BorderLayout());
		moreTile.setOpaque(false);
		moreTile.setBorder(null);
		javax.swing.JLabel moreLabel = new javax.swing.JLabel("more...", edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getIconForValue("popupArrow"), SwingConstants.LEADING);
		moreLabel.setHorizontalTextPosition(SwingConstants.LEADING);
		moreLabel.setIconTextGap(2);
		moreTile.add(moreLabel, java.awt.BorderLayout.CENTER);

		moreTile.addMouseListener(new java.awt.event.MouseAdapter() {

			@Override
			public void mouseReleased(java.awt.event.MouseEvent ev) {
				if (ev.getX() >= 0 && ev.getX() < ev.getComponent().getWidth() && ev.getY() >= 0 && ev.getY() < ev.getComponent().getHeight()) {
					if (FormattedElementViewController.this.isEnabled()) {
						java.util.Vector structure = new java.util.Vector();
						edu.cmu.cs.stage3.alice.core.Property[] properties = getUnsetProperties();
						for (Property propertie : properties) {
							SetPropertyImmediatelyFactory factory = new SetPropertyImmediatelyFactory(propertie, true);
							structure.add(new edu.cmu.cs.stage3.util.StringObjectPair(propertie.getName(), edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makePropertyStructure(propertie, factory, true, true, true, null)));
						}
						edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.createAndShowPopupMenu(structure, moreTile, 0, moreTile.getHeight());
					}
				}
			}
		});

		addMouseListener(mouseListener);
		grip.addMouseListener(mouseListener);
		subPanel.addMouseListener(mouseListener); // this didn't used to be
													// necessary. I don't know
													// what's going on

		// edu.cmu.cs.stage3.alice.authoringtool.util.Configuration.addConfigurationListener(
		// new
		// edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationListener()
		// {
		// public void changing(
		// edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationEvent
		// ev ) {}
		// public void changed(
		// edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationEvent
		// ev ) {
		// if( ev.getKeyName().equals(
		// "edu.cmu.cs.stage3.alice.authoringtool.useJavaSyntax" ) ) {
		// if( element != null ) {
		// FormattedElementViewController.this.format =
		// edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getFormat(
		// element.getClass() );
		// FormattedElementViewController.this.refreshGUI();
		// }
		// }
		// }
		// }
		// );
	}

	public edu.cmu.cs.stage3.alice.core.Element getElement() {
		return element;
	}

	public void setElement(edu.cmu.cs.stage3.alice.core.Element element) {
		super.reset();

		stopListening();

		this.element = element;
		if (this.element != null) {
			format = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getFormat(element.getClass());

			calculateVisibleProperties();
			setTransferable(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.TransferableFactory.createTransferable(element));

			if (element instanceof edu.cmu.cs.stage3.alice.core.response.Comment) { // TODO:
																					// specify
																					// in
																					// config
				setBackground(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("Comment"));
			} else if (element instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Comment) { // TODO:
																										// specify
																										// in
																										// config
				setBackground(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("Comment"));
			} else if (element instanceof edu.cmu.cs.stage3.alice.core.response.Print) { // TODO:
																							// specify
																							// in
																							// config
				setBackground(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("Print"));
			} else if (element instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Print) { // TODO:
																										// specify
																										// in
																										// config
				setBackground(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("Print"));
			} else if (element instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.PropertyAssignment) { // TODO:
																													// specify
																													// in
																													// config
				setBackground(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("PropertyAssignment"));
			} else if (element instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Return) { // TODO:
																										// specify
																										// in
																										// config
				setBackground(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("Return"));
			} else if (element instanceof edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse) { // TODO:
																												// specify
																												// in
																												// config
				setBackground(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("userDefinedResponse"));
			} else if (element instanceof edu.cmu.cs.stage3.alice.core.Response) { // TODO:
																					// specify
																					// in
																					// config
				setBackground(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("response"));
			} else if (element instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion) { // TODO:
																															// specify
																															// in
																															// config
				setBackground(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("userDefinedQuestion"));
			} else if (element instanceof edu.cmu.cs.stage3.alice.core.Question) { // TODO:
																					// specify
																					// in
																					// config
				setBackground(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("question"));
			} else {
				setBackground(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("formattedElementViewController"));
			}

			this.add(subPanel, java.awt.BorderLayout.CENTER);
			addDragSourceComponent(subPanel);

			startListening();
		}

		refreshGUI();
	}

	public java.awt.Component getMoreTile() {
		return moreTile;
	}

	protected void startListening() {
		element.data.addPropertyListener(updateListener);
		if (element instanceof edu.cmu.cs.stage3.alice.core.Response) {
			edu.cmu.cs.stage3.alice.core.Response response = (edu.cmu.cs.stage3.alice.core.Response) element;
			response.isCommentedOut.addPropertyListener(commentedListener);
			// } else if( element instanceof
			// edu.cmu.cs.stage3.alice.core.question.userdefined.Component ) {
			// edu.cmu.cs.stage3.alice.core.question.userdefined.Component
			// questionComponent =
			// (edu.cmu.cs.stage3.alice.core.question.userdefined.Component)element;
			// questionComponent.isCommentedOut.addPropertyListener(
			// commentedListener );
		}

		if (element instanceof edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse) {
			edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse callToUserDefinedResponse = (edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse) element;
			callToUserDefinedResponse.userDefinedResponse.addPropertyListener(userDefinedListener);
			if (callToUserDefinedResponse.userDefinedResponse.getUserDefinedResponseValue() != null) {
				callToUserDefinedResponse.userDefinedResponse.getUserDefinedResponseValue().name.addPropertyListener(updateListener);
			}
		} else if (element instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion) {
			edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion callToUserDefinedQuestion = (edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion) element;
			callToUserDefinedQuestion.userDefinedQuestion.addPropertyListener(userDefinedListener);
			if (callToUserDefinedQuestion.userDefinedQuestion.getUserDefinedQuestionValue() != null) {
				callToUserDefinedQuestion.userDefinedQuestion.getUserDefinedQuestionValue().name.addPropertyListener(updateListener);
			}
		}
	}

	protected void stopListening() {
		if (element instanceof edu.cmu.cs.stage3.alice.core.Response) {
			edu.cmu.cs.stage3.alice.core.Response response = (edu.cmu.cs.stage3.alice.core.Response) element;
			response.isCommentedOut.removePropertyListener(commentedListener);
			// } else if( element instanceof
			// edu.cmu.cs.stage3.alice.core.question.userdefined.Component ) {
			// edu.cmu.cs.stage3.alice.core.question.userdefined.Component
			// questionComponent =
			// (edu.cmu.cs.stage3.alice.core.question.userdefined.Component)element;
			// questionComponent.isCommentedOut.removePropertyListener(
			// commentedListener );
		}

		if (element instanceof edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse) {
			edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse callToUserDefinedResponse = (edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse) element;
			callToUserDefinedResponse.userDefinedResponse.removePropertyListener(userDefinedListener);
			if (callToUserDefinedResponse.userDefinedResponse.getUserDefinedResponseValue() != null) {
				callToUserDefinedResponse.userDefinedResponse.getUserDefinedResponseValue().name.removePropertyListener(updateListener);
			}
		} else if (element instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion) {
			edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion callToUserDefinedQuestion = (edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion) element;
			callToUserDefinedQuestion.userDefinedQuestion.removePropertyListener(userDefinedListener);
			if (callToUserDefinedQuestion.userDefinedQuestion.getUserDefinedQuestionValue() != null) {
				callToUserDefinedQuestion.userDefinedQuestion.getUserDefinedQuestionValue().name.removePropertyListener(updateListener);
			}
		}
	}

	private void calculateVisibleProperties() {
		visibleProperties = new java.util.LinkedList();
		if (element != null) {
			String visiblePropertiesString = (String) element.data.get("edu.cmu.cs.stage3.alice.authoringtool.visibleProperties");
			if (visiblePropertiesString != null) {
				java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(visiblePropertiesString, ",");
				while (tokenizer.hasMoreTokens()) {
					String token = tokenizer.nextToken();

					edu.cmu.cs.stage3.alice.core.Property property = element.getPropertyNamed(token);
					if (property != null && !visibleProperties.contains(property)) {
						visibleProperties.add(property);
					}
				}
			}
		}

		edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer formatTokenizer = new edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer(format);
		while (formatTokenizer.hasMoreTokens()) {
			String token = formatTokenizer.nextToken();
			if (token.startsWith("<") && token.endsWith(">")) {
				edu.cmu.cs.stage3.alice.core.Property property = element.getPropertyNamed(token.substring(token.lastIndexOf("<") + 1, token.indexOf(">")));
				if (property != null && !visibleProperties.contains(property)) {
					visibleProperties.add(property);
				}
			}
		}

		if (visibleProperties.size() > 0) {
			StringBuffer sb = new StringBuffer();
			for (java.util.Iterator iter = visibleProperties.iterator(); iter.hasNext();) {
				Object o = iter.next();
				if (o instanceof edu.cmu.cs.stage3.alice.core.Property) {
					o = ((edu.cmu.cs.stage3.alice.core.Property) o).getName();
				}
				sb.append((String) o);
				sb.append(",");
			}
			sb.setLength(sb.length() - 1);
			element.data.put("edu.cmu.cs.stage3.alice.authoringtool.visibleProperties", sb.toString());
		}
	}

	@Override
	public void setEnabled(boolean b) {
		super.setEnabled(b);
		java.awt.Component[] children = subPanel.getComponents();
		for (Component element2 : children) {
			element2.setEnabled(b);
		}
	}

	protected edu.cmu.cs.stage3.alice.core.Property[] getUnsetProperties() {
		// HACK - special case for script, script definded, and comment
		// TODO: specify these in config file
		if (element instanceof edu.cmu.cs.stage3.alice.core.response.ScriptResponse || element instanceof edu.cmu.cs.stage3.alice.core.response.ScriptDefinedResponse || element instanceof edu.cmu.cs.stage3.alice.core.response.Comment) {
			return new edu.cmu.cs.stage3.alice.core.Property[0];
		}

		String[] propertiesToOmit = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getParameterizedPropertiesToOmit();
		java.util.List propertiesToOmitList = java.util.Arrays.asList(propertiesToOmit);

		java.util.LinkedList unsetProperties = new java.util.LinkedList();
		if (FormattedElementViewController.this.element != null) {
			edu.cmu.cs.stage3.alice.core.Property[] properties = FormattedElementViewController.this.element.getProperties();
			for (int i = 0; i < properties.length; i++) {
				if (!propertiesToOmitList.contains(properties[i].getName())) {
					if (!FormattedElementViewController.this.visibleProperties.contains(properties[i])) {
						unsetProperties.add(properties[i]);
					}
				}
			}
		}
		if (unsetProperties.size() > 0) {
			return (edu.cmu.cs.stage3.alice.core.Property[]) unsetProperties.toArray(new edu.cmu.cs.stage3.alice.core.Property[0]);
		} else {
			return new edu.cmu.cs.stage3.alice.core.Property[0];
		}
	}

	@Override
	public void paintForeground(java.awt.Graphics g) {
		super.paintForeground(g);
		if (element instanceof edu.cmu.cs.stage3.alice.core.Response) {
			if (((edu.cmu.cs.stage3.alice.core.Response) element).isCommentedOut.booleanValue()) {
				edu.cmu.cs.stage3.alice.authoringtool.util.GUIEffects.paintDisabledEffect(g, getBounds());
			}
		}
	}

	@Override
	public void goToSleep() {
		stopListening();
		sleeping = true;
	}

	@Override
	public void wakeUp() {
		startListening();
		sleeping = false;
	}

	@Override
	public void clean() {
		stopListening();
		element = null;
		setTransferable(null);
		removeAll();
	}

	@Override
	public void die() {
		clean();
	}

	@Override
	public void release() {
		super.release();
		edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.releaseGUI(this);
	}

	public void refreshGUI() {
		subPanel.removeAll();
		if (element != null) {
			calculateVisibleProperties();
			// add formatted tokens
			java.util.LinkedList unusedVisibleProperties = new java.util.LinkedList(visibleProperties);
			edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer formatTokenizer = new edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer(format);
			int i = 0;
			while (formatTokenizer.hasMoreTokens()) {
				String token = formatTokenizer.nextToken();
				javax.swing.JComponent gui = null;
				if (token.startsWith("<") && token.endsWith(">")) {
					edu.cmu.cs.stage3.alice.core.Property property = element.getPropertyNamed(token.substring(token.lastIndexOf("<") + 1, token.indexOf(">")));
					if (property != null) {
						if ((element instanceof edu.cmu.cs.stage3.alice.core.response.PropertyAnimation || element instanceof edu.cmu.cs.stage3.alice.core.question.PropertyValue) && property.getName().equals("propertyName")) {
							gui = new StringPropertyLabel((edu.cmu.cs.stage3.alice.core.property.StringProperty) property);
						} else if (element instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.PropertyAssignment && property.getName().equals("propertyName")) {
							gui = new StringPropertyLabel((edu.cmu.cs.stage3.alice.core.property.StringProperty) property);
						} else if (element instanceof edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse && property.getName().equals("userDefinedResponse")) {
							gui = new PropertyLabel(property);
						} else if (element instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion && property.getName().equals("userDefinedQuestion")) {
							gui = new PropertyLabel(property);
						} else {
							// why was the following line ever written?
							// boolean allowExpressions = !
							// String.class.isAssignableFrom(
							// property.getValueClass() );
							boolean allowExpressions = true;
							boolean omitName = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.shouldGUIOmitPropertyName(property);
							gui = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getPropertyViewController(property, true, allowExpressions, omitName, new SetPropertyImmediatelyFactory(property, false));
						}

						unusedVisibleProperties.remove(property);
					}
				} else {
					while (token.indexOf("&lt;") > -1) {
						token = new StringBuffer(token).replace(token.indexOf("&lt;"), token.indexOf("&lt;") + 4, "<").toString();
					}
					gui = new javax.swing.JLabel(token);
					gui.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 3, 0, 3));
					int fontSize = Integer.parseInt(authoringToolConfig.getValue("fontSize"));
					/*
					 * if (element instanceof
					 * edu.cmu.cs.stage3.alice.core.question
					 * .BinaryNumberResultingInBooleanQuestion || element
					 * instanceof edu.cmu.cs.stage3.alice.core.question.
					 * BinaryNumberResultingInNumberQuestion || element
					 * instanceof
					 * edu.cmu.cs.stage3.alice.core.question.IsEqualTo ||
					 * element instanceof
					 * edu.cmu.cs.stage3.alice.core.question.IsNotEqualTo){
					 * //((javax.swing.JLabel)gui).setFont( new java.awt.Font(
					 * "Courier", java.awt.Font.BOLD, (int)(18*fontSize/12.0) )
					 * );
					 * //((javax.swing.JLabel)gui).setForeground(java.awt.Color
					 * .black); } else if ("true".equalsIgnoreCase(
					 * (String)edu.cmu
					 * .cs.stage3.alice.authoringtool.AuthoringToolResources
					 * .getMiscItem( "javaLikeSyntax" ) ) && ( element
					 * instanceof edu.cmu.cs.stage3.alice.core.question.Not ||
					 * element instanceof
					 * edu.cmu.cs.stage3.alice.core.question.And || element
					 * instanceof edu.cmu.cs.stage3.alice.core.question.Or ||
					 * element instanceof
					 * edu.cmu.cs.stage3.alice.core.question.StringConcatQuestion
					 * ) ){ ((javax.swing.JLabel)gui).setFont( new
					 * java.awt.Font( "Courier", java.awt.Font.BOLD,
					 * (int)(18*fontSize/12.0) ) );
					 * ((javax.swing.JLabel)gui).setForeground
					 * (java.awt.Color.black); }else
					 */
					if (element instanceof edu.cmu.cs.stage3.alice.core.response.Comment || element instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Comment) { // TODO:
																																											// put
																																											// in
																																											// config
																																											// file
						((javax.swing.JLabel) gui).setForeground(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("commentForeground"));
						((javax.swing.JLabel) gui).setFont(new java.awt.Font("Helvetica", java.awt.Font.BOLD, (int) (13 * fontSize / 12.0)));
					}
				}
				if (gui != null) {
					subPanel.add(gui, new java.awt.GridBagConstraints(i++, 0, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 0, 0), 0, 0));
				}
			}

			// add remaining properties
			for (java.util.Iterator iter = unusedVisibleProperties.iterator(); iter.hasNext();) {
				edu.cmu.cs.stage3.alice.core.Property property = (edu.cmu.cs.stage3.alice.core.Property) iter.next();
				if (property != null) {
					javax.swing.JComponent gui = null;
					if (element instanceof edu.cmu.cs.stage3.alice.core.response.PropertyAnimation && property.getName().equals("propertyName")) {
						gui = new StringPropertyLabel((edu.cmu.cs.stage3.alice.core.property.StringProperty) property);
					} else if (element instanceof edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse && property.getName().equals("userDefinedResponse")) {
						gui = new PropertyLabel(property);
					} else {
						boolean allowExpressions = !String.class.isAssignableFrom(property.getValueClass());
						boolean omitName = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.shouldGUIOmitPropertyName(property);
						gui = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getPropertyViewController(property, true, allowExpressions, omitName, new SetPropertyImmediatelyFactory(property, false));
					}
					subPanel.add(gui, new java.awt.GridBagConstraints(i++, 0, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 0, 0), 0, 0));
				}
			}

			// add more tile
			boolean isUserDefined = FormattedElementViewController.this.element instanceof edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse || FormattedElementViewController.this.element instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion;
			if (getUnsetProperties().length > 0 && !isUserDefined) {
				subPanel.add(moreTile, new java.awt.GridBagConstraints(i++, 0, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 4, 0, 0), 0, 0));
			}

			subPanel.add(javax.swing.Box.createGlue(), new java.awt.GridBagConstraints(i++, 0, 1, 1, 1.0, 0.0, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.HORIZONTAL, new java.awt.Insets(0, 0, 0, 0), 0, 0));
		}

		revalidate();
		repaint();
	}

	public class SetPropertyImmediatelyFactory extends edu.cmu.cs.stage3.alice.authoringtool.util.SetPropertyImmediatelyFactory {
		protected boolean addToVisibleProperties;

		public SetPropertyImmediatelyFactory(edu.cmu.cs.stage3.alice.core.Property property, boolean addToVisibleProperties) {
			super(property);
			this.addToVisibleProperties = addToVisibleProperties;
		}

		@Override
		protected void run(Object value) {
			super.run(value);
			if (addToVisibleProperties) {
				visibleProperties.add(SetPropertyImmediatelyFactory.this.property);
				element.data.put("edu.cmu.cs.stage3.alice.authoringtool.visibleProperties", (String) element.data.get("edu.cmu.cs.stage3.alice.authoringtool.visibleProperties") + "," + property.getName());
				refreshGUI();
			}
		}
	}

	class MouseListener extends edu.cmu.cs.stage3.alice.authoringtool.util.CustomMouseAdapter {

		@Override
		public void popupResponse(java.awt.event.MouseEvent ev) {
			java.util.Vector structure = edu.cmu.cs.stage3.alice.authoringtool.util.ElementPopupUtilities.getDefaultStructure(element);
			if (structure != null && !structure.isEmpty()) {
				edu.cmu.cs.stage3.alice.authoringtool.util.ElementPopupUtilities.createAndShowElementPopupMenu(element, structure, FormattedElementViewController.this, ev.getX(), ev.getY());
			}
		}

		@Override
		public void doubleClickResponse(java.awt.event.MouseEvent ev) {
			if (element instanceof edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse) {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().editObject(((edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse) element).userDefinedResponse.getUserDefinedResponseValue(), FormattedElementViewController.this);
			} else if (element instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion) {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().editObject(((edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion) element).userDefinedQuestion.getUserDefinedQuestionValue(), FormattedElementViewController.this);
			}
		}
	}
}
