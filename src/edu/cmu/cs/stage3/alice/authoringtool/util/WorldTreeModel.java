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
public class WorldTreeModel extends TreeModelSupport implements edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateListener, javax.swing.tree.TreeModel {
	protected edu.cmu.cs.stage3.alice.core.Element root;
	protected Object[] emptyPath = { new edu.cmu.cs.stage3.alice.core.World() };
	protected ChildrenListener childrenListener = new ChildrenListener();
	protected NameListener nameListener = new NameListener();
	protected edu.cmu.cs.stage3.alice.core.Element currentScope;

	public edu.cmu.cs.stage3.alice.core.World HACK_getOriginalRoot() {
		return (edu.cmu.cs.stage3.alice.core.World)emptyPath[ 0 ];
	}

	public edu.cmu.cs.stage3.alice.core.Element getCurrentScope() {
		return currentScope;
	}

	public void setCurrentScope( edu.cmu.cs.stage3.alice.core.Element scope ) {
		currentScope = scope;
	}

	public boolean isElementInScope( edu.cmu.cs.stage3.alice.core.Element element ) {
		if( currentScope != null ) {
			if( element == currentScope ) {
				return true;
			} else if( element.isDescendantOf( currentScope ) ) {
				return true;
			}
		}

		return false;
	}

