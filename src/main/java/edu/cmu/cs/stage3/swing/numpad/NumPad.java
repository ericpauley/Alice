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

package edu.cmu.cs.stage3.swing.numpad;

import java.awt.GridBagConstraints;
import java.awt.Insets;

/**
 * @author Jason Pratt, Dennis Cosgrove
 */
public class NumPad extends edu.cmu.cs.stage3.swing.ContentPane {
	public NumPad() {
		init();
	}

	@Override
	public String getTitle() {
		return "Custom Number";
	}

	@Override
	public void addOKActionListener(java.awt.event.ActionListener l) {
		okayButton.addActionListener(l);
	}

	@Override
	public void removeOKActionListener(java.awt.event.ActionListener l) {
		okayButton.removeActionListener(l);
	}

	@Override
	public void addCancelActionListener(java.awt.event.ActionListener l) {
		cancelButton.addActionListener(l);
	}

	@Override
	public void removeCancelActionListener(java.awt.event.ActionListener l) {
		cancelButton.removeActionListener(l);
	}
	private void doKey(javax.swing.JButton button, String imageString, java.awt.event.ActionListener onClick) {
		button.addActionListener(onClick);

		java.awt.Image image = null;
		java.net.URL resource = NumPad.class.getResource(imageString + ".gif");
		if (resource != null) {
			image = java.awt.Toolkit.getDefaultToolkit().getImage(resource);
		}
		javax.swing.ImageIcon icon = new javax.swing.ImageIcon(image);
		javax.swing.ImageIcon rolloverIcon = null;
		javax.swing.ImageIcon pressedIcon = null;
		if (image != null) {
			rolloverIcon = new javax.swing.ImageIcon(createLightenedOrDarkenedImage(image, 30));
			pressedIcon = new javax.swing.ImageIcon(createLightenedOrDarkenedImage(image, -30));
		}

		if (icon != null) {
			button.setText(null);
			button.setBorder(null);
			button.setIcon(icon);
			java.awt.Dimension iconSize = new java.awt.Dimension(icon.getIconWidth(), icon.getIconHeight());
			button.setMinimumSize(iconSize);
			button.setMaximumSize(iconSize);
			button.setPreferredSize(iconSize);
			if (rolloverIcon != null) {
				button.setRolloverIcon(rolloverIcon);
			}
			if (pressedIcon != null) {
				button.setPressedIcon(pressedIcon);
			}
		}
	}

