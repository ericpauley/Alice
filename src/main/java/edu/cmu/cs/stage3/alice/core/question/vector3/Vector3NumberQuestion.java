package edu.cmu.cs.stage3.alice.core.question.vector3;

import edu.cmu.cs.stage3.alice.core.property.Vector3Property;

public abstract class Vector3NumberQuestion extends edu.cmu.cs.stage3.alice.core.question.NumberQuestion {
	public final Vector3Property vector3 = new Vector3Property(this, "vector3", new javax.vecmath.Vector3d());
	protected abstract double getValue(javax.vecmath.Vector3d vector3);

	@Override
	public Object getValue() {
		javax.vecmath.Vector3d value = vector3.getVector3Value();
		if (value != null) {
			return new Double(getValue(value));
		} else {
			return null;
		}
	}
}