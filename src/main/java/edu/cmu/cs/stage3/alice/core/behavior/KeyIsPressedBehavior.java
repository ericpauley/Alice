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

import edu.cmu.cs.stage3.alice.core.RenderTarget;
import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.IntegerProperty;

public class KeyIsPressedBehavior extends AbstractConditionalBehavior implements java.awt.event.KeyListener {
	private static Class[] s_supportedCoercionClasses = {KeyClickBehavior.class};
	private int m_keyCode = -1;

	@Override
	public Class[] getSupportedCoercionClasses() {
		return s_supportedCoercionClasses;
	}
	public final IntegerProperty keyCode = new IntegerProperty(this, "keyCode", null);
	public final ElementArrayProperty renderTargets = new ElementArrayProperty(this, "renderTargets", null, edu.cmu.cs.stage3.alice.core.RenderTarget[].class);

	private edu.cmu.cs.stage3.alice.core.RenderTarget[] m_renderTargets = null;

	@Override
	public void manufactureDetails() {
		super.manufactureDetails();
		edu.cmu.cs.stage3.alice.core.Variable keyChar = new edu.cmu.cs.stage3.alice.core.Variable();
		keyChar.name.set("keyChar");
		keyChar.setParent(this);
		keyChar.valueClass.set(Character.class);
		details.add(keyChar);
	}
	private void updateDetails(java.awt.event.KeyEvent keyEvent) {
		for (int i = 0; i < details.size(); i++) {
			Variable detail = (Variable) details.get(i);
			if (detail.name.getStringValue().equals("keyChar")) {
				detail.value.set(new Character(keyEvent.getKeyChar()));
			}
		}
	}
	private boolean checkKeyCode(int actualValue) {
		int requiredValue = keyCode.intValue(actualValue);
		return actualValue == requiredValue;
	}
	@Override
	public void keyPressed(java.awt.event.KeyEvent keyEvent) {
		if (m_keyCode == -1) {
			int keyCode = keyEvent.getKeyCode();
			if (checkKeyCode(keyCode)) {
				m_keyCode = keyCode;
				updateDetails(keyEvent);
				set(true);
			}
		}
	}
	@Override
	public void keyReleased(java.awt.event.KeyEvent keyEvent) {
		if (m_keyCode != -1) {
			int keyCode = keyEvent.getKeyCode();
			if (keyCode == m_keyCode) {
				updateDetails(keyEvent);
				set(false);
				m_keyCode = -1;
			}
		}
	}
	@Override
	public void keyTyped(java.awt.event.KeyEvent keyEvent) {
	}

	@Override
	protected void started(edu.cmu.cs.stage3.alice.core.World world, double time) {
		super.started(world, time);
		m_renderTargets = (edu.cmu.cs.stage3.alice.core.RenderTarget[]) renderTargets.get();
		if (m_renderTargets == null) {
			m_renderTargets = (edu.cmu.cs.stage3.alice.core.RenderTarget[]) world.getDescendants(edu.cmu.cs.stage3.alice.core.RenderTarget.class);
		}
		for (RenderTarget m_renderTarget : m_renderTargets) {
			m_renderTarget.addKeyListener(this);
		}
		m_keyCode = -1;
	}

	@Override
	protected void stopped(edu.cmu.cs.stage3.alice.core.World world, double time) {
		super.stopped(world, time);
		for (RenderTarget m_renderTarget : m_renderTargets) {
			m_renderTarget.removeKeyListener(this);
		}
		m_renderTargets = null;
	}
}