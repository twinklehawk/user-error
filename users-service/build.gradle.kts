import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot")
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
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("io.r2dbc:r2dbc-postgresql")
    implementation("com.auth0:java-jwt")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib-jdk8"))
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("io.mockk:mockk")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.flywaydb:flyway-core")
    testRuntimeOnly("org.postgresql:postgresql")
    testRuntimeOnly(project(":db"))
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
    val runIntTests = System.getProperties().getProperty("runIntTests") == "true"
    useJUnitPlatform {
        if (!runIntTests)
            excludeTags("integrationTest")
    }
}
