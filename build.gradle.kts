plugins {
    kotlin("jvm") version "1.3.72" apply false
    kotlin("plugin.spring") version "1.3.72" apply false
    id("org.springframework.boot") version "2.3.3.RELEASE" apply false
    id("com.jfrog.bintray") version "1.8.5" apply false
    id("com.github.ben-manes.versions") version "0.29.0"
    id("io.gitlab.arturbosch.detekt") version "1.11.2" apply false
}

allprojects {
    repositories {
        jcenter()
    }
    group = "net.plshark"
    version = "0.4.1"
}

tasks.withType<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask> {
    checkConstraints = true
    gradleReleaseChannel = "current"
}

configure(subprojects.filter{ it.name != "platform" }) {
    apply(plugin = "jacoco")
    apply(plugin = "io.gitlab.arturbosch.detekt")

    tasks.withType<JacocoReport> {
        reports {
            xml.isEnabled = true
            html.isEnabled = true
            csv.isEnabled = false
        }
    }

    tasks.withType<io.gitlab.arturbosch.detekt.Detekt> {
        failFast = true
        buildUponDefaultConfig = true
        jvmTarget = "1.8"
    }

    dependencies {
        "detektPlugins"("io.gitlab.arturbosch.detekt:detekt-formatting:1.11.2")
    }
}

configure(subprojects.filter{ it.name != "platform" && it.name != "user-error-api" }) {
    tasks.withType<JacocoCoverageVerification> {
        violationRules {
            rule {
                limit {
                    minimum = "0.7".toBigDecimal()
                }
            }
        }
    }
}

task("printVersion") {
    doFirst {
        println("$version")
    }
}
