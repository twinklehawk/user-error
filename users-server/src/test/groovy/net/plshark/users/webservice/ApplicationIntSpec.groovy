package net.plshark.users.webservice

import javax.inject.Inject

import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest(classes = Application.class)
class ApplicationIntSpec extends Specification {

    @Inject
    Application application

    def "context can be built"() {
        expect:
        application != null
    }
}
