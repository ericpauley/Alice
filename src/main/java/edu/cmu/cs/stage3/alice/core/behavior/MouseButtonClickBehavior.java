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

package edu.cmu.cs.stage3.alice.core.behavior;

import edu.cmu.cs.stage3.alice.core.Expression;
import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.alice.core.Question;
import edu.cmu.cs.stage3.alice.core.RenderTarget;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.IntegerProperty;
import edu.cmu.cs.stage3.alice.core.property.TransformableProperty;
import edu.cmu.cs.stage3.alice.core.question.PickQuestion;

public class MouseButtonClickBehavior extends TriggerBehavior implements java.awt.event.MouseListener {
	private static Class[] s_supportedCoercionClasses = {MouseButtonIsPressedBehavior.class};

	@Override
	public Class[] getSupportedCoercionClasses() {
		return s_supportedCoercionClasses;
	}
	public final IntegerProperty requiredModifierMask = new IntegerProperty(this, "requiredModifierMask", new Integer(0));
	public final IntegerProperty excludedModifierMask = new IntegerProperty(this, "excludedModifierMask", new Integer(0));
	public final ElementArrayProperty renderTargets = new ElementArrayProperty(this, "renderTargets", null, edu.cmu.cs.stage3.alice.core.RenderTarget[].class);

	// should this be a ModelProperty?
	public final TransformableProperty onWhat = new TransformableProperty(this, "onWhat", null);

	private edu.cmu.cs.stage3.alice.core.RenderTarget[] m_renderTargets = null;
	private java.awt.event.MouseEvent m_pressedEvent = null;
	public long m_clickTimeThreshold = 300;
	public int m_clickDistanceThresholdSquared = 100;

	@Override
	public void manufactureAnyNecessaryDetails() {
		if (details.size() == 2) {
			Question what = new PickQuestion();
			what.name.set("what");
			what.setParent(this);
			details.add(what);
		}
		for (int i = 0; i < details.size(); i++) {
			Object o = details.get(i);
			if (o instanceof PickQuestion) {
				((PickQuestion) o).name.set("what");
			}
		}
	}

	@Override
	public void manufactureDetails() {
		super.manufactureDetails();

		Variable x = new Variable();
		x.name.set("x");
		x.setParent(this);
		x.valueClass.set(Number.class);
		details.add(x);

		Variable y = new Variable();
		y.name.set("y");
		y.setParent(this);
		y.valueClass.set(Number.class);
		details.add(y);

		manufactureAnyNecessaryDetails();
	}
	private void updateDetails(java.awt.event.MouseEvent mouseEvent) {
		for (int i = 0; i < details.size(); i++) {
			Expression detail = (Expression) details.get(i);
			if (detail.name.getStringValue().equals("x")) {
				((Variable) detail).value.set(new Double(mouseEvent.getX()));
			} else if (detail.name.getStringValue().equals("y")) {
				((Variable) detail).value.set(new Double(mouseEvent.getY()));
			} else if (detail.name.getStringValue().equals("what")) {
				((PickQuestion) detail).setMouseEvent(mouseEvent);
			}
		}
	}
	private boolean checkModifierMask(java.awt.event.InputEvent e) {
		int modifiers = e.getModifiers();
		Integer requiredModifierMaskValue = (Integer) requiredModifierMask.getValue();
		Integer excludedModifierMaskValue = (Integer) excludedModifierMask.getValue();
		int required = 0;
		if (requiredModifierMaskValue != null) {
			required = requiredModifierMaskValue.intValue();
		}
		int excluded = 0;
		if (excludedModifierMaskValue != null) {
			excluded = excludedModifierMaskValue.intValue();
		}
		return (modifiers & required) == required && (modifiers & excluded) == 0;
	}

	@Override
	public void mouseClicked(java.awt.event.MouseEvent mouseEvent) {
	}
	@Override
	public void mouseEntered(java.awt.event.MouseEvent mouseEvent) {
	}
	@Override
	public void mouseExited(java.awt.event.MouseEvent mouseEvent) {
	}
	@Override
	public void mousePressed(java.awt.event.MouseEvent mouseEvent) {
		m_pressedEvent = mouseEvent;
	}

	@Override
	public void mouseReleased(java.awt.event.MouseEvent mouseEvent) {
		int dx = mouseEvent.getX() - m_pressedEvent.getX();
		int dy = mouseEvent.getY() - m_pressedEvent.getY();
		long dt = mouseEvent.getWhen() - m_pressedEvent.getWhen();
		if (dt < m_clickTimeThreshold) {
			if (dx * dx + dy * dy < m_clickDistanceThresholdSquared) {
				if (isEnabled.booleanValue()) {
					if (checkModifierMask(mouseEvent)) {
						Transformable onWhatValue = onWhat.getTransformableValue();
						boolean success;
						if (onWhatValue != null) {
							edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo pickInfo = RenderTarget.pick(mouseEvent);
							if (pickInfo != null && pickInfo.getCount() > 0) {
								Model model = (Model) pickInfo.getVisualAt(0).getBonus();
								success = onWhatValue == model || onWhatValue.isAncestorOf(model);
							} else {
								success = false;
							}
						} else {
							success = true;
						}
						if (success) {
							updateDetails(mouseEvent);
							trigger(mouseEvent.getWhen() * 0.001);
						}
					}
				}
			}
		}
	}

	@Override
	protected void started(World world, double time) {
		super.started(world, time);
		m_renderTargets = (RenderTarget[]) renderTargets.get();
		if (m_renderTargets == null) {
			m_renderTargets = (RenderTarget[]) world.getDescendants(RenderTarget.class);
		}
		for (RenderTarget m_renderTarget : m_renderTargets) {
			m_renderTarget.addMouseListener(this);
		}
	}

	@Override
	protected void stopped(World world, double time) {
		super.stopped(world, time);
		for (RenderTarget m_renderTarget : m_renderTargets) {
			m_renderTarget.removeMouseListener(this);
		}
		m_renderTargets = null;
	}
}