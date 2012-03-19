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

import edu.cmu.cs.stage3.alice.core.property.NumberProperty;

public abstract class BinaryNumberResultingInNumberQuestion extends NumberQuestion {
	public final NumberProperty a = new NumberProperty(this, "a", new Double(0));
	public final NumberProperty b = new NumberProperty(this, "b", new Double(0));
	protected abstract double getValue(double a, double b);

	@Override
	public Object getValue() {
		double aValue = a.doubleValue();
		double bValue = b.doubleValue();
		if (this instanceof edu.cmu.cs.stage3.alice.core.question.math.Min) {
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext = "minimum of " + aValue + " and " + bValue + " is ";
		} else if (this instanceof edu.cmu.cs.stage3.alice.core.question.math.Max) {
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext = "maximum of " + aValue + " and " + bValue + " is ";
		} else if (this instanceof edu.cmu.cs.stage3.alice.core.question.math.ATan2) {
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext = "arctan2 " + aValue + " " + bValue + " is ";
		} else if (this instanceof edu.cmu.cs.stage3.alice.core.question.math.Pow) {
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext = aValue + " raised to the " + bValue + " power is ";
		} else if (this instanceof edu.cmu.cs.stage3.alice.core.question.math.IEEERemainder) {
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext = "IEEERemainder of " + aValue + "/" + bValue + " is ";
		}
		return new Double(getValue(aValue, bValue));
	}
}