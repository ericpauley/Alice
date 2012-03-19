package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.Question;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.ClassProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.property.TransformableProperty;

public class VariableNamed extends Question {
	public final TransformableProperty owner = new TransformableProperty(this, "owner", null);
	public final StringProperty variableName = new StringProperty(this, "variableName", "");
	public final ClassProperty valueClass = new ClassProperty(this, "valueClass", null);
	public final BooleanProperty ignoreCase = new BooleanProperty(this, "ignoreCase", Boolean.TRUE);

	@Override
	public Object getValue() {
		Transformable ownerValue = owner.getTransformableValue();
		String nameValue = variableName.getStringValue();
		if (ownerValue != null) {
			for (int i = 0; i < ownerValue.variables.size(); i++) {
				Variable variableI = (Variable) ownerValue.variables.get(i);
				if (variableI != null) {
					String nameI = variableI.name.getStringValue();
					if (nameI != null) {
						if (ignoreCase.booleanValue()) {
							if (nameI.equalsIgnoreCase(nameValue)) {
								return variableI.getValue();
							}
						} else {
							if (nameI.equals(nameValue)) {
								return variableI.getValue();
							}
						}
					}
				}
			}
		}
		return null;
	}

	@Override
	public Class getValueClass() {
		Class cls = valueClass.getClassValue();
		if (cls != null) {
			return cls;
		} else {
			return java.lang.Object.class;
		}
	}
}