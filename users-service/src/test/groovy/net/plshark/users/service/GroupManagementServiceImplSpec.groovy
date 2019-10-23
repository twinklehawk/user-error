package net.plshark.users.service

import net.plshark.users.model.Group
import net.plshark.users.repo.GroupRolesRepository
import net.plshark.users.repo.GroupsRepository
import net.plshark.users.repo.UserGroupsRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import reactor.test.publisher.PublisherProbe
import spock.lang.Specification

class GroupManagementServiceImplSpec extends Specification {

    GroupsRepository groupsRepo = Mock()
    UserGroupsRepository userGroupsRepo = Mock()
    GroupRolesRepository groupRolesRepo = Mock()
    def service = new GroupManagementServiceImpl(groupsRepo, userGroupsRepo, groupRolesRepo)

    def 'should be able to retrieve groups by name'() {
        groupsRepo.getForName('group-name') >> Mono.just(Group.create(123L, 'group-name'))

        expect:
        StepVerifier.create(service.getGroupByName('group-name'))
                .expectNext(Group.create(123L, 'group-name'))
                .verifyComplete()
    }

    def 'should be able to retrieve all groups'() {
        groupsRepo.getGroups(100, 0) >> Flux.just(
                Group.create(1L, 'group1'),
                Group.create(2L, 'group2'))

        expect:
        StepVerifier.create(service.getGroups(100, 0))
                .expectNext(Group.create(1L, 'group1'), Group.create(2L, 'group2'))
                .verifyComplete()
    }

    def 'inserting should save and return the saved group'() {
        groupsRepo.insert(Group.create('group')) >> Mono.just(Group.create(1L, 'group'))

        expect:
        StepVerifier.create(service.insertGroup(Group.create('group')))
                .expectNext(Group.create(1L, 'group'))
                .verifyComplete()
    }

    def 'deleting should delete all user/group associations, all group/role associations, and the group'() {
        PublisherProbe userGroupsProbe = PublisherProbe.empty()
        userGroupsRepo.deleteUserGroupsForGroup(100) >> userGroupsProbe.mono()
        PublisherProbe groupRolesProbe = PublisherProbe.empty()
        groupRolesRepo.deleteForGroup(100) >> groupRolesProbe.mono()
        PublisherProbe groupsProbe = PublisherProbe.empty()
        groupsRepo.delete(100) >> groupsProbe.mono()

        expect:
        StepVerifier.create(service.deleteGroup(100))
                .verifyComplete()
        userGroupsProbe.assertWasSubscribed()
        groupRolesProbe.assertWasSubscribed()
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
