plugins {
    `java-platform`
}

javaPlatform {
    allowDependencies()
}

dependencies {
    api(enforcedPlatform("org.springframework.boot:spring-boot-dependencies:2.5.2"))

    constraints {
        api("com.auth0:java-jwt:3.18.1")
        api("io.mockk:mockk:1.12.0")
        api("com.google.guava:guava:30.1.1-jre")
    }
}
