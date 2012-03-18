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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.*;

import java.beans.*;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import java.util.*;

import javax.accessibility.*;

import javax.swing.event.*;
import javax.swing.plaf.*;

import javax.swing.*;

/**
 * adapted from javax.swing.JMenu to improve on popup menu behavior.
 * proper subclassing was prevented by private data and methods.
 * @author Jason Pratt
 */

public class AliceMenu extends javax.swing.JMenu implements Accessible, MenuElement {
	/**
	 * @see #getUIClassID
	 * @see #readObject
	 */
	private static final String uiClassID = "MenuUI";

	/*
	 * The popup menu portion of the menu.
	 */
	private AlicePopupMenu popupMenu;

	/*
	 * The button's model listeners.  Default is <code>null</code>.
	 */
	private ChangeListener menuChangeListener = null;

	/*
	 * Only one <code>MenuEvent</code> is needed for each menu since the
	 * event's only state is the source property.  The source of events
	 * generated is always "this".  Default is <code>null</code>.
	 */
	private MenuEvent menuEvent = null;

	/* Registry of listeners created for <code>Action-JMenuItem</code>
	 * linkage.  This is needed so that references can
	 * be cleaned up at remove time to allow garbage collection
	 * Default is <code>null</code>.
	 */
	private static Hashtable listenerRegistry = null;

	/*
	 * Used by the look and feel (L&F) code to handle
	 * implementation specific menu behaviors.
	 */
	private int delay;

	/**
	 * Set to true when a KEY_PRESSED event is received and the menu is
	 * selected, and false when a KEY_RELEASED (or focus lost) is received.
	 * If processKeyEvent is invoked with a KEY_TYPED or KEY_RELEASED event,
	 * and this is false, a MenuKeyEvent is NOT created. This is needed to
	 * avoid activating a menuitem when the menu and menuitem share the
	 * same mnemonic
	 */
	private boolean receivedKeyPressed;

	/* Diagnostic aids -- should be false for production builds. */
	private static final boolean TRACE = false;  // trace creates and disposes
	private static final boolean VERBOSE = false;  // show reuse hits/misses
	private static final boolean DEBUG = false;  // show bad params, misc.

	/**
	 * Constructs a new <code>AliceMenu</code> with no text.
	 */
	public AliceMenu() {
		this( "" );
	}
	/**
	 * Constructs a new <code>AliceMenu</code> with the supplied string
	 * as its text.
	 *
	 * @param s  the text for the menu label
	 */
	public AliceMenu( String s ) {
		super( s );
	}
	/**
	 * Constructs a menu whose properties are taken from the
	 * <code>Action</code> supplied.
	 * @param a an <code>Action</code>
	 *
	 * @since 1.3
	 */
	public AliceMenu( Action a ) {
		this();
		setAction( a );
	}
	/**
	 * Constructs a new <code>AliceMenu</code> with the supplied string as
	 * its text and specified as a tear-off menu or not.
	 *
	 * @param s the text for the menu label
	 * @param b can the menu be torn off (not yet implemented)
	 */
	public AliceMenu( String s, boolean b ) {
		this( s );
	}
	/**
	 * Notification from the <code>UIFactory</code> that the L&F has changed.
	 * Called to replace the UI with the latest version from the
	 * <code>UIFactory</code>.
	 *
	 * @see JComponent#updateUI
	 */
	
	public void updateUI() {
		setUI( (MenuItemUI)UIManager.getUI( this ) );

		if( popupMenu != null ) {
			popupMenu.setUI( (PopupMenuUI)UIManager.getUI( popupMenu ) );
		}
	}
	/**
	 * Returns the name of the L&F class that renders this component.
	 *
	 * @return the string "MenuUI"
	 * @see JComponent#getUIClassID
	 * @see UIDefaults#getUI
	 */
	
	public String getUIClassID() {
		return uiClassID;
	}
	//    public void repaint(long tm, int x, int y, int width, int height) {
	//        Thread.currentThread().dumpStack();
	//        super.repaint(tm,x,y,width,height);
	//    }
	/**
	 * Sets the data model for the "menu button" -- the label
	 * that the user clicks to open or close the menu.
	 *
	 * @param newModel the <code>ButtonModel</code>
	 * @see #getModel
	 * @beaninfo
	 * description: The menu's model
	 *       bound: true
	 *      expert: true
	 *      hidden: true
	 */
	
	public void setModel( ButtonModel newModel ) {
		ButtonModel oldModel = getModel();
		super.setModel( newModel );

		if( ( oldModel != null ) && ( menuChangeListener != null ) ) {
			oldModel.removeChangeListener( menuChangeListener );
			menuChangeListener = null;
		}

		model = newModel;

		if( newModel != null ) {
			menuChangeListener = createMenuChangeListener();
			newModel.addChangeListener( menuChangeListener );
		}
	}
	/**
	 * Returns true if the menu is currently selected (highlighted).
	 *
	 * @return true if the menu is selected, else false
	 */
	
	public boolean isSelected() {
		return getModel().isSelected();
	}
	/**
	 * Sets the selection status of the menu.
	 *
	 * @param b  true to select (highlight) the menu; false to de-select
	 *        the menu
	 * @beaninfo
	 *      description: When the menu is selected, its popup child is shown.
	 *           expert: true
	 *           hidden: true
	 */
	
	public void setSelected( boolean b ) {
		ButtonModel model = getModel();
		boolean oldValue = model.isSelected();

		if( ( accessibleContext != null ) && ( oldValue != b ) ) {
			if( b ) {
				accessibleContext.firePropertyChange( AccessibleContext.ACCESSIBLE_STATE_PROPERTY, null, AccessibleState.SELECTED );
			} else {
				accessibleContext.firePropertyChange( AccessibleContext.ACCESSIBLE_STATE_PROPERTY, AccessibleState.SELECTED, null );
			}
		}
		if( b != model.isSelected() ) {
			getModel().setSelected( b );
		}
	}
	/**
	 * Returns true if the menu's popup window is visible.
	 *
	 * @return true if the menu is visible, else false
	 */
	
