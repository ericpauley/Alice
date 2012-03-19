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

package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.property.VariableProperty;

public class Increment extends Animation {
	public final VariableProperty variable = new VariableProperty(this, "variable", null);

	@Override
	protected Number getDefaultDuration() {
		return new Double(0);
	}
	public class RuntimeIncrement extends RuntimeAnimation {
		private double m_amountAlreadyIncremented = 0;
		private void incrementValue(double amount) {
			Variable variableValue = variable.getVariableValue();
			Number number = (Number) variableValue.value.getValue();
			variableValue.value.set(new Double(number.doubleValue() + amount));
			m_amountAlreadyIncremented += amount;
		}

		@Override
		public void prologue(double t) {
			super.prologue(t);
			m_amountAlreadyIncremented = 0;
		}

		@Override
		public void update(double t) {
			super.update(t);
			incrementValue(getPortion(t) - m_amountAlreadyIncremented);
		}
		// todo?
		// public void epilogue( double t ) {
	}
}