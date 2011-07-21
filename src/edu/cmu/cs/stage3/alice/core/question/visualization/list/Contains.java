package edu.cmu.cs.stage3.alice.core.question.visualization.list;

import edu.cmu.cs.stage3.alice.core.property.ListOfModelsVisualizationProperty;
import edu.cmu.cs.stage3.alice.core.property.ModelProperty;

public class Contains extends edu.cmu.cs.stage3.alice.core.question.BooleanQuestion {
	public final ListOfModelsVisualizationProperty subject = new ListOfModelsVisualizationProperty( this, "subject", null );
	public final ModelProperty item = new ModelProperty( this, "item", null );
	
	public Object getValue() {
		edu.cmu.cs.stage3.alice.core.visualization.ListOfModelsVisualization listOfModelsVisualizationValue = subject.getListOfModelsVisualizationValue();
		if( listOfModelsVisualizationValue!=null ) {
            if( listOfModelsVisualizationValue.contains( item.getModelValue() ) ) {
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
		} else {
			return null;
		}
	}
}