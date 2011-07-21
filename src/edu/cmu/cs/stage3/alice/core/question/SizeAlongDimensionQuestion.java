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

package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.Dimension;
import edu.cmu.cs.stage3.alice.core.Question;
import edu.cmu.cs.stage3.alice.core.property.TransformableProperty;
import edu.cmu.cs.stage3.alice.core.Transformable;

public abstract class SizeAlongDimensionQuestion extends Question {
	public final TransformableProperty subject = new TransformableProperty( this, "subject", null );
	protected abstract Dimension getDimension();
	
	public Object getValue() {
		Transformable subjectValue = subject.getTransformableValue();
		if( subjectValue!=null ) {
			if (this instanceof Width){
				edu.cmu.cs.stage3.alice.core.response.Print.outputtext= subjectValue.name.getStringValue()+"'s width is ";	
			} else if (this instanceof Height){
				edu.cmu.cs.stage3.alice.core.response.Print.outputtext= subjectValue.name.getStringValue()+"'s height is ";
			} else if (this instanceof Depth){
				edu.cmu.cs.stage3.alice.core.response.Print.outputtext= subjectValue.name.getStringValue()+"'s depth is ";
			} 
			return new Double( subjectValue.getSizeAlongDimension( getDimension() ) );
		} else {
			return null;
		}
	}
	
	public Class getValueClass() {
		return Number.class;
	}
}