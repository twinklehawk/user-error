plugins {
    kotlin("jvm") version "1.7.10" apply false
    kotlin("plugin.spring") version "1.6.21" apply false
    id("org.springframework.boot") version "2.7.1" apply false
    id("io.gitlab.arturbosch.detekt") version "1.19.0" apply false
}

allprojects {
    repositories {
        mavenCentral()
    }
    group = "net.plshark"
    version = "0.4.2"
}

configure(subprojects.filter{ it.name != "platform" }) {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "jacoco")
    apply(plugin = "io.gitlab.arturbosch.detekt")

    dependencies {
        "detektPlugins"("io.gitlab.arturbosch.detekt:detekt-formatting:1.19.0")
    }

    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_11
        withSourcesJar()
    }
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "11"
        }
    }
    tasks.withType<Test> {
        useJUnitPlatform()
    }
    tasks.withType<JacocoReport> {
        reports {
            xml.required.set(true)
            html.required.set(true)
            csv.required.set(false)
        }
    }
    tasks.withType<io.gitlab.arturbosch.detekt.Detekt> {
        allRules = true
        buildUponDefaultConfig = true
        jvmTarget = "11"
    }
}

configure(subprojects.filter{ it.name != "platform" && it.name != "user-error-api" }) {
    tasks.withType<JacocoCoverageVerification> {
        violationRules {
            rule {
                limit {
                    minimum = "0.8".toBigDecimal()
                }
            }
        }
    }
}

configure(subprojects.filter{ it.name == "user-error-client" || it.name == "user-error-api" }) {
    apply(plugin = "maven-publish")
    configure<PublishingExtension> {
        repositories {
            maven {
                name = "github"
                url = uri("https://maven.pkg.github.com/twinklehawk/user-error")
                credentials {
                    username = System.getenv("GITHUB_ACTOR")
                    password = System.getenv("GITHUB_TOKEN")
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
}

task("printVersion") {
    doFirst {
        println("$version")
    }
}
