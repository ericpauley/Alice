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

public abstract class LoopNElementPanel extends CompositeElementPanel  implements edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener{

    private javax.swing.JLabel timesLabel;
    protected javax.swing.JComponent endInput;
	protected javax.swing.JComponent countInput;
	protected javax.swing.JComponent startInput;
	protected javax.swing.JComponent incrementInput;
	protected javax.swing.JComponent indexTile;
	protected edu.cmu.cs.stage3.alice.core.property.NumberProperty m_start;
	protected edu.cmu.cs.stage3.alice.core.property.NumberProperty m_increment;
    protected edu.cmu.cs.stage3.alice.core.property.NumberProperty m_end;
	protected edu.cmu.cs.stage3.alice.core.property.VariableProperty m_index;
	protected javax.swing.JLabel fromLabel;
	protected javax.swing.JLabel upToLabel;
	protected javax.swing.JLabel incrementLabel;
	protected javax.swing.JPanel complicatedPanel;
	protected javax.swing.JPanel simplePanel;
	protected javax.swing.JLabel complicatedEndBrace;
	protected javax.swing.JButton switchButton;
	protected String toComplicatedString = "show complicated version";
	protected String toSimpleString = "show simple version";
	protected boolean isComplicated = false;
	
	protected static String IS_COMPLICATED_LOOP_KEY = "edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor IS_COMPLICATED_LOOP_KEY";

    public LoopNElementPanel(){
        super();
        headerText = "Loop";
        backgroundColor = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("LoopNInOrder");
    }

	protected int countPreviousInstances(java.awt.Component parent, Object toCheck){
		if (parent == null){
			return 0;
		}
		if (parent instanceof LoopNElementPanel){
			return countPreviousInstances(parent.getParent(), toCheck) + 1;
		}
		return 0;
	}

	protected String getIndexName(){
		String toReturn = "index";
		int count = countPreviousInstances(this.getParent(), this);
		if (count > 0){
			toReturn += String.valueOf(count);
		}
		return toReturn;	
	}
	
    
	protected void variableInit(){
        super.variableInit();
        if (m_element instanceof edu.cmu.cs.stage3.alice.core.response.LoopNInOrder){
            edu.cmu.cs.stage3.alice.core.response.LoopNInOrder proxy = (edu.cmu.cs.stage3.alice.core.response.LoopNInOrder)m_element;
			m_end = proxy.end;
			m_start = proxy.start;
			m_increment = proxy.increment;
			m_index = proxy.index;
			edu.cmu.cs.stage3.alice.core.Variable v = ((edu.cmu.cs.stage3.alice.core.Variable)m_index.getValue());
			v.name.set(getIndexName());
        }
        else if (m_element instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.LoopN){
            edu.cmu.cs.stage3.alice.core.question.userdefined.LoopN proxy = (edu.cmu.cs.stage3.alice.core.question.userdefined.LoopN)m_element;
			m_end = proxy.end;
			m_start = proxy.start;
			m_increment = proxy.increment;
			m_index = proxy.index;
        }
		Object isComplicatedValue = m_element.data.get( IS_COMPLICATED_LOOP_KEY );
		   if( isComplicatedValue instanceof Boolean ) {
			   isComplicated = ((Boolean)isComplicatedValue).booleanValue();
		   }
		   if (isComplicated){
		   		switchButton.setText(toSimpleString);
		   }
		   else{
				switchButton.setText(toComplicatedString);
		   }
    }

    
	protected void startListening() {
		super.startListening();
        if (m_end != null){
			m_end.addPropertyListener(this);
        }
		listenToChildren(this.m_components);
	}

	
	protected void stopListening() {
        super.stopListening();
        if (m_end != null){
			m_end.removePropertyListener(this);
        }
		stopListenToChildren(this.m_components);
	}

	protected void listenToChildren(edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty components){
		if (components != null){
			components.addObjectArrayPropertyListener(this);
			for (int i=0; i<components.size(); i++){
				if (components.get(i) instanceof edu.cmu.cs.stage3.alice.core.response.CompositeResponse){
					edu.cmu.cs.stage3.alice.core.response.CompositeResponse current = (edu.cmu.cs.stage3.alice.core.response.CompositeResponse)components.get(i);
					listenToChildren(current.componentResponses);
				}
				else if (components.get(i) instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Composite){
					edu.cmu.cs.stage3.alice.core.question.userdefined.Composite current = (edu.cmu.cs.stage3.alice.core.question.userdefined.Composite)components.get(i);
					listenToChildren(current.components);
				}
			}
		}
	}

