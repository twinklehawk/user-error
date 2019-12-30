package net.plshark.users.auth.repo.springdata

import com.opentable.db.postgres.junit.EmbeddedPostgresRules
import com.opentable.db.postgres.junit.PreparedDbRule
import net.plshark.testutils.PlsharkFlywayPreparer
import net.plshark.users.auth.model.UserAuthSettings
import net.plshark.users.model.User
import net.plshark.users.repo.springdata.DatabaseClientHelper
import net.plshark.users.repo.springdata.SpringDataUsersRepository
import org.junit.Rule
import reactor.test.StepVerifier
import spock.lang.Specification

class SpringDataUserAuthSettingsRepositorySpec extends Specification {

    @Rule
    PreparedDbRule dbRule = EmbeddedPostgresRules.preparedDatabase(PlsharkFlywayPreparer.defaultPreparer())

    SpringDataUserAuthSettingsRepository repo
    SpringDataUsersRepository usersRepo

    def setup() {
        def db = DatabaseClientHelper.buildTestClient(dbRule)
        repo = new SpringDataUserAuthSettingsRepository(db)
        usersRepo = new SpringDataUsersRepository(db)
    }

    def 'inserting settings returns the inserted settings with the ID set'() {
        when:
        def user = usersRepo.insert(User.builder().username('test-user').password('test-pass').build()).block()
        def inserted = repo.insert(UserAuthSettings.builder()
                .userId(user.id)
                .refreshTokenEnabled(false)
                .authTokenExpiration(40)
                .build()).block()

        then:
        inserted.id != null
        inserted.userId == user.id
        !inserted.refreshTokenEnabled
        inserted.authTokenExpiration == 40
        inserted.refreshTokenExpiration == null
    }

    def 'cannot insert settings with an ID already set'() {
        when:
        repo.insert(UserAuthSettings.builder().id(100).userId(200).refreshTokenEnabled(false).build()).block()

        then:
        thrown(IllegalArgumentException)
    }

    def 'cannot insert settings without a user ID set'() {
        when:
        repo.insert(UserAuthSettings.builder().refreshTokenEnabled(false).build()).block()

        then:
        thrown(NullPointerException)
    }

    def 'can retrieve previously inserted settings by user ID'() {
        def user = usersRepo.insert(User.builder().username('test-user').password('test-pass').build()).block()
        def inserted = repo.insert(UserAuthSettings.builder().userId(user.id).refreshTokenEnabled(true).build()).block()

        when:
        def settings = repo.findByUserId(user.id).block()

        then:
        settings == inserted
    }

    def 'retrieving by user ID when no rows match returns empty'() {
        expect:
        StepVerifier.create(repo.findByUserId(1000))
                .verifyComplete()
    }

    def 'can retrieve previously inserted settings by username'() {
        def user = usersRepo.insert(User.builder().username('test-user').password('test-pass').build()).block()
        def inserted = repo.insert(UserAuthSettings.builder().userId(user.id).refreshTokenEnabled(true).build()).block()

        when:
        def settings = repo.findByUsername(user.username).block()

        then:
        settings == inserted
    }

    def 'retrieving by username when no rows match returns empty'() {
        expect:
        StepVerifier.create(repo.findByUsername('not a user'))
                .verifyComplete()
    }
}
