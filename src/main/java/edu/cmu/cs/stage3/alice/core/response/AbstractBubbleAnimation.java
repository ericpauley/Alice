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

import edu.cmu.cs.stage3.alice.core.property.TransformableProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.property.ColorProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;

public abstract class AbstractBubbleAnimation extends edu.cmu.cs.stage3.alice.core.Response {
	public final TransformableProperty subject = new TransformableProperty( this, "subject", null );
	public final StringProperty what = new StringProperty( this, "what", "hello" );
	public final ColorProperty bubbleColor = new ColorProperty( this, "bubbleColor", edu.cmu.cs.stage3.alice.scenegraph.Color.WHITE );
	public final ColorProperty textColor = new ColorProperty( this, "textColor", edu.cmu.cs.stage3.alice.scenegraph.Color.BLACK );
	public final NumberProperty fontSize = new NumberProperty( this, "fontSize", new Integer(20) );
	public final StringProperty fontName = new StringProperty( this, "fontName", "Arial");

	private edu.cmu.cs.stage3.alice.core.World m_world;

	public abstract class RuntimeAbstractBubbleAnimation extends RuntimeResponse {
		private edu.cmu.cs.stage3.alice.core.World m_world;
		private edu.cmu.cs.stage3.alice.core.bubble.Bubble m_bubble;
		protected abstract edu.cmu.cs.stage3.alice.core.bubble.Bubble createBubble();
		
		public void prologue( double t ) {
			super.prologue( t );
			edu.cmu.cs.stage3.alice.core.Transformable subjectValue = AbstractBubbleAnimation.this.subject.getTransformableValue();
			if( subjectValue == null ) {
				throw new edu.cmu.cs.stage3.alice.core.SimulationPropertyException( "subject must not be null.", getCurrentStack(), AbstractBubbleAnimation.this.subject );
			}
			String whatValue = AbstractBubbleAnimation.this.what.getStringValue();
			if (( whatValue == null ) || (whatValue.length() == 0)) {
				throw new edu.cmu.cs.stage3.alice.core.SimulationPropertyException( "what must not be null.", getCurrentStack(), AbstractBubbleAnimation.this.what );
			}

			if( m_bubble == null ) {
				m_bubble = createBubble();
			}
			
			m_bubble.setReferenceFrame( subjectValue );
			m_bubble.setOffsetFromReferenceFrame( subjectValue.getBoundingBox().getCenterOfTopFace() );
			m_bubble.setText( whatValue );
			m_bubble.setFont( new java.awt.Font( AbstractBubbleAnimation.this.fontName.getStringValue(), java.awt.Font.PLAIN, AbstractBubbleAnimation.this.fontSize.intValue() ) );
			m_bubble.setBackgroundColor( AbstractBubbleAnimation.this.bubbleColor.getColorValue().createAWTColor() );
			m_bubble.setForegroundColor( AbstractBubbleAnimation.this.textColor.getColorValue().createAWTColor() );
			m_bubble.setIsShowing( true );
			m_world = AbstractBubbleAnimation.this.getWorld();
			if( m_world != null ) {
				m_world.bubbles.add( m_bubble );
			}
/*
			m_character = AbstractBubbleAnimation.this.subject.getTransformableValue();
			m_what = AbstractBubbleAnimation.this.what.getStringValue();

			if (m_character != null) {
				javax.vecmath.Vector3d top = null;
				bubble.message.set( m_what );

				bubble.bubbleColor.set(AbstractBubbleAnimation.this.bubbleColor.getColorValue());
				bubble.textColor.set(AbstractBubbleAnimation.this.textColor.getColorValue());
				bubble.fontSize.set(AbstractBubbleAnimation.this.fontSize.getNumberValue());
				bubble.fontName.set(AbstractBubbleAnimation.this.fontName.getStringValue());

				bubble.drawBubble();
				bubble.vehicle.set( m_character );
				if (headTransformable != null) {
					top = headTransformable.getBoundingBox().getMaximum();
					bubble.setPositionRightNow(-0.1, top.y, 0, headTransformable);
				} else {
					top = m_character.getBoundingBox().getMaximum();
					if (top != null) {
						bubble.setPositionRightNow(-0.1, top.y, 0, m_character);
					} else {
						bubble.setPositionRightNow(0, 0, 5, m_character);
					}
				}
			}
*/
		}

		
		public void epilogue( double t ) {
			if( m_bubble != null ) {
				m_bubble.setIsShowing( false );
				if( m_world != null ) {
					m_world.bubbles.remove( m_bubble );
					m_world = null;
				}
			}
			super.epilogue(t);
		}
	}
}