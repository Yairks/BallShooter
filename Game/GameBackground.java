package Game;

import Characters.Enemy;
import Characters.Shot;
import Characters.StickFigure;
import Control.Levels;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;
import java.util.logging.Level;

/**
 *
 * Created by Yair on 2/19/2017.
 */
public class GameBackground {
    private Canvas canvas;
    private GraphicsContext gc;
    private WritableImage partial;
    private Image background;
    //global (within the full Image) coordinates of the upper left corner of the current WritableImage
    private double globalXPosition, globalYPosition;
    private boolean[][] whereTheWallsAre;
    public static final int IMAGE_WIDTH = 1000;
    public static final int IMAGE_HEIGHT = 1000;
    private static HBox gameBar, livesBar;
    public static final int CANVAS_WIDTH = 600;
    public static final int CANVAS_HEIGHT = 567;

    public GameBackground(int level) {
        canvas = new Canvas(Game.SCENE_WIDTH, Game.SCENE_HEIGHT);
        gc = canvas.getGraphicsContext2D();

        background = new Image("file:C:\\Users\\Yair\\Documents\\StickFigure\\Background.png");

        globalXPosition = globalYPosition = 200;

        PixelReader pr = background.getPixelReader();
        whereTheWallsAre = new boolean[IMAGE_WIDTH][IMAGE_HEIGHT];

        for(int x = 0; x < IMAGE_WIDTH; x++) {
            for(int y = 0; y < IMAGE_HEIGHT; y++) {
                if(pr.getColor(x, y).equals(Color.BLACK))
                    whereTheWallsAre[x][y] = true;
            }
        }

        //Create the bar at the bottom
        gameBar = new HBox();
        gameBar.setAlignment(Pos.CENTER);
        gameBar.setPrefWidth(Game.SCENE_WIDTH);
        gameBar.setSpacing(120);

        //Set the background color of the gameBar.
        gameBar.setBackground(new Background(
                new BackgroundFill(Color.BURLYWOOD, CornerRadii.EMPTY, new Insets(1, 0, 0, 0))));
        //Set the Border of the gameBar.
        gameBar.setBorder(new Border(
                new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
                        new BorderWidths(3, 3, 3, 3))));

        gameBar.relocate(0, Game.SCENE_HEIGHT - 33);

        //Display the current level inside the gameBar.
        Label levelLabel = new Label("Level " + level);
        levelLabel.setFont(Font.font("arial", FontWeight.EXTRA_BOLD, 24));
        levelLabel.setTextFill(Color.BLACK);

