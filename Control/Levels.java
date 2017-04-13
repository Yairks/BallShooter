package Control;

import javafx.scene.Group;
import javafx.scene.Scene;

import Game.Game;
import Game.StartBackground;
import Game.LevelGenerator;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Central controlling class that stores all the levels of the game.
 *
 * Created by Yair on 2/22/2017.
 */
public class Levels {
    public static final int START_SCENE = 0;
    public static final int LEVEL1 = 1;
    public static final int LEVEL2 = 2;
    private static final Scene[] levels;
    private static int currentLevel;
    private static final List<List<Integer>> ENEMIES_BY_LEVEL;

    static {
        levels = new Scene[9];
        //Generate the start scene
        levels[START_SCENE] = (new StartBackground()).getStartScene();
        //The other scenes will be created dynamically later on

        currentLevel = 0;

        //Number of enemies in each level
        //Position of the lists within the lists
        //is the level (posiiton 1 is level 1, etc.)
        //The second dimension of Lists determines
        //the strength of the enenmies (position 0 has
        //strength 1, etc.)
        ENEMIES_BY_LEVEL = new ArrayList<List<Integer>>();
        ENEMIES_BY_LEVEL.add(Arrays.asList(0));
        ENEMIES_BY_LEVEL.add(Arrays.asList(1));
        ENEMIES_BY_LEVEL.add(Arrays.asList(4, 1));
        ENEMIES_BY_LEVEL.add(Arrays.asList(5, 2));
        ENEMIES_BY_LEVEL.add(Arrays.asList(7, 4));
        ENEMIES_BY_LEVEL.add(Arrays.asList(10, 5));
        ENEMIES_BY_LEVEL.add(Arrays.asList(10, 5, 2));
        ENEMIES_BY_LEVEL.add(Arrays.asList(5, 5, 5));
        ENEMIES_BY_LEVEL.add(Arrays.asList(8, 8, 8));
    }

    /**
     * Called when the next level is achieved or when
     * the game is started. Level 0 is the startup scene,
     * and each level thereafter corresponds to the actual
     * level (eg "1" would set level 1).
     *
     * @param level
     */
    public static void setLevel(int level) {
        if(level == 0) {
            Game.setScene(levels[level]);
            return;
        }

        levels[level] = LevelGenerator.generateLevel(ENEMIES_BY_LEVEL.get(level), level);
        Game.setScene(levels[level]);
        currentLevel = level;
    }

    /**
     * If the player beat the level, call this method to go to the next level.
     * If there is no next level, it will call the You Won scene (for now this
     * is the startup scene).
     */
    public static void nextLevel() {
        if(currentLevel >= 8)
            currentLevel = -1;

        currentLevel++;

        levels[currentLevel] = LevelGenerator.generateLevel(ENEMIES_BY_LEVEL.get(currentLevel), currentLevel);
        Game.setScene(levels[currentLevel]);
    }

    /**
     * Method for regenerating a level when it has been played.
     * Should only be called by LevelGenerator.
     * @param level
     */
    public static void regenerateLevel(int level) {
        levels[level] = LevelGenerator.generateLevel(ENEMIES_BY_LEVEL.get(level), level);
    }
}
