package net.plshark.errors;

/**
 * Exception indicating a request was invalid
 */
public class BadRequestException extends RuntimeException {

    private static final long serialVersionUID = 5359694168645391199L;

    /**
     * Create a new instance
     * @param message the detail message
     */
    public BadRequestException(String message) {
        super(message);
    }

    /**
     * Create a new instance
     * @param message the detail message
     * @param cause the cause
     */
    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
