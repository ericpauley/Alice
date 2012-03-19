package edu.cmu.cs.stage3.caitlin.stencilhelp.client;

public class WaitAndUpdateThread extends Thread {
	long millis = 0;
	StencilManager.Stencil stencil = null;
	LayoutChangeListener obj = null;

	public WaitAndUpdateThread(long millis, StencilManager.Stencil stencil, LayoutChangeListener obj) {
		this.millis = millis;
		this.stencil = stencil;
		this.obj = obj;
	}

	@Override
	public void run() {

		try {
			Thread.sleep(millis);
		} catch (java.lang.InterruptedException ie) {}

		boolean success = obj.layoutChanged();

		if (success == false) {
			// System.out.println("update thread - still missing");
			stencil.setErrorStencil(true);
		}
	}
}