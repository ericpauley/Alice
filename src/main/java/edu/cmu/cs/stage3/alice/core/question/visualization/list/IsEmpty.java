package edu.cmu.cs.stage3.alice.core.question.visualization.list;

import edu.cmu.cs.stage3.alice.core.property.ListOfModelsVisualizationProperty;

public class IsEmpty extends edu.cmu.cs.stage3.alice.core.question.BooleanQuestion {
	public final ListOfModelsVisualizationProperty subject = new ListOfModelsVisualizationProperty(this, "subject", null);

	@Override
	public Object getValue() {
		edu.cmu.cs.stage3.alice.core.visualization.ListOfModelsVisualization listOfModelsVisualizationValue = subject.getListOfModelsVisualizationValue();
		if (listOfModelsVisualizationValue != null) {
			if (listOfModelsVisualizationValue.isEmpty()) {
				return Boolean.TRUE;
			} else {
				return Boolean.FALSE;
			}
		} else {
			return null;
		}
	}
}
