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

public class DefaultReferenceGenerator implements edu.cmu.cs.stage3.alice.core.ReferenceGenerator {
	edu.cmu.cs.stage3.alice.core.Element m_internalRoot;
	public DefaultReferenceGenerator( edu.cmu.cs.stage3.alice.core.Element internalRoot ) {
		m_internalRoot = internalRoot;
	}
	protected boolean isExternal( edu.cmu.cs.stage3.alice.core.Element element ) {
		return !element.isReferenceInternalTo( m_internalRoot );
	}

	public edu.cmu.cs.stage3.util.Criterion generateReference( edu.cmu.cs.stage3.alice.core.Element element ) {
		if( element!=null ) {
            if( m_internalRoot != null ) {
                if( isExternal( element ) ) {
                    return new edu.cmu.cs.stage3.alice.core.criterion.ExternalReferenceKeyedCriterion( element.getKey( m_internalRoot.getRoot() ) );
                } else {
                    return new edu.cmu.cs.stage3.alice.core.criterion.InternalReferenceKeyedCriterion( element.getKey( m_internalRoot ) );
                }
            } else {
                return new edu.cmu.cs.stage3.alice.core.criterion.ExternalReferenceKeyedCriterion( element.getKey() );
            }
		} else {
			return null;
		}
	}
}