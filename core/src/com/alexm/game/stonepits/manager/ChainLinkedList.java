package com.alexm.game.stonepits.manager;

import java.util.LinkedList;
import java.util.ListIterator;

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

    public boolean hasNextTo(T node){
        return indexOf(node) < size()-1;
    }
}
