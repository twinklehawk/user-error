plugins {
    `java-platform`
}

javaPlatform {
    allowDependencies()
}

dependencies {
    api(enforcedPlatform("org.springframework.boot:spring-boot-dependencies:2.3.3.RELEASE"))

    constraints {
        api("com.auth0:java-jwt:3.10.3")
        api("io.mockk:mockk:1.10.0")
        api("com.google.guava:guava:29.0-jre")
    }
}
