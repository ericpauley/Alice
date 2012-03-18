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

public class DefaultReferenceResolver implements edu.cmu.cs.stage3.alice.core.ReferenceResolver {
	private Element m_internalRoot;
	private Element m_externalRoot;
	public DefaultReferenceResolver( Element internalRoot, Element externalRoot ) {
		m_internalRoot = internalRoot;
		m_externalRoot = externalRoot;
	}
	
	public Element getInternalRoot() {
		return m_internalRoot;
	}
	public void setInternalRoot( Element internalRoot ) {
		m_internalRoot = internalRoot;
	}
	
	public Element getExternalRoot() {
		return m_externalRoot;
	}
	public void setExternalRoot( Element externalRoot ) {
		m_externalRoot = externalRoot;
	}
	
	public Element resolveReference( edu.cmu.cs.stage3.util.Criterion criterion ) throws edu.cmu.cs.stage3.alice.core.UnresolvableReferenceException {
		if( criterion instanceof edu.cmu.cs.stage3.alice.core.criterion.ElementKeyedCriterion ) {
			String key = ((edu.cmu.cs.stage3.alice.core.criterion.ElementKeyedCriterion)criterion).getKey();
			Element resolved = null;
			if( criterion instanceof edu.cmu.cs.stage3.alice.core.criterion.InternalReferenceKeyedCriterion ) {
				resolved = m_internalRoot.getDescendantKeyedIgnoreCase( key );
			} else if( criterion instanceof edu.cmu.cs.stage3.alice.core.criterion.ExternalReferenceKeyedCriterion ) {
				if( m_externalRoot != null ) {
                    resolved = m_externalRoot.getDescendantKeyedIgnoreCase( key );
				} else {
                    throw new edu.cmu.cs.stage3.alice.core.UnresolvableReferenceException( criterion, "external root is null" );
				}
			} else {
				Element root;
				if( m_externalRoot != null ) {
					root = m_externalRoot;
				} else {
					root = m_internalRoot;
				}
				int index = key.indexOf( Element.SEPARATOR );
				String trimmedKey;
				if( index == -1 ) {
					trimmedKey = "";
				} else {
					trimmedKey = key.substring( index+1 );
				}
				resolved = root.getDescendantKeyedIgnoreCase( trimmedKey );
			}
			if( resolved!=null ) {
				return resolved;
			} else {
                throw new edu.cmu.cs.stage3.alice.core.UnresolvableReferenceException( criterion, "internal root: " + m_internalRoot + " external root: " + m_externalRoot );
			}
		} else {
            //todo
            throw new edu.cmu.cs.stage3.alice.core.UnresolvableReferenceException( criterion, "must be edu.cmu.cs.stage3.alice.core.criterion.ElementKeyedCriterion" );
		}
	}
}