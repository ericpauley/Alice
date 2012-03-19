package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.Transformable;

public class IsObjectInView extends SubjectObjectQuestion {
	private boolean isObjectInView(Transformable subjectValue, Transformable objectValue) {
		return true;
	}

	@Override
	public Class getValueClass() {
		return Boolean.class;
	}

	@Override
	protected Object getValue(Transformable subjectValue, Transformable objectValue) {
		if (isObjectInView(subjectValue, objectValue)) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}
}