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

import edu.cmu.cs.stage3.alice.core.ReferenceFrame;
import edu.cmu.cs.stage3.alice.core.property.ReferenceFrameProperty;
import edu.cmu.cs.stage3.alice.core.property.TransformableProperty;
import edu.cmu.cs.stage3.alice.core.Transformable;

//todo: absolute transformation listener
public abstract class TransformResponse extends edu.cmu.cs.stage3.alice.core.Response {
	public final TransformableProperty subject = new TransformableProperty( this, "subject", null );
	public final ReferenceFrameProperty asSeenBy = new ReferenceFrameProperty( this, "asSeenBy", null );
	public abstract class RuntimeTransformResponse extends RuntimeResponse {
		protected Transformable m_subject;
		protected ReferenceFrame m_asSeenBy;
		
		public void prologue( double t ) {
			super.prologue( t );
			m_subject = TransformResponse.this.subject.getTransformableValue();
			m_asSeenBy = TransformResponse.this.asSeenBy.getReferenceFrameValue();
            if( m_subject == null ) {
				throw new edu.cmu.cs.stage3.alice.core.SimulationPropertyException( "subject must not be null.", getCurrentStack(), TransformResponse.this.subject );
            }
		}
	}
}
