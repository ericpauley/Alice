package edu.cmu.cs.stage3.alice.core.response.visualization.list;

import edu.cmu.cs.stage3.alice.core.property.ModelProperty;

public class ListVisualizationWithItemAnimation extends ListVisualizationAnimation {
    public final ModelProperty item = new ModelProperty( this, "item", null );
    public class RuntimeListVisualizationWithItemAnimation extends RuntimeListVisualizationAnimation {
        public edu.cmu.cs.stage3.alice.core.Model getItem() {
            return item.getModelValue();
        }
    }
}
