import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    id("org.springframework.boot")
    groovy
    kotlin("jvm")
    kotlin("plugin.spring")
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
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib-jdk8"))
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("io.mockk:mockk:1.9.3")
    testImplementation("com.opentable.components:otj-pg-embedded")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    // for flyway
    runtimeOnly("org.springframework:spring-jdbc")
    runtimeOnly("org.postgresql:postgresql")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
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
