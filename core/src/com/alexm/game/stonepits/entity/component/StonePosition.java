package com.alexm.game.stonepits.entity.component;

import com.artemis.Component;
import com.artemis.Entity;

/**
 * Component to serve Stone position in graphics
 */
public class StonePosition extends Component{
    private float x;
    private float y;
    private Entity stone;

    /**
     * Assign Stone coordinates
     * @param x coordinate
     * @param y coordinate
     */
    public void setCoordinates(float x, float y){
        this.x=x;
        this.y=y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    /**
     * @return Gdx Entity object that store Entity Id for comparing and retrieving Gdx engine objects\info
     */
    public Entity getStone() {
        return stone;
    }

    /**
     * Assign source Gdx entity object like an owner of Stone component
     * @param stone entity
     */
    public void setStone(Entity stone) {
        this.stone = stone;
    }

    /**
     * Overridden method to check whether provided position is the same as current in terms of coordinate
     * @param obj position to check
     * @return true if positions have the same coordinates
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof StonePosition && this.x == ((StonePosition)obj).x && this.y == ((StonePosition)obj).y;
    }
}
