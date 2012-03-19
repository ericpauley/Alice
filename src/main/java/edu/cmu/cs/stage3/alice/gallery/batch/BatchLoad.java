package edu.cmu.cs.stage3.alice.gallery.batch;

public class BatchLoad extends Batch {

	@Override
	protected void initialize(edu.cmu.cs.stage3.alice.core.World world) {
		edu.cmu.cs.stage3.alice.core.Camera camera = new edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera();
		camera.name.set("Camera");
		world.addChild(camera);
		edu.cmu.cs.stage3.alice.core.Model model = new edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera();
		model.name.set("Ground");
		world.addChild(model);
	}
	/*
	 * public static void main( String[] args ) { final String srcRootPath =
	 * args[ 0 ]; java.io.File srcDirectory = new java.io.File( srcRootPath );
	 * BatchLoad batchLoad = new BatchLoad(); batchLoad.forEachElement(
	 * srcDirectory, new ElementHandler() { private java.util.Dictionary
	 * m_poseKeyMap = null; private void outln( String s ) { System.out.println(
	 * s ); System.out.flush(); } public void handleElement(
	 * edu.cmu.cs.stage3.alice.core.Element element, java.io.File src ) { outln(
	 * src.toString() ); } } ); }
	 */
}
