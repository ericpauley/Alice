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

package edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author David Culyba
 * @version 1.0
 */

public abstract class WhileElementPanel extends CompositeElementPanel {
    protected javax.swing.JComponent conditionalInput;
    protected javax.swing.JLabel endHeader;
    protected edu.cmu.cs.stage3.alice.core.property.BooleanProperty m_condition;

    public WhileElementPanel(){
        super();
        headerText = "While";
        backgroundColor = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("WhileLoopInOrder");
    }

     
	protected void variableInit(){
        super.variableInit();
        if (m_element instanceof edu.cmu.cs.stage3.alice.core.response.WhileLoopInOrder){
            edu.cmu.cs.stage3.alice.core.response.WhileLoopInOrder proxy = (edu.cmu.cs.stage3.alice.core.response.WhileLoopInOrder)m_element;
            m_condition = proxy.condition;
        }
        else if (m_element instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.While){
            edu.cmu.cs.stage3.alice.core.question.userdefined.While proxy = (edu.cmu.cs.stage3.alice.core.question.userdefined.While)m_element;
            m_condition = proxy.condition;
        }
    }

    
	protected void startListening() {
		super.startListening();
        if (m_condition != null){
            m_condition.addPropertyListener(this);
        }
	}

	
	protected void stopListening() {
        super.stopListening();
        if (m_condition != null){
    		m_condition.removePropertyListener(this);
        }
	}

    
	public void setHeaderLabel(){
        if (headerLabel != null){
            headerLabel.setText(headerText);
            if (CompositeElementEditor.IS_JAVA){
                headerLabel.setText("while (");
            }
        }
        if (endHeader != null){
            endHeader.setText("");
            if (CompositeElementEditor.IS_JAVA){
                if (!isExpanded){
                    endHeader.setText(") { "+getDots()+" }");
                }
                else{
                    endHeader.setText(") {");
                }
            }
        }

    }

    
	protected void generateGUI(){
        super.generateGUI();
        if (endHeader == null){
            endHeader = new javax.swing.JLabel();
        }
    }

    
	protected void restoreDrag(){
        super.restoreDrag();
        this.addDragSourceComponent(endHeader);
    }

    
	protected void updateGUI(){
        super.updateGUI();
        edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory pif = new edu.cmu.cs.stage3.alice.authoringtool.util.SetPropertyImmediatelyFactory(m_condition);
        conditionalInput = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getPropertyViewController(m_condition, true, true, edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.shouldGUIOmitPropertyName(m_condition), pif);
        headerPanel.remove(glue);
        headerPanel.add(conditionalInput, new java.awt.GridBagConstraints(3,0,1,1,0,0,java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0,2,0,2), 0,0));
        headerPanel.add(endHeader, new java.awt.GridBagConstraints(4,0,1,1,0,0,java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0,2,0,2), 0,0));
        headerPanel.add(glue, new java.awt.GridBagConstraints(5,0,1,1,1,0,java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.BOTH, new java.awt.Insets(0,0,0,0), 0,0));
    }

    //Texture stuff

    protected static java.awt.image.BufferedImage whileLoopBackgroundImage;
	protected static java.awt.Dimension whileLoopBackgroundImageSize = new java.awt.Dimension( -1, -1 );

    protected void createBackgroundImage( int width, int height ) {
        whileLoopBackgroundImageSize.setSize( width, height );
		whileLoopBackgroundImage = new java.awt.image.BufferedImage( width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB );
		java.awt.Graphics2D g = (java.awt.Graphics2D)whileLoopBackgroundImage.getGraphics();
		g.addRenderingHints( new java.awt.RenderingHints( java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON ) );
        g.setColor( backgroundColor );
		g.fillRect( 0, 0, width, height );
     /*   g.setColor( backgroundLineColor );
		int spacing = 10;
		for( int x = 0; x <= width; x += spacing ) {
			g.drawLine( x, 0, x, height );
		}*/
	}

	protected void paintTextureEffect( java.awt.Graphics g, java.awt.Rectangle bounds ) {
		if( (bounds.width > whileLoopBackgroundImageSize.width) || (bounds.height > whileLoopBackgroundImageSize.height) ) {
			createBackgroundImage( bounds.width, bounds.height );
		}
		g.setClip( bounds.x, bounds.y, bounds.width, bounds.height );
		g.drawImage( whileLoopBackgroundImage, bounds.x, bounds.y, this );
	}

}