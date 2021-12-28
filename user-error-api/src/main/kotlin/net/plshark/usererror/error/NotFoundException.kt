package net.plshark.usererror.error

/**
 * Exception that indicates an object was not found
 */
class NotFoundException : RuntimeException {
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
