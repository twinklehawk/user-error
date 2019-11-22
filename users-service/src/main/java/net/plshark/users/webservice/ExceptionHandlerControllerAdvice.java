package net.plshark.users.webservice;

import net.plshark.errors.BadRequestException;
import net.plshark.errors.DuplicateException;
import net.plshark.errors.ErrorResponse;
import net.plshark.errors.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Controller advice for handling exceptions
 */
@ControllerAdvice
public class ExceptionHandlerControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(ExceptionHandlerControllerAdvice.class);

    /**
     * Handle a BadRequestException
     * @param e the exception
     * @param request the request that caused the exception
     * @return the response to return to the client
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException e, ServerHttpRequest request) {
        log.debug("Bad request", e);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity
                .status(status)
                .body(buildResponse(status, e, request));
    }

    /**
     * Handle an ObjectNotFoundException
     * @param e the exception
     * @param request the request that caused the exception
     * @return the response to return to the client
     */
    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleObjectNotFound(ObjectNotFoundException e, ServerHttpRequest request) {
        log.debug("Object not found", e);
        HttpStatus status = HttpStatus.NOT_FOUND;
        return ResponseEntity
                .status(status)
                .body(buildResponse(status, e, request));
    }

    /**
     * Handle a DuplicateException
     * @param e the exception
     * @param request the request that caused the exception
     * @return the response to return to the client
     */
    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateException e, ServerHttpRequest request) {
        log.debug("Duplicate", e);
        HttpStatus status = HttpStatus.CONFLICT;
        return ResponseEntity
                .status(status)
                .body(buildResponse(status, e, request));
    }

    private ErrorResponse buildResponse(HttpStatus status, Throwable e, ServerHttpRequest request) {
        return ErrorResponse.builder()
                .status(status.value())
                .statusDetail(status.getReasonPhrase())
                .message(e.getMessage())
                .path(request.getURI().toString())
                .build();
    }
}
