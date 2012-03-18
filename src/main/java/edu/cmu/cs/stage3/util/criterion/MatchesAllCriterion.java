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

package edu.cmu.cs.stage3.util.criterion;

public class MatchesAllCriterion implements edu.cmu.cs.stage3.util.Criterion {
	protected edu.cmu.cs.stage3.util.Criterion[] m_criteria;

	public MatchesAllCriterion( edu.cmu.cs.stage3.util.Criterion[] criteria ) {
		m_criteria = criteria;
	}

	public boolean accept( Object o ) {
		if( m_criteria != null ) {
			for( int i = 0; i < m_criteria.length; i++ ) {
				if( ! m_criteria[i].accept( o ) ) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}
	
	public edu.cmu.cs.stage3.util.Criterion[] getCriteria() {
		return m_criteria;
	}
}
