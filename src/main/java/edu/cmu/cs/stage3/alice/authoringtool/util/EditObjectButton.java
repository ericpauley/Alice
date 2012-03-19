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

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;

/**
 * @author Jason Pratt
 */
public class EditObjectButton extends javax.swing.JButton implements edu.cmu.cs.stage3.alice.authoringtool.util.GUIElement, edu.cmu.cs.stage3.alice.authoringtool.util.Releasable {
	protected edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;
	protected Object object;
	protected javax.swing.JComponent animationSource;
	private edu.cmu.cs.stage3.alice.authoringtool.util.Configuration authoringToolConfig = edu.cmu.cs.stage3.alice.authoringtool.util.Configuration.getLocalConfiguration(AuthoringTool.class.getPackage());

	public EditObjectButton() {
		setBackground(new java.awt.Color(240, 240, 255));
		setMargin(new java.awt.Insets(0, 2, 0, 2));
		setText("edit");
		int fontSize = Integer.parseInt(authoringToolConfig.getValue("fontSize"));
		setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, (int) (11 * fontSize / 12.0)));
		setFocusPainted(false);
		setBorder(new javax.swing.plaf.BorderUIResource.CompoundBorderUIResource(new CustomButtonBorder(), new javax.swing.plaf.basic.BasicBorders.MarginBorder()));
		// setBorder( new javax.swing.plaf.metal.MetalBorders.ButtonBorder() {
		// protected java.awt.Insets borderInsets = new java.awt.Insets( 1, 3,
		// 1, 3 );
		// public java.awt.Insets getBorderInsets( java.awt.Component c ) {
		// return this.borderInsets;
		// }
		// } );
		addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent ev) {
				if (authoringTool != null) {
					if (object != null) {
						if (animationSource != null) {
							authoringTool.editObject(object, animationSource);
						} else {
							authoringTool.editObject(object);
						}
					} else {
						edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("object unexpectedly null in EditObjectButton", null);
					}
				} else {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("authoringTool unexpectedly null in EditObjectButton", null);
				}
			}
		});
	}

	public void setAuthoringTool(edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool) {
		this.authoringTool = authoringTool;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public Object getObject() {
		return object;
	}

	public void setAnimationSource(javax.swing.JComponent animationSource) {
		this.animationSource = animationSource;
	}

	@Override
	public void goToSleep() {
	}
	@Override
	public void wakeUp() {
	}

	@Override
	public void clean() {
		object = null;
		animationSource = null;
	}

	@Override
	public void die() {
		clean();
		authoringTool = null;
	}

	@Override
	public void release() {
		edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.releaseGUI(this);
	}

	class CustomButtonBorder extends javax.swing.border.AbstractBorder implements javax.swing.plaf.UIResource {
		protected java.awt.Insets insets = new java.awt.Insets(1, 3, 1, 3);
		protected javax.swing.border.Border line = javax.swing.BorderFactory.createLineBorder(java.awt.Color.darkGray, 1);
		protected javax.swing.border.Border spacer = javax.swing.BorderFactory.createEmptyBorder(0, 3, 0, 3);
		protected javax.swing.border.Border raisedBevel = new CustomBevelBorder(javax.swing.border.BevelBorder.RAISED);
		protected javax.swing.border.Border loweredBevel = new CustomBevelBorder(javax.swing.border.BevelBorder.LOWERED);
		protected javax.swing.border.Border raisedBorder = javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createCompoundBorder(line, raisedBevel), spacer);
		protected javax.swing.border.Border loweredBorder = javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createCompoundBorder(line, loweredBevel), spacer);
		// protected javax.swing.border.Border raisedBorder =
		// javax.swing.BorderFactory.createCompoundBorder( raisedBevel, spacer
		// );
		// protected javax.swing.border.Border loweredBorder =
		// javax.swing.BorderFactory.createCompoundBorder( loweredBevel, spacer
		// );

		@Override
		public void paintBorder(java.awt.Component c, java.awt.Graphics g, int x, int y, int w, int h) {
			javax.swing.JButton button = (javax.swing.JButton) c;
			javax.swing.ButtonModel model = button.getModel();

			if (model.isEnabled()) {
				if (model.isPressed() && model.isArmed()) {
					loweredBorder.paintBorder(button, g, x, y, w, h);
				} else {
					raisedBorder.paintBorder(button, g, x, y, w, h);
				}
			} else {
				raisedBorder.paintBorder(button, g, x, y, w, h);
			}
		}

		@Override
		public java.awt.Insets getBorderInsets(java.awt.Component c) {
			return insets;
		}
	}

	class CustomBevelBorder extends javax.swing.border.BevelBorder {
		public CustomBevelBorder(int type) {
			super(type);
		}

		@Override
		public java.awt.Color getHighlightInnerColor(java.awt.Component c) {
			return c.getBackground();
		}

		@Override
		public java.awt.Color getShadowInnerColor(java.awt.Component c) {
			return c.getBackground();
		}
	}
}