package edu.cmu.cs.stage3.alice.core.response.vector3;

import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.Vector3Property;

public class Vector3Response extends edu.cmu.cs.stage3.alice.core.Response {
	public final Vector3Property vector3 = new Vector3Property( this, "vector3", new javax.vecmath.Vector3d() );
	public final NumberProperty value = new NumberProperty( this, "value", new Double( 0 ) );
	
	protected Number getDefaultDuration() {
		return new Double( 0 );
	}
	public abstract class RuntimeVector3Response extends RuntimeResponse {
        private javax.vecmath.Vector3d m_vector3;
        private double m_value;
        protected abstract void set( javax.vecmath.Vector3d vector3d, double value );
		
		public void prologue( double t ) {
			super.prologue( t );
			m_vector3 = Vector3Response.this.vector3.getVector3Value();
			m_value = Vector3Response.this.value.doubleValue();
		}
		
		public void epilogue( double t ) {
			super.prologue( t );
			set( m_vector3, m_value );
		}
	}
}
