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

public class ComponentResponsePanel extends edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.ComponentElementPanel {

	protected edu.cmu.cs.stage3.alice.core.Response m_response;

	public ComponentResponsePanel() {
		setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
		this.remove(grip);
	}

	@Override
	public void set(edu.cmu.cs.stage3.alice.core.Element element) {
		if (element instanceof edu.cmu.cs.stage3.alice.core.Response) {
			m_response = (edu.cmu.cs.stage3.alice.core.Response) element;
			super.set(element);
		}
	}

	public edu.cmu.cs.stage3.alice.core.Response getResponse() {
		return m_response;
	}

}