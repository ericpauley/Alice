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

public class BehaviorGroupEditor extends edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel implements edu.cmu.cs.stage3.alice.core.event.PropertyListener, edu.cmu.cs.stage3.alice.authoringtool.util.GUIElement, edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeComponentOwner{

    protected edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeComponentElementPanel componentElementPanel;
    protected edu.cmu.cs.stage3.alice.core.Element m_element;
    protected edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty m_components;
    protected String headerText = "Events";
    protected edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel headerPanel;
    protected javax.swing.JPanel containingPanel;
    protected javax.swing.JButton expandButton;
    protected javax.swing.Action expandAction;
    protected javax.swing.JLabel headerLabel;
    protected boolean isExpanded = true;
    protected java.awt.Color backgroundColor = new java.awt.Color(255,255,255);
    protected javax.swing.ImageIcon plus;
    protected javax.swing.ImageIcon minus;
    protected java.awt.Component glue;
    protected edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;
    protected java.awt.event.ActionListener actionListener;
    protected boolean shouldShowLabel = true;


    protected static String IS_EXPANDED_KEY = "edu.cmu.cs.stage3.alice.authoringtool.editors.behavioreditor IS_EXPANDED_KEY";


    public BehaviorGroupEditor(){
        actionListener = new java.awt.event.ActionListener(){
            public void actionPerformed(java.awt.event.ActionEvent e){
                if (isExpanded){
                    reduceComponentElementPanel();
                }
                else{
                    expandComponentElementPanel();
                }
            }
        };
        this.setBorder(null);
        generateGUI();
    }

    public void set(edu.cmu.cs.stage3.alice.core.Element element, edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringToolIn){
        clean();
        m_element = element;
        this.authoringTool = authoringToolIn;
        variableInit();
        startListening();
        setHeaderLabel();
        updateGUI();
        setDropTargets();
    }
    
    public java.util.Vector getBehaviorComponents(){
    	java.util.Vector toReturn = new java.util.Vector();
    	for (int i=0; i < componentElementPanel.getComponentCount(); i++){
    		if (componentElementPanel.getComponent(i) instanceof BasicBehaviorPanel){
    			toReturn.add(componentElementPanel.getComponent(i));
    		}
    	}
    	return toReturn;
    }

    public void setAuthoringTool(edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool){
        this.authoringTool = authoringTool;
        if (componentElementPanel != null){
            componentElementPanel.setAuthoringTool(authoringTool);
        }

    }

    public void setEmptyString(String emptyString){
        if (componentElementPanel != null){
            componentElementPanel.setEmptyString(emptyString);
        }
    }

    protected void setDropTargets(){
        headerLabel.setDropTarget(new java.awt.dnd.DropTarget( headerLabel, componentElementPanel));
        this.setDropTarget(new java.awt.dnd.DropTarget( this, componentElementPanel));
        containingPanel.setDropTarget(new java.awt.dnd.DropTarget( containingPanel, componentElementPanel));
        headerPanel.setDropTarget(new java.awt.dnd.DropTarget( headerPanel, componentElementPanel));
        glue.setDropTarget(new java.awt.dnd.DropTarget( glue, componentElementPanel));
        expandButton.setDropTarget(new java.awt.dnd.DropTarget( expandButton, componentElementPanel));
    }

    protected void variableInit(){
        Object isExpandedValue = m_element.data.get( IS_EXPANDED_KEY );
        headerText = m_element.name.getStringValue();
        if( isExpandedValue instanceof Boolean ) {
            isExpanded = ((Boolean)isExpandedValue).booleanValue();
        }
        if (m_element instanceof edu.cmu.cs.stage3.alice.core.Sandbox){
            edu.cmu.cs.stage3.alice.core.Sandbox proxy = (edu.cmu.cs.stage3.alice.core.Sandbox)m_element;
            m_components = proxy.behaviors;
            componentElementPanel = new edu.cmu.cs.stage3.alice.authoringtool.editors.behaviorgroupseditor.CompositeComponentBehaviorPanel();
            componentElementPanel.set(m_components, this, authoringTool);
            componentElementPanel.setBackground(backgroundColor);
        }
    }

    public java.awt.Component getGrip(){
        return null;
    }

    
	public java.awt.Container getParent(){
        return super.getParent();
    }

    protected void startListening() {
        if (m_element != null){
            m_element.name.addPropertyListener(this);
        }
    }

    protected void stopListening() {
        if (m_element != null){
            m_element.name.removePropertyListener(this);
        }
    }

    public void goToSleep() {
        stopListening();
        if( componentElementPanel != null ) {
            componentElementPanel.goToSleep();
        }
    }

    public void wakeUp() {
        startListening();
        if( componentElementPanel != null ) {
            componentElementPanel.wakeUp();
        }
    }

    
	public void release() {
        super.release();
        if (componentElementPanel != null){
            componentElementPanel.release();
        }
        edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.releaseGUI( this );
    }

    public void clean() {
        goToSleep();
        if( componentElementPanel != null ) {
            if (containingPanel != null){
                containingPanel.remove( componentElementPanel );
            }
            componentElementPanel.release();
            componentElementPanel = null;
        }
    }

    protected void removeAllListening(){
        expandButton.removeActionListener(actionListener);

    }

    public void die() {
        clean();
        removeAllListening();
    }