        //Add a quit button
        Label quit = new Label("Quit");
        quit.setFont(Font.font("arial", FontWeight.EXTRA_BOLD, 20));
        quit.setTextFill(Color.BLUE);
        quit.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                StickFigure.setLives(3);
                //Remove the current level before switching
                LevelGenerator.destroyCurrentLevel();
                Levels.setLevel(0);
            }
        });

        quit.setCursor(Cursor.HAND);

        //Add a retry button
        Label retry = new Label("Retry");
        retry.setFont(Font.font("arial", FontWeight.EXTRA_BOLD, 20));
        retry.setTextFill(Color.BLUE);
        retry.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //Clean up before you go.
                LevelGenerator.destroyCurrentLevel();
                Levels.setLevel(level);
            }
        });

        retry.setCursor(Cursor.HAND);

        gameBar.getChildren().addAll(quit, levelLabel, retry);

        //Create the lives bar
        livesBar = new HBox();
        livesBar.setSpacing(0);
        livesBar.setPrefWidth(StickFigure.WIDTH);
        livesBar.setPrefHeight(20);
        livesBar.relocate(0, CANVAS_HEIGHT - StickFigure.HEIGHT / 4);

        Image miniSF = new Image("file:C:\\Users\\Yair\\Documents\\StickFigure\\1.png");

        for(int i = 0; i < StickFigure.getLives(); i++) {
            ImageView life = new ImageView(miniSF);
            life.setFitHeight(StickFigure.HEIGHT / 3);
            life.setFitWidth(StickFigure.WIDTH / 3);
            livesBar.getChildren().add(life);
        }
    }

    /**
     * Method for redrawing the canvas with a new location for the stick figure
     * @param stickFigure
     */
    public void draw(StickFigure stickFigure, List<Enemy> enemies, Shot shot) {
        //Clear and redraw the canvas.
        gc.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
        gc.setFill(Color.AQUAMARINE);
        gc.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);

        //BACKGROUND
        //Create a WritableImage that displays only part of the Image
        partial = new WritableImage(background.getPixelReader(), (int) globalXPosition, (int) globalYPosition,
                CANVAS_WIDTH, CANVAS_HEIGHT);
        gc.drawImage(partial, 0, 0);

        //STICKFIGURE
        //If the direction is left, then reverse the image horizontally
        if(stickFigure.getCurrentDirection().equals("right"))
            gc.drawImage(stickFigure.getCurrentImage(), stickFigure.getXPosition(), stickFigure.getYPosition(),
                    StickFigure.WIDTH, StickFigure.HEIGHT);

        else
            gc.drawImage(stickFigure.getCurrentImage(), stickFigure.getXPosition(), stickFigure.getYPosition(),
                    -StickFigure.WIDTH, StickFigure.HEIGHT);

        //ENEMIES
        for(Enemy enemy : enemies) {
            gc.setFill(enemy.getFillColor());
            gc.setStroke(enemy.getBorderColor());
            gc.setLineWidth(3);
            gc.fillOval(enemy.getLocalXCoordinate(globalXPosition), enemy.getLocalYCoordinate(globalYPosition),
                    enemy.getDiameter(), enemy.getDiameter());
            gc.strokeOval(enemy.getLocalXCoordinate(globalXPosition), enemy.getLocalYCoordinate(globalYPosition),
                    enemy.getDiameter(), enemy.getDiameter());
        }

        //SHOTS
        gc.setFill(Color.DARKRED);
        if(shot.isVisible()) {
            gc.fillRect(shot.getGlobalXCoordinate() - globalXPosition,
                    shot.getGlobalYCoordinate() - globalYPosition, shot.getWidth(), shot.getHeight());
        }
    }

    /**
     * Method for shifting the background.
     * Instead of directly adjusting the globalXPosition
     * and globalYPosition (which could lead to improper use),
     * this method allows for the location to be adjusted relatively
     * but not absolutely.
     *
     * @param xTranslate How much the x coordinate should be increased by
     * @param yTranslate How much the y coordinate should be increased by
     */
    public void setBackgroundShift(double xTranslate, double yTranslate) {
        //If the WritableImage's bounds are not fully contained within the larger Image,
        //throw an exception
        if(globalXPosition > Game.SCENE_WIDTH ||
                globalXPosition < 0 ||
                globalYPosition > Game.SCENE_HEIGHT ||
                globalYPosition < 0)
            return;

        globalXPosition += xTranslate;
        globalYPosition += yTranslate;
    }

    /**
     * Checks if the stick figure hit a wall
     *
     * @param sf The StickFigure whose location we're checking
     * @return True is the stick figure hit a wall
     */
    public boolean hasCollided(StickFigure sf) {
        //Go through every coordinate on the stick figure's bounds
        //and check if it is part of the actual stick figure.
        //Then, do some checks.
        for (int x = 0; x < StickFigure.WIDTH; x++) {
            for (int y = 0; y < StickFigure.HEIGHT; y++) {
                if (sf.getLocalCoordinates()[x][y]) {
                    if(whereTheWallsAre[(int) (globalXPosition + sf.getXPosition() +
                            (sf.getCurrentDirection().equals("right") ? x : -x))]
                            [(int) (globalYPosition + sf.getYPosition() + y)]) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Used for checking if one of the Enemies has hit a wall
     *
     * @param enemy The enemy whose location we're checking
     * @return True if it has hit a wall
     */
    public boolean hasCollided(Enemy enemy) {
        int enemyGlobalX = (int) enemy.getGlobalXCoordinate();
        int enemyGlobalY = (int) enemy.getGlobalYCoordinate();

        //If part of the Enemy is outside the screen, of course it hit a wall.
        if(enemyGlobalX < 0 || enemyGlobalX >= IMAGE_WIDTH - 40 ||
                enemyGlobalY < 0 || enemyGlobalY >= IMAGE_HEIGHT - 40)
            return true;

        for(int x = 0; x < 40; x++) {
            for(int y = 0; y < 40; y++) {
                if(enemy.getCircleCoordinates()[x][y] && whereTheWallsAre[enemyGlobalX + x][enemyGlobalY + y]) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Checks if the shot hit a wall. It needs only 4 pieces of data to do it.
     * @param shot The shot whose location we're testing
     * @return True if the shot hit a wall.
     */
    public boolean hasCollided(Shot shot) {
        int shotGlobalX = (int) shot.getGlobalXCoordinate();
        int shotGlobalY = (int) shot.getGlobalYCoordinate();
        int height = (int) shot.getHeight();
        int width = (int) shot.getWidth();

        //If the shot went out of bounds
        if(shotGlobalX < 0 || shotGlobalX + width >= IMAGE_WIDTH)
            return true;

        if(whereTheWallsAre[shotGlobalX][shotGlobalY] ||
                whereTheWallsAre[shotGlobalX][shotGlobalY + height] ||
                whereTheWallsAre[shotGlobalX + width][shotGlobalY] ||
                whereTheWallsAre[shotGlobalX + width][shotGlobalY + height])
            return true;

        return false;
    }

    public Canvas getCanvas() { return canvas; }
    public HBox getGameBar() { return gameBar; }
    public HBox getLivesBar() { return livesBar; }
    public WritableImage getPartial() { return partial; }
    public Image getBackground() { return background; }

    /**
     * Getter method to find the location of the upper left corner
     * of the WritableImage within the larger Image it is
     * cropped from.
     * @return The X coordinate of the upper left corner of the WritableImage
     */
    public double getGlobalXPosition() { return globalXPosition; }

    /**
     * Getter method to find the location of the upper left corner
     * of the WritableImage within the larger Image it is
     * cropped from.
     * @return The Y coordinate of the upper left corner of the WritableImage
     */
    public double getGlobalYPosition() { return globalYPosition; }
}