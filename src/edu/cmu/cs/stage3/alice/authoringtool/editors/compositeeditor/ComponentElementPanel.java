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

public class ComponentElementPanel extends edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel{

    protected edu.cmu.cs.stage3.alice.core.Element m_element;

    public ComponentElementPanel(){
        this.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,0,0,0));
        this.remove(grip);
    }

    public void set(edu.cmu.cs.stage3.alice.core.Element element) {
        m_element = element;
        try{
            this.add(edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getGUI(m_element));
        }
        catch (Exception e){
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "An error occurred while creating the graphics component for this object.", e );
        }
       // edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel added = (edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel)this.getComponent(0);
    }
    
	protected java.awt.Color getCustomBackgroundColor(){
		if (this.getComponentCount() > 0){
			return this.getComponent(0).getBackground();
		}
		return java.awt.Color.white;
	}
	
	public boolean isDisabled(){
		if (m_element instanceof edu.cmu.cs.stage3.alice.core.Response){
			return ((edu.cmu.cs.stage3.alice.core.Response)m_element).isCommentedOut.booleanValue();
		} else if (m_element instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Component){
			return ((edu.cmu.cs.stage3.alice.core.question.userdefined.Component)m_element).isCommentedOut.booleanValue();
		} else{
			return false;
		}
	}

    public void goToSleep() {
	}

	public void wakeUp() {
	}

	public void clean() {
        removeAll();
	}

	public void die() {
		clean();
	}

    public edu.cmu.cs.stage3.alice.core.Element getElement(){
        return m_element;
    }
    
	

}