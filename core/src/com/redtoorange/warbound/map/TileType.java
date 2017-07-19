package com.redtoorange.warbound.map;

/**
 * TileType.java - Description
 *
 * @author Andrew McGuiness
 * @version 6/22/2017
 */
public enum TileType {
    DIRT("dirt", 1), SAND("sand", 2f), GRASS("grass", 1), STONE("stone", 9999);

    public final String key;
    public final float cost;

    TileType( String key, float cost){
        this.cost = cost;
        this.key = key;
    }
}
