package net.plshark;

/**
 * Exception that indicates an object was not found
 */
public class ObjectNotFoundException extends Exception {

    private static final long serialVersionUID = -8213485241336803509L;

    /**
     * Create a new instance
     * @param message the detail message
     */
    public ObjectNotFoundException(String message) {
        super(message);
    }

    /**
     * Create a new instance
     * @param message the detail message
     * @param cause the cause
     */
    public ObjectNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
