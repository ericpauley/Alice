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
public class CommonMathQuestionsTransferable implements java.awt.datatransfer.Transferable {
	public static java.awt.datatransfer.DataFlavor commonMathQuestionsFlavor = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReferenceFlavorForClass( edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CommonMathQuestionsTransferable.class );

	protected java.awt.datatransfer.DataFlavor myFlavor;
	protected java.awt.datatransfer.DataFlavor[] flavors;

	public CommonMathQuestionsTransferable() {
		try {
			myFlavor = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReferenceFlavorForClass( edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CommonMathQuestionsTransferable.class );
			myFlavor.setHumanPresentableName( "commonMathQuestionsTransferable" );
		} catch( Exception e ) {
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( e.getMessage(), e );
		}

		flavors = new java.awt.datatransfer.DataFlavor[2];
		flavors[0] = myFlavor;
		flavors[1] = java.awt.datatransfer.DataFlavor.stringFlavor;
	}

	public java.awt.datatransfer.DataFlavor[] getTransferDataFlavors() {
		return flavors;
	}

	public boolean isDataFlavorSupported( java.awt.datatransfer.DataFlavor flavor ) {
		for( int i = 0; i < flavors.length; i++ ) {
			if( flavor.equals( flavors[i] ) ) {
				return true;
			}
		}
		return false;
	}

	public Object getTransferData( java.awt.datatransfer.DataFlavor flavor ) throws java.awt.datatransfer.UnsupportedFlavorException, java.io.IOException {
		if( flavor.getRepresentationClass().equals( edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CommonMathQuestionsTransferable.class ) ) {
			return this;
		} else if( flavor.equals( java.awt.datatransfer.DataFlavor.stringFlavor ) ) {
			return this.toString();
		} else {
			throw new java.awt.datatransfer.UnsupportedFlavorException( flavor );
		}
	}
}
