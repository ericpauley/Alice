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

package edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor;


/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public abstract class CompositeResponsePanel extends edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeElementPanel{

    protected edu.cmu.cs.stage3.alice.core.response.CompositeResponse m_response;

    public CompositeResponsePanel(){
        super();
        headerText = "Composite Response";
    }

    public void set(edu.cmu.cs.stage3.alice.core.response.CompositeResponse response, edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringToolIn){
        m_response = (edu.cmu.cs.stage3.alice.core.response.CompositeResponse)m_element;
        super.set(response, authoringToolIn);
    }

    public edu.cmu.cs.stage3.alice.core.Response getResponse(){
        return m_response;
    }

}