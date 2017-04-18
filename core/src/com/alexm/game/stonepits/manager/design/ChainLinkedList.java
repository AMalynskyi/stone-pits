package com.alexm.game.stonepits.manager.design;

import java.util.LinkedList;

/**
 * List with useful method
 */
public class ChainLinkedList<T> extends LinkedList<T>{

    /**
     * When you need to get an object next to current
     * @param node current object
     * @return next to current in a sequence
     */
    public T nextTo(T node){
        return hasNextTo(node) ? listIterator(indexOf(node)+1).next() : null;
    }

    /**
     * If there another node exist after current
     * @param node current
     * @return true if exist
     */
    public boolean hasNextTo(T node){
        return indexOf(node) < size()-1;
    }
}
