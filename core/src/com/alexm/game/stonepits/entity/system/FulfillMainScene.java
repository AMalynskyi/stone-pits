package com.alexm.game.stonepits.entity.system;

import com.alexm.game.stonepits.StonePits;
import com.alexm.game.stonepits.entity.component.*;
import com.alexm.game.stonepits.manager.GameSceneManager;
import com.alexm.game.stonepits.manager.SoundManager;
import com.artemis.*;
import com.artemis.managers.PlayerManager;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.runtime.component.*;
import com.kotcrab.vis.runtime.system.VisGroupManager;
import com.kotcrab.vis.runtime.system.VisIDManager;
import com.kotcrab.vis.runtime.system.render.RenderBatchingSystem;

/**
 * Draw players score and stones ordering
 * User: Oleksandr Malynskyi
 * Date: 03/21/17
 */
public class FulfillMainScene extends BaseEntitySystem {

    private ComponentMapper<Bounds> boundsCm;
    private ComponentMapper<VisSprite> spriteCm;
    private ComponentMapper<Pit> smallPitCm;

    private PlayerManager playerManager;
    private VisGroupManager groupManager;
    private VisIDManager idManager;

    private StonePits game;
    private SoundManager soundManager;
    private RenderBatchingSystem renderSystem;


    private VisSprite stoneTemplate;

    private Array<Pit> sp1s = new Array<Pit>(), sp2s = new Array<Pit>();


    /**
     * Creates an entity system that uses the specified aspect as a matcher
     * against entities.
     *
     * @param aspect to match against entities
     */
    public FulfillMainScene(StonePits game) {
        super(Aspect.all(VisSprite.class));
        this.game = game;
        soundManager = game.getSoundManager();
    }


    @Override
    protected void begin() {

        Entity bp1 = idManager.get("bp_1");
        Entity bp2 = idManager.get("bp_2");

        stoneTemplate = spriteCm.get(idManager.get("stone"));

        Array<Entity> p1Pits =  groupManager.get("Player1Pits");
        for(Entity pit : p1Pits){
            playerManager.setPlayer(pit, GameSceneManager.PL1);

            if(!pit.equals(bp1)) {
                sp1s.add(smallPitCm.get(pit));
            }
        }

        Array<Entity> p2Pits =  groupManager.get("Player2Pits");

        for(Entity pit : p2Pits){
            playerManager.setPlayer(pit, GameSceneManager.PL2);

            if(!pit.equals(bp2)) {
                sp2s.add(smallPitCm.get(pit));
            }
        }

        Array<Pit> pits = new Array<Pit>();
        pits.addAll(sp1s);
        pits.addAll(sp2s);

        for(Pit pit : pits){

            for(int i=0; i < pit.getCorePositions().size() ; i++) {

                //PUT STONE IN A PIT
                VisSprite stoneSprite = new VisSprite(stoneTemplate);
                Transform stoneTransform = new Transform();

                Entity ent = world.createEntity();
                ent.edit()
                        .add(new Renderable(0))
                        .add(new Layer(2))
                        .add(stoneSprite)
                        .add(stoneTransform)
                        .add(new Origin())
                        .add(new Stone());
                pit.putStoneInAPit(ent);
            }
            soundManager.playDrop();

        }

        renderSystem.markDirty();

        setEnabled(false);
    }

    @Override
    protected void processSystem() {

    }
}
