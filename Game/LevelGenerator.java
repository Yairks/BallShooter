package Game;

import Characters.Enemy;
import Characters.StickFigure;
import Control.Levels;
import Exceptions.DirectionException;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static Game.Game.SCENE_HEIGHT;
import static Game.Game.SCENE_WIDTH;

/**
 * This class generates a new level Scene based on several inputs, including
 * the number of Enemies and the current level.
 *
 * Created by Yair on 4/6/2017.
 */
public class LevelGenerator {
    private static StickFigure stickFigure;
    private static ArrayList<Enemy> enemies;
    private static GameBackground gameBackground;
    private static Scene gameScene;
    private static Timer timer;
    private static Group rootnode;
    private static List<Integer> numEnemies;
    private static int level;
    private static Label countdown;

    /**
     * Called to create a new level. The length
     * of array numEnemies determines the number of enemies
     * and the number at each position determines the
     * strength of each enenmy. The level is the current
     * level.
     * @param numEnemies The strength of each enemy
     * @param level The current level
     * @return A fully functional Scene
     */
    public static Scene generateLevel(List<Integer> numEnemies, int level) {
        LevelGenerator.numEnemies = numEnemies;
        LevelGenerator.level = level;

        //gameBackground will handle all the movements, collisions and drawings of the characters
        gameBackground = new GameBackground(level);

        //Create the various objects in the Game
        stickFigure = new StickFigure();

        //This'll keep track of all the Enemies
        enemies = new ArrayList<>();
        //Add the enemies. i is the number of enemies
        //to be created at a given strength level.
        for(int i = 0; i < numEnemies.size(); i++) {
            for(int x = 0; x < numEnemies.get(i); x++) {
                //The position is one less than the strength.
                enemies.add(new Enemy(gameBackground, stickFigure, i + 1));
            }
        }

        //Label for start of game
        //The countdown will be adjusted by the timer accessing
        //the "adjustCountdown()" method (below).
        countdown = new Label("3");
        countdown.setTextFill(Color.ANTIQUEWHITE);
        countdown.setFont(Font.font("arial", FontWeight.BOLD, 80));

        //Put the gameBackground Canvas into a group
        rootnode = new Group();
        rootnode.getChildren().addAll(gameBackground.getCanvas(), gameBackground.getGameBar(),
                gameBackground.getLivesBar(), countdown);

        gameScene = new Scene(rootnode, SCENE_WIDTH, SCENE_HEIGHT);

        //Apply the css and rendering and stuff.
        rootnode.applyCss();
        rootnode.layout();

        //Center the countdown Label.
        countdown.relocate(Game.SCENE_WIDTH / 2 - countdown.getWidth() / 2, Game.SCENE_HEIGHT / 2);

        //When a key is pressed, the handle() method won't actually do the movement,
        //because otherwise the stick figure will jerk forward a bit, pause, and then
        //start moving. To create clean, constant movement, isWalking will be set
        //to true and the Animation Timer will (during the next frame) react to that.
        gameScene.setOnKeyPressed((KeyEvent event) -> {
            //Record the key pressed
            //If it isn't the right or left arrow key, ignore it
            try {
                stickFigure.keyChange(event.getCode().getName(), true);

            } catch (DirectionException exc) {
                //Ignore the key event.
                System.out.println(exc.getMessage());
            }
        });

        //When key is released, tell the stickFigure that.
        gameScene.setOnKeyReleased((KeyEvent event) -> {
            //Record the key pressed
            //If it isn't the right or left arrow key, ignore it
            try {
                stickFigure.keyChange(event.getCode().getName(), false);

            } catch (DirectionException exc) {
                //Ignore the key event.
                System.out.println(exc.getMessage());
            }
        });

        //This is the initial drawing of the setup.
        gameBackground.draw(stickFigure, enemies, stickFigure.getShot());

        //The timer that basically runs the game
        timer = new Timer(gameBackground, stickFigure, enemies);
        timer.start();

        return gameScene;
    }

    /**
     * Method for changing the countdown till the level starts
     */
    public static void adjustCountdown() {
        //If the countdown is down to one, get rid of it.
        if(Integer.parseInt(countdown.getText()) == 1) {
            System.out.println("HI");
            rootnode.getChildren().remove(countdown);
            return;
        }

        countdown.setText((Integer.parseInt(countdown.getText()) - 1) + "");
    }

    static void gameOver() {
        timer.stop();

        Label label = new Label("Game Over");
        label.setFont(Font.font(48));
        label.setTextFill(Color.WHITE);

        rootnode.getChildren().add(label);

        label.relocate((SCENE_WIDTH - label.getWidth()) / 2, SCENE_HEIGHT / 4);
    }

    static void destroyCurrentLevel() {
        //Remove all children from the rootnode
        timer.stop();
    }

    static void addNode(Node node, boolean centeredVertically, boolean centeredHorizontally) {
        rootnode.getChildren().add(node);
        rootnode.applyCss();
        rootnode.layout();

        if(centeredVertically)
            node.setLayoutY(Game.SCENE_HEIGHT / 2 - node.getBoundsInParent().getHeight() / 2);
        if(centeredHorizontally)
            node.setLayoutX(Game.SCENE_WIDTH / 2 - node.getBoundsInParent().getWidth() / 2);
    }

    /**
     * Called when the stickfigure gets hit
     */
    static void loseALife() {
        stickFigure.loseALife();
        //If dead, remove the retry button.
        if(stickFigure.isDead()) {
            gameBackground.getGameBar().getChildren().remove(2);
        }
        //Otherwise, remove a life ImageView
        else {
            gameBackground.getLivesBar().getChildren().remove(0);
        }
        gameOver();
    }
}
