package edu.cmu.cs.stage3.alice.core.response.vector3;

public class SetY extends Vector3Response {
	public class RuntimeSetY extends RuntimeVector3Response {
        
		protected void set( javax.vecmath.Vector3d vector3, double v ) {
            vector3.y = v;
        }
	}
}
