package net.plshark.usererror.error

/**
 * Exception that indicates an object was not found
 */
class NotFoundException : UserErrorException {

    constructor() : super()

    constructor(message: String) : super(message)

    constructor(cause: Throwable) : super(cause)

    constructor(message: String, cause: Throwable) : super(message, cause)
}
