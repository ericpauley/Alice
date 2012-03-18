package edu.cmu.cs.stage3.alice.gallery.modeleditor;

class IconManager {
	private static java.util.Dictionary s_classToImageMap = new java.util.Hashtable();
	private static javax.swing.ImageIcon m_defaultIcon;
	private static javax.swing.ImageIcon m_modelIcon;
	private static javax.swing.ImageIcon m_partIcon;
	private static javax.swing.ImageIcon m_methodIcon;
	private static javax.swing.ImageIcon m_questionIcon;
	private static javax.swing.ImageIcon m_propertyIcon;
	static {
		m_defaultIcon = loadImageFromResource( "images/default.gif" );
		m_modelIcon = loadImageFromResource( "images/model.png" );
		m_partIcon = loadImageFromResource( "images/subpart.gif" );
		m_methodIcon = loadImageFromResource( "images/method.png" );
		m_questionIcon = loadImageFromResource( "images/question.png" );
		m_propertyIcon = loadImageFromResource( "images/property.png" );
		s_classToImageMap.put( edu.cmu.cs.stage3.alice.core.light.AmbientLight.class, loadImageFromResource( "images/ambientLight.gif" ) );
		s_classToImageMap.put( edu.cmu.cs.stage3.alice.core.light.DirectionalLight.class, loadImageFromResource( "images/directionalLight.gif" ) );
		s_classToImageMap.put( edu.cmu.cs.stage3.alice.core.light.PointLight.class, loadImageFromResource( "images/pointLight.png" ) );
		//s_classToImageMap.put( edu.cmu.cs.stage3.alice.core.light.SpotLight.class, loadImageFromResource( "images/spotLight.png" ) );
		s_classToImageMap.put( edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera.class, loadImageFromResource( "images/camera.png" ) );
		s_classToImageMap.put( edu.cmu.cs.stage3.alice.core.TextureMap.class, loadImageFromResource( "images/types/edu.cmu.cs.stage3.alice.core.TextureMap.gif" ) );
		s_classToImageMap.put( edu.cmu.cs.stage3.alice.core.Sound.class, loadImageFromResource( "images/types/edu.cmu.cs.stage3.alice.core.Sound.gif" ) );
		s_classToImageMap.put( edu.cmu.cs.stage3.alice.core.Pose.class, loadImageFromResource( "images/types/edu.cmu.cs.stage3.alice.core.Pose.gif" ) );
	}
	static private javax.swing.ImageIcon loadImageFromResource( String name ) {
		java.net.URL resource = edu.cmu.cs.stage3.alice.authoringtool.JAlice.class.getResource( name );
		return new javax.swing.ImageIcon( resource );
	}
	static public javax.swing.ImageIcon lookupIcon( Object o ) {
		javax.swing.ImageIcon icon = (javax.swing.ImageIcon)s_classToImageMap.get( o.getClass() );
		if( icon == null ) {
			if( o instanceof edu.cmu.cs.stage3.alice.core.Model ) {
				edu.cmu.cs.stage3.alice.core.Model model = (edu.cmu.cs.stage3.alice.core.Model)o;
				if( model.isFirstClass.booleanValue() ) {
					return m_modelIcon;
				} else {
					return m_partIcon;
				}
			} else if( o instanceof edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse ) { 
				return m_methodIcon;
			} else if( o instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion ) { 
				return m_methodIcon;
			} else if( o instanceof edu.cmu.cs.stage3.alice.core.Variable ) { 
				return m_propertyIcon;
			} else {
				return m_defaultIcon;
			}
		} else {
			return icon;
		}
	}
}

