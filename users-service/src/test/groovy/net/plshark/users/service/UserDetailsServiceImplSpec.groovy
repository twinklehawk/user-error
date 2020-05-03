package net.plshark.users.service

import net.plshark.users.repo.UserGroupsRepository
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
    UserGroupsRepository userGroupsRepo = Mock()
    UserDetailsServiceImpl service = new UserDetailsServiceImpl(usersRepo, userRolesRepo, userGroupsRepo)

    def "a user and its roles are mapped to the correct UserDetails"() {
        usersRepo.getForUsernameWithPassword("user") >> Mono.just(User.builder().id(25L)
                .username('user').password('pass').build())
        userRolesRepo.getRolesForUser(25) >> Flux.just(
                Role.builder().id(3).applicationId(1).name('normal-user').build(),
                Role.builder().id(5).applicationId(1).name('admin').build())
        userGroupsRepo.getGroupRolesForUser(25) >> Flux.just(
                Role.builder().id(3).applicationId(1).name('group-role-1').build(),
                Role.builder().id(5).applicationId(1).name('group-role-2').build())

        expect:
        StepVerifier.create(service.findByUsername("user"))
            .expectNextMatches({ details ->
                details.username == "user" &&
                details.password == "pass" &&
                details.authorities.size() == 4 &&
                details.authorities.contains(new SimpleGrantedAuthority("ROLE_normal-user")) &&
                details.authorities.contains(new SimpleGrantedAuthority("ROLE_admin")) &&
                details.authorities.contains(new SimpleGrantedAuthority("ROLE_group-role-1")) &&
                details.authorities.contains(new SimpleGrantedAuthority("ROLE_group-role-2"))
            })
            .verifyComplete()
    }

    def "UsernameNotFoundException thrown when no user is found for username"() {
        usersRepo.getForUsernameWithPassword("user") >> Mono.empty()

        expect:
        StepVerifier.create(service.findByUsername("user"))
            .verifyError(UsernameNotFoundException.class)
    }

    def "empty roles returns no granted authorities"() {
        usersRepo.getForUsernameWithPassword("user") >> Mono.just(User.builder().id(25L)
                .username('user').password('pass').build())
        userRolesRepo.getRolesForUser(25) >> Flux.empty()
        userGroupsRepo.getGroupRolesForUser(25) >> Flux.empty()

        expect:
        StepVerifier.create(service.findByUsername("user"))
            .expectNextMatches({ details -> details.authorities.size() == 0})
            .verifyComplete()
    }
}
