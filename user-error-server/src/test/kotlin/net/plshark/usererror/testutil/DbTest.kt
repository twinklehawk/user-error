package net.plshark.usererror.testutil

import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.extension.ExtendWith

@Tag("databaseTest")
@ExtendWith(DbExtension::class)
annotation class DbTest
