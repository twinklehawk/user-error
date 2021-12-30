package net.plshark.usererror.error

/**
 * Exception indicating an object with the requested properties already exists
 */
class DuplicateException : UserErrorException {

    constructor() : super()

    constructor(message: String) : super(message)

    constructor(cause: Throwable) : super(cause)

    constructor(message: String, cause: Throwable) : super(message, cause)
}