	public boolean isPopupMenuVisible() {
		ensurePopupMenuCreated();

		return popupMenu.isVisible();
	}
	/**
	 * Sets the visibility of the menu's popup.  If the menu is
	 * not enabled, this method will have no effect.
	 *
	 * @param b  a boolean value -- true to make the menu visible,
	 *           false to hide it
	 * @beaninfo
	 *      description: The popup menu's visibility
	 *           expert: true
	 *           hidden: true
	 */
	
	public void setPopupMenuVisible( boolean b ) {
		if( ! isEnabled() ) {
			return;
		}
		if( DEBUG ) {
			System.out.println( "in AliceMenu.setPopupMenuVisible " + b );

			// Thread.dumpStack();
		}

		boolean isVisible = isPopupMenuVisible();

		if( b != isVisible ) {
			ensurePopupMenuCreated();

			// Set location of popupMenu (pulldown or pullright)
			//  Perhaps this should be dictated by L&F
			if( ( b == true ) && isShowing() ) {
				Point p = getPopupMenuOrigin();
				getPopupMenu().show( this, p.x, p.y );
			} else {
				getPopupMenu().setVisible( false );
			}
		}
	}
	/**
	 * Computes the origin for the <code>AliceMenu</code>'s popup menu.
	 *
	 * @return a <code>Point</code> in the coordinate space of the
	 *        menu which should be used as the origin
	 *         of the <code>AliceMenu</code>'s popup menu
	 */
	
	protected Point getPopupMenuOrigin() {
		int x = 0;
		int y = 0;
		JPopupMenu pm = getPopupMenu();

		// Figure out the sizes needed to caclulate the menu position
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		screenSize.height -= 28; //HACK to avoid standard Windows Task Bar -JFP
		Dimension s = getSize();
		Dimension pmSize = pm.getSize();

		// For the first time the menu is popped up,
		// the size has not yet been initiated
		if( pmSize.width == 0 ) {
			pmSize = pm.getPreferredSize();
		}

		Point position = getLocationOnScreen();
		Container parent = getParent();

		if( parent instanceof JPopupMenu ) {

			// We are a submenu (pull-right)
			if( this.getComponentOrientation().isLeftToRight() ) {

				// First determine x:
				if( ( position.x + s.width + pmSize.width ) < screenSize.width ) {
					x = s.width;  // Prefer placement to the right
				} else {
					x = 0 - pmSize.width;  // Otherwise place to the left
				}
			} else {

				// First determine x:
				if( position.x < pmSize.width ) {
					x = s.width;  // Prefer placement to the right
				} else {
					x = 0 - pmSize.width;  // Otherwise place to the left
				}
			}
			// Then the y:
			if( ( position.y + pmSize.height ) < screenSize.height ) {
				y = 0;  // Prefer dropping down
			} else {
				y = s.height - pmSize.height;  // Otherwise drop 'up'
			}
		} else {

			// We are a toplevel menu (pull-down)
			if( this.getComponentOrientation().isLeftToRight() ) {

				// First determine the x:
				if( ( position.x + pmSize.width ) < screenSize.width ) {
					x = 0;  // Prefer extending to right
				} else {
					x = s.width - pmSize.width;  // Otherwise extend to left
				}
			} else {

				// First determine the x:
				if( ( position.x + s.width ) < pmSize.width ) {
					x = 0;  // Prefer extending to right
				} else {
					x = s.width - pmSize.width;  // Otherwise extend to left
				}
			}
			// Then the y:
			if( ( position.y + s.height + pmSize.height ) < screenSize.height ) {
				y = s.height;  // Prefer dropping down
			} else {
				y = 0 - pmSize.height;  // Otherwise drop 'up'
			}
		}

		return new Point( x, y );
	}
	/**
	 * Returns the suggested delay, in milliseconds, before submenus
	 * are popped up or down.
	 * Each look and feel (L&F) may determine its own policy for
	 * observing the <code>delay</code> property.
	 * In most cases, the delay is not observed for top level menus
	 * or while dragging.  The default for <code>delay</code> is 0.
	 * This method is a property of the look and feel code and is used
	 * to manage the idiosyncracies of the various UI implementations.
	 *
	 *
	 * @return the <code>delay</code> property
	 */
	
	public int getDelay() {
		return delay;
	}
	/**
	 * Sets the suggested delay before the menu's <code>PopupMenu</code>
	 * is popped up or down.  Each look and feel (L&F) may determine
	 * it's own policy for observing the delay property.  In most cases,
	 * the delay is not observed for top level menus or while dragging.
	 * This method is a property of the look and feel code and is used
	 * to manage the idiosyncracies of the various UI implementations.
	 *
	 * @param       d the number of milliseconds to delay
	 * @exception   IllegalArgumentException if <code>d</code>
	 *                       is less than 0
	 * @beaninfo
	 *      description: The delay between menu selection and making the popup menu visible
	 *           expert: true
	 */
	
	public void setDelay( int d ) {
		if( d < 0 ) {
			throw new IllegalArgumentException( "Delay must be a positive integer" );
		}

		delay = d;
	}

	/**
	 * The window-closing listener for the popup.
	 *
	 * @see WinListener
	 */
	protected WinListener popupListener;

	private void ensurePopupMenuCreated() {
		if( popupMenu == null ) {
			final AliceMenu thisMenu = this;
			this.popupMenu = new AlicePopupMenu();
			popupMenu.setInvoker( this );
			popupListener = createWinListener( popupMenu );
			popupMenu.addPopupMenuListener( new PopupMenuListener() {
				public void popupMenuWillBecomeVisible( PopupMenuEvent e ) {}


				public void popupMenuWillBecomeInvisible( PopupMenuEvent e ) {}


				public void popupMenuCanceled( PopupMenuEvent e ) {
					fireMenuCanceled();
				}
			} );
		}
	}
	/**
	 * Sets the location of the popup component.
	 *
	 * @param x the x coordinate of the popup's new position
	 * @param y the y coordinate of the popup's new position
	 */
	
