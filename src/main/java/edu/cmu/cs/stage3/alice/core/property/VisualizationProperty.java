package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Visualization;

public class VisualizationProperty extends ModelProperty {
	protected VisualizationProperty( Element owner, String name, Visualization defaultValue, Class valueClass ) {
		super( owner, name, defaultValue, valueClass );
	}
	public VisualizationProperty( Element owner, String name, Visualization defaultValue ) {
		this( owner, name, defaultValue, Visualization.class );
	}
	public Visualization getVisualizationValue() {
		return (Visualization)getModelValue();
	}
}
