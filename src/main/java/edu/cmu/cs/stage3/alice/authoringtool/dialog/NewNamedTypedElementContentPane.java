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

import java.awt.GridBagConstraints;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;

/**
 * @author Jason Pratt, Dennis Cosgrove, Dave Culyba
 */
public abstract class NewNamedTypedElementContentPane extends NewNamedElementContentPane {
	private static final int INSET = 8;

	private edu.cmu.cs.stage3.alice.authoringtool.util.TypeChooser m_typeChooser;
	private edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory factory;
	private edu.cmu.cs.stage3.alice.core.Element context;

	private javax.swing.JPanel valuePanel;
	private javax.swing.JComponent placeholder;
	private javax.swing.JComponent pad;
	private javax.swing.JComponent valueComponent;
	private javax.swing.JComponent valuesComponent;

	protected javax.swing.JLabel valueLabel;
	private javax.swing.JComponent valueViewController;
	public javax.swing.JCheckBox makeCollectionCheckBox;
	private javax.swing.JComboBox collectionTypeCombo;

	private edu.cmu.cs.stage3.alice.authoringtool.util.ObjectArrayPropertyEditor objectArrayPropertyEditor;
	private javax.swing.JScrollPane objectArrayScrollPane;

	private edu.cmu.cs.stage3.alice.core.Variable m_variable;
	protected boolean listsOnly;
	protected boolean showValue;

	protected void initVariables() {
		listsOnly = false;
		showValue = true;
	}

