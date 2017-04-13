package Game;

import Control.Levels;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Random;

import static Game.Game.SCENE_HEIGHT;
import static Game.Game.SCENE_WIDTH;

/**
 * Created by Yair on 4/6/2017.
 */
public class StartBackground {
    private Scene startScene;

    public StartBackground() {
        Label title = new Label("Shoot the Balls");
        Label subtitle = new Label("Arrow keys to move and space to shoot");
        Label startGame = new Label("Click to start the game");

        title.setFont(Font.font(48));
        subtitle.setFont(Font.font(20));
        startGame.setFont(Font.font(16));

        title.setTextFill(Color.WHITE);
        subtitle.setTextFill(Color.BLACK);
        startGame.setTextFill(Color.BLUE);

        String[] randomFacts = { "Pro tip: don't get hit by the balls.",
                "You can shoot only 1 shot at a time, so use it wisely",
                "Pro tip: There's a time limit. Shoot quickly.",
                "While you're playing, I'll be going through your pictures..." +
                        "\n     I mean, making sure your game is bug free.",
                "Did you know? Games played on phones are called apps."};

        Label randomFact = new Label(randomFacts[(new Random().nextInt(5))]);
        randomFact.setFont(Font.font(18));
        randomFact.setTextFill(Color.DARKRED);

        Group initGroup = new Group(title, subtitle, startGame, randomFact);

        startScene = new Scene(initGroup, SCENE_WIDTH, SCENE_HEIGHT, Color.VIOLET);
        //Try deleting this thing below. The styling goes kabloee without it.
        initGroup.applyCss();
        initGroup.layout();

        startScene.setCursor(Cursor.HAND);

        //This is how you start the game
        startScene.setOnMouseClicked((MouseEvent event) -> Levels.setLevel(Levels.LEVEL1));

        title.relocate((SCENE_WIDTH - title.getWidth()) / 2, SCENE_HEIGHT / 4);
        subtitle.relocate((SCENE_WIDTH - subtitle.getWidth()) / 2,
                (SCENE_HEIGHT + title.getWidth()) / 4);
        startGame.relocate((SCENE_WIDTH - startGame.getWidth()) / 2, SCENE_HEIGHT - startGame.getHeight());
        randomFact.relocate((SCENE_WIDTH - randomFact.getWidth()) / 2,
                subtitle.getBoundsInParent().getMaxY() + randomFact.getHeight());
    }

    public Scene getStartScene() { return startScene; }
}
