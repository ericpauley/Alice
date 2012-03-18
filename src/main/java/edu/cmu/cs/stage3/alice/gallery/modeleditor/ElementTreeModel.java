package edu.cmu.cs.stage3.alice.gallery.modeleditor;

class ElementTreeModel implements javax.swing.tree.TreeModel {
	private edu.cmu.cs.stage3.alice.core.Element m_root;
	private java.util.Vector m_treeModelListeners = new java.util.Vector();
	
	private Object[] getPath( edu.cmu.cs.stage3.alice.core.Element element ) {
		java.util.Vector v = new java.util.Vector();
		while( element != m_root.getParent() ) {
			v.insertElementAt( element, 0 );
			element = element.getParent();
		}
		return v.toArray();
	}

	private boolean isAccepted( edu.cmu.cs.stage3.alice.core.Element e ) {
		if( e instanceof edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray ) {
			return false; 
		} else if( e instanceof edu.cmu.cs.stage3.alice.core.Response ) {
			return e instanceof edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse;
		} else if( e instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Component ) {
			return false;
		} else {
			return true;
		}
	}
	private void fireTreeStructureChanged( Object[] path ) {
		javax.swing.event.TreeModelEvent e = new javax.swing.event.TreeModelEvent( this, path );
		java.util.Enumeration enum0 = m_treeModelListeners.elements();
		while( enum0.hasMoreElements() ) {
			javax.swing.event.TreeModelListener l = (javax.swing.event.TreeModelListener)enum0.nextElement();
			l.treeStructureChanged( e );
		}
	}
	public void setRoot( edu.cmu.cs.stage3.alice.core.Element root ) {
		m_root = root;
		fireTreeStructureChanged( getPath( m_root ) );
	}
	public void addTreeModelListener( javax.swing.event.TreeModelListener l ) {
		m_treeModelListeners.addElement( l );
	} 
	public void removeTreeModelListener( javax.swing.event.TreeModelListener l ) {
		m_treeModelListeners.removeElement( l );
	}
	public Object getChild( Object parent, int index ) {
		edu.cmu.cs.stage3.alice.core.Element parentElement = (edu.cmu.cs.stage3.alice.core.Element)parent;
		//return parentElement.getChildAt( index );
		int i = 0;
		for( int lcv=0; lcv<parentElement.getChildCount(); lcv++ ) {
			edu.cmu.cs.stage3.alice.core.Element childAtLCV = parentElement.getChildAt( lcv );
			if( isAccepted( childAtLCV ) ) {
				if( i == index ) {
					return childAtLCV;
				}
				i++;
			}
		}
		return null;
	} 
	public int getChildCount( Object parent ) {
		edu.cmu.cs.stage3.alice.core.Element parentElement = (edu.cmu.cs.stage3.alice.core.Element)parent;
		//return parentElement.getChildCount();
		int i = 0;
		for( int lcv=0; lcv<parentElement.getChildCount(); lcv++ ) {
			edu.cmu.cs.stage3.alice.core.Element childAtLCV = parentElement.getChildAt( lcv );
			if( isAccepted( childAtLCV ) ) {
				i++;
			}
		}
		return i;
	}
	public int getIndexOfChild( Object parent, Object child ) {
		edu.cmu.cs.stage3.alice.core.Element parentElement = (edu.cmu.cs.stage3.alice.core.Element)parent;
		//return parentElement.getIndexOfChild( (edu.cmu.cs.stage3.alice.core.Element)child );
		int i = 0;
		for( int lcv=0; lcv<parentElement.getChildCount(); lcv++ ) {
			edu.cmu.cs.stage3.alice.core.Element childAtLCV = parentElement.getChildAt( lcv );
			if( childAtLCV == child ) {
				return i;
			}
			if( isAccepted( childAtLCV ) ) {
				i++;
			}
		}
		return -1;
	}
	public Object getRoot() {
		return m_root;
	}
	public boolean isLeaf(Object node) {
		return getChildCount( node ) == 0;
	}
	public void valueForPathChanged( javax.swing.tree.TreePath path, Object newValue ) {
		//System.out.println( "*** valueForPathChanged : " + path + " --> " + newValue );
	}
	
	public void removeDescendant( edu.cmu.cs.stage3.alice.core.Element descendant ) {
		Object[] path = getPath( descendant.getParent() );
		descendant.removeFromParent();
		fireTreeStructureChanged( path );
	}
}
