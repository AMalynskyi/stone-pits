package com.alexm.game.stonepits.entity.system;

import com.alexm.game.stonepits.entity.component.Bounds;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.kotcrab.vis.runtime.component.Transform;
import com.kotcrab.vis.runtime.component.VisSprite;

/**
 * System for sprite objects bounds fulfill
 * Every Sprite object need to be calculated to have own bounds component
 */
public class SpriteBoundsSystem extends IteratingSystem {

	/*Injections for component mappers*/

	private ComponentMapper<Bounds> boundsCm;
	private ComponentMapper<VisSprite> spriteCm;
	private ComponentMapper<Transform> transformCm;

	/**
	 * Dedicated for all VisSprite objects
	 */
	public SpriteBoundsSystem() {
		super(Aspect.all(VisSprite.class));
	}

	/**
	 * Calculate bounds coordinates for newly sprite entity
	 * @param entityId of sprite
	 */
	@Override
	protected void inserted(int entityId) {
		boundsCm.create(entityId);

		Transform transform = transformCm.get(entityId);
		VisSprite sprite = spriteCm.get(entityId);

		Bounds bounds = boundsCm.get(entityId);

		bounds.set(transform.getX(), transform.getY(), sprite.getWidth(), sprite.getHeight());
	}

	@Override
	protected void process(int entityId) {}
}
