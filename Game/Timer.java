package Game;

import Characters.Enemy;
import Characters.StickFigure;
import Control.Levels;
import Game.GameBackground;

import javafx.animation.AnimationTimer;

import java.util.List;

import Game.LevelGenerator;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * A timer which contains a method called every
 * frame. This is where the bulk of the game exists.
 *
 * Created by Yair on 2/21/2017.
 */
class Timer extends AnimationTimer {
    private GameBackground gameBackground;
    private StickFigure stickFigure;
    private List<Enemy> enemies;
    private boolean started;
    private long startTime, then, countdown;
    private double frames, fps;

    Timer(GameBackground background, StickFigure stickFigure, List<Enemy> enemies) {
        this.gameBackground = background;
        this.stickFigure = stickFigure;
        this.enemies = enemies;
        started = false;
    }

    @Override
    public void handle(long now) {

        if (then == 0) {
            started = false;
            then = startTime = countdown = now;
        }

        //Only move every fifth of a second
        if(now - then < 20000000) {
            return;
        }

        //Before the Enemies start moving, the player gets
        //to run away. If this is your first time reading the
        //code, ignore this bit for now.
        if(!started) {
            stickFigure.walk(gameBackground);
            gameBackground.draw(stickFigure, enemies, stickFigure.getShot());
            then = now;

            if(now - countdown >= 1000000000L) {
                LevelGenerator.adjustCountdown();
                countdown = now;
            }

            if(now - startTime >= 3000000000L) {
                LevelGenerator.adjustCountdown();
                started = true;
            }
            return;
        }


        //Call the walk method. It will decide whether or not to
        //it should do anything.
        stickFigure.walk(gameBackground);

        for(Enemy enemy : enemies) {
            enemy.move(gameBackground);
        }

        stickFigure.getShot().move(gameBackground);

        //Check if player lost
        for(Enemy enemy : enemies) {
            //If the enemy was hit by a shot, lose some strength.
            if(enemy.hitByShot(stickFigure.getShot())) {
                //Destroy the shot, too.
                stickFigure.getShot().selfDestruct();
                enemy.loseStrength();
            }

            //If the enemy was hit by the player, both are affected.
            if(enemy.hitStickFigure(stickFigure) && !stickFigure.isInvincible()) {

                //The player loses a life.
                LevelGenerator.loseALife();
            }
        }

        //Check if the player died.
        if(stickFigure.isDead())
            LevelGenerator.gameOver();

        //Remove any Enemies with no strength, unless the game is over.
        enemies.removeIf(enemy -> (enemy.getStrength() == 0) && !stickFigure.isDead());

        //Check if player beat the level
        if(enemies.isEmpty()) {
            stop();

            Label levelComplete = new Label("Level Complete!");
            levelComplete.setFont(Font.font("arial", FontWeight.BOLD, 80));
            levelComplete.setTextFill(Color.CHOCOLATE);

            LevelGenerator.addNode(levelComplete, false, false);

            Button nextLevel = new Button("Next Level");
            nextLevel.setFont(Font.font(30));
            nextLevel.setTextFill(Color.CHARTREUSE);
            nextLevel.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));

            nextLevel.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    Levels.nextLevel();
                }
            });

            nextLevel.setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    nextLevel.setBackground(new Background(new BackgroundFill(Color.DARKSLATEGRAY, null, null)));
                }
            });

            nextLevel.setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    nextLevel.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
                }
            });

            nextLevel.setCursor(Cursor.HAND);

            LevelGenerator.addNode(nextLevel, true, true);
        }

        gameBackground.draw(stickFigure, enemies, stickFigure.getShot());

        then = now;
    }
}
