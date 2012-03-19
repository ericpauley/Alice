package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.CameraProperty;
import edu.cmu.cs.stage3.alice.core.property.ModelProperty;

public class CanSee extends BooleanQuestion {
	public final CameraProperty camera = new CameraProperty(this, "camera", null);
	public final ModelProperty object = new ModelProperty(this, "object", null);
	public final BooleanProperty checkForOcclusion = new BooleanProperty(this, "checkForOcclusion", Boolean.FALSE);

	@Override
	public Object getValue() {
		edu.cmu.cs.stage3.alice.core.Camera cameraValue = camera.getCameraValue();
		edu.cmu.cs.stage3.alice.core.Model objectValue = object.getModelValue();
		if (cameraValue.canSee(objectValue, checkForOcclusion.booleanValue())) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}
}