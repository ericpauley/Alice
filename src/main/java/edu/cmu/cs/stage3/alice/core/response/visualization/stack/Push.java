package edu.cmu.cs.stage3.alice.core.response.visualization.stack;

public class Push extends StackVisualizationWithItemAnimation {
	public class RuntimePush extends RuntimeStackVisualizationWithItemAnimation {
		
		public void epilogue( double t ) {
			super.epilogue( t );
			getCollection().values.addValue( getItem() );
		}
	}
}
