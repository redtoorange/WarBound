package com.redtoorange.warbound.ai;

import com.redtoorange.warbound.units.Unit;

/**
 * UnitOrder.java - Description
 *
 * @author Andrew McGuiness
 * @version 6/22/2017
 */
public abstract class UnitOrder {
    public Facing unitFacing = Facing.SOUTH;

    public abstract void executeOrder( float deltaTime );
    public abstract void cancelOrder();
    public abstract void receiveOrder( Unit unit );

    protected abstract void completeOrder();
    public abstract boolean isCompleted();
}
