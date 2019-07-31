package net.plshark.users.service

import net.plshark.users.model.Role
import net.plshark.users.model.User
import net.plshark.users.repo.UserRolesRepository
import net.plshark.users.repo.UsersRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UsernameNotFoundException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification

class UserDetailsServiceImplSpec extends Specification {

    UsersRepository usersRepo = Mock()
    UserRolesRepository userRolesRepo = Mock()
    UserDetailsServiceImpl service = new UserDetailsServiceImpl(usersRepo, userRolesRepo)

    def "constructor does not accept null args"() {
        when:
        new UserDetailsServiceImpl(null, userRolesRepo)

        then:
        thrown(NullPointerException)

        when:
        new UserDetailsServiceImpl(usersRepo, null)

        then:
        thrown(NullPointerException)
    }

    def "a user and its roles are mapped to the correct UserDetails"() {
        usersRepo.getForUsername("user") >> Mono.just(User.create(25, "user", "pass"))
        userRolesRepo.getRolesForUser(25) >> Flux.just(
                Role.create(3, "normal-user", "app"),
                Role.create(5, "admin", "app"))

        expect:
        StepVerifier.create(service.findByUsername("user"))
            .expectNextMatches({ details ->
                details.username == "user" &&
                details.password == "pass" &&
                details.authorities.size() == 2 &&
                details.authorities.contains(new SimpleGrantedAuthority("ROLE_normal-user")) &&
                details.authorities.contains(new SimpleGrantedAuthority("ROLE_admin"))
            })
            .verifyComplete()
    }

    def "UsernameNotFoundException thrown when no user is found for username"() {
        usersRepo.getForUsername("user") >> Mono.empty()

        expect:
        StepVerifier.create(service.findByUsername("user"))
            .verifyError(UsernameNotFoundException.class)
    }

    def "empty roles returns no granted authorities"() {
        usersRepo.getForUsername("user") >> Mono.just(User.create(25, "user", "pass"))
        userRolesRepo.getRolesForUser(25) >> Flux.empty()

        expect:
        StepVerifier.create(service.findByUsername("user"))
            .expectNextMatches({ details -> details.authorities.size() == 0})
            .verifyComplete()
    }
}
