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
public class TransferableFactory {
	public static java.awt.datatransfer.Transferable createTransferable( Object object ) {
		if( object instanceof edu.cmu.cs.stage3.alice.core.Element ) {
			return new ElementReferenceTransferable( (edu.cmu.cs.stage3.alice.core.Element)object );
		} else if( object instanceof edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedResponsePrototype ) {
			return new CallToUserDefinedResponsePrototypeReferenceTransferable( (edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedResponsePrototype)object );
		} else if( object instanceof edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype ) {
			return new CallToUserDefinedQuestionPrototypeReferenceTransferable( (edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype)object );
		} else if( object instanceof edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype ) {
			return new ResponsePrototypeReferenceTransferable( (edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype)object );
		} else if( object instanceof edu.cmu.cs.stage3.alice.authoringtool.util.QuestionPrototype ) {
			return new QuestionPrototypeReferenceTransferable( (edu.cmu.cs.stage3.alice.authoringtool.util.QuestionPrototype)object );
		} else if( object instanceof edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype ) {
			return new ElementPrototypeReferenceTransferable( (edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype)object );
		} else if( object instanceof edu.cmu.cs.stage3.alice.authoringtool.util.ObjectArrayPropertyItem ) {
			return new ObjectArrayPropertyItemTransferable( (edu.cmu.cs.stage3.alice.authoringtool.util.ObjectArrayPropertyItem)object );
		} else if( object instanceof edu.cmu.cs.stage3.alice.core.Property ) {
			return new PropertyReferenceTransferable( (edu.cmu.cs.stage3.alice.core.Property)object );
		} else if( object instanceof edu.cmu.cs.stage3.alice.core.CopyFactory ) {
			return new CopyFactoryTransferable( (edu.cmu.cs.stage3.alice.core.CopyFactory)object );
		} else {
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "cannot create Transferable for: " + object, null );
			return null;
		}
	}
}