	public void setMenuLocation( int x, int y ) {
		if( popupMenu != null ) {
			popupMenu.setLocation( x, y );
		}
	}
	/**
	 * Appends a menu item to the end of this menu.
	 * Returns the menu item added.
	 *
	 * @param menuItem the <code>JMenuitem</code> to be added
	 * @return the <code>JMenuItem</code> added
	 */
	
	public JMenuItem add( JMenuItem menuItem ) {
		AccessibleContext ac = menuItem.getAccessibleContext();
		ac.setAccessibleParent( this );
		ensurePopupMenuCreated();

		return popupMenu.add( menuItem );
	}
	/**
	 * Appends a component to the end of this menu.
	 * Returns the component added.
	 *
	 * @param c the <code>Component</code> to add
	 * @return the <code>Component</code> added
	 */
	
	public Component add( Component c ) {
		if( c instanceof JComponent ) {
			AccessibleContext ac = ( (JComponent)c ).getAccessibleContext();

			if( ac != null ) {
				ac.setAccessibleParent( this );
			}
		}

		ensurePopupMenuCreated();
		popupMenu.add( c );

		return c;
	}
	/**
	 * Adds the specified component to this container at the given
	 * position. If <code>index</code> equals -1, the component will
	 * be appended to the end.
	 * @param     c   the <code>Component</code> to add
	 * @param     index    the position at which to insert the component
	 * @return    the <code>Component</code> added
	 * @see      #remove
	 * @see java.awt.Container#add(Component, int)
	 */
	
	public Component add( Component c, int index ) {
		if( c instanceof JComponent ) {
			AccessibleContext ac = ( (JComponent)c ).getAccessibleContext();

			if( ac != null ) {
				ac.setAccessibleParent( this );
			}
		}

		ensurePopupMenuCreated();
		popupMenu.add( c, index );

		return c;
	}
	/**
	 * Creates a new menu item with the specified text and appends
	 * it to the end of this menu.
	 *
	 * @param s the string for the menu item to be added
	 */
	
	public JMenuItem add( String s ) {
		return add( new JMenuItem( s ) );
	}
	/**
	 * Creates a new menu item attached to the specified
	 * <code>Action</code> object and appends it to the end of this menu.
	 * As of JDK 1.3, this is no longer the preferred method for adding
	 * <code>Actions</code> to
	 * a container. Instead it is recommended to configure a control with
	 * an action using <code>setAction</code>,
	 * and then add that control directly
	 * to the <code>Container</code>.
	 *
	 * @param a the <code>Action</code> for the menu item to be added
	 * @see Action
	 */
	
	public JMenuItem add( Action a ) {
		JMenuItem mi = createActionComponent( a );
		mi.setAction( a );
		add( mi );

		return mi;
	}
	/**
	 * Factory method which creates the <code>JMenuItem</code> for
	 * <code>Action</code>s added to the <code>AliceMenu</code>.
	 * As of JDK 1.3, this is no
	 * longer the preferred method. Instead it is recommended to configure
	 * a control with an action using <code>setAction</code>,
	 * and then adding that
	 * control directly to the <code>Container</code>.
	 *
	 * @param a the <code>Action</code> for the menu item to be added
	 * @return the new menu item
	 * @see Action
	 */
	
	protected JMenuItem createActionComponent( Action a ) {
		JMenuItem mi = new JMenuItem( (String)a.getValue( Action.NAME ), (Icon)a.getValue( Action.SMALL_ICON ) ) {
			
			protected PropertyChangeListener createActionPropertyChangeListener( Action a ) {
				PropertyChangeListener pcl = createActionChangeListener( this );

				if( pcl == null ) {
					pcl = super.createActionPropertyChangeListener( a );
				}

				return pcl;
			}
		};

		mi.setHorizontalTextPosition( SwingConstants.RIGHT );
		mi.setVerticalTextPosition( SwingConstants.CENTER );
		mi.setEnabled( a.isEnabled() );

		return mi;
	}
	/**
	 * Returns a properly configured <code>PropertyChangeListener</code>
	 * which updates the control as changes to the <code>Action</code> occur.
	 * As of JDK 1.3, this is no longer the preferred method for adding
	 * <code>Action</code>s to a <code>Container</code>.
	 * Instead it is recommended to configure a control with
	 * an action using <code>setAction</code>, and then add that
	 * control directly
	 * to the <code>Container</code>.
	 */
	
	protected PropertyChangeListener createActionChangeListener( JMenuItem b ) {
		return new ActionChangedListener( b );
	}

	private class ActionChangedListener implements PropertyChangeListener {
		JMenuItem menuItem;

		ActionChangedListener( JMenuItem mi ) {
			super();
			setTarget( mi );
		}

		public void propertyChange( PropertyChangeEvent e ) {
			String propertyName = e.getPropertyName();

			if( e.getPropertyName().equals( Action.NAME ) ) {
				String text = (String)e.getNewValue();
				menuItem.setText( text );
			} else if( propertyName.equals( "enabled" ) ) {
				Boolean enabledState = (Boolean)e.getNewValue();
				menuItem.setEnabled( enabledState.booleanValue() );
			} else if( e.getPropertyName().equals( Action.SMALL_ICON ) ) {
				Icon icon = (Icon)e.getNewValue();
				menuItem.setIcon( icon );
				menuItem.invalidate();
				menuItem.repaint();
			}
		}

		public void setTarget( JMenuItem b ) {
			this.menuItem = b;
		}
	}

	/**
	 * Appends a new separator to the end of the menu.
	 */
	
	public void addSeparator() {
		ensurePopupMenuCreated();
		popupMenu.addSeparator();
	}
	/**
	 * Inserts a new menu item with the specified text at a
	 * given position.
	 *
	 * @param s the text for the menu item to add
	 * @param pos an integer specifying the position at which to add the
	 *               new menu item
	 * @exception IllegalArgumentException when the value of
	 *            <code>pos</code> < 0
	 */
	
