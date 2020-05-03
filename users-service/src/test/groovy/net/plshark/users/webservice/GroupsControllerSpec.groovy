package net.plshark.users.webservice

import net.plshark.errors.ObjectNotFoundException

import net.plshark.users.service.GroupsService
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import reactor.test.publisher.PublisherProbe
import spock.lang.Specification

class GroupsControllerSpec extends Specification {

    def groupsService = Mock(GroupsService)
    def controller = new GroupsController(groupsService)

    def 'get should pass through the response from the service'() {
        def group = Group.builder().id(1).name('group').build()
        groupsService.get('group') >> Mono.just(group)

        expect:
        StepVerifier.create(controller.get('group'))
                .expectNext(group)
                .verifyComplete()
    }

    def 'getting a group should throw an exception when the group does not exist'() {
        groupsService.get('test-group') >> Mono.empty()

        expect:
        StepVerifier.create(controller.get('test-group'))
                .verifyError(ObjectNotFoundException)
    }

    def 'insert should pass through the response from the service'() {
        def request = Group.builder().name('group').build()
        def inserted = Group.builder().id(1).name('group').build()
        groupsService.create(request) >> Mono.just(inserted)

        expect:
        StepVerifier.create(controller.create(request))
                .expectNext(inserted)
                .verifyComplete()
    }

    def 'delete should pass through the response from the service'() {
        def probe = PublisherProbe.empty()
        groupsService.delete('group') >> probe.mono()

        expect:
        StepVerifier.create(controller.delete('group'))
                .verifyComplete()
        probe.assertWasSubscribed()
    }
}
