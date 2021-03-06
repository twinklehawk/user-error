package net.plshark.errors

/**
 * Exception that indicates an object was not found
 */
class ObjectNotFoundException : RuntimeException {
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