	public void insert( String s, int pos ) {
		if( pos < 0 ) {
			throw new IllegalArgumentException( "index less than zero." );
		}

		ensurePopupMenuCreated();
		popupMenu.insert( new JMenuItem( s ), pos );
	}
	/**
	 * Inserts the specified <code>JMenuitem</code> at a given position.
	 *
	 * @param mi the <code>JMenuitem</code> to add
	 * @param pos an integer specifying the position at which to add the
	 *               new <code>JMenuitem</code>
	 * @return the new menu item
	 * @exception IllegalArgumentException if the value of
	 *            <code>pos</code> < 0
	 */
	
	public JMenuItem insert( JMenuItem mi, int pos ) {
		if( pos < 0 ) {
			throw new IllegalArgumentException( "index less than zero." );
		}

		AccessibleContext ac = mi.getAccessibleContext();
		ac.setAccessibleParent( this );
		ensurePopupMenuCreated();
		popupMenu.insert( mi, pos );

		return mi;
	}
	/**
	 * Inserts a new menu item attached to the specified <code>Action</code>
	 * object at a given position.
	 *
	 * @param a the <code>Action</code> object for the menu item to add
	 * @param pos an integer specifying the position at which to add the
	 *               new menu item
	 * @exception IllegalArgumentException if the value of
	 *            <code>pos</code> < 0
	 */
	
	public JMenuItem insert( Action a, int pos ) {
		if( pos < 0 ) {
			throw new IllegalArgumentException( "index less than zero." );
		}

		ensurePopupMenuCreated();

		JMenuItem mi = new JMenuItem( (String)a.getValue( Action.NAME ), (Icon)a.getValue( Action.SMALL_ICON ) );
		mi.setHorizontalTextPosition( SwingConstants.RIGHT );
		mi.setVerticalTextPosition( SwingConstants.CENTER );
		mi.setEnabled( a.isEnabled() );
		mi.setAction( a );
		popupMenu.insert( mi, pos );

		return mi;
	}
	/**
	 * Inserts a separator at the specified position.
	 *
	 * @param       index an integer specifying the position at which to
	 *                    insert the menu separator
	 * @exception   IllegalArgumentException if the value of
	 *                       <code>index</code> < 0
	 */
	
	public void insertSeparator( int index ) {
		if( index < 0 ) {
			throw new IllegalArgumentException( "index less than zero." );
		}

		ensurePopupMenuCreated();
		popupMenu.insert( new JPopupMenu.Separator(), index );
	}
	/**
	 * Returns the <code>JMenuItem</code> at the specified position.
	 * If the component at <code>pos</code> is not a menu item,
	 * <code>null</code> is returned.
	 * This method is included for AWT compatibility.
	 *
	 * @param pos    an integer specifying the position
	 * @exception   IllegalArgumentException if the value of
	 *                       <code>pos</code> < 0
	 * @return  the menu item at the specified position; or <code>null</code>
	 *        if the item as the specified position is not a menu item
	 */
	
	public JMenuItem getItem( int pos ) {
		if( pos < 0 ) {
			throw new IllegalArgumentException( "index less than zero." );
		}

		Component c = getMenuComponent( pos );

		if( c instanceof JMenuItem ) {
			JMenuItem mi = (JMenuItem)c;

			return mi;
		}

		// 4173633
		return null;
	}
	/**
	 * Returns the number of items on the menu, including separators.
	 * This method is included for AWT compatibility.
	 *
	 * @return an integer equal to the number of items on the menu
	 * @see #getMenuComponentCount
	 */
	
	public int getItemCount() {
		return getMenuComponentCount();
	}
	/**
	 * Returns true if the menu can be torn off.  This method is not
	 * yet implemented.
	 *
	 * @return true if the menu can be torn off, else false
	 * @exception  Error  if invoked -- this method is not yet implemented
	 */
	
	public boolean isTearOff() {
		throw new Error( "boolean isTearOff() {} not yet implemented" );
	}
	/**
	 * Removes the specified menu item from this menu.  If there is no
	 * popup menu, this method will have no effect.
	 *
	 * @param    item the <code>JMenuItem</code> to be removed from the menu
	 */
	
	public void remove( JMenuItem item ) {
		if( popupMenu != null ) {
			popupMenu.remove( item );
		}
	}
	/**
	 * Removes the menu item at the specified index from this menu.
	 *
	 * @param       pos the position of the item to be removed
	 * @exception   IllegalArgumentException if the value of
	 *                       <code>pos</code> < 0, or if <code>pos</code>
	 *                 is greater than the number of menu items
	 */
	
	public void remove( int pos ) {
		if( pos < 0 ) {
			throw new IllegalArgumentException( "index less than zero." );
		}
		if( pos > getItemCount() ) {
			throw new IllegalArgumentException( "index greater than the number of items." );
		}
		if( popupMenu != null ) {
			popupMenu.remove( pos );
		}
	}
	/**
	 * Removes the component <code>c</code> from this menu.
	 *
	 * @param       c the component to be removed
	 */
	
	public void remove( Component c ) {
		if( popupMenu != null ) {
			popupMenu.remove( c );
		}
	}
	/**
	 * Removes all menu items from this menu.
	 */
	
	public void removeAll() {
		if( popupMenu != null ) {
			popupMenu.removeAll();
		}
	}
	/**
	 * Returns the number of components on the menu.
	 *
	 * @return an integer containing the number of components on the menu
	 */
	
	public int getMenuComponentCount() {
		int componentCount = 0;

		if( popupMenu != null ) {
			componentCount = popupMenu.getComponentCount();
		}

		return componentCount;
	}
	/**
	 * Returns the component at position <code>n</code>.
	 *
	 * @param n the position of the component to be returned
	 * @return the component requested, or <code>null</code>
	 *            if there is no popup menu
	 *
	 */
	
	public Component getMenuComponent( int n ) {
		if( popupMenu != null ) {
			return popupMenu.getComponent( n );
		}

		return null;
	}
	/**
	 * Returns an array of <code>Component</code>s of the menu's
	 * subcomponents.  Note that this returns all <code>Component</code>s
	 * in the popup menu, including separators.
	 *
	 * @return an array of <code>Component</code>s or an empty array
	 *        if there is no popup menu
	 */
	
