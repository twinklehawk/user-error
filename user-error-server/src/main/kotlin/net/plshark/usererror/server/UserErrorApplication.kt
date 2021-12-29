package net.plshark.usererror.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class UserErrorApplication

fun main(args: Array<String>) {
    @Suppress("SpreadOperator")
    runApplication<UserErrorApplication>(*args)
}
