package edu.cmu.cs.stage3.alice.core.question.visualization.list;

import edu.cmu.cs.stage3.alice.core.property.ListOfModelsVisualizationProperty;
import edu.cmu.cs.stage3.alice.core.property.ModelProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;

public class FirstIndexOfItem extends edu.cmu.cs.stage3.alice.core.question.NumberQuestion {
	public final ListOfModelsVisualizationProperty subject = new ListOfModelsVisualizationProperty(this, "subject", null);
	public final ModelProperty item = new ModelProperty(this, "item", null);
	public final NumberProperty startFromIndex = new NumberProperty(this, "startFromIndex", null);

	@Override
	public Object getValue() {
		edu.cmu.cs.stage3.alice.core.visualization.ListOfModelsVisualization listOfModelsVisualizationValue = subject.getListOfModelsVisualizationValue();
		if (listOfModelsVisualizationValue != null) {
			return new Integer(listOfModelsVisualizationValue.indexOf(item.getModelValue(), startFromIndex.intValue(-1)));
		} else {
			return null;
		}
	}
}