	public Component[] getMenuComponents() {
		if( popupMenu != null ) {
			return popupMenu.getComponents();
		}

		return new Component[0];
	}
	/**
	 * Returns true if the menu is a 'top-level menu', that is, if it is
	 * the direct child of a menubar.
	 *
	 * @return true if the menu is activated from the menu bar;
	 *         false if the menu is activated from a menu item
	 *         on another menu
	 */
	
	public boolean isTopLevelMenu() {
		if( getParent() instanceof JMenuBar ) {
			return true;
		}

		return false;
	}
	/**
	 * Returns true if the specified component exists in the
	 * submenu hierarchy.
	 *
	 * @param c the <code>Component</code> to be tested
	 * @return true if the <code>Component</code> exists, false otherwise
	 */
	
	public boolean isMenuComponent( Component c ) {

		// Are we in the MenuItem part of the menu
		if( c == this ) {
			return true;
		}
		// Are we in the PopupMenu?
		if( c instanceof JPopupMenu ) {
			JPopupMenu comp = (JPopupMenu)c;

			if( comp == this.getPopupMenu() ) {
				return true;
			}
		}

		// Are we in a Component on the PopupMenu
		int ncomponents = this.getMenuComponentCount();
		Component[] component = this.getMenuComponents();

		for( int i = 0; i < ncomponents; i++ ) {
			Component comp = component[i];

			// Are we in the current component?
			if( comp == c ) {
				return true;
			}
			// Hmmm, what about Non-menu containers?
			// Recursive call for the Menu case
			if( comp instanceof AliceMenu ) {
				AliceMenu subMenu = (AliceMenu)comp;

				if( subMenu.isMenuComponent( c ) ) {
					return true;
				}
			}
		}

		return false;
	}
	/*
		 * Returns a point in the coordinate space of this menu's popupmenu
		 * which corresponds to the point <code>p</code> in the menu's
		 * coordinate space.
		 *
		 * @param p the point to be translated
		 * @return the point in the coordinate space of this menu's popupmenu
		 */
	private Point translateToPopupMenu( Point p ) {
		return translateToPopupMenu( p.x, p.y );
	}
	/*
		 * Returns a point in the coordinate space of this menu's popupmenu
		 * which corresponds to the point (x,y) in the menu's coordinate space.
		 *
		 * @param x the x coordinate of the point to be translated
		 * @param y the y coordinate of the point to be translated
		 * @return the point in the coordinate space of this menu's popupmenu
		 */
	private Point translateToPopupMenu( int x, int y ) {
		int newX;
		int newY;

		if( getParent() instanceof JPopupMenu ) {
			newX = x - getSize().width;
			newY = y;
		} else {
			newX = x;
			newY = y - getSize().height;
		}

		return new Point( newX, newY );
	}
	/**
	 * Returns the popupmenu associated with this menu.  If there is
	 * no popupmenu, it will create one.
	 */
	
	public JPopupMenu getPopupMenu() {
		ensurePopupMenuCreated();

		return popupMenu;
	}
	/**
	 * Adds a listener for menu events.
	 *
	 * @param l the listener to be added
	 */
	
	public void addMenuListener( MenuListener l ) {
		listenerList.add( MenuListener.class, l );
	}
	/**
	 * Removes a listener for menu events.
	 *
	 * @param l the listener to be removed
	 */
	
	public void removeMenuListener( MenuListener l ) {
		listenerList.remove( MenuListener.class, l );
	}
	/**
	 * Notifies all listeners that have registered interest for
	 * notification on this event type.  The event instance
	 * is lazily created using the parameters passed into
	 * the fire method.
	 *
	 * @exception Error  if there is a <code>null</code> listener
	 * @see EventListenerList
	 */
	
	protected void fireMenuSelected() {
		if( DEBUG ) {
			System.out.println( "In AliceMenu.fireMenuSelected" );
		}

		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();

		// Process the listeners last to first, notifying
		// those that are interested in this event
		for( int i = listeners.length - 2; i >= 0; i -= 2 ) {
			if( listeners[i] == MenuListener.class ) {
				if( listeners[i + 1] == null ) {
					throw new Error( getText() + " has a NULL Listener!! " + i );
				} else {

					// Lazily create the event:
					if( menuEvent == null ) {
						menuEvent = new MenuEvent( this );
					}

					( (MenuListener)listeners[i + 1] ).menuSelected( menuEvent );
				}
			}
		}
	}
	/**
	 * Notifies all listeners that have registered interest for
	 * notification on this event type.  The event instance
	 * is lazily created using the parameters passed into
	 * the fire method.
	 *
	 * @exception Error if there is a <code>null</code> listener
	 * @see EventListenerList
	 */
	
	protected void fireMenuDeselected() {
		if( DEBUG ) {
			System.out.println( "In AliceMenu.fireMenuDeselected" );
		}

		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();

		// Process the listeners last to first, notifying
		// those that are interested in this event
		for( int i = listeners.length - 2; i >= 0; i -= 2 ) {
			if( listeners[i] == MenuListener.class ) {
				if( listeners[i + 1] == null ) {
					throw new Error( getText() + " has a NULL Listener!! " + i );
				} else {

					// Lazily create the event:
					if( menuEvent == null ) {
						menuEvent = new MenuEvent( this );
					}

					( (MenuListener)listeners[i + 1] ).menuDeselected( menuEvent );
				}
			}
		}
	}
	/**
	 * Notifies all listeners that have registered interest for
	 * notification on this event type.  The event instance
	 * is lazily created using the parameters passed into
	 * the fire method.
	 *
	 * @exception Error if there is a <code>null</code> listener
	 * @see EventListenerList
	 */
	
