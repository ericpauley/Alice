package edu.cmu.cs.stage3.alice.core.question.visualization.list;

import edu.cmu.cs.stage3.alice.core.property.ListOfModelsVisualizationProperty;

public class Size extends edu.cmu.cs.stage3.alice.core.question.NumberQuestion {
	public final ListOfModelsVisualizationProperty subject = new ListOfModelsVisualizationProperty( this, "subject", null );
	
	public Object getValue() {
		edu.cmu.cs.stage3.alice.core.visualization.ListOfModelsVisualization listOfModelsVisualizationValue = subject.getListOfModelsVisualizationValue();
		if( listOfModelsVisualizationValue!=null ) {
            return new Integer( listOfModelsVisualizationValue.size() );
		} else {
			return null;
		}
	}
}
