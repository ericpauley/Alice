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
public class WatcherPanel extends javax.swing.JPanel /*implements edu.cmu.cs.stage3.alice.core.event.PropertyListener, edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener, edu.cmu.cs.stage3.alice.core.event.ChildrenListener*/ {
	protected java.util.List variablesToWatch = new java.util.ArrayList();
	protected java.util.List propertiesToWatch = new java.util.ArrayList();
	protected edu.cmu.cs.stage3.alice.core.Element root;

	public WatcherPanel() {
		setBackground( java.awt.Color.white );
		setLayout( new java.awt.GridBagLayout() );
		addContainerListener( GUIElementContainerListener.getStaticListener() );
	}

	public boolean isThereSomethingToWatch() {
		return (variablesToWatch.size() + propertiesToWatch.size()) > 0;
	}

	public void clear() {
		removeAllVariablesBeingWatched();
		removeAllPropertiesBeingWatched();
	}

	public void addVariableToWatch( edu.cmu.cs.stage3.alice.core.Variable variable ) {
		variablesToWatch.add( variable );
		refreshGUI();
	}

	public void removeVariableBeingWatched( edu.cmu.cs.stage3.alice.core.Variable variable ) {
		variablesToWatch.remove( variable );
		refreshGUI();
	}

	public void removeAllVariablesBeingWatched() {
		variablesToWatch.clear();
		refreshGUI();
	}

	public boolean isVariableBeingWatched( edu.cmu.cs.stage3.alice.core.Variable variable ) {
		return variablesToWatch.contains( variable );
	}

	public void addPropertyToWatch( edu.cmu.cs.stage3.alice.core.Property property ) {
		propertiesToWatch.add( property );
		refreshGUI();
	}

	public void removePropertyBeingWatched( edu.cmu.cs.stage3.alice.core.Property property ) {
		propertiesToWatch.remove( property );
		refreshGUI();
	}

	public void removeAllPropertiesBeingWatched() {
		propertiesToWatch.clear();
		refreshGUI();
	}

	public boolean isPropertyBeingWatched( edu.cmu.cs.stage3.alice.core.Property property ) {
		return propertiesToWatch.contains( property );
	}

	protected void refreshGUI() {
		this.removeAll();
		int count = 0;
		for( java.util.Iterator iter = variablesToWatch.iterator(); iter.hasNext(); ) {
			edu.cmu.cs.stage3.alice.core.Variable variable = (edu.cmu.cs.stage3.alice.core.Variable)iter.next();
			edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory factory = new edu.cmu.cs.stage3.alice.authoringtool.util.SetPropertyImmediatelyFactory( variable.value );
			javax.swing.JComponent gui = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getVariableGUI( variable, true, factory );
			if( gui != null ) {
				this.add( gui, new java.awt.GridBagConstraints( 0, count++, 1, 1, 1.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets( 2, 2, 2, 2 ), 0, 0 ) );
			} else {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Unable to create gui for variable: " + variable, null );
			}
		}

		this.add( javax.swing.Box.createVerticalStrut( 8 ), new java.awt.GridBagConstraints( 0, count++, 1, 1, 1.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets( 2, 2, 2, 2 ), 0, 0 ) );

		for( java.util.Iterator iter = propertiesToWatch.iterator(); iter.hasNext(); ) {
			edu.cmu.cs.stage3.alice.core.Property property = (edu.cmu.cs.stage3.alice.core.Property)iter.next();
			edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory factory = new edu.cmu.cs.stage3.alice.authoringtool.util.SetPropertyImmediatelyFactory( property );
			javax.swing.JComponent gui = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getPropertyGUI( property, true, false, factory );
			if( gui != null ) {
				this.add( gui, new java.awt.GridBagConstraints( 0, count++, 1, 1, 1.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets( 2, 2, 2, 2 ), 0, 0 ) );
			} else {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Unable to create gui for property: " + property, null );
			}
		}

		java.awt.Component glue = javax.swing.Box.createGlue();
		this.add( glue, new java.awt.GridBagConstraints( 0, count++, 1, 1, 1.0, 1.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.BOTH, new java.awt.Insets( 2, 2, 2, 2 ), 0, 0 ) );

		revalidate();
		repaint();
	}

