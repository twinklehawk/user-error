plugins {
    kotlin("plugin.spring")
}

val internal: Configuration by configurations.creating {
    isVisible = false
    isCanBeConsumed = false
    isCanBeResolved = false
}
configurations["compileClasspath"].extendsFrom(internal)
configurations["runtimeClasspath"].extendsFrom(internal)
configurations["testCompileClasspath"].extendsFrom(internal)
configurations["testRuntimeClasspath"].extendsFrom(internal)

dependencies {
    internal(enforcedPlatform(project(":platform")))
    api("org.slf4j:slf4j-api")
    api("io.projectreactor:reactor-core")
    api("com.fasterxml.jackson.core:jackson-annotations")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib-jdk8"))
    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")
    runtimeOnly("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("io.mockk:mockk")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testRuntimeOnly("ch.qos.logback:logback-classic")
}
