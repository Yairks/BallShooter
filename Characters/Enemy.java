package Characters;

import Game.Game;
import Game.GameBackground;
import Game.LevelGenerator;

import javafx.scene.layout.Background;
import javafx.scene.paint.Color;

import java.util.Random;

/**
 *
 * Created by Yair on 2/20/2017.
 */
public class Enemy {
    //Location of the Enemy on the entire background Image (may be offscreen)
    private double globalXCoordinate;
    private double globalYCoordinate;
    private int diameter;
    //Angle at which it moves
    private double angle;
    private Color fillColor, borderColor;
    private boolean[][] circleCoordinates;
    private StickFigure stickFigure;
    private int strength, speed;

    public Enemy/*HAHA public enemy -- geddit?*/(GameBackground background, StickFigure stickFigure, int strength) {
        this.stickFigure = stickFigure;
        this.strength = strength;
        diameter = 40;

        switch(strength) {
            case 1:
                fillColor = Color.CHARTREUSE;
                borderColor = Color.DARKGREEN;
                speed = 5;
                break;
            case 2:
                System.out.println("You've reached strength 2");
                fillColor = Color.LIGHTCORAL;
                borderColor = Color.RED;
                speed = 6;
                break;
            case 3:
                fillColor = Color.LIGHTGRAY;
                borderColor = Color.DIMGRAY;
                speed = 7;
        }

        //Create a random location for this thing. If that fails, try again.
        spawn(background);
    }

    /**
     * Called initially when the enemy is being created.
     * Not to be confused with respawn().
     * @param background
     */
    private void spawn(GameBackground background) {
        Random random = new Random();
        this.globalXCoordinate = random.nextDouble() * (Game.SCENE_WIDTH - 80) + 40;
        this.globalYCoordinate = random.nextDouble() * (Game.SCENE_HEIGHT - 80) + 40;
        this.angle = random.nextDouble() * 2 * Math.PI;

        circleCoordinates = new boolean[diameter][diameter];
        for(int x = 0; x < diameter; x++)
            for(int y = 0; y < diameter; y++)
                if(x*x + y*y <= diameter * diameter)
                    circleCoordinates[x][y] = true;

        while(background.hasCollided(this) || hitStickFigure(stickFigure))
            spawn(background);
    }

    private void respawn() {
        switch(strength) {
            case 1:
                fillColor = Color.CHARTREUSE;
                borderColor = Color.DARKGREEN;
                speed = 5;
                break;
            case 2:
                fillColor = Color.LIGHTCORAL;
                borderColor = Color.RED;
                speed = 6;
                break;
            case 3:
                fillColor = Color.LIGHTGRAY;
                borderColor = Color.DIMGRAY;
                speed = 7;
                break;
        }
    }

    public void move(GameBackground background) {
        globalXCoordinate += speed * Math.cos(angle);
        globalYCoordinate += speed * Math.sin(angle);

        //If no collisions, then awesome
        if(!background.hasCollided(this))
            return;

        //Uh oh, we collided
        globalXCoordinate -= speed * Math.cos(angle);
        globalYCoordinate -= speed * Math.sin(angle);

        //First, try flipping the angle over the y axis
        angle = Math.PI - angle;

        globalXCoordinate += speed * Math.cos(angle);
        globalYCoordinate += speed * Math.sin(angle);

        //See if that angle's okay
        if(!background.hasCollided(this))
            return;

        //Darn it, back to the drawing board
        //Undo that last thing and try again.
        globalXCoordinate -= speed * Math.cos(angle);
        globalYCoordinate -= speed * Math.sin(angle);
        angle = Math.PI - angle;

        //Flip it over the x axis
        angle *= -1;

        globalXCoordinate += speed * Math.cos(angle);
        globalYCoordinate += speed * Math.sin(angle);

        //Please work this time
        if(!background.hasCollided(this))
            return;

        //Man, you've hit a corner. That stinks, but we'll get you out of here
        globalXCoordinate -= speed * Math.cos(angle);
        globalYCoordinate -= speed * Math.sin(angle);

        //We have to flip it over the line y = x, so in addition to
        //the negative thing from before, we'll have to do the first thing as well.
        angle = Math.PI - angle;

        globalXCoordinate += speed * Math.cos(angle);
        globalYCoordinate += speed * Math.sin(angle);

        //If this doesn't work, we're screwed
        if(!background.hasCollided(this))
            return;

        //Oh well. It was a nice effort. I move that we respawn somewhere else.
        System.err.println("We had to respawn an Enemy");
        spawn(background);
    }