	protected void fireMenuCanceled() {
		if( DEBUG ) {
			System.out.println( "In AliceMenu.fireMenuCanceled" );
		}

		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();

		// Process the listeners last to first, notifying
		// those that are interested in this event
		for( int i = listeners.length - 2; i >= 0; i -= 2 ) {
			if( listeners[i] == MenuListener.class ) {
				if( listeners[i + 1] == null ) {
					throw new Error( getText() + " has a NULL Listener!! " + i );
				} else {

					// Lazily create the event:
					if( menuEvent == null ) {
						menuEvent = new MenuEvent( this );
					}

					( (MenuListener)listeners[i + 1] ).menuCanceled( menuEvent );
				}
			}
		}
	}

	class MenuChangeListener implements ChangeListener, Serializable {
		boolean isSelected = false;

		public void stateChanged( ChangeEvent e ) {
			ButtonModel model = (ButtonModel)e.getSource();
			boolean modelSelected = model.isSelected();

			if( modelSelected != isSelected ) {
				if( modelSelected == true ) {
					fireMenuSelected();
				} else {
					fireMenuDeselected();
				}

				isSelected = modelSelected;
			}
		}
	}


	private ChangeListener createMenuChangeListener() {
		return new MenuChangeListener();
	}
	/**
	 * Creates a window-closing listener for the popup.
	 *
	 * @param p the <code>JPopupMenu</code>
	 * @return the new window-closing listener
	 *
	 * @see WinListener
	 */
	
	protected WinListener createWinListener( JPopupMenu p ) {
		return new WinListener( p );
	}
	/**
	 * A listener class that watches for a popup window closing.
	 * When the popup is closing, the listener deselects the menu.
	 * <p>
	 * <strong>Warning:</strong>
	 * Serialized objects of this class will not be compatible with
	 * future Swing releases.  The current serialization support is appropriate
	 * for short term storage or RMI between applications running the same
	 * version of Swing.  A future release of Swing will provide support for
	 * long term persistence.
	 */
//	protected class WinListener extends WindowAdapter implements Serializable {
//		JPopupMenu popupMenu;
//
//		/**
//		 *  Create the window listener for the specified popup.
//		 */
//		public WinListener( JPopupMenu p ) {
//			this.popupMenu = p;
//		}
//		/**
//		 * Deselect the menu when the popup is closed from outside.
//		 */
//		public void windowClosing( WindowEvent e ) {
//			setSelected( false );
//		}
//	}

	/**
	 * Messaged when the menubar selection changes to activate or
	 * deactivate this menu.
	 * Overrides <code>JMenuItem.menuSelectionChanged</code>.
	 *
	 * @param isIncluded  true if this menu is active, false if
	 *        it is not
	 */
	
	public void menuSelectionChanged( boolean isIncluded ) {
		if( DEBUG ) {
			System.out.println( "In AliceMenu.menuSelectionChanged to " + isIncluded );
		}

		setSelected( isIncluded );
	}
	/**
	 * Returns an array of <code>MenuElement</code>s containing the submenu
	 * for this menu component.  If popup menu is <code>null</code> returns
	 * an empty array.  This method is required to conform to the
	 * <code>MenuElement</code> interface.  Note that since
	 * <code>JSeparator</code>s do not conform to the <code>MenuElement</code>
	 * interface, this array will only contain <code>JMenuItem</code>s.
	 *
	 * @return an array of <code>MenuElement</code> objects
	 */
	
	public MenuElement[] getSubElements() {
		if( popupMenu == null ) {
			return new MenuElement[0];
		} else {
			MenuElement[] result = new MenuElement[1];
			result[0] = popupMenu;

			return result;
		}
	}
	// implements javax.swing.MenuElement
	/**
	 * Returns the <code>java.awt.Component</code> used to
	 * paint this <code>MenuElement</code>.
	 * The returned component is used to convert events and detect if
	 * an event is inside a menu component.
	 */
	
	public Component getComponent() {
		return this;
	}
	/**
	 * <code>setAccelerator</code> is not defined for <code>AliceMenu</code>.
	 * Use <code>setMnemonic</code> instead.
	 * @param keyStroke  the keystroke combination which will invoke
	 *            the <code>JMenuItem</code>'s actionlisteners
	 *            without navigating the menu hierarchy
	 * @exception Error  if invoked -- this method is not defined for AliceMenu.
	 *            Use <code>setMnemonic</code> instead
	 *
	 * @beaninfo
	 *     description: The keystroke combination which will invoke the JMenuItem's
	 *                  actionlisteners without navigating the menu hierarchy
	 *          hidden: true
	 */
	
	public void setAccelerator( KeyStroke keyStroke ) {
		throw new Error( "setAccelerator() is not defined for AliceMenu.  Use setMnemonic() instead." );
	}
	/**
	 * Processes any focus events, such as
	 * <code>FocusEvent.FOCUS_GAINED</code> or
	 * <code>FocusEvent.FOCUS_LOST</code>.
	 *
	 * @param e the <code>FocusEvent</code>
	 * @see FocusEvent
	 */
	
	protected void processFocusEvent( FocusEvent e ) {
		switch( e.getID() ) {
			case FocusEvent.FOCUS_LOST:
				receivedKeyPressed = false;
				break;

			default:
				break;
		}

		super.processFocusEvent( e );
	}
	/**
	 * Processes key stroke events for this menu, such as mnemonics and
	 * accelerators.
	 * @param e  the key event to be processed
	 */
	
