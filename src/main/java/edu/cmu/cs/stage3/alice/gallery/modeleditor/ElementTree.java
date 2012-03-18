package edu.cmu.cs.stage3.alice.gallery.modeleditor;

class ElementTree extends javax.swing.JTree {
	public ElementTree( ElementTreeModel model ) {
		super( model );
	}
	
	public String convertValueToText( Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus ) {
		edu.cmu.cs.stage3.alice.core.Element element = (edu.cmu.cs.stage3.alice.core.Element)value;
		if( element != null ) {
			return element.name.getStringValue();
		} else {
			return null;
		}
	}
}

