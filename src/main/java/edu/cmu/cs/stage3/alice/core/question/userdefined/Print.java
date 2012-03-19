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

package edu.cmu.cs.stage3.alice.core.question.userdefined;

import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.ObjectProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;

public class Print extends Component {
	public final StringProperty text = new StringProperty(this, "text", "");
	public final ObjectProperty object = new ObjectProperty(this, "object", null, Object.class) {

		@Override
		protected boolean getValueOfExpression() {
			return true;
		}
	};
	public final BooleanProperty addNewLine = new BooleanProperty(this, "addNewLine", Boolean.TRUE);

	@Override
	public Object[] execute() {
		String output = text.getStringValue();
		Object o = object.get();
		if (o != null) {
			o = object.getValue();
			output += o;
		}
		if (Print.this.addNewLine.booleanValue()) {
			System.out.println(output);
		} else {
			System.out.print(output);
		}
		return null;
	}
}
