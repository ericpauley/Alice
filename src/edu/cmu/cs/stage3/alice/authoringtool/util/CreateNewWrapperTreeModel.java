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
public class CreateNewWrapperTreeModel implements javax.swing.tree.TreeModel {
	protected javax.swing.tree.TreeModel wrappedTreeModel;
	protected Object createNewObject;

	public CreateNewWrapperTreeModel( javax.swing.tree.TreeModel treeModelToWrap, Object createNewObject ) {
		if( (treeModelToWrap != null) && (createNewObject != null) ) {
			wrappedTreeModel = treeModelToWrap;
			this.createNewObject = createNewObject;
		} else {
			throw new IllegalArgumentException( "treeModelToWrap and createNewObject cannot be null" );
		}
	}

	public Object getRoot() {
		return wrappedTreeModel.getRoot();
	}

	public Object getChild( Object parent, int index ) {
		if( (parent == wrappedTreeModel.getRoot()) && (index == wrappedTreeModel.getChildCount( parent )) ) {
			return createNewObject;
		} else {
			return wrappedTreeModel.getChild( parent, index );
		}
	}

	public int getChildCount( Object parent ) {
		if( parent == wrappedTreeModel.getRoot() ) {
			return wrappedTreeModel.getChildCount( parent ) + 1;
		} else {
			return wrappedTreeModel.getChildCount( parent );
		}
	}

	public boolean isLeaf( Object node ) {
		if( node == createNewObject ) {
			return true;
		} else {
			return wrappedTreeModel.isLeaf( node );
		}
	}

	public void valueForPathChanged( javax.swing.tree.TreePath path, Object newValue ) {
		if( ! (path.getLastPathComponent() == createNewObject) ) {
			wrappedTreeModel.valueForPathChanged( path, newValue );
		}
	}

	public int getIndexOfChild( Object parent, Object child ) {
		if( child == createNewObject ) {
			return wrappedTreeModel.getChildCount( parent );
		} else {
			return wrappedTreeModel.getIndexOfChild( parent, child );
		}
	}

	public void addTreeModelListener( javax.swing.event.TreeModelListener l ) {
		wrappedTreeModel.addTreeModelListener( l );
	}

	public void removeTreeModelListener( javax.swing.event.TreeModelListener l ) {
		wrappedTreeModel.removeTreeModelListener( l );
	}
}