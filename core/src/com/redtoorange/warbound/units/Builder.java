package com.redtoorange.warbound.units;

import com.redtoorange.warbound.buildings.Building;
import com.redtoorange.warbound.map.MapTile;

/**
 * Builder.java - Description
 *
 * @author Andrew McGuiness
 * @version 8/2/2017
 */
public interface Builder {
    void enterBuilding();
    void exitBuilding();
    void moveToBuilding( Building building, MapTile destination );
    void ejectFromBuilding( MapTile mapTile );
}
