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
public class FilteringElementTreeModel extends TreeModelSupport implements edu.cmu.cs.stage3.alice.core.event.PropertyListener, edu.cmu.cs.stage3.alice.core.event.ChildrenListener, edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateListener, javax.swing.tree.TreeModel {
	protected edu.cmu.cs.stage3.alice.core.Element root;
	protected Object[] emptyPath = { new edu.cmu.cs.stage3.alice.core.World() };
	protected java.util.LinkedList inclusionList;
	protected java.util.LinkedList exclusionList;

	public FilteringElementTreeModel() {}

	public java.util.LinkedList getInclusionList() {
		return inclusionList;
	}

	public void setInclusionList( java.util.LinkedList list ) {
		inclusionList = list;
		update();
	}

	public java.util.LinkedList getExclusionList() {
		return exclusionList;
	}

	public void setExclusionList( java.util.LinkedList list ) {
		exclusionList = list;
		update();
	}

	public boolean isAcceptedByFilter( edu.cmu.cs.stage3.alice.core.Element element ) {
		if( inclusionList == null ) {
			return false;
		}

		// anyone meeting an exclusion Criterion get booted
		if( exclusionList != null ) {
			for( java.util.Iterator iter = exclusionList.iterator(); iter.hasNext(); ) {
				Object item = iter.next();
				if( item instanceof edu.cmu.cs.stage3.util.Criterion ) {
					if( ((edu.cmu.cs.stage3.util.Criterion)item).accept( element ) ) {
						return false;
					}
				}
			}
		}

		// anyone left who meets an inclusion Criterion is accepted
		if( inclusionList != null ) {
			for( java.util.Iterator iter = inclusionList.iterator(); iter.hasNext(); ) {
				Object item = iter.next();
				if( item instanceof edu.cmu.cs.stage3.util.Criterion ) {
					if( ((edu.cmu.cs.stage3.util.Criterion)item).accept( element ) ) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public boolean isElementInTree( edu.cmu.cs.stage3.alice.core.Element element ) {
		if( element == root ) {
			return true;
		}

		if( element == null ) {
			return false;
		}

		if( isElementInTree( element.getParent() ) && isAcceptedByFilter( element ) ) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * if someone alters the inclusion or exclusion lists on their own, they must call this method to update the tree
	 */
	public void update() {
		clearAllListening( root );
		startListeningToTree( root );

		if( root == null ) {
			javax.swing.event.TreeModelEvent ev = new javax.swing.event.TreeModelEvent( this, emptyPath );
			fireTreeStructureChanged( ev );
		} else {
			javax.swing.event.TreeModelEvent ev = new javax.swing.event.TreeModelEvent( this, getPath( root ) );
			fireTreeStructureChanged( ev );
		}
	}

	public void setRoot( edu.cmu.cs.stage3.alice.core.Element root ) {
		clearAllListening( this.root );
		this.root = root;
		startListeningToTree( root );

		if( root == null ) {
			javax.swing.event.TreeModelEvent ev = new javax.swing.event.TreeModelEvent( this, emptyPath );
			fireTreeStructureChanged( ev );
		} else {
			javax.swing.event.TreeModelEvent ev = new javax.swing.event.TreeModelEvent( this, getPath( root ) );
			fireTreeStructureChanged( ev );
		}
	}

	public Object[] getPath( edu.cmu.cs.stage3.alice.core.Element element ) {
		java.util.LinkedList list = new java.util.LinkedList();

		edu.cmu.cs.stage3.alice.core.Element e = element;
		while( true ) {
			if( e == null ) {
				return new Object[] {};  // element's not in the tree
			}
			list.addFirst( e );
			if( e == root ) {
				break;
			}
			e = e.getParent();
		}

		return list.toArray();
	}

	public void setListeningEnabled( boolean enabled ) {
		if( enabled ) {
			startListeningToTree( root );
		} else {
			stopListeningToTree( root );
		}
	}

	////////////////////////////////
	// TreeModel Interface
	////////////////////////////////

	public Object getRoot() {
		return root;
	}

	public boolean isLeaf( Object node ) {
		if( node == null ) {
			return true;
		}
		/* might want this
		if( node instanceof edu.cmu.cs.stage3.alice.core.Group ) {
			return false;
		}
		*/
		if( node == root ) {
			return false;
		}
		if( ! (node instanceof edu.cmu.cs.stage3.alice.core.Element) ) {
			throw new IllegalArgumentException( "nodes must be edu.cmu.cs.stage3.alice.core.Elements" );
		}

		edu.cmu.cs.stage3.alice.core.Element element = (edu.cmu.cs.stage3.alice.core.Element)node;
		edu.cmu.cs.stage3.alice.core.Element[] children = element.getChildren();
		for( int i = 0; i < children.length; i++ ) {
			if( isAcceptedByFilter( children[i] ) ) {
				return false;
			}
		}

		return true;
	}

	public int getChildCount( Object parent ) {
		if( ! (parent instanceof edu.cmu.cs.stage3.alice.core.Element) ) {
			throw new IllegalArgumentException( "nodes must be edu.cmu.cs.stage3.alice.core.Elements" );
		}

		int childCount = 0;
		edu.cmu.cs.stage3.alice.core.Element element = (edu.cmu.cs.stage3.alice.core.Element)parent;
		edu.cmu.cs.stage3.alice.core.Element[] children = element.getChildren();
		for( int i = 0; i < children.length; i++ ) {
			if( isAcceptedByFilter( children[i] ) ) {
				childCount++;
			}
		}

		return childCount;
	}

	public Object getChild( Object parent, int index ) {
		if( ! (parent instanceof edu.cmu.cs.stage3.alice.core.Element) ) {
			throw new IllegalArgumentException( "nodes must be edu.cmu.cs.stage3.alice.core.Elements" );
		}

		int childCount = 0;
		edu.cmu.cs.stage3.alice.core.Element element = (edu.cmu.cs.stage3.alice.core.Element)parent;
		edu.cmu.cs.stage3.alice.core.Element[] children = element.getChildren();

		for( int i = 0; i < children.length; i++ ) {
			if( isAcceptedByFilter( children[i] ) ) {
				if( childCount == index ) {
					return children[i];
				}
				childCount++;
			}
		}

		return null;
	}

	public int getIndexOfChild( Object parent, Object child ) {
		if( ! (parent instanceof edu.cmu.cs.stage3.alice.core.Element) ) {
			throw new IllegalArgumentException( "nodes must be edu.cmu.cs.stage3.alice.core.Elements" );
		}
		if( ! (child instanceof edu.cmu.cs.stage3.alice.core.Element) ) {
			throw new IllegalArgumentException( "nodes must be edu.cmu.cs.stage3.alice.core.Elements" );
		}

		int childCount = 0;
		edu.cmu.cs.stage3.alice.core.Element element = (edu.cmu.cs.stage3.alice.core.Element)parent;
		edu.cmu.cs.stage3.alice.core.Element[] children = element.getChildren();
		for( int i = 0; i < children.length; i++ ) {
			if( isAcceptedByFilter( children[i] ) ) {
				if( children[i] == child ) { // should this use .equals?  I don't think so...
					return childCount;
				}
				childCount++;
			}
		}

		return -1;
	}

	public void valueForPathChanged( javax.swing.tree.TreePath path, Object newValue ) {
		if( (path.getLastPathComponent() instanceof edu.cmu.cs.stage3.alice.core.Element) && (newValue instanceof String) ) {
			edu.cmu.cs.stage3.alice.core.Element element = (edu.cmu.cs.stage3.alice.core.Element)path.getLastPathComponent();
			Object previousName = element.name.get();
			try {
				element.name.set( newValue );
			} catch( IllegalArgumentException e ) {
				//TODO produce some kind of event so that the interface can take appropriate action.
				element.name.set( previousName );
			}
		} else {
			throw new RuntimeException( "FilteringElementTreeModel only allows name changes through the model" );
		}
	}

	///////////////////////////////////////////////
	// AuthoringToolStateListener interface
	///////////////////////////////////////////////

	public void stateChanging( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {
		if( ev.getCurrentState() == edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent.RUNTIME_STATE ) {
			FilteringElementTreeModel.this.setListeningEnabled( false );
		} else {
			FilteringElementTreeModel.this.setListeningEnabled( true );
		}
	}
	public void worldLoading( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldUnLoading( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldStarting( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldStopping( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldPausing( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldSaving( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}

	public void stateChanged( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldLoaded( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldUnLoaded( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldStarted( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldStopped( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldPaused( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldSaved( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}

	////////////////////////////////////////////////////
	// ChildrenListener and PropertyListener Interface
	////////////////////////////////////////////////////

	public void childrenChanging( edu.cmu.cs.stage3.alice.core.event.ChildrenEvent childrenEvent ) {}

	public void childrenChanged( edu.cmu.cs.stage3.alice.core.event.ChildrenEvent childrenEvent ) {
		edu.cmu.cs.stage3.alice.core.Element element = childrenEvent.getChild();
		edu.cmu.cs.stage3.alice.core.Element parent = (edu.cmu.cs.stage3.alice.core.Element)childrenEvent.getSource();
		Object[] path = getPath( parent );

		if( childrenEvent.getChangeType() == edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_INSERTED ) {
			if( isAcceptedByFilter( element ) ) {
				startListeningToTree( element );

				int[] childIndices = { getIndexOfChild( parent, element ) };
				Object[] children = { element };
				javax.swing.event.TreeModelEvent ev = new javax.swing.event.TreeModelEvent( this, path, childIndices, children );
				fireTreeNodesInserted( ev );
			}
		} else if( childrenEvent.getChangeType() == edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_REMOVED ) {
			stopListeningToTree( element );

			// this is a cop-out; it would be rather difficult to calculate the previous position in the filtered tree
			// of the already deleted element
			javax.swing.event.TreeModelEvent ev = new javax.swing.event.TreeModelEvent( this, path );
			fireTreeStructureChanged( ev );
		} else if( childrenEvent.getChangeType() == edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_SHIFTED ) {
			if( isElementInTree( element ) ) {
				// this one isn't a cop-out
				javax.swing.event.TreeModelEvent ev = new javax.swing.event.TreeModelEvent( this, path );
				fireTreeStructureChanged( ev );
			}
		}
	}

	public void propertyChanging( edu.cmu.cs.stage3.alice.core.event.PropertyEvent propertyEvent ) {}

	public void propertyChanged( edu.cmu.cs.stage3.alice.core.event.PropertyEvent propertyEvent ) {
		// only listen to name changes for now.
		// TODO: this probably wants a more sophisticated mechanism.
		if( propertyEvent.getProperty() == propertyEvent.getProperty().getOwner().name ) {
			edu.cmu.cs.stage3.alice.core.Element element = propertyEvent.getProperty().getOwner();
			edu.cmu.cs.stage3.alice.core.Element parent = element.getParent();
			Object[] path = getPath( parent );
			int[] childIndices = { getIndexOfChild( parent, element ) };
			Object[] children = { element };
			if( (path == null) || (path.length == 0) ) {
				path = getPath( element );
				childIndices = null;
				children = null;
			}
			javax.swing.event.TreeModelEvent ev = new javax.swing.event.TreeModelEvent( this, path, childIndices, children );
			fireTreeNodesChanged( ev );
		}
	}

	////////////////////////////////
	// protected methods
	////////////////////////////////

	protected void startListeningToTree( edu.cmu.cs.stage3.alice.core.Element element ) {
		if( element != null ) {
			element.addChildrenListener( this );
			element.name.addPropertyListener( this );

			edu.cmu.cs.stage3.alice.core.Element[] children = element.getChildren();
			for( int i = 0; i < children.length; i++ ) {
				if( isAcceptedByFilter( children[i] ) ) {
					startListeningToTree( children[i] );
				}
			}
		}
	}

	protected void stopListeningToTree( edu.cmu.cs.stage3.alice.core.Element element ) {
		if( element != null ) {
			element.removeChildrenListener( this );
			element.name.removePropertyListener( this );

			edu.cmu.cs.stage3.alice.core.Element[] children = element.getChildren();
			for( int i = 0; i < children.length; i++ ) {
				if( isAcceptedByFilter( children[i] ) ) {
					stopListeningToTree( children[i] );
				}
			}
		}
	}

	protected void clearAllListening( edu.cmu.cs.stage3.alice.core.Element element ) {
		if( element != null ) {
			element.removeChildrenListener( this );
			element.name.removePropertyListener( this );

			edu.cmu.cs.stage3.alice.core.Element[] children = element.getChildren();
			for( int i = 0; i < children.length; i++ ) {
				clearAllListening( children[i] );
			}
		}
	}

	/**
	 * Determines how close a subclass is to a superclass
	 *
	 * @returns  the depth of the class hierarchy between the given superclass and subclass
	 */
	protected static int getClassDepth( Class superclass, Class subclass ) {
		if( ! superclass.isAssignableFrom( subclass ) ) {
			return -1;
		}

		Class temp = subclass;
		int i = 0;
		while( (temp != superclass) && (superclass.isAssignableFrom( temp ) ) ) {
			i++;
			temp = temp.getSuperclass();
		}

		return i;
	}
}