	protected void processKeyEvent( KeyEvent e ) {
		if( DEBUG ) {
			System.out.println( "in AliceMenu.processKeyEvent for " + getText() + "  " + KeyStroke.getKeyStrokeForEvent( e ) );
			System.out.println( "Event consumption = " + e.isConsumed() );
			Thread.dumpStack();
		}

		boolean createMenuEvent = false;

		switch( e.getID() ) {
			case KeyEvent.KEY_PRESSED:
				if( isSelected() ) {
					createMenuEvent = receivedKeyPressed = true;
				} else {
					receivedKeyPressed = false;
				}

				break;

			case KeyEvent.KEY_RELEASED:
				if( receivedKeyPressed ) {
					receivedKeyPressed = false;
					createMenuEvent = true;
				}

				break;

			default:
				createMenuEvent = receivedKeyPressed;
				break;
		}
		if( createMenuEvent ) {
			MenuSelectionManager.defaultManager().processKeyEvent( e );
		}
		if( e.isConsumed() ) {
			return;
		}
		/* The "if" block below  fixes bug #4108907.
			   Without this code, opened menus that
			   weren't interested in TAB key events (most menus are not) would
			   allow such events to propagate up until a component was found
			   that was interested in the event. This would often result in
			   the focus being moved to another component as a result of the
			   TAB, while the menu stayed open. The behavior that is most
			   probably desired is that menus are modal, and thus consume
			   all keyboard events while they are open. This is implemented
			   by the inner "if" clause. But if the desired behavior on TABs
			   is that the menu should close and allow the focus to move,
			   the "else" clause takes care of that. Note that this is probably
			   not the right way to implement that behavior; instead, the menu
			   should unpost whenever it looses focus, which would also fix
			   another bug: 4156858.
			   The fact that one has to special-case TABS here in AliceMenu code
			   also offends me...
			   hania 23 July 1998 */
		if( isSelected() && ( e.getKeyCode() == KeyEvent.VK_TAB || e.getKeyChar() == '\t' ) ) {
			if( (Boolean)UIManager.get( "Menu.consumesTabs" ) == Boolean.TRUE ) {
				e.consume();

				return;
			} else {
				MenuSelectionManager.defaultManager().clearSelectedPath();
			}
		}

		super.processKeyEvent( e );
	}
	/**
	 * Programatically performs a "click".  This overrides the method
	 * <code>AbstractButton.doClick</code> in order to make the menu pop up.
	 * @param pressTime  indicates the number of milliseconds the
	 *        button was pressed for
	 */
	
	public void doClick( int pressTime ) {
		MenuElement[] me = buildMenuElementArray( this );
		MenuSelectionManager.defaultManager().setSelectedPath( me );
	}
	/*
		 * Build an array of menu elements - from <code>PopupMenu</code> to
		 * the root <code>JMenuBar</code>.
		 * @param  leaf  the leaf node from which to start building up the array
		 * @return the array of menu items
		 */
	private MenuElement[] buildMenuElementArray( AliceMenu leaf ) {
		Vector elements = new Vector();
		Component current = leaf.getPopupMenu();
		JPopupMenu pop;
		AliceMenu menu;
		JMenuBar bar;

		while( true ) {
			if( current instanceof JPopupMenu ) {
				pop = (JPopupMenu)current;
				elements.insertElementAt( pop, 0 );
				current = pop.getInvoker();
			} else if( current instanceof AliceMenu ) {
				menu = (AliceMenu)current;
				elements.insertElementAt( menu, 0 );
				current = menu.getParent();
			} else if( current instanceof JMenuBar ) {
				bar = (JMenuBar)current;
				elements.insertElementAt( bar, 0 );

				MenuElement[] me = new MenuElement[elements.size()];
				elements.copyInto( me );

				return me;
			}
		}
	}
	/**
	 * See <code>readObject</code> and <code>writeObject</code> in
	 * <code>JComponent</code> for more
	 * information about serialization in Swing.
	 */
	private void writeObject( ObjectOutputStream s )
					  throws IOException {
		s.defaultWriteObject();

		if( ( ui != null ) && ( getUIClassID().equals( uiClassID ) ) ) {
			ui.installUI( this );
		}
	}
	/**
	 * Returns a string representation of this <code>AliceMenu</code>. This
	 * method is intended to be used only for debugging purposes, and the
	 * content and format of the returned string may vary between
	 * implementations. The returned string may be empty but may not
	 * be <code>null</code>.
	 *
	 * @return  a string representation of this AliceMenu.
	 */
	
	protected String paramString() {
		return super.paramString();
	}
/////////////////
// Accessibility support
////////////////
	/**
	 * Gets the AccessibleContext associated with this AliceMenu.
	 * For JMenus, the AccessibleContext takes the form of an
	 * AccessibleJMenu.
	 * A new AccessibleJMenu instance is created if necessary.
	 *
	 * @return an AccessibleJMenu that serves as the
	 *         AccessibleContext of this AliceMenu
	 */
	
	public AccessibleContext getAccessibleContext() {
		if( accessibleContext == null ) {
			accessibleContext = new AccessibleJMenu();
		}

		return accessibleContext;
	}
	/**
	 * This class implements accessibility support for the
	 * <code>AliceMenu</code> class.  It provides an implementation of the
	 * Java Accessibility API appropriate to menu user-interface elements.
	 * <p>
	 * <strong>Warning:</strong>
	 * Serialized objects of this class will not be compatible with
	 * future Swing releases.  The current serialization support is appropriate
	 * for short term storage or RMI between applications running the same
	 * version of Swing.  A future release of Swing will provide support for
	 * long term persistence.
	 */
	protected class AccessibleJMenu extends AccessibleAbstractButton implements ChangeListener, AccessibleSelection {
		public AccessibleJMenu() {
			super();
			AliceMenu.this.addChangeListener( this );
		}

		public void stateChanged( ChangeEvent e ) {
			firePropertyChange( AccessibleContext.ACCESSIBLE_VISIBLE_DATA_PROPERTY, new Boolean( false ), new Boolean( true ) );
		}

		/**
		 * Returns the number of accessible children in the object.  If all
		 * of the children of this object implement Accessible, than this
		 * method should return the number of children of this object.
		 *
		 * @return the number of accessible children in the object.
		 */
		
		public int getAccessibleChildrenCount() {
			Component[] children = getMenuComponents();
			int count = 0;

			for( int j = 0; j < children.length; j++ ) {
				if( children[j] instanceof Accessible ) {
					count++;
				}
			}

			return count;
		}
		/**
		 * Returns the nth Accessible child of the object.
		 *
		 * @param i zero-based index of child
		 * @return the nth Accessible child of the object
		 */
		
