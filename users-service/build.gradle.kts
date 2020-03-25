plugins {
    java
    id("org.springframework.boot")
}

dependencies {
    annotationProcessor(enforcedPlatform(project(":platform")))
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    implementation(enforcedPlatform(project(":platform")))
    implementation(project(":users-api"))
    implementation(project(":users-client"))
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot.experimental:spring-boot-starter-data-r2dbc")
    implementation("io.r2dbc:r2dbc-postgresql")
    implementation("com.auth0:java-jwt")
    implementation("org.flywaydb:flyway-core")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.codehaus.groovy:groovy-all")
    testImplementation("org.spockframework:spock-core")
    testImplementation("org.hamcrest:hamcrest-core")
    testImplementation("com.opentable.components:otj-pg-embedded")
    testRuntimeOnly("net.bytebuddy:byte-buddy")
    testRuntimeOnly("org.objenesis:objenesis")
    // for flyway
    runtimeOnly("org.springframework:spring-jdbc")
    runtimeOnly("org.postgresql:postgresql")
}
