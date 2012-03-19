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
 * Title: Description: Copyright: Copyright (c) 2001 Company:
 * 
 * @author
 * @version 1.0
 */

public class ResponseEditor extends edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeElementEditor {

	public final String editorName = "Response Editor";

	protected edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel doInOrderPrototype;
	protected edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel doTogetherPrototype;
	protected edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel doIfTruePrototype;
	protected edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel loopPrototype;
	protected javax.swing.JComponent waitPrototype;
	protected edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel sequentialLoopPrototype;
	protected edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel forAllTogetherPrototype;
	protected edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel scriptPrototype;
	protected edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel scriptDefinedPrototype;
	protected edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel loopIfTruePrototype;
	protected edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel commentPrototype;
	protected javax.swing.JComponent printPrototype;
	protected edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel mathPrototype;

	public ResponseEditor() {
		super();
	}

	public void setObject(edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse toEdit) {
		clearAllListening();
		elementBeingEdited = toEdit;
		updateGui();
	}

	@Override
	protected java.awt.Color getEditorColor() {
		return edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("userDefinedResponseEditor");
	}

	@Override
	protected edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.MainCompositeElementPanel createElementTree(edu.cmu.cs.stage3.alice.core.Element selected) {
		if (selected instanceof edu.cmu.cs.stage3.alice.core.response.CompositeResponse) {
			MainCompositeResponsePanel toReturn = null;
			if (selected instanceof edu.cmu.cs.stage3.alice.core.response.DoInOrder) {
				toReturn = new MainSequentialResponsePanel();
				toReturn.set(selected, authoringTool);
			} else if (selected instanceof edu.cmu.cs.stage3.alice.core.response.DoTogether) {
				toReturn = new MainParallelResponsePanel();
				toReturn.set(selected, authoringTool);
			}
			return toReturn;
		}
		return null;
	}

