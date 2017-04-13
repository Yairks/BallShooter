package Characters;

import Game.GameBackground;

/**
 * The Shot object.
 *
 * Created by Yair on 2/21/2017.
 */
public class Shot {
    private double globalXCoordinate, globalYCoordinate;
    private boolean movingRight;
    private double width, height;
    private boolean visible;

    Shot(double globalXCoordinate, double globalYCoordinate, boolean movingRight) {
        this.globalXCoordinate = globalXCoordinate;
        this.globalYCoordinate = globalYCoordinate;
        this.movingRight = movingRight;

        height = 2.5;
        width = 0;

        visible = true;
    }

    public void move(GameBackground background) {
        if(!visible)
            return;

        width += 20;

        //This allows the shot to move to the left
        if(!movingRight)
            globalXCoordinate -= 20;

        //If it hit a wall, the shot goes kablooey.
        if(background.hasCollided(this))
            selfDestruct();
    }

    public void selfDestruct() {
        visible = false;
    }

    public double getGlobalXCoordinate() { return globalXCoordinate; }

    public double getGlobalYCoordinate() {
        return globalYCoordinate;
    }

    public boolean isMovingRight() {
        return movingRight;
    }

    public boolean isVisible() {
        return visible;
    }

    public double getWidth() { return width; }
    public double getHeight() { return height; }
}