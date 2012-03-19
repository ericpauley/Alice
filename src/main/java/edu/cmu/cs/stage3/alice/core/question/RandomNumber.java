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
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;

public class RandomNumber extends Question {
	public final NumberProperty minimum = new NumberProperty(this, "minimum", new Double(0));
	public final NumberProperty maximum = new NumberProperty(this, "maximum", new Double(1));
	public final BooleanProperty integerOnly = new BooleanProperty(this, "integerOnly", Boolean.FALSE);

	@Override
	public Object getValue() {
		double minimumValue = minimum.doubleValue();
		double maximumValue = maximum.doubleValue();
		if (minimumValue > maximumValue) {
			double temp = minimumValue;
			minimumValue = maximumValue;
			maximumValue = temp;
		}
		double r = Math.random();
		double value = r * (maximumValue - minimumValue) + minimumValue;
		if (integerOnly.booleanValue()) {
			// todo: handle [min, max] as opposed to [ min, max )
			return new Double((int) value);// return new Integer( (int)value );
		} else {
			return new Double(value);
		}
	}

	@Override
	public Class getValueClass() {
		if (integerOnly.booleanValue()) {
			return Integer.class;
		} else {
			return Number.class;
		}
	}
}