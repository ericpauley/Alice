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

package edu.cmu.cs.stage3.alice.authoringtool.viewcontroller;

/**
 * @author Jason Pratt
 */
public class PropertyLabel extends javax.swing.JLabel {
	protected edu.cmu.cs.stage3.alice.core.Property property;

	public PropertyLabel( edu.cmu.cs.stage3.alice.core.Property property ) {
		this.property = property;
		setOpaque( false );
		property.addPropertyListener(
			new edu.cmu.cs.stage3.alice.core.event.PropertyListener() {
				public void propertyChanging( edu.cmu.cs.stage3.alice.core.event.PropertyEvent propertyEvent ) {}
				public void propertyChanged( edu.cmu.cs.stage3.alice.core.event.PropertyEvent propertyEvent ) {
					PropertyLabel.this.update();
				}
			}
		);
		update();
	}

	public void update() {
		setText( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue( property.get(), property ) );
	}
}
