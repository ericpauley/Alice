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

//todo: deprecate?
public class ExpressionIsAccessibleFromCriterion extends edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion {
	private edu.cmu.cs.stage3.alice.core.Element m_from;
	public ExpressionIsAccessibleFromCriterion( edu.cmu.cs.stage3.alice.core.Element from ) {
		super( edu.cmu.cs.stage3.alice.core.Expression.class );
		m_from = from;
	}
	
	public boolean accept( Object o ) {
		if( super.accept( o ) ) {
            //todo
            /*
			edu.cmu.cs.stage3.alice.core.Expression expression = (edu.cmu.cs.stage3.alice.core.Expression)o;
			edu.cmu.cs.stage3.alice.core.Element parentValue = expression.getParent();
			if( parentValue instanceof edu.cmu.cs.stage3.alice.core.group.ExpressionGroup ) {
				edu.cmu.cs.stage3.alice.core.group.ExpressionGroup expressionGroup = (edu.cmu.cs.stage3.alice.core.group.ExpressionGroup)parentValue;
				if( expressionGroup.isPublic.booleanValue() ) {
					return true;
				} else {
					return m_from.isDescendantOf( expressionGroup.getParent() );
				}
			} else {
				//todo? warning
				return false;
			}
            */
            return false;
		} else {
			return false;
		}
	}
}