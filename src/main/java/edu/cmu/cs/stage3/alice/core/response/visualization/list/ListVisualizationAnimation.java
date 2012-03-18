package edu.cmu.cs.stage3.alice.core.response.visualization.list;

import edu.cmu.cs.stage3.alice.core.property.ListOfModelsVisualizationProperty;

public class ListVisualizationAnimation extends edu.cmu.cs.stage3.alice.core.response.Animation {
    public final ListOfModelsVisualizationProperty subject = new ListOfModelsVisualizationProperty( this, "subject", null );
    public class RuntimeListVisualizationAnimation extends RuntimeAnimation {
        protected edu.cmu.cs.stage3.alice.core.Collection getCollection() {
            return subject.getCollectionOfModelsVisualizationValue().getItemsCollection();
        }
    }
}
