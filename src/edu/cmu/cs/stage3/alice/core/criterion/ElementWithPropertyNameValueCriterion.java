/*
 * Created on Jun 3, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package edu.cmu.cs.stage3.alice.core.criterion;

/**
 * @author caitlin
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ElementWithPropertyNameValueCriterion implements edu.cmu.cs.stage3.util.Criterion {
	private String propertyName = null;
	private Object propertyValue = null;
	private boolean returnEqual = true;
	
	public ElementWithPropertyNameValueCriterion(String propertyName, Object value){
		this(propertyName, value, true);
	}
	
	public ElementWithPropertyNameValueCriterion(String propertyName, Object value, boolean returnEqual){
		this.propertyName = propertyName;
		this.propertyValue = value;
		this.returnEqual = returnEqual;
	}

	public boolean accept( Object o ) {
		if (o instanceof edu.cmu.cs.stage3.alice.core.Element) {
			edu.cmu.cs.stage3.alice.core.Element element = (edu.cmu.cs.stage3.alice.core.Element)o;
			
			edu.cmu.cs.stage3.alice.core.Property property = element.getPropertyNamed(propertyName);
			if (property != null) {
				if (property.getValue().equals(propertyValue)) {
					if (returnEqual) return true;
					else return false;
				} else {
					if (returnEqual) return false;
					else return true;
				}
			} else return false;
		} else return false;
	}
}
