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

package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;

public abstract class CompositeResponse extends edu.cmu.cs.stage3.alice.core.Response {
	public final ElementArrayProperty componentResponses = new ElementArrayProperty( this, "componentResponses", null, edu.cmu.cs.stage3.alice.core.Response[].class );
	protected static RuntimeResponse[] s_emptyRuntimeResponses = new RuntimeResponse[ 0 ];
	public abstract class RuntimeCompositeResponse extends RuntimeResponse {
		private RuntimeResponse[] m_runtimeResponses = null;
		protected RuntimeResponse[] manufactureComponentRuntimeResponses( ElementArrayProperty property ) {
			RuntimeResponse[] runtimeResponses = new RuntimeResponse[ property.size() ];
			int actualRuntimeResponseCount = 0;
			for( int i=0; i<property.size(); i++ ) {
				edu.cmu.cs.stage3.alice.core.Response response = (edu.cmu.cs.stage3.alice.core.Response)property.get( i );
				if( response != null ) {
					if( response.isCommentedOut.booleanValue() ) {
						//pass
					} else {
						runtimeResponses[actualRuntimeResponseCount] = response.manufactureRuntimeResponse();
						actualRuntimeResponseCount++;
					}
				} else {
					//TODO?
				}
			}
			if( actualRuntimeResponseCount<runtimeResponses.length ) {
				RuntimeResponse[] trimmedRuntimeResponses = new RuntimeResponse[ actualRuntimeResponseCount ];
				System.arraycopy( runtimeResponses, 0, trimmedRuntimeResponses, 0, actualRuntimeResponseCount );
				return trimmedRuntimeResponses;
			} else {
				return runtimeResponses;
			}
		}
		protected RuntimeResponse[] getRuntimeResponses() {
            if( m_runtimeResponses == null ) {
                m_runtimeResponses = manufactureComponentRuntimeResponses( componentResponses );
            }
			return m_runtimeResponses;
		}
        protected void childrenEpiloguesIfNecessary( double t ) {
            RuntimeResponse[] runtimeResponses = getRuntimeResponses();
            for( int i=0; i<runtimeResponses.length; i++ ) {
                if( runtimeResponses[ i ].isActive() ) {
                    runtimeResponses[ i ].epilogue( t );
                }
            }
        }
        
		public void epilogue( double t ) {
			super.epilogue( t );
            childrenEpiloguesIfNecessary( t );
        }
	}
}
