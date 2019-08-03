package net.plshark.users.webservice

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest(classes = [ Application.class, TestConfig.class ],
        properties = [ 'auth.algorithm=none', 'auth.issuer=test', 'spring.flyway.placeholders.schema=users',
                'spring.flyway.placeholders.username=testuser', 'spring.flyway.placeholders.password=pass',
                'spring.flyway.schemas=users' ])
class ApplicationIntSpec extends Specification {

    @Autowired
    Application application

    def "context can be built"() {
        expect:
        application != null
    }
}
