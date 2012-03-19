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

public abstract class UnaryNumberResultingInNumberQuestion extends NumberQuestion {
	public final NumberProperty a = new NumberProperty(this, "a", new Double(0));
	protected abstract double getValue(double a);

	@Override
	public Object getValue() {
		double aValue = a.doubleValue(0.0);
		if (this instanceof edu.cmu.cs.stage3.alice.core.question.math.Abs) {
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext = "absolute value of " + aValue + " is ";
		} else if (this instanceof edu.cmu.cs.stage3.alice.core.question.math.Sqrt) {
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext = "square root of " + aValue + " is ";
		} else if (this instanceof edu.cmu.cs.stage3.alice.core.question.math.Floor) {
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext = "floor " + aValue + " is ";
		} else if (this instanceof edu.cmu.cs.stage3.alice.core.question.math.Ceil) {
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext = "ceiling " + aValue + " is ";
		} else if (this instanceof edu.cmu.cs.stage3.alice.core.question.math.Sin) {
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext = "sin " + aValue + " is ";
		} else if (this instanceof edu.cmu.cs.stage3.alice.core.question.math.Cos) {
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext = "cos " + aValue + " is ";
		} else if (this instanceof edu.cmu.cs.stage3.alice.core.question.math.Tan) {
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext = "tan " + aValue + " is ";
		} else if (this instanceof edu.cmu.cs.stage3.alice.core.question.math.ACos) {
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext = "arccos " + aValue + " is ";
		} else if (this instanceof edu.cmu.cs.stage3.alice.core.question.math.ASin) {
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext = "arcsin " + aValue + " is ";
		} else if (this instanceof edu.cmu.cs.stage3.alice.core.question.math.ATan) {
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext = "arctan " + aValue + " is ";
		} else if (this instanceof edu.cmu.cs.stage3.alice.core.question.math.Log) {
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext = "natural log of " + aValue + " is ";
		} else if (this instanceof edu.cmu.cs.stage3.alice.core.question.math.Exp) {
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext = "e raised to the " + aValue + " power is ";
		} else if (this instanceof edu.cmu.cs.stage3.alice.core.question.math.Round) {
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext = "round " + aValue + " is ";
		} else if (this instanceof edu.cmu.cs.stage3.alice.core.question.math.ToRadians) {
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext = aValue + " converted from radians to degrees is ";
		} else if (this instanceof edu.cmu.cs.stage3.alice.core.question.math.ToDegrees) {
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext = aValue + " converted from degrees to radians is ";
		}
		if (this instanceof edu.cmu.cs.stage3.alice.core.question.math.Int) {
			return String.valueOf((int) getValue(aValue));
		}

		return new Double(getValue(aValue));
	}
}