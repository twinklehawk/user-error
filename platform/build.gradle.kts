plugins {
    `java-platform`
}

javaPlatform {
    allowDependencies()
}

dependencies {
    api(enforcedPlatform("org.springframework.boot:spring-boot-dependencies:2.6.1"))

    constraints {
        api("com.auth0:java-jwt:3.18.2")
        api("io.mockk:mockk:1.12.1")
        api("com.google.guava:guava:31.0.1-jre")
    }
}
