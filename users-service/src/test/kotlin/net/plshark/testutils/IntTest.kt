package net.plshark.testutils

import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceAccessMode
import org.junit.jupiter.api.parallel.ResourceLock

@Tag("integrationTest")
@Execution(ExecutionMode.SAME_THREAD)
@ResourceLock("integration", mode = ResourceAccessMode.READ_WRITE)
open class IntTest {
    companion object {
        val DB_URL = "r2dbc:postgresql://test_user:test_user_pass@localhost:5432/postgres?schema=users"
    }
}