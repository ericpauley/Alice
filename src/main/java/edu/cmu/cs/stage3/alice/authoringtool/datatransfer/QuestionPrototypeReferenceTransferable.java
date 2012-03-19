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

/**
 * @author Jason Pratt
 */
public class QuestionPrototypeReferenceTransferable extends ElementPrototypeReferenceTransferable {
	public final static java.awt.datatransfer.DataFlavor questionPrototypeReferenceFlavor = new java.awt.datatransfer.DataFlavor(java.awt.datatransfer.DataFlavor.javaJVMLocalObjectMimeType + "; class=edu.cmu.cs.stage3.alice.authoringtool.util.QuestionPrototype", "questionPrototypeReferenceFlavor");

	protected edu.cmu.cs.stage3.alice.authoringtool.util.QuestionPrototype questionPrototype;

	public QuestionPrototypeReferenceTransferable(edu.cmu.cs.stage3.alice.authoringtool.util.QuestionPrototype questionPrototype) {
		super(questionPrototype);
		this.questionPrototype = questionPrototype;

		flavors = new java.awt.datatransfer.DataFlavor[3];
		flavors[0] = questionPrototypeReferenceFlavor;
		flavors[1] = ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor;
		flavors[2] = java.awt.datatransfer.DataFlavor.stringFlavor;
	}

	@Override
	public Object getTransferData(java.awt.datatransfer.DataFlavor flavor) throws java.awt.datatransfer.UnsupportedFlavorException, java.io.IOException {
		if (flavor.equals(questionPrototypeReferenceFlavor)) {
			return questionPrototype;
		} else if (flavor.equals(ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor)) {
			return questionPrototype;
		} else if (flavor.equals(java.awt.datatransfer.DataFlavor.stringFlavor)) {
			return questionPrototype.toString();
		} else {
			throw new java.awt.datatransfer.UnsupportedFlavorException(flavor);
		}
	}
}