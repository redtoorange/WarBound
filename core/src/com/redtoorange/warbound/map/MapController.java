package com.redtoorange.warbound.map;

import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.redtoorange.warbound.buildings.BuildingType;
import com.redtoorange.warbound.units.UnitType;
import com.redtoorange.warbound.utilities.PerlinNoiseGenerator;

/**
 * MapController.java - Description
 *
 * @author Andrew McGuiness
 * @version 6/21/2017
 */
public class MapController implements WeightedGraph<MapTile>{
    private TextureAtlas tileAtlas;
    private float[][] noiseMap;

    private MapTile[][] tiles;
    private int width;
    private int height;
    private float startx;
    private float starty;

    public ObjectMap<MapTile, UnitType> unitSpawns = new ObjectMap<MapTile, UnitType>(  );
    public ObjectMap<MapTile, BuildingType> buildingSpawns = new ObjectMap<MapTile, BuildingType>(  );

    public MapController( String name, float startx, float starty ){
        TiledMap tiledMap = new TmxMapLoader( new InternalFileHandleResolver() ).load( name );
        TiledMapTileLayer groundLayer = (TiledMapTileLayer ) tiledMap.getLayers().get( "ground" );
//        TiledMapTileSet tileSet = tiledMap.getTileSets().getTileSet( 0 );

        this.width = groundLayer.getWidth();
        this.height = groundLayer.getHeight();


        tiles = new MapTile[width][height];



        this.startx = startx;
        this.starty = starty;

        for( int x = 0; x < width; x++){
            for( int y = 0; y < height; y++){
                TiledMapTile tile = groundLayer.getCell( x, y ).getTile();
                TileType type = TileType.parseString( tile.getProperties().get( "type", String.class ) );

                tiles[x][y] = new MapTile( x + startx, y + starty, x, y, tile.getTextureRegion(), this, type);
            }
        }

        buildTraversalGraph( width, height );

        float tileWidth = tiledMap.getProperties().get( "tilewidth", Integer.class );
        float tileHeight = tiledMap.getProperties().get( "tileheight", Integer.class );

        MapObjects unitObjects = tiledMap.getLayers().get( "units" ).getObjects();
        for( RectangleMapObject obj : unitObjects.getByType( RectangleMapObject.class ) ){
            float ox = obj.getRectangle().x / tileWidth;
            float oy = obj.getRectangle().y / tileHeight;

            MapTile t = getTileByGridPos( MathUtils.floor( ox ), MathUtils.floor( oy ) );
            String s = obj.getProperties().get( "Unit", String.class );

            unitSpawns.put( t, UnitType.parseString( s ));
        }

        MapObjects buildingObjects = tiledMap.getLayers().get( "buildings" ).getObjects();
        for( RectangleMapObject obj : buildingObjects.getByType( RectangleMapObject.class ) ){
            float ox = (obj.getRectangle().x / tileWidth) + ((obj.getRectangle().width / tileWidth) / 2);
            float oy = (obj.getRectangle().y / tileHeight) + ((obj.getRectangle().height / tileHeight) / 2);

            MapTile t = getTileByGridPos( MathUtils.floor( ox ), MathUtils.floor( oy ) );
            String s = obj.getProperties().get( "building", String.class );

            buildingSpawns.put( t, BuildingType.parseString( s ));
        }
    }

    public MapController( float startx, float starty, int width, int height){
        tileAtlas = new TextureAtlas( "tiles/Tiles.pack" );
        tiles = new MapTile[width][height];

        this.width = width;
        this.height = height;

        this.startx = startx;
        this.starty = starty;

        noiseMap = PerlinNoiseGenerator.generatePerlinNoise( width, height, 3 );

        buildMap( startx, starty, width, height );
        buildTraversalGraph( width, height );
    }

