package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.scenegraph.Visual;

public class IsCollidingWith extends SubjectObjectQuestion {
	private edu.cmu.cs.stage3.alice.core.World m_world = null;

	@Override
	public Class getValueClass() {
		return Boolean.class;
	}

	@Override
	protected Object getValue(edu.cmu.cs.stage3.alice.core.Transformable subjectValue, edu.cmu.cs.stage3.alice.core.Transformable objectValue) {
		edu.cmu.cs.stage3.alice.core.World world = subjectValue.getWorld();
		edu.cmu.cs.stage3.alice.scenegraph.Visual[] subjectSGVisuals = subjectValue.getAllSceneGraphVisuals();
		edu.cmu.cs.stage3.alice.scenegraph.Visual[] objectSGVisuals = subjectValue.getAllSceneGraphVisuals();
		edu.cmu.cs.stage3.alice.scenegraph.Visual[][] collisions = world.getCollisions();
		for (Visual[] pair : collisions) {
			Object a = ((edu.cmu.cs.stage3.alice.core.Model) pair[0].getBonus()).getSandbox();
			Object b = ((edu.cmu.cs.stage3.alice.core.Model) pair[1].getBonus()).getSandbox();
			if (a == subjectValue) {
				if (b == objectValue) {
					return Boolean.TRUE;
				}
			} else if (b == subjectValue) {
				if (a == objectValue) {
					return Boolean.TRUE;
				}
			}
		}
		return Boolean.FALSE;
	}

	@Override
	protected void started(edu.cmu.cs.stage3.alice.core.World world, double time) {
		super.started(world, time);
		m_world = world;
		m_world.addCollisionManagementFor(subject.getTransformableValue());
		m_world.addCollisionManagementFor(object.getTransformableValue());
	}

	@Override
	protected void stopped(edu.cmu.cs.stage3.alice.core.World world, double time) {
		super.stopped(world, time);
		if (m_world != world) {
			throw new Error();
		}
		m_world.removeCollisionManagementFor(subject.getTransformableValue());
		m_world.removeCollisionManagementFor(object.getTransformableValue());
		m_world = null;
	}
}