    public void prePropertyChange( edu.cmu.cs.stage3.alice.core.event.PropertyEvent propertyEvent ) {
    }

    public void propertyChanging( edu.cmu.cs.stage3.alice.core.event.PropertyEvent propertyEvent ) {
    }

    public void propertyChanged( edu.cmu.cs.stage3.alice.core.event.PropertyEvent propertyEvent ){
        if (propertyEvent.getProperty() == m_element.name){
            headerText = m_element.name.getStringValue();
            headerLabel.setText(headerText);
            revalidate();
            repaint();
        }
    }

    
	public void setBackground(java.awt.Color color){
        super.setBackground(color);
        if (containingPanel != null) {containingPanel.setBackground(backgroundColor);}
        if (headerLabel != null) {headerLabel.setBackground(backgroundColor);}
        if (headerPanel != null) {headerPanel.setBackground(backgroundColor);}
        if (componentElementPanel != null) {componentElementPanel.setBackground(backgroundColor);}
    }

    protected void generateGUI(){
        this.setOpaque(false);
        this.setLayout(new java.awt.GridBagLayout());
        plus = new javax.swing.ImageIcon( edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeElementPanel.class.getResource( "images/plus.gif" ) );
        minus = new javax.swing.ImageIcon( edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeElementPanel.class.getResource( "images/minus.gif" ) );
        expandButton = new javax.swing.JButton();
        expandButton.setContentAreaFilled( false );
        expandButton.setMargin( new java.awt.Insets( 0, 0, 0, 0 ) );
        expandButton.setFocusPainted( false );
        expandButton.setBorderPainted( false );
        expandButton.setBorder(null);
        expandButton.addActionListener(actionListener);
        glue = javax.swing.Box.createHorizontalGlue();
        if (headerLabel == null){
            headerLabel = new javax.swing.JLabel();
            setHeaderLabel();
            headerLabel.setOpaque(false);
        }
        if (containingPanel == null){
            containingPanel = new javax.swing.JPanel();
            containingPanel.setLayout(new java.awt.BorderLayout());
            containingPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,0,0,0));
            containingPanel.setOpaque(false);
        }
        if (headerPanel == null){
            headerPanel = new edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel();
            headerPanel.setLayout(new java.awt.GridBagLayout());
            headerPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,0,0,0));
            headerPanel.setOpaque(false);
        }
    }

    public void removeLabel(){
        shouldShowLabel = false;
        containingPanel.remove(headerPanel);
        this.expandComponentElementPanel();
        this.revalidate();
        this.repaint();
    }

    public void addLabel(){
        shouldShowLabel = true;
        containingPanel.add(headerPanel, java.awt.BorderLayout.NORTH);
        this.revalidate();
        this.repaint();
    }

    protected void updateGUI(){
        containingPanel.removeAll();
        headerPanel.removeAll();
        if (isExpanded){
            expandButton.setIcon(minus);
        }
        else{
            expandButton.setIcon(plus);
        }
        headerLabel.setText(headerText);
        headerPanel.add(expandButton, new java.awt.GridBagConstraints(0,0,1,1,0,0,java.awt.GridBagConstraints.NORTHWEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0,0,0,0), 0,0));
        headerPanel.add(headerLabel, new java.awt.GridBagConstraints(1,0,1,1,0,0,java.awt.GridBagConstraints.NORTHWEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0,4,0,2), 0,0));
        headerPanel.add(glue, new java.awt.GridBagConstraints(2,0,1,1,1,1,java.awt.GridBagConstraints.NORTHWEST, java.awt.GridBagConstraints.BOTH, new java.awt.Insets(0,0,0,0), 0,0));
        if (shouldShowLabel){
            containingPanel.add(headerPanel, java.awt.BorderLayout.NORTH);
        }
        if (isExpanded){
            containingPanel.add(componentElementPanel, java.awt.BorderLayout.CENTER);
        }
        this.add(containingPanel, new java.awt.GridBagConstraints(1,0,1,1,1,1,java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.BOTH, new java.awt.Insets(0,0,0,0), 0,0));
        this.setBackground(backgroundColor);
    }


    public boolean isExpanded(){
        if (isExpanded){
            return true;
        }
        return false;
    }

    public void expandComponentElementPanel(){
        if (!isExpanded){
            m_element.data.put( IS_EXPANDED_KEY, Boolean.TRUE );
            isExpanded = true;
            setHeaderLabel();
            expandButton.setIcon(minus);
            containingPanel.add(componentElementPanel, java.awt.BorderLayout.CENTER);
            this.revalidate();
        }
    }

    public void setHeaderLabel(){
        if (headerLabel != null){
            headerLabel.setText(headerText);
        }
    }

    public void reduceComponentElementPanel(){
        if (isExpanded){
            m_element.data.put( IS_EXPANDED_KEY, Boolean.FALSE );
            isExpanded = false;
            setHeaderLabel();
            expandButton.setIcon(plus);
            containingPanel.remove(componentElementPanel);
            this.revalidate();
            //   this.repaint();
        }
    }

    public edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeComponentElementPanel getComponentPanel(){
        return componentElementPanel;
    }

    public void addResponsePanel(javax.swing.JComponent toAdd, int position){
        componentElementPanel.addElementPanel(toAdd, position);
    }

    public edu.cmu.cs.stage3.alice.core.Element getElement(){
        return m_element;
    }




}