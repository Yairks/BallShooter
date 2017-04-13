package Exceptions;

/**
 * Created by Yair on 2/20/2017.
 */
public class OutOfBoundsException extends Exception {
    OutOfBoundsException(String coordinates) {
        super("Out of bounds. WritableImage cannot be drawn at " + coordinates);
    }
}