package net.plshark.errors

/**
 * Exception indicating a request was invalid
 */
class BadRequestException : RuntimeException {
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