package net.plshark.users.webservice

import net.plshark.errors.BadRequestException
import net.plshark.errors.DuplicateException
import net.plshark.errors.ErrorResponse
import net.plshark.errors.ObjectNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

/**
 * Controller advice for handling exceptions
 */
@ControllerAdvice
class ExceptionHandlerControllerAdvice {

    companion object {
        private val log = LoggerFactory.getLogger(ExceptionHandlerControllerAdvice::class.java)
    }

    /**
     * Handle a BadRequestException
     * @param e the exception
     * @param request the request that caused the exception
     * @return the response to return to the client
     */
    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequest(e: BadRequestException, request: ServerHttpRequest): ResponseEntity<ErrorResponse> {
        log.debug("Bad request", e)
        val status = HttpStatus.BAD_REQUEST
        return ResponseEntity
            .status(status)
            .body(buildResponse(status, e, request))
    }

    /**
     * Handle an ObjectNotFoundException
     * @param e the exception
     * @param request the request that caused the exception
     * @return the response to return to the client
     */
    @ExceptionHandler(ObjectNotFoundException::class)
    fun handleObjectNotFound(e: ObjectNotFoundException, request: ServerHttpRequest): ResponseEntity<ErrorResponse> {
        log.debug("Object not found", e)
        val status = HttpStatus.NOT_FOUND
        return ResponseEntity
            .status(status)
            .body(buildResponse(status, e, request))
    }

    /**
     * Handle a DuplicateException
     * @param e the exception
     * @param request the request that caused the exception
     * @return the response to return to the client
     */
    @ExceptionHandler(DuplicateException::class)
    fun handleDuplicate(e: DuplicateException, request: ServerHttpRequest): ResponseEntity<ErrorResponse> {
        log.debug("Duplicate", e)
        val status = HttpStatus.CONFLICT
        return ResponseEntity
            .status(status)
            .body(buildResponse(status, e, request))
    }

    private fun buildResponse(status: HttpStatus, e: Throwable, request: ServerHttpRequest): ErrorResponse {
        return ErrorResponse(
            status = status.value(),
            statusDetail = status.reasonPhrase,
            message = e.message,
            path = request.uri.toString()
        )
    }
}
