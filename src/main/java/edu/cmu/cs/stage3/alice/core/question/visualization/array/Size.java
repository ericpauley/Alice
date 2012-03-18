package edu.cmu.cs.stage3.alice.core.question.visualization.array;

import edu.cmu.cs.stage3.alice.core.property.ArrayOfModelsVisualizationProperty;

public class Size extends edu.cmu.cs.stage3.alice.core.question.NumberQuestion {
	public final ArrayOfModelsVisualizationProperty subject = new ArrayOfModelsVisualizationProperty( this, "subject", null );
	
	public Object getValue() {
		edu.cmu.cs.stage3.alice.core.visualization.ArrayOfModelsVisualization arrayOfModelsVisualizationValue = subject.getArrayOfModelsVisualizationValue();
		if( arrayOfModelsVisualizationValue!=null ) {
            return new Integer( arrayOfModelsVisualizationValue.size() );
		} else {
			return null;
		}
	}
}