		public Accessible getAccessibleChild( int i ) {
			Component[] children = getMenuComponents();
			int count = 0;

			for( int j = 0; j < children.length; j++ ) {
				if( children[j] instanceof Accessible ) {
					if( count == i ) {
						if( children[j] instanceof JComponent ) {

							// FIXME:  [[[WDW - probably should set this when
							// the component is added to the menu.  I tried
							// to do this in most cases, but the separators
							// added by addSeparator are hard to get to.]]]
							AccessibleContext ac = ( (Accessible)children[j] ).getAccessibleContext();
							ac.setAccessibleParent( AliceMenu.this );
						}

						return (Accessible)children[j];
					} else {
						count++;
					}
				}
			}

			return null;
		}
		/**
		 * Get the role of this object.
		 *
		 * @return an instance of AccessibleRole describing the role of the
		 * object
		 * @see AccessibleRole
		 */
		
		public AccessibleRole getAccessibleRole() {
			return AccessibleRole.MENU;
		}
		/**
		 * Get the AccessibleSelection associated with this object.  In the
		 * implementation of the Java Accessibility API for this class,
	 * return this object, which is responsible for implementing the
		 * AccessibleSelection interface on behalf of itself.
	 *
	 * @return this object
		 */
		
		public AccessibleSelection getAccessibleSelection() {
			return this;
		}
		/**
		 * Returns 1 if a sub-menu is currently selected in this menu.
		 *
		 * @return 1 if a menu is currently selected, else 0
		 */
		public int getAccessibleSelectionCount() {
			MenuElement[] me = MenuSelectionManager.defaultManager().getSelectedPath();

			if( me != null ) {
				for( int i = 0; i < me.length; i++ ) {
					if( me[i] == AliceMenu.this ) {  // this menu is selected
						if( ( i + 1 ) < me.length ) {
							return 1;
						}
					}
				}
			}

			return 0;
		}
		/**
		 * Returns the currently selected sub-menu if one is selected,
		 * otherwise null (there can only be one selection, and it can
	 * only be a sub-menu, as otherwise menu items don't remain
	 * selected).
		 */
		public Accessible getAccessibleSelection( int i ) {

			// if i is a sub-menu & popped, return it
			if( ( i < 0 ) || ( i >= getItemCount() ) ) {
				return null;
			}

			MenuElement[] me = MenuSelectionManager.defaultManager().getSelectedPath();

			if( me != null ) {
				for( int j = 0; j < me.length; j++ ) {
					if( me[j] == AliceMenu.this ) {  // this menu is selected

						// so find the next JMenuItem in the MenuElement
						// array, and return it!
						while( ( ++j ) < me.length ) {
							if( me[j] instanceof JMenuItem ) {
								return (Accessible)me[j];
							}
						}
					}
				}
			}

			return null;
		}
		/**
		 * Returns true if the current child of this object is selected
	 * (that is, if this child is a popped-up submenu).
		 *
		 * @param i the zero-based index of the child in this Accessible
		 * object.
		 * @see AccessibleContext#getAccessibleChild
		 */
		public boolean isAccessibleChildSelected( int i ) {

			// if i is a sub-menu and is pop-ed up, return true, else false
			MenuElement[] me = MenuSelectionManager.defaultManager().getSelectedPath();

			if( me != null ) {
				JMenuItem mi = AliceMenu.this.getItem( i );

				for( int j = 0; j < me.length; j++ ) {
					if( me[j] == mi ) {
						return true;
					}
				}
			}

			return false;
		}
		/**
		 * Selects the <code>i</code>th menu in the menu.
		 * If that item is a submenu,
		 * it will pop up in response.  If a different item is already
	 * popped up, this will force it to close.  If this is a sub-menu
	 * that is already popped up (selected), this method has no
	 * effect.
		 *
		 * @param i the index of the item to be selected
		 * @see #getAccessibleStateSet
		 */
		public void addAccessibleSelection( int i ) {
			if( ( i < 0 ) || ( i >= getItemCount() ) ) {
				return;
			}

			JMenuItem mi = getItem( i );

			if( mi != null ) {
				if( mi instanceof AliceMenu ) {
					MenuElement[] me = buildMenuElementArray( (AliceMenu)mi );
					MenuSelectionManager.defaultManager().setSelectedPath( me );
				} else {
					mi.doClick();
					MenuSelectionManager.defaultManager().setSelectedPath( null );
				}
			}
		}
		/**
		 * Removes the nth item from the selection.  In general, menus
	 * can only have one item within them selected at a time
	 * (e.g. one sub-menu popped open).
		 *
		 * @param i the zero-based index of the selected item
		 */
		public void removeAccessibleSelection( int i ) {
			if( ( i < 0 ) || ( i >= getItemCount() ) ) {
				return;
			}

			JMenuItem mi = getItem( i );

			if( ( mi != null ) && mi instanceof AliceMenu ) {
				if( ( (AliceMenu)mi ).isSelected() ) {
					MenuElement[] old = MenuSelectionManager.defaultManager().getSelectedPath();
					MenuElement[] me = new MenuElement[old.length - 2];

					for( int j = 0; j < ( old.length - 2 ); j++ ) {
						me[j] = old[j];
					}

					MenuSelectionManager.defaultManager().setSelectedPath( me );
				}
			}
		}
		/**
		 * Clears the selection in the object, so that nothing in the
		 * object is selected.  This will close any open sub-menu.
		 */
		public void clearAccessibleSelection() {

			// if this menu is selected, reset selection to only go
			// to this menu; else do nothing
			MenuElement[] old = MenuSelectionManager.defaultManager().getSelectedPath();

			if( old != null ) {
				for( int j = 0; j < old.length; j++ ) {
					if( old[j] == AliceMenu.this ) {  // menu is in the selection!
						MenuElement[] me = new MenuElement[j + 1];
						System.arraycopy( old, 0, me, 0, j );
						me[j] = AliceMenu.this.getPopupMenu();
						MenuSelectionManager.defaultManager().setSelectedPath( me );
					}
				}
			}
		}
		/**
		 * Normally causes every selected item in the object to be selected
		 * if the object supports multiple selections.  This method
		 * makes no sense in a menu bar, and so does nothing.
		 */
		public void selectAllAccessibleSelection() {}

	}  // inner class AccessibleJMenu
}