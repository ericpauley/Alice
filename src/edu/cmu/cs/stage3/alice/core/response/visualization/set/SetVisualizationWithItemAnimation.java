package edu.cmu.cs.stage3.alice.core.response.visualization.set;

import edu.cmu.cs.stage3.alice.core.property.ModelProperty;

public class SetVisualizationWithItemAnimation extends SetVisualizationAnimation {
    public final ModelProperty item = new ModelProperty( this, "item", null );
    public class RuntimeSetVisualizationWithItemAnimation extends RuntimeSetVisualizationAnimation {
		public edu.cmu.cs.stage3.alice.core.Model getItem() {
			return item.getModelValue();
		}
    }
}

