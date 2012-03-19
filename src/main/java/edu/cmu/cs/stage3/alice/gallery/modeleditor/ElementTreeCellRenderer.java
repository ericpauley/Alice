package edu.cmu.cs.stage3.alice.gallery.modeleditor;

class ElementTreeCellRenderer extends javax.swing.tree.DefaultTreeCellRenderer {

	@Override
	public java.awt.Component getTreeCellRendererComponent(javax.swing.JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		openIcon = closedIcon = leafIcon = IconManager.lookupIcon(value);
		java.awt.Component component = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
		if (value instanceof edu.cmu.cs.stage3.alice.core.TextureMap) {
			edu.cmu.cs.stage3.alice.core.TextureMap tm = (edu.cmu.cs.stage3.alice.core.TextureMap) value;
			java.awt.image.BufferedImage image = (java.awt.image.BufferedImage) tm.getSceneGraphTextureMap().getImage();
			StringBuffer sb = new StringBuffer();
			sb.append(image.getWidth());
			sb.append('x');
			sb.append(image.getHeight());
			setToolTipText(sb.toString());
		} else if (value instanceof edu.cmu.cs.stage3.alice.core.Model) {
			edu.cmu.cs.stage3.alice.core.Model model = (edu.cmu.cs.stage3.alice.core.Model) value;
			edu.cmu.cs.stage3.alice.core.util.IndexedTriangleArrayCounter itaCounter = new edu.cmu.cs.stage3.alice.core.util.IndexedTriangleArrayCounter();
			model.visit(itaCounter, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_PARTS);
			StringBuffer sb = new StringBuffer();
			sb.append("vertices: ");
			sb.append(itaCounter.getVertexCount());
			sb.append("; triangles: ");
			sb.append(itaCounter.getIndexCount() / 3);
			setToolTipText(sb.toString());
		} else {
			setToolTipText(null);
		}
		return component;
	}
}
