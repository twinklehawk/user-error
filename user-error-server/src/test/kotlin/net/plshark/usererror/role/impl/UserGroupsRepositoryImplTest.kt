package net.plshark.usererror.role.impl

import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import net.plshark.usererror.role.ApplicationCreate
import net.plshark.usererror.role.GroupCreate
import net.plshark.usererror.role.RoleCreate
import net.plshark.usererror.role.RolesRepositoryImpl
import net.plshark.usererror.testutil.DbTest
import net.plshark.usererror.user.UserCreate
import net.plshark.usererror.user.UsersRepositoryImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.r2dbc.core.DatabaseClient

@DbTest
class UserGroupsRepositoryImplTest {

    private lateinit var repo: UserGroupsRepositoryImpl
    private lateinit var usersRepo: UsersRepositoryImpl
    private lateinit var groupsRepo: GroupsRepositoryImpl
    private lateinit var appsRepo: ApplicationsRepositoryImpl
    private lateinit var rolesRepo: RolesRepositoryImpl
    private lateinit var groupRolesRepo: GroupRolesRepositoryImpl

    @BeforeEach
    fun setup(dbClient: DatabaseClient) {
        repo = UserGroupsRepositoryImpl(dbClient)
        groupsRepo = GroupsRepositoryImpl(dbClient)
        usersRepo = UsersRepositoryImpl(dbClient)
        appsRepo = ApplicationsRepositoryImpl(dbClient)
        rolesRepo = RolesRepositoryImpl(dbClient)
        groupRolesRepo = GroupRolesRepositoryImpl(dbClient)
    }

    @Test
    fun `insert should save a group and user association and should be retrievable`() = runBlocking {
        val group = groupsRepo.insert(GroupCreate("test-name"))
        val user = usersRepo.insert(UserCreate("test-user", "pass"))

        repo.insert(user.id, group.id)

        assertEquals(listOf(group), repo.findGroupsByUserId(user.id).toList())
    }

    @Test
    fun `retrieving should return empty when no users are assigned to the group`() = runBlocking {
        assertEquals(0, repo.findGroupsByUserId(123).count())
    }

    @Test
    fun `delete should delete a group-user association`() = runBlocking {
        val group = groupsRepo.insert(GroupCreate("test-group"))
        val user = usersRepo.insert(UserCreate("test-user", "pass"))
        repo.insert(user.id, group.id)

        repo.deleteById(user.id, group.id)

        assertEquals(0, repo.findGroupsByUserId(user.id).count())
    }

    @Test
    fun `delete should not throw an exception if the group-user association does not already exist`() = runBlocking {
        repo.deleteById(100, 200)
    }

    @Test
    fun `retrieving roles should return each role in each group the user belongs to`(): Unit = runBlocking {
        val app1 = appsRepo.insert(ApplicationCreate("test-app"))
        val role1 = rolesRepo.insert(RoleCreate(app1.id, "role1"))
        val role2 = rolesRepo.insert(RoleCreate(app1.id, "role2"))
        rolesRepo.insert(RoleCreate(app1.id, "role3"))
        val group = groupsRepo.insert(GroupCreate("test-group"))
        val user = usersRepo.insert(UserCreate("user", "pass"))
        groupRolesRepo.insert(group.id, role1.id)
        groupRolesRepo.insert(group.id, role2.id)
        repo.insert(user.id, group.id)

        val list = repo.findGroupRolesByUserId(user.id).toList()
        assertEquals(2, list.size)
        assertTrue(list.contains(role1))
        assertTrue(list.contains(role2))
    }
}
