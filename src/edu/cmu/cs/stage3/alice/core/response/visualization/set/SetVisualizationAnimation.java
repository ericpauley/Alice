package edu.cmu.cs.stage3.alice.core.response.visualization.set;

import edu.cmu.cs.stage3.alice.core.property.SetOfModelsVisualizationProperty;

public class SetVisualizationAnimation extends edu.cmu.cs.stage3.alice.core.response.Animation {
    public final SetOfModelsVisualizationProperty subject = new SetOfModelsVisualizationProperty( this, "subject", null );
    public class RuntimeSetVisualizationAnimation extends RuntimeAnimation {
		protected edu.cmu.cs.stage3.alice.core.Collection getCollection() {
			return subject.getCollectionOfModelsVisualizationValue().getItemsCollection();
		}
    }
}
