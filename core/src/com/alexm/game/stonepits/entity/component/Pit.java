package com.alexm.game.stonepits.entity.component;

import com.artemis.Component;
import com.artemis.Entity;
import com.kotcrab.vis.runtime.component.Renderable;
import com.kotcrab.vis.runtime.component.Transform;
import com.kotcrab.vis.runtime.component.VisText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * User: Oleksandr Malynskyi
 * Date: 03/24/17
 */
public class Pit extends Component {

    private Entity entity;
    private boolean isBigPit = false;

    private Entity pitScoreText;

    private LinkedList<StonePosition> corePositions = new LinkedList<StonePosition>(
                Arrays.asList(
                        new StonePosition[]{
                                new StonePosition(),new StonePosition(),new StonePosition(),
                                new StonePosition(),new StonePosition(),new StonePosition()
                        })
        );

    private LinkedList<StonePosition> stoneList = new LinkedList<StonePosition>();

    public Entity getPitScoreText() {
        return pitScoreText;
    }

    public void setPitScoreText(Entity pitScoreText) {
        this.pitScoreText = pitScoreText;
    }

    public void clearPit(){
        stoneList = new LinkedList<StonePosition>();
        pitScoreText.getComponent(VisText.class).setText(String.valueOf(stoneList.size()));
    }

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

    public LinkedList<StonePosition> getCorePositions(){
        return corePositions;
    }

    public LinkedList<StonePosition> getStones(){
        return stoneList;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public void markAsBigPit(boolean isBigPit) {
        this.isBigPit = isBigPit;
    }

    public boolean isBigPit() {
        return isBigPit;
    }

}
