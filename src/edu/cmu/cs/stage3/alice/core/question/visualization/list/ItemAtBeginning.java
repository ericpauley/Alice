package edu.cmu.cs.stage3.alice.core.question.visualization.list;

import edu.cmu.cs.stage3.alice.core.property.ListOfModelsVisualizationProperty;

public class ItemAtBeginning extends edu.cmu.cs.stage3.alice.core.question.ModelQuestion {
	public final ListOfModelsVisualizationProperty subject = new ListOfModelsVisualizationProperty( this, "subject", null );
	
	public Object getValue() {
		edu.cmu.cs.stage3.alice.core.visualization.ListOfModelsVisualization listOfModelsVisualizationValue = subject.getListOfModelsVisualizationValue();
		if( listOfModelsVisualizationValue!=null ) {
            return listOfModelsVisualizationValue.get( 0 );
		} else {
			return null;
		}
	}
}