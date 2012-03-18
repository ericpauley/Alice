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
 * @author David Culyba, Dennis Cosgrove
 */

import java.awt.GridBagConstraints;
import java.awt.Insets;

public class TempColorPicker extends javax.swing.JPanel {
    protected CompositeElementEditor editor;
	private class TileColorPicker extends javax.swing.JPanel{
		public java.awt.Color toChange;
		public java.awt.Color foregroundToChange;
		public javax.swing.JLabel tile;
		public javax.swing.JButton openPicker;
		public javax.swing.JButton openPicker2;
		public String nameKey;
		public Object classKey;
		public String foregroundNameKey;


		public TileColorPicker(String nameKey, Object classKey){
			this.toChange = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor(nameKey);
			this.nameKey = nameKey;
			this.classKey = classKey;
			String name = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(classKey);
			tile = new javax.swing.JLabel(name);
			tile.setOpaque(true);
			tile.setBackground(toChange);
			openPicker = new javax.swing.JButton("select color");
			openPicker.addActionListener(new java.awt.event.ActionListener(){
				public void actionPerformed( java.awt.event.ActionEvent ev ) {
					javax.swing.JColorChooser colorChooser = new javax.swing.JColorChooser();
					java.awt.Color newColor = edu.cmu.cs.stage3.swing.DialogManager.showDialog( colorChooser, "Pick color for "+TileColorPicker.this.tile.getText(), TileColorPicker.this.toChange );
					TileColorPicker.this.tile.setBackground(newColor);
					TileColorPicker.this.toChange = newColor;
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.putColor(TileColorPicker.this.nameKey, newColor);
					editor.guiInit();
					TileColorPicker.this.repaint();
					editor.repaint();
				}
			});
			this.setLayout(new java.awt.GridBagLayout());
			this.add(tile,  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			this.add(openPicker,  new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 8, 0, 0), 0, 0));
			this.setBorder(javax.swing.BorderFactory.createEmptyBorder(4,4,4,4));
		}

		public TileColorPicker(String nameKey, Class classKey, String foregroundNameKey){
			this(nameKey, classKey);
			this.foregroundNameKey = foregroundNameKey;
			this.foregroundToChange = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor(foregroundNameKey);
			tile.setForeground(this.foregroundToChange);
			openPicker2 = new javax.swing.JButton("select text color");
			openPicker2.addActionListener(new java.awt.event.ActionListener(){
				public void actionPerformed( java.awt.event.ActionEvent ev ) {
					javax.swing.JColorChooser colorChooser = new javax.swing.JColorChooser();
					java.awt.Color newColor = edu.cmu.cs.stage3.swing.DialogManager.showDialog( colorChooser, "Pick color for "+TileColorPicker.this.tile.getText(), TileColorPicker.this.foregroundToChange );
					TileColorPicker.this.tile.setForeground(newColor);
					TileColorPicker.this.foregroundToChange = newColor;
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.putColor(TileColorPicker.this.foregroundNameKey, newColor);
					editor.guiInit();
					TileColorPicker.this.repaint();
					editor.repaint();
				}
			});
			this.add(openPicker2, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 8, 0, 0), 0, 0));
		}
	}

    public TempColorPicker(CompositeElementEditor r) {
        editor = r;
        setLayout(new java.awt.GridBagLayout());
        add(new TileColorPicker("DoInOrder", edu.cmu.cs.stage3.alice.core.response.DoInOrder.class),  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 4, 0, 0), 0, 0));
        add(new TileColorPicker("DoTogether", edu.cmu.cs.stage3.alice.core.response.DoTogether.class),  new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 4, 0, 0), 0, 0));
        add(new TileColorPicker("IfElseInOrder", edu.cmu.cs.stage3.alice.core.response.IfElseInOrder.class),  new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 4, 0, 0), 0, 0));
        add(new TileColorPicker("LoopNInOrder", edu.cmu.cs.stage3.alice.core.response.LoopNInOrder.class),  new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 4, 0, 0), 0, 0));
        add(new TileColorPicker("WhileLoopInOrder", edu.cmu.cs.stage3.alice.core.response.WhileLoopInOrder.class),  new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 4, 0, 0), 0, 0));
        add(new TileColorPicker("ForEachInOrder", edu.cmu.cs.stage3.alice.core.response.ForEachInOrder.class),  new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 4, 0, 0), 0, 0));
        add(new TileColorPicker("ForAllTogether", edu.cmu.cs.stage3.alice.core.response.ForEachTogether.class),  new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 4, 0, 0), 0, 0));
        add(new TileColorPicker("response", "response"),  new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 4, 0, 0), 0, 0));
        add(new TileColorPicker("Comment", edu.cmu.cs.stage3.alice.core.response.Comment.class, "commentForeground"),  new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 4, 0, 0), 0, 0));
        add(new TileColorPicker(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.QUESTION_STRING, "+ - * /"),  new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 4, 0, 0), 0, 0));
        add(new TileColorPicker("userDefinedQuestionEditor", edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.QUESTION_STRING+" editor"),  new GridBagConstraints(0, 10, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 4, 0, 0), 0, 0));
    }
}