    private void buildMap( float startx, float starty, int width, int height ) {
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                tiles[x][y] = new MapTile( x + startx, y + starty, x, y,
                        this, genTileType( x, y ) );
            }
        }
    }

    private void buildTraversalGraph( int width, int height ) {
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                Array<MapTile> neighbors = new Array< MapTile >(  );

                //WEST
                if( x > 0){
                    neighbors.add( tiles[x-1][y] );
                }
                //SOUTH_WEST
                if( x > 0 && y > 0){
                    neighbors.add( tiles[x-1][y-1] );
                }
                //NORTH_WEST
                if( x > 0 && y < height-1){
                    neighbors.add( tiles[x-1][y+1] );
                }

                //EAST
                if(x < width-1){
                    neighbors.add( tiles[x+1][y] );
                }
                //SOUTH_EAST
                if( x < width-1 && y > 0){
                    neighbors.add( tiles[x+1][y-1] );
                }
                //NORTH_EAST
                if( x < width-1 && y < height-1){
                    neighbors.add( tiles[x+1][y+1] );
                }

                //SOUTH
                if( y > 0){
                    neighbors.add( tiles[x][y-1] );
                }
                //NORTH
                if( y < height-1 ){
                    neighbors.add( tiles[x][y+1] );
                }

                tiles[x][y].setNeighbors( neighbors );
            }
        }
    }

    public void update( float deltaTime ){
        for(int y = height-1; y >= 0; y--){
            for(int x = 0; x < width; x++){
                if( tiles[x][y] != null){
                    tiles[x][y].update( deltaTime );
                }
            }
        }
    }

    public void draw( SpriteBatch batch ){
        drawTiles( batch );
        drawDoodads( batch );
    }

    private void drawTiles( SpriteBatch batch ){
        for(int y = height-1; y >= 0; y--){
            for(int x = 0; x < width; x++){
                if( tiles[x][y] != null){
                    tiles[x][y].drawTile( batch );
                }
            }
        }
    }

    private void drawDoodads( SpriteBatch batch ){
        for(int y = height-1; y >= 0; y--){
            for(int x = 0; x < width; x++){
                if( tiles[x][y] != null){
                    tiles[x][y].drawDoodad( batch );
                }
            }
        }
    }

    /**
     * Find a tile in the map based on it's x,y position inside the internal map grid.
     * @param x grid position
     * @param y grid position
     * @return  null if x and y are outside of the map bounds.
     */
    public MapTile getTileByGridPos(int x, int y){
        MapTile t = null;

        if( validPosition( x, y ))
            t = tiles[x][y];

        return t;
    }

    public MapTile getTileByWorldPos(float worldX, float worldY){
        MapTile t = null;

        for(int y = 0; y < height && t == null; y++){
            for(int x = 0; x < width && t == null; x++){
                if(tiles[x][y] != null && tiles[x][y].contains(worldX, worldY)){
                    t = tiles[x][y];
                }
            }
        }

        return t;
    }

    public MapTile getTileByWorldPos( Vector2 pos ){
        return getTileByWorldPos(pos.x, pos.y);
    }

    public void destroyTile( MapTile tile ){
        tiles[tile.getMapX()][tile.getMapY()] = null;
    }

    public TextureRegion getTexture( TileType type ){
        return tileAtlas.findRegion( type.key + MathUtils.random( 1, 2 ) );
    }


    public TileType genTileType( int x, int y){
        float key = noiseMap[x][y];
        TileType type = TileType.DIRT;

        if( key <= 0.25f)
            type = TileType.DIRT;
        else if( key <= 0.5f)
            type = TileType.GRASS;
        else if( key <= 0.75f)
            type = TileType.SAND;
        else if( key > 0.75f)
            type = TileType.STONE;


        return type;
    }


    @Override
    public double Cost( MapTile a, MapTile b ) {
        if( b.blocked() )
            return 100000f;

        return Math.abs( a.getCost() - b.getCost() );
    }

    @Override
    public Iterable< MapTile > Neighbors( MapTile id ) {
        return id.getNeighbors();
    }

    public boolean validPosition( int x, int y){
        return (x >= 0 && x < width && y >= 0 && y < height);
    }
}
