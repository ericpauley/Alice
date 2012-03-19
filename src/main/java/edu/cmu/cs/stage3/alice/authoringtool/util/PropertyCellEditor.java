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

import edu.cmu.cs.stage3.util.Enumerable;
import edu.cmu.cs.stage3.util.StringObjectPair;

/**
 * @deprecated
 */
@Deprecated
public class PropertyCellEditor implements javax.swing.table.TableCellEditor, javax.swing.event.CellEditorListener, javax.swing.event.PopupMenuListener {
	// protected javax.swing.DefaultCellEditor booleanEditor = new
	// javax.swing.DefaultCellEditor( new javax.swing.JComboBox( new Object [] {
	// "true", "false" } ) );
	protected EnumerableEditor enumerableEditor = new EnumerableEditor();
	protected ColorEditor colorEditor = new ColorEditor();
	protected ElementEditor elementEditor = new ElementEditor();
	protected NumberEditor numberEditor = new NumberEditor();
	protected BooleanEditor booleanEditor = new BooleanEditor();
	// protected FontEditor fontEditor = new FontEditor();
	protected javax.swing.DefaultCellEditor stringEditor = new javax.swing.DefaultCellEditor(new javax.swing.JTextField());
	protected DefaultEditor defaultEditor = new DefaultEditor();

	// protected javax.swing.DefaultCellEditor defaultEditor = new
	// javax.swing.DefaultCellEditor( new javax.swing.JTextField() );

	protected javax.swing.table.TableCellEditor currentEditor = null;
	protected Class currentValueClass = null;
	protected boolean isNullValid;
	protected javax.swing.event.EventListenerList listenerList = new javax.swing.event.EventListenerList();

	protected java.util.Hashtable classesToEditors = new java.util.Hashtable();
	protected edu.cmu.cs.stage3.alice.core.Element element = null;

	public PropertyCellEditor() {
		classesToEditors.put(java.awt.Color.class, colorEditor);
		classesToEditors.put(edu.cmu.cs.stage3.alice.scenegraph.Color.class, colorEditor); // TODO:
																							// create
																							// custom
																							// scenegraph.Color
																							// editor?
		classesToEditors.put(java.lang.Boolean.class, booleanEditor);
		classesToEditors.put(edu.cmu.cs.stage3.util.Enumerable.class, enumerableEditor);
		classesToEditors.put(edu.cmu.cs.stage3.alice.core.Element.class, elementEditor);
		classesToEditors.put(Number.class, numberEditor);
		classesToEditors.put(String.class, stringEditor);
		classesToEditors.put(edu.cmu.cs.stage3.alice.core.ReferenceFrame.class, elementEditor);
		// classesToEditors.put( java.awt.Font.class, fontEditor );

		for (java.util.Enumeration enum0 = classesToEditors.elements(); enum0.hasMoreElements();) {
			javax.swing.table.TableCellEditor editor = (javax.swing.table.TableCellEditor) enum0.nextElement();
			editor.removeCellEditorListener(this);
			editor.addCellEditorListener(this);
		}
		defaultEditor.addCellEditorListener(this);
	}

	public edu.cmu.cs.stage3.alice.core.Element getElement() {
		return element;
	}

	public void setElement(edu.cmu.cs.stage3.alice.core.Element element) {
		this.element = element;
	}

	@Override
	public Object getCellEditorValue() {
		if (currentEditor != null) {
			return currentEditor.getCellEditorValue();
		} else {
			return null;
		}
	}

	@Override
	public boolean isCellEditable(java.util.EventObject ev) {
		if (ev instanceof java.awt.event.MouseEvent) {
			return ((java.awt.event.MouseEvent) ev).getClickCount() >= 1;
		}
		return true;
	}

	@Override
	public boolean shouldSelectCell(java.util.EventObject anEvent) {
		// DEBUG System.out.println( "shouldSelectCell" );
		// DEBUG Thread.dumpStack();
		return true;
	}

	@Override
	public boolean stopCellEditing() {
		// DEBUG System.out.println( "stopCellEditing" );
		if (currentEditor != null) {
			return currentEditor.stopCellEditing();
		}
		return true;
	}

