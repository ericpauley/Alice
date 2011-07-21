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
public class GroupingPanel extends javax.swing.JPanel implements java.awt.dnd.DropTargetListener, edu.cmu.cs.stage3.alice.authoringtool.util.Releasable {
	protected javax.swing.border.Border outerBorder;
	protected javax.swing.border.Border innerBorder;
	protected javax.swing.border.Border border;

	// preferences
	protected edu.cmu.cs.stage3.alice.authoringtool.util.Configuration authoringToolConfig = edu.cmu.cs.stage3.alice.authoringtool.util.Configuration.getLocalConfiguration( edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.class.getPackage() );

	private class PartialLineBorder extends javax.swing.border.AbstractBorder {
		protected java.awt.Color color;
		protected int thickness;
		protected boolean includeTop;
		protected boolean includeLeft;
		protected boolean includeBottom;
		protected boolean includeRight;

		public PartialLineBorder( java.awt.Color color, int thickness, boolean includeTop, boolean includeLeft, boolean includeBottom, boolean includeRight ) {
			this.color = color;
			this.thickness = thickness;
			this.includeTop = includeTop;
			this.includeLeft = includeLeft;
			this.includeBottom = includeBottom;
			this.includeRight = includeRight;
		}

		
		public void paintBorder( java.awt.Component c, java.awt.Graphics g, int x, int y, int width, int height ) {
			java.awt.Color oldColor = g.getColor();
			g.setColor( color );
			for( int i = 0; i < thickness; i++ )  {
				if( includeTop ) {
					g.drawLine( x, y + i, x + width - 1, y + i );
				}
				if( includeLeft ) {
					g.drawLine( x + i, y, x + i, y + height - 1 );
				}
				if( includeBottom ) {
					g.drawLine( x, y - i + height - 1, x + width - 1, y - i + height - 1 );
				}
				if( includeRight ) {
					g.drawLine( x - i + width - 1, y, x - i + width - 1, y + height - 1 );
				}
			}
			g.setColor( oldColor );
		}

		
		public java.awt.Insets getBorderInsets( java.awt.Component c ) {
			return new java.awt.Insets( includeTop ? thickness : 0, includeLeft ? thickness : 0, includeBottom ? thickness : 0, includeRight ? thickness : 0 );
		}

		
		public java.awt.Insets getBorderInsets( java.awt.Component c, java.awt.Insets insets ) {
			insets.top = includeTop ? thickness : 0;
			insets.left = includeLeft ? thickness : 0;
			insets.bottom = includeBottom ? thickness : 0;
			insets.right = includeRight ? thickness : 0;
			return insets;
		}

		public java.awt.Color getLineColor() {
			return color;
		}

		public int getThickness()       {
			return thickness;
		}

		
		public boolean isBorderOpaque() {
			return true;
		}
	}

	public GroupingPanel() {
		outerBorder = new PartialLineBorder( java.awt.Color.lightGray, 1, false, false, true, true );
		innerBorder = javax.swing.BorderFactory.createEmptyBorder( 1, 1, 1, 1 );
		border = javax.swing.BorderFactory.createCompoundBorder( outerBorder, innerBorder );
		setBorder( border );
		setDoubleBuffered( true );

		//setOpaque( false );

		setDropTarget( new java.awt.dnd.DropTarget( this, this ) );

		addContainerListener(
			new java.awt.event.ContainerAdapter() {
				
				public void componentAdded( java.awt.event.ContainerEvent ev ) {
					if( ev.getChild().getDropTarget() == null ) { // is this heavy-handed?
						ev.getChild().setDropTarget( new java.awt.dnd.DropTarget( ev.getChild(), GroupingPanel.this ) );
					}
				}
				
				public void componentRemoved( java.awt.event.ContainerEvent ev ) { //MEMFIX
					if( ev.getChild().getDropTarget() != null ) { // is this heavy-handed?
						ev.getChild().getDropTarget().setActive( false );
						ev.getChild().setDropTarget( null );
					}
				}
			}
		);
		addContainerListener( GUIElementContainerListener.getStaticListener() );
	}

	public void release() {
		removeAll(); //MEMFIX
//		java.awt.Component[] components = getComponents();
//		if( components != null ) {
//			for( int i = 0; i < components.length; i++ ) {
//				if( components[i] instanceof edu.cmu.cs.stage3.alice.authoringtool.util.Releasable ) {
//					((edu.cmu.cs.stage3.alice.authoringtool.util.Releasable)components[i]).release();
//				}
//			}
//		}
	}

	public void paintBackground( java.awt.Graphics g ) {
	}

	public void paintForeground( java.awt.Graphics g ) {
	}

	
	public void paintComponent( java.awt.Graphics g ) {
		super.paintComponent( g );
		paintBackground( g );
	}

	
	public void paintChildren( java.awt.Graphics g ) {
		super.paintChildren( g );
		paintForeground( g );
	}

	
	public void printComponent( java.awt.Graphics g ) {
		if( authoringToolConfig.getValue( "printing.fillBackground" ).equalsIgnoreCase( "true" ) ) {
			super.printComponent( g );
		} else {
			// do nothing (avoid painting background)
		}
	}

	// might get in trouble for going to *any* Container that's a DropTargetListener.  we'll see...
	public void dragEnter( java.awt.dnd.DropTargetDragEvent dtde ) {
		if( getParent() instanceof java.awt.dnd.DropTargetListener ) {
			((java.awt.dnd.DropTargetListener)getParent()).dragEnter( dtde );
		} else {
			dtde.rejectDrag();
		}
	}

	public void dragExit( java.awt.dnd.DropTargetEvent dte ) {
		if( getParent() instanceof java.awt.dnd.DropTargetListener ) {
			((java.awt.dnd.DropTargetListener)getParent()).dragExit( dte );
		}
	}

	public void dragOver( java.awt.dnd.DropTargetDragEvent dtde ) {
		if( getParent() instanceof java.awt.dnd.DropTargetListener ) {
			((java.awt.dnd.DropTargetListener)getParent()).dragOver( dtde );
		} else {
			dtde.rejectDrag();
		}
	}

	public void drop( java.awt.dnd.DropTargetDropEvent dtde ) {
		if( getParent() instanceof java.awt.dnd.DropTargetListener ) {
			((java.awt.dnd.DropTargetListener)getParent()).drop( dtde );
		} else {
			dtde.rejectDrop();
		}
	}

	public void dropActionChanged( java.awt.dnd.DropTargetDragEvent dtde ) {
		if( getParent() instanceof java.awt.dnd.DropTargetListener ) {
			((java.awt.dnd.DropTargetListener)getParent()).dropActionChanged( dtde );
		} else {
			dtde.rejectDrag();
		}
	}
}