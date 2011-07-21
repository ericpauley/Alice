package edu.cmu.cs.stage3.alice.core.question.visualization.model;

import edu.cmu.cs.stage3.alice.core.property.ModelVisualizationProperty;

public class Item extends edu.cmu.cs.stage3.alice.core.question.ModelQuestion {
	public final ModelVisualizationProperty subject = new ModelVisualizationProperty( this, "subject", null );
	
	public Object getValue() {
		edu.cmu.cs.stage3.alice.core.visualization.ModelVisualization modelVisualizationValue = subject.getModelVisualizationValue();
		if( modelVisualizationValue!=null ) {
            return modelVisualizationValue.getItem();
		} else {
			return null;
		}
	}
}