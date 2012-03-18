package edu.cmu.cs.stage3.alice.core.question;

public class Pose extends SubjectQuestion {
	
	public Class getValueClass() {
		return edu.cmu.cs.stage3.alice.core.Pose.class;
	}
	
	protected Object getValue( edu.cmu.cs.stage3.alice.core.Transformable subjectValue ) {
        return edu.cmu.cs.stage3.alice.core.Pose.manufacturePose( subjectValue, subjectValue );
    }
}