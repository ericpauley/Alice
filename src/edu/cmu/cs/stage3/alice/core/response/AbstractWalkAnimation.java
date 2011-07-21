/*
 * Created on Dec 13, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.criterion.ElementNameContainsCriterion;
import edu.cmu.cs.stage3.alice.core.event.PropertyListener;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.math.Vector3;
import edu.cmu.cs.stage3.util.HowMuch;

/**
 * @author caitlin
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AbstractWalkAnimation extends Animation implements PropertyListener {
	public final edu.cmu.cs.stage3.alice.core.property.TransformableProperty subject = new edu.cmu.cs.stage3.alice.core.property.TransformableProperty( this, "subject", null );
	public final edu.cmu.cs.stage3.alice.core.property.AmountProperty stepAmount = new edu.cmu.cs.stage3.alice.core.property.AmountProperty(this, "step size", edu.cmu.cs.stage3.alice.core.Amount.NORMAL);
	public final edu.cmu.cs.stage3.alice.core.property.AmountProperty bounceAmount = new edu.cmu.cs.stage3.alice.core.property.AmountProperty(this, "bounce size", edu.cmu.cs.stage3.alice.core.Amount.NORMAL);
	public final edu.cmu.cs.stage3.alice.core.property.AmountProperty armAmount = new edu.cmu.cs.stage3.alice.core.property.AmountProperty(this, "arm swing size", edu.cmu.cs.stage3.alice.core.Amount.NORMAL);
	public final edu.cmu.cs.stage3.alice.core.property.BooleanProperty swingArms = new edu.cmu.cs.stage3.alice.core.property.BooleanProperty(this, "swing arms", Boolean.TRUE);
	
	public final NumberProperty stepSpeed = new NumberProperty(this, "stepsPerSecond", new Double(1.5));
	
	public AbstractWalkAnimation() {
		duration.set(new Double(Double.NaN));
		
		duration.addPropertyListener(this);
		stepSpeed.addPropertyListener(this);
		
	}
	
	
	
	protected void propertyChanged( Property property, Object value ) {
		super.propertyChanged(property, value);
		if (property.equals(duration)) {
			if (Double.isNaN( ((Double)value).doubleValue()) ){
			} else {
				stepSpeed.set(new Double(Double.NaN));

			}
		} else if (property.equals(stepSpeed)){
			if (Double.isNaN( ((Double)value).doubleValue()) ){
			} else {
				duration.set(new Double(Double.NaN));
			}
		}		
	}
	
	
	public class RuntimeAbstractWalkAnimation extends RuntimeAnimation {
		protected edu.cmu.cs.stage3.alice.core.Transformable subject;
		
		protected static final double normalContactAngle = .46;
		protected static final double normalBackRecoilAngle = 1.3;
		protected static final double normalFrontRecoilAngle = 0.5;
		
		protected double upperArmAngle = 0.3;
		protected double lowerArmAngle = 0.1;
		
		protected double portionContact = 1.0/3.0;
		protected double portionRecoil = 1.0/6.0;
		protected double portionPassing = 1.0/3.0;
		protected double portionHighPoint = 1.0/6.0;
		
		protected double contactAngle = 0.2450;
		
		protected double recoilBackLowerAngle = 0.6;
		protected double recoilFrontUpperAngle = 0.3;
		
		protected double passingFrontUpperAngle = 0;
		protected double passingFrontLowerAngle = 0;
		protected double passingFrontFootAngle = 0;
		
		protected double passingBackLowerAngle = 0.2;
		
		protected double highPointFrontUpperAngle = 0.2;
		protected double highPointBackUpperAngle = 0.7;
		protected double highPointBackLowerAngle = 0;
		
		protected double heightFromGround = 0.0;
		protected double initialBoundingBoxHeight = 0.0;
		
		protected boolean firstTimeContact = true;
		protected boolean firstTimeRecoil = true;
		protected boolean firstTimePassing = true;
		protected boolean firstTimeHighPoint = true;
		
		//legs
		protected edu.cmu.cs.stage3.alice.core.Transformable rightUpper = null;
		protected edu.cmu.cs.stage3.alice.core.Transformable rightLower = null;
		protected edu.cmu.cs.stage3.alice.core.Transformable rightFoot = null;
		
		protected edu.cmu.cs.stage3.alice.core.Transformable leftUpper = null;
		protected edu.cmu.cs.stage3.alice.core.Transformable leftLower = null;
		protected edu.cmu.cs.stage3.alice.core.Transformable leftFoot = null;
		
		//arms
		protected edu.cmu.cs.stage3.alice.core.Transformable rightUpperArm = null;
		protected edu.cmu.cs.stage3.alice.core.Transformable rightLowerArm = null;
		
		protected edu.cmu.cs.stage3.alice.core.Transformable leftUpperArm = null;
		protected edu.cmu.cs.stage3.alice.core.Transformable leftLowerArm = null;
		
		// leg Lengths
		protected double totalLength = 0.0;
		protected double upperLength = 0.0;
		protected double lowerLength = 0.0;
		protected double footLength = 0.0;
		protected double footHorizLength = 0.0;
		
		// leg initial orient
		protected edu.cmu.cs.stage3.math.Matrix33 rightUpperInitialOrient = null;
		protected edu.cmu.cs.stage3.math.Matrix33 rightLowerInitialOrient = null;
		protected edu.cmu.cs.stage3.math.Matrix33 rightFootInitialOrient = null;
		
		protected edu.cmu.cs.stage3.math.Matrix33 leftUpperInitialOrient = null;
		protected edu.cmu.cs.stage3.math.Matrix33 leftLowerInitialOrient = null;
		protected edu.cmu.cs.stage3.math.Matrix33 leftFootInitialOrient = null;	
		
//		arm initial orient
		protected edu.cmu.cs.stage3.math.Matrix33 rightUpperArmInitialOrient = null;
		protected edu.cmu.cs.stage3.math.Matrix33 rightLowerArmInitialOrient = null;
	
		protected edu.cmu.cs.stage3.math.Matrix33 leftUpperArmInitialOrient = null;
		protected edu.cmu.cs.stage3.math.Matrix33 leftLowerArmInitialOrient = null;	
		
		protected edu.cmu.cs.stage3.math.Vector3 initialPos = null;
		
		protected edu.cmu.cs.stage3.math.Matrix33 defaultOrient = new edu.cmu.cs.stage3.math.Matrix33();
		
		//leg contact quaternions
		protected edu.cmu.cs.stage3.math.Matrix33 frontUpperContactOrient = new edu.cmu.cs.stage3.math.Matrix33();
		protected edu.cmu.cs.stage3.math.Matrix33 frontLowerContactOrient = new edu.cmu.cs.stage3.math.Matrix33();
		protected edu.cmu.cs.stage3.math.Matrix33 frontFootContactOrient = new edu.cmu.cs.stage3.math.Matrix33();

		protected edu.cmu.cs.stage3.math.Matrix33 backUpperContactOrient = new edu.cmu.cs.stage3.math.Matrix33();
		protected edu.cmu.cs.stage3.math.Matrix33 backLowerContactOrient = new edu.cmu.cs.stage3.math.Matrix33();
		protected edu.cmu.cs.stage3.math.Matrix33 backFootContactOrient = new edu.cmu.cs.stage3.math.Matrix33();
		
		protected edu.cmu.cs.stage3.math.Vector3 contactPos = null;
		protected double distanceToMoveContact = 0.0;	
		
//		leg recoil quaternions
		protected edu.cmu.cs.stage3.math.Matrix33 frontUpperRecoilOrient = new edu.cmu.cs.stage3.math.Matrix33();
		protected edu.cmu.cs.stage3.math.Matrix33 frontLowerRecoilOrient = new edu.cmu.cs.stage3.math.Matrix33();
		protected edu.cmu.cs.stage3.math.Matrix33 frontFootRecoilOrient = new edu.cmu.cs.stage3.math.Matrix33();

		protected edu.cmu.cs.stage3.math.Matrix33 backUpperRecoilOrient = new edu.cmu.cs.stage3.math.Matrix33();
		protected edu.cmu.cs.stage3.math.Matrix33 backLowerRecoilOrient = new edu.cmu.cs.stage3.math.Matrix33();
		protected edu.cmu.cs.stage3.math.Matrix33 backFootRecoilOrient = new edu.cmu.cs.stage3.math.Matrix33();
		
		protected edu.cmu.cs.stage3.math.Vector3 recoilPos = null;
		protected double distanceToMoveRecoil = 0.0;	
		
		//leg passing stuff
		protected edu.cmu.cs.stage3.math.Matrix33 frontUpperPassingOrient = new edu.cmu.cs.stage3.math.Matrix33();
		protected edu.cmu.cs.stage3.math.Matrix33 frontLowerPassingOrient = new edu.cmu.cs.stage3.math.Matrix33();
		protected edu.cmu.cs.stage3.math.Matrix33 frontFootPassingOrient = new edu.cmu.cs.stage3.math.Matrix33();

		protected edu.cmu.cs.stage3.math.Matrix33 backUpperPassingOrient = new edu.cmu.cs.stage3.math.Matrix33();
		protected edu.cmu.cs.stage3.math.Matrix33 backLowerPassingOrient = new edu.cmu.cs.stage3.math.Matrix33();
		protected edu.cmu.cs.stage3.math.Matrix33 backFootPassingOrient = new edu.cmu.cs.stage3.math.Matrix33();

		protected edu.cmu.cs.stage3.math.Vector3 passingPos = null;
		protected double distanceToMovePassing = 0.0;	
		
		// leg high point stuff
		protected edu.cmu.cs.stage3.math.Matrix33 frontUpperHighPointOrient = new edu.cmu.cs.stage3.math.Matrix33();
		protected edu.cmu.cs.stage3.math.Matrix33 frontLowerHighPointOrient = new edu.cmu.cs.stage3.math.Matrix33();
		protected edu.cmu.cs.stage3.math.Matrix33 frontFootHighPointOrient = new edu.cmu.cs.stage3.math.Matrix33();

		protected edu.cmu.cs.stage3.math.Matrix33 backUpperHighPointOrient = new edu.cmu.cs.stage3.math.Matrix33();
		protected edu.cmu.cs.stage3.math.Matrix33 backLowerHighPointOrient = new edu.cmu.cs.stage3.math.Matrix33();
		protected edu.cmu.cs.stage3.math.Matrix33 backFootHighPointOrient = new edu.cmu.cs.stage3.math.Matrix33();

		protected edu.cmu.cs.stage3.math.Vector3 highPointPos = null;
		protected double distanceToMoveHighPoint = 0.0;
		
//		arm orients
		protected edu.cmu.cs.stage3.math.Matrix33 frontUpperArmOrient = new edu.cmu.cs.stage3.math.Matrix33();
		protected edu.cmu.cs.stage3.math.Matrix33 frontLowerArmOrient = new edu.cmu.cs.stage3.math.Matrix33();

		protected edu.cmu.cs.stage3.math.Matrix33 backUpperArmOrient = new edu.cmu.cs.stage3.math.Matrix33();
		protected edu.cmu.cs.stage3.math.Matrix33 backLowerArmOrient = new edu.cmu.cs.stage3.math.Matrix33();
		
//		current orientations
		protected edu.cmu.cs.stage3.math.Matrix33 rightUpperCurrentOrient = new edu.cmu.cs.stage3.math.Matrix33();
		protected edu.cmu.cs.stage3.math.Matrix33 rightLowerCurrentOrient = new edu.cmu.cs.stage3.math.Matrix33();
		protected edu.cmu.cs.stage3.math.Matrix33 rightFootCurrentOrient = new edu.cmu.cs.stage3.math.Matrix33();

		protected edu.cmu.cs.stage3.math.Matrix33 leftUpperCurrentOrient = new edu.cmu.cs.stage3.math.Matrix33();
		protected edu.cmu.cs.stage3.math.Matrix33 leftLowerCurrentOrient = new edu.cmu.cs.stage3.math.Matrix33();
		protected edu.cmu.cs.stage3.math.Matrix33 leftFootCurrentOrient = new edu.cmu.cs.stage3.math.Matrix33();
		
		protected edu.cmu.cs.stage3.math.Matrix33 rightUpperArmCurrentOrient = new edu.cmu.cs.stage3.math.Matrix33();
		protected edu.cmu.cs.stage3.math.Matrix33 rightLowerArmCurrentOrient = new edu.cmu.cs.stage3.math.Matrix33();

		protected edu.cmu.cs.stage3.math.Matrix33 leftUpperArmCurrentOrient = new edu.cmu.cs.stage3.math.Matrix33();
		protected edu.cmu.cs.stage3.math.Matrix33 leftLowerArmCurrentOrient = new edu.cmu.cs.stage3.math.Matrix33();	
	
						
		
		public void prologue( double t ) {
			
			//System.out.println("abstract walk prologue");
			super.prologue( t );
		
			resetData();
			
			subject = AbstractWalkAnimation.this.subject.getTransformableValue();
			recoilFrontUpperAngle = normalFrontRecoilAngle;
			
			if (AbstractWalkAnimation.this.armAmount.getValue().equals(edu.cmu.cs.stage3.alice.core.Amount.HUGE)) {
				upperArmAngle = 0.8;
				lowerArmAngle = 1.2;
			} else if (AbstractWalkAnimation.this.armAmount.getValue().equals(edu.cmu.cs.stage3.alice.core.Amount.BIG)) {
				upperArmAngle = 0.675;
				lowerArmAngle = 0.925;
			} else if (AbstractWalkAnimation.this.armAmount.getValue().equals(edu.cmu.cs.stage3.alice.core.Amount.NORMAL)) {
				upperArmAngle = 0.55;
				lowerArmAngle = 0.65;
			} else if (AbstractWalkAnimation.this.armAmount.getValue().equals(edu.cmu.cs.stage3.alice.core.Amount.LITTLE)) {
				upperArmAngle = 0.425;
				lowerArmAngle = 0.375;
			} else if (AbstractWalkAnimation.this.armAmount.getValue().equals(edu.cmu.cs.stage3.alice.core.Amount.TINY)) {
				upperArmAngle = 0.3;
				lowerArmAngle = 0.1;
			}
			
			if (AbstractWalkAnimation.this.bounceAmount.getValue().equals(edu.cmu.cs.stage3.alice.core.Amount.HUGE)) {
				recoilFrontUpperAngle = 0.5;
				recoilBackLowerAngle = 2.0;
			} else if (AbstractWalkAnimation.this.bounceAmount.getValue().equals(edu.cmu.cs.stage3.alice.core.Amount.BIG)) {
				recoilFrontUpperAngle = 0.37;
				recoilBackLowerAngle = 1.625;
			} else if (AbstractWalkAnimation.this.bounceAmount.getValue().equals(edu.cmu.cs.stage3.alice.core.Amount.NORMAL)) {
				recoilFrontUpperAngle = 0.25;
				recoilBackLowerAngle = 1.25;
			} else if (AbstractWalkAnimation.this.bounceAmount.getValue().equals(edu.cmu.cs.stage3.alice.core.Amount.LITTLE)) {
				recoilFrontUpperAngle = 0.12;
				recoilBackLowerAngle = 0.875;
			} else if (AbstractWalkAnimation.this.bounceAmount.getValue().equals(edu.cmu.cs.stage3.alice.core.Amount.TINY)) {
				recoilFrontUpperAngle = 0.0;
				recoilBackLowerAngle = 0.5;
			}

			if (AbstractWalkAnimation.this.stepAmount.getValue().equals(edu.cmu.cs.stage3.alice.core.Amount.HUGE)) {
				contactAngle = normalContactAngle * 1.5;
			} else if (AbstractWalkAnimation.this.stepAmount.getValue().equals(edu.cmu.cs.stage3.alice.core.Amount.BIG)) {
				contactAngle = normalContactAngle * 1.25;
			} else if (AbstractWalkAnimation.this.stepAmount.getValue().equals(edu.cmu.cs.stage3.alice.core.Amount.NORMAL)) {
				contactAngle = normalContactAngle;
			} else if (AbstractWalkAnimation.this.stepAmount.getValue().equals(edu.cmu.cs.stage3.alice.core.Amount.LITTLE)) {
				contactAngle = normalContactAngle * 0.75;
			} else if (AbstractWalkAnimation.this.stepAmount.getValue().equals(edu.cmu.cs.stage3.alice.core.Amount.TINY)) {
				contactAngle = normalContactAngle * 0.5;
			}		
			
			findLegs();
			findArms();
			setLegLengths();
			setInitialOrientations();
			setContactData();
			setRecoilData();
			setPassingData();
			setHighPointData();			
			setArmData();
		}
		
		// in the step methods, portion is the portion for the current step, not the animation
		// as a whole.
		
		public void stepRight(double portion, boolean lastStep) {
			step(rightUpper, portion, lastStep);
		}
		
		public void stepLeft(double portion, boolean lastStep) {
			step(leftUpper, portion, lastStep);
		}
		
		protected void step(edu.cmu.cs.stage3.alice.core.Transformable leg, double portion, boolean lastStep){
//			move arms...
			//System.out.println("update: " + portion);
			adjustHeight();
			
			if (AbstractWalkAnimation.this.swingArms.getValue().equals(Boolean.TRUE)) {
				updateArms(leg, portion, lastStep);
			}
			
			if (portion < portionContact) {
				if(firstTimeContact) {
					firstTimeContact = false;
					firstTimeHighPoint = true;
					getCurrentOrientations();
				}
				portion = portion / portionContact;
				updateContact(leg, portion);
			} else if (portion < portionContact + portionRecoil) {
				if (firstTimeRecoil) {
					firstTimeRecoil = false;
					firstTimeContact = true;
					getCurrentOrientations();
				}
				portion = (portion - portionContact) / portionRecoil;
				if (leftLower != null) {
					updateRecoil(leg, portion);
				} 
			} else if (portion < portionContact + portionRecoil + portionPassing) {
				if (firstTimePassing) {
					firstTimePassing = false;
					firstTimeRecoil = true;
					getCurrentOrientations();
				}
				portion = (portion - portionContact - portionRecoil) / portionPassing;
				updatePassing(leg, portion);
			} else {
				if (firstTimeHighPoint) {
					firstTimeHighPoint = false;
					firstTimePassing = true;
					getCurrentOrientations();
				}
				portion = (portion - portionContact - portionRecoil - portionPassing) / portionHighPoint;
				updateHighPoint(leg, portion, lastStep);
			}
			
			adjustHeight();
			
		}
		
		protected void adjustHeight() {
			double distanceAboveGround = 0.0;
			if ((rightFoot != null)&& (leftFoot!=null)) {
				double rightHeight =  rightFoot.getBoundingBox(subject.getWorld()).getCenterOfBottomFace().y;
				double leftHeight = leftFoot.getBoundingBox(subject.getWorld()).getCenterOfBottomFace().y;
				
				distanceAboveGround = java.lang.Math.min(rightHeight, leftHeight);				
			} else {
				distanceAboveGround = subject.getBoundingBox(subject.getWorld()).getCenterOfBottomFace().y;
			}
			double roundHeight = java.lang.Math.round(subject.getBoundingBox(subject.getWorld()).getCenterOfBottomFace().y);
			int level = (int)java.lang.Math.round(roundHeight/256);
			subject.moveRightNow(edu.cmu.cs.stage3.alice.core.Direction.DOWN,(distanceAboveGround - (256.0 * level)), subject.getWorld() );
		}
		
		public void getCurrentOrientations() {
						
			if (rightUpper != null) rightUpperCurrentOrient = rightUpper.getOrientationAsAxes();
			if (rightLower != null) rightLowerCurrentOrient = rightLower.getOrientationAsAxes();
			if (rightFoot != null) rightFootCurrentOrient = rightFoot.getOrientationAsAxes();

			if (leftUpper != null) leftUpperCurrentOrient = leftUpper.getOrientationAsAxes();
			if (leftLower != null) leftLowerCurrentOrient = leftLower.getOrientationAsAxes();
			if (leftFoot != null) leftFootCurrentOrient = leftFoot.getOrientationAsAxes();
			
			if (rightUpperArm != null) rightUpperArmCurrentOrient = rightUpperArm.getOrientationAsAxes();
			if (rightLowerArm != null) rightLowerArmCurrentOrient = rightLowerArm.getOrientationAsAxes();

			if (leftUpperArm != null) leftUpperArmCurrentOrient = leftUpperArm.getOrientationAsAxes();
			if (leftLowerArm != null) leftLowerArmCurrentOrient = leftLowerArm.getOrientationAsAxes();
			
	}
		
		
		// search model to find the legs
		public void findLegs() {
			edu.cmu.cs.stage3.alice.core.Element[] legs = subject.search(new ElementNameContainsCriterion("UpperLeg"));
			for (int i = 0; i < legs.length; i++) {
				if ((legs[i].getKey().indexOf("leftU") != -1) && (legs[i] instanceof edu.cmu.cs.stage3.alice.core.Transformable) ){
					leftUpper = (edu.cmu.cs.stage3.alice.core.Transformable)legs[i];
					leftLower = getTransformableChild(leftUpper);
					if (leftLower.name.getStringValue().indexOf("Foot") != -1){
						leftFoot = leftLower;
						leftLower = null;
					}
					if (leftLower != null) {
						leftFoot = getTransformableChild(leftLower); 		
					} else {
						leftFoot = getTransformableChild(leftUpper);
					} 	
				} else if ((legs[i].getKey().indexOf("rightU") != -1) && (legs[i] instanceof edu.cmu.cs.stage3.alice.core.Transformable) ) {
					rightUpper = (edu.cmu.cs.stage3.alice.core.Transformable)legs[i];
					//System.out.println("right upper " + rightUpper);
					rightLower = getTransformableChild(rightUpper);
					if (rightLower.name.getStringValue().indexOf("Foot") != -1){
						rightFoot = rightLower;
						rightLower = null;
					}
					if (rightLower != null) {
						rightFoot = getTransformableChild(rightLower); 		
					} else {
						rightFoot = getTransformableChild(rightUpper);
					} 
				}
			}
		}
		
		public void findArms() {
			edu.cmu.cs.stage3.alice.core.Element[] arms = subject.search(new ElementNameContainsCriterion("UpperArm"));
			for (int i = 0; i < arms.length; i++) {
				if ((arms[i].getKey().indexOf("left") != -1) && (arms[i] instanceof edu.cmu.cs.stage3.alice.core.Transformable) ){
					leftUpperArm = (edu.cmu.cs.stage3.alice.core.Transformable)arms[i];
					leftLowerArm = getTransformableChild(leftUpperArm);
					if ((leftLowerArm != null) && (leftLowerArm.name.getStringValue().indexOf("Hand") != -1)){
						leftLowerArm = null;
					}	
				} else if ((arms[i].getKey().indexOf("right") != -1) && (arms[i] instanceof edu.cmu.cs.stage3.alice.core.Transformable) ) {
					rightUpperArm = (edu.cmu.cs.stage3.alice.core.Transformable)arms[i];
					rightLowerArm = getTransformableChild(rightUpperArm);
					if ((rightLowerArm != null) && (rightLowerArm.name.getStringValue().indexOf("Hand") != -1)){
						rightLowerArm = null;
					}
				}
			}
		}
		
		public void resetData() {		
			contactAngle = 0.245;
		
			recoilBackLowerAngle = 0.2;
			recoilFrontUpperAngle = 0.4;
	
			passingFrontUpperAngle = 0;
			passingFrontLowerAngle = 0;
			passingFrontFootAngle = 0;
	
			passingBackLowerAngle = 0.2;
	
			highPointFrontUpperAngle = 0.2;
			highPointBackUpperAngle = 0.7;
			highPointBackLowerAngle = 0;
			
			frontUpperContactOrient = new edu.cmu.cs.stage3.math.Matrix33();
			frontLowerContactOrient = new edu.cmu.cs.stage3.math.Matrix33();
			frontFootContactOrient = new edu.cmu.cs.stage3.math.Matrix33();

			backUpperContactOrient = new edu.cmu.cs.stage3.math.Matrix33();
			backLowerContactOrient = new edu.cmu.cs.stage3.math.Matrix33();
			backFootContactOrient = new edu.cmu.cs.stage3.math.Matrix33();

			contactPos = null;
			distanceToMoveContact = 0.0;	

//					leg recoil quaternions
			frontUpperRecoilOrient = new edu.cmu.cs.stage3.math.Matrix33();
			frontLowerRecoilOrient = new edu.cmu.cs.stage3.math.Matrix33();
			frontFootRecoilOrient = new edu.cmu.cs.stage3.math.Matrix33();

			backUpperRecoilOrient = new edu.cmu.cs.stage3.math.Matrix33();
			backLowerRecoilOrient = new edu.cmu.cs.stage3.math.Matrix33();
			backFootRecoilOrient = new edu.cmu.cs.stage3.math.Matrix33();

			recoilPos = null;
			distanceToMoveRecoil = 0.0;	

			//leg passing stuff
			frontUpperPassingOrient = new edu.cmu.cs.stage3.math.Matrix33();
			frontLowerPassingOrient = new edu.cmu.cs.stage3.math.Matrix33();
			frontFootPassingOrient = new edu.cmu.cs.stage3.math.Matrix33();

			backUpperPassingOrient = new edu.cmu.cs.stage3.math.Matrix33();
			backLowerPassingOrient = new edu.cmu.cs.stage3.math.Matrix33();
			backFootPassingOrient = new edu.cmu.cs.stage3.math.Matrix33();

			passingPos = null;
			distanceToMovePassing = 0.0;	

			// leg high point stuff
			frontUpperHighPointOrient = new edu.cmu.cs.stage3.math.Matrix33();
			frontLowerHighPointOrient = new edu.cmu.cs.stage3.math.Matrix33();
			frontFootHighPointOrient = new edu.cmu.cs.stage3.math.Matrix33();

			backUpperHighPointOrient = new edu.cmu.cs.stage3.math.Matrix33();
			backLowerHighPointOrient = new edu.cmu.cs.stage3.math.Matrix33();
			backFootHighPointOrient = new edu.cmu.cs.stage3.math.Matrix33();

			highPointPos = null;
			distanceToMoveHighPoint = 0.0;
		}
		
		//assuming that legs are the same length
		public void setLegLengths() {
			Vector3 top = new Vector3();
			javax.vecmath.Vector3d bottom = new javax.vecmath.Vector3d();
			
			footLength = 0.0;
			footHorizLength = 0.0;
			
			if (rightFoot != null) {
				top = rightFoot.getPosition(rightFoot); 
			 	bottom = rightFoot.getBoundingBox(rightFoot).getCenterOfBottomFace();
			 	
				footLength = top.y - bottom.y;
				footHorizLength = bottom.z - top.z;
			}
			
			lowerLength = 0.0;
			
			if (rightLower != null) {
//				this may want to be full distance (assuming that x,z offsets are really small and unimportant)
			   top = rightLower.getPosition(rightLower);
			   if( rightFoot != null) bottom = rightFoot.getPosition(rightLower);
			   else bottom = rightLower.getBoundingBox(rightLower).getCenterOfBottomFace();
			   lowerLength = top.y - bottom.y;
			}
			
			upperLength = 0.0;
			
			if (rightUpper != null) {
				top = rightUpper.getPosition(rightUpper);
				
				if( rightLower != null) bottom = rightLower.getPosition(rightUpper);
				else if (rightFoot != null) bottom = rightFoot.getPosition(rightLower);
				else bottom = rightUpper.getBoundingBox(rightUpper).getCenterOfBottomFace();
				
				upperLength = top.y - bottom.y;
			}
			
			totalLength = footLength + lowerLength + upperLength;	
		}
		
		public double getStepLength() {
			double stepLength = totalLength * java.lang.Math.sin(contactAngle) * 1.5;
			if (stepLength == 0.0) stepLength = 1.0;
			return stepLength;
		}
		
		public void setInitialOrientations() {
			if(rightUpper != null) rightUpperInitialOrient = rightUpper.getOrientationAsAxes(rightUpper);
			//System.out.println("rightUpper: " + rightUpperInitialOrient);
			if(rightLower != null) rightLowerInitialOrient = rightLower.getOrientationAsAxes(rightLower);
			//System.out.println("rightLower: " + rightLowerInitialOrient);
			if(rightFoot != null) rightFootInitialOrient = rightFoot.getOrientationAsAxes(rightFoot);
			//System.out.println("rightFoot: " + rightFootInitialOrient);
			
			if(leftUpper != null) leftUpperInitialOrient = leftUpper.getOrientationAsAxes(leftUpper);
			//System.out.println("leftUpper: " + leftUpperInitialOrient);
			if(leftLower != null) leftLowerInitialOrient = leftLower.getOrientationAsAxes(leftLower);
			//System.out.println("leftLower: " + leftLowerInitialOrient);
			if(leftFoot != null) leftFootInitialOrient = leftFoot.getOrientationAsAxes(leftFoot);
			//System.out.println("leftFoot: " + leftFootInitialOrient);
			
			if(rightUpperArm != null) rightUpperArmInitialOrient = rightUpperArm.getOrientationAsAxes(rightUpperArm);
			//System.out.println("rightUpperArm: " + rightUpperArmInitialOrient);
			if(rightLowerArm != null) rightLowerArmInitialOrient = rightLowerArm.getOrientationAsAxes(rightLowerArm);
			//System.out.println("rightLowerArm: " + rightLowerArmInitialOrient);

			if(leftUpperArm != null) leftUpperArmInitialOrient = leftUpperArm.getOrientationAsAxes(leftUpperArm);
			//System.out.println("leftUpperArm: " + leftUpperArmInitialOrient);
			if(leftLowerArm != null) leftLowerArmInitialOrient = leftLowerArm.getOrientationAsAxes(leftLowerArm);
			//System.out.println("leftLowerArm: " + leftLowerArmInitialOrient);
			
			if ((rightUpper != null) && (leftUpper != null)) {
			
				Vector3 top = rightUpper.getPosition(rightUpper);
				javax.vecmath.Vector3d bottom = rightUpper.getBoundingBox(rightUpper).getCenterOfBottomFace();
				double offset = (top.y - bottom.y) - totalLength;
			
				top = leftUpper.getPosition(leftUpper);
				bottom = leftUpper.getBoundingBox(leftUpper).getCenterOfBottomFace();
				double offset2 = (top.y - bottom.y) - totalLength;
				
				if(offset2>offset) offset = offset2;

				//initialPos = subject.getPosition(subject.getWorld());
				initialPos = subject.getPosition(new javax.vecmath.Vector3d(0,offset, 0), subject.getWorld());	
				heightFromGround = initialPos.y;		
				initialBoundingBoxHeight = getCurrentLegHeight();
			}
		}
		
		public double getCurrentLegHeight() {
			if (rightUpper != null) {
				rightUpper.getBoundingBox(rightUpper.getWorld(), HowMuch.INSTANCE);
	
				double boundingBoxHeight = rightUpper.getBoundingBox(rightUpper.getWorld()).getHeight();
				double boundingBoxHeight2 = leftUpper.getBoundingBox(leftUpper.getWorld()).getHeight();
				if (boundingBoxHeight2 > boundingBoxHeight) boundingBoxHeight = boundingBoxHeight2; 
				
				return boundingBoxHeight;
			} else return 0.0;
		}
		
		public void setContactData() {
			double rotationLower = 0.0;
			double rotationUpper = 0.0;
			
						
			if ( (leftLower == null) || (rightLower == null) ) {
				//System.out.println("lower leg null");
				rotationUpper = contactAngle;
			} else {
			
				double lowerLegEffectiveLength = java.lang.Math.sqrt(footHorizLength*footHorizLength + (lowerLength + footLength)*(lowerLength + footLength));
				double kneeAngle = (totalLength*totalLength-upperLength*upperLength-lowerLegEffectiveLength*lowerLegEffectiveLength)/(-2.0*upperLength*lowerLegEffectiveLength);
				
				//System.out.println("knee Angle: " + kneeAngle);
				//System.out.println("lowerLegLength: " + lowerLegEffectiveLength);
				kneeAngle = java.lang.Math.acos(kneeAngle);
				//System.out.println("knee Angle: " + kneeAngle);
				
				rotationLower = (java.lang.Math.PI-kneeAngle) + java.lang.Math.atan(footHorizLength/(footLength + lowerLength));
				rotationUpper = contactAngle - java.lang.Math.asin((lowerLegEffectiveLength * java.lang.Math.sin(kneeAngle))/totalLength);
						
				recoilBackLowerAngle += rotationLower;
				recoilFrontUpperAngle += contactAngle;
				
				passingFrontUpperAngle = recoilFrontUpperAngle;
				passingFrontLowerAngle = recoilFrontUpperAngle + 0.2;
				passingFrontFootAngle = 0.2;			
				passingBackLowerAngle += recoilBackLowerAngle;
				
				highPointBackLowerAngle = passingBackLowerAngle/2.0;
			}
				
			frontUpperContactOrient.rotateX(-1.0*contactAngle);
			backUpperContactOrient.rotateX(rotationUpper);
			backLowerContactOrient.rotateX(rotationLower);
				
			distanceToMoveContact = totalLength - (totalLength * java.lang.Math.cos(contactAngle));
			contactPos = subject.getPosition(new javax.vecmath.Vector3d(0,-1.0*distanceToMoveContact, 0), subject.getWorld());			
		}
		
		public void setRecoilData() {
			frontUpperRecoilOrient.rotateX(-1.0 * recoilFrontUpperAngle);
			frontLowerRecoilOrient.rotateX(recoilFrontUpperAngle);
			backLowerRecoilOrient.rotateX(recoilBackLowerAngle);		
			
			double distance = upperLength - (upperLength * java.lang.Math.cos(passingFrontUpperAngle)) + lowerLength - (lowerLength * java.lang.Math.cos(passingFrontLowerAngle-passingFrontUpperAngle));
			recoilPos = subject.getPosition(new javax.vecmath.Vector3d(0,-1.0*distance, 0), subject.getWorld());			
		}
		
		public void setPassingData() {
			frontUpperPassingOrient.rotateX(-1.0 * passingFrontUpperAngle);
			frontLowerPassingOrient.rotateX(passingFrontLowerAngle);
			frontFootPassingOrient.rotateX(-1.0 * passingFrontFootAngle);
			
			backUpperPassingOrient.rotateX(-1.0 * passingFrontUpperAngle);
			backLowerPassingOrient.rotateX(passingBackLowerAngle);
			
			double distance = upperLength - (upperLength * java.lang.Math.cos(recoilFrontUpperAngle));
			passingPos = subject.getPosition(new javax.vecmath.Vector3d(0,-1.0*distance, 0), subject.getWorld());			
			
		}
		
		public void setHighPointData() {
			frontUpperHighPointOrient.rotateX(highPointFrontUpperAngle);

			backUpperHighPointOrient.rotateX(-1.0 * highPointBackUpperAngle);
			backLowerHighPointOrient.rotateX(highPointBackLowerAngle);

			double distance = totalLength - (totalLength * java.lang.Math.cos(highPointFrontUpperAngle));
			highPointPos = subject.getPosition(new javax.vecmath.Vector3d(0,-1.0*distance, 0), subject.getWorld());			
		}
		
		public void setArmData() {
			frontUpperArmOrient.rotateX(-1.0 * upperArmAngle);
			frontLowerArmOrient.rotateX(-1.0 * lowerArmAngle);

			backUpperArmOrient.rotateX(2.0 * upperArmAngle);		
		}
		
		public edu.cmu.cs.stage3.alice.core.Transformable getTransformableChild(edu.cmu.cs.stage3.alice.core.Transformable parent) {
			if (parent == null) return null;
			edu.cmu.cs.stage3.alice.core.Element[] legBits = parent.getChildren((edu.cmu.cs.stage3.alice.core.Transformable.class));
			
//			if this leg has more than one part, we've got a problem
			if (legBits.length == 1) {
				return (edu.cmu.cs.stage3.alice.core.Transformable) legBits[0];
			} else return null;
		}
		
				
		public void updateContact( edu.cmu.cs.stage3.alice.core.Transformable leg, double portion) {
			if (portion <= 1.0) {
				if (leg == null) {
				} else if (leg.equals(rightUpper)){
					setQuaternion(rightUpper, rightUpperCurrentOrient, frontUpperContactOrient,portion);
					setQuaternion(rightLower, rightLowerCurrentOrient, frontLowerContactOrient,portion);

					setQuaternion(leftUpper, leftUpperCurrentOrient, backUpperContactOrient, portion);
					setQuaternion(leftLower, leftLowerCurrentOrient, backLowerContactOrient, portion);
				} else {
					setQuaternion(leftUpper, leftUpperCurrentOrient, frontUpperContactOrient,portion);
					setQuaternion(leftLower, leftLowerCurrentOrient, frontLowerContactOrient,portion);

					setQuaternion(rightUpper, rightUpperCurrentOrient, backUpperContactOrient, portion);
					setQuaternion(rightLower, rightLowerCurrentOrient, backLowerContactOrient, portion);
				}
			}
		}
		
		public void updateRecoil( edu.cmu.cs.stage3.alice.core.Transformable leg, double portion ) {
			if (leg == null){
			} else if (portion <= 1.0) {
				if (leg.equals(rightUpper)){
					setQuaternion(rightUpper, rightUpperCurrentOrient, frontUpperRecoilOrient, portion);
					setQuaternion(rightLower, rightLowerCurrentOrient, frontLowerRecoilOrient, portion);

					setQuaternion(leftUpper, leftUpperCurrentOrient, backUpperRecoilOrient, portion);
					setQuaternion(leftLower, leftLowerCurrentOrient, backLowerRecoilOrient, portion);
				} else {
					setQuaternion(leftUpper, leftUpperCurrentOrient, frontUpperRecoilOrient, portion);
					setQuaternion(leftLower, leftLowerCurrentOrient, frontLowerRecoilOrient, portion);

					setQuaternion(rightUpper, rightUpperCurrentOrient, backUpperRecoilOrient, portion);
					setQuaternion(rightLower, rightLowerCurrentOrient, backLowerRecoilOrient, portion);
				}
	
			}		
		}
		
		public void updatePassing( edu.cmu.cs.stage3.alice.core.Transformable leg, double portion ) {
			if (leg == null) {
			} else if (portion <= 1.0) {
				if (leg.equals(rightUpper)){
					setQuaternion(rightUpper, rightUpperCurrentOrient, frontUpperPassingOrient, portion);
					setQuaternion(rightLower, rightLowerCurrentOrient, frontLowerPassingOrient, portion);
					setQuaternion(rightFoot, rightFootCurrentOrient, frontFootPassingOrient, portion);

					setQuaternion(leftUpper, leftUpperCurrentOrient, backUpperPassingOrient, portion);
					setQuaternion(leftLower, leftLowerCurrentOrient, backLowerPassingOrient, portion);
					setQuaternion(leftFoot, leftFootCurrentOrient, backFootPassingOrient, portion);
				} else {
					setQuaternion(leftUpper, leftUpperCurrentOrient, frontUpperPassingOrient, portion);
					setQuaternion(leftLower, leftLowerCurrentOrient, frontLowerPassingOrient, portion);
					setQuaternion(leftFoot, leftFootCurrentOrient, frontFootPassingOrient, portion);

					setQuaternion(rightUpper, rightUpperCurrentOrient, backUpperPassingOrient, portion);
					setQuaternion(rightLower, rightLowerCurrentOrient, backLowerPassingOrient, portion);
					setQuaternion(rightFoot, rightFootCurrentOrient, backFootPassingOrient, portion);
				}
			}	
		}
		
		public void updateHighPoint( edu.cmu.cs.stage3.alice.core.Transformable leg, double portion, boolean lastStep ) {
			if (leg == null) {
			} else if (portion <= 1.0) {
				if (lastStep) {
					if (leg.equals(rightUpper)){
						setQuaternion(rightUpper, rightUpperCurrentOrient, defaultOrient, portion);
						setQuaternion(rightLower, rightLowerCurrentOrient, defaultOrient, portion);
						setQuaternion(rightFoot, rightFootCurrentOrient, defaultOrient, portion);

						setQuaternion(leftUpper, leftUpperCurrentOrient, defaultOrient, portion);
						setQuaternion(leftLower, leftLowerCurrentOrient, defaultOrient, portion);
					} else {
						setQuaternion(leftUpper, leftUpperCurrentOrient, defaultOrient, portion);
						setQuaternion(leftLower, leftLowerCurrentOrient, defaultOrient, portion);
						setQuaternion(leftFoot, leftFootCurrentOrient, defaultOrient, portion);

						setQuaternion(rightUpper, rightUpperCurrentOrient, defaultOrient, portion);
						setQuaternion(rightLower, rightLowerCurrentOrient, defaultOrient, portion);
					}
				} else {
					
					if (leg.equals(rightUpper)){
						setQuaternion(rightUpper, rightUpperCurrentOrient, frontUpperHighPointOrient, portion);
						setQuaternion(rightLower, rightLowerCurrentOrient, frontLowerHighPointOrient, portion);
						setQuaternion(rightFoot, rightFootCurrentOrient, frontFootHighPointOrient, portion);
	
						setQuaternion(leftUpper, leftUpperCurrentOrient, backUpperHighPointOrient, portion);
						setQuaternion(leftLower, leftLowerCurrentOrient, backLowerHighPointOrient, portion);
					} else {
						setQuaternion(leftUpper, leftUpperCurrentOrient, frontUpperHighPointOrient, portion);
						setQuaternion(leftLower, leftLowerCurrentOrient, frontLowerHighPointOrient, portion);
						setQuaternion(leftFoot, leftFootCurrentOrient, frontFootHighPointOrient, portion);
	
						setQuaternion(rightUpper, rightUpperCurrentOrient, backUpperHighPointOrient, portion);
						setQuaternion(rightLower, rightLowerCurrentOrient, backLowerHighPointOrient, portion);
					}
				}

			}		
		}
		
		public void updateArms(edu.cmu.cs.stage3.alice.core.Transformable leg, double portion, boolean lastStep){
			if (lastStep && (leg != null)) {
				setQuaternion(leftUpperArm, leftUpperArmCurrentOrient, defaultOrient, portion);
				setQuaternion(leftLowerArm, leftLowerArmCurrentOrient, defaultOrient, portion);

				setQuaternion(rightUpperArm, rightUpperArmCurrentOrient, defaultOrient, portion);
				setQuaternion(rightLowerArm, rightLowerArmCurrentOrient, defaultOrient, portion);
			} else {
				if (leg == null) {
				} else if (leg.equals(leftUpper)) {
					setQuaternion(rightUpperArm, rightUpperArmCurrentOrient, frontUpperArmOrient, portion);
					setQuaternion(rightLowerArm, rightLowerArmCurrentOrient, frontLowerArmOrient, portion);
					
					setQuaternion(leftUpperArm, leftUpperArmCurrentOrient, backUpperArmOrient, portion);
					setQuaternion(leftLowerArm, leftLowerArmCurrentOrient, backLowerArmOrient, portion);
				} else{
					setQuaternion(leftUpperArm, leftUpperArmCurrentOrient, frontUpperArmOrient, portion);
					setQuaternion(leftLowerArm, leftLowerArmCurrentOrient, frontLowerArmOrient, portion);

					setQuaternion(rightUpperArm, rightUpperArmCurrentOrient, backUpperArmOrient, portion);
					setQuaternion(rightLowerArm, rightLowerArmCurrentOrient, backLowerArmOrient, portion);
				}
			}
		}
		
		
		public void epilogue( double t ) {
			
			super.epilogue(t);

			if(leftUpper != null) {				
				if (AbstractWalkAnimation.this.swingArms.getValue().equals(Boolean.TRUE)) {
					setQuaternion(leftUpperArm, leftUpperArmCurrentOrient, defaultOrient, 1.0);
					setQuaternion(leftLowerArm, leftLowerArmCurrentOrient, defaultOrient, 1.0);
		
					setQuaternion(rightUpperArm, rightUpperArmCurrentOrient, defaultOrient, 1.0);
					setQuaternion(rightLowerArm, rightLowerArmCurrentOrient, defaultOrient, 1.0);
				}
				
				setQuaternion(rightUpper, rightUpperCurrentOrient, defaultOrient, 1.0);
				setQuaternion(rightLower, rightLowerCurrentOrient, defaultOrient, 1.0);
				setQuaternion(rightFoot, rightFootCurrentOrient, defaultOrient, 1.0);
	
				setQuaternion(leftUpper, leftUpperCurrentOrient, defaultOrient, 1.0);
				setQuaternion(leftLower, leftLowerCurrentOrient, defaultOrient, 1.0);
				setQuaternion(rightFoot, rightFootCurrentOrient, defaultOrient, 1.0);
			}
			
			adjustHeight();

		}
		
		
		private void setQuaternion(edu.cmu.cs.stage3.alice.core.Transformable part, edu.cmu.cs.stage3.math.Matrix33 initialOrient, edu.cmu.cs.stage3.math.Matrix33 finalOrient, double portion){
			double positionPortion =  m_style.getPortion(portion, 1 );
			edu.cmu.cs.stage3.math.Matrix33 currentOrient = edu.cmu.cs.stage3.math.Matrix33.interpolate( initialOrient, finalOrient, positionPortion );
			
			if (part != null) {
				//System.out.println("set orient: " + part.name.getStringValue() + " " + currentOrient);
				part.setOrientationRightNow( currentOrient );
				//System.out.println("set orient: " + part.name.getStringValue() + " " + currentOrient);
			} 
		}
	}
}