	@Override
	protected void initPrototypes() {
		String doInOrderString = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(edu.cmu.cs.stage3.alice.core.response.DoInOrder.class);
		String doTogetherString = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(edu.cmu.cs.stage3.alice.core.response.DoTogether.class);
		String doIfTrueString = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(edu.cmu.cs.stage3.alice.core.response.IfElseInOrder.class);
		String loopString = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(edu.cmu.cs.stage3.alice.core.response.LoopNInOrder.class);
		// protected final String waitString =
		// edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(edu.cmu.cs.stage3.alice.core.response.Wait.class);
		String sequentialLoopString = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(edu.cmu.cs.stage3.alice.core.response.ForEachInOrder.class);
		String forAllTogetherString = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(edu.cmu.cs.stage3.alice.core.response.ForEachTogether.class);
		String scriptString = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(edu.cmu.cs.stage3.alice.core.response.ScriptResponse.class);
		String scriptDefinedString = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(edu.cmu.cs.stage3.alice.core.response.ScriptDefinedResponse.class);
		String loopIfTrueString = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(edu.cmu.cs.stage3.alice.core.response.WhileLoopInOrder.class);
		String commentString = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(edu.cmu.cs.stage3.alice.core.response.Comment.class);
		String mathString = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue("+ - * /");

		java.awt.Color DO_IN_ORDER_COLOR = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("DoInOrder");
		java.awt.Color DO_TOGETHER_COLOR = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("DoTogether");
		java.awt.Color COUNT_LOOP_COLOR = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("LoopNInOrder");
		java.awt.Color DO_IF_COLOR = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("IfElseInOrder");
		// WAIT_COLOR =
		// edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("Wait");
		java.awt.Color SEQUENTIAL_LOOP_COLOR = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("ForEachInOrder");
		java.awt.Color FOR_ALL_TOGETHER_COLOR = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("ForAllTogether");
		java.awt.Color SCRIPT_COLOR = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("ScriptResponse");
		java.awt.Color SCRIPT_DEFINED_COLOR = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("ScriptDefinedResponse");
		java.awt.Color COMMENT_COLOR = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("Comment");
		java.awt.Color COMMENT_FOREGROUND = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("commentForeground");
		java.awt.Color DO_WHILE_COLOR = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("WhileLoopInOrder");
		// PRINT_COLOR =
		// edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("Print");
		java.awt.Color MATH_COLOR = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("question");

		doInOrderPrototype = new edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel();
		// doInOrderPrototype.setBackground(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("SequentialResponse"));
		doInOrderPrototype.setBackground(DO_IN_ORDER_COLOR);
		javax.swing.JLabel DIOJLabel = new javax.swing.JLabel(doInOrderString);
		doInOrderPrototype.add(DIOJLabel, java.awt.BorderLayout.CENTER);
		doInOrderPrototype.setTransferable(new edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ResponsePrototypeReferenceTransferable(new edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype(edu.cmu.cs.stage3.alice.core.response.DoInOrder.class, null, null)));
		doInOrderPrototype.addDragSourceComponent(DIOJLabel);

		doTogetherPrototype = new edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel();
		// doTogetherPrototype.setBackground(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("ParallelResponse"));
		doTogetherPrototype.setBackground(DO_TOGETHER_COLOR);
		javax.swing.JLabel DTJLabel = new javax.swing.JLabel(doTogetherString);
		doTogetherPrototype.add(DTJLabel, java.awt.BorderLayout.CENTER);
		doTogetherPrototype.setTransferable(new edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ResponsePrototypeReferenceTransferable(new edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype(edu.cmu.cs.stage3.alice.core.response.DoTogether.class, null, null)));
		doTogetherPrototype.addDragSourceComponent(DTJLabel);

		doIfTruePrototype = new edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel();
		// doIfTruePrototype.setBackground(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("ConditionalResponse"));
		doIfTruePrototype.setBackground(DO_IF_COLOR);
		javax.swing.JLabel DITLabel = new javax.swing.JLabel(doIfTrueString);
		doIfTruePrototype.add(DITLabel, java.awt.BorderLayout.CENTER);
		String DITdesired[] = {"condition"};
		doIfTruePrototype.setTransferable(new edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ResponsePrototypeReferenceTransferable(new edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype(edu.cmu.cs.stage3.alice.core.response.IfElseInOrder.class, null, DITdesired)));
		doIfTruePrototype.addDragSourceComponent(DITLabel);

		String CLdesired[] = {"end"};
		loopPrototype = new edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel();
		// loopPrototype.setBackground(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("LoopNInOrder"));
		loopPrototype.setBackground(COUNT_LOOP_COLOR);
		javax.swing.JLabel LLabel = new javax.swing.JLabel(loopString);
		loopPrototype.add(LLabel, java.awt.BorderLayout.CENTER);
		loopPrototype.setTransferable(new edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ResponsePrototypeReferenceTransferable(new edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype(edu.cmu.cs.stage3.alice.core.response.LoopNInOrder.class, null, CLdesired)));
		loopPrototype.addDragSourceComponent(LLabel);

		String LITdesired[] = {"condition"};
		loopIfTruePrototype = new edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel();
		// loopIfTruePrototype.setBackground(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("WhileLoopInOrder"));
		loopIfTruePrototype.setBackground(DO_WHILE_COLOR);
		javax.swing.JLabel LITLabel = new javax.swing.JLabel(loopIfTrueString);
		loopIfTruePrototype.add(LITLabel, java.awt.BorderLayout.CENTER);
		loopIfTruePrototype.setTransferable(new edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ResponsePrototypeReferenceTransferable(new edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype(edu.cmu.cs.stage3.alice.core.response.WhileLoopInOrder.class, null, LITdesired)));
		loopIfTruePrototype.addDragSourceComponent(LITLabel);

		String SLdesired[] = {"list"};
		sequentialLoopPrototype = new edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel();
		// sequentialLoopPrototype.setBackground(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("ForEachInOrder"));
		sequentialLoopPrototype.setBackground(SEQUENTIAL_LOOP_COLOR);
		javax.swing.JLabel SLLabel = new javax.swing.JLabel(sequentialLoopString);
		sequentialLoopPrototype.add(SLLabel, java.awt.BorderLayout.CENTER);
		sequentialLoopPrototype.setTransferable(new edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ResponsePrototypeReferenceTransferable(new edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype(edu.cmu.cs.stage3.alice.core.response.ForEachInOrder.class, null, SLdesired)));
		sequentialLoopPrototype.addDragSourceComponent(SLLabel);

		String FATdesired[] = {"list"};
		forAllTogetherPrototype = new edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel();
		// sequentialLoopPrototype.setBackground(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("ForEachInOrder"));
		forAllTogetherPrototype.setBackground(FOR_ALL_TOGETHER_COLOR);
		javax.swing.JLabel FATLabel = new javax.swing.JLabel(forAllTogetherString);
		forAllTogetherPrototype.add(FATLabel, java.awt.BorderLayout.CENTER);
		forAllTogetherPrototype.setTransferable(new edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ResponsePrototypeReferenceTransferable(new edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype(edu.cmu.cs.stage3.alice.core.response.ForEachTogether.class, null, FATdesired)));
		forAllTogetherPrototype.addDragSourceComponent(FATLabel);

		String Wdesired[] = {"duration"};
		waitPrototype = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getGUI(new edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype(edu.cmu.cs.stage3.alice.core.response.Wait.class, null, Wdesired));
		// waitPrototype.addDragSourceComponent(WLabel);

		scriptDefinedPrototype = new edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel();
		scriptDefinedPrototype.setBackground(SCRIPT_DEFINED_COLOR);
		// scriptDefinedPrototype.setBackground(SCRIPT_COLOR);
		javax.swing.JLabel scriptDefinedLabel = new javax.swing.JLabel(scriptDefinedString);
		// javax.swing.JLabel scriptDefinedLabel = new
		// javax.swing.JLabel("Script Defined Response");
		scriptDefinedPrototype.add(scriptDefinedLabel, java.awt.BorderLayout.CENTER);
		scriptDefinedPrototype.setTransferable(new edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ResponsePrototypeReferenceTransferable(new edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype(edu.cmu.cs.stage3.alice.core.response.ScriptDefinedResponse.class, null, null)));
		scriptDefinedPrototype.addDragSourceComponent(scriptDefinedLabel);

		scriptPrototype = new edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel();
		scriptPrototype.setBackground(SCRIPT_COLOR);
		javax.swing.JLabel scriptLabel = new javax.swing.JLabel(scriptString);
		scriptPrototype.add(scriptLabel, java.awt.BorderLayout.CENTER);
		scriptPrototype.setTransferable(new edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ResponsePrototypeReferenceTransferable(new edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype(edu.cmu.cs.stage3.alice.core.response.ScriptResponse.class, null, null)));
		scriptPrototype.addDragSourceComponent(scriptLabel);

		edu.cmu.cs.stage3.util.StringObjectPair Cknown[] = {new edu.cmu.cs.stage3.util.StringObjectPair("text", "No comment")};
		commentPrototype = new edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel();
		commentPrototype.setBackground(COMMENT_COLOR);
		javax.swing.JLabel commentLabel = new javax.swing.JLabel(commentString);
		commentLabel.setForeground(COMMENT_FOREGROUND);
		commentPrototype.add(commentLabel, java.awt.BorderLayout.CENTER);
		commentPrototype.setTransferable(new edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ResponsePrototypeReferenceTransferable(new edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype(edu.cmu.cs.stage3.alice.core.response.Comment.class, Cknown, null)));
		commentPrototype.addDragSourceComponent(commentLabel);

		printPrototype = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getGUI(new edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype(edu.cmu.cs.stage3.alice.core.response.Print.class, null, null));

		mathPrototype = new edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel();
		mathPrototype.setBackground(MATH_COLOR);
		javax.swing.JLabel mathLabel = new javax.swing.JLabel(mathString);
		int fontSize = Integer.parseInt(authoringToolConfig.getValue("fontSize"));
		mathLabel.setFont(new java.awt.Font("Monospaced", java.awt.Font.BOLD, (int) (12 * (fontSize / 12.0))));
		mathPrototype.add(mathLabel, java.awt.BorderLayout.CENTER);
		mathPrototype.setTransferable(new edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CommonMathQuestionsTransferable());
		mathPrototype.addDragSourceComponent(mathLabel);

	}

	@Override
	protected void addPrototypes(java.awt.Container prototypeContainer) {

		prototypeContainer.add(doInOrderPrototype);
		prototypeContainer.add(doTogetherPrototype);
		prototypeContainer.add(doIfTruePrototype);
		prototypeContainer.add(loopPrototype);
		prototypeContainer.add(loopIfTruePrototype);
		prototypeContainer.add(sequentialLoopPrototype);
		prototypeContainer.add(forAllTogetherPrototype);

		prototypeContainer.add(javax.swing.Box.createHorizontalStrut(10));

		prototypeContainer.add(waitPrototype);
		if (authoringToolConfig.getValue("enableScripting").equalsIgnoreCase("true")) {
			prototypeContainer.add(scriptPrototype);
			prototypeContainer.add(scriptDefinedPrototype);

		}
		prototypeContainer.add(printPrototype);
		prototypeContainer.add(commentPrototype);

		java.awt.Component buttonGlue = javax.swing.Box.createHorizontalGlue();
		prototypeContainer.add(buttonGlue);

	}
}