	protected void stopListenToChildren(edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty components){
		if (components != null){
			components.removeObjectArrayPropertyListener(this);
		
			for (int i=0; i<components.size(); i++){
				if (components.get(i) instanceof edu.cmu.cs.stage3.alice.core.response.CompositeResponse){
					edu.cmu.cs.stage3.alice.core.response.CompositeResponse current = (edu.cmu.cs.stage3.alice.core.response.CompositeResponse)components.get(i);
					stopListenToChildren(current.componentResponses);
				}
				else if (components.get(i) instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Composite){
					edu.cmu.cs.stage3.alice.core.question.userdefined.Composite current = (edu.cmu.cs.stage3.alice.core.question.userdefined.Composite)components.get(i);
					stopListenToChildren(current.components);
				}
			}
		}
	}

    
	protected void removeAllListening(){
        super.removeAllListening();
        this.removeDragSourceComponent(timesLabel);
		this.removeDragSourceComponent(complicatedPanel);
		this.removeDragSourceComponent(simplePanel);
		this.removeDragSourceComponent(fromLabel);
		this.removeDragSourceComponent(upToLabel);
		this.removeDragSourceComponent(incrementLabel);
		this.removeDragSourceComponent(complicatedEndBrace);
    }
    
	
	protected void setDropTargets(){
		super.setDropTargets();
		timesLabel.setDropTarget(new java.awt.dnd.DropTarget( timesLabel, componentElementPanel));
		complicatedPanel.setDropTarget(new java.awt.dnd.DropTarget( complicatedPanel, componentElementPanel));
		simplePanel.setDropTarget(new java.awt.dnd.DropTarget( simplePanel, componentElementPanel));
		fromLabel.setDropTarget(new java.awt.dnd.DropTarget( fromLabel, componentElementPanel));
		upToLabel.setDropTarget(new java.awt.dnd.DropTarget( upToLabel, componentElementPanel));
		complicatedEndBrace.setDropTarget(new java.awt.dnd.DropTarget( complicatedEndBrace, componentElementPanel));
		incrementLabel.setDropTarget(new java.awt.dnd.DropTarget( incrementLabel, componentElementPanel));
		indexTile.setDropTarget(new java.awt.dnd.DropTarget( indexTile, componentElementPanel));
					
	}

    
	public void setHeaderLabel(){
        if (headerLabel != null){
            headerLabel.setText(headerText);
            if (CompositeElementEditor.IS_JAVA){
            	if (isComplicated){
					headerLabel.setText("for (int");
            	} else{
            		int start = 0;
            		if (m_start != null){
            			start = m_start.intValue();
            		}
					headerLabel.setText("for (int "+getIndexName()+"="+start+"; "+getIndexName()+"<");
            	}
            }
        }
        if (timesLabel != null){
        	Number number = m_end.getNumberValue();
        	if (number != null && number.intValue() <= 1){
            	timesLabel.setText("time");
        	}
        	else{
				timesLabel.setText("times");
        	}
            if (CompositeElementEditor.IS_JAVA){
				int increment = 1;
				if (m_increment != null){
					increment = m_increment.intValue();
				}
                if (!isExpanded){
                	if (increment == 1){
                    	timesLabel.setText("; "+getIndexName()+"++) { "+getDots()+" }");
                	} else{
						timesLabel.setText("; "+getIndexName()+"+="+increment+") { "+getDots()+" }");
                	}
                }
                else{
                	if (increment == 1){
                    	timesLabel.setText("; "+getIndexName()+"++) {");
                	} else{
						timesLabel.setText("; "+getIndexName()+"+="+increment+") {");
                	}
                }
            }
        }
        if (fromLabel != null){
			if (CompositeElementEditor.IS_JAVA){
				fromLabel.setText("=");
			}
			else{
        		fromLabel.setText("from");
			}
        }
		if (upToLabel != null){
			if (CompositeElementEditor.IS_JAVA){
				upToLabel.setText("; "+getIndexName()+"<");
			}
			else{
        		upToLabel.setText("up to (but not including)");
			}
		}
		if (incrementLabel != null){
			if (CompositeElementEditor.IS_JAVA){
				incrementLabel.setText("; "+getIndexName()+" +=");
			} else{
        		incrementLabel.setText("incrementing by");
			}
		}
		if (complicatedEndBrace != null){
			if (CompositeElementEditor.IS_JAVA){
				complicatedEndBrace.setText("){");
			} else{
				complicatedEndBrace.setText("");
			}
		}

    }

    
	protected void generateGUI(){
        super.generateGUI();
        timesLabel = new javax.swing.JLabel();
     	fromLabel = new javax.swing.JLabel();
		upToLabel = new javax.swing.JLabel();
		incrementLabel = new javax.swing.JLabel();
		complicatedEndBrace = new javax.swing.JLabel();
		complicatedPanel = new javax.swing.JPanel();
		complicatedPanel.setOpaque(false);
		complicatedPanel.setBorder(null);
		complicatedPanel.setLayout(new java.awt.GridBagLayout());
		simplePanel = new javax.swing.JPanel();
		simplePanel.setOpaque(false);
		simplePanel.setBorder(null);
		simplePanel.setLayout(new java.awt.GridBagLayout());
		switchButton = new javax.swing.JButton();
		switchButton.setPreferredSize(new java.awt.Dimension(180,21));
		switchButton.setBackground(new java.awt.Color( 240, 240, 255 ));
		switchButton.setMargin(new java.awt.Insets(2,2,2,2));
		switchButton.setForeground(new java.awt.Color(90,110,110));
		switchButton.setOpaque(true);
		switchButton.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent e){
				if (isComplicated){
					isComplicated = false;
					switchButton.setText(toComplicatedString);
					m_element.data.put( IS_COMPLICATED_LOOP_KEY, Boolean.FALSE );
				}
				else{
					isComplicated = true;
					switchButton.setText(toSimpleString);
					m_element.data.put( IS_COMPLICATED_LOOP_KEY, Boolean.TRUE );
				}
				updateGUI();
			}
		});	
    }

    
	protected void restoreDrag(){
        super.restoreDrag();
        this.addDragSourceComponent(timesLabel);
		this.addDragSourceComponent(complicatedPanel);
		this.addDragSourceComponent(simplePanel);
		this.addDragSourceComponent(fromLabel);
		this.addDragSourceComponent(upToLabel);
		this.addDragSourceComponent(complicatedEndBrace);
		this.addDragSourceComponent(incrementLabel);
    }

	protected void updateName(){
		edu.cmu.cs.stage3.alice.core.Variable v = ((edu.cmu.cs.stage3.alice.core.Variable)m_index.getValue());
		v.name.set(getIndexName());
		complicatedPanel.remove(indexTile);
		indexTile = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getVariableDnDPanel((edu.cmu.cs.stage3.alice.core.Variable)m_index.get());
		complicatedPanel.add(indexTile, new java.awt.GridBagConstraints(0,0,1,1,0,0,java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0,2,0,2), 0,0));
	}

	protected boolean isTopOccurrance(edu.cmu.cs.stage3.alice.core.Element parent){
		if (parent == null){
			   return true;
		}
	   if (parent instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.LoopN){
		   return false;
	   }
	   if (parent instanceof edu.cmu.cs.stage3.alice.core.response.LoopNInOrder){
		   return false;
	   }
		return isTopOccurrance(parent.getParent());
	}


	public void objectArrayPropertyChanging( edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent objectArrayPropertyEvent ){
	
	}
	
	protected void setAllNames(edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty currentContainer, int currentLevel){
		String baseName = "index";
		for (int i=0; i<currentContainer.size(); i++){
			edu.cmu.cs.stage3.alice.core.Element var = null;
			if (currentContainer.get(i) instanceof edu.cmu.cs.stage3.alice.core.response.LoopNInOrder){
				var = ((edu.cmu.cs.stage3.alice.core.Element)((edu.cmu.cs.stage3.alice.core.response.LoopNInOrder)currentContainer.get(i)).index.get());
			}
			else if (currentContainer.get(i) instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.LoopN){
				var = ((edu.cmu.cs.stage3.alice.core.Element)((edu.cmu.cs.stage3.alice.core.question.userdefined.LoopN)currentContainer.get(i)).index.get());
			}
			if (var != null){
				var.name.set(baseName+"_#"+currentLevel);
			}
			if (currentContainer.get(i) instanceof edu.cmu.cs.stage3.alice.core.response.CompositeResponse){
				setAllNames( ((edu.cmu.cs.stage3.alice.core.response.CompositeResponse)currentContainer.get(i)).componentResponses , currentLevel+1);
			}
			else if (currentContainer.get(i) instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Composite){
				setAllNames( ((edu.cmu.cs.stage3.alice.core.question.userdefined.Composite)currentContainer.get(i)).components , currentLevel+1);
			}
		}
	}
	
	public void objectArrayPropertyChanged( edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent objectArrayPropertyEvent ){
		stopListening();
		startListening();
		if (isTopOccurrance(m_element.getParent())){
			edu.cmu.cs.stage3.alice.core.Variable v = ((edu.cmu.cs.stage3.alice.core.Variable)m_index.getValue());
			v.name.set("index");
			setAllNames(m_components, 2);
		}
	}


    
	protected void updateGUI(){
        super.updateGUI();
  
		
        edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory pifCount = new edu.cmu.cs.stage3.alice.authoringtool.util.SetPropertyImmediatelyFactory(m_end);
        countInput = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getPropertyViewController(m_end, true, true, edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.shouldGUIOmitPropertyName(m_end), pifCount);
		edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory pifstart = new edu.cmu.cs.stage3.alice.authoringtool.util.SetPropertyImmediatelyFactory(m_start);
		startInput = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getPropertyViewController(m_start, true, true, edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.shouldGUIOmitPropertyName(m_start), pifstart);
        
		edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory pifInc = new edu.cmu.cs.stage3.alice.authoringtool.util.SetPropertyImmediatelyFactory(m_increment);
		incrementInput = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getPropertyViewController(m_increment, true, true, edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.shouldGUIOmitPropertyName(m_increment), pifInc);
		
		edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory pifEnd = new edu.cmu.cs.stage3.alice.authoringtool.util.SetPropertyImmediatelyFactory(m_end);
		endInput = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getPropertyViewController(m_end, true, true, edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.shouldGUIOmitPropertyName(m_end), pifEnd);		
        
		indexTile = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getVariableDnDPanel((edu.cmu.cs.stage3.alice.core.Variable)m_index.get());
		setHeaderLabel();
		complicatedPanel.removeAll();
		complicatedPanel.add(indexTile, new java.awt.GridBagConstraints(0,0,1,1,0,0,java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0,2,0,2), 0,0));
		complicatedPanel.add(fromLabel, new java.awt.GridBagConstraints(1,0,1,1,0,0,java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0,2,0,2), 0,0));
		complicatedPanel.add(startInput, new java.awt.GridBagConstraints(2,0,1,1,0,0,java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0,2,0,2), 0,0));
		complicatedPanel.add(upToLabel, new java.awt.GridBagConstraints(3,0,1,1,0,0,java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0,2,0,2), 0,0));
		complicatedPanel.add(endInput, new java.awt.GridBagConstraints(4,0,1,1,0,0,java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0,2,0,2), 0,0));
		complicatedPanel.add(incrementLabel, new java.awt.GridBagConstraints(5,0,1,1,0,0,java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0,2,0,2), 0,0));
		complicatedPanel.add(incrementInput, new java.awt.GridBagConstraints(6,0,1,1,0,0,java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0,2,0,2), 0,0));
		complicatedPanel.add(complicatedEndBrace, new java.awt.GridBagConstraints(7,0,1,1,0,0,java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0,2,0,2), 0,0));
		
		simplePanel.removeAll();
		simplePanel.add(countInput, new java.awt.GridBagConstraints(0,0,1,1,0,0,java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0,2,0,2), 0,0));
		simplePanel.add(timesLabel, new java.awt.GridBagConstraints(1,0,1,1,0,0,java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0,2,0,2), 0,0));
		
        headerPanel.remove(glue);
        if (isComplicated){
        	headerPanel.add(complicatedPanel, new java.awt.GridBagConstraints(3,0,1,1,0,0,java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0,2,0,2), 0,0));
        }
        else{
			headerPanel.add(simplePanel, new java.awt.GridBagConstraints(3,0,1,1,0,0,java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0,2,0,2), 0,0));
        }
		
		headerPanel.add(switchButton, new java.awt.GridBagConstraints(4,0,1,1,0,0,java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0,8,0,2), 0,0));
        headerPanel.add(glue, new java.awt.GridBagConstraints(5,0,1,1,1,0,java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.BOTH, new java.awt.Insets(0,0,0,0), 0,0));
    }

    //Texture stuff

    protected static java.awt.image.BufferedImage countLoopBackgroundImage;
	protected static java.awt.Dimension countLoopBackgroundImageSize = new java.awt.Dimension( -1, -1 );

    protected void createBackgroundImage( int width, int height ) {
        countLoopBackgroundImageSize.setSize( width, height );
		countLoopBackgroundImage = new java.awt.image.BufferedImage( width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB );
		java.awt.Graphics2D g = (java.awt.Graphics2D)countLoopBackgroundImage.getGraphics();
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
		if( (bounds.width > countLoopBackgroundImageSize.width) || (bounds.height > countLoopBackgroundImageSize.height) ) {
			createBackgroundImage( bounds.width, bounds.height );
		}
		g.setClip( bounds.x, bounds.y, bounds.width, bounds.height );
		g.drawImage( countLoopBackgroundImage, bounds.x, bounds.y, this );
	}

}