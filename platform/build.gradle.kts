plugins {
    `java-platform`
}

val testcontainersVersion = "1.16.2"

javaPlatform {
    allowDependencies()
}

dependencies {
    api(enforcedPlatform("org.springframework.boot:spring-boot-dependencies:2.6.2"))

    constraints {
        api("com.auth0:java-jwt:3.18.2")
        api("io.mockk:mockk:1.12.2")
        api("com.google.guava:guava:31.0.1-jre")
        api("org.testcontainers:testcontainers:$testcontainersVersion")
        api("org.testcontainers:postgresql:$testcontainersVersion")
    }
}
