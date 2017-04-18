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
 * Game Scene Managing Strategy to process all game actions
 * Implements <b>Strategy Behavioral Design Pattern</b>
 */
public class GameSceneManagerStrategy extends BaseSceneManagerStrategy {

    /*Keep references for game action entities*/
    private PlayerManager playerManager;
    private Entity star1;
    private Entity star2;
    private Entity gameover;
    private Entity won;
    private Entity play1;
    private Entity play2;
    private Entity turnText;

    /*Game objects*/
    private LinkedList<Entity> p1Pits = new LinkedList<>();
    private LinkedList<Entity> p2Pits = new LinkedList<>();
    private Entity bp1, bp2;
    private LinkedList<Entity> p1SmallPits;
    private LinkedList<Entity> p2SmallPits;

    /*Text for changing players turn*/
    public final String P1_TURN = "Player 1 turn \n - click to play";
    public final String P2_TURN = "Player 2 turn \n - click to play";

    /*Injections of Gdx mappers and managers*/
    private ComponentMapper<Pit> pitCm;
    private ComponentMapper<VisText> textCm;
    private VisGroupManager groupManager;
    private RenderBatchingSystem renderSystem;

    /*List of stones that are sticked to cursor*/
    LinkedList<Entity> flyStones = new LinkedList<>();

    /*Players turn indicators*/
    public static final String PL1 = "PLAYER1", PL2 = "PLAYER2";

    /*Current player turn*/
    private String whoseTurn = PL1;

    /*Last pit where stone was dropped*/
    private Pit lastPit;
    /*If player have tried to drop stone in a wrong pit, correct pit for drop is highlighted in a color*/
    private Entity highlightedPit;
    /*Color of highlighted pit*/
    private Color highlightedColor;

    /**
     * Create instance passing game to pull threads
     *
     * @param game application
     */
    public GameSceneManagerStrategy(StonePits game) {
        super(game);

        game.getStateHandlers().put(StonePits.GameState.RUNNING, new PlayStateHandler());
        game.getStateHandlers().put(StonePits.GameState.START, new StartedStateHandler());
    }

