package net.plshark.users.webservice

import javax.inject.Inject

import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest(classes = [ Application.class, TestConfig.class ],
        properties = [ 'auth.algorithm=none', 'auth.issuer=test', 'spring.flyway.placeholders.schema=users',
                'spring.flyway.placeholders.username=testuser', 'spring.flyway.placeholders.password=pass',
                'spring.flyway.schemas=users' ])
class ApplicationIntSpec extends Specification {

    @Inject
    Application application

    def "context can be built"() {
        expect:
        application != null
    }
}
