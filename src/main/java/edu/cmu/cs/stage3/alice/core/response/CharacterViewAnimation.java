/*
 * Created on May 18, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.math.Matrix33;

/**
 * @author caitlin
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CharacterViewAnimation extends AbstractPositionAnimation {
	//public final Vector3Property position = new Vector3Property( this, "position", new javax.vecmath.Vector3d( 0,0,0 ) );

	public static CharacterViewAnimation createCharacterViewAnimation( Object subject, Object asSeenBy ) {
		CharacterViewAnimation charViewAnimation = new CharacterViewAnimation();
		charViewAnimation.subject.set( subject );
		charViewAnimation.asSeenBy.set( asSeenBy );
		return charViewAnimation;
	}
	
	
	public class RuntimeCloseUpAnimation extends RuntimeAbstractPositionAnimation {
		private edu.cmu.cs.stage3.math.Box m_subjectBoundingBox;
		private edu.cmu.cs.stage3.alice.core.Model m_characterHead = null;
		private Matrix33 m_orientationBegin = null;
		private Matrix33 m_orientationEnd = null;
		
		
		protected javax.vecmath.Vector3d getPositionBegin() {
			return m_subject.getPosition( edu.cmu.cs.stage3.alice.core.ReferenceFrame.ABSOLUTE );
		}
			
		
		protected javax.vecmath.Vector3d getPositionEnd() {
			javax.vecmath.Vector3d v = new javax.vecmath.Vector3d(0,0,0); 
			
			if (asSeenBy.get() instanceof Model) {
				Model character = (Model) asSeenBy.get();
				edu.cmu.cs.stage3.alice.core.Element[] heads = character.search(new edu.cmu.cs.stage3.alice.core.criterion.ElementNamedCriterion("head", true) );
				if ((heads.length > 0) && (heads[0] instanceof Model)) {
					m_characterHead = (Model) heads[0];
					v = m_characterHead.getBoundingBox().getCenterOfFrontFace();
					return m_characterHead.getPosition(v, edu.cmu.cs.stage3.alice.core.ReferenceFrame.ABSOLUTE);
				} else {
					v = character.getBoundingBox().getCenterOfFrontFace();
					v.y *= 1.8;
					return character.getPosition(v, edu.cmu.cs.stage3.alice.core.ReferenceFrame.ABSOLUTE);
				}
			} else {
			}
			return m_asSeenBy.getPosition( v, edu.cmu.cs.stage3.alice.core.ReferenceFrame.ABSOLUTE );
		}
		
		protected Matrix33 getOrientationBegin() {
			return m_subject.getOrientationAsAxes(edu.cmu.cs.stage3.alice.core.ReferenceFrame.ABSOLUTE);
		}
		
		protected Matrix33 getOrientationEnd() {
			if (m_characterHead != null) {
				return m_characterHead.getOrientationAsAxes(edu.cmu.cs.stage3.alice.core.ReferenceFrame.ABSOLUTE);
			} else {
				return m_asSeenBy.getOrientationAsAxes(edu.cmu.cs.stage3.alice.core.ReferenceFrame.ABSOLUTE);
			}
		}
		
		
		public void prologue( double t ) {
			m_asSeenBy = CharacterViewAnimation.this.asSeenBy.getReferenceFrameValue();
			if (m_asSeenBy == null) {
				throw new edu.cmu.cs.stage3.alice.core.SimulationPropertyException( "character value must not be null.", getCurrentStack(), CharacterViewAnimation.this.asSeenBy );            
			}	
			super.prologue( t );		
			if (m_subject == m_asSeenBy) {
				throw new edu.cmu.cs.stage3.alice.core.SimulationPropertyException( "subject and character values must not be the same.", getCurrentStack(), CharacterViewAnimation.this.subject );            
			}
			m_orientationBegin = getOrientationBegin();
			m_orientationEnd = null;
		}
		
		
		public void update( double t ) {
			super.update( t );
			if( m_orientationEnd==null ) {
				m_orientationEnd = getOrientationEnd();
			}
			m_subject.setOrientationRightNow( edu.cmu.cs.stage3.math.Matrix33.interpolate( m_orientationBegin, m_orientationEnd, getPortion( t ) ), edu.cmu.cs.stage3.alice.core.ReferenceFrame.ABSOLUTE );
		}
	}
}

