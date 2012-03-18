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

public class ForEachPanel extends edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.ForEachElementPanel {

     public ForEachPanel(){
        super();
    }

    
	protected java.awt.Color getCustomBackgroundColor(){
        return edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("ForEachInOrder");
    }


    public void set(edu.cmu.cs.stage3.alice.core.question.userdefined.ForEach r, edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringToolIn){
        super.set(r, authoringToolIn);
    }

}