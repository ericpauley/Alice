package edu.cmu.cs.stage3.alice.core.response.visualization.model;

import edu.cmu.cs.stage3.alice.core.property.ModelVisualizationProperty;

public class ModelVisualizationAnimation extends edu.cmu.cs.stage3.alice.core.response.Animation {
    public final ModelVisualizationProperty subject = new ModelVisualizationProperty( this, "subject", null );
    public class RuntimeModelVisualizationAnimation extends RuntimeAnimation {
    }
}
