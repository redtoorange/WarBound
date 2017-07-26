package com.redtoorange.warbound.utilities;

import com.badlogic.gdx.utils.Array;

/**
 * PriorityQueue.java - Description
 *
 * @author Andrew McGuiness
 * @version 6/22/2017
 */
public class PriorityQueue<T> {

    private Array<Tuple<T, Double>> elements = new Array<Tuple<T, Double>>();

    public int Count(){
        return elements.size;
    }

    public void Enqueue(T item, double priority){
        elements.add( new Tuple(item, priority) );
    }

    public T Dequeue(){
        int bestIndex = 0;

        for (int i = 0; i < elements.size; i++) {
            if (elements.get( i ).y < elements.get(bestIndex).y) {
                bestIndex = i;
            }
        }

        T bestItem = elements.get(bestIndex).x;
        elements.removeIndex(bestIndex);

        return bestItem;
    }
}
