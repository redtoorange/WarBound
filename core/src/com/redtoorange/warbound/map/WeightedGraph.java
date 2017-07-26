package com.redtoorange.warbound.map;

/**
 * WeightedGraph.java - Description
 *
 * @author Andrew McGuiness
 * @version 6/22/2017
 */
public interface WeightedGraph<T> {
    double Cost( T a, T b);
    Iterable<T> Neighbors(T id);
}
