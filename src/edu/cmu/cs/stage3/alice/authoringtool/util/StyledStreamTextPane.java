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
public class StyledStreamTextPane extends javax.swing.JTextPane {
	public javax.swing.text.Style defaultStyle = javax.swing.text.StyleContext.getDefaultStyleContext().getStyle( javax.swing.text.StyleContext.DEFAULT_STYLE );
	public javax.swing.text.Style stdOutStyle;
	public javax.swing.text.Style stdErrStyle;

	javax.swing.text.DefaultStyledDocument document = new javax.swing.text.DefaultStyledDocument();
	javax.swing.text.Position endPosition;
	StyleStream defaultStream;

	public StyledStreamTextPane() {
		setDocument( document );
		endPosition = document.getEndPosition();

		javax.swing.text.StyleConstants.setFontFamily( defaultStyle, "Monospaced" );
		stdOutStyle = this.addStyle( "stdOut", defaultStyle );
		stdErrStyle = this.addStyle( "stdErr", defaultStyle );
		javax.swing.text.StyleConstants.setForeground( stdErrStyle, edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("stdErrTextColor") );

		defaultStream = getNewStyleStream( defaultStyle );

		setDropTarget( new java.awt.dnd.DropTarget( this, new OutputDropTargetListener() ) );
		addMouseListener( mouseListener );
	}

	public StyleStream getNewStyleStream( javax.swing.text.Style style ) {
		return new StyleStream( this, style );
	}

	// prevent word wrapping
	
	public boolean getScrollableTracksViewportWidth() {
		java.awt.Component parent = getParent();
		if( parent != null ) {
			int parentWidth = parent.getSize().width;
			int preferredWidth = getUI().getPreferredSize( this ).width;
			return preferredWidth < parentWidth;
		} else {
			return false;
		}
	}

//	public java.awt.Dimension getPreferredScrollableViewportSize() {
//		return new java.awt.Dimension( 5000, 100 );
//	}
//
//	public boolean getScrollableTracksViewportWidth() {
//		return false;
//	}
//
//	public java.awt.Dimension getMinimumSize() {
//		return new java.awt.Dimension( 5000, 0 );
//	}
//
//	public java.awt.Dimension getPreferredSize() {
//		return new java.awt.Dimension( 5000, 200 );
//	}

//	public class StyleStream extends java.io.PrintStream {
//		protected javax.swing.text.Style style;
//		protected StyledStreamTextPane styledStreamTextPane;
//
//		public StyleStream( StyledStreamTextPane styledStreamTextPane, javax.swing.text.Style style ) {
//			super( System.out );
//			this.styledStreamTextPane = styledStreamTextPane;
//			this.style = style;
//		}
//
//		public void write( int b ) {
//			try {
//				styledStreamTextPane.document.insertString( styledStreamTextPane.endPosition.getOffset() - 1, String.valueOf( b ), style );
//			} catch( javax.swing.text.BadLocationException e ) {
//				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Error while printing.", e );
//			}
//		}
//
//		public void write( byte buf[], int off, int len ) {
//			try {
//				styledStreamTextPane.document.insertString( styledStreamTextPane.endPosition.getOffset() - 1, new String( buf, off, len ), style );
//			} catch( javax.swing.text.BadLocationException e ) {
//				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Error while printing.", e );
//			}
//		}
//	}

	protected final java.awt.event.MouseListener mouseListener = new CustomMouseAdapter() {
		
		protected void popupResponse( java.awt.event.MouseEvent e ) {
			javax.swing.JPopupMenu popup = createPopup();
			popup.show( e.getComponent(), e.getX(), e.getY() );
			PopupMenuUtilities.ensurePopupIsOnScreen( popup );
		}

		private javax.swing.JPopupMenu createPopup() {
			Runnable clearAllRunnable = new Runnable() {
				public void run() {
					StyledStreamTextPane.this.setText( "" );
				}
			};

			java.util.Vector structure = new java.util.Vector();
			structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "Clear All", clearAllRunnable ) );

