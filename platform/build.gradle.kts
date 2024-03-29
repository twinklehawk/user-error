plugins {
    `java-platform`
}

val testcontainersVersion = "1.17.4"

javaPlatform {
    allowDependencies()
}

dependencies {
    api(enforcedPlatform("org.springframework.boot:spring-boot-dependencies:2.7.4"))

    constraints {
        api("com.auth0:java-jwt:4.0.0")
        api("io.mockk:mockk:1.13.2")
        api("com.google.guava:guava:31.1-jre")
        api("org.testcontainers:testcontainers:$testcontainersVersion")
        api("org.testcontainers:postgresql:$testcontainersVersion")
    }
}