	public void setRoot( edu.cmu.cs.stage3.alice.core.Element root ) {
		stopListeningToTree( this.root );
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
				return emptyPath;  // element's not in the tree
//				return new Object[] {};  // element's not in the tree
			}
			list.addFirst( e );
			if( e == root ) {
				break;
			}
			e = e.getParent();
		}

		if( list.isEmpty() ) {
			return emptyPath;
		} else {
			return list.toArray();
		}
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
		if( node == root ) {
			return false;
		}

		return !(getChildCount( node ) > 0);
	}

	public int getChildCount( Object parent ) {
		if( ! (parent instanceof edu.cmu.cs.stage3.alice.core.Element) ) {
			throw new IllegalArgumentException( "nodes must be edu.cmu.cs.stage3.alice.core.Elements" );
		}

		int childCount = 0;
		edu.cmu.cs.stage3.alice.core.Element element = (edu.cmu.cs.stage3.alice.core.Element)parent;
		String[] childrenProperties = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getWorldTreeChildrenPropertiesStructure( element.getClass() );
		if( childrenProperties != null ) {
			for( int i = 0; i < childrenProperties.length; i++ ) {
				edu.cmu.cs.stage3.alice.core.Property p = element.getPropertyNamed( childrenProperties[i] );
				if( p instanceof edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty ) {
					edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty oap = (edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty)p;
					if( edu.cmu.cs.stage3.alice.core.Element.class.isAssignableFrom( oap.getComponentType() ) ) {
						//DEBUG System.out.println( oap + ".size(): " + oap.size() );
						for( int j = 0; j < oap.size(); j++ ) {
							//DEBUG System.out.println( "item " + j + ": " + oap.get( j ) );
							if( element.hasChild( (edu.cmu.cs.stage3.alice.core.Element)oap.get( j ) ) ) {
								childCount++;
							}
						}
					}
				}
			}
		}

		//DEBUG System.out.println( "getChildCount( " + parent + " ): " + childCount );
		return childCount;
	}

	public Object getChild( Object parent, int index ) {
		if( ! (parent instanceof edu.cmu.cs.stage3.alice.core.Element) ) {
			throw new IllegalArgumentException( "nodes must be edu.cmu.cs.stage3.alice.core.Elements" );
		}

		int childCount = 0;
		edu.cmu.cs.stage3.alice.core.Element element = (edu.cmu.cs.stage3.alice.core.Element)parent;
		String[] childrenProperties = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getWorldTreeChildrenPropertiesStructure( element.getClass() );
		if( childrenProperties != null ) {
			for( int i = 0; i < childrenProperties.length; i++ ) {
				edu.cmu.cs.stage3.alice.core.Property p = element.getPropertyNamed( childrenProperties[i] );
				if( p instanceof edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty ) {
					edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty oap = (edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty)p;
					if( edu.cmu.cs.stage3.alice.core.Element.class.isAssignableFrom( oap.getComponentType() ) ) {
						for( int j = 0; j < oap.size(); j++ ) {
							if( element.hasChild( (edu.cmu.cs.stage3.alice.core.Element)oap.get( j ) ) ) {
								if( childCount == index ) {
									return oap.get( j );
								}
								childCount++;
							}
						}
					}
				}
			}
		}

		return null;
	}

	public int getIndexOfChild( Object parent, Object child ) {
		if( parent == null ) {
			return -1;
		}
		if( ! (parent instanceof edu.cmu.cs.stage3.alice.core.Element) ) {
			throw new IllegalArgumentException( "nodes must be edu.cmu.cs.stage3.alice.core.Elements" );
		}
		if( ! (child instanceof edu.cmu.cs.stage3.alice.core.Element) ) {
			throw new IllegalArgumentException( "nodes must be edu.cmu.cs.stage3.alice.core.Elements" );
		}

		int childCount = 0;
		edu.cmu.cs.stage3.alice.core.Element element = (edu.cmu.cs.stage3.alice.core.Element)parent;
		String[] childrenProperties = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getWorldTreeChildrenPropertiesStructure( element.getClass() );
		if( childrenProperties != null ) {
			for( int i = 0; i < childrenProperties.length; i++ ) {
				edu.cmu.cs.stage3.alice.core.Property p = element.getPropertyNamed( childrenProperties[i] );
				if( p instanceof edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty ) {
					edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty oap = (edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty)p;
					if( edu.cmu.cs.stage3.alice.core.Element.class.isAssignableFrom( oap.getComponentType() ) ) {
						for( int j = 0; j < oap.size(); j++ ) {
							if( element.hasChild( (edu.cmu.cs.stage3.alice.core.Element)oap.get( j ) ) ) {
								if( child == oap.get( j ) ) {
									return childCount;
								}
								childCount++;
							}
						}
					}
				}
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
			} catch( edu.cmu.cs.stage3.alice.core.IllegalNameValueException e ) {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( e.getMessage(), e );
				element.name.set( previousName );
			}
		} else {
			throw new RuntimeException( "only allows name changes through the model" );
		}
	}

	///////////////////////////////////////////////
	// AuthoringToolStateListener interface
	///////////////////////////////////////////////

	public void stateChanging( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {
		if( ev.getCurrentState() == edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent.RUNTIME_STATE ) {
			setListeningEnabled( false );
		} else {
			setListeningEnabled( true );
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

	////////////////////////////////////////
	// ChildrenListener and NameListener
	////////////////////////////////////////

	public class ChildrenListener implements edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener {
		public void objectArrayPropertyChanging( edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent ev ) {}

		public void objectArrayPropertyChanged( final edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent ev ) {
			edu.cmu.cs.stage3.alice.core.Element element = (edu.cmu.cs.stage3.alice.core.Element)ev.getItem();
			edu.cmu.cs.stage3.alice.core.Element parent = ev.getObjectArrayProperty().getOwner();
			Object[] path = getPath( parent );

			if( ev.getChangeType() == edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent.ITEM_INSERTED ) {
				if( element.getParent() == parent ) { //TODO: probably shouldn't be dependent on being parented before being added to objectArrayProperty
					startListeningToTree( element );

					int[] childIndices = { getIndexOfChild( parent, element ) };
					Object[] children = { element };

//					javax.swing.event.TreeModelEvent tmev = new javax.swing.event.TreeModelEvent( this, path, childIndices, children );
//					fireTreeNodesInserted( tmev );

					// HACK;  i get weird tree behavior with the code above
					javax.swing.event.TreeModelEvent tmev = new javax.swing.event.TreeModelEvent( this, path );
					fireTreeStructureChanged( tmev );
				}
			} else if( ev.getChangeType() == edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent.ITEM_REMOVED ) {
				stopListeningToTree( element );

				// this is a cop-out; it would be a pain to calculate the previous position in the filtered tree
				// of the already deleted element
				javax.swing.event.TreeModelEvent tmev = new javax.swing.event.TreeModelEvent( this, path );
				fireTreeStructureChanged( tmev );
			} else if( ev.getChangeType() == edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent.ITEM_SHIFTED ) {
				// this one isn't a cop-out
				javax.swing.event.TreeModelEvent tmev = new javax.swing.event.TreeModelEvent( this, path );
				fireTreeStructureChanged( tmev );
			}
		}
	}

	public class NameListener implements edu.cmu.cs.stage3.alice.core.event.PropertyListener {
		public void propertyChanging( edu.cmu.cs.stage3.alice.core.event.PropertyEvent ev ) {}

		public void propertyChanged( edu.cmu.cs.stage3.alice.core.event.PropertyEvent ev ) {
			edu.cmu.cs.stage3.alice.core.Element element = ev.getProperty().getOwner();
			edu.cmu.cs.stage3.alice.core.Element parent = element.getParent();
			Object[] path = getPath( parent );
			int[] childIndices = { getIndexOfChild( parent, element ) };
			Object[] children = { element };
			if( (path == null) || (path.length == 0) ) {
				path = getPath( element );
				childIndices = null;
				children = null;
			}
			javax.swing.event.TreeModelEvent tmev = new javax.swing.event.TreeModelEvent( this, path, childIndices, children );
			fireTreeNodesChanged( tmev );
		}
	}

	////////////////////////////////
	// protected methods
	////////////////////////////////

	protected void startListeningToTree( edu.cmu.cs.stage3.alice.core.Element element ) {
		if( element != null ) {
			element.name.addPropertyListener( nameListener );

			String[] childrenProperties = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getWorldTreeChildrenPropertiesStructure( element.getClass() );
			if( childrenProperties != null ) {
				for( int i = 0; i < childrenProperties.length; i++ ) {
					edu.cmu.cs.stage3.alice.core.Property p = element.getPropertyNamed( childrenProperties[i] );
					if( p instanceof edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty ) {
						edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty oap = (edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty)p;
						if( edu.cmu.cs.stage3.alice.core.Element.class.isAssignableFrom( oap.getComponentType() ) ) {
							oap.addObjectArrayPropertyListener( childrenListener );
							for( int j = 0; j < oap.size(); j++ ) {
								if( element.hasChild( (edu.cmu.cs.stage3.alice.core.Element)oap.get( j ) ) ) {
									startListeningToTree( (edu.cmu.cs.stage3.alice.core.Element)oap.get( j ) );
								}
							}
						}
					}
				}
			}
		}
	}

	protected void stopListeningToTree( edu.cmu.cs.stage3.alice.core.Element element ) {
		if( element != null ) {
			element.name.removePropertyListener( nameListener );

			String[] childrenProperties = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getWorldTreeChildrenPropertiesStructure( element.getClass() );
			if( childrenProperties != null ) {
				for( int i = 0; i < childrenProperties.length; i++ ) {
					edu.cmu.cs.stage3.alice.core.Property p = element.getPropertyNamed( childrenProperties[i] );
					if( p instanceof edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty ) {
						edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty oap = (edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty)p;
						if( edu.cmu.cs.stage3.alice.core.Element.class.isAssignableFrom( oap.getComponentType() ) ) {
							oap.removeObjectArrayPropertyListener( childrenListener );
							for( int j = 0; j < oap.size(); j++ ) {
								if( element.hasChild( (edu.cmu.cs.stage3.alice.core.Element)oap.get( j ) ) ) {
									stopListeningToTree( (edu.cmu.cs.stage3.alice.core.Element)oap.get( j ) );
								}
							}
						}
					}
				}
			}
		}
	}

	/*
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
	*/

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
