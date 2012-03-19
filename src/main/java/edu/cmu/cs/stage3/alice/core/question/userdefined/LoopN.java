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

import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.VariableProperty;

public class LoopN extends Composite {
	public final NumberProperty count = new NumberProperty(this, "count", new Double(Double.POSITIVE_INFINITY));

	public final VariableProperty index = new VariableProperty(this, "index", null);

	public final NumberProperty start = new NumberProperty(this, "start", new Double(0));
	public final NumberProperty end = new NumberProperty(this, "end", new Double(Double.POSITIVE_INFINITY));
	public final NumberProperty increment = new NumberProperty(this, "increment", new Double(1));

	@Override
	protected void internalFindAccessibleExpressions(Class cls, java.util.Vector v) {
		internalAddExpressionIfAssignableTo((edu.cmu.cs.stage3.alice.core.Expression) index.get(), cls, v);
		super.internalFindAccessibleExpressions(cls, v);
	}

	@Override
	public Object[] execute() {
		if (start.get() == null) {
			throw new NullPointerException();
		}
		if (end.get() == null) {
			throw new NullPointerException();
		}
		if (index.get() == null) {
			throw new NullPointerException();
		}
		edu.cmu.cs.stage3.alice.core.Variable indexVariable = index.getVariableValue();
		double lcv = start.doubleValue();
		while (lcv < end.doubleValue()) {
			indexVariable.value.set(new Double(lcv));
			Object[] value = super.execute();
			lcv += increment.doubleValue(1);
			if (value != null) {
				return value;
			}
		}
		return null;
	}
}
