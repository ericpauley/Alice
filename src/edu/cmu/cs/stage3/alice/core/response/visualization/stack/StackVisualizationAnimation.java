package edu.cmu.cs.stage3.alice.core.response.visualization.stack;

import edu.cmu.cs.stage3.alice.core.property.StackOfModelsVisualizationProperty;

public class StackVisualizationAnimation extends edu.cmu.cs.stage3.alice.core.response.Animation {
    public final StackOfModelsVisualizationProperty subject = new StackOfModelsVisualizationProperty( this, "subject", null );
    public class RuntimeStackVisualizationAnimation extends RuntimeAnimation {
		protected edu.cmu.cs.stage3.alice.core.Collection getCollection() {
			return subject.getCollectionOfModelsVisualizationValue().getItemsCollection();
		}
    }
}

