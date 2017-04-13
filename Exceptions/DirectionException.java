package Exceptions;

/**
 * Created by Yair on 2/19/2017.
 */
public class DirectionException extends Exception {
    public DirectionException(String direction) {
        super("Incorrect direction entered: " + direction);
    }
}