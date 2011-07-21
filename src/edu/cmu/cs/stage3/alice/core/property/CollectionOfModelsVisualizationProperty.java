package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.visualization.CollectionOfModelsVisualization;

public abstract class CollectionOfModelsVisualizationProperty extends VisualizationProperty {
	protected CollectionOfModelsVisualizationProperty( Element owner, String name, CollectionOfModelsVisualization defaultValue, Class valueClass ) {
		super( owner, name, defaultValue, valueClass );
	}
	public CollectionOfModelsVisualization getCollectionOfModelsVisualizationValue() {
		return (CollectionOfModelsVisualization)getVisualizationValue();
	}
}
