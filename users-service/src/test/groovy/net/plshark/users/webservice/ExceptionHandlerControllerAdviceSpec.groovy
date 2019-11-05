package net.plshark.users.webservice

import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest

import net.plshark.BadRequestException
import net.plshark.ObjectNotFoundException
import spock.lang.Specification

class ExceptionHandlerControllerAdviceSpec extends Specification {

    ServerHttpRequest request = Mock()
    ExceptionHandlerControllerAdvice advice = new ExceptionHandlerControllerAdvice()

    def setup() {
        request.getURI() >> URI.create("http://test/url")
    }

    def "bad request builds correct response body"() {
        when:
        def response = advice.handleBadRequest(new BadRequestException("bad request"), request)

        then:
        response.statusCode == HttpStatus.BAD_REQUEST
        response.body.message == "bad request"
        response.body.path == "http://test/url"
        response.body.status == 400
        response.body.statusDetail == HttpStatus.BAD_REQUEST.getReasonPhrase()
        response.body.timestamp != null
    }

    def "object not found builds correct response body"() {
        when:
        def response = advice.handleObjectNotFound(new ObjectNotFoundException("not found"), request)

        then:
        response.statusCode == HttpStatus.NOT_FOUND
        response.body.message == "not found"
        response.body.path == "http://test/url"
        response.body.status == 404
        response.body.statusDetail == HttpStatus.NOT_FOUND.getReasonPhrase()
        response.body.timestamp != null
    }
/*
    def "method not supported builds correct response body"() {
        when:
        def response = advice.handleMethodNotSupported(new HttpRequestMethodNotSupportedException("get something"), request)

        then:
        response.statusCode == HttpStatus.METHOD_NOT_ALLOWED
        response.body.message == "Request method 'get something' not supported"
        response.body.path == "http://test/url"
        response.body.status == 405
        response.body.statusDetail == HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase()
        response.body.timestamp != null
    }
*/
    def "generic exception builds correct response body"() {
        when:
        def response = advice.handleThrowable(new Exception("problem"), request)

        then:
        response.statusCode == HttpStatus.INTERNAL_SERVER_ERROR
        response.body.message == null
        response.body.path == "http://test/url"
        response.body.status == 500
        response.body.statusDetail == HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()
        response.body.timestamp != null
    }
}