	@Override
	public void cancelCellEditing() {
		// DEBUG System.out.println( "cancelCellEditing" );
		if (currentEditor != null) {
			currentEditor.cancelCellEditing();
		}
	}

	@Override
	public void addCellEditorListener(javax.swing.event.CellEditorListener l) {
		listenerList.add(javax.swing.event.CellEditorListener.class, l);
	}

	@Override
	public void removeCellEditorListener(javax.swing.event.CellEditorListener l) {
		listenerList.remove(javax.swing.event.CellEditorListener.class, l);
	}

	@Override
	public java.awt.Component getTableCellEditorComponent(javax.swing.JTable table, Object value, boolean isSelected, int row, int column) {
		// DEBUG System.out.println( "getTableCellEditorComponent" );
		// DEBUG Thread.dumpStack();
		Class valueClass = null;
		javax.swing.table.TableModel model = table.getModel();
		if (model instanceof edu.cmu.cs.stage3.alice.authoringtool.util.TypedTableModel) {
			valueClass = ((edu.cmu.cs.stage3.alice.authoringtool.util.TypedTableModel) model).getTypeAt(row, column);
			isNullValid = ((edu.cmu.cs.stage3.alice.authoringtool.util.TypedTableModel) model).isNullValidAt(row, column);
		} else {
			isNullValid = true;
		}
		// DEBUG System.out.println( "valueClass: " + valueClass );
		if (valueClass == null) {
			if (value != null) {
				valueClass = value.getClass();
			}
		}
		// DEBUG System.out.println( "valueClass: " + valueClass );

		currentValueClass = valueClass;
		if (valueClass != null) {
			currentEditor = (javax.swing.table.TableCellEditor) classesToEditors.get(valueClass);
		} else {
			valueClass = Object.class;
			currentEditor = null;
		}
		/*
		 * the following isn't perfect... if there isn't an exact match of
		 * valueClass to editor, we just find the first thing that works. no
		 * attempt is made to find the best editor available. TODO: find best
		 * editor for class
		 */
		if (currentEditor == null) {
			for (java.util.Enumeration enum0 = classesToEditors.keys(); enum0.hasMoreElements();) {
				Class editorClass = (Class) enum0.nextElement();
				if (editorClass.isAssignableFrom(valueClass)) {
					currentEditor = (javax.swing.table.TableCellEditor) classesToEditors.get(editorClass);
					break;
				}
			}
		}
		if (currentEditor == null) {
			currentEditor = defaultEditor;
		}
		// DEBUG System.out.println( "currentEditor: " +
		// currentEditor.getClass() );

		java.awt.Component editorComponent = currentEditor.getTableCellEditorComponent(table, value, isSelected, row, column);
		return editorComponent;
	}

