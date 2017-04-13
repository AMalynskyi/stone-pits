package com.alexm.game.stonepits.entity.component;

import com.artemis.Component;
import com.badlogic.gdx.math.Rectangle;

/**
 * Component holding sprites boundaries
 * Using boundaries game play will determine actions belonging to this entity if actions coordinates
 * are inside of boundaries
 */
public class Bounds extends Component {

    public Rectangle bounds = new Rectangle();

	/**
	 * Initializing boundaries with coordinates
	 * @param x dimension coordinate
	 * @param y dimension coordinate
	 * @param width value
	 * @param height value
	 * @return boundaries rectangle
	 */
   	public Rectangle set (float x, float y, float width, float height) {
   		return bounds.set(x, y, width, height);
   	}

   	public float getY () {
   		return bounds.getY();
   	}

	public float getX () {
		return bounds.getX();
	}

	/**
	 * Does provided rectangle boundaries overlaps with current one
	 * @param r provided rectangle
	 * @return true if overlaps
	 */
    public boolean overlaps (Rectangle r) {
   		return bounds.overlaps(r);
   	}

	/**
	 * Whether provided x,y dimension point is inside of current boundaries or not
	 * @param x point coordinate
	 * @param y point coordinate
	 * @return true if point is inside of current bounds
	 */
    public boolean contains (float x, float y) {
   		return bounds.contains(x, y);
   	}

}
