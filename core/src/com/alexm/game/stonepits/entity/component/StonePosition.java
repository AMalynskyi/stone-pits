package com.alexm.game.stonepits.entity.component;

import com.artemis.Component;
import com.artemis.Entity;

/**
 * User: Oleksandr Malynskyi
 * Date: 03/25/17
 */
public class StonePosition extends Component{
    private float x;
    private float y;
    private Entity stone;

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

    public Entity getStone() {
        return stone;
    }

    public void setStone(Entity stone) {
        this.stone = stone;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof StonePosition && this.x == ((StonePosition)obj).x && this.y == ((StonePosition)obj).y;
    }
}
