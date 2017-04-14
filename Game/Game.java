package Game;

import Characters.Enemy;
import Characters.StickFigure;
import Control.Levels;
import Exceptions.DirectionException;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Game.GameBackground;

/**
 * Shoot the evil balls and beat the game!
 *
 * Created by Yair on 2/18/2017.
 */
public class Game extends Application {
    public static final int SCENE_WIDTH = 600;
    public static final int SCENE_HEIGHT = 600;
    private static Stage stage;

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage) {
        this.stage = stage;

        //Start the startup scene
        Levels.setLevel(Levels.START_SCENE);

        stage.show();
        stage.sizeToScene();
    }

    public static void setScene(Scene scene) { stage.setScene(scene); }
}