package com.redtoorange.warbound.ai;

import com.redtoorange.warbound.units.Unit;

/**
 * UnitOrder.java - Description
 *
 * @author Andrew McGuiness
 * @version 6/22/2017
 */
public abstract class UnitOrder {
    protected boolean completed;

    public abstract void executed( float deltaTime );
    public abstract void cancelled();
    public abstract void recieved( Unit unit );
    public abstract void completed();

    public boolean isCompleted(){
        return completed;
    }
}