	@Override
	public void editingStopped(javax.swing.event.ChangeEvent changeEvent) {
		// hack for bug: 4234793
		hackPopupTimer.stop();

		// DEBUG System.out.println( "editingStopped" );
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == javax.swing.event.CellEditorListener.class) {
				if (changeEvent == null) {
					changeEvent = new javax.swing.event.ChangeEvent(this);
				}
				((javax.swing.event.CellEditorListener) listeners[i + 1]).editingStopped(changeEvent);
			}
		}
	}

	@Override
	public void editingCanceled(javax.swing.event.ChangeEvent changeEvent) {
		// DEBUG System.out.println( "editingCanceled" );
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == javax.swing.event.CellEditorListener.class) {
				if (changeEvent == null) {
					changeEvent = new javax.swing.event.ChangeEvent(this);
				}
				((javax.swing.event.CellEditorListener) listeners[i + 1]).editingCanceled(changeEvent);
			}
		}
	}

	// ////////////////////////////////////
	// PopupMenuListener interface
	// ////////////////////////////////////

	@Override
	public void popupMenuCanceled(javax.swing.event.PopupMenuEvent ev) {
		// currently not working because of bug: 4234793
		if (currentEditor != null) {
			currentEditor.cancelCellEditing();
		}
	}

	// hack for bug: 4234793
	private final javax.swing.Timer hackPopupTimer = new javax.swing.Timer(200, new java.awt.event.ActionListener() {
		@Override
		public void actionPerformed(java.awt.event.ActionEvent ev) {
			if (currentEditor != null) {
				currentEditor.cancelCellEditing();
			}
		}
	});

	@Override
	public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent ev) {
		// hack for bug: 4234793
		hackPopupTimer.setRepeats(false);
		hackPopupTimer.start();
	}
	@Override
	public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent ev) {
		// hack for bug: 4234793
		hackPopupTimer.stop();
	}

	// ///////////////////////
	// Sub editors
	// ///////////////////////

	class DefaultEditor extends javax.swing.DefaultCellEditor {
		protected Object currentObject = null;
		// protected edu.cmu.cs.stage3.alice.authoringtool.RunnableFactory
		// objectRunnableFactory;

		public DefaultEditor() {
			super(new javax.swing.JCheckBox());

			/*
			 * objectRunnableFactory = new
			 * edu.cmu.cs.stage3.alice.authoringtool.RunnableFactory() { public
			 * Runnable createRunnable( Object object ) { return new
			 * ObjectRunnable( object ); } };
			 */

			final javax.swing.JButton button = new javax.swing.JButton("");
			button.setBackground(java.awt.Color.white);
			button.setBorderPainted(false);
			button.setMargin(new java.awt.Insets(0, 0, 0, 0));

			editorComponent = button;

			button.addActionListener(createActionListener());
		}

		// the following protected methods allow subclasses to jump in at the
		// appropriate level
		// for customized behavior, while still using the DefaultEditor's
		// functionality where appropriate
		protected java.awt.event.ActionListener createActionListener() {
			return new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					javax.swing.JPopupMenu popup = createPopupMenu();
					if (popup != null) {
						popup.show(editorComponent, 0, 0);
						PopupMenuUtilities.ensurePopupIsOnScreen(popup);
						popup.addPopupMenuListener(PropertyCellEditor.this);
					}
				}
			};
		}

		protected javax.swing.JPopupMenu createPopupMenu() {
			java.util.Vector structure = createPopupStructure();
			if (structure != null) {
				return PopupMenuUtilities.makePopupMenu(structure);
			} else {
				return null;
			}
		}

		protected java.util.Vector createPopupStructure() {
			java.util.Vector structure = createExpressionStructure();
			if (structure != null && isNullValid) {
				if (structure.size() > 0) {
					structure.insertElementAt(new StringObjectPair("Separator", javax.swing.JSeparator.class), 0);
				}
				// structure.insertElementAt( new StringObjectPair( "<None>",
				// objectRunnableFactory.createRunnable( null ) ), 0 );
			}
			return structure;
		}

		protected java.util.Vector createExpressionStructure() {
			return null;
			// return PopupMenuUtilities.makeFlatElementStructure(
			// element.getRoot(), new
			// edu.cmu.cs.stage3.alice.core.criterion.ExpressionIsAssignableToCriterion(
			// PropertyCellEditor.this.currentValueClass ),
			// objectRunnableFactory );
		}

		@Override
		public Object getCellEditorValue() {
			return currentObject;
		}

		@Override
		public java.awt.Component getTableCellEditorComponent(javax.swing.JTable table, Object value, boolean isSelected, int row, int column) {
			currentObject = value;
			return editorComponent;
		}

		class ObjectRunnable implements Runnable {
			Object object;

			public ObjectRunnable(Object object) {
				this.object = object;
			}

			@Override
			public void run() {
				currentObject = object;
				fireEditingStopped();
			}
		}
	}

	class EnumerableEditor extends DefaultEditor {

		@Override
		protected java.util.Vector createPopupStructure() {
			java.util.Vector structure = new java.util.Vector();
			edu.cmu.cs.stage3.util.Enumerable[] items = edu.cmu.cs.stage3.util.Enumerable.getItems(currentValueClass);
			for (Enumerable item : items) {
				// structure.add( new edu.cmu.cs.stage3.util.StringObjectPair(
				// items[i].getRepr(), objectRunnableFactory.createRunnable(
				// items[i] ) ) );
			}

			java.util.Vector expressionStructure = createExpressionStructure();
			if (expressionStructure != null && expressionStructure.size() > 0) {
				String className = currentValueClass.getName();
				structure.add(new edu.cmu.cs.stage3.util.StringObjectPair("Seperator", javax.swing.JSeparator.class));
				structure.add(new edu.cmu.cs.stage3.util.StringObjectPair("Expressions which evaluate to " + className, expressionStructure));
			}

			if (isNullValid) {
				if (structure.size() > 0) {
					structure.insertElementAt(new StringObjectPair("Separator", javax.swing.JSeparator.class), 0);
				}
				// structure.insertElementAt( new StringObjectPair( "<None>",
				// objectRunnableFactory.createRunnable( null ) ), 0 );
			}

			return structure;
		}
	}

	class ColorEditor extends DefaultEditor {
		final javax.swing.JColorChooser colorChooser = new javax.swing.JColorChooser();
		java.awt.event.ActionListener okListener = new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				ColorEditor.this.currentObject = colorChooser.getColor();
			}
		};
		final javax.swing.JDialog dialog = javax.swing.JColorChooser.createDialog(editorComponent, "Pick a Color", true, colorChooser, okListener, null);

		Runnable customRunnable = new Runnable() {
			@Override
			public void run() {
				// hack for bug: 4234793
				hackPopupTimer.stop();
				if (currentObject instanceof java.awt.Color) {
					colorChooser.setColor((java.awt.Color) currentObject);
				}
				// dialog.setLocationRelativeTo( comboBox );
				dialog.show();
				fireEditingStopped();
			}
		};

		@Override
		protected javax.swing.JPopupMenu createPopupMenu() {
			javax.swing.JMenu menu = new javax.swing.JMenu("");

			javax.swing.JMenuItem item;

			/*
			 * item = new javax.swing.JMenuItem( "white",
			 * ColorRenderer.getIconFromColor( java.awt.Color.white, 16, 10 ) );
			 * item.addActionListener(
			 * PopupMenuUtilities.getPopupMenuItemActionListener(
			 * objectRunnableFactory.createRunnable( java.awt.Color.white ) ) );
			 * menu.add( item ); item = new javax.swing.JMenuItem( "black",
			 * ColorRenderer.getIconFromColor( java.awt.Color.black, 16, 10 ) );
			 * item.addActionListener(
			 * PopupMenuUtilities.getPopupMenuItemActionListener(
			 * objectRunnableFactory.createRunnable( java.awt.Color.black ) ) );
			 * menu.add( item ); item = new javax.swing.JMenuItem( "red",
			 * ColorRenderer.getIconFromColor( java.awt.Color.red, 16, 10 ) );
			 * item.addActionListener(
			 * PopupMenuUtilities.getPopupMenuItemActionListener(
			 * objectRunnableFactory.createRunnable( java.awt.Color.red ) ) );
			 * menu.add( item ); item = new javax.swing.JMenuItem( "green",
			 * ColorRenderer.getIconFromColor( java.awt.Color.green, 16, 10 ) );
			 * item.addActionListener(
			 * PopupMenuUtilities.getPopupMenuItemActionListener(
			 * objectRunnableFactory.createRunnable( java.awt.Color.green ) ) );
			 * menu.add( item ); item = new javax.swing.JMenuItem( "blue",
			 * ColorRenderer.getIconFromColor( java.awt.Color.blue, 16, 10 ) );
			 * item.addActionListener(
			 * PopupMenuUtilities.getPopupMenuItemActionListener(
			 * objectRunnableFactory.createRunnable( java.awt.Color.blue ) ) );
			 * menu.add( item ); item = new javax.swing.JMenuItem( "yellow",
			 * ColorRenderer.getIconFromColor( java.awt.Color.yellow, 16, 10 )
			 * ); item.addActionListener(
			 * PopupMenuUtilities.getPopupMenuItemActionListener(
			 * objectRunnableFactory.createRunnable( java.awt.Color.yellow ) )
			 * ); menu.add( item ); item = new javax.swing.JMenuItem( "magenta",
			 * ColorRenderer.getIconFromColor( java.awt.Color.magenta, 16, 10 )
			 * ); item.addActionListener(
			 * PopupMenuUtilities.getPopupMenuItemActionListener(
			 * objectRunnableFactory.createRunnable( java.awt.Color.magenta ) )
			 * ); menu.add( item ); item = new javax.swing.JMenuItem( "cyan",
			 * ColorRenderer.getIconFromColor( java.awt.Color.cyan, 16, 10 ) );
			 * item.addActionListener(
			 * PopupMenuUtilities.getPopupMenuItemActionListener(
			 * objectRunnableFactory.createRunnable( java.awt.Color.cyan ) ) );
			 * menu.add( item ); item = new javax.swing.JMenuItem( "orange",
			 * ColorRenderer.getIconFromColor( java.awt.Color.orange, 16, 10 )
			 * ); item.addActionListener(
			 * PopupMenuUtilities.getPopupMenuItemActionListener(
			 * objectRunnableFactory.createRunnable( java.awt.Color.orange ) )
			 * ); menu.add( item ); item = new javax.swing.JMenuItem( "pink",
			 * ColorRenderer.getIconFromColor( java.awt.Color.pink, 16, 10 ) );
			 * item.addActionListener(
			 * PopupMenuUtilities.getPopupMenuItemActionListener(
			 * objectRunnableFactory.createRunnable( java.awt.Color.pink ) ) );
			 * menu.add( item ); item = new javax.swing.JMenuItem( "lightGray",
			 * ColorRenderer.getIconFromColor( java.awt.Color.lightGray, 16, 10
			 * ) ); item.addActionListener(
			 * PopupMenuUtilities.getPopupMenuItemActionListener(
			 * objectRunnableFactory.createRunnable( java.awt.Color.lightGray )
			 * ) ); menu.add( item ); item = new javax.swing.JMenuItem( "gray",
			 * ColorRenderer.getIconFromColor( java.awt.Color.gray, 16, 10 ) );
			 * item.addActionListener(
			 * PopupMenuUtilities.getPopupMenuItemActionListener(
			 * objectRunnableFactory.createRunnable( java.awt.Color.gray ) ) );
			 * menu.add( item ); item = new javax.swing.JMenuItem( "darkGray",
			 * ColorRenderer.getIconFromColor( java.awt.Color.darkGray, 16, 10 )
			 * ); item.addActionListener(
			 * PopupMenuUtilities.getPopupMenuItemActionListener(
			 * objectRunnableFactory.createRunnable( java.awt.Color.darkGray ) )
			 * ); menu.add( item );
			 * 
			 * item = new javax.swing.JMenuItem( "Custom..." );
			 * item.addActionListener(
			 * PopupMenuUtilities.getPopupMenuItemActionListener( customRunnable
			 * ) ); menu.add( item );
			 * 
			 * java.util.Vector expressionStructure =
			 * createExpressionStructure(); if( (expressionStructure != null) &&
			 * (expressionStructure.size() > 0) ) { javax.swing.JMenu submenu =
			 * PopupMenuUtilities.makeMenu(
			 * "Expressions which evaluate to Color", expressionStructure ); if(
			 * submenu != null ) { menu.addSeparator(); menu.add( submenu ); } }
			 */

			return menu.getPopupMenu();
		}

		@Override
		public java.awt.Component getTableCellEditorComponent(javax.swing.JTable table, Object value, boolean isSelected, int row, int column) {
			if (value instanceof edu.cmu.cs.stage3.alice.scenegraph.Color) {
				edu.cmu.cs.stage3.alice.scenegraph.Color c = (edu.cmu.cs.stage3.alice.scenegraph.Color) value;
				value = new java.awt.Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
			}
			return super.getTableCellEditorComponent(table, value, isSelected, row, column);
		}
	}

	class ElementEditor extends DefaultEditor {

		@Override
		protected java.util.Vector createPopupStructure() {
			/*
			 * if( PropertyCellEditor.this.currentValueClass ==
			 * edu.cmu.cs.stage3.alice.core.Expression.class ) {
			 * java.util.Vector structure =
			 * PopupMenuUtilities.makeElementStructure(
			 * PropertyCellEditor.this.element.getRoot(), new
			 * edu.cmu.cs.stage3.util.InstanceOfCriterion(
			 * edu.cmu.cs.stage3.alice.core.Expression.class ),
			 * objectRunnableFactory );
			 * 
			 * if( (structure != null) && isNullValid ) { if( structure.size() >
			 * 0 ) { structure.insertElementAt( new StringObjectPair(
			 * "Separator", javax.swing.JSeparator.class ), 0 ); }
			 * structure.insertElementAt( new StringObjectPair( "<None>",
			 * objectRunnableFactory.createRunnable( null ) ), 0 ); }
			 * 
			 * return structure; } else { java.util.Vector structure = null; if(
			 * (edu.cmu.cs.stage3.alice.core.ReferenceFrame.class ==
			 * PropertyCellEditor.this.currentValueClass) ||
			 * (edu.cmu.cs.stage3.alice.core.Element.class ==
			 * PropertyCellEditor.this.currentValueClass) ||
			 * (edu.cmu.cs.stage3.alice.core.Transformable.class ==
			 * PropertyCellEditor.this.currentValueClass) ) { structure =
			 * PopupMenuUtilities.makeElementStructure(
			 * PropertyCellEditor.this.element.getRoot(), new
			 * edu.cmu.cs.stage3.util.InstanceOfCriterion(
			 * PropertyCellEditor.this.currentValueClass ),
			 * objectRunnableFactory ); } else { structure =
			 * PopupMenuUtilities.makeFlatElementStructure(
			 * PropertyCellEditor.this.element.getRoot(), new
			 * edu.cmu.cs.stage3.util.InstanceOfCriterion(
			 * PropertyCellEditor.this.currentValueClass ),
			 * objectRunnableFactory ); } if( structure == null ) { structure =
			 * new java.util.Vector(); }
			 * 
			 * java.util.Vector expressionStructure =
			 * createExpressionStructure(); if( (expressionStructure != null) &&
			 * (expressionStructure.size() > 0) ) { String className =
			 * PropertyCellEditor.this.currentValueClass.getName(); if(
			 * structure.size() > 0 ) { structure.add( new
			 * edu.cmu.cs.stage3.util.StringObjectPair( "Seperator",
			 * javax.swing.JSeparator.class ) ); } structure.add( new
			 * edu.cmu.cs.stage3.util.StringObjectPair(
			 * "Expressions which evaluate to " + className, expressionStructure
			 * ) ); }
			 * 
			 * if( isNullValid ) { if( structure.size() > 0 ) {
			 * structure.insertElementAt( new StringObjectPair( "Separator",
			 * javax.swing.JSeparator.class ), 0 ); } structure.insertElementAt(
			 * new StringObjectPair( "<None>",
			 * objectRunnableFactory.createRunnable( null ) ), 0 ); }
			 * 
			 * return structure; }
			 */
			return null;
		}
	}

	class NumberEditor extends javax.swing.DefaultCellEditor {
		protected Object currentNumber = null;
		// protected edu.cmu.cs.stage3.alice.authoringtool.RunnableFactory
		// numberExpressionRunnableFactory;
		protected javax.swing.JPanel panel = new javax.swing.JPanel();
		protected javax.swing.JTextField textField = new javax.swing.JTextField();
		protected javax.swing.JButton button = new javax.swing.JButton("Element...");

		public NumberEditor() {
			super(new javax.swing.JCheckBox());

			panel.setLayout(new java.awt.BorderLayout());
			panel.add(textField, java.awt.BorderLayout.CENTER);
			panel.add(button, java.awt.BorderLayout.EAST);

			editorComponent = panel;

			textField.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					String input = textField.getText();
					try {
						Double value = Double.valueOf(input);
						currentNumber = value;
					} catch (NumberFormatException ex) {
						// TODO: load from Element name
						if (currentNumber != null) {
							textField.setText(currentNumber.toString());
						} else {
							textField.setText("");
						}
					}
					NumberEditor.this.fireEditingStopped();
				}
			});

			// big problems with the whole focus thing
			// textField.addFocusListener( PropertyCellEditor.this );

			button.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					java.util.Vector structure = null;
					// structure = PopupMenuUtilities.makeFlatElementStructure(
					// element.getRoot(), new
					// edu.cmu.cs.stage3.alice.core.criterion.ExpressionIsAssignableToCriterion(
					// PropertyCellEditor.this.currentValueClass ),
					// numberExpressionRunnableFactory );
					if (structure != null) {
						javax.swing.JMenu menu = PopupMenuUtilities.makeMenu("", structure);
						if (menu != null) {
							javax.swing.JPopupMenu popup = menu.getPopupMenu();
							popup.show(button, 0, 0);
							PopupMenuUtilities.ensurePopupIsOnScreen(popup);
							popup.addPopupMenuListener(PropertyCellEditor.this);
						}
					}
				}
			});
		}

		@Override
		public Object getCellEditorValue() {
			return currentNumber;
		}

		@Override
		public java.awt.Component getTableCellEditorComponent(javax.swing.JTable table, Object value, boolean isSelected, int row, int column) {
			currentNumber = value;

			if (currentNumber instanceof Number) {
				textField.setText(currentNumber.toString());
			} else {
				textField.setText("");
			}

			edu.cmu.cs.stage3.alice.core.Element[] elements = element.getRoot().search(new edu.cmu.cs.stage3.alice.core.criterion.ExpressionIsAssignableToCriterion(Number.class));
			if (elements.length > 0) {
				button.setEnabled(true);
			} else {
				button.setEnabled(false);
			}

			return editorComponent;
		}

		class NumberExpressionRunnable implements Runnable {
			edu.cmu.cs.stage3.alice.core.Expression expression;

			public NumberExpressionRunnable(edu.cmu.cs.stage3.alice.core.Expression expression) {
				this.expression = expression;
			}

			@Override
			public void run() {
				if (Number.class.isAssignableFrom(expression.getValueClass())) {
					currentNumber = expression;
				}
				fireEditingStopped();
			}
		}
	}

	class BooleanEditor extends DefaultEditor {

		@Override
		protected java.util.Vector createPopupStructure() {
			java.util.Vector structure = new java.util.Vector();

			// structure.add( new edu.cmu.cs.stage3.util.StringObjectPair(
			// "True", objectRunnableFactory.createRunnable( Boolean.TRUE ) ) );
			// structure.add( new edu.cmu.cs.stage3.util.StringObjectPair(
			// "False", objectRunnableFactory.createRunnable( Boolean.FALSE ) )
			// );

			java.util.Vector expressionStructure = createExpressionStructure();
			if (expressionStructure != null && expressionStructure.size() > 0) {
				structure.add(new edu.cmu.cs.stage3.util.StringObjectPair("Seperator", javax.swing.JSeparator.class));
				structure.add(new edu.cmu.cs.stage3.util.StringObjectPair("Expressions which evaluate to Boolean", expressionStructure));
			}

			return structure;
		}
	}

	/*
	 * TODO: if we ever bring back 3D Text class FontEditor extends
	 * javax.swing.DefaultCellEditor { java.awt.Font currentFont = null;
	 * 
	 * public FontEditor() { super( new javax.swing.JCheckBox() );
	 * 
	 * final javax.swing.JButton button = new javax.swing.JButton( "" );
	 * button.setBackground( java.awt.Color.white );
	 * 
	 * this.editorComponent = button;
	 * 
	 * button.addActionListener( new java.awt.event.ActionListener() { public
	 * void actionPerformed( java.awt.event.ActionEvent e ) {
	 * edu.cmu.cs.stage3.alice.authoringtool.util.FontChooser fc = new
	 * edu.cmu.cs.stage3.alice.authoringtool.util.FontChooser( button,
	 * "Choose a font" ); fc.setFontValue( currentFont );
	 * fc.setLocationRelativeTo( button ); fc.show(); if( fc.getLastAction() ==
	 * edu.cmu.cs.stage3.alice.authoringtool.util.FontChooser.OK_ACTION ) {
	 * FontEditor.this.currentFont = fc.getFontValue(); } fireEditingStopped();
	 * } } ); }
	 * 
	 * public Object getCellEditorValue() { return currentFont; }
	 * 
	 * public java.awt.Component getTableCellEditorComponent( javax.swing.JTable
	 * table, Object value, boolean isSelected, int row, int column ) {
	 * currentFont = (java.awt.Font)value; return editorComponent; } }
	 */
}