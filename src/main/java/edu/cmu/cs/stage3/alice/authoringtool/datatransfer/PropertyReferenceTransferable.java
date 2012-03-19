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
import java.awt.datatransfer.Transferable;

/**
 * @author Jason Pratt
 */
public class PropertyReferenceTransferable implements Transferable {
	// public final static DataFlavor propertyReferenceFlavor = new DataFlavor(
	// DataFlavor.javaJVMLocalObjectMimeType +
	// "; class=edu.cmu.cs.stage3.alice.core.Property",
	// "propertyReferenceFlavor" );
	public final static DataFlavor propertyReferenceFlavor = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReferenceFlavorForClass(edu.cmu.cs.stage3.alice.core.Property.class);

	protected DataFlavor[] flavors;
	protected edu.cmu.cs.stage3.alice.core.Property property;

	public PropertyReferenceTransferable(edu.cmu.cs.stage3.alice.core.Property property) {
		this.property = property;

		flavors = new DataFlavor[2];
		flavors[0] = propertyReferenceFlavor;
		flavors[1] = DataFlavor.stringFlavor; // bug work-around
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return flavors;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		for (DataFlavor flavor2 : flavors) {
			if (flavor.equals(flavor2)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws java.awt.datatransfer.UnsupportedFlavorException, java.io.IOException {
		if (flavor.equals(propertyReferenceFlavor)) {
			return property;
		} else if (flavor.equals(DataFlavor.stringFlavor)) {
			return property.toString();
		} else {
			throw new java.awt.datatransfer.UnsupportedFlavorException(flavor);
		}
	}
}