    public boolean hitStickFigure(StickFigure stickFigure) {
        for(int x = 2; x < diameter - 2; x++) {
            for(int y = 2; y < diameter - 2; y++) {
                if(circleCoordinates[x][y]) {
                    double pointX = x + globalXCoordinate;
                    double pointY = y + globalYCoordinate;

                    //sfx and sfy are the difference between the two coordinates.
                    int sfx;
                    if(stickFigure.getCurrentDirection().equals("right"))
                        sfx = (int) (pointX - stickFigure.getGlobalXPosition());
                    else
                        sfx = (int) (stickFigure.getGlobalXPosition() - pointX);

                    int sfy = (int) (pointY - stickFigure.getGlobalYPosition());

                    if(sfx >=0 && sfx < StickFigure.WIDTH && sfy >= 0 && sfy < StickFigure.HEIGHT)
                        if(stickFigure.getLocalCoordinates()[sfx][sfy]) {
                            System.out.println("--------------------COLLISION DETECTED--------------------");
                            System.out.println("StickFigure Global Coordinates: (" + stickFigure.getGlobalXPosition() + ", "
                                    + stickFigure.getGlobalYPosition() + ")");
                            System.out.println("Enemy Global Coordinates: (" + pointX + ", "
                                    + pointY + ")");
                            System.out.println("StickFigure local coordinates: (" + sfx + ", " + sfy + ")");
                            System.out.println("Enemy local coordinates: (" + x + ", " + y + ")");

                            return true;
                        }
                }
            }
        }

        return false;
    }

    /**
     * Check if the Enemy was hit by a shot
     * @param shot The shot that might have hit the Enemy
     * @return True if the shot hit the Enemy
     */
    public boolean hitByShot(Shot shot) {
        if(!shot.isVisible())
            return false;

        for(int x = 0; x < diameter; x++) {
            for(int y = 0; y < diameter; y++) {
                if(!circleCoordinates[x][y])
                    continue;

                double pointX = x + globalXCoordinate;
                double pointY = y + globalYCoordinate;

                //sfx and sfy are the difference between the two coordinates.
                int sfx = (int) (pointX - shot.getGlobalXCoordinate());
                int sfy = (int) (pointY - shot.getGlobalYCoordinate());

                //Check if they're touching.
                if(sfx >=0 && sfx < shot.getWidth() && sfy >= 0 && sfy < shot.getHeight()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void loseStrength() {
        strength--;
        respawn();
    }

    public int getStrength() { return strength; }
    public double getGlobalXCoordinate() { return globalXCoordinate; }
    public double getGlobalYCoordinate() { return  globalYCoordinate; }
    public double getDiameter() { return diameter; }

    /**
     * Method to find the local x coordinate of the center of the circle.
     * @param globalBackgroundXCoordinate The x coordinate of the partial background in the full background
     * @return The local x coordinate
     */
    public double getLocalXCoordinate(double globalBackgroundXCoordinate) {
        return globalXCoordinate - globalBackgroundXCoordinate;
    }

    /**
     * Method to find the local y coordinate of the center of the circle.
     * @param globalBackgroundYCoordinate The y coordinate of the partial background in the full background
     * @return The local y coordinate
     */
    public double getLocalYCoordinate(double globalBackgroundYCoordinate) {
        return globalYCoordinate - globalBackgroundYCoordinate;
    }

    public boolean[][] getCircleCoordinates() {
        return circleCoordinates;
    }

    public Color getFillColor() {
        return fillColor;
    }

    public Color getBorderColor() {
        return borderColor;
    }
}