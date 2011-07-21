package edu.cmu.cs.stage3.alice.core.response.vector3;

public class SetZ extends Vector3Response {
	public class RuntimeSetZ extends RuntimeVector3Response {
        
		protected void set( javax.vecmath.Vector3d vector3, double v ) {
            vector3.z = v;
        }
	}
}
