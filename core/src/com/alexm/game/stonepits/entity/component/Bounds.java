package com.alexm.game.stonepits.entity.component;

import com.artemis.Component;
import com.badlogic.gdx.math.Rectangle;

/**
 * Component holding sprites boundaries for ability sprite belongs actions
 */
public class Bounds extends Component {

    public Rectangle bounds = new Rectangle();

   	public Rectangle set (float x, float y, float width, float height) {
   		return bounds.set(x, y, width, height);
   	}

   	public float getY () {
   		return bounds.getY();
   	}

	public float getX () {
		return bounds.getX();
	}

    public boolean overlaps (Rectangle r) {
   		return bounds.overlaps(r);
   	}

    public Rectangle set (Rectangle rect) {
   		return bounds.set(rect);
   	}

    public boolean contains (float x, float y) {
   		return bounds.contains(x, y);
   	}

}
