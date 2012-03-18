package edu.cmu.cs.stage3.alice.core.summary;

public abstract class ElementSummary {
	private edu.cmu.cs.stage3.alice.core.Element m_element;
	private int m_elementCount = -1;
	protected edu.cmu.cs.stage3.alice.core.Element getElement() {
		return m_element;
	}
	protected void setElement( edu.cmu.cs.stage3.alice.core.Element element ) {
		m_element = element;
	}
	public int getElementCount() {
		if( m_element != null ) {
			return m_element.getElementCount();			
		} else {
			return m_elementCount;
		}
	}
	
	public void encode( java.io.OutputStream os ) {
	}
	public void decode( java.io.InputStream is ) {
	}
}