	//////////////
	// Listening
	//////////////

//	public void startListening( edu.cmu.cs.stage3.alice.core.Element root ) {
//		stopListening();
//		this.root = root;
//		listenTo( root );
//	}
//
//	public void stopListening() {
//		if( root != null ) {
//			stopListeningTo( root );
//			root = null;
//		}
//	}
//
//	protected void addPropertyIfAppropriate( edu.cmu.cs.stage3.alice.core.Property property ) {
//		if( (property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.Variable) && property.getName().equals( "value" ) ) {
//			if( ! (property.getOwner().getParent() instanceof edu.cmu.cs.stage3.alice.core.Behavior) ) { // special case avoidance of behavior variables
//				if( ! isVariableBeingWatched( (edu.cmu.cs.stage3.alice.core.Variable)property.getOwner() ) ) {
//					addVariableToWatch( (edu.cmu.cs.stage3.alice.core.Variable)property.getOwner() );
//				}
//			}
//		}
//	}
//
//	synchronized public void propertyChanging( edu.cmu.cs.stage3.alice.core.event.PropertyEvent ev ) {	}
//	synchronized public void propertyChanged( edu.cmu.cs.stage3.alice.core.event.PropertyEvent ev ) {
//		addPropertyIfAppropriate( ev.getProperty() );
//	}
//
//	synchronized public void objectArrayPropertyChanging( edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent ev ) {}
//	synchronized public void objectArrayPropertyChanged( edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent ev ) {
//		addPropertyIfAppropriate( ev.getProperty() );
//	}
//
//	synchronized public void childrenChanging( edu.cmu.cs.stage3.alice.core.event.ChildrenEvent childrenEvent ) {}
//	synchronized public void childrenChanged( edu.cmu.cs.stage3.alice.core.event.ChildrenEvent childrenEvent ) {
//		int changeType = childrenEvent.getChangeType();
//		if( changeType == edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_INSERTED ) {
//			listenTo( childrenEvent.getChild() );
//		} else if( changeType == edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_REMOVED ) {
//			stopListeningTo( childrenEvent.getChild() );
//		}
//	}
//
//	synchronized protected void listenTo( edu.cmu.cs.stage3.alice.core.Element element ) {
//		if( element != null ) {
//			edu.cmu.cs.stage3.alice.core.Element[] elements = element.search( new edu.cmu.cs.stage3.util.InstanceOfCriterion( edu.cmu.cs.stage3.alice.core.Element.class ) );
//			for( int i = 0; i < elements.length; i++ ) {
//				edu.cmu.cs.stage3.alice.core.Property[] properties = elements[i].getProperties();
//				for( int j = 0; j < properties.length; j++ ) {
//					if( properties[j] instanceof edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty ) {
//						((edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty)properties[j]).addObjectArrayPropertyListener( this );
//					} else {
//						properties[j].addPropertyListener( this );
//					}
//				}
//				boolean alreadyChildrenListening = false;
//				edu.cmu.cs.stage3.alice.core.event.ChildrenListener[] childrenListeners = elements[i].getChildrenListeners();
//				for( int j = 0; j < childrenListeners.length; j++ ) {
//					if( childrenListeners[j] == this ) {
//						alreadyChildrenListening = true;
//					}
//				}
//				if( ! alreadyChildrenListening ) {
//					elements[i].addChildrenListener( this );
//				}
//			}
//		}
//	}
//
//	synchronized protected void stopListeningTo( edu.cmu.cs.stage3.alice.core.Element element ) {
//		if( element != null ) {
//			edu.cmu.cs.stage3.alice.core.Element[] elements = element.search( new edu.cmu.cs.stage3.util.InstanceOfCriterion( edu.cmu.cs.stage3.alice.core.Element.class ) );
//			for( int i = 0; i < elements.length; i++ ) {
//				edu.cmu.cs.stage3.alice.core.Property[] properties = elements[i].getProperties();
//				for( int j = 0; j < properties.length; j++ ) {
//					if( properties[j] instanceof edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty ) {
//						((edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty)properties[j]).removeObjectArrayPropertyListener( this );
//					} else {
//						properties[j].removePropertyListener( this );
//					}
//				}
//				elements[i].removeChildrenListener( this );
//			}
//		}
//	}
}