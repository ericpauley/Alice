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

import edu.cmu.cs.stage3.alice.core.Direction;
import edu.cmu.cs.stage3.alice.core.Expression;
import edu.cmu.cs.stage3.alice.core.event.ExpressionEvent;
import edu.cmu.cs.stage3.alice.core.event.ExpressionListener;
import edu.cmu.cs.stage3.alice.core.property.DirectionProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;

public abstract class DirectionSpeedTransformResponse extends TransformResponse {
	public final DirectionProperty direction = new DirectionProperty(this, "direction", getDefaultDirection());
	public final NumberProperty speed = new NumberProperty(this, "speed", new Double(1));
	protected abstract Direction getDefaultDirection();
	protected abstract boolean acceptsDirection(Direction direction);

	@Override
	protected void propertyChanging(edu.cmu.cs.stage3.alice.core.Property property, Object value) {
		if (property == direction) {
			if (value instanceof Direction) {
				if (acceptsDirection((Direction) value)) {
					// pass
				} else {
					throw new RuntimeException(this + " does not accept direction " + value);
				}
			}
		} else {
			super.propertyChanging(property, value);
		}
	}
	public class RuntimeDirectionSpeedTransformResponse extends RuntimeTransformResponse implements ExpressionListener {
		private double m_speed = Double.NaN;
		private boolean m_isSpeedDirty = true;
		private Expression m_expression = null;
		protected double getSpeed() {
			if (m_isSpeedDirty) {
				m_speed = speed.doubleValue();
			}
			return m_speed;
		}
		@Override
		public void expressionChanged(ExpressionEvent expressionEvent) {
			m_isSpeedDirty = true;
		}

		@Override
		public void prologue(double t) {
			super.prologue(t);
			if (direction.getDirectionValue() == null) {
				throw new edu.cmu.cs.stage3.alice.core.SimulationPropertyException("direction value must not be null.", null, direction);
			}
			if (speed.getValue() == null) {
				throw new edu.cmu.cs.stage3.alice.core.SimulationPropertyException("speed value must not be null.", null, speed);
			}
			Object o = speed.get();
			if (o instanceof Expression) {
				m_expression = (Expression) o;
				m_expression.addExpressionListener(this);
			} else {
				m_expression = null;
			}
		}

		@Override
		public void epilogue(double t) {
			super.epilogue(t);
			if (m_expression != null) {
				m_expression.removeExpressionListener(this);
				m_expression = null;
			}
		}
	}
}
