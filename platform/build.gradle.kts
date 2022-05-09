plugins {
    `java-platform`
}

val testcontainersVersion = "1.17.1"

javaPlatform {
    allowDependencies()
}

dependencies {
    api(enforcedPlatform("org.springframework.boot:spring-boot-dependencies:2.6.7"))

    constraints {
        api("com.auth0:java-jwt:3.19.2")
        api("io.mockk:mockk:1.12.3")
        api("com.google.guava:guava:31.1-jre")
        api("org.testcontainers:testcontainers:$testcontainersVersion")
        api("org.testcontainers:postgresql:$testcontainersVersion")
    }
}
