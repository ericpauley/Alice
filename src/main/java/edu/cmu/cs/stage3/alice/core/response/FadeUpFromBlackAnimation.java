/*
 * Created on Jun 3, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package edu.cmu.cs.stage3.alice.core.response;

/**
 * @author caitlin
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class FadeUpFromBlackAnimation extends AbstractFadeAnimation {
	public class RuntimeFadeUpFromBlackAnimation extends RuntimeAbstractFadeAnimation {

		@Override
		protected boolean endsBlack() {
			return false;
		}
	}
}
