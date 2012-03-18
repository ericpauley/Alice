package edu.cmu.cs.stage3.alice.core.response.visualization.list;

public class InsertItemAtEnd extends ListVisualizationWithItemAnimation {
	public class RuntimeInsertItemAtEnd extends RuntimeListVisualizationWithItemAnimation {
		
		public void epilogue( double t ) {
			super.epilogue( t );
			getCollection().values.addValue( getItem() );
		}
	}
}
