package edu.cmu.cs.stage3.alice.core.behavior;

import edu.cmu.cs.stage3.alice.core.Camera;
import edu.cmu.cs.stage3.alice.core.Direction;
import edu.cmu.cs.stage3.alice.core.RenderTarget;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.ElementProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.TransformableProperty;
import edu.cmu.cs.stage3.alice.core.navigation.KeyMapping;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Stage3
 * @author Ben Buchwald
 * @version 1.0
 */

public class KeyboardNavigationBehavior extends InternalResponseBehavior {

    private edu.cmu.cs.stage3.alice.core.RenderTarget renderTarget;

    private javax.vecmath.Vector3d speed = new javax.vecmath.Vector3d(0,0,0);
    private javax.vecmath.Vector3d turning = new javax.vecmath.Vector3d(0,0,0);

    public NumberProperty maxSpeed = new NumberProperty( this, "maxSpeed", new Double(15) );
    public NumberProperty maxTurning = new NumberProperty( this, "maxTurning", new Double(.15) );
    public NumberProperty speedAccel = new NumberProperty( this, "speedAccel", new Double(20) );
    public NumberProperty turningAccel = new NumberProperty( this, "turningAccel", new Double(.35) );
    public BooleanProperty stayOnGround = new BooleanProperty( this, "stayOnGround", Boolean.TRUE );
    public TransformableProperty subject = new TransformableProperty( this, "subject", null );
    public ElementProperty keyMap = new ElementProperty(this, "keyMap", null, KeyMapping.class );

	protected void disable() {
        renderTarget.removeKeyListener( ((KeyMapping)keyMap.get()) );
	}


