package com.alexm.game.stonepits.manager;

import com.alexm.game.stonepits.StonePits;
import com.alexm.game.stonepits.entity.component.Bounds;
import com.alexm.game.stonepits.entity.component.Pit;
import com.alexm.game.stonepits.entity.component.StonePosition;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.managers.PlayerManager;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.kotcrab.vis.runtime.component.*;
import com.kotcrab.vis.runtime.system.VisGroupManager;
import com.kotcrab.vis.runtime.system.render.RenderBatchingSystem;

import java.util.LinkedList;

/**
 * User: Oleksandr Malynskyi
 * Date: 03/24/17
 */
public class GameSceneManager extends  BaseSceneManager {

    private PlayerManager playerManager;
    private Entity star1;
    private Entity star2;
    private Entity gameover;
    private Entity won;
    private Entity play1;
    private Entity play2;
    private Entity turnText;


    private LinkedList<Entity> p1Pits = new LinkedList<>();
    private LinkedList<Entity> p2Pits = new LinkedList<>();
    private Entity bp1, bp2;
    private LinkedList<Entity> p1SmallPits;
    private LinkedList<Entity> p2SmallPits;

    public final String P1_TURN = "Player 1 turn \n - click to play";
    public final String P2_TURN = "Player 2 turn \n - click to play";

    private ComponentMapper<Pit> pitCm;
    private ComponentMapper<VisText> textCm;
    private VisGroupManager groupManager;

    private RenderBatchingSystem renderSystem;

    LinkedList<Entity> flyStones = new LinkedList<>();

    public static final String PL1 = "PLAYER1", PL2 = "PLAYER2";

    private String whoseTurn = PL1;

    private Pit lastPit;
    private Entity highlightedPit;
    private Color highlightedColor;

    /**
     * Create instance passing game to pull threads
     *
     * @param game application
     */
    public GameSceneManager(StonePits game) {
        super(game);
    }

    @Override
    public void afterSceneInit() {
        super.afterSceneInit();

        soundManager.playMainTheme();

        for(int i=1 ; i<=6 ; i++) {
            p1Pits.add(idManager.get("sp1_" + i));
            p2Pits.add(idManager.get("sp2_" + i));
        }
        bp1 = idManager.get("bp_1");
        p1SmallPits = new LinkedList<>(p1Pits);
        p1Pits.add(bp1);
        bp2 = idManager.get("bp_2");
        p2SmallPits = new LinkedList<>(p2Pits);
        p2Pits.add(bp2);

        star1 = idManager.get("star1");
        star1.edit().add(new Invisible());

        star2 = idManager.get("star2");
        star2.edit().add(new Invisible());

        gameover = idManager.get("gameover");
        gameover.edit().add(new Invisible());

        won = idManager.get("won");
        won.edit().add(new Invisible());

        play1 = idManager.get("play1");
        play1.edit().add(new Invisible());

        play2 = idManager.get("play2");
        play2.edit().add(new Invisible());

        turnText = idManager.get("turn");
        idManager.get("p2GO").edit().add(new Invisible());
        idManager.get("p1GO").edit().add(new Invisible());

        game.gameMessage();
    }

    public void switchTurn(String player){
        game.gameMessage();

        if(player == null)
            whoseTurn = whoseTurn == null ? PL1 : whoseTurn.equals(PL1) ? PL2 : PL1;
        else
            whoseTurn = player;

        textCm.get(turnText).setText(whoseTurn.equals(PL1) ? P1_TURN : P2_TURN);

        turnText.edit().remove(Invisible.class);

    }


    public void gameOver(){

        soundManager.playOver();

        star1.edit().remove(Invisible.class);
        star2.edit().remove(Invisible.class);
        gameover.edit().remove(Invisible.class);
        won.edit().remove(Invisible.class);

        // Winner of the game is the player who has the most stones in his big pit.
        if(bp1.getComponent(Pit.class).getStones().size() > bp2.getComponent(Pit.class).getStones().size()){
            play1.edit().remove(Invisible.class);
        }else {
            play2.edit().remove(Invisible.class);
        }

        game.gameOver();
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        if(game.isGameRunning()) {
            unprojectVec.set(screenX, screenY, 0);
            cameraManager.getCamera().unproject(unprojectVec);

            float x = unprojectVec.x;
            float y = unprojectVec.y;

            if (button == Input.Buttons.MIDDLE) {

                if (flyStones.size() > 0) {
                    Pit pit = findPit(x, y, whoseTurn);
                    if (pit != null) {

                        // sows the stones on to the right,
                        // ONE in each of the following pits, including his own big pit.
                        Entity nextPit = findNextPit(lastPit);

                        if(pit.getEntity().equals(nextPit)) {
                            dropStone(pit);
                        }else if(highlightedColor == null){
                            highlightedPit = nextPit;
                            highlightedColor = highlightedPit.getComponent(Tint.class).getTint();
                            highlightedPit.getComponent(Tint.class).setTint(Color.SALMON);
                        }
                    }
                }

            } else if (button == Input.Buttons.LEFT) {

                if (flyStones.size() == 0) {

                    Pit pit = findPit(x, y, whoseTurn);

                    //capture is possible only from FULL small pits
                    if (pit != null && !pit.getEntity().equals(bp1) && !pit.getEntity().equals(bp2)
                            && pit.getStones().size() > 0) {
                        captureStones(x, y, pit);
                    }

                }

            }
        }else if(game.isGameMessage()){
            turnText.edit().add(new Invisible());
            game.gameRunning();
        }
        return true;
    }

