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

public class TriggerBehaviorPanel extends BasicBehaviorPanel implements java.awt.event.ComponentListener{

    javax.swing.JComponent triggerPanel;
    private boolean isSecondLine = false;
    private java.awt.Component topParent = null;
    private java.awt.Component containingPanel = null;
    private boolean shouldCheckSize = true;
    private javax.swing.JLabel lastLabel;

    public TriggerBehaviorPanel(){
        super();
        this.addComponentListener(this);
    }

    public void set(edu.cmu.cs.stage3.alice.core.behavior.TriggerBehavior behavior, edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool) {
        super.set(behavior, authoringTool);
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
		
		edu.cmu.cs.stage3.alice.core.Response response = (edu.cmu.cs.stage3.alice.core.Response)((edu.cmu.cs.stage3.alice.core.behavior.TriggerBehavior)m_behavior).triggerResponse.get();
		toWriteTo.append("<tr>\n<td bgcolor="+getHTMLColorString(bgColor)+" colspan=\"2\">"+strikeStart);
		labelPanel.remove(lastLabel);
		toWriteTo.append(edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getHTMLStringForComponent(labelPanel));
		labelPanel.add(lastLabel, new java.awt.GridBagConstraints(labelPanel.getComponentCount(),0,1,1,0,0,java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0,2,0,0), 0,0));
		toWriteTo.append(strikeEnd+"</td>\n</tr>\n");
		toWriteTo.append("<tr>\n<td bgcolor="+getHTMLColorString(bgColor)+" align=\"right\">"+strikeStart+"<b>Do:</b>"+strikeEnd+"</td>\n");
		toWriteTo.append("<td bgcolor="+getHTMLColorString(bgColor)+" width=\"100%\"><table cellpadding=\"2\" cellspacing=\"0\" width=\"100%\">");
		toWriteTo.append(edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getHTMLStringForComponent(triggerPanel));
		toWriteTo.append("</table>\n</td>\n</tr>\n");
	}

    
	protected void removeAllListening(){
        super.removeAllListening();
        if (m_containingPanel != null ){
            this.removeDragSourceComponent(m_containingPanel);
            m_containingPanel.removeMouseListener(behaviorMouseListener);
        }
        if (triggerPanel != null){
            this.removeDragSourceComponent(triggerPanel);
            triggerPanel.removeMouseListener(behaviorMouseListener);
            this.removeComponentListener(this);
            triggerPanel.removeComponentListener(this);
        }
    }

    
	public void release(){
        super.release();
    }

    
	protected void guiInit(){
        super.guiInit();
        if (m_containingPanel == null){
            m_containingPanel = new edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel();
            m_containingPanel.setBorder(null);
            m_containingPanel.setLayout(new java.awt.GridBagLayout());
            m_containingPanel.setBackground(COLOR);
            m_containingPanel.addMouseListener(behaviorMouseListener);
        }
        this.remove(m_containingPanel);
        this.addDragSourceComponent(m_containingPanel);
        m_containingPanel.removeAll();
        if (labelPanel == null){
            labelPanel = new edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel();
            labelPanel.setBorder(null);
            labelPanel.setLayout(new java.awt.GridBagLayout());
            labelPanel.setBackground(COLOR);
            labelPanel.addMouseListener(behaviorMouseListener);
        }
        this.addDragSourceComponent(labelPanel);
        labelPanel.removeAll();
        this.setBackground(COLOR);
        buildLabel(labelPanel);
        int x = labelPanel.getComponentCount();
		lastLabel = new javax.swing.JLabel();
        if (isSecondLine){
        	lastLabel.setText(",");
        }
        else{
			lastLabel.setText(",  do");
        }
		labelPanel.add(lastLabel, new java.awt.GridBagConstraints(x,0,1,1,0,0,java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0,2,0,0), 0,0));
        edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory triggerFactory = new edu.cmu.cs.stage3.alice.authoringtool.util.SetPropertyImmediatelyFactory(((edu.cmu.cs.stage3.alice.core.behavior.TriggerBehavior)m_behavior).triggerResponse);
        triggerPanel = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getPropertyViewController(((edu.cmu.cs.stage3.alice.core.behavior.TriggerBehavior)m_behavior).triggerResponse, false, true, true, triggerFactory);
        triggerPanel.addComponentListener(this);
        m_containingPanel.add(labelPanel, new java.awt.GridBagConstraints(0,0,2,1,0,0,java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0,2,0,2), 0,0));
        if (isSecondLine){
            m_containingPanel.add(new javax.swing.JLabel("do "), new java.awt.GridBagConstraints(0,1,1,1,0,0,java.awt.GridBagConstraints.EAST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0,ConditionalBehaviorPanel.INDENT,0,2), 0,0));
            m_containingPanel.add(triggerPanel, new java.awt.GridBagConstraints(1,1,1,1,1,0,java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0,2,0,2), 0,0));
        }
        else{
            m_containingPanel.add(triggerPanel, new java.awt.GridBagConstraints(x+1,0,1,1,1,0,java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0,2,0,2), 0,0));
        }

        this.add(m_containingPanel, java.awt.BorderLayout.CENTER);
        this.repaint();
        this.revalidate();
    }

    private java.awt.Component getTopParent(java.awt.Component c){
        if (c == null){
            return null;
        }
        if (c instanceof BehaviorGroupsEditor){
            return c;
        }
        return getTopParent(c.getParent());
    }

    private void recheckLength(){
        if (containingPanel == null){
            topParent = getTopParent(this.getParent());
            if (topParent != null){
				topParent.addComponentListener(this);
            }
            if (topParent instanceof BehaviorGroupsEditor){
            	containingPanel = ((BehaviorGroupsEditor)topParent).getContainingPanel();
            }
        }
        if (topParent == null){
            return;
        }
		int calculatedWidth = labelPanel.getWidth() + triggerPanel.getWidth()+45+BehaviorGroupsEditor.SPACE;
		if (isSecondLine){
			calculatedWidth += 23;
		}
        
        if (isSecondLine){
            if (calculatedWidth < topParent.getWidth()){
                isSecondLine = false;
                guiInit();
            }
        }
        else{
            if (calculatedWidth > topParent.getWidth()){
                isSecondLine = true;
                guiInit();
            }
        }

    }

    public void componentHidden(java.awt.event.ComponentEvent e){}

    public void componentMoved(java.awt.event.ComponentEvent e){
    }

    public void componentResized(java.awt.event.ComponentEvent e){
        if (shouldCheckSize){
            recheckLength();
        }
    }

    public void componentShown(java.awt.event.ComponentEvent e) {
    }

}