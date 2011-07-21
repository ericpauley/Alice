/*
 * Created on May 28, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package edu.cmu.cs.stage3.alice.core.response;

/**
 * @author caitlin
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class NarrateAnimation extends AbstractBubbleAnimation {
	public class RuntimeNarrateAnimation extends RuntimeAbstractBubbleAnimation {
		
		protected edu.cmu.cs.stage3.alice.core.bubble.Bubble createBubble() {
			return new edu.cmu.cs.stage3.alice.core.bubble.NarrateBubble();
		}
	}
}
