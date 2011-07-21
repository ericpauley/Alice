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

package edu.cmu.cs.stage3.caitlin.personbuilder;

import javax.swing.JPanel;

public class RenderPanel extends JPanel {
	ModelWrapper modelWrapper = null;

	public RenderPanel(ModelWrapper modelWrapper) {
		this.modelWrapper = modelWrapper;
		this.setLayout(new java.awt.BorderLayout());
	}

	public void initialize() {
		this.add(modelWrapper.getRenderPanel(), java.awt.BorderLayout.CENTER);
		javax.swing.JLabel label = new javax.swing.JLabel("Click and drag to rotate your person");
		label.setBackground(new java.awt.Color(155, 159, 206));
		label.setForeground(java.awt.Color.black);
		label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		this.add(label, java.awt.BorderLayout.NORTH);
	}
}