    /**
     * Prepare game scene and it's assets for playing
     */
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

    }

    /**
     * Change turn of players
     * @param player whose turn now, if null - than turn is calculated as another from current player
     */
    public void switchTurn(String player){
        game.gameStart();

        if(player == null)
            whoseTurn = whoseTurn == null ? PL1 : whoseTurn.equals(PL1) ? PL2 : PL1;
        else
            whoseTurn = player;

        textCm.get(turnText).setText(whoseTurn.equals(PL1) ? P1_TURN : P2_TURN);

        turnText.edit().remove(Invisible.class);

    }

    /**
     * Game Over actions:
     * - show congrats
     * - decide who won
     */
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

    /**
     * Find a proper pit for sow action in a game rules order
     * @param pit very last pit that was dropped with previous stone
     * @return next Pit in order
     */
    public Entity findNextPit(Pit pit){
        return whoseTurn.equals(PL1)
                      ? p1Pits.listIterator(
                          p1Pits.indexOf(pit.getEntity()) == p1Pits.size()-1
                              ? 0 : p1Pits.indexOf(pit.getEntity()) + 1).next()
                      : p2Pits.listIterator(
                          p2Pits.indexOf(pit.getEntity()) == p2Pits.size()-1
                              ? 0 : p2Pits.indexOf(pit.getEntity()) + 1).next();
    }

    /**
     * Put currently flying Stone in a Pit
     * For last dropped Stone need to check game rules for gameover, switch turn, etc.
     * @param pit to drop into
     */
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

    /**
     * Checking game play rules:
     * - game over conditions
     * - another turn for same user
     * - capturing alien stones
     */
    public void checkRules(){

        if (flyStones.size() == 0) {

            GamePlayRuler playRuler = new GamePlayRuler();

            if(!playRuler.checkRules())
                switchTurn(null);
        }
    }

    /**
     * Actions for capturing stones from pit
     * @param x cursor coordinate
     * @param y cursor coordinate
     * @param pit to empty
     */
    protected void captureStones(float x, float y, Pit pit){

        //reset highlight
        if (highlightedColor != null) {
            highlightedPit.getComponent(Tint.class).setTint(highlightedColor);
            highlightedColor = null;
            highlightedPit = null;
        }

        // pit need to be emptied and stones heap should change coordinates on delta relating to cursor position
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

    /**
     * Find pit of current user with given coordinates
     * If no pit found, it means either cursor coordinates outside of the pit bounds or pit doesn't belong to current user
     * @param x cursor coordinate
     * @param y cursor coordinate
     * @param playerId whose player pit need to find
     * @return pit if found
     */
    public Pit findPit(float x, float y, String playerId){

        for(Entity pit : playerManager.getEntitiesOfPlayer(playerId)){
            if(boundsCm.get(pit).contains(x,y))
                return pitCm.get(pit);
        }

        return null;
    }

    /**
     * For every stone in a fly list change it's position in a delta related to give cursor coordinates
     * @param x cursor coordinate
     * @param y cursor coordinate
     */
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

    /**
     * Bunch of Game Rules to check
     */
    public class GamePlayRuler {

        private ChainLinkedList<RulesChainNode> rules = new ChainLinkedList<>();

        public GamePlayRuler() {
            rules.add(new GameOverRule());
            rules.add(new OneMoreTurnRule());
            rules.add(new GrabOppositeStonesRule());
        }

        /**
         * Check series of rules
         * @return true if some rule was satisfied
         */
        public boolean checkRules(){
            return rules.iterator().next().process(rules);
        }
    }

    public class GrabOppositeStonesRule extends RulesChainNode{

        @Override
        public boolean checkRule() {
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

                    //highlighting opposite player pit that was emptied
                    highlightedPit = oppositePit.getEntity();
                    highlightedColor = highlightedPit.getComponent(Tint.class).getTint();
                    highlightedPit.getComponent(Tint.class).setTint(Color.SALMON);
                }
                //when user takes the last stones from other users pits, it could be game over
                return new GameOverRule().checkRule();
            }
            return false;
        }
    }

    public class OneMoreTurnRule extends RulesChainNode{

        @Override
        public boolean checkRule() {
            //If the player's last stone lands in his own big pit, he gets another turn.
            if (lastPit.isBigPit()) {
                switchTurn(whoseTurn);
                return true;
            }
            return false;
        }
    }

    public class GameOverRule extends RulesChainNode{

        @Override
        public boolean checkRule() {

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
                return true;
            }
            return false;
        }
    }

    /**
     * Handling of actions during game play rendering:
     * - capture & drop stones by clicking middle & left mouse
     * - draw flying stones after cursor
     *
     * Implements <b>State Behavioral Design Pattern</b>
     */
    public class PlayStateHandler extends GameStateHandler{

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            //game play
            //To get correct world position of touch point or mouse cursor it is necessary to
            // unproject the raw screen position coordinates with camera that operate in world space.
            unprojectVec.set(screenX, screenY, 0);
            cameraManager.getCamera().unproject(unprojectVec);

            float x = unprojectVec.x;
            float y = unprojectVec.y;

            //action for dropping stone
            if (button == Input.Buttons.MIDDLE) {

                if (flyStones.size() > 0) { //only if there are captured stones sticked to cursor
                    Pit pit = findPit(x, y, whoseTurn);
                    if (pit != null) {

                        // sows the stones on to the right,
                        // ONE in each of the following pits, including his own big pit.
                        Entity nextPit = findNextPit(lastPit);

                        //if pit was found, that means propper order and game rules was passed by current action and we
                        //are able to drop a stone in a pit
                        if(pit.getEntity().equals(nextPit)) {
                            dropStone(pit);
                        }else if(highlightedColor == null){ //for wrong pit click, highlight correct one
                            highlightedPit = nextPit;
                            highlightedColor = highlightedPit.getComponent(Tint.class).getTint();
                            highlightedPit.getComponent(Tint.class).setTint(Color.SALMON);
                        }
                    }
                }

            } else if (button == Input.Buttons.LEFT) { //capture stones from pit

                if (flyStones.size() == 0) {  //possible only if all stones was sowed

                    //find pit of appropriate user with event coordinates
                    Pit pit = findPit(x, y, whoseTurn);

                    //capture is possible only for small pit with stones of current user
                    if (pit != null && !pit.getEntity().equals(bp1) && !pit.getEntity().equals(bp2)
                            && pit.getStones().size() > 0) {
                        captureStones(x, y, pit);
                    }

                }
            }

            return true;
        }

        /**
         * When stones are flying after cursor need to changes their position every rendering mouse move
         * @param screenX cursor coordinate
         * @param screenY cursor coordinate
         * @return whether input was finally processed
         */
        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            unprojectVec.set(screenX, screenY, 0);
            cameraManager.getCamera().unproject(unprojectVec);

          	float x = unprojectVec.x;
          	float y = unprojectVec.y;

            flyStones(x, y);

            return true;
        }
    }

    public class StartedStateHandler extends GameStateHandler{

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            //start game play after switch turn message
            turnText.edit().add(new Invisible());
            game.gameRunning();

            return true;
        }
    }
}
