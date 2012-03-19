package edu.cmu.cs.stage3.alice.core.question.visualization.array;

import edu.cmu.cs.stage3.alice.core.property.ArrayOfModelsVisualizationProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;

public class ItemAtIndex extends edu.cmu.cs.stage3.alice.core.question.ModelQuestion {
	public final ArrayOfModelsVisualizationProperty subject = new ArrayOfModelsVisualizationProperty(this, "subject", null);
	public final NumberProperty index = new NumberProperty(this, "index", new Integer(-1));

	@Override
	public Object getValue() {
		edu.cmu.cs.stage3.alice.core.visualization.ArrayOfModelsVisualization arrayOfModelsVisualizationValue = subject.getArrayOfModelsVisualizationValue();
		if (arrayOfModelsVisualizationValue != null) {
			return arrayOfModelsVisualizationValue.get(index.intValue());
		} else {
			return null;
		}
	}
}