			return PopupMenuUtilities.makePopupMenu( structure );
		}
	};

	class OutputDropTargetListener implements java.awt.dnd.DropTargetListener {
		public OutputDropTargetListener() {}

		public void dragEnter( java.awt.dnd.DropTargetDragEvent dtde ) {
//			java.awt.datatransfer.DataFlavor[] flavors = dtde.getCurrentDataFlavors();
//			System.out.println( "flavors:" );
//			for( int i = 0; i < flavors.length; i++ ) {
//				System.out.println( flavors[i].getHumanPresentableName() );
//				System.out.println( "\t" + flavors[i].getMimeType() );
//				System.out.println( "\t" + flavors[i].getPrimaryType() );
//				System.out.println( "\t" + flavors[i] );
//			}
		}

		public void dragOver( java.awt.dnd.DropTargetDragEvent dtde ) {}

		public void drop( java.awt.dnd.DropTargetDropEvent dtde ) {
			java.awt.datatransfer.Transferable transferable = dtde.getTransferable();
			java.awt.datatransfer.DataFlavor[] flavors = transferable.getTransferDataFlavors();

			if( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor ) ) {
				try {
					dtde.acceptDrop( java.awt.dnd.DnDConstants.ACTION_LINK );
					ElementPrototype elementPrototype = (ElementPrototype)transferable.getTransferData( edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor );

					if( elementPrototype.getDesiredProperties().length > 0 ) {
						PopupItemFactory factory = new PopupItemFactory() {
							public Object createItem( final Object object ) {
								return new Runnable() {
									public void run() {
										if( object instanceof ElementPrototype ) {
											edu.cmu.cs.stage3.alice.core.Element e = ((ElementPrototype)object).createNewElement();
											if( e instanceof edu.cmu.cs.stage3.alice.core.Question ) {
												StyledStreamTextPane.this.defaultStream.println( ((edu.cmu.cs.stage3.alice.core.Question)e).getValue() );
											} else {
												StyledStreamTextPane.this.defaultStream.println( e );
											}
										} else {
											StyledStreamTextPane.this.defaultStream.println( object );
										}
									}
								};
							}
						};
						java.util.Vector structure = PopupMenuUtilities.makePrototypeStructure( elementPrototype, factory, null );
						javax.swing.JPopupMenu popup = PopupMenuUtilities.makePopupMenu( structure );
						popup.show( StyledStreamTextPane.this, (int)dtde.getLocation().getX(), (int)dtde.getLocation().getY() );
						PopupMenuUtilities.ensurePopupIsOnScreen( popup );
					} else {
						edu.cmu.cs.stage3.alice.core.Element e = elementPrototype.createNewElement();
						if( e instanceof edu.cmu.cs.stage3.alice.core.Question ) {
							StyledStreamTextPane.this.defaultStream.println( ((edu.cmu.cs.stage3.alice.core.Question)e).getValue() );
						} else {
							StyledStreamTextPane.this.defaultStream.println( e );
						}
					}

					dtde.getDropTargetContext().dropComplete( true );
					return;
				} catch( java.awt.datatransfer.UnsupportedFlavorException e ) {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Drop didn't work: bad flavor", e );
				} catch( java.io.IOException e ) {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Drop didn't work: IOException", e );
				}
			}

			if( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(transferable, java.awt.datatransfer.DataFlavor.stringFlavor ) ) {
				try {
					dtde.acceptDrop( java.awt.dnd.DnDConstants.ACTION_COPY );
					Object transferredObject = transferable.getTransferData( java.awt.datatransfer.DataFlavor.stringFlavor );
					StyledStreamTextPane.this.defaultStream.println( transferredObject );
					dtde.getDropTargetContext().dropComplete( true );
					return;
				} catch( java.awt.datatransfer.UnsupportedFlavorException e ) {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Drop didn't work: bad flavor", e );
				} catch( java.io.IOException e ) {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Drop didn't work: IOException", e );
				}
			} else if( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(transferable, java.awt.datatransfer.DataFlavor.getTextPlainUnicodeFlavor() ) ) {
				try {
					dtde.acceptDrop( java.awt.dnd.DnDConstants.ACTION_COPY );
					java.io.Reader transferredObject = java.awt.datatransfer.DataFlavor.getTextPlainUnicodeFlavor().getReaderForText( transferable );
					StyledStreamTextPane.this.defaultStream.println( transferredObject );

					final java.io.BufferedReader fileReader = new java.io.BufferedReader( transferredObject );
					Thread fileReaderThread = new Thread() {
						
						public void run() {
							String line;
							try {
								while( true ) {
									line = fileReader.readLine();
									if( line == null ) {
										break;
									} else {
										StyledStreamTextPane.this.defaultStream.println( line );
									}
								}
								fileReader.close();
							} catch( java.io.IOException e ) {
								edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Error reading file.", e );
							}
						}
					};
					fileReaderThread.start();

					dtde.getDropTargetContext().dropComplete( true );
					return;
				} catch( java.awt.datatransfer.UnsupportedFlavorException e ) {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Drop didn't work: bad flavor", e );
				} catch( java.io.IOException e ) {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Drop didn't work: IOException", e );
				}
			}
			dtde.rejectDrop();
			dtde.getDropTargetContext().dropComplete( true );
		}

		public void dropActionChanged( java.awt.dnd.DropTargetDragEvent dtde ) {}

		public void dragExit( java.awt.dnd.DropTargetEvent dte ) {}
	}
}

