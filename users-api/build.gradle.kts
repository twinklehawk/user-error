plugins {
    `java-library`
}

dependencies {
    implementation(enforcedPlatform(project(":platform")))
    api("org.slf4j:slf4j-api")
    api("io.projectreactor:reactor-core")
    api("com.fasterxml.jackson.core:jackson-annotations")
    api("com.google.guava:guava")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    runtimeOnly("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    runtimeOnly("com.fasterxml.jackson.module:jackson-module-parameter-names")
    testImplementation("org.codehaus.groovy:groovy-all")
    testImplementation("org.spockframework:spock-core")
    testImplementation("org.hamcrest:hamcrest-core")
    testRuntimeOnly("net.bytebuddy:byte-buddy")
    testRuntimeOnly("org.objenesis:objenesis")
    testRuntimeOnly("ch.qos.logback:logback-classic")
}
