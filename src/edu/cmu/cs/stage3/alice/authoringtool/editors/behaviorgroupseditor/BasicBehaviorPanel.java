/*
 * Copyright (c) 1999-2003, Carnegie Mellon University. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 
 * 3. Products derived from the software may not be called "Alice",
 *    nor may "Alice" appear in their name, without prior written
 *    permission of Carnegie Mellon University.
 * 
 * 4. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *    "This product includes software developed by Carnegie Mellon University"
 */

package edu.cmu.cs.stage3.alice.authoringtool.editors.behaviorgroupseditor;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public abstract class BasicBehaviorPanel extends edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel implements edu.cmu.cs.stage3.alice.authoringtool.util.GUIElement, edu.cmu.cs.stage3.alice.core.event.PropertyListener {

    public static final java.awt.Color COLOR = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("behavior");

    protected javax.swing.JPopupMenu popUpMenu;

    protected edu.cmu.cs.stage3.alice.core.Behavior m_behavior = null;
    protected edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel m_containingPanel;
    protected edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel labelPanel;
    protected edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;
    protected String typeString;

    protected final java.awt.event.MouseListener behaviorMouseListener = new edu.cmu.cs.stage3.alice.authoringtool.util.CustomMouseAdapter() {
        
		protected void popupResponse( java.awt.event.MouseEvent e ) {
            popUpMenu.show( e.getComponent(), e.getX(), e.getY() );
            edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.ensurePopupIsOnScreen( popUpMenu );
        }
    };

    public BasicBehaviorPanel(){
        this.addMouseListener(behaviorMouseListener);
        grip.addMouseListener(behaviorMouseListener);
    }

    public void set(edu.cmu.cs.stage3.alice.core.Behavior behavior, String type, edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool) {
        clean();
        super.reset();
        this.authoringTool = authoringTool;
        m_behavior = behavior;
        setTransferable( new edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable( behavior ) ); // added by JFP
        typeString = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(m_behavior.getClass());
        m_behavior.isEnabled.addPropertyListener(this);
        popUpMenu = createPopup();
        guiInit();
    }

    public void set(edu.cmu.cs.stage3.alice.core.Behavior behavior, edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool) {
        clean();
        super.reset();
        this.authoringTool = authoringTool;
        m_behavior = behavior;
        setTransferable( new edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable( behavior ) ); // added by JFP
        typeString = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(m_behavior.getClass());
        m_behavior.isEnabled.addPropertyListener(this);
        popUpMenu = createPopup();
        guiInit();
    }
	
	protected String getHTMLColorString(java.awt.Color color){
		String r = Integer.toHexString(color.getRed());
		String g = Integer.toHexString(color.getGreen());
		String b = Integer.toHexString(color.getBlue());
	
		if (r.length() == 1){
			r = "0"+r;
		}
		if (g.length() == 1){
			g = "0"+g;
		}
		if (b.length() == 1){
			b = "0"+b;
		}
		return new String("#"+r+g+b);
	}
    
	public void getHTML(StringBuffer toWriteTo, boolean useColor){
		java.awt.Color bgColor = COLOR;
		String strikeStart = "";
		String strikeEnd = "";
		if (!m_behavior.isEnabled.booleanValue()){
			bgColor = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("disabledHTML");
			strikeStart = "<strike><font color=\""+getHTMLColorString(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("disabledHTMLText"))+"\">";
			strikeEnd = "</font></strike>";
		}
		toWriteTo.append("<tr>\n<td bgcolor="+getHTMLColorString(bgColor)+"><b>"+strikeStart+
											edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getHTMLStringForComponent(this)+"</b>"+strikeEnd+"</td>\n</tr>\n");
	}

	public edu.cmu.cs.stage3.alice.core.Behavior getBehavior(){
		return m_behavior;
	}

    protected javax.swing.JPopupMenu createPopup(){
        java.util.Vector popupStructure =  new java.util.Vector();
        // popupStructure.add( edu.cmu.cs.stage3.alice.authoringtool.util.ElementPopupUtilities.MakeCopyRunnable.class );
        popupStructure.add( edu.cmu.cs.stage3.alice.authoringtool.util.ElementPopupUtilities.DeleteRunnable.class );
        java.util.Vector coerceStructure = edu.cmu.cs.stage3.alice.authoringtool.util.ElementPopupUtilities.makeCoerceToStructure(m_behavior);
        if (coerceStructure != null && coerceStructure.size() > 0){
            popupStructure.add( coerceStructure.elementAt(0));
        }
        edu.cmu.cs.stage3.util.StringObjectPair commentOut = null;
        if (m_behavior.isEnabled.booleanValue()){
            Runnable setEnabled = new Runnable(){
                public void run(){
                    m_behavior.isEnabled.set(false);
                    BasicBehaviorPanel.this.repaint();
                }
            };
            commentOut = new edu.cmu.cs.stage3.util.StringObjectPair("disable", setEnabled);
        }
        else{
            Runnable setEnabled = new Runnable(){
                public void run(){
                    m_behavior.isEnabled.set(true);
                    BasicBehaviorPanel.this.repaint();
                }
            };
            commentOut = new edu.cmu.cs.stage3.util.StringObjectPair("enable", setEnabled);
        }
        popupStructure.add(commentOut);
        return edu.cmu.cs.stage3.alice.authoringtool.util.ElementPopupUtilities.makeElementPopupMenu( m_behavior, popupStructure );
    }

    public void goToSleep() {
        if (m_behavior != null){
            m_behavior.isEnabled.removePropertyListener(this);
        }
    }

    public void wakeUp() {
        if (m_behavior != null){
            m_behavior.isEnabled.addPropertyListener(this);
        }
    }

    
	public void release() {
        super.release();
        releasePanel(m_containingPanel);
        edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.releaseGUI( this );
    }

    public void clean() {
        goToSleep();
        popUpMenu = null;
        if( m_containingPanel != null ) {
            releasePanel(m_containingPanel);
            m_containingPanel.removeAll();
        }
    }

    protected void removeAllListening(){
        setTransferable(null);
        this.removeMouseListener(behaviorMouseListener);
        grip.removeMouseListener(behaviorMouseListener);
    }

    public void die() {
        clean();
        removeAllListening();
        this.removeAll();
        m_behavior = null;
        m_containingPanel = null;
        authoringTool = null;
    }

    protected void releasePanel(java.awt.Container toRelease){
        if (toRelease != null){
            for (int i=0; i<toRelease.getComponentCount(); i++){
                if (toRelease.getComponent(i) instanceof edu.cmu.cs.stage3.alice.authoringtool.util.Releasable){
                    ((edu.cmu.cs.stage3.alice.authoringtool.util.Releasable)toRelease.getComponent(i)).release();
                }
            }
        }
    }
	
    public static void buildLabel(javax.swing.JPanel container, String typeString){
        int locationLeft, locationRight;
        int oldLocation = 0;
        int insertX = 0;
        String currentSubstring;
        locationLeft = typeString.indexOf('<',0);
        locationRight = typeString.indexOf('>',locationLeft);
        while (locationLeft > -1 && locationRight > -1 && oldLocation < typeString.length()){
            currentSubstring = typeString.substring(oldLocation, locationLeft);
            String key = typeString.substring(locationLeft+1, locationRight);
            java.awt.Component toAdd;
            javax.swing.Icon icon = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getIconForValue(key);
            if (icon == null){
                toAdd = new edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel();
                ((edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel)toAdd).setLayout( new java.awt.BorderLayout( 0, 0 ) );
                ((edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel)toAdd).setBackground( COLOR );
                ((edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel)toAdd).setBorder( javax.swing.BorderFactory.createCompoundBorder( ((edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel)toAdd).getBorder(), javax.swing.BorderFactory.createEmptyBorder( 0, 2, 0, 0 ) ) );
                javax.swing.JLabel expressionLabel = new javax.swing.JLabel(key);
                ((edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel)toAdd).add(expressionLabel, java.awt.BorderLayout.CENTER);
            }
            else{
                toAdd = new javax.swing.JLabel(icon);
            }
            container.add(new javax.swing.JLabel(currentSubstring), new java.awt.GridBagConstraints(insertX,0,1,1,0,0,java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0,2,0,0), 0,0));
            insertX++;
            container.add(toAdd, new java.awt.GridBagConstraints(insertX,0,1,1,0,0,java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0,2,0,0) , 0,0));
            insertX++;
            oldLocation = locationRight+1;
            locationLeft = typeString.indexOf('<',oldLocation);
            locationRight = typeString.indexOf('>',locationLeft);
        }
        if (oldLocation < typeString.length()){
            currentSubstring = typeString.substring(oldLocation, typeString.length());
            container.add(new javax.swing.JLabel(currentSubstring), new java.awt.GridBagConstraints(insertX,0,1,1,0,0,java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0,2,0,0), 0,0));
            insertX++;
        }
    }

    protected void buildLabel(javax.swing.JPanel container){
        int locationLeft, locationRight;
        int oldLocation = 0;
        int insertX = 0;
        String currentSubstring;
        locationLeft = typeString.indexOf('<',0);
        locationRight = typeString.indexOf('>',locationLeft);
        while (locationLeft > -1 && locationRight > -1 && oldLocation < typeString.length()){
            currentSubstring = typeString.substring(oldLocation, locationLeft);
            String key = typeString.substring(locationLeft+1, locationRight);
            java.awt.Component toAdd;
            final edu.cmu.cs.stage3.alice.core.Property prop = m_behavior.getPropertyNamed(key);
            if (prop != null){
                edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory propPIF = new edu.cmu.cs.stage3.alice.authoringtool.util.SetPropertyImmediatelyFactory(prop);
                boolean shouldAllowExpressions = true;
                Class desiredValueClass = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.getDesiredValueClass(prop);
                if ( edu.cmu.cs.stage3.alice.core.Response.class.isAssignableFrom( desiredValueClass ) || prop.getName().equalsIgnoreCase("keyCode")){
                	shouldAllowExpressions = false;
                }
                toAdd = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getPropertyViewController(prop, true, shouldAllowExpressions ,edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.shouldGUIOmitPropertyName(prop), propPIF);
            }
            else{
                toAdd = new javax.swing.JLabel(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getIconForValue(key));
                if (toAdd == null){
                    toAdd = new javax.swing.JLabel("(no image)");
                }
            }
            container.add(new javax.swing.JLabel(currentSubstring), new java.awt.GridBagConstraints(insertX,0,1,1,0,0,java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0,2,0,0), 0,0));
            insertX++;
            container.add(toAdd, new java.awt.GridBagConstraints(insertX,0,1,1,0,0,java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0,2,0,0) , 0,0));
            insertX++;
            oldLocation = locationRight+1;
            locationLeft = typeString.indexOf('<',oldLocation);
            locationRight = typeString.indexOf('>',locationLeft);
        }
        if (oldLocation < typeString.length()){
            currentSubstring = typeString.substring(oldLocation, typeString.length());
            container.add(new javax.swing.JLabel(currentSubstring), new java.awt.GridBagConstraints(insertX,0,1,1,0,0,java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0,2,0,0), 0,0));
            insertX++;
        }
    }

    public void prePropertyChange( edu.cmu.cs.stage3.alice.core.event.PropertyEvent propertyEvent ){
    }

    public void propertyChanging( edu.cmu.cs.stage3.alice.core.event.PropertyEvent propertyEvent){
    }

    
	public void paintForeground( java.awt.Graphics g ) {
        super.paintForeground( g );
        if( !m_behavior.isEnabled.booleanValue() ) {
            java.awt.Rectangle bounds = new java.awt.Rectangle(0,0,this.getWidth(), this.getHeight());
            edu.cmu.cs.stage3.alice.authoringtool.util.GUIEffects.paintDisabledEffect( g, bounds );
        }
    }

    public void propertyChanged( edu.cmu.cs.stage3.alice.core.event.PropertyEvent propertyEvent ){
        //stopListening();
        popUpMenu = createPopup();
        guiInit();
    }

    protected void guiInit(){
    }

}