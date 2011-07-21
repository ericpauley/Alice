package edu.cmu.cs.stage3.alice.gallery.modeleditor;

class ElementTreeCellEditor extends javax.swing.tree.DefaultTreeCellEditor {
	public ElementTreeCellEditor( javax.swing.JTree tree, ElementTreeCellRenderer renderer ) {
		super( tree, renderer );
	}
	
	public boolean isCellEditable( java.util.EventObject e ) {
		if( e == null ) {
			return true;
		} else {
			return super.isCellEditable( e );
		}
	}
	
	protected boolean canEditImmediately( java.util.EventObject event ) {
		if( event instanceof java.awt.event.MouseEvent ) {
			java.awt.event.MouseEvent me = (java.awt.event.MouseEvent)event;
			if( ( me.getModifiers() & java.awt.event.InputEvent.BUTTON1_MASK ) != 0 ) {
				return me.getClickCount()>0;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	
	protected void prepareForEditing() {
		super.prepareForEditing();
		if( editingComponent instanceof javax.swing.JTextField ) {
			((javax.swing.JTextField)editingComponent).selectAll();
		}
	}
	
	public java.awt.Component getTreeCellEditorComponent( javax.swing.JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row ) {
		java.awt.Component editor = super.getTreeCellEditorComponent( tree, value, selected, expanded, leaf, row );
		editingIcon = IconManager.lookupIcon( value );
		return editor;
	}
}

