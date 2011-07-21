package edu.cmu.cs.stage3.alice.core.response.visualization.model;

import edu.cmu.cs.stage3.math.*;
import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.alice.core.visualization.ModelVisualization;

public class SetItem extends ModelVisualizationWithItemAnimation {
	public class RuntimeSetItem extends RuntimeModelVisualizationWithItemAnimation {
		private Quaternion m_quaternion0;
		private Quaternion m_quaternion1;
        private HermiteCubic m_xHermite;
        private HermiteCubic m_yHermite;
        private HermiteCubic m_zHermite;
        private ModelVisualization m_subject;
        private Model m_value;
		
		public void prologue( double t ) {
            m_subject = subject.getModelVisualizationValue();
            m_value = item.getModelValue();
            Model prev = m_subject.getItem();
            if( prev != null && prev != m_value ) {
                prev.visualization.set( null );
            }
            if( m_value != null ) {

                //todo?
                m_value.visualization.set( null );

                Matrix44 transformation0 = m_value.getTransformation( m_subject );
                Matrix44 transformation1 = new Matrix44( m_subject.getTransformationFor( m_value ) );
                m_quaternion0 = transformation0.getAxes().getQuaternion();
                m_quaternion1 = transformation1.getAxes().getQuaternion();
                double dx = transformation0.m30-transformation1.m30;
                double dy = transformation0.m31-transformation1.m31;
                double dz = transformation0.m32-transformation1.m32;
                double distance = Math.sqrt( dx*dx + dy*dy + dz*dz );
                double s = distance/2;
                m_xHermite = new HermiteCubic( transformation0.m30, transformation1.m30, transformation0.m20*s, transformation1.m20*s );
                m_yHermite = new HermiteCubic( transformation0.m31, transformation1.m31, transformation0.m21*s, transformation1.m21*s );
                m_zHermite = new HermiteCubic( transformation0.m32, transformation1.m32, transformation0.m22*s, transformation1.m22*s );
            }
			super.prologue( t );
		}
		
		public void update( double t ) {
			super.update( t );
            if( m_value != null ) {
                double portion = getPortion( t );
                double x = m_xHermite.evaluate( portion );
                double y = m_yHermite.evaluate( portion );
                double z = m_zHermite.evaluate( portion );
                m_value.setPositionRightNow( x, y, z, m_subject );
                edu.cmu.cs.stage3.math.Quaternion q = edu.cmu.cs.stage3.math.Quaternion.interpolate( m_quaternion0, m_quaternion1, getPortion( t ) );
                m_value.setOrientationRightNow( q, m_subject );
            }
        }
		
		public void epilogue( double t ) {
			super.epilogue( t );
            m_subject.setItem( m_value );
        }
	}
}
