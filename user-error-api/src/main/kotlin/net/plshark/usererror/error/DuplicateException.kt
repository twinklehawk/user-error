package net.plshark.usererror.error

/**
 * Exception indicating an object with the requested properties already exists
 */
class DuplicateException : RuntimeException {
    /**
     * Create a new instance
     * @param message the detail message
     */
    constructor(message: String?) : super(message)

    /**
     * Create a new instance
     * @param message the detail message
     * @param cause the cause
     */
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}
