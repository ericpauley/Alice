package edu.cmu.cs.stage3.alice.core.response.visualization.stack;

public class Pop extends StackVisualizationAnimation {
	public class RuntimePop extends RuntimeStackVisualizationAnimation {
		
		public void epilogue( double t ) {
			super.epilogue( t );
			getCollection().values.remove( getCollection().values.size()-1 );
		}
	}
}
