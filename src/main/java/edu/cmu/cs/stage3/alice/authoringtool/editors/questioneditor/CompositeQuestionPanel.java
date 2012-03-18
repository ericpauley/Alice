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

package edu.cmu.cs.stage3.alice.authoringtool.editors.questioneditor;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author David Culyba
 * @version 1.0
 */

public abstract class CompositeQuestionPanel extends edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeElementPanel{

    protected edu.cmu.cs.stage3.alice.core.question.userdefined.Composite m_question;

    public CompositeQuestionPanel(){
        super();
        headerText = "Composite Question";
    }

    
	public void set(edu.cmu.cs.stage3.alice.core.Element question, edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringToolIn){
        m_question = (edu.cmu.cs.stage3.alice.core.question.userdefined.Composite)m_element;
        super.set(question, authoringToolIn);
    }

    public edu.cmu.cs.stage3.alice.core.question.userdefined.Composite getQuestion(){
        return m_question;
    }

}