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

import edu.cmu.cs.stage3.alice.core.ReferenceFrame;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.ReferenceFrameProperty;
import edu.cmu.cs.stage3.alice.core.property.TransformableProperty;
import edu.cmu.cs.stage3.alice.core.property.Vector3Property;

public class BillboardBehavior extends InternalResponseBehavior implements edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationListener {
	public final TransformableProperty subject = new TransformableProperty(this, "subject", null);
	public final TransformableProperty target = new TransformableProperty(this, "target", null);
	public final Vector3Property offset = new Vector3Property(this, "offset", null);
	public final Vector3Property upGuide = new Vector3Property(this, "upGuide", null);
	public final ReferenceFrameProperty asSeenBy = new ReferenceFrameProperty(this, "asSeenBy", null);
	public final BooleanProperty onlyAffectYaw = new BooleanProperty(this, "onlyAffectYaw", Boolean.FALSE);

	private Transformable m_subject;
	private Transformable m_target;
	private javax.vecmath.Vector3d m_offset;
	private javax.vecmath.Vector3d m_upGuide;
	private ReferenceFrame m_asSeenBy;
	private boolean m_onlyAffectYaw;

	private boolean m_isDirty = false;

	@Override
	protected void propertyChanging(edu.cmu.cs.stage3.alice.core.Property property, Object value) {
		if (property == target) {
			if (value == subject.get()) {
				throw new IllegalArgumentException("billboard cannot point at self");
			}
		} else {
			super.propertyChanging(property, value);
		}
	}

	@Override
	protected void propertyChanged(edu.cmu.cs.stage3.alice.core.Property property, Object value) {
		if (property == target) {
			// todo
		} else {
			super.propertyChanged(property, value);
		}
	}

	@Override
	public void absoluteTransformationChanged(edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationEvent absoluteTransformationEvent) {
		m_isDirty = true;
	}

	@Override
	public void internalSchedule(double time, double dt) {
		if (m_isDirty) {
			if (m_subject != null && m_target != null) {
				m_subject.pointAtRightNow(m_target, m_offset, m_upGuide, m_asSeenBy, m_onlyAffectYaw);
			}
			m_isDirty = false;
		}
	}

	@Override
	public void started(World world, double time) {
		super.started(world, time);
		m_subject = subject.getTransformableValue();
		m_target = target.getTransformableValue();
		m_offset = offset.getVector3Value();
		m_upGuide = upGuide.getVector3Value();
		m_asSeenBy = asSeenBy.getReferenceFrameValue();
		m_onlyAffectYaw = onlyAffectYaw.booleanValue();
		if (m_subject != null) {
			m_subject.addAbsoluteTransformationListener(this);
		}
		if (m_target != null) {
			m_target.addAbsoluteTransformationListener(this);
		}
		m_isDirty = false;
	}

	@Override
	public void stopped(World world, double time) {
		if (m_subject != null) {
			m_subject.removeAbsoluteTransformationListener(this);
		}
		if (m_target != null) {
			m_target.removeAbsoluteTransformationListener(this);
		}
		super.stopped(world, time);
	}
}