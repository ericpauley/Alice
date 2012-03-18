package edu.cmu.cs.stage3.alice.core.summary;

public class ModelSummary extends ElementSummary {
	private String m_name;
	private String m_modeledBy;
	private String m_paintedBy;
	private int m_partCount;
	private String m_physicalSizeDescription;
	private String[] m_methodNames;
	private String[] m_questionNames;
	private String[] m_soundNames;

	private edu.cmu.cs.stage3.alice.core.Model getModel() {
		return (edu.cmu.cs.stage3.alice.core.Model)getElement();
	}
	public void setModel( edu.cmu.cs.stage3.alice.core.Model model ) {
		super.setElement( model );
		m_name = null;
		m_modeledBy = null;
		m_paintedBy = null;
		m_partCount = -1;
		m_physicalSizeDescription = null;
		m_methodNames = null;
		m_questionNames = null;
		m_soundNames = null;
	}
	
	public String getName() {
		if( getModel() != null ) {
			return getModel().name.getStringValue();
		} else {
			return m_name;
		}
	}
	public String getModeledBy() {
		if( getModel() != null ) {
			return (String)getModel().data.get( "modeled by" );
		} else {
			return m_modeledBy;
		}
	}
	public String getPaintedBy() {
		if( getModel() != null ) {
			return (String)getModel().data.get( "painted by" );
		} else {
			return m_paintedBy;
		}
	}
	public int getPartCount() {
		return m_partCount;
	}
	//todo: getBoundingBox?
	public String getPhysicalSizeDescription() {
		return m_physicalSizeDescription;
	}
	public String[] getMethodNames() {
		if( getModel() != null ) {
			String[] methodNames = new String[ getModel().responses.size() ];
			for( int i=0; i<methodNames.length; i++ ) {
				edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse methodI = (edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse)getModel().responses.get( i );
				methodNames[ i ] = methodI.name.getStringValue(); 
			}
			return methodNames;
		} else {
			return m_methodNames;
		}
	}
	public String[] getQuestionNames() {
		return m_questionNames;
	}
	public String[] getSoundNames() {
		return m_soundNames;
	}
}
