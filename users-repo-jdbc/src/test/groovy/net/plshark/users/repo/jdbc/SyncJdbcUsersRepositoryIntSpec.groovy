package net.plshark.users.repo.jdbc

import javax.inject.Inject

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.EmptyResultDataAccessException

import net.plshark.jdbc.RepoTestConfig
import net.plshark.users.User
import spock.lang.Specification

@SpringBootTest(classes = RepoTestConfig.class)
class SyncJdbcUsersRepositoryIntSpec extends Specification {

    @Inject
    SyncJdbcUsersRepository repo

    def "inserting a user returns the inserted user with the ID set"() {
        when:
        User inserted = repo.insert(new User("name", "pass"))

        then:
        inserted.id.isPresent()
        inserted.username == "name"
        inserted.password.get() == "pass"

        cleanup:
        repo.delete(inserted.id.get())
    }

    def "can retrieve a previously inserted user by ID"() {
        User inserted = repo.insert(new User("name", "pass"))

        when:
        User user = repo.getForId(inserted.id.get()).get()

        then:
        user == inserted

        cleanup:
        repo.delete(inserted.id.get())
    }

    def "can retrieve  previously inserted user by username"() {
        User inserted = repo.insert(new User("name", "pass"))

        when:
        User user = repo.getForUsername("name").get()

        then:
        user == inserted

        cleanup:
        repo.delete(inserted.id.get())
    }

    def "can delete a previously inserted user by ID"() {
        User inserted = repo.insert(new User("name", "pass"))

        when:
        repo.delete(inserted.id.get())
        Optional<User> retrieved = repo.getForId(inserted.id.get())

        then: "should return empty since the row should be gone"
        !retrieved.isPresent()
    }

    def "no exception is thrown when attempting to delete a user that does not exist"() {
        when:
        repo.delete(10000)

        then:
        notThrown(Exception)
    }

    def "update password should change the password if the current password is correct"() {
        User inserted = repo.insert(new User("name", "pass"))

        when:
        repo.updatePassword(inserted.id.get(), "pass", "new-pass")
        User user = repo.getForId(inserted.id.get()).get()

        then:
        user.password.get() == "new-pass"

        cleanup:
        repo.delete(inserted.id.get())
    }

    def "update password should throw an EmptyResultDataAccessException if the current password is wrong"() {
        User inserted = repo.insert(new User("name", "pass"))

        when:
        repo.updatePassword(inserted.id.get(), "wrong-pass", "new-pass")

        then:
        thrown(EmptyResultDataAccessException)

        when:
        User user = repo.getForId(inserted.id.get()).get()

        then:
        user.password.get() == "pass"

        cleanup:
        repo.delete(inserted.id.get())
    }

    def "update password should throw an EmptyResultDataAccessException if no user has the ID"() {
        when:
        repo.updatePassword(1000, "pass", "new-pass")

        then:
        thrown(EmptyResultDataAccessException)
    }
}
