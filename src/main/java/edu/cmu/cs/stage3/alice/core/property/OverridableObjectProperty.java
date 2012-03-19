package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;

public class OverridableObjectProperty extends ObjectProperty {
	private Class m_overrideValueClass = null;
	public OverridableObjectProperty(Element owner, String name, Object defaultValue) {
		super(owner, name, defaultValue, Object.class);
	}
	public void setOverrideValueClass(Class overrideValueClass) {
		m_overrideValueClass = overrideValueClass;
	}

	@Override
	public Class getValueClass() {
		if (m_overrideValueClass != null) {
			return m_overrideValueClass;
		} else {
			return super.getValueClass();
		}
	}
}
