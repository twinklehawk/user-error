package net.plshark.usererror.server.role.impl

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import net.plshark.usererror.role.GroupCreate
import net.plshark.usererror.server.testutil.DbTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.r2dbc.core.DatabaseClient

@DbTest
class GroupsRepositoryImplTest {

    private lateinit var repo: GroupsRepositoryImpl

    @BeforeEach
    fun setup(dbClient: DatabaseClient) {
        repo = GroupsRepositoryImpl(dbClient)
    }

    @Test
    fun `inserting a group returns the inserted group with the ID set`() = runBlocking {
        val group = repo.insert(GroupCreate("test-group"))

        assertNotNull(group.id)
        assertEquals("test-group", group.name)
    }

    @Test
    fun `can retrieve a previously inserted group by ID`() = runBlocking {
        val group = repo.insert(GroupCreate("group"))

        assertEquals(group, repo.findById(group.id))
    }

    @Test
    fun `retrieving a group by ID when no group matches returns empty`() = runBlocking {
        assertNull(repo.findById(100))
    }

    @Test
    fun `can retrieve a previously inserted group by name`() = runBlocking {
        val group = repo.insert(GroupCreate("group"))

        assertEquals(group, repo.findByName("group"))
    }

    @Test
    fun `retrieving a group by name when no group matches returns empty`() = runBlocking {
        assertNull(repo.findByName("name"))
    }

    @Test
    fun `can delete a previously inserted group by ID`() = runBlocking {
        val group = repo.insert(GroupCreate("group"))
        repo.deleteById(group.id)

        assertNull(repo.findByName("name"))
    }

    @Test
    fun `no exception is thrown when attempting to delete a group that does not exist`() = runBlocking {
        repo.deleteById(200)
    }

    @Test
    fun `getGroups should return all results when there are less than max results`() = runBlocking {
        repo.insert(GroupCreate("group1"))
        repo.insert(GroupCreate("group2"))
        repo.insert(GroupCreate("group3"))

        // one group is inserted by the migration scripts
        assertEquals(4, repo.getGroups(50, 0).toList().size)
    }

    @Test
    fun `getGroups should return up to max results when there are more results`() = runBlocking {
        repo.insert(GroupCreate("group1"))
        repo.insert(GroupCreate("group2"))
        repo.insert(GroupCreate("group3"))

        assertEquals(2, repo.getGroups(2, 0).toList().size)
    }

    @Test
    fun `getGroups should start at the correct offset`() = runBlocking {
        repo.insert(GroupCreate("group1"))
        repo.insert(GroupCreate("group2"))
        repo.insert(GroupCreate("group3"))

        val list = repo.getGroups(2, 2).toList()
        assertEquals(2, list.size)
        assertEquals("group2", list[0].name)
        assertEquals("group3", list[1].name)
    }
}
