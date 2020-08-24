package net.plshark.users.repo.springdata

import io.r2dbc.spi.ConnectionFactories
import net.plshark.testutils.DbIntTest
import net.plshark.users.model.GroupCreate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.r2dbc.core.DatabaseClient
import reactor.test.StepVerifier

class SpringDataGroupsRepositoryTest : DbIntTest() {

    private lateinit var repo: SpringDataGroupsRepository

    @BeforeEach
    fun setup() {
        val connectionFactory = ConnectionFactories.get(DB_URL)
        val dbClient = DatabaseClient.create(connectionFactory)
        repo = SpringDataGroupsRepository(dbClient)
    }

    @Test
    fun `inserting a group returns the inserted group with the ID set`() {
        val group = repo.insert(GroupCreate("test-group")).block()!!

        assertNotNull(group.id)
        assertEquals("test-group", group.name)
    }

    @Test
    fun `can retrieve a previously inserted group by ID`() {
        val group = repo.insert(GroupCreate("group")).block()!!

        StepVerifier.create(repo.findById(group.id))
                .expectNext(group)
                .verifyComplete()
    }

    @Test
    fun `retrieving a group by ID when no group matches returns empty`() {
        StepVerifier.create(repo.findById(100))
                .expectNextCount(0)
                .verifyComplete()
    }

    @Test
    fun `can retrieve a previously inserted group by name`() {
        val group = repo.insert(GroupCreate("group")).block()!!

        StepVerifier.create(repo.findByName("group"))
                .expectNext(group)
                .verifyComplete()
    }

    @Test
    fun `retrieving a group by name when no group matches returns empty`() {
        StepVerifier.create(repo.findByName("name"))
                .expectNextCount(0)
                .verifyComplete()
    }

    @Test
    fun `can delete a previously inserted group by ID`() {
        val group = repo.insert(GroupCreate("group")).block()!!
        repo.delete(group.id).block()

        StepVerifier.create(repo.findByName("name"))
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
        repo.insert(GroupCreate("group1"))
                .then(repo.insert(GroupCreate("group2")))
                .then(repo.insert(GroupCreate("group3")))
                .block()

        StepVerifier.create(repo.getGroups(50, 0))
                // one group is inserted by the migration scripts
                .expectNextCount(4)
                .verifyComplete()
    }

    @Test
    fun `getGroups should return up to max results when there are more results`() {
        repo.insert(GroupCreate("group1"))
            .then(repo.insert(GroupCreate("group2")))
            .then(repo.insert(GroupCreate("group3")))
            .block()

        StepVerifier.create(repo.getGroups(2, 0))
                .expectNextCount(2)
                .verifyComplete()
    }

    @Test
    fun `getGroups should start at the correct offset`() {
        repo.insert(GroupCreate("group1"))
            .then(repo.insert(GroupCreate("group2")))
            .then(repo.insert(GroupCreate("group3")))
            .block()

        StepVerifier.create(repo.getGroups(2, 2))
            .expectNextMatches { group -> group.name == "group2" }
            .expectNextMatches { group -> group.name == "group3" }
            .verifyComplete()
    }
}
