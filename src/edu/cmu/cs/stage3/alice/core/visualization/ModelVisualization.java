package edu.cmu.cs.stage3.alice.core.visualization;

import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.alice.core.TextureMap;

public class ModelVisualization extends edu.cmu.cs.stage3.alice.core.Visualization {
    
	public void unhook( Model model ) {
        if( getItem()==model ) {
            setItem( null );
        }
    }
    private TextureMap getEmptyTextureMap() {
        return (TextureMap)getChildNamed( "EmptyTexture" );
    }
    private TextureMap getFilledTextureMap() {
        return (TextureMap)getChildNamed( "FilledTexture" );
    }
    private Variable m_itemVariable = null;
    private Variable getItemVariable() {
        if( m_itemVariable == null ) {
            m_itemVariable = (Variable)getChildNamed( "Item" );
        }
        return m_itemVariable;
    }
    public Model getItem() {
        return (Model)getItemVariable().value.getValue();
    }
    public void setItem( Model value ) {
        getItemVariable().value.set( value );
    }
    private void synchronize( Model curr ) {
        Model prev = getItem();
        if( prev != null && prev != curr ) {
            prev.visualization.set( null );
        }
        if( curr != null ) {
            curr.setTransformationRightNow( getTransformationFor( curr ), this );
            curr.vehicle.set( this, true );
            curr.visualization.set( this );
            diffuseColorMap.set( getFilledTextureMap() );
        } else {
            diffuseColorMap.set( getEmptyTextureMap() );
        }
    }
    
	protected void loadCompleted() {
        super.loadCompleted();
        Variable item = getItemVariable();
        item.value.addPropertyListener( new edu.cmu.cs.stage3.alice.core.event.PropertyListener() {
            public void propertyChanging( edu.cmu.cs.stage3.alice.core.event.PropertyEvent propertyEvent ) {
            }
            public void propertyChanged( edu.cmu.cs.stage3.alice.core.event.PropertyEvent propertyEvent ) {
                ModelVisualization.this.synchronize( (Model)propertyEvent.getValue() );
            }
        } );
        synchronize( getItem() );
    }
    public javax.vecmath.Matrix4d getTransformationFor( edu.cmu.cs.stage3.alice.core.Model model ) {
        javax.vecmath.Matrix4d m = new javax.vecmath.Matrix4d();
        m.setIdentity();
        if( model != null ) {
            edu.cmu.cs.stage3.math.Box box = model.getBoundingBox();
            javax.vecmath.Vector3d v = box.getCenterOfBottomFace();
            if( v!=null ) {
                v.negate();
                m.m30 = v.x;
                m.m31 = v.y;
                m.m32 = v.z;
            }
        }
        return m;
    }
}
