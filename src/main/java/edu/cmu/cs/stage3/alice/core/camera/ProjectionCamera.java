package edu.cmu.cs.stage3.alice.core.camera;

import edu.cmu.cs.stage3.alice.core.property.Matrix44Property;

public class ProjectionCamera extends edu.cmu.cs.stage3.alice.core.Camera {
	public final Matrix44Property projection = new Matrix44Property(this, "projection", edu.cmu.cs.stage3.math.MathUtilities.createIdentityMatrix4d());
	public ProjectionCamera() {
		super(new edu.cmu.cs.stage3.alice.scenegraph.ProjectionCamera());
	}
	public edu.cmu.cs.stage3.alice.scenegraph.ProjectionCamera getSceneGraphProjectionCamera() {
		return (edu.cmu.cs.stage3.alice.scenegraph.ProjectionCamera) getSceneGraphCamera();
	}

	@Override
	protected edu.cmu.cs.stage3.alice.core.decorator.ViewVolumeDecorator createViewVolumeDecorator() {
		edu.cmu.cs.stage3.alice.core.decorator.ProjectionViewVolumeDecorator projectionViewVolumeDecorator = new edu.cmu.cs.stage3.alice.core.decorator.ProjectionViewVolumeDecorator();
		projectionViewVolumeDecorator.setProjectionCamera(this);
		return projectionViewVolumeDecorator;
	}

	@Override
	protected void propertyChanged(edu.cmu.cs.stage3.alice.core.Property property, Object value) {
		if (property == projection) {
			getSceneGraphProjectionCamera().setProjection((javax.vecmath.Matrix4d) value);
		} else {
			super.propertyChanged(property, value);
		}
	}
}