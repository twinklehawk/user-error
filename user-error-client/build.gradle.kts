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
    api(project(":user-error-api"))
    api("org.springframework:spring-webflux")
    api("com.auth0:java-jwt")
    api("org.springframework.security:spring-security-web")
    api("org.springframework.security:spring-security-config")
    implementation("com.github.ben-manes.caffeine:caffeine")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework:spring-context")
    testImplementation("org.springframework:spring-test")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("io.mockk:mockk")
    testImplementation("com.squareup.okhttp3:mockwebserver")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testRuntimeOnly("ch.qos.logback:logback-classic")
    testRuntimeOnly("io.projectreactor.netty:reactor-netty-http")
}
