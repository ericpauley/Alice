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

package edu.cmu.cs.stage3.alice.authoringtool.util;

/**
 * @author Jason Pratt
 */

public class InvisibleSplitPaneUI extends javax.swing.plaf.basic.BasicSplitPaneUI {
	public static javax.swing.plaf.ComponentUI createUI(javax.swing.JComponent x) {
		return new InvisibleSplitPaneUI();
	}

	@Override
	public javax.swing.plaf.basic.BasicSplitPaneDivider createDefaultDivider() {
		return new InvisibleSplitPaneDivider(this);
	}

	// public void finishedPaintingChildren( javax.swing.JSplitPane jc,
	// java.awt.Graphics g ) {
	// }

	public class InvisibleSplitPaneDivider extends javax.swing.plaf.basic.BasicSplitPaneDivider {
		public InvisibleSplitPaneDivider(javax.swing.plaf.basic.BasicSplitPaneUI ui) {
			super(ui);
		}

		@Override
		public void paint(java.awt.Graphics g) {
		}
	}
}