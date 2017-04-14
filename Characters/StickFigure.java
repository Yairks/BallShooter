package Characters;

import Exceptions.DirectionException;
import Game.Game;
import Game.GameBackground;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents the stick figure of the 
 * This class contains all three versions of the stick figure
 * image (to simulate movement).
 * Contains methods to walk, switch direction, and get the
 * coordinates of the stick figure.
 *
 * Created by Yair on 2/18/2017.
 */
public class StickFigure {

    private String currentdirection;
    private boolean right, left, up, down, shoot, invincible;
    private List<Image> images;
    private Iterator<Image> iter;
    private Image currentImage;
    //xPosition and yPosition are the upper left coordinates of the stick figure
    private double xPosition, yPosition;
    public boolean[][] stickFigure;
    private double globalXPosition, globalYPosition;
    private final static int FLIP_COMPENSATION = 100;
    public final static int WIDTH = 100;
    public final static int HEIGHT = 160;
    private Shot shot;
    private static int lives;
    private long invincibleTime;

    public StickFigure() {

        //Initialize the List which will hold all 4 images
        images = new ArrayList<>();

        Image image;
        //Load the image and put it in the List.
        image = new Image(getClass().getResourceAsStream("/StickFigures/1.png"));
        images.add(image);

        //Repeat with the other images
        image = new Image(getClass().getResourceAsStream("/StickFigures/1.png"));
        images.add(image);

        image = new Image(getClass().getResourceAsStream("/StickFigures/3.png"));
        images.add(image);

        //This image is the same as the second, because it's easier this way.
        image = new Image(getClass().getResourceAsStream("/StickFigures/2.png"));
        images.add(image);

        //The Iterator keeps track of which Image we are up to.
        iter = images.listIterator();

        //Get the first Image
        currentImage = iter.next();

        //Set the initial positions
        xPosition = Game.SCENE_WIDTH / 2;
        yPosition = Game.SCENE_HEIGHT / 2;

        //Initialize the directions
        right = left = up = down = false;

        //The stick figure is initially facing right
        currentdirection = "right";

        //The locations of each of the 4 stickFigures
        //is recorded in this array.
        stickFigure = new boolean[WIDTH][HEIGHT];

        for (int i = 0; i < 4; i++) {
            for (int x = 0; x < WIDTH; x++) {
                for (int y = 0; y < HEIGHT; y++) {
                    if (images.get(i).getPixelReader().getColor(x * 4, y * 4).equals(Color.BLACK)) {
                        stickFigure[x][y] = true;
                    }
                }
            }
        }

        shoot(0, 0, true);
        invincibleTime = -3000000000L;
        invincible = false;
    }

    public void walk(GameBackground background) {

        //I calculated the amount that should be added to each
        //instead of directly adding because that makes it possible
        //to undo the operation if it turns out that the stick figure hit
        //a wall.
        int globalXPositionAdder = 0;
        int globalYPositionAdder = 0;
        int xPositionAdder = 0;
        int yPositionAdder = 0;

        if(right) {
            if (background.getGlobalXPosition() < GameBackground.IMAGE_WIDTH - Game.SCENE_WIDTH
                    && xPosition == Game.SCENE_WIDTH / 2)
                globalXPositionAdder = 5;
            else
                xPositionAdder = 5;
        }

        if(left) {
            if (background.getGlobalXPosition() > 0 && xPosition == Game.SCENE_WIDTH / 2)
                globalXPositionAdder = -5;
            else
                xPositionAdder = -5;
        }

        //Okay now we're going to check if the horizontal movement is a-okay

        //Move tbe background (if necessary).
        background.setBackgroundShift(globalXPositionAdder, 0);

        //Move the stick figure within the screen (if necessary).
        xPosition += xPositionAdder;

        //If the move hit a wall, undo it.
        if(background.hasCollided(this)) {
            xPosition -= xPositionAdder;

            //Move the backgroung back to where it was
            background.setBackgroundShift(-globalXPositionAdder, 0);
        }

        if(up) {
            if (background.getGlobalYPosition() > 0 && yPosition <= Game.SCENE_HEIGHT / 3)
                globalYPositionAdder = -5;
            else
                yPositionAdder = -5;
        }

        if(down) {
            if (background.getGlobalYPosition() < 420 &&
                    yPosition >= Game.SCENE_HEIGHT * 1 / 2)
                globalYPositionAdder = 5;
            else
                yPositionAdder = 5;
        }

        //Now, do the same thing but in the vertical direction

        //Move tbe background (if necessary).
        background.setBackgroundShift(0, globalYPositionAdder);

        //Move the stick figure within the screen (if necessary).
        yPosition += yPositionAdder;

        //If the move hit a wall, undo it.
        if(background.hasCollided(this)) {
            yPosition -= yPositionAdder;

            //Move the backgroung back to where it was
            background.setBackgroundShift(0, -globalYPositionAdder);
        }

        //This allows instantaneous reloading, rather than having to wait.
        if(shoot) {
            if (currentdirection.equals("right"))
                shoot(globalXPosition + 68.75, globalYPosition + 68.75, true);

            else
                shoot(globalXPosition - 68.75, globalYPosition + 68.75, false);
        }


        if(!right && !left && !up && !down) {
            return;
        }

        //Reset the Image iterator if it is done.
        if (!iter.hasNext()) {
            iter = images.listIterator();
        }

        //Load the next Image
        currentImage = iter.next();

        globalXPosition = background.getGlobalXPosition() + xPosition;
        globalYPosition = background.getGlobalYPosition() + yPosition;
    }

