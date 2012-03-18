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
public class DnDManager {
	private static java.awt.datatransfer.Transferable currentTransferable;
	private static java.awt.Component currentDragComponent;
	private static java.util.HashSet listeners = new java.util.HashSet();
	private static DnDListener dndListener = new DnDListener();
	private static java.awt.dnd.DragSourceContext currentContext;

	public static java.awt.datatransfer.Transferable getCurrentTransferable() {
		return currentTransferable;
	}

	public static void setCurrentTransferable( java.awt.datatransfer.Transferable transferable ) {
		currentTransferable = transferable;
	}

	public static java.awt.Component getCurrentDragComponent() {
		return currentDragComponent;
	}
	
	public static java.awt.dnd.DragSourceContext getCurrentDragContext() {
			return currentContext;
		}

	public static void addListener( edu.cmu.cs.stage3.alice.authoringtool.util.event.DnDManagerListener listener ) {
		synchronized( dndListener ) {
			listeners.add( listener );
		}
	}

	public static void removeListener( edu.cmu.cs.stage3.alice.authoringtool.util.event.DnDManagerListener listener ) {
		synchronized( dndListener ) {
			listeners.remove( listener );
		}
	}

	public static DnDListener getInternalListener() {
		return dndListener;
	}

	public static void fireDragGestureRecognized( java.awt.dnd.DragGestureEvent ev ) {
		for( java.util.Iterator iter = DnDManager.listeners.iterator(); iter.hasNext(); ) {
			((java.awt.dnd.DragGestureListener)iter.next()).dragGestureRecognized( ev );
		}
	}

	public static void fireDragStarted( java.awt.datatransfer.Transferable transferable, java.awt.Component dragComponent ) {
		currentTransferable = transferable;
		currentDragComponent = dragComponent;
		for( java.util.Iterator iter = DnDManager.listeners.iterator(); iter.hasNext(); ) {
			((edu.cmu.cs.stage3.alice.authoringtool.util.event.DnDManagerListener)iter.next()).dragStarted();
		}
	}

	public static class DnDListener implements java.awt.dnd.DragSourceListener {
		synchronized public void dragEnter( java.awt.dnd.DragSourceDragEvent dsde ) {
			//DEBUG System.out.println( "DnDManager.dragEnter" );
			currentTransferable = dsde.getDragSourceContext().getTransferable();
			currentDragComponent = dsde.getDragSourceContext().getComponent();
			currentContext = dsde.getDragSourceContext();
			for( java.util.Iterator iter = DnDManager.listeners.iterator(); iter.hasNext(); ) {
				((java.awt.dnd.DragSourceListener)iter.next()).dragEnter( dsde );
			}
		}

		synchronized public void dragExit( java.awt.dnd.DragSourceEvent dse ) {
			//DEBUG System.out.println( "DnDManager.dragExit" );
			currentTransferable = dse.getDragSourceContext().getTransferable();
			currentDragComponent = dse.getDragSourceContext().getComponent();
			for( java.util.Iterator iter = DnDManager.listeners.iterator(); iter.hasNext(); ) {
				((java.awt.dnd.DragSourceListener)iter.next()).dragExit( dse );
			}
		}

		synchronized public void dragOver( java.awt.dnd.DragSourceDragEvent dsde ) {
			//DEBUG System.out.println( "DnDManager.dragOver" );
			currentTransferable = dsde.getDragSourceContext().getTransferable();
			currentDragComponent = dsde.getDragSourceContext().getComponent();
			for( java.util.Iterator iter = DnDManager.listeners.iterator(); iter.hasNext(); ) {
				((java.awt.dnd.DragSourceListener)iter.next()).dragOver( dsde );
			}
		}

		synchronized public void dropActionChanged( java.awt.dnd.DragSourceDragEvent dsde ) {
			//DEBUG System.out.println( "DnDManager.dropActionChanged" );
			currentTransferable = dsde.getDragSourceContext().getTransferable();
			currentDragComponent = dsde.getDragSourceContext().getComponent();
			for( java.util.Iterator iter = DnDManager.listeners.iterator(); iter.hasNext(); ) {
				((java.awt.dnd.DragSourceListener)iter.next()).dropActionChanged( dsde );
			}
		}

		synchronized public void dragDropEnd( java.awt.dnd.DragSourceDropEvent dsde ) {
			//DEBUG System.out.println( "DnDManager.dragDropEnd" );
			currentTransferable = null;
			currentDragComponent = null;
			for( java.util.Iterator iter = DnDManager.listeners.iterator(); iter.hasNext(); ) {
				((java.awt.dnd.DragSourceListener)iter.next()).dragDropEnd( dsde );
			}
		}
	}
}