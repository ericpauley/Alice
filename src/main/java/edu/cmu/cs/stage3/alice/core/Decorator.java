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

package edu.cmu.cs.stage3.alice.core;

public abstract class Decorator {
	protected edu.cmu.cs.stage3.alice.scenegraph.Visual m_sgVisual = null;
	protected edu.cmu.cs.stage3.alice.scenegraph.Appearance m_sgAppearance = null;
	protected abstract ReferenceFrame getReferenceFrame();
    private boolean m_isDirty = true;

    public void markDirty() {
        setIsDirty( true );
    }
    public void setIsDirty( boolean isDirty ) {
        m_isDirty = isDirty;
        if( isDirty ) {
            updateIfShowing();
        }
    }
    public boolean isDirty() {
        return m_isDirty;
    }
	protected void update() {
		ReferenceFrame referenceFrame = getReferenceFrame();
		if( referenceFrame != null ) {
			if( m_sgAppearance==null ) {
				m_sgAppearance = new edu.cmu.cs.stage3.alice.scenegraph.Appearance();
				m_sgAppearance.setShadingStyle( edu.cmu.cs.stage3.alice.scenegraph.ShadingStyle.NONE );
				m_sgAppearance.setBonus( referenceFrame );
			}
			if( m_sgVisual==null ) {
				m_sgVisual = new edu.cmu.cs.stage3.alice.scenegraph.Visual();
				m_sgVisual.setFrontFacingAppearance( m_sgAppearance );
				m_sgVisual.setIsShowing( false );
				m_sgVisual.setBonus( referenceFrame );
			}
			m_sgVisual.setParent( referenceFrame.getSceneGraphContainer() );
		}
	}

	protected void updateIfShowing() {
		if( isShowing() ) {
			update();
		}
	}
    public void internalRelease( int pass ) {
        switch( pass ) {
        case 1:
            if( m_sgVisual != null ) {
                m_sgVisual.setFrontFacingAppearance( null );
                m_sgVisual.setGeometry( null );
                m_sgVisual.setParent( null );
            }
            break;
        case 2:
            if( m_sgVisual != null ) {
                m_sgVisual.release();
                m_sgVisual = null;
            }
            if( m_sgAppearance != null ) {
                m_sgAppearance.release();
                m_sgAppearance = null;
            }
            break;
        }
    }

	public boolean isShowing() {
		if( m_sgVisual==null ) {
			return false;
		} else {
			return m_sgVisual.getIsShowing();
		}
	}
	public void setIsShowing( boolean value ) {
		if( value ) {
			update();
			showRightNow();
		} else {
			hideRightNow();
		}
	}

	public void setIsShowing( Boolean value ) {
		setIsShowing( value!=null && value.booleanValue() );
	}
	protected void showRightNow() {
		if( m_sgVisual != null ) {
			m_sgVisual.setIsShowing( true );
		}
	}
	protected void hideRightNow() {
		if( m_sgVisual != null ) {
			m_sgVisual.setIsShowing( false );
		}
	}
}
