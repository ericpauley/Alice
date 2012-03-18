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

public abstract class ForEachElementPanel extends CompositeElementPanel implements edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener{

    protected boolean nameListening = false;
    protected javax.swing.JLabel endHeader;
    protected String endHeaderText;
    protected String middleHeaderText;
    protected javax.swing.JComponent listInput;
    protected javax.swing.JComponent variable;
    protected edu.cmu.cs.stage3.alice.core.property.ListProperty m_list;
    protected edu.cmu.cs.stage3.alice.core.property.VariableProperty m_each;

    public ForEachElementPanel(){
        super();
        backgroundColor = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("ForEachInOrder");
        headerText = "For all";
        endHeaderText = "at a time";
        middleHeaderText = ", one";
    }

    
	protected void variableInit(){
        super.variableInit();
        if (m_element instanceof edu.cmu.cs.stage3.alice.core.response.ForEach){
            edu.cmu.cs.stage3.alice.core.response.ForEach proxy = (edu.cmu.cs.stage3.alice.core.response.ForEach)m_element;
            m_list = proxy.list;
            m_each = proxy.each;
        }
        else if (m_element instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.ForEach){
            edu.cmu.cs.stage3.alice.core.question.userdefined.ForEach proxy = (edu.cmu.cs.stage3.alice.core.question.userdefined.ForEach)m_element;
            m_list = proxy.list;
            m_each = proxy.each;
        }
    }

    
	protected void setDropTargets(){
        super.setDropTargets();
        endHeader.setDropTarget(new java.awt.dnd.DropTarget( endHeader, componentElementPanel));
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

    
	protected void startListening() {
		super.startListening();
        if (m_list != null){
            m_list.addPropertyListener(this);
            if (m_list.get() != null){
                ((edu.cmu.cs.stage3.alice.core.Element)m_list.get()).name.addPropertyListener(this);
                nameListening = true;
            }
        }
		if (m_each != null){
			m_each.addPropertyListener(this);
		}
        listenToChildren(this.m_components);
	}

	
	protected void stopListening() {
        super.stopListening();
		if (m_list != null){
            m_list.removePropertyListener(this);
            if (m_list.get() != null){
                ((edu.cmu.cs.stage3.alice.core.Element)m_list.get()).name.removePropertyListener(this);
                nameListening = true;
            }
        }
        if (m_each != null){
        	m_each.removePropertyListener(this);
        }
		stopListenToChildren(this.m_components);
	}

    public int countPreviousInstances(edu.cmu.cs.stage3.alice.core.Element parent, Object toCheck){
        if (parent == null){
            return 0;
        }
        edu.cmu.cs.stage3.alice.core.property.ListProperty list = null;
        if (parent instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.ForEach){
            list = ((edu.cmu.cs.stage3.alice.core.question.userdefined.ForEach)parent).list;
        }
        if (parent instanceof edu.cmu.cs.stage3.alice.core.response.ForEach){
            list = ((edu.cmu.cs.stage3.alice.core.response.ForEach)parent).list;
        }
        if (list != null && list.get() == toCheck){
            if (parent.getParent() != null && !(parent.getParent() instanceof edu.cmu.cs.stage3.alice.core.Sandbox)){
                return countPreviousInstances(parent.getParent(), toCheck) + 1;
            }
            else{
                return 1;
            }
        }
        if (parent.getParent() != null && !(parent.getParent() instanceof edu.cmu.cs.stage3.alice.core.Sandbox)){
            return countPreviousInstances(parent.getParent(), toCheck);
        }
        else{
            return 0;
        }
    }

    private String makeVariableName(edu.cmu.cs.stage3.alice.core.Element inputList){
        int numPrevious = 0;
        if (m_element.getParent() != null && !(m_element.getParent() instanceof edu.cmu.cs.stage3.alice.core.Sandbox)){
            numPrevious = countPreviousInstances(m_element.getParent(), inputList);
        }
        String toAdd = "";
        if (numPrevious > 0){
            toAdd = "_#"+(numPrevious+1);
        }
        return ("item_from_"+inputList.name.getStringValue()+toAdd);
    }

    
	public void setHeaderLabel(){
        if (headerLabel != null){
            headerLabel.setText(headerText);
        }
        if (endHeader != null){
            endHeader.setText(endHeaderText);
            if (CompositeElementEditor.IS_JAVA){
                if (!isExpanded){
                    endHeader.setText(endHeaderText+" { "+getDots()+" }");
                }
                else{
                    endHeader.setText(endHeaderText+" {");
                }
            }
        }

    }

    
	protected void generateGUI(){
        super.generateGUI();
        if (endHeader == null){
            endHeader = new javax.swing.JLabel(endHeaderText);
        }
    }

    
	protected void restoreDrag(){
        super.restoreDrag();
        this.addDragSourceComponent(endHeader);
    }
    
	protected void updateName(){
		edu.cmu.cs.stage3.alice.core.Variable v = ((edu.cmu.cs.stage3.alice.core.Variable)m_each.getValue());
		v.name.set(makeVariableName( ((edu.cmu.cs.stage3.alice.core.Element)m_list.get()) ));
		headerPanel.remove(variable);
		variable = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getVariableDnDPanel((edu.cmu.cs.stage3.alice.core.Variable)m_each.get());
		headerPanel.add(variable, new java.awt.GridBagConstraints(5,0,1,1,0,0,java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0,2,0,2), 0,0));
	}

    
	protected void updateGUI(){
        super.updateGUI();
        edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory pif = new edu.cmu.cs.stage3.alice.authoringtool.util.SetPropertyImmediatelyFactory(m_list){
            
			public void run(Object v){
                edu.cmu.cs.stage3.alice.core.Element inputList;
                String name;
                if (v instanceof edu.cmu.cs.stage3.alice.core.List){
                }
                else if (v instanceof edu.cmu.cs.stage3.alice.core.Variable){
                    if (!(((edu.cmu.cs.stage3.alice.core.Variable)v).value.get() instanceof edu.cmu.cs.stage3.alice.core.List)){
                        return;
                    }
                }
                else{
                    return;
                }
                super.run(v);
            }
        };
        if (m_list.get() != null){
            setVariable();
        }
        variable = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getVariableDnDPanel((edu.cmu.cs.stage3.alice.core.Variable)m_each.get());
        listInput = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getPropertyViewController(m_list, false, true, edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.shouldGUIOmitPropertyName(m_list), pif);
        headerPanel.remove(glue);
        headerPanel.add(listInput, new java.awt.GridBagConstraints(3,0,1,1,0,0,java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0,2,0,2), 0,0));
        headerPanel.add(new javax.swing.JLabel(middleHeaderText), new java.awt.GridBagConstraints(4,0,1,1,0,0,java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0,2,0,2), 0,0));
        headerPanel.add(variable, new java.awt.GridBagConstraints(5,0,1,1,0,0,java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0,2,0,2), 0,0));
        headerPanel.add(endHeader, new java.awt.GridBagConstraints(6,0,1,1,0,0,java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0,2,0,2), 0,0));
        headerPanel.add(glue, new java.awt.GridBagConstraints(7,0,1,1,1,1,java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.BOTH, new java.awt.Insets(0,0,0,0), 0,0));
    }

    //Texture stuff

    protected static java.awt.image.BufferedImage forEachBackgroundImage;
	protected static java.awt.Dimension forEachBackgroundImageSize = new java.awt.Dimension( -1, -1 );

	protected boolean isTopOccurrance(edu.cmu.cs.stage3.alice.core.Element parent, Object toCheck){
		if (parent == null){
			   return true;
		}
	   edu.cmu.cs.stage3.alice.core.property.ListProperty list = null;
	   if (parent instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.ForEach){
		   list = ((edu.cmu.cs.stage3.alice.core.question.userdefined.ForEach)parent).list;
	   }
	   if (parent instanceof edu.cmu.cs.stage3.alice.core.response.ForEach){
		   list = ((edu.cmu.cs.stage3.alice.core.response.ForEach)parent).list;
	   }
	   if (list != null && list.get() == toCheck){
		   return false;
	   }
		return isTopOccurrance(parent.getParent(), toCheck);
		
	}
	
    protected void setVariableName(){
        edu.cmu.cs.stage3.alice.core.Element l = (edu.cmu.cs.stage3.alice.core.Element)m_list.get();
        String newName = makeVariableName(l);
        ((edu.cmu.cs.stage3.alice.core.Element)m_each.get()).name.set(newName);
    }

    private void setVariable(){
		if (m_each.get() != null && m_list.get() != null){
			edu.cmu.cs.stage3.alice.core.Element l = (edu.cmu.cs.stage3.alice.core.Element)m_list.get();
			Class valueClass = null;
			if (l instanceof edu.cmu.cs.stage3.alice.core.List){
				valueClass = (Class)((edu.cmu.cs.stage3.alice.core.List)l).valueClass.get();
			}
			else if (l instanceof edu.cmu.cs.stage3.alice.core.Variable){
				valueClass = (Class)((edu.cmu.cs.stage3.alice.core.List)((edu.cmu.cs.stage3.alice.core.Variable)l).value.get()).valueClass.get();
			}
			((edu.cmu.cs.stage3.alice.core.Variable)m_each.get()).valueClass.set(valueClass);
			setVariableName();
		}
 
    }

	public void objectArrayPropertyChanging( edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent objectArrayPropertyEvent ){
		
	}
	
	protected void setAllNames(edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty currentContainer, int currentLevel){
		String baseName = "item_from_"+((edu.cmu.cs.stage3.alice.core.Element)m_list.get()).name.getStringValue();
		for (int i=0; i<currentContainer.size(); i++){
			Object list = null;
			edu.cmu.cs.stage3.alice.core.Element var = null;
			if (currentContainer.get(i) instanceof edu.cmu.cs.stage3.alice.core.response.ForEach){
				list = ((edu.cmu.cs.stage3.alice.core.response.ForEach)currentContainer.get(i)).list.get();
				var = ((edu.cmu.cs.stage3.alice.core.Element)((edu.cmu.cs.stage3.alice.core.response.ForEach)currentContainer.get(i)).each.get());
			}
			else if (currentContainer.get(i) instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.ForEach){
				list = ((edu.cmu.cs.stage3.alice.core.question.userdefined.ForEach)currentContainer.get(i)).list.get();
				var = ((edu.cmu.cs.stage3.alice.core.Element)((edu.cmu.cs.stage3.alice.core.question.userdefined.ForEach)currentContainer.get(i)).each.get());
			}
			if (list == m_list.get()){
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
		if (isTopOccurrance(m_element.getParent(), m_list.get())){
			m_each.getElementValue().name.set("item_from_"+((edu.cmu.cs.stage3.alice.core.Element)m_list.get()).name.getStringValue());
			setAllNames(m_components, 2);
		}
	}


    
	public void propertyChanged( edu.cmu.cs.stage3.alice.core.event.PropertyEvent propertyEvent ){
		if (propertyEvent.getProperty() == m_each){
			if (m_each.get() != null){
				edu.cmu.cs.stage3.alice.core.Element l = (edu.cmu.cs.stage3.alice.core.Element)m_list.get();
				Class valueClass = null;
				if (l instanceof edu.cmu.cs.stage3.alice.core.List){
					valueClass = (Class)((edu.cmu.cs.stage3.alice.core.List)l).valueClass.get();
				}
				else if (l instanceof edu.cmu.cs.stage3.alice.core.Variable){
					valueClass = (Class)((edu.cmu.cs.stage3.alice.core.List)((edu.cmu.cs.stage3.alice.core.Variable)l).value.get()).valueClass.get();
				}
				((edu.cmu.cs.stage3.alice.core.Variable)m_each.get()).valueClass.set(valueClass);
				setVariableName();
			}
		}
        if (propertyEvent.getProperty() == m_list){
            if (m_list.get() != null && !nameListening){
                ((edu.cmu.cs.stage3.alice.core.Element)m_list.get()).name.addPropertyListener(this);
                nameListening = true;
            }
            setVariable();
        }
        else if (m_list.get() != null && propertyEvent.getProperty() == ((edu.cmu.cs.stage3.alice.core.Element)m_list.get()).name){
            setVariableName();
        }
        else{
            super.propertyChanged(propertyEvent);
        }
    }

    protected void createBackgroundImage( int width, int height ) {
        forEachBackgroundImageSize.setSize( width, height );
		forEachBackgroundImage = new java.awt.image.BufferedImage( width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB );
		java.awt.Graphics2D g = (java.awt.Graphics2D)forEachBackgroundImage.getGraphics();
		g.addRenderingHints( new java.awt.RenderingHints( java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON ) );
        g.setColor( backgroundColor );
		g.fillRect( 0, 0, width, height );
	}

	protected void paintTextureEffect( java.awt.Graphics g, java.awt.Rectangle bounds ) {
		if( (bounds.width > forEachBackgroundImageSize.width) || (bounds.height > forEachBackgroundImageSize.height) ) {
			createBackgroundImage( bounds.width, bounds.height );
		}
		g.setClip( bounds.x, bounds.y, bounds.width, bounds.height );
		g.drawImage( forEachBackgroundImage, bounds.x, bounds.y, this );
	}

}