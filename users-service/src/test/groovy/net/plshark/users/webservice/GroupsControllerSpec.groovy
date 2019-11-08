package net.plshark.users.webservice

import net.plshark.ObjectNotFoundException
import net.plshark.users.model.Group
import net.plshark.users.service.GroupsService
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import reactor.test.publisher.PublisherProbe
import spock.lang.Specification

class GroupsControllerSpec extends Specification {

    def groupsService = Mock(GroupsService)
    def controller = new GroupsController(groupsService)

    def 'get should pass through the response from the service'() {
        def group = Group.create(1, 'group')
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
        def request = Group.create('group')
        def inserted = Group.create(1, 'group')
        groupsService.insert(request) >> Mono.just(inserted)

        expect:
        StepVerifier.create(controller.insert(request))
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
