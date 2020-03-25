plugins {
    `java-platform`
}

javaPlatform {
    allowDependencies()
}

dependencies {
    api(enforcedPlatform("org.springframework.boot:spring-boot-dependencies:2.2.5.RELEASE"))
    api(enforcedPlatform("org.springframework.boot.experimental:spring-boot-bom-r2dbc:0.1.0.M3"))

    constraints {
        api("org.codehaus.groovy:groovy-all:2.5.9")
        api("org.spockframework:spock-core:1.3-groovy-2.5")
        api("org.spockframework:spock-spring:1.3-groovy-2.5")
        api("org.objenesis:objenesis:3.1")
        api("com.opentable.components:otj-pg-embedded:0.13.3")
        api("com.auth0:java-jwt:3.10.1")
        api("com.google.guava:guava:28.2-jre")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["javaPlatform"])
        }
    }
}
