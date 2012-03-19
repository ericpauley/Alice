package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.property.ModelProperty;

public class DistanceBetween extends NumberQuestion {
	public final ModelProperty a = new ModelProperty(this, "a", null);
	public final ModelProperty b = new ModelProperty(this, "b", null);

	@Override
	public Object getValue() {
		return new Double(edu.cmu.cs.stage3.alice.core.Model.getDistanceBetween(a.getModelValue(), b.getModelValue()));
	}
}