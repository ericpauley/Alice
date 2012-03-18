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

public class ElementReferenceTransferable implements java.awt.datatransfer.Transferable {
	// these are for convenience
	public final static java.awt.datatransfer.DataFlavor elementReferenceFlavor = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReferenceFlavorForClass( edu.cmu.cs.stage3.alice.core.Element.class );
	public final static java.awt.datatransfer.DataFlavor expressionReferenceFlavor = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReferenceFlavorForClass( edu.cmu.cs.stage3.alice.core.Expression.class );
	public final static java.awt.datatransfer.DataFlavor questionReferenceFlavor = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReferenceFlavorForClass( edu.cmu.cs.stage3.alice.core.Question.class );
	public final static java.awt.datatransfer.DataFlavor responseReferenceFlavor = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReferenceFlavorForClass( edu.cmu.cs.stage3.alice.core.Response.class );
	public final static java.awt.datatransfer.DataFlavor callToUserDefinedQuestionReferenceFlavor = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReferenceFlavorForClass( edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion.class );
	public final static java.awt.datatransfer.DataFlavor callToUserDefinedResponseReferenceFlavor = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReferenceFlavorForClass( edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse.class );
	public final static java.awt.datatransfer.DataFlavor behaviorReferenceFlavor = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReferenceFlavorForClass( edu.cmu.cs.stage3.alice.core.Behavior.class );
	public final static java.awt.datatransfer.DataFlavor textureMapReferenceFlavor = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReferenceFlavorForClass( edu.cmu.cs.stage3.alice.core.TextureMap.class );
	public final static java.awt.datatransfer.DataFlavor transformableReferenceFlavor = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReferenceFlavorForClass( edu.cmu.cs.stage3.alice.core.Transformable.class );
	public final static java.awt.datatransfer.DataFlavor variableReferenceFlavor = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReferenceFlavorForClass( edu.cmu.cs.stage3.alice.core.Variable.class );
	public final static java.awt.datatransfer.DataFlavor worldReferenceFlavor = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReferenceFlavorForClass( edu.cmu.cs.stage3.alice.core.World.class );

	protected java.awt.datatransfer.DataFlavor[] flavors;
	protected edu.cmu.cs.stage3.alice.core.Element element;

	public ElementReferenceTransferable( edu.cmu.cs.stage3.alice.core.Element element ) {
		this.element = element;

		java.util.HashSet assignables = new java.util.HashSet();
		if( element != null ) {
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.findAssignables( element.getClass(), assignables, true );
		} else {
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.findAssignables( edu.cmu.cs.stage3.alice.core.Element.class, assignables, true );
		}

		flavors = new java.awt.datatransfer.DataFlavor[assignables.size() + 1];
		int i = 0;
		for( java.util.Iterator iter = assignables.iterator(); iter.hasNext(); ) {
			flavors[i++] = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReferenceFlavorForClass( (Class)iter.next() );
		}

		flavors[i++] = java.awt.datatransfer.DataFlavor.stringFlavor;
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
		if( flavor.equals( java.awt.datatransfer.DataFlavor.stringFlavor ) ) {
			return element.toString();
		} else if( flavor.getRepresentationClass().isAssignableFrom( element.getClass() ) ) {
			return element;
		} else {
			throw new java.awt.datatransfer.UnsupportedFlavorException( flavor );
		}
	}

	public ElementReferenceTransferable createCopy() {
		if( element != null ) {
			edu.cmu.cs.stage3.alice.core.Element copy = element.createCopyNamed( element.name.getStringValue() );
			return new ElementReferenceTransferable( copy );
		} else {
			return null;
		}
	}
}