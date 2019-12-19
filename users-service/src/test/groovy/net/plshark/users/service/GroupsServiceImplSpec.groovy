package net.plshark.users.service

import net.plshark.errors.DuplicateException
import net.plshark.errors.ObjectNotFoundException
import net.plshark.users.model.Group
import net.plshark.users.repo.GroupRolesRepository
import net.plshark.users.repo.GroupsRepository
import org.springframework.dao.DataIntegrityViolationException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import reactor.test.publisher.PublisherProbe
import spock.lang.Specification

class GroupsServiceImplSpec extends Specification {

    GroupsRepository groupsRepo = Mock()
    GroupRolesRepository groupRolesRepo = Mock()
    def service = new GroupsServiceImpl(groupsRepo, groupRolesRepo)

    def 'should be able to retrieve groups by name'() {
        groupsRepo.getForName('group-name') >> Mono.just(Group.builder().id(123L).name('group-name').build())

        expect:
        StepVerifier.create(service.get('group-name'))
                .expectNext(Group.builder().id(123L).name('group-name').build())
                .verifyComplete()

        StepVerifier.create(service.getRequired('group-name'))
                .expectNext(Group.builder().id(123L).name('group-name').build())
                .verifyComplete()
    }

    def 'should throw an exception when a required group is not found'() {
        groupsRepo.getForName('group-name') >> Mono.empty()

        expect:
        StepVerifier.create(service.getRequired('group-name'))
                .verifyError(ObjectNotFoundException)
    }

    def 'should be able to retrieve all groups'() {
        groupsRepo.getGroups(100, 0) >> Flux.just(
                Group.builder().id(1L).name('group1').build(),
                Group.builder().id(2L).name('group2').build())

        expect:
        StepVerifier.create(service.getGroups(100, 0))
                .expectNext(Group.builder().id(1L).name('group1').build(),
                        Group.builder().id(2L).name('group2').build())
                .verifyComplete()
    }

    def 'creating should save and return the saved group'() {
        def request = Group.builder().name('group').build()
        def inserted = Group.builder().id(1L).name('group').build()
        groupsRepo.insert(request) >> Mono.just(inserted)

        expect:
        StepVerifier.create(service.create(request))
                .expectNext(inserted)
                .verifyComplete()
    }

    def 'create should map the exception for a duplicate name to a DuplicateException'() {
        def request = Group.builder().name('app').build()
        groupsRepo.insert(request) >> Mono.error(new DataIntegrityViolationException("test error"))

        expect:
        StepVerifier.create(service.create(request))
                .verifyError(DuplicateException)
    }

    def 'deleting should delete the group'() {
        PublisherProbe groupsProbe = PublisherProbe.empty()
        groupsRepo.delete(100) >> groupsProbe.mono()

        expect:
        StepVerifier.create(service.delete(100))
                .verifyComplete()
        groupsProbe.assertWasSubscribed()
    }

    def 'deleting by name should delete the group'() {
        groupsRepo.getForName('group') >> Mono.just(Group.builder().id(100L).name('group').build())
        PublisherProbe groupsProbe = PublisherProbe.empty()
        groupsRepo.delete(100) >> groupsProbe.mono()

        expect:
        StepVerifier.create(service.delete('group'))
                .verifyComplete()
        groupsProbe.assertWasSubscribed()
    }

    def 'should be able to add a role to a group'() {
        PublisherProbe probe = PublisherProbe.empty()
        groupRolesRepo.insert(1, 2) >> probe.mono()

        expect:
        StepVerifier.create(service.addRoleToGroup(1, 2))
                .verifyComplete()
        probe.assertWasSubscribed()
    }

    def 'should be able to remove a role from a group'() {
        PublisherProbe probe = PublisherProbe.empty()
        groupRolesRepo.delete(1, 2) >> probe.mono()

        expect:
        StepVerifier.create(service.removeRoleFromGroup(1, 2))
                .verifyComplete()
        probe.assertWasSubscribed()
    }
}
