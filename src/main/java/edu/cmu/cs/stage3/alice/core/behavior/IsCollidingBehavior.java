package edu.cmu.cs.stage3.alice.core.behavior;

import edu.cmu.cs.stage3.alice.core.Collection;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.property.CollectionProperty;

public class IsCollidingBehavior extends AbstractConditionalBehavior {
	private static Class[] s_supportedCoercionClasses = {CollisionBehavior.class};

	@Override
	public Class[] getSupportedCoercionClasses() {
		return s_supportedCoercionClasses;
	}

	public final CollectionProperty a = new CollectionProperty(this, "a", null);
	public final CollectionProperty b = new CollectionProperty(this, "b", null);

	private World m_world = null;
	private java.util.Vector m_a = new java.util.Vector();
	private java.util.Vector m_b = new java.util.Vector();

	@Override
	protected void started(World world, double time) {
		super.started(world, time);
		m_world = world;

		m_a.clear();
		Collection aCollection = a.getCollectionValue();
		for (int i = 0; i < aCollection.values.size(); i++) {
			m_a.addElement(aCollection.values.get(i));
		}

		m_b.clear();
		Collection bCollection = b.getCollectionValue();
		for (int i = 0; i < bCollection.values.size(); i++) {
			m_b.addElement(bCollection.values.get(i));
		}

		for (int i = 0; i < m_a.size(); i++) {
			m_world.addCollisionManagementFor((Transformable) m_a.elementAt(i));
		}
		for (int i = 0; i < m_b.size(); i++) {
			m_world.addCollisionManagementFor((Transformable) m_b.elementAt(i));
		}
	}

	@Override
	protected void stopped(World world, double time) {
		super.stopped(world, time);
		if (m_world != world) {
			throw new Error();
		}
		for (int i = 0; i < m_a.size(); i++) {
			m_world.removeCollisionManagementFor((Transformable) m_a.elementAt(i));
		}
		for (int i = 0; i < m_b.size(); i++) {
			m_world.removeCollisionManagementFor((Transformable) m_b.elementAt(i));
		}
		m_world = null;
	}
}
