package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.visualization.ListOfModelsVisualization;

public class ListOfModelsVisualizationProperty extends CollectionOfModelsVisualizationProperty {
	public ListOfModelsVisualizationProperty( Element owner, String name, ListOfModelsVisualization defaultValue ) {
		super( owner, name, defaultValue, ListOfModelsVisualization.class );
	}
	public ListOfModelsVisualization getListOfModelsVisualizationValue() {
		return (ListOfModelsVisualization)getCollectionOfModelsVisualizationValue();
	}
}
