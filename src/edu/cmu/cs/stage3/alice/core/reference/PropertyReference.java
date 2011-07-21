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

package edu.cmu.cs.stage3.alice.core.reference;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.ReferenceResolver;
import edu.cmu.cs.stage3.util.Criterion;

public class PropertyReference {
	private Property m_property;
	private Criterion m_criterion;
	public PropertyReference( Property property, Criterion criterion ) {
		m_property = property;
		m_criterion = criterion;
	}
	public Property getProperty() {
		return m_property;
	}
	public Criterion getCriterion() {
		return m_criterion;
	}
    public Element getReference() {
        return (Element)m_property.get();
    }
    // allow for override
    protected Object resolveReference( ReferenceResolver referenceResolver ) throws edu.cmu.cs.stage3.alice.core.UnresolvableReferenceException {
    	return referenceResolver.resolveReference( m_criterion );
    }
	public void resolve( ReferenceResolver referenceResolver ) throws edu.cmu.cs.stage3.alice.core.UnresolvableReferenceException {
		m_property.set( resolveReference( referenceResolver ) );
	}
	
	public String toString() {
		return "PropertyReference[property="+getProperty()+",criterion="+getCriterion()+"]";
	}
}

