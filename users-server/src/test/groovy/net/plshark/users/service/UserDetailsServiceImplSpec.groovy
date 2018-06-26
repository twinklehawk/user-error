package net.plshark.users.service

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException

import net.plshark.users.Role
import net.plshark.users.User
import net.plshark.users.repo.UserRolesRepository
import net.plshark.users.repo.UsersRepository
import net.plshark.users.service.UserDetailsServiceImpl.UserInfo
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
        usersRepo.getForUsername("user") >> Mono.just(new User(25, "user", "pass"))
        userRolesRepo.getRolesForUser(25) >> Flux.just(new Role(3, "normal-user"), new Role(5, "admin"))

        expect:
        StepVerifier.create(service.findByUsername("user"))
            .expectNextMatches({ details ->
                details.username == "user" &&
                details.password == "pass" &&
                details.authorities.size() == 2 &&
                details.authorities.contains(new SimpleGrantedAuthority("ROLE_normal-user")) &&
                details.authorities.contains(new SimpleGrantedAuthority("ROLE_admin")) &&
                details instanceof UserInfo &&
                ((UserInfo) details).userId == 25})
            .verifyComplete()
    }

    def "UsernameNotFoundException thrown when no user is found for username"() {
        usersRepo.getForUsername("user") >> Mono.empty()

        expect:
        StepVerifier.create(service.findByUsername("user"))
            .verifyError(UsernameNotFoundException.class)
    }

    def "empty roles returns no granted authorities"() {
        usersRepo.getForUsername("user") >> Mono.just(new User(25, "user", "pass"))
        userRolesRepo.getRolesForUser(25) >> Flux.empty()

        expect:
        StepVerifier.create(service.findByUsername("user"))
            .expectNextMatches({ details -> details.authorities.size() == 0})
            .verifyComplete()
    }

    def "user ID returned from authentication when authentication is instance of UserInfo"() {
        UserDetails details = UserInfo.forUser(new User(25, "user", "pass"), Arrays.asList(new Role(3, "normal-user"), new Role(5, "admin")))
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(details,
            details.getPassword(), details.getAuthorities())

        expect:
        StepVerifier.create(service.getUserIdForAuthentication(token))
            .expectNext(25L)
            .verifyComplete()
    }

    def "user ID is looked up when using authentication from external source"() {
        usersRepo.getForUsername("user") >> Mono.just(new User(25, "user", "pass"))
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("user", "pass",
            Collections.emptyList())

        expect:
        StepVerifier.create(service.getUserIdForAuthentication(token))
            .expectNext(25L)
            .verifyComplete()
    }
}
