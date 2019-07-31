package net.plshark.users.webservice

import net.plshark.users.model.Role
import net.plshark.users.model.User
import net.plshark.users.model.UserInfo
import net.plshark.users.service.UserManagementService

import java.nio.charset.StandardCharsets

import javax.inject.Inject

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers
import org.springframework.test.web.reactive.server.WebTestClient

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Ignore
import spock.lang.Specification

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Ignore("Need to make not notes-specific")
class WebSecurityConfigIntSpec extends Specification {

    @Inject
    ApplicationContext context
    @Inject
    UserManagementService userMgmt
    @Inject
    ObjectMapper mapper
    UserInfo normalUser
    UserInfo adminUser
    WebTestClient client

    def setup() {
        client = WebTestClient.bindToApplicationContext(context)
            .build()

        Role userRole = userMgmt.getRoleByName("user").toFuture().get()
        Role adminRole = userMgmt.getRoleByName("admin").toFuture().get()

        normalUser = userMgmt.insertUser(User.create("test-user", "pass")).block()
        userMgmt.grantRoleToUser(normalUser, userRole).toFuture().get()

        adminUser = userMgmt.insertUser(User.create("admin-user", "pass")).block()
        userMgmt.grantRoleToUser(adminUser, adminRole).toFuture().get()
    }

    def cleanup() {
        if (normalUser != null)
            userMgmt.deleteUser(normalUser).toFuture().get()
        if (adminUser != null)
            userMgmt.deleteUser(adminUser).toFuture().get()
    }

    def "no csrf token should result in a forbidden response"() {
        expect:
        client.post().uri("/notes")
            .exchange().expectStatus().isForbidden()
    }

    def "a valid csrf token but no authentication header should be rejected with an unauthorized response"() {
        expect:
        client.mutateWith(SecurityMockServerConfigurers.csrf())
            .get().uri("/notes/1")
            .exchange().expectStatus().isUnauthorized()
    }

    def "invalid credentials should be rejected with an unauthorized response"() {
        expect:
        client.mutateWith(SecurityMockServerConfigurers.csrf())
            .get().uri("/notes/1")
                .header("Authorization", httpBasic("bad-user","bad-pass"))
            .exchange().expectStatus().isUnauthorized()
    }

    def "an invalid password should be rejected with an unauthorized response"() {
        expect:
        client.mutateWith(SecurityMockServerConfigurers.csrf())
            .get().uri("/notes/1")
                .header("Authorization", httpBasic("test-user","bad-pass"))
            .exchange().expectStatus().isUnauthorized()
    }

    def "valid credentials should be accepted"() {
        expect:
        client.mutateWith(SecurityMockServerConfigurers.csrf())
            .get().uri("/notes/1")
                .header("Authorization", httpBasic("test-user","pass"))
            // the note does not exist, so not found is expected
            .exchange().expectStatus().isNotFound()
    }

    def "normal methods require users-user role"() {
        expect:
        client.mutateWith(SecurityMockServerConfigurers.csrf())
            .get().uri("/notes/1")
                .header("Authorization", httpBasic("admin-user","pass"))
            .exchange().expectStatus().isForbidden()
        client.mutateWith(SecurityMockServerConfigurers.csrf())
            .get().uri("/notes/1")
                .header("Authorization", httpBasic("test-user","pass"))
            .exchange().expectStatus().isNotFound()
    }

    def "admin methods require users-admin role"() {
        expect:
        client.mutateWith(SecurityMockServerConfigurers.csrf())
            .delete().uri("/users/100")
                .header("Authorization", httpBasic("test-user","pass"))
            .exchange().expectStatus().isForbidden()
        client.mutateWith(SecurityMockServerConfigurers.csrf())
            .delete().uri("/users/100")
                .header("Authorization", httpBasic("admin-user","pass"))
            .exchange().expectStatus().isOk()
    }

    def httpBasic(String user, String pass) {
        def str = user + ":" + pass
        return "Basic " + Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8))
    }
}
