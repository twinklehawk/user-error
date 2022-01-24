plugins {
    `java-platform`
}

val testcontainersVersion = "1.16.3"

javaPlatform {
    allowDependencies()
}

dependencies {
    api(enforcedPlatform("org.springframework.boot:spring-boot-dependencies:2.6.3"))

    constraints {
        api("com.auth0:java-jwt:3.18.3")
        api("io.mockk:mockk:1.12.2")
        api("com.google.guava:guava:31.0.1-jre")
        api("org.testcontainers:testcontainers:$testcontainersVersion")
        api("org.testcontainers:postgresql:$testcontainersVersion")
    }
}
