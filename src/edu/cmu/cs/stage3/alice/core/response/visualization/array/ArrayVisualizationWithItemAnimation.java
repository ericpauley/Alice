package edu.cmu.cs.stage3.alice.core.response.visualization.array;

import edu.cmu.cs.stage3.alice.core.property.ModelProperty;

public class ArrayVisualizationWithItemAnimation extends ArrayVisualizationAnimation {
    public final ModelProperty item = new ModelProperty( this, "item", null );
    public class RuntimeArrayVisualizationWithItemAnimation extends RuntimeArrayVisualizationAnimation {
    }
}