    public Entity findNextPit(Pit pit){
        return whoseTurn.equals(PL1)
                      ? p1Pits.listIterator(
                          p1Pits.indexOf(pit.getEntity()) == p1Pits.size()-1
                              ? 0 : p1Pits.indexOf(pit.getEntity()) + 1).next()
                      : p2Pits.listIterator(
                          p2Pits.indexOf(pit.getEntity()) == p2Pits.size()-1
                              ? 0 : p2Pits.indexOf(pit.getEntity()) + 1).next();
    }

    public void dropStone(Pit pit){

        if(flyStones.size() > 0) {
            pit.putStoneInAPit(flyStones.removeLast());

            if (highlightedColor != null) {
                highlightedPit.getComponent(Tint.class).setTint(highlightedColor);
                highlightedColor = null;
                highlightedPit = null;
            }
            soundManager.playDrop();

            lastPit = pit;

            if (flyStones.size() == 0) {
                checkRules();
            }
        }
    }

    public void checkRules(){

        if (flyStones.size() == 0) {

            //Check for game over
            boolean isEmpty = true;

            //The game is over as soon as one of the sides run out of stones.
            for (Entity smallPit : whoseTurn.equals(PL1) ? p1SmallPits : p2SmallPits) {
                if (smallPit.getComponent(Pit.class).getStones().size() > 0) {
                    isEmpty = false;
                    break;
                }
            }

            if (isEmpty) {
                Pit bp = whoseTurn.equals(PL1) ? bp2.getComponent(Pit.class) : bp1.getComponent(Pit.class);
                // The player who still has stones in his pits keeps them and puts them in his/hers big pit.
                for (Entity smallPit : whoseTurn.equals(PL1) ? p2SmallPits : p1SmallPits) {
                    for (StonePosition stone : smallPit.getComponent(Pit.class).getStones()) {
                        bp.putStoneInAPit(stone.getStone());
                        soundManager.playDrop();
                    }
                }
                gameOver();
                return;
            }


            //If the player's last stone lands in his own big pit, he gets another turn.
            if (lastPit.isBigPit()) {
                switchTurn(whoseTurn);
                return;
            }

            //During the game the pits are emptied on both sides. Always when the last stone
            //lands in an own empty pit, the player captures his own stone and all stones in the
            //opposite pit (the other players' pit) and puts them in his own pit.
            if(lastPit.getStones().size() == 1){
                float x = lastPit.getEntity().getComponent(Bounds.class).getX();
                Pit oppositePit = null;
                for(Entity opponentPit : whoseTurn.equals(PL1) ? p2SmallPits : p1SmallPits){
                    if(opponentPit.getComponent(Bounds.class).getX() == x){
                        oppositePit = opponentPit.getComponent(Pit.class);
                        break;
                    }
                }
                if(oppositePit != null && oppositePit.getStones().size() > 0){
                    for(StonePosition sp : oppositePit.getStones()){
                        lastPit.putStoneInAPit(sp.getStone());
                        soundManager.playDrop();
                    }
                    oppositePit.clearPit();
                    highlightedPit = oppositePit.getEntity();
                    highlightedColor = highlightedPit.getComponent(Tint.class).getTint();
                    highlightedPit.getComponent(Tint.class).setTint(Color.SALMON);
                }
            }

            //switch turn
            switchTurn(null);
        }
    }

    protected void captureStones(float x, float y, Pit pit){

        if (highlightedColor != null) {
            highlightedPit.getComponent(Tint.class).setTint(highlightedColor);
            highlightedColor = null;
            highlightedPit = null;
        }

        Transform transform;
        LinkedList<StonePosition> content = pit.getStones();
        float deltaX = x - content.getFirst().getX();
        float deltaY = y - content.getFirst().getY();

        for(StonePosition pos : content){
            transform = transformCm.get(pos.getStone());
            transform.setPosition(transform.getX() + deltaX, transform.getY() + deltaY);
            pos.getStone().getComponent(Renderable.class).zIndex=1;
            flyStones.add(pos.getStone());
        }

        pit.clearPit();

        renderSystem.markDirty();

        soundManager.playGrab();

        //to know the next possible for drop
        lastPit = pit;
    }

    public Pit findPit(float x, float y, String playerId){

        for(Entity pit : playerManager.getEntitiesOfPlayer(playerId)){
            if(boundsCm.get(pit).contains(x,y))
                return pitCm.get(pit);
        }

        return null;
    }

    public void flyStones(float x, float y){
        if(flyStones != null && flyStones.size() > 0){
            Transform transform;
            float deltaX = x - transformCm.get(flyStones.get(0)).getX();
            float deltaY = y - transformCm.get(flyStones.get(0)).getY();

            for(Entity stone : flyStones){
                transform = transformCm.get(stone);
                transform.setPosition(transform.getX() + deltaX, transform.getY() + deltaY);
            }
            renderSystem.markDirty();
        }
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        unprojectVec.set(screenX, screenY, 0);
        cameraManager.getCamera().unproject(unprojectVec);

      	float x = unprojectVec.x;
      	float y = unprojectVec.y;

        flyStones(x, y);

        return false;
    }
}
