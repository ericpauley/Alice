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

public class QuestionEditor extends edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeElementEditor{


    public final String editorName = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.QUESTION_STRING+" Editor";

    protected edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel ifElsePrototype;
    protected edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel loopNPrototype;
    protected edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel forEachPrototype;
    protected edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel scriptPrototype;
    protected edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel whilePrototype;
    protected edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel commentPrototype;
    protected javax.swing.JComponent printPrototype;
    protected edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel returnPrototype;
    protected edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel mathPrototype;

    public final static java.awt.datatransfer.DataFlavor componentReferenceFlavor = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReferenceFlavorForClass( edu.cmu.cs.stage3.alice.core.question.userdefined.Component.class );

    public QuestionEditor(){
        super();
    }

    public void setObject(edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion toEdit){
        clearAllListening();
        this.elementBeingEdited = toEdit;
        updateGui();
    }

    
	protected java.awt.Color getEditorColor(){
        return edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("userDefinedQuestionEditor");
    }

    
	protected edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.MainCompositeElementPanel createElementTree(edu.cmu.cs.stage3.alice.core.Element selected){
        if (selected instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion){
            MainCompositeQuestionPanel toReturn = new MainCompositeQuestionPanel();
            toReturn.set(selected, authoringTool);
            return toReturn;
        }
        return null;
    }


    
	protected void initPrototypes(){

        String ifElseString = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(edu.cmu.cs.stage3.alice.core.response.IfElseInOrder.class);
        String loopNString = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(edu.cmu.cs.stage3.alice.core.response.LoopNInOrder.class);
        String forEachString = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(edu.cmu.cs.stage3.alice.core.response.ForEachInOrder.class);
        String scriptString = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(edu.cmu.cs.stage3.alice.core.response.ScriptResponse.class);
        String whileString = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(edu.cmu.cs.stage3.alice.core.response.WhileLoopInOrder.class);
        String commentString = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(edu.cmu.cs.stage3.alice.core.response.Comment.class);
        String returnString = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(edu.cmu.cs.stage3.alice.core.question.userdefined.Return.class);
        String mathString = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue("+ - * /");


        java.awt.Color LOOP_N_COLOR = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("LoopNInOrder");
        java.awt.Color IF_ELSE_COLOR = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("IfElseInOrder");
        java.awt.Color FOR_EACH_COLOR = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("ForEachInOrder");
        java.awt.Color SCRIPT_COLOR = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("ScriptResponse");
        java.awt.Color COMMENT_COLOR = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("Comment");
        java.awt.Color COMMENT_FOREGROUND = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("commentForeground");
        java.awt.Color WHILE_COLOR = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("WhileLoopInOrder");
        java.awt.Color MATH_COLOR = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("question");
        java.awt.Color RETURN_COLOR = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("Return");
		java.awt.Color PRINT_COLOR = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("Print");

        ifElsePrototype = new edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel();
        //ifElsePrototype.setBackground(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("ConditionalResponse"));
        ifElsePrototype.setBackground(IF_ELSE_COLOR);
        javax.swing.JLabel DITLabel = new javax.swing.JLabel(ifElseString);
        ifElsePrototype.add(DITLabel, java.awt.BorderLayout.CENTER);
        String DITdesired[] = {"condition"};
        ifElsePrototype.setTransferable(
                new edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementPrototypeReferenceTransferable(
                new edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype(
                edu.cmu.cs.stage3.alice.core.question.userdefined.IfElse.class, null, DITdesired)));
        ifElsePrototype.addDragSourceComponent(DITLabel);

        String CLdesired[] = {"end"};
        loopNPrototype = new edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel();
        //loopNPrototype.setBackground(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("LoopNInOrder"));
        loopNPrototype.setBackground(LOOP_N_COLOR);
        javax.swing.JLabel LLabel = new javax.swing.JLabel(loopNString);
        loopNPrototype.add(LLabel, java.awt.BorderLayout.CENTER);
        loopNPrototype.setTransferable(
                new edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementPrototypeReferenceTransferable(
                new edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype(
                edu.cmu.cs.stage3.alice.core.question.userdefined.LoopN.class, null, CLdesired)));
        loopNPrototype.addDragSourceComponent(LLabel);

        String LITdesired[] = {"condition"};
        whilePrototype = new edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel();
        //whilePrototype.setBackground(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("WhileLoopInOrder"));
        whilePrototype.setBackground(WHILE_COLOR);
        javax.swing.JLabel LITLabel = new javax.swing.JLabel(whileString);
        whilePrototype.add(LITLabel, java.awt.BorderLayout.CENTER);
        whilePrototype.setTransferable(
                new edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementPrototypeReferenceTransferable(
                new edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype(
                edu.cmu.cs.stage3.alice.core.question.userdefined.While.class, null, LITdesired)));
        whilePrototype.addDragSourceComponent(LITLabel);

        String SLdesired[] = {"list"};
        forEachPrototype = new edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel();
        //forEachPrototype.setBackground(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("ForEachInOrder"));
        forEachPrototype.setBackground(FOR_EACH_COLOR);
        javax.swing.JLabel SLLabel = new javax.swing.JLabel(forEachString);
        forEachPrototype.add(SLLabel, java.awt.BorderLayout.CENTER);
        forEachPrototype.setTransferable(
                new edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementPrototypeReferenceTransferable(
                new edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype(
                edu.cmu.cs.stage3.alice.core.question.userdefined.ForEach.class, null, SLdesired)));
        forEachPrototype.addDragSourceComponent(SLLabel);


        edu.cmu.cs.stage3.util.StringObjectPair Cknown[] = {new edu.cmu.cs.stage3.util.StringObjectPair("text", "No comment")};
        commentPrototype = new edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel();
        commentPrototype.setBackground(COMMENT_COLOR);
        javax.swing.JLabel commentLabel = new javax.swing.JLabel(commentString);
        commentLabel.setForeground(COMMENT_FOREGROUND);
        commentPrototype.add(commentLabel, java.awt.BorderLayout.CENTER);
        commentPrototype.setTransferable(
                new edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementPrototypeReferenceTransferable(
                new edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype(
                edu.cmu.cs.stage3.alice.core.question.userdefined.Comment.class, Cknown, null)));
        commentPrototype.addDragSourceComponent(commentLabel);

        printPrototype = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getGUI(
                new edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype(
                edu.cmu.cs.stage3.alice.core.question.userdefined.Print.class, null, null));
        printPrototype.setBackground(PRINT_COLOR);

        returnPrototype = new edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel();
        returnPrototype.setBackground(RETURN_COLOR);
        javax.swing.JLabel returnLabel = new javax.swing.JLabel(returnString);
        returnPrototype.add(returnLabel, java.awt.BorderLayout.CENTER);
        returnPrototype.setTransferable(
                new edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementPrototypeReferenceTransferable(
                new edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype(
                edu.cmu.cs.stage3.alice.core.question.userdefined.Return.class, null, null)));
        returnPrototype.addDragSourceComponent(returnLabel);

        mathPrototype = new edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel();
        mathPrototype.setBackground(MATH_COLOR);
        javax.swing.JLabel mathLabel = new javax.swing.JLabel(mathString);
        int fontSize = Integer.parseInt(authoringToolConfig.getValue("fontSize"));
        mathLabel.setFont( new java.awt.Font( "Monospaced", java.awt.Font.BOLD, (int)(12*fontSize/12.0) ) );
        mathPrototype.add(mathLabel, java.awt.BorderLayout.CENTER);
        mathPrototype.setTransferable( new edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CommonMathQuestionsTransferable() );
        mathPrototype.addDragSourceComponent(mathLabel);

    }

    
	protected void addPrototypes(java.awt.Container prototypeContainer){

        prototypeContainer.add(ifElsePrototype);
        prototypeContainer.add(loopNPrototype);
        prototypeContainer.add(whilePrototype);
        prototypeContainer.add(forEachPrototype);
        prototypeContainer.add(printPrototype);
        prototypeContainer.add(commentPrototype);
        prototypeContainer.add(returnPrototype);

        java.awt.Component buttonGlue = javax.swing.Box.createHorizontalGlue();
        prototypeContainer.add(buttonGlue);
    }

}