/*
 * Created on Dec 13, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle;

/**
 * @author caitlin
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WalkAnimation extends AbstractWalkAnimation {
	public final edu.cmu.cs.stage3.alice.core.property.NumberProperty distance = new edu.cmu.cs.stage3.alice.core.property.NumberProperty(this, "distance", new Double(1.0));

	public WalkAnimation() {
		style.set(TraditionalAnimationStyle.BEGIN_AND_END_ABRUPTLY);
	}

	@Override
	protected void propertyChanging(edu.cmu.cs.stage3.alice.core.Property property, Object value) {
		if (property == distance) {
			if (value instanceof Number) {
				double distance = ((Number) value).doubleValue();

				// duration.set((new Double(distance * 3)));
			}
		} else {
			super.propertyChanging(property, value);
		}
	}

	public class RuntimeWalkAnimation extends RuntimeAbstractWalkAnimation {
		double currentPos = 0.0;
		double stepLength = -1.0;

		double numberOfSteps = -1.0;
		double timePerStep = -1.0;

		private boolean done = false;

		protected double getActualStepLength() {
			if (stepLength == -1) {
				stepLength = getStepLength();
				if (stepLength == 0.0) {
					stepLength = 1.0;
				}
			}

			if (numberOfSteps == -1) {
				numberOfSteps = java.lang.Math.round(distance.doubleValue() / stepLength);
			}

			return distance.doubleValue() / numberOfSteps;

		}

		protected double getTotalTime() {
			getActualStepLength();
			if (Double.isNaN(duration.doubleValue())) {
				return numberOfSteps / stepSpeed.doubleValue();
			} else {
				return duration.doubleValue();
			}
		}

		@Override
		public double getTimeRemaining(double t) {
			return getTotalTime() - getTimeElapsed(t);
		}

		@Override
		public void prologue(double t) {
			super.prologue(t);
			currentPos = 0.0;
		}

		@Override
		public void update(double t) {
			if (getTimeRemaining(t) > 0) {
				super.update(t);

				done = false;

				if (timePerStep == -1) {
					if (!Double.isNaN(duration.doubleValue())) {
						timePerStep = duration.doubleValue() / numberOfSteps;
					} else {
						timePerStep = 1.0 / stepSpeed.doubleValue();
					}
				}

				int stepNumber = (int) java.lang.Math.ceil(getTimeElapsed(t) * (1.0 / timePerStep)) - 1;
				if (stepNumber == -1) {
					stepNumber = 0;
				}
				if (stepNumber == numberOfSteps) {
					stepNumber -= 1;
				}
				double portionOfStep = (getTimeElapsed(t) - stepNumber * timePerStep) / timePerStep;

				if (portionOfStep > 1.0) {
					portionOfStep = 1.0;
				}

				boolean lastStep = false;
				if (stepNumber == numberOfSteps - 1) {
					lastStep = true;
				}

				if (stepNumber % 2 == 0) {
					stepRight(portionOfStep, lastStep);
				} else {
					stepLeft(portionOfStep, lastStep);
				}

				double portion = getTimeElapsed(t) / getTotalTime();
				double targetDistance = distance.doubleValue() * portion;

				if (targetDistance - currentPos > 0) {

					WalkAnimation.this.subject.getTransformableValue().moveRightNow(edu.cmu.cs.stage3.alice.core.Direction.FORWARD, targetDistance - currentPos);
					currentPos += targetDistance - currentPos;

					adjustHeight();
				}
			}

		}
	}
}
