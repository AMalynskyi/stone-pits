package com.alexm.game.stonepits.entity.system;

import com.alexm.game.stonepits.entity.component.Bounds;
import com.alexm.game.stonepits.entity.component.Pit;
import com.alexm.game.stonepits.entity.component.StonePosition;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.kotcrab.vis.runtime.component.*;
import com.kotcrab.vis.runtime.system.VisGroupManager;
import com.kotcrab.vis.runtime.system.VisIDManager;

import java.util.Iterator;

/**
 * User: Oleksandr Malynskyi
 * Date: 03/24/17
 */
public class PitsSystem extends EntityProcessingSystem {

    private ComponentMapper<Pit> pitsCm;
    private ComponentMapper<Bounds> boundsCm;
    private ComponentMapper<VisSprite> spriteCm;
    private ComponentMapper<VisText> textCm;
    private ComponentMapper<Tint> tintCm;
    private ComponentMapper<Transform> transformCm;

    private VisIDManager idManager;
    private VisGroupManager groupManager;

    /**
     * Creates a new EntityProcessingSystem.
     *
     * @param aspect the aspect to match entities
     */
    public PitsSystem() {
        super(Aspect.all(VisSprite.class));
    }

    @Override
    public void removed(Entity e) {
    }

    @Override
    public void inserted(Entity entity) {

        VisID visID = entity.getComponent(VisID.class);

        if(visID != null && (visID.id.startsWith("bp") || visID.id.startsWith("sp"))) {
            if (visID.id.startsWith("bp"))
                pitsCm.create(entity).markAsBigPit(true);
            else if (visID.id.startsWith("sp"))
                pitsCm.create(entity);

            Bounds pitRect = boundsCm.get(entity);

            VisSprite stoneTemplate = spriteCm.get(idManager.get("stone"));
            VisSprite stoneSprite = new VisSprite(stoneTemplate);
            VisText scoreTemplate = textCm.get(idManager.get("pitscore"));

            float stepX = pitRect.bounds.getWidth() / 5;
            float stepY = pitRect.bounds.getHeight() / 5;

            Pit pit = pitsCm.get(entity);
            pit.setEntity(entity);

            Transform scoreTransform = new Transform(
                    pitRect.getX() + pitRect.bounds.getWidth()/2 - scoreTemplate.getWidth()/2,
                    pitRect.getY(),
                    transformCm.get(idManager.get("pitscore")).getScaleX(),
                    transformCm.get(idManager.get("pitscore")).getScaleY(),
                    transformCm.get(idManager.get("pitscore")).getRotation()
            );

            VisText text = new VisText(scoreTemplate);

            Entity ent = world.createEntity();
            ent.edit()
                    .add(new Renderable(0))
                    .add(new Layer(2))
                    .add(text)
                    .add(new Tint(tintCm.get(idManager.get("pitscore")).getTint()))
                    .add(scoreTransform)
                    .add(new Origin());

            pit.setPitScoreText(ent);

            float stoneX = pitRect.bounds.getX(), stoneY = pitRect.bounds.getY();
            Iterator<StonePosition> iterator = pit.getCorePositions().iterator();

            StonePosition pos = iterator.next();

            stoneX = stoneX + stepX - stoneSprite.getWidth()/4;
            stoneY = stoneY + stepY ;
            pos.setCoordinates(stoneX, stoneY);

            pos = iterator.next();
            pos.setCoordinates(stoneX, stoneY + stepY );

            pos = iterator.next();
            stoneX = stoneX + stepX ;
            stoneY = stoneY + stepY ;
            pos.setCoordinates(stoneX, stoneY);

            pos = iterator.next();
            pos.setCoordinates(stoneX, stoneY + stepY );

            pos = iterator.next();
            stoneX = stoneX + stepX;
            stoneY = stoneY - stepY;
            pos.setCoordinates(stoneX, stoneY);

            pos = iterator.next();
            pos.setCoordinates(stoneX, stoneY + stepY);

        }
    }

    @Override
    protected void process(Entity e) {

        VisID visID = e.getComponent(VisID.class);

        if(visID != null && (visID.id.startsWith("bp") || visID.id.startsWith("sp"))) {

            Bounds pitRect = boundsCm.get(e);

            VisText score = textCm.get(e.getComponent(Pit.class).getPitScoreText());

            Pit pit = pitsCm.get(e);

            Transform scoreTransform = pit.getPitScoreText().getComponent(Transform.class);
            scoreTransform.setPosition(
                    pitRect.getX() + pitRect.bounds.getWidth() / 2 - score.getWidth() / 2,
                    pitRect.getY());
        }
    }

}
