/*
 * Created on Jun 1, 2004
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
public class TitleAnimation extends AbstractBubbleAnimation {
	
	public TitleAnimation() {
		super();
		this.fontName.set("Arial");
		this.fontSize.set(new Integer(60));
		this.bubbleColor.set( edu.cmu.cs.stage3.alice.scenegraph.Color.BLACK);
		this.textColor.set( edu.cmu.cs.stage3.alice.scenegraph.Color.WHITE);
	}
	public class RuntimeTitleAnimation extends RuntimeAbstractBubbleAnimation {
		
		protected edu.cmu.cs.stage3.alice.core.bubble.Bubble createBubble() {
			return new edu.cmu.cs.stage3.alice.core.bubble.TitleBubble();
		}
	}

}
