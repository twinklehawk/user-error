package net.plshark.errors;

/**
 * Exception indicating an object with the requested properties already exists
 */
public class DuplicateException extends RuntimeException {

    /**
     * Create a new instance
     * @param message the detail message
     */
    public DuplicateException(String message) {
        super(message);
    }

    /**
     * Create a new instance
     * @param message the detail message
     * @param cause the cause
     */
    public DuplicateException(String message, Throwable cause) {
        super(message, cause);
    }
}
