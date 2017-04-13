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
 * System to perform initial game preparation:
 * - getting stone Sprite as the template
 * - creating every 6 stones for all small pits of both users
 * - mark rendering system as ready to draw
 */
public class FulfillMainScene extends BaseEntitySystem {

    /*
     * Injections of components mappers
     * Injections are driven by Artemis World class
     */

    /**Mapper to get Bounds for entity*/
    private ComponentMapper<Bounds> boundsCm;
    /**Mapper to get Sprite object for entity*/
    private ComponentMapper<VisSprite> spriteCm;
    /**Mapper to get Pit object for entity*/
    private ComponentMapper<Pit> pitCm;

    /**Manager to serve entities belonging to specific player*/
    private PlayerManager playerManager;
    /**Manager of entities groups with specific group id*/
    private VisGroupManager groupManager;
    /**Manager that allows retrieving of entities knowing VisId value assigned in VisEditor during scene design*/
    private VisIDManager idManager;

    /**Injection of render system*/
    private RenderBatchingSystem renderSystem;

    /**Reference to game application listener*/
    private StonePits game;
    /**Reference to sound manager*/
    private SoundManager soundManager;

    /**Small pits Arrays of both users*/
    private Array<Pit> sp1s = new Array<>(), sp2s = new Array<>();


    /**
     * Creates an entity system that uses VisSprite aspect as a matcher
     * against entities.
     *
     * @param game Reference to game application listener
     */
    public FulfillMainScene(StonePits game) {
        super(Aspect.all(VisSprite.class));
        this.game = game;
        soundManager = game.getSoundManager();
    }

    /**
     * On the very first step of system initiation do stones initial creation and disposition
     */
    @Override
    protected void begin() {

        Entity bp1 = idManager.get("bp_1");
        Entity bp2 = idManager.get("bp_2");

        //stone template
        VisSprite stoneTemplate = spriteCm.get(idManager.get("stone"));

        //collect all small pits of player1
        Array<Entity> p1Pits =  groupManager.get("Player1Pits");
        for(Entity pit : p1Pits){
            playerManager.setPlayer(pit, GameSceneManager.PL1);

            if(!pit.equals(bp1)) {
                sp1s.add(pitCm.get(pit));
            }
        }

        //collect all small pits of player2
        Array<Entity> p2Pits =  groupManager.get("Player2Pits");

        for(Entity pit : p2Pits){
            playerManager.setPlayer(pit, GameSceneManager.PL2);

            if(!pit.equals(bp2)) {
                sp2s.add(pitCm.get(pit));
            }
        }

        Array<Pit> pits = new Array<>();
        pits.addAll(sp1s);
        pits.addAll(sp2s);

        //for all players small pits create 6 stones and put them in a correct positions
        for(Pit pit : pits){

            for(int i=0; i < pit.getCorePositions().size() ; i++) {

                //create new sprite from template
                VisSprite stoneSprite = new VisSprite(stoneTemplate);
                //use transform component to set coordinates for graphics drawing
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
        //mark render system as required to draw
        renderSystem.markDirty();

        //that's it, current system did the job and should be disabled, not to execute every further rendering event
        setEnabled(false);
    }

    @Override
    protected void processSystem() {}
}
