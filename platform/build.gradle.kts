plugins {
    `java-platform`
}

javaPlatform {
    allowDependencies()
}

dependencies {
    api(enforcedPlatform("org.springframework.boot:spring-boot-dependencies:2.4.5"))

    constraints {
        api("com.auth0:java-jwt:3.16.0")
        api("io.mockk:mockk:1.11.0")
        api("com.google.guava:guava:30.1.1-jre")
    }
}
