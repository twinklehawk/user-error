plugins {
    `java-platform`
}

javaPlatform {
    allowDependencies()
}

dependencies {
    api(enforcedPlatform("org.springframework.boot:spring-boot-dependencies:2.3.1.RELEASE"))

    constraints {
        api("com.auth0:java-jwt:3.10.1")
        api("io.mockk:mockk:1.9.3")
        api("com.google.guava:guava:28.2-jre")
    }
}
