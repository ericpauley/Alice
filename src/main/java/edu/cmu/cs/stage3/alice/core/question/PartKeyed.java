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

import edu.cmu.cs.stage3.alice.core.Question;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.property.TransformableProperty;

public class PartKeyed extends Question {
	public final TransformableProperty owner = new TransformableProperty( this, "owner", null );
	public final StringProperty key = new StringProperty( this, "key", "" );
	public final BooleanProperty ignoreCase = new BooleanProperty( this, "ignoreCase", Boolean.TRUE );
	
	public Object getValue() {
		edu.cmu.cs.stage3.alice.core.Transformable parentValue = owner.getTransformableValue();
		String keyValue = key.getStringValue();
		if( ignoreCase.booleanValue() ) {
			return parentValue.getPartKeyedIgnoreCase( keyValue );
		} else {
			return parentValue.getPartKeyed( keyValue );
		}
	}
	
	public Class getValueClass() {
		return edu.cmu.cs.stage3.alice.core.Model.class;
	}
}