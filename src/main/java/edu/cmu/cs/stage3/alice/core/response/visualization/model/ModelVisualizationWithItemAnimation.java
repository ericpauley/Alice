package edu.cmu.cs.stage3.alice.core.response.visualization.model;

import edu.cmu.cs.stage3.alice.core.property.ModelProperty;

public class ModelVisualizationWithItemAnimation extends ModelVisualizationAnimation {
    public final ModelProperty item = new ModelProperty( this, "item", null );
    public class RuntimeModelVisualizationWithItemAnimation extends RuntimeModelVisualizationAnimation {
    }
}
