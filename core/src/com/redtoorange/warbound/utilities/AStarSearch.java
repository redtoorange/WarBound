package com.redtoorange.warbound.utilities;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.redtoorange.warbound.map.MapTile;
import com.redtoorange.warbound.map.WeightedGraph;
import com.redtoorange.warbound.units.Unit;

/**
 * AStarSearch.java - Description
 *
 * @author Andrew McGuiness
 * @version 6/22/2017
 */
public class AStarSearch {
    public ObjectMap< MapTile, MapTile > cameFrom = new ObjectMap< MapTile, MapTile >();
    public ObjectMap< MapTile, Double > costSoFar = new ObjectMap< MapTile, Double >();

    public Array< MapTile > path = new Array< MapTile >();

    /**
     * Calculate a path from start to goal on the given graph.
     *
     * @param graph Source map
     * @param start Starting Tile
     * @param goal  Ending Tile
     */
    public AStarSearch( WeightedGraph< MapTile > graph, MapTile start, MapTile goal ) {
        if( start == null || goal == null) {
            System.out.println( "**Error finding path!**" );
            System.out.println( "\tstart: " + start );
            System.out.println( "\tgoal: " + goal );
            return;
        }

        PriorityQueue< MapTile > frontier = new PriorityQueue< MapTile >();
        frontier.Enqueue( start, 0 );

        cameFrom.put( start, start );
        costSoFar.put( start, 0.0 );

        while ( frontier.Count() > 0 ) {
            MapTile current = frontier.Dequeue();

            if ( current.equals( goal ) ) {
                break;
            }

            for ( MapTile next : graph.Neighbors( current ) ) {
                //Allow tiles are occupied by units, but they are much more costly.
                if ( !next.blocked() || ( next.isOccupied() && next.getOccupier() instanceof Unit ) ) {
                    double newCost = costSoFar.get( current ) + graph.Cost( current, next );
                    if ( !costSoFar.containsKey( next ) || newCost < costSoFar.get( next ) ) {
                        costSoFar.put( next, newCost );
                        double priority = newCost + Heuristic( next, goal );
                        frontier.Enqueue( next, priority );
                        cameFrom.put( next, current );
                    }
                }
            }
        }

        //The path has been found, reverse it and save it inside of path
        MapTile temp = goal;
        path.add( goal );
        boolean completePath = false;

        while ( cameFrom.containsKey( start ) ) {
            temp = cameFrom.get( temp );

            if ( temp == null )
                break;

            if ( temp.equals( start ) ) {
                completePath = true;
                break;
            } else
                path.add( temp );
        }

        if ( !completePath )
            path.clear();
    }

    /**
     * Calculate a Heuristic cost for the distance between two tiles.
     *
     * @param a Tile A
     * @param b Tile B
     * @return Heuristic coast of the movement
     */
    public static double Heuristic( MapTile a, MapTile b ) {
        double cost = Math.abs( a.getWorldPosition().x - b.getWorldPosition().x ) + Math.abs( a.getWorldPosition().y - b.getWorldPosition().y );

        if ( a.getMapX() != b.getMapX() && a.getMapY() != b.getMapY() )
            cost += 0.001;

        return cost;
    }
}
