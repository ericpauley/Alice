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

package edu.cmu.cs.stage3.alice.authoringtool.datatransfer;

import java.awt.datatransfer.DataFlavor;

/**
 * @author Jason Pratt
 */
public class ElementPrototypeReferenceTransferable implements java.awt.datatransfer.Transferable {
	public final static java.awt.datatransfer.DataFlavor elementPrototypeReferenceFlavor = new java.awt.datatransfer.DataFlavor(java.awt.datatransfer.DataFlavor.javaJVMLocalObjectMimeType + "; class=edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype", "elementPrototypeReferenceFlavor");

	protected java.awt.datatransfer.DataFlavor[] flavors;
	protected edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype elementPrototype;

	public ElementPrototypeReferenceTransferable(edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype elementPrototype) {
		this.elementPrototype = elementPrototype;

		flavors = new java.awt.datatransfer.DataFlavor[2];
		flavors[0] = elementPrototypeReferenceFlavor;
		flavors[1] = java.awt.datatransfer.DataFlavor.stringFlavor;
	}

	@Override
	public java.awt.datatransfer.DataFlavor[] getTransferDataFlavors() {
		return flavors;
	}

	@Override
	public boolean isDataFlavorSupported(java.awt.datatransfer.DataFlavor flavor) {
		for (DataFlavor flavor2 : flavors) {
			if (flavor.equals(flavor2)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Object getTransferData(java.awt.datatransfer.DataFlavor flavor) throws java.awt.datatransfer.UnsupportedFlavorException, java.io.IOException {
		if (flavor.equals(elementPrototypeReferenceFlavor)) {
			return elementPrototype;
		} else if (flavor.equals(java.awt.datatransfer.DataFlavor.stringFlavor)) {
			return elementPrototype.toString();
		} else {
			throw new java.awt.datatransfer.UnsupportedFlavorException(flavor);
		}
	}
}