	@Override
	protected void initTopComponents(java.awt.GridBagConstraints gbc) {
		super.initTopComponents(gbc);
		initVariables();

		edu.cmu.cs.stage3.alice.authoringtool.util.Configuration authoringToolConfig = edu.cmu.cs.stage3.alice.authoringtool.util.Configuration.getLocalConfiguration(AuthoringTool.class.getPackage());

		m_typeChooser = new edu.cmu.cs.stage3.alice.authoringtool.util.TypeChooser(getValidityChecker());

		valuePanel = new javax.swing.JPanel();
		valuePanel.setLayout(new java.awt.GridBagLayout());
		valueLabel = new javax.swing.JLabel();
		valuesComponent = new javax.swing.JPanel();
		valuesComponent.setBorder(null);
		valuesComponent.setOpaque(false);
		valuesComponent.setLayout(new java.awt.BorderLayout());

		valueComponent = new javax.swing.JPanel();
		valueComponent.setBorder(null);
		valueComponent.setOpaque(false);
		valueComponent.setLayout(new java.awt.BorderLayout());

		m_variable = new edu.cmu.cs.stage3.alice.core.Variable();
		factory = new edu.cmu.cs.stage3.alice.authoringtool.util.SetPropertyImmediatelyFactory(m_variable.value);

		objectArrayPropertyEditor = new edu.cmu.cs.stage3.alice.authoringtool.util.ObjectArrayPropertyEditor();
		objectArrayScrollPane = new javax.swing.JScrollPane(objectArrayPropertyEditor);
		objectArrayScrollPane.setPreferredSize(new java.awt.Dimension(1, 150));

		makeCollectionCheckBox = new javax.swing.JCheckBox("make a");
		int fontSize = Integer.parseInt(authoringToolConfig.getValue("fontSize"));
		makeCollectionCheckBox.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, (int) (12 * fontSize / 12.0)));
		makeCollectionCheckBox.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				refreshValuePanel();
			}
		});
		m_typeChooser.addChangeListener(new javax.swing.event.ChangeListener() {
			@Override
			public void stateChanged(javax.swing.event.ChangeEvent ev) {
				refreshValuePanel();
			}
		});

		collectionTypeCombo = new javax.swing.JComboBox();
		collectionTypeCombo.addItem(edu.cmu.cs.stage3.alice.core.List.class);
		collectionTypeCombo.addItem(edu.cmu.cs.stage3.alice.core.Array.class);
		collectionTypeCombo.setRenderer(new javax.swing.DefaultListCellRenderer() {

			@Override
			public java.awt.Component getListCellRendererComponent(javax.swing.JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				if (value instanceof Class) {
					String className = ((Class) value).getName();
					value = className.substring(className.lastIndexOf(".") + 1);
				}
				return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			}
		});
		collectionTypeCombo.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent ev) {
				refreshValuePanel();
			}
		});
		pad = new javax.swing.JPanel();
		placeholder = new javax.swing.JPanel();

		gbc.gridwidth = java.awt.GridBagConstraints.RELATIVE;
		gbc.insets.right = 0;
		add(new javax.swing.JLabel("Type:"), gbc);
		gbc.insets.right = 8;
		gbc.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		gbc.weightx = 1.0;
		add(m_typeChooser, gbc);
		gbc.weightx = 0.0;
		gbc.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		layoutValuePanel();
		add(valuePanel, gbc);
	}

	@Override
	public void reset(edu.cmu.cs.stage3.alice.core.Element context) {
		super.reset(context);
		this.context = context;
		m_variable = new edu.cmu.cs.stage3.alice.core.Variable();
		factory = new edu.cmu.cs.stage3.alice.authoringtool.util.SetPropertyImmediatelyFactory(m_variable.value);
		layoutValuePanel();
	}

	@Override
	public void preDialogShow(javax.swing.JDialog dialog) {
		super.preDialogShow(dialog);
		if (!getListsOnly()) {
			makeCollectionCheckBox.setSelected(false);
		}
	}

	@Override
	public void postDialogShow(javax.swing.JDialog dialog) {
		super.postDialogShow(dialog);
	}

	@Override
	public boolean isReadyToDispose(int option) {
		if (option == edu.cmu.cs.stage3.swing.ContentPane.OK_OPTION) {
			m_typeChooser.addCurrentTypeToList();
		} else if (option == edu.cmu.cs.stage3.swing.ContentPane.CANCEL_OPTION) {
			m_variable = null;
		} else {
			m_variable = null;
		}
		return true;
	}

	public boolean getShowValue() {
		return showValue;
	}

	public boolean getListsOnly() {
		return listsOnly;
	}

	private void updateCollection() {
		Class type = m_typeChooser.getType();
		Class collectionType = (Class) collectionTypeCombo.getSelectedItem();
		edu.cmu.cs.stage3.alice.core.Collection collection = null;
		if (m_variable.value.get() != null && collectionType.isAssignableFrom(m_variable.value.get().getClass())) {
			collection = (edu.cmu.cs.stage3.alice.core.Collection) m_variable.value.get();
			if (collection.valueClass.getClassValue() != type) {
				collection.values.clear();
				collection.valueClass.set(type);
			}
		} else {
			try {
				collection = (edu.cmu.cs.stage3.alice.core.Collection) collectionType.newInstance();
			} catch (Exception e) {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("Could not create a collection of type " + collectionType, e);
				collection = new edu.cmu.cs.stage3.alice.core.List();
			}
			collection.valueClass.set(type);
			// The view controller does not like it when we change the value of
			// the variable behind the scenes
			if (valueViewController instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.PropertyViewController) {
				((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.PropertyViewController) valueViewController).release();
				valueViewController = null;
			}

			m_variable.value.set(null);
			m_variable.valueClass.set(collectionType);
			m_variable.value.set(collection);
		}
		objectArrayPropertyEditor.setType(type);
		objectArrayPropertyEditor.setObjectArrayProperty(collection.values);
	}

	private void updateVariableValue() {
		Class type = m_typeChooser.getType();
		Object currentValue = m_variable.value.get();
		if (currentValue == null || !type.isAssignableFrom(currentValue.getClass())) {
			if (valueViewController instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.PropertyViewController) {
				((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.PropertyViewController) valueViewController).release();
				valueViewController = null;
			}
			m_variable.value.set(null);
			m_variable.valueClass.set(type);
			m_variable.value.set(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getDefaultValueForClass(type));
			// if( Boolean.class.isAssignableFrom( type ) ) {
			// m_variable.value.set( Boolean.TRUE );
			// } else if( Double.class.isAssignableFrom( type ) ) {
			// m_variable.value.set( new Double( 1.0 ) );
			// } else if(
			// edu.cmu.cs.stage3.alice.scenegraph.Color.class.isAssignableFrom(
			// type ) ) {
			// m_variable.value.set(
			// edu.cmu.cs.stage3.alice.scenegraph.Color.WHITE );
			// } else if(
			// edu.cmu.cs.stage3.util.Enumerable.class.isAssignableFrom( type )
			// ) {
			// edu.cmu.cs.stage3.util.Enumerable[] items =
			// edu.cmu.cs.stage3.util.Enumerable.getItems( type );
			// if( items.length > 0 ) {
			// m_variable.value.set( items[0] );
			// } else {
			// m_variable.value.set( null );
			// }
			// } else {
			// m_variable.value.set( null );
			// }
			valueViewController = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getPropertyViewController(m_variable.value, true, false, true, factory);
			if (valueViewController instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ElementPropertyViewController) {
				((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ElementPropertyViewController) valueViewController).setRoot(context.getRoot());
			}
		}

	}

	private void layoutValuePanel() {
		valuePanel.removeAll();
		if (listsOnly) {
			makeCollectionCheckBox.setSelected(true);
			collectionTypeCombo.setSelectedItem(edu.cmu.cs.stage3.alice.core.List.class);
			makeCollectionCheckBox.setEnabled(false);
			collectionTypeCombo.setEnabled(false);
		} else {
			makeCollectionCheckBox.setSelected(false);
			makeCollectionCheckBox.setEnabled(true);
			collectionTypeCombo.setEnabled(true);
		}
		GridBagConstraints gbcValue = new GridBagConstraints();
		gbcValue.fill = GridBagConstraints.BOTH;
		gbcValue.insets.top = INSET;
		if (showValue) {
			gbcValue.insets.left = INSET;
			gbcValue.insets.right = INSET;
			valuePanel.add(valueLabel, gbcValue);
			valuePanel.add(valueComponent, gbcValue);
			gbcValue.weightx = 1.0;
			valuePanel.add(pad, gbcValue);
			gbcValue.weightx = 0.0;
			gbcValue.insets.right = 0;
			valuePanel.add(makeCollectionCheckBox, gbcValue);
			gbcValue.gridwidth = GridBagConstraints.REMAINDER;
			gbcValue.insets.left = INSET;
			gbcValue.insets.right = INSET;
			valuePanel.add(collectionTypeCombo, gbcValue);
			gbcValue.weightx = 0.0;
			gbcValue.weighty = 1.0;
			valuePanel.add(valuesComponent, gbcValue);
		} else {
			gbcValue.insets.right = INSET;
			gbcValue.weightx = 1.0;
			valuePanel.add(pad, gbcValue);
			gbcValue.weightx = 0.0;
			valuePanel.add(makeCollectionCheckBox, gbcValue);
			gbcValue.gridwidth = GridBagConstraints.REMAINDER;
			valuePanel.add(collectionTypeCombo, gbcValue);
			gbcValue.weighty = 1.0;
			valuePanel.add(placeholder, gbcValue);
		}
		refreshValuePanel();
	}

	private void refreshValuePanel() {
		if (m_typeChooser.getType() != null) {
			if (makeCollectionCheckBox.isSelected()) {
				updateCollection();
			} else {
				updateVariableValue();
			}
		}
		valuesComponent.removeAll();
		valueComponent.removeAll();
		if (makeCollectionCheckBox.isSelected()) {
			valueLabel.setText("Values:");
			valueComponent.add(placeholder, java.awt.BorderLayout.CENTER);
			valuesComponent.add(objectArrayScrollPane, java.awt.BorderLayout.CENTER);
		} else {
			valueLabel.setText("Value:");
			if (valueViewController != null) {
				valueComponent.add(valueViewController, java.awt.BorderLayout.CENTER);
			}
			valuesComponent.add(placeholder, java.awt.BorderLayout.CENTER);
		}
		packDialog();
	}

	public void setListsOnly(boolean b) {
		if (listsOnly != b) {
			listsOnly = b;
			layoutValuePanel();
		}
	}

	public void setShowValue(boolean showValue) {
		if (this.showValue != showValue) {
			this.showValue = showValue;
			layoutValuePanel();
		}
	}

	public edu.cmu.cs.stage3.alice.core.Variable getVariable() {
		if (m_variable == null) {
			return null;
		}
		if ((makeCollectionCheckBox.isSelected() || listsOnly) && m_variable.value.get() instanceof edu.cmu.cs.stage3.alice.core.Collection) {
			m_variable.addChild((edu.cmu.cs.stage3.alice.core.Collection) m_variable.value.get());
		}

		m_variable.name.set(getNameValue());
		return m_variable;
	}

	public Class getTypeValue() {
		return m_typeChooser.getType();
	}
}