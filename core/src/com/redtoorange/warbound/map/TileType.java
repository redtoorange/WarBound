package com.redtoorange.warbound.map;

/**
 * TileType.java - Description
 *
 * @author Andrew McGuiness
 * @version 6/22/2017
 */
public enum TileType {
    DIRT("dirt", 1), SAND("sand", 1), GRASS("grass", 1), STONE("stone", 9999);

    public final String key;
    public final float cost;

    TileType( String key, float cost){
        this.cost = cost;
        this.key = key;
    }

    public static TileType parseString( String type ){
        TileType tileType = DIRT;

        switch ( type ){
            case "sand": case "Sand": case "SAND":
                tileType = SAND;
                break;

            case "grass": case "Grass": case "GRASS":
                tileType = GRASS;
                break;

            case "stone": case "Stone": case "STONE":
                tileType = STONE;
                break;

            case "dirt": case "Dirt": case "DIRT":
                tileType = DIRT;
                break;
        }

        return tileType;
    }
}
