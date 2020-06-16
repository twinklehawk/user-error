import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `maven-publish`
    kotlin("jvm")
    kotlin("plugin.spring")
}

val internal by configurations.creating {
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
    api(project(":users-api"))
    api("org.springframework:spring-webflux")
    api("com.auth0:java-jwt")
    api("org.springframework.security:spring-security-web")
    api("org.springframework.security:spring-security-config")
    implementation("com.github.ben-manes.caffeine:caffeine")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib-jdk8"))
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework:spring-context")
    testImplementation("org.springframework:spring-test")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("io.mockk:mockk")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testRuntimeOnly("ch.qos.logback:logback-classic")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    withSourcesJar()
}
tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}
tasks.withType<Test> {
    useJUnitPlatform()
}

publishing {
    repositories {
        maven {
            name = "bintray"
            val bintrayUsername = "twinklehawk"
            val bintrayRepoName = "maven"
            val bintrayPackageName = "net.plshark.users"
            url = uri("https://api.bintray.com/maven/$bintrayUsername/$bintrayRepoName/$bintrayPackageName/;publish=1")
            credentials {
                username = System.getenv("BINTRAY_USER")
                password = System.getenv("BINTRAY_API_KEY")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            versionMapping {
                usage("java-api") {
                    fromResolutionResult()
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
        }
    }
}
