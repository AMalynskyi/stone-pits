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
 * System for pits entities processing:
 * - calculate core stone positions coordinates
 * - calculate and adjust coordinates for pit score text entity
 */
public class PitsSystem extends EntityProcessingSystem {

    /*Injecting component mappers*/

    private ComponentMapper<Pit> pitsCm;
    private ComponentMapper<Bounds> boundsCm;
    private ComponentMapper<VisSprite> spriteCm;
    private ComponentMapper<VisText> textCm;
    private ComponentMapper<Tint> tintCm;
    private ComponentMapper<Transform> transformCm;

    /*Injecting managers*/

    private VisIDManager idManager;
    private VisGroupManager groupManager;

    /**
     * Creates a new PitsSystem with sprite aspect to react on a sprite object changes
     */
    public PitsSystem() {
        super(Aspect.all(VisSprite.class));
    }

    @Override
    public void removed(Entity e) {}

    /**
     * For every created in a game frame pit entities:
     * - create Pit component
     * - calculate stones core positions coordinates
     * - create Text entity for pit score text
     * - calculate Score Text entity coordinates on the frame for every pit
     * @param entity to process
     */
    @Override
    public void inserted(Entity entity) {

        VisID visID = entity.getComponent(VisID.class);

        //for all pits, small & big
        if(visID != null && (visID.id.startsWith("bp") || visID.id.startsWith("sp"))) {
            if (visID.id.startsWith("bp"))
                pitsCm.create(entity).markAsBigPit(true);
            else if (visID.id.startsWith("sp"))
                pitsCm.create(entity);

            Bounds pitRect = boundsCm.get(entity);

            VisSprite stoneSprite = spriteCm.get(idManager.get("stone"));

            Entity score = idManager.get("pitscore");
            VisText scoreTemplate = textCm.get(score);
            Transform scoreTransformTemp = transformCm.get(score);

            //Create Score Text entity from existing template and calculate it's coordinates

            Transform scoreTransform = new Transform(
                    pitRect.getX() + pitRect.bounds.getWidth()/2 - scoreTemplate.getWidth()/2,
                    pitRect.getY(),
                    scoreTransformTemp.getScaleX(),
                    scoreTransformTemp.getScaleY(),
                    scoreTransformTemp.getRotation()
            );

            VisText text = new VisText(scoreTemplate);

            Entity ent = world.createEntity();
            ent.edit()
                    .add(new Renderable(0))
                    .add(new Layer(2))
                    .add(text)
                    .add(new Tint(tintCm.get(score).getTint()))
                    .add(scoreTransform)
                    .add(new Origin());

            // Calculate initial core stone positions coordinates

            Pit pit = pitsCm.get(entity);
            pit.setEntity(entity);

            pit.setPitScoreText(ent);

            float stepX = pitRect.bounds.getWidth() / 5;
            float stepY = pitRect.bounds.getHeight() / 5;

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

    /**
     * During every render for Pit entity updating\processing adjust score text position
     * Because 1-symbol number text and 2-symbols number text need to be centered with different coordinates
     * @param e entity to process
     */
    @Override
    protected void process(Entity e) {

        VisID visID = e.getComponent(VisID.class);

        if(visID != null && (visID.id.startsWith("bp") || visID.id.startsWith("sp"))) {

            Bounds pitRect = boundsCm.get(e);

            Pit pit = pitsCm.get(e);

            VisText score = textCm.get(pit.getPitScoreText());

            Transform scoreTransform = pit.getPitScoreText().getComponent(Transform.class);
            scoreTransform.setPosition(
                    pitRect.getX() + pitRect.bounds.getWidth() / 2 - score.getWidth() / 2,
                    pitRect.getY());
        }
    }

}
