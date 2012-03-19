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

public class MouseLookingBehavior extends InternalResponseBehavior implements java.awt.event.MouseListener, java.awt.event.MouseMotionListener {

	private RenderTarget renderTarget;

	private javax.vecmath.Vector3d turning = new javax.vecmath.Vector3d();

	private boolean mouseActive;

	private int lastX;
	private int lastY;

	public NumberProperty turningRate = new NumberProperty(this, "turningRate", new Double(.001));
	public TransformableProperty subject = new TransformableProperty(this, "subject", null);
	public BooleanProperty onlyAffectsYaw = new BooleanProperty(this, "onlyAffectsYaw", Boolean.TRUE);

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
				if (subject.get() == null) {
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

		// look

		subject.getTransformableValue().turnRightNow(Direction.FORWARD, turning.x);

		Transformable t = new Transformable();
		t.setPositionRightNow(subject.getTransformableValue().getPosition());
		t.setOrientationRightNow(subject.getTransformableValue().vehicle.getReferenceFrameValue().getOrientationAsQuaternion());

		subject.getTransformableValue().turnRightNow(Direction.RIGHT, turning.y, t);

		turning.x = 0;
		turning.y = 0;
		turning.z = 0;
	}

	// Implements MouseListener
	@Override
	public void mouseClicked(java.awt.event.MouseEvent e) {
	}
	@Override
	public void mouseEntered(java.awt.event.MouseEvent e) {
		lastX = e.getX();
		lastY = e.getY();
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
		lastX = e.getX();
		lastY = e.getY();
		if (mouseActive) {
			if (((Boolean) onlyAffectsYaw.get()).booleanValue()) {
				turning.y += x * turningRate.getNumberValue().doubleValue();
			} else {
				turning.y += x * turningRate.getNumberValue().doubleValue();
				turning.x += y * turningRate.getNumberValue().doubleValue();
			}
		}
	}

	@Override
	public void mouseMoved(java.awt.event.MouseEvent e) {
		int x = e.getX() - lastX;
		int y = e.getY() - lastY;
		if (lastX == -1) {
			x = 0;
		}
		if (lastY == -1) {
			y = 0;
		}
		lastX = e.getX();
		lastY = e.getY();
		if (mouseActive) {
			if (((Boolean) onlyAffectsYaw.get()).booleanValue()) {
				turning.y += x * turningRate.getNumberValue().doubleValue();
			} else {
				turning.y += x * turningRate.getNumberValue().doubleValue();
				turning.x += y * turningRate.getNumberValue().doubleValue();
			}
		}
	}

}