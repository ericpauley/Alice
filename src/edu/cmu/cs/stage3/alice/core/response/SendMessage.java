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

import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.property.TransformableProperty;
import edu.cmu.cs.stage3.alice.core.Transformable;

public class SendMessage extends edu.cmu.cs.stage3.alice.core.Response {
	public final StringProperty message = new StringProperty( this, "message", null );
	public final TransformableProperty fromWho = new TransformableProperty( this, "fromWho", null );
	public final TransformableProperty toWhom = new TransformableProperty( this, "toWhom", null );
	public class RuntimeSendMessage extends RuntimeResponse {
		
		public void prologue( double t ) {
			super.prologue( t );
			String messageValue = SendMessage.this.message.getStringValue();
			Transformable fromWhoValue = SendMessage.this.fromWho.getTransformableValue();
			Transformable toWhomValue = SendMessage.this.toWhom.getTransformableValue();
			World world = getWorld();
			if( world!=null ) {
				world.sendMessage( SendMessage.this, messageValue, fromWhoValue, toWhomValue, System.currentTimeMillis() );
			}
		}
	}
}

