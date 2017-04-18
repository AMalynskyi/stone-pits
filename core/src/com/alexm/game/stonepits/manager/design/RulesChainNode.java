package com.alexm.game.stonepits.manager.design;

/**
 * Abstract class for game rules check node in a chain
 * Implement it to build a chain of specific game play rules
 *
 * Implements <b>Chain Of Responsibility Object Behavioral Design Pattern</b>
 */
public abstract class RulesChainNode {

    /**
     * Check game play rule and do appropriate actions inside
     * @return true if rule was succeeded
     */
    public abstract boolean checkRule();

    /**
     * Implementation of chain responsibility logic:
     * Process next rule in a chain, if current rule node was not accomplished
     * @param chain reference to chain for next rule execution
     * @return true when some rule was satisfied
     */
    public boolean process(ChainLinkedList<RulesChainNode> chain){

        if(!checkRule()){
            if(chain.hasNextTo(this))
                return chain.nextTo(this).process(chain);
            else
                return false;
        }else{
            return true;
        }
    }
}
