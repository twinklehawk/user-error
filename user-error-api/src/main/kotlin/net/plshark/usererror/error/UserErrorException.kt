package net.plshark.usererror.error

/**
 * Base class for all user error exceptions
 */
open class UserErrorException : RuntimeException {

    constructor() : super()

    constructor(message: String) : super(message)

    constructor(cause: Throwable) : super(cause)

    constructor(message: String, cause: Throwable) : super(message, cause)
}
