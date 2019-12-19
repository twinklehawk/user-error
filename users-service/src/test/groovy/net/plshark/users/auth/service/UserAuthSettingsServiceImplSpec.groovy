package net.plshark.users.auth.service

import net.plshark.users.auth.model.UserAuthSettings
import net.plshark.users.auth.repo.UserAuthSettingsRepository
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification

class UserAuthSettingsServiceImplSpec extends Specification {

    def userSettingsRepo = Mock(UserAuthSettingsRepository)
    def service = new UserAuthSettingsServiceImpl(userSettingsRepo)

    def 'looking up settings for a user should return the matching settings when found'() {
        def settings = UserAuthSettings.builder().refreshTokenEnabled(false).build()
        userSettingsRepo.findByUsername('test-user') >> Mono.just(settings)

        expect:
        StepVerifier.create(service.findByUsername('test-user'))
                .expectNext(settings)
                .verifyComplete()
    }

    def 'looking up settings for a user should return default settings when no match is found'() {
        userSettingsRepo.findByUsername('test-user') >> Mono.empty()

        expect:
        StepVerifier.create(service.findByUsername('test-user'))
                .expectNextCount(1)
                .verifyComplete()
    }
}
