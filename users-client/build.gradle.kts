plugins {
    `java-library`
}

dependencies {
    implementation(enforcedPlatform(project(":platform")))
    implementation("com.github.ben-manes.caffeine:caffeine")
    api(project(":users-api"))
    api("org.springframework:spring-webflux")
    api("com.auth0:java-jwt")
    api("org.springframework.security:spring-security-web")
    api("org.springframework.security:spring-security-config")
    api("com.google.guava:guava")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.codehaus.groovy:groovy-all")
    testImplementation("org.spockframework:spock-core")
    testImplementation("org.hamcrest:hamcrest-core")
    testImplementation("org.springframework:spring-context")
    testImplementation("org.springframework:spring-test")
    testRuntimeOnly("net.bytebuddy:byte-buddy")
    testRuntimeOnly("org.objenesis:objenesis")
    testRuntimeOnly("ch.qos.logback:logback-classic")
}