    public void keyChange(String key, boolean keyPressed) throws DirectionException {
        key = key.toLowerCase();

        //Figure out which key was pressed
        switch(key) {
            case "left":
                //If the key was released, don't reverse the Image, just return.
                if(!keyPressed) {
                    left = false;
                    return;
                }

                left = true;
                //The stick figure can only move in 1 direction at a time, so right has to be declared false
                right = false;

                //Reverse the Image
                //When flipping the images, the image also moves. Add in the flip compensation
                if(currentdirection.equals("right")) {
                    xPosition += FLIP_COMPENSATION;
                    currentdirection = "left";
                }
                break;

            case "right":
                if(!keyPressed) {
                    right = false;
                    return;
                }

                right = true;
                left = false;

                //When flipping the images, the image also moves. Subtract the flip compensation
                if(currentdirection.equals("left")) {
                    xPosition -= FLIP_COMPENSATION;
                    currentdirection = "right";
                }
                break;

            case "up":
                if(!keyPressed)
                    up = false;
                else {
                    up = true;
                    down = false;
                }
                break;

            case "down":
                if(!keyPressed)
                    down = false;
                else {
                    down = true;
                    up = false;
                }
                break;

            case "space":
                if(!keyPressed) {
                    shoot = false;
                    return;
                }

                //Shoot away, my friend
                //The gun is located at (68.75, 68.75) on the stick figure.
                if (currentdirection.equals("right"))
                    shoot(globalXPosition + 68.75, globalYPosition + 68.75, true);

                else
                    shoot(globalXPosition - 68.75, globalYPosition + 68.75, false);

                shoot = true;
                break;

            default:
                throw new DirectionException(key);
        }
    }

    private void shoot(double shotGlobalX, double shotGlobalY, boolean movingRight) {
        //If you haven't yet shot or if the shot already self-destructed
        if(shot == null || !shot.isVisible()) {
            shot = new Shot(shotGlobalX, shotGlobalY, movingRight);
        }
    }

    public void loseALife() {
        lives--;
        invincible = true;
        invincibleTime = System.nanoTime();
    }

    public Shot getShot() {
        return shot;
    }

    public boolean isInvincible() {
        //Check to see that you're still invincible
        if(invincible) {
            if(System.nanoTime() - invincibleTime >= 3000000000L) {
                System.out.println("Hi there cowboy.");
                invincible = false;
            }
        }
        return invincible;
    }

    /**
     * This method return the local coordinate system
     * for the Images (mashed together)
     *
     * @return The local coordinate system for the current Image.
     */
    public boolean[][] getLocalCoordinates() { return stickFigure; }

    public Image getCurrentImage() { return currentImage; }

    public double getXPosition() { return  xPosition; }
    public double getYPosition() { return yPosition; }
    public double getGlobalXPosition() { return globalXPosition; }
    public double getGlobalYPosition() { return globalYPosition; }
    public String getCurrentDirection() { return currentdirection; }
    public boolean isDead() { return lives <= 0; }
    public static int getLives() { return lives; }
    public static void setLives(int lives) {
        StickFigure.lives = lives;
    }
}