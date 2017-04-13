package com.alexm.game.stonepits.entity.component;

import com.artemis.Component;
import com.artemis.Entity;
import com.kotcrab.vis.runtime.component.Renderable;
import com.kotcrab.vis.runtime.component.Transform;
import com.kotcrab.vis.runtime.component.VisText;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 *
 * Component that serves Pit entity
 */
public class Pit extends Component {

    private Entity entity;
    private boolean isBigPit = false;

    private Entity pitScoreText;

    /**
     * Initial list of Stones Positions inside of the Pit
     * It hold permanent coordinates for stones inside of pit and sequence for putting stone inside
     * Stones can be added or removed, but coordinates for every Pit should be kept unchanged all Pit's life
     * And every time when you put a new stone in a pit you need to take next coordinates in a sequence
     * and assign them to the stone object
     */
    private LinkedList<StonePosition> corePositions = new LinkedList<>(
                Arrays.asList(
                        new StonePosition[]{
                                new StonePosition(),new StonePosition(),new StonePosition(),
                                new StonePosition(),new StonePosition(),new StonePosition()
                        })
        );

    /**
     * List for changeable set of stones inside of pit
     * Adding or removing stones in a sequence of core positions coordinates
     */
    private LinkedList<StonePosition> stoneList = new LinkedList<>();

    /**
     * @return entity with the value of the stones amount inside of this pit
     */
    public Entity getPitScoreText() {
        return pitScoreText;
    }

    /**
     * Assign Text Entity for this pit stones amount
     * @param pitScoreText Gdx entity
     */
    public void setPitScoreText(Entity pitScoreText) {
        this.pitScoreText = pitScoreText;
    }

    /**
     * Clear pit of stones
     * E.g. when stones need to be moved to other player pit
     */
    public void clearPit(){
        stoneList = new LinkedList<>();
        pitScoreText.getComponent(VisText.class).setText(String.valueOf(stoneList.size()));
    }

    /**
     * For provided Stone entity:
     * - retrieve next position for new stone from core position list
     * - assign stone with coordinates
     * - add stone to the stones list
     * - reset z index, because it could be more than zero when stone is on the fly
     * to see flying stone in front of stones lying inside of pit
     * - count new value for pit score amount of stones
     * @param stone to add into current pit
     */
    public void putStoneInAPit(Entity stone){

        ListIterator<StonePosition> listIterator =
                corePositions.listIterator(stoneList.size() == 0 ? 0
                        :corePositions.indexOf(stoneList.getLast())+1);
        if(!listIterator.hasNext())
            listIterator = corePositions.listIterator(0);

        StonePosition corePos = listIterator.next();
        StonePosition pos = new StonePosition();
        pos.setCoordinates(corePos.getX(), corePos.getY());
        pos.setStone(stone);
        stoneList.add(pos);

        stone.getComponent(Renderable.class).zIndex=0;

        Transform transform = stone.getComponent(Transform.class);
        transform.setPosition(pos.getX(), pos.getY());

        pitScoreText.getComponent(VisText.class).setText(String.valueOf(stoneList.size()));
    }

    /**
     * @return permanent initial positions sequence
     */
    public LinkedList<StonePosition> getCorePositions(){
        return corePositions;
    }

    /**
     * @return list of stones that are lying inside pit
     */
    public LinkedList<StonePosition> getStones(){
        return stoneList;
    }

    /**
     * @return pit Gdx Entity object that store Entity Id for comparing and retrieving Gdx engine objects\info
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * Assign source Gdx entity object like an owner of Pit component
     * @param entity pit
     */
    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    /**
     * For easiest way of determining whether current pit is Big or Small
     *
     * Big and Small pit holds pretty same attributes to implement a separate pit type
     * simple flag would be enough
     *
     * @param isBigPit true if it is a Big pit
     */
    public void markAsBigPit(boolean isBigPit) {
        this.isBigPit = isBigPit;
    }

    /**
     * Understanding pit size\type
     * @return true if it's a big pit
     */
    public boolean isBigPit() {
        return isBigPit;
    }

}