	private void keyInit() {
		doKey(oneButton, "one", new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent ev) {
				numberTextField.grabFocus();
				numberTextField.dispatchEvent(new java.awt.event.KeyEvent(numberTextField, java.awt.event.KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0, java.awt.event.KeyEvent.VK_UNDEFINED, '1'));
			}
		});
		doKey(twoButton, "two", new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent ev) {
				numberTextField.grabFocus();
				numberTextField.dispatchEvent(new java.awt.event.KeyEvent(numberTextField, java.awt.event.KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0, java.awt.event.KeyEvent.VK_UNDEFINED, '2'));
			}
		});
		doKey(threeButton, "three", new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent ev) {
				numberTextField.grabFocus();
				numberTextField.dispatchEvent(new java.awt.event.KeyEvent(numberTextField, java.awt.event.KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0, java.awt.event.KeyEvent.VK_UNDEFINED, '3'));
			}
		});
		doKey(fourButton, "four", new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent ev) {
				numberTextField.grabFocus();
				numberTextField.dispatchEvent(new java.awt.event.KeyEvent(numberTextField, java.awt.event.KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0, java.awt.event.KeyEvent.VK_UNDEFINED, '4'));
			}
		});
		doKey(fiveButton, "five", new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent ev) {
				numberTextField.grabFocus();
				numberTextField.dispatchEvent(new java.awt.event.KeyEvent(numberTextField, java.awt.event.KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0, java.awt.event.KeyEvent.VK_UNDEFINED, '5'));
			}
		});
		doKey(sixButton, "six", new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent ev) {
				numberTextField.grabFocus();
				numberTextField.dispatchEvent(new java.awt.event.KeyEvent(numberTextField, java.awt.event.KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0, java.awt.event.KeyEvent.VK_UNDEFINED, '6'));
			}
		});
		doKey(sevenButton, "seven", new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent ev) {
				numberTextField.grabFocus();
				numberTextField.dispatchEvent(new java.awt.event.KeyEvent(numberTextField, java.awt.event.KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0, java.awt.event.KeyEvent.VK_UNDEFINED, '7'));
			}
		});
		doKey(eightButton, "eight", new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent ev) {
				numberTextField.grabFocus();
				numberTextField.dispatchEvent(new java.awt.event.KeyEvent(numberTextField, java.awt.event.KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0, java.awt.event.KeyEvent.VK_UNDEFINED, '8'));
			}
		});
		doKey(nineButton, "nine", new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent ev) {
				numberTextField.grabFocus();
				numberTextField.dispatchEvent(new java.awt.event.KeyEvent(numberTextField, java.awt.event.KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0, java.awt.event.KeyEvent.VK_UNDEFINED, '9'));
			}
		});
		doKey(zeroButton, "zero", new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent ev) {
				numberTextField.grabFocus();
				numberTextField.dispatchEvent(new java.awt.event.KeyEvent(numberTextField, java.awt.event.KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0, java.awt.event.KeyEvent.VK_UNDEFINED, '0'));
			}
		});
		doKey(decimalButton, "decimal", new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent ev) {
				numberTextField.grabFocus();
				numberTextField.dispatchEvent(new java.awt.event.KeyEvent(numberTextField, java.awt.event.KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0, java.awt.event.KeyEvent.VK_UNDEFINED, '.'));
			}
		});
		doKey(slashButton, "slash", new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent ev) {
				numberTextField.grabFocus();
				numberTextField.dispatchEvent(new java.awt.event.KeyEvent(numberTextField, java.awt.event.KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0, java.awt.event.KeyEvent.VK_UNDEFINED, '/'));
			}
		});
		doKey(backspaceButton, "backspace", new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent ev) {
				// numberTextField.grabFocus();
				// numberTextField.dispatchEvent(new
				// java.awt.event.KeyEvent(numberTextField,
				// java.awt.event.KeyEvent.KEY_TYPED,
				// System.currentTimeMillis(), 0,
				// java.awt.event.KeyEvent.VK_UNDEFINED, (char) 8));
				try {
					numberTextField.setText(numberTextField.getText(0, numberTextField.getText().length() - 1).toString());
				} catch (Exception e) {}
			}
		});
		doKey(clearButton, "clear", new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent ev) {
				numberTextField.setText("");
			}
		});
		doKey(plusMinusButton, "plusminus", new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent ev) {
				try {
					if ("-".equals(numberTextField.getDocument().getText(0, 1))) {
						numberTextField.getDocument().remove(0, 1);
					} else {
						numberTextField.getDocument().insertString(0, "-", null);
					}
				} catch (javax.swing.text.BadLocationException e) {
					throw new RuntimeException(e.getMessage());
				}
			}
		});
	}

	private void init() {
		jbInit();
		keyInit();
		numberTextField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
			@Override
			public void changedUpdate(javax.swing.event.DocumentEvent e) {
				NumPad.this.refresh();
			}
			@Override
			public void insertUpdate(javax.swing.event.DocumentEvent e) {
				NumPad.this.refresh();
			}
			@Override
			public void removeUpdate(javax.swing.event.DocumentEvent e) {
				NumPad.this.refresh();
			}
		});
		numberTextField.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent ev) {
				if (okayButton.isEnabled()) {
					okayButton.doClick();
				} else {
					java.awt.Toolkit.getDefaultToolkit().beep();
				}
			}
		});
		refresh();
	}

	public String getNumberString() {
		return numberTextField.getText();
	}

	public void setNumberString(String value) {
		if (value != null) {
			numberTextField.setText(value);
		} else {
			numberTextField.setText("");
		}
	}

	public void selectAll() {
		numberTextField.selectAll();
	}

	protected void refresh() {
		Double number = NumPad.parseDouble(numberTextField.getText());

		if (number != null) {
			okayButton.setEnabled(true);
			numberTextField.setForeground(java.awt.Color.black);
		} else {
			okayButton.setEnabled(false);
			numberTextField.setForeground(java.awt.Color.red);
		}
	}

	// ///////////////////
	// Duplicated code
	// ///////////////////

	public static Double parseDouble(String doubleString) {
		Double number = null;
		if (doubleString.trim().equalsIgnoreCase("infinity")) {
			number = new Double(Double.POSITIVE_INFINITY);
		} else if (doubleString.trim().equalsIgnoreCase("-infinity")) {
			number = new Double(Double.NEGATIVE_INFINITY);
		} else if (doubleString.indexOf('/') > -1) {
			if (doubleString.lastIndexOf('/') == doubleString.indexOf('/')) {
				String numeratorString = doubleString.substring(0, doubleString.indexOf('/'));
				String denominatorString = doubleString.substring(doubleString.indexOf('/') + 1);
				try {
					number = new Double(Double.parseDouble(numeratorString) / Double.parseDouble(denominatorString));
				} catch (NumberFormatException e) {}
			}
		} else {
			try {
				number = Double.valueOf(doubleString);
			} catch (NumberFormatException e) {}
		}

		return number;
	}

	/**
	 * positive percent value lightens, negative percent value darkens
	 */
	public java.awt.Image createLightenedOrDarkenedImage(java.awt.Image i, int percent) {
		LightenDarkenFilter filter = new LightenDarkenFilter(percent);
		java.awt.image.ImageProducer prod = new java.awt.image.FilteredImageSource(i.getSource(), filter);
		java.awt.Image filteredImage = java.awt.Toolkit.getDefaultToolkit().createImage(prod);

		return filteredImage;
	}

	public class LightenDarkenFilter extends java.awt.image.RGBImageFilter {
		private int percent;

		/**
		 * positive percent value lightens, negative percent value darkens
		 */
		public LightenDarkenFilter(int p) {
			percent = p;

			canFilterIndexColorModel = true;
		}

		@Override
		public int filterRGB(int x, int y, int rgb) {
			int r = rgb >> 16 & 0xff;
			int g = rgb >> 8 & 0xff;
			int b = rgb >> 0 & 0xff;

			if (percent > 0) {
				r = 255 - (255 - r) * (100 - percent) / 100;
				g = 255 - (255 - g) * (100 - percent) / 100;
				b = 255 - (255 - b) * (100 - percent) / 100;
			} else {
				r = r * (100 + percent) / 100;
				g = g * (100 + percent) / 100;
				b = b * (100 + percent) / 100;
			}

			if (r < 0) {
				r = 0;
			}
			if (g < 0) {
				g = 0;
			}
			if (b < 0) {
				b = 0;
			}

			if (r > 255) {
				r = 255;
			}
			if (g > 255) {
				g = 255;
			}
			if (b > 255) {
				b = 255;
			}

			return rgb & 0xff000000 | r << 16 | g << 8 | b << 0;
		}
	}

	// /////////////////////
	// Autogenerated...
	// /////////////////////

	java.awt.BorderLayout borderLayout1 = new java.awt.BorderLayout();
	javax.swing.JPanel mainPanel = new javax.swing.JPanel();
	javax.swing.JPanel buttonPanel = new javax.swing.JPanel();
	java.awt.GridBagLayout gridBagLayout1 = new java.awt.GridBagLayout();
	javax.swing.JTextField numberTextField = new javax.swing.JTextField();
	java.awt.GridBagLayout gridBagLayout2 = new java.awt.GridBagLayout();
	javax.swing.JButton okayButton = new javax.swing.JButton();
	javax.swing.JButton cancelButton = new javax.swing.JButton();
	javax.swing.JPanel keyWell = new javax.swing.JPanel();
	java.awt.GridBagLayout gridBagLayout3 = new java.awt.GridBagLayout();
	javax.swing.JButton sevenButton = new javax.swing.JButton();
	javax.swing.JButton eightButton = new javax.swing.JButton();
	javax.swing.JButton nineButton = new javax.swing.JButton();
	javax.swing.JButton backspaceButton = new javax.swing.JButton();
	javax.swing.JButton fourButton = new javax.swing.JButton();
	javax.swing.JButton fiveButton = new javax.swing.JButton();
	javax.swing.JButton sixButton = new javax.swing.JButton();
	javax.swing.JButton clearButton = new javax.swing.JButton();
	javax.swing.JButton oneButton = new javax.swing.JButton();
	javax.swing.JButton twoButton = new javax.swing.JButton();
	javax.swing.JButton threeButton = new javax.swing.JButton();
	javax.swing.JButton plusMinusButton = new javax.swing.JButton();
	javax.swing.JButton zeroButton = new javax.swing.JButton();
	javax.swing.JButton decimalButton = new javax.swing.JButton();
	javax.swing.JButton slashButton = new javax.swing.JButton();

	private void jbInit() {
		setLayout(borderLayout1);
		mainPanel.setLayout(gridBagLayout1);
		buttonPanel.setLayout(gridBagLayout2);
		okayButton.setText("Okay");
		cancelButton.setText("Cancel");
		numberTextField.setFont(new java.awt.Font("Dialog", 0, 24));
		keyWell.setLayout(gridBagLayout3);
		keyWell.setBackground(new java.awt.Color(17, 17, 17));
		sevenButton.setText("7");
		eightButton.setText("8");
		nineButton.setText("9");
		backspaceButton.setText("<");
		fourButton.setText("4");
		fiveButton.setText("5");
		sixButton.setText("6");
		clearButton.setText("C");
		oneButton.setText("1");
		twoButton.setText("2");
		threeButton.setText("3");
		plusMinusButton.setText("-");
		zeroButton.setText("0");
		decimalButton.setText(".");
		slashButton.setText("/");
		add(mainPanel, java.awt.BorderLayout.NORTH);
		mainPanel.add(numberTextField, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(8, 8, 8, 8), 0, 0));
		add(buttonPanel, java.awt.BorderLayout.CENTER);
		buttonPanel.add(okayButton, new GridBagConstraints(0, 1, 1, 1, 0.0, 1.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
		buttonPanel.add(cancelButton, new GridBagConstraints(1, 1, 1, 1, 0.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
		mainPanel.add(keyWell, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 8, 8, 8), 0, 0));
		keyWell.add(sevenButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(4, 4, 0, 0), 0, 0));
		keyWell.add(eightButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(4, 4, 0, 0), 0, 0));
		keyWell.add(nineButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(4, 4, 0, 0), 0, 0));
		keyWell.add(backspaceButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(4, 4, 0, 4), 0, 0));
		keyWell.add(fourButton, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(4, 4, 0, 0), 0, 0));
		keyWell.add(fiveButton, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(4, 4, 0, 0), 0, 0));
		keyWell.add(sixButton, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(4, 4, 0, 0), 0, 0));
		keyWell.add(clearButton, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(4, 4, 0, 4), 0, 0));
		keyWell.add(oneButton, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(4, 4, 0, 0), 0, 0));
		keyWell.add(twoButton, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(4, 4, 0, 0), 0, 0));
		keyWell.add(threeButton, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(4, 4, 0, 0), 0, 0));
		keyWell.add(plusMinusButton, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(4, 4, 0, 4), 0, 0));
		keyWell.add(zeroButton, new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(4, 4, 4, 0), 0, 0));
		keyWell.add(decimalButton, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(4, 4, 4, 0), 0, 0));
		keyWell.add(slashButton, new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(4, 4, 4, 4), 0, 0));
	}
}