   	protected void enable() {
        renderTarget.addKeyListener( ((KeyMapping)keyMap.get()) );
    }

	
	public void started( World world, double time ) {
		super.started( world, time );
		if( isEnabled.booleanValue() ) {
            RenderTarget[] rts = (RenderTarget[])world.getDescendants(RenderTarget.class);
            if (rts.length>0) {
                renderTarget = rts[0];
                if (subject.get()==null) {
                    Camera[] cameras = renderTarget.getCameras();
                    if (cameras.length>0)
                        subject.set(cameras[0]);
                }
            }
            if (keyMap.get()==null)
                keyMap.set(new KeyMapping());
            ((KeyMapping)keyMap.get()).cleanState();


            enable();


            //((KeyMapping)keyMap.get()).printInstructions();
		}
	}

	
	public void stopped( World world, double time ) {
		super.stopped( world, time );
		if( isEnabled.booleanValue() ) {
            disable();
		}
	}

	
	public void internalSchedule( double time, double dt ) {
		KeyMapping keyMapping = (KeyMapping)keyMap.getElementValue();
		int actions;
		if( keyMapping != null ) {
			actions = keyMapping.getActions();
		} else {
			actions = 0;
		}
        // accelerate if keys are pressed
        // decelerate otherwise
        if ((actions & KeyMapping.NAV_MOVEFORWARD) != 0)
            speed.z += dt*speedAccel.getNumberValue().doubleValue();
        else if ((actions & KeyMapping.NAV_MOVEBACKWARD) != 0)
            speed.z -= dt*speedAccel.getNumberValue().doubleValue();
        else if (Math.abs(speed.z) >= dt*speedAccel.getNumberValue().doubleValue())
            speed.z += ((speed.z<0)?1:-1)*dt*speedAccel.getNumberValue().doubleValue();
        else
            speed.z = 0;
        if ((actions & KeyMapping.NAV_MOVELEFT) != 0)
            speed.x -= dt*speedAccel.getNumberValue().doubleValue();
        else if ((actions & KeyMapping.NAV_MOVERIGHT) != 0)
            speed.x += dt*speedAccel.getNumberValue().doubleValue();
        else if (Math.abs(speed.x) >= dt*speedAccel.getNumberValue().doubleValue())
            speed.x += ((speed.x<0)?1:-1)*dt*speedAccel.getNumberValue().doubleValue();
        else
            speed.x = 0;
        if ((actions & KeyMapping.NAV_MOVEUP) != 0)
            speed.y += dt*speedAccel.getNumberValue().doubleValue();
        else if ((actions & KeyMapping.NAV_MOVEDOWN) != 0)
            speed.y -= dt*speedAccel.getNumberValue().doubleValue();
        else if (Math.abs(speed.y) >= dt*speedAccel.getNumberValue().doubleValue())
            speed.y += ((speed.y<0)?1:-1)*dt*speedAccel.getNumberValue().doubleValue();
        else
            speed.y = 0;
        if ((actions & KeyMapping.NAV_TURNLEFT) != 0)
            turning.y -= dt*turningAccel.getNumberValue().doubleValue();
        else if ((actions & KeyMapping.NAV_TURNRIGHT) != 0)
            turning.y += dt*turningAccel.getNumberValue().doubleValue();
        else if (Math.abs(turning.y) >= dt*turningAccel.getNumberValue().doubleValue())
            turning.y += ((turning.y<0)?1:-1)*dt*turningAccel.getNumberValue().doubleValue();
        else
            turning.y = 0;
        if ((actions & KeyMapping.NAV_TURNUP) != 0)
            turning.x -= dt*turningAccel.getNumberValue().doubleValue();
        else if ((actions & KeyMapping.NAV_TURNDOWN) != 0)
            turning.x += dt*turningAccel.getNumberValue().doubleValue();
        else if (Math.abs(turning.x) >= dt*turningAccel.getNumberValue().doubleValue())
            turning.x += ((turning.x<0)?1:-1)*dt*turningAccel.getNumberValue().doubleValue();
        else
            turning.x = 0;
        if ((actions & KeyMapping.NAV_ROLLLEFT) != 0)
            turning.z += dt*turningAccel.getNumberValue().doubleValue();
        else if ((actions & KeyMapping.NAV_ROLLRIGHT) != 0)
            turning.z -= dt*turningAccel.getNumberValue().doubleValue();
        else if (Math.abs(turning.z) >= dt*turningAccel.getNumberValue().doubleValue())
            turning.z += ((turning.z<0)?1:-1)*dt*turningAccel.getNumberValue().doubleValue();
        else
            turning.z = 0;
        if ((actions & KeyMapping.NAV_HEADSUP) != 0) {
        }

        // obey maximums
        if (speed.x < -maxSpeed.getNumberValue().doubleValue())
            speed.x = -maxSpeed.getNumberValue().doubleValue();
        if (speed.x > maxSpeed.getNumberValue().doubleValue())
            speed.x = maxSpeed.getNumberValue().doubleValue();
        if (speed.y < -maxSpeed.getNumberValue().doubleValue())
            speed.y = -maxSpeed.getNumberValue().doubleValue();
        if (speed.y > maxSpeed.getNumberValue().doubleValue())
            speed.y = maxSpeed.getNumberValue().doubleValue();
        if (speed.z < -maxSpeed.getNumberValue().doubleValue())
            speed.z = -maxSpeed.getNumberValue().doubleValue();
        if (speed.z > maxSpeed.getNumberValue().doubleValue())
            speed.z = maxSpeed.getNumberValue().doubleValue();
        if (turning.x < -maxTurning.getNumberValue().doubleValue())
            turning.x = -maxTurning.getNumberValue().doubleValue();
        if (turning.x > maxTurning.getNumberValue().doubleValue())
            turning.x = maxTurning.getNumberValue().doubleValue();
        if (turning.y < -maxTurning.getNumberValue().doubleValue())
            turning.y = -maxTurning.getNumberValue().doubleValue();
        if (turning.y > maxTurning.getNumberValue().doubleValue())
            turning.y = maxTurning.getNumberValue().doubleValue();
        if (turning.z < -maxTurning.getNumberValue().doubleValue())
            turning.z = -maxTurning.getNumberValue().doubleValue();
        if (turning.z > maxTurning.getNumberValue().doubleValue())
            turning.z = maxTurning.getNumberValue().doubleValue();

        // move
        javax.vecmath.Vector3d vector = new javax.vecmath.Vector3d( dt*speed.x, dt*speed.y, dt*speed.z );
        Transformable subjectTransformable = subject.getTransformableValue();
		try {
			vector = subjectTransformable.preventPassingThroughOtherObjects( vector, 2 );
		} catch( Throwable t ) {
			//pass
		}
        subjectTransformable.moveRightNow( vector );
        /*
        double yPos = ((Transformable)subject.get()).getPosition().getItem(1);
        ((Transformable)subject.get()).moveRightNow(Direction.FORWARD,dt*speed.z);
        if (((Boolean)stayOnGround.get()).booleanValue()) {
            Vector3 pos = ((Transformable)subject.get()).getPosition();
            pos.setItem(1,yPos);
            ((Transformable)subject.get()).setPositionRightNow(pos);
        }
        ((Transformable)subject.get()).moveRightNow(Direction.RIGHT,dt*speed.x);
        ((Transformable)subject.get()).moveRightNow(Direction.UP,dt*speed.y);
        */
        ((Transformable)subject.get()).turnRightNow(Direction.FORWARD,dt*turning.x);

        if (((Boolean)stayOnGround.get()).booleanValue()) {
            Transformable t = new Transformable();
            t.setPositionRightNow(((Transformable)subject.get()).getPosition(((Transformable)subject.get()).getWorld()));
            //t.setOrientationRightNow(((Transformable)subject.get()).getWorld().getOrientationAsQuaternion());
            ((Transformable)subject.get()).turnRightNow(Direction.RIGHT,dt*turning.y,t);
        } else {
            ((Transformable)subject.get()).turnRightNow(Direction.RIGHT,dt*turning.y);
        }

        ((Transformable)subject.get()).rollRightNow(Direction.LEFT,dt*turning.z);
    }
}