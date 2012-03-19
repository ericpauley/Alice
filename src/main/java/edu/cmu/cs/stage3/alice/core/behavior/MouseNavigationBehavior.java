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

import edu.cmu.cs.stage3.alice.core.Camera;
import edu.cmu.cs.stage3.alice.core.Direction;
import edu.cmu.cs.stage3.alice.core.RenderTarget;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.TransformableProperty;

/**
 * Title: Description: Copyright: Copyright (c) 2001 Company: Stage3
 * 
 * @author
 * @version 1.0
 */
public class MouseNavigationBehavior extends InternalResponseBehavior implements java.awt.event.MouseListener, java.awt.event.MouseMotionListener {

	private RenderTarget renderTarget;

	private javax.vecmath.Vector3d turning = new javax.vecmath.Vector3d(0, 0, 0);
	private double movement = 0;

	private boolean mouseActive;

	private int lastX;
	private int lastY;

	public NumberProperty speed = new NumberProperty(this, "speed", new Double(.1));
	public NumberProperty turningRate = new NumberProperty(this, "turningRate", new Double(.001));
	public TransformableProperty subject = new edu.cmu.cs.stage3.alice.core.property.TransformableProperty(this, "subject", null);
	public BooleanProperty looking = new BooleanProperty(this, "looking", Boolean.FALSE);
	public BooleanProperty stayOnGround = new BooleanProperty(this, "stayOnGround", Boolean.TRUE);
	public BooleanProperty positionRelative = new BooleanProperty(this, "positionRelative", Boolean.TRUE);

	protected void disable() {
		renderTarget.removeMouseListener(this);
		renderTarget.removeMouseMotionListener(this);
		mouseActive = false;
	}

	protected void enable() {
		renderTarget.addMouseListener(this);
		renderTarget.addMouseMotionListener(this);
		mouseActive = false;
	}

	@Override
	public void started(World world, double time) {
		super.started(world, time);
		if (isEnabled.booleanValue()) {
			RenderTarget[] rts = (RenderTarget[]) world.getDescendants(RenderTarget.class);
			if (rts.length > 0) {
				renderTarget = rts[0];
				if (subject.getTransformableValue() == null) {
					Camera[] cameras = renderTarget.getCameras();
					if (cameras.length > 0) {
						subject.set(cameras[0]);
					}
				}
			}

			enable();
		}
	}

	@Override
	public void stopped(World world, double time) {
		super.stopped(world, time);
		if (isEnabled.booleanValue()) {
			disable();
		}
	}

	@Override
	public void internalSchedule(double time, double dt) {

		if (!mouseActive) {
			return;
		}

		// double dt = time;

		if (!positionRelative.getBooleanValue().booleanValue()) {
			dt = 1;
		}

		// look

		subject.getTransformableValue().turnRightNow(Direction.FORWARD, turning.x * dt);

		Transformable t = new Transformable();
		t.setPositionRightNow(subject.getTransformableValue().getPosition());
		t.setOrientationRightNow(subject.getTransformableValue().vehicle.getReferenceFrameValue().getOrientationAsQuaternion());

		subject.getTransformableValue().turnRightNow(Direction.RIGHT, turning.y * dt, t);

		// move

		double yPos = subject.getTransformableValue().getPosition().y;
		subject.getTransformableValue().moveRightNow(Direction.FORWARD, dt * movement);
		if (stayOnGround.getBooleanValue().booleanValue()) {
			javax.vecmath.Vector3d pos = subject.getTransformableValue().getPosition();
			pos.y = yPos;
			subject.getTransformableValue().setPositionRightNow(pos);
		}

		if (!positionRelative.getBooleanValue().booleanValue()) {
			turning.x = 0;
			turning.y = 0;
			turning.z = 0;
			movement = 0;
		}
	}

	// Implements MouseListener
	@Override
	public void mouseClicked(java.awt.event.MouseEvent e) {
	}
	@Override
	public void mouseEntered(java.awt.event.MouseEvent e) {
		if (!positionRelative.getBooleanValue().booleanValue()) {
			lastX = e.getX();
			lastY = e.getY();
		}

	}
	@Override
	public void mouseExited(java.awt.event.MouseEvent e) {
	}

	@Override
	public void mousePressed(java.awt.event.MouseEvent e) {
		lastX = e.getX();
		lastY = e.getY();
		mouseActive = true;
	}

	@Override
	public void mouseReleased(java.awt.event.MouseEvent e) {
		mouseActive = false;
		turning.x = 0;
		turning.y = 0;
		turning.z = 0;
		movement = 0;
	}

	// Implements MouseMotionListener
	@Override
	public void mouseDragged(java.awt.event.MouseEvent e) {
		int x = e.getX() - lastX;
		int y = e.getY() - lastY;
		if (lastX == -1) {
			x = 0;
		}
		if (lastY == -1) {
			y = 0;
		}
		if (!positionRelative.getBooleanValue().booleanValue()) {
			lastX = e.getX();
			lastY = e.getY();
		}
		if (mouseActive) {
			turning.y = (positionRelative.getBooleanValue().booleanValue() ? 0 : turning.y) + x * turningRate.getNumberValue().doubleValue();
			if (((Boolean) looking.get()).booleanValue()) {
				turning.x = (positionRelative.getBooleanValue().booleanValue() ? 0 : turning.x) + y * turningRate.getNumberValue().doubleValue();
			} else {
				movement = (positionRelative.getBooleanValue().booleanValue() ? 0 : movement) - y * speed.getNumberValue().doubleValue();
			}
		}
	}

	@Override
	public void mouseMoved(java.awt.event.MouseEvent e) {
		int x = e.getX() - lastX;
		int y = e.getY() - lastY;
		if (!positionRelative.getBooleanValue().booleanValue()) {
			lastX = e.getX();
			lastY = e.getY();
		}
		if (!positionRelative.getBooleanValue().booleanValue()) {
			lastX = e.getX();
			lastY = e.getY();
		}
		if (mouseActive) {
			turning.y = (positionRelative.getBooleanValue().booleanValue() ? 0 : turning.y) + x * turningRate.getNumberValue().doubleValue();
			if (((Boolean) looking.get()).booleanValue()) {
				turning.x = (positionRelative.getBooleanValue().booleanValue() ? 0 : turning.x) + y * turningRate.getNumberValue().doubleValue();
			} else {
				movement = (positionRelative.getBooleanValue().booleanValue() ? 0 : movement) - y * speed.getNumberValue().doubleValue();
			}
		}
	}

}