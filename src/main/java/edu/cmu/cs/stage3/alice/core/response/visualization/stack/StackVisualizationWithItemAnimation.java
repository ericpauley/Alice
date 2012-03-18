package edu.cmu.cs.stage3.alice.core.response.visualization.stack;

import edu.cmu.cs.stage3.alice.core.property.ModelProperty;

public class StackVisualizationWithItemAnimation extends StackVisualizationAnimation {
    public final ModelProperty item = new ModelProperty( this, "item", null );
    public class RuntimeStackVisualizationWithItemAnimation extends RuntimeStackVisualizationAnimation {
		public edu.cmu.cs.stage3.alice.core.Model getItem() {
			return item.getModelValue();
		}
    }
}
