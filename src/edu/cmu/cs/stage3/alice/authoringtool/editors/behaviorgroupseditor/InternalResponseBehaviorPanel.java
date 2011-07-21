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

public class InternalResponseBehaviorPanel extends BasicBehaviorPanel {

    java.awt.Component automaticPanel;

    public InternalResponseBehaviorPanel(){
        super();
    }
    public void set(edu.cmu.cs.stage3.alice.core.behavior.InternalResponseBehavior behavior, edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool) {
        super.set(behavior, authoringTool);
    }

    
	protected void removeAllListening(){
        super.removeAllListening();
        if (m_containingPanel != null ){
            this.removeDragSourceComponent(m_containingPanel);
            m_containingPanel.removeMouseListener(behaviorMouseListener);
        }
    }

    
	protected void guiInit(){
        if (m_containingPanel == null){
            m_containingPanel = new edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel();
            m_containingPanel.setLayout(new java.awt.GridBagLayout());
            m_containingPanel.setBackground(COLOR);
            m_containingPanel.addMouseListener(behaviorMouseListener);
            m_containingPanel.setBorder(null);
        }
        this.remove(m_containingPanel);
        this.addDragSourceComponent(m_containingPanel);
        m_containingPanel.removeAll();
        if (labelPanel == null){
            labelPanel = new edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel();
            labelPanel.setLayout(new java.awt.GridBagLayout());
            labelPanel.setBackground(COLOR);
            labelPanel.addMouseListener(behaviorMouseListener);
            labelPanel.setBorder(null);
        }
        this.addDragSourceComponent(labelPanel);
        labelPanel.removeAll();
        this.setBackground(COLOR);
        int base = 0;
        buildLabel(labelPanel);
        java.awt.Component glue = javax.swing.Box.createHorizontalGlue();
        this.addDragSourceComponent(glue);
        m_containingPanel.add(labelPanel, new java.awt.GridBagConstraints(0,0,1,1,0,0,java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.BOTH, new java.awt.Insets(0,0,0,0), 0,0));
        m_containingPanel.add(glue, new java.awt.GridBagConstraints(1,0,1,1,1,0,java.awt.GridBagConstraints.EAST, java.awt.GridBagConstraints.BOTH, new java.awt.Insets(0,0,0,0), 0,0));
        this.add(m_containingPanel, java.awt.BorderLayout.CENTER);
        this.repaint();
        this.revalidate();
    }
}