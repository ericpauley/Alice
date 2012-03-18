package edu.cmu.cs.stage3.alice.core.response.vector3;

public class SetX extends Vector3Response {
	public class RuntimeSetX extends RuntimeVector3Response {
        
		protected void set( javax.vecmath.Vector3d vector3, double v ) {
            vector3.x = v;
        }
	}
}
