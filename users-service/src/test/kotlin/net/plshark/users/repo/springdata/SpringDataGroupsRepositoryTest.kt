package net.plshark.users.repo.springdata

import com.opentable.db.postgres.junit.EmbeddedPostgresRules
import net.plshark.testutils.PlsharkFlywayPreparer
import net.plshark.users.model.Group
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.test.StepVerifier

class SpringDataGroupsRepositoryTest {

    //@Rule
    val dbRule = EmbeddedPostgresRules.preparedDatabase(PlsharkFlywayPreparer.defaultPreparer())

    private lateinit var repo: SpringDataGroupsRepository

    @BeforeEach
    fun setup() {
        repo = SpringDataGroupsRepository(DatabaseClientHelper.buildTestClient(dbRule))
    }

    @Test
    fun `inserting a group returns the inserted group with the ID set`() {
        val group = repo.insert(Group(null, "test-group")).block()!!

        assertNotNull(group.id)
        assertEquals("test-group", group.name)
    }

    @Test
    fun `can retrieve a previously inserted group by ID`() {
        val group = repo.insert(Group(null, "group")).block()!!

        StepVerifier.create(repo.getForId(group.id!!))
                .expectNext(group)
                .verifyComplete()
    }

    @Test
    fun `retrieving a group by ID when no group matches returns empty`() {
        StepVerifier.create(repo.getForId(100))
                .expectNextCount(0)
                .verifyComplete()
    }

    @Test
    fun `can retrieve a previously inserted group by name`() {
        val group = repo.insert(Group(null, "group")).block()!!

        StepVerifier.create(repo.getForName("group"))
                .expectNext(group)
                .verifyComplete()
    }

    @Test
    fun `retrieving a group by name when no group matches returns empty`() {
        StepVerifier.create(repo.getForName("name"))
                .expectNextCount(0)
                .verifyComplete()
    }

    @Test
    fun `can delete a previously inserted group by ID`() {
        val group = repo.insert(Group(null, "group")).block()!!
        repo.delete(group.id!!).block()

        StepVerifier.create(repo.getForName("name"))
                .expectNextCount(0)
                .verifyComplete()
    }

    @Test
    fun `no exception is thrown when attempting to delete a group that does not exist`() {
        StepVerifier.create(repo.delete(200))
                .verifyComplete()
    }

    @Test
    fun `getGroups should return all results when there are less than max results`() {
        repo.insert(Group(null, "group1"))
                .then(repo.insert(Group(null, "group2")))
                .then(repo.insert(Group(null, "group3")))
                .block()

        StepVerifier.create(repo.getGroups(50, 0))
                // one group is inserted by the migration scripts
                .expectNextCount(4)
                .verifyComplete()
    }

    @Test
    fun `getGroups should return up to max results when there are more results`() {
        repo.insert(Group(null, "group1"))
            .then(repo.insert(Group(null, "group2")))
            .then(repo.insert(Group(null, "group3")))
            .block()

        StepVerifier.create(repo.getGroups(2, 0))
                .expectNextCount(2)
                .verifyComplete()
    }

    @Test
    fun `getGroups should start at the correct offset`() {
        repo.insert(Group(null, "group1"))
            .then(repo.insert(Group(null, "group2")))
            .then(repo.insert(Group(null, "group3")))
            .block()

        StepVerifier.create(repo.getGroups(2, 2))
            .expectNextMatches { group -> group.name == "group2" }
            .expectNextMatches { group -> group.name == "group3" }
            .verifyComplete()
    }
}
