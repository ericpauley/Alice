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

package edu.cmu.cs.stage3.alice.core.criterion;

public class ExpressionIsAssignableToCriterion extends edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion {
	private Class m_valueClass;
	protected ExpressionIsAssignableToCriterion(Class expressionClass, Class valueClass) {
		super(expressionClass);
		m_valueClass = valueClass;
	}
	public ExpressionIsAssignableToCriterion(Class cls) {
		this(edu.cmu.cs.stage3.alice.core.Expression.class, cls);
	}

	@Override
	public boolean accept(Object o) {
		if (super.accept(o)) {
			Class valueClass = ((edu.cmu.cs.stage3.alice.core.Expression) o).getValueClass();
			if (valueClass != null) {
				return m_valueClass.isAssignableFrom(valueClass);
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}