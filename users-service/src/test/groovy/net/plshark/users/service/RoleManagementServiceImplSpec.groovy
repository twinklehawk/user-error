package net.plshark.users.service

import net.plshark.users.model.Role
import net.plshark.users.repo.GroupRolesRepository
import net.plshark.users.repo.RolesRepository
import net.plshark.users.repo.UserRolesRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import reactor.test.publisher.PublisherProbe
import spock.lang.Specification

class RoleManagementServiceImplSpec extends Specification {

    RolesRepository rolesRepo = Mock()
    UserRolesRepository userRolesRepo = Mock()
    GroupRolesRepository groupRolesRepo = Mock()
    def service = new RoleManagementServiceImpl(rolesRepo, userRolesRepo, groupRolesRepo)

    def 'inserting should save a role and return the saved role'() {
        rolesRepo.insert(Role.create('role', 'app')) >>
                Mono.just(Role.create(1L, 'role', 'app'))

        expect:
        StepVerifier.create(service.insertRole(Role.create('role', 'app')))
                .expectNext(Role.create(1L, 'role', 'app'))
                .verifyComplete()
    }

    def 'deleting a role should delete any group/role associations, any user/role associations, and the role'() {
        PublisherProbe userRolesProbe = PublisherProbe.empty()
        userRolesRepo.deleteUserRolesForRole(100) >> userRolesProbe.mono()
        PublisherProbe groupRolesProbe = PublisherProbe.empty()
        groupRolesRepo.deleteForRole(100) >> groupRolesProbe.mono()
        PublisherProbe rolesProbe = PublisherProbe.empty()
        rolesRepo.delete(100) >> rolesProbe.mono()

        expect:
        StepVerifier.create(service.deleteRole(100))
                .verifyComplete()
        userRolesProbe.assertWasSubscribed()
        groupRolesProbe.assertWasSubscribed()
        rolesProbe.assertWasSubscribed()
    }

    def 'should be able to retrieve a role by name'() {
        rolesRepo.getForName('role-name', 'app-name') >>
                Mono.just(Role.create(123L, 'role-name', 'app-name'))

        expect:
        StepVerifier.create(service.getRoleByName('role-name', 'app-name'))
                .expectNext(Role.create(123L, 'role-name', 'app-name'))
                .verifyComplete()
    }

    def 'should be able to retrieve all roles'() {
        rolesRepo.getRoles(100, 0) >> Flux.just(
                Role.create(1L, 'role1', 'app1'),
                Role.create(2L, 'role2', 'app2'))

        expect:
        StepVerifier.create(service.getRoles(100, 0))
                .expectNext(Role.create(1L, 'role1', 'app1'),
                        Role.create(2L, 'role2', 'app2'))
                .verifyComplete()
    }
}
