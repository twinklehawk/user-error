plugins {
    id("io.freefair.lombok") version "4.1.6" apply false
    id("org.springframework.boot") version "2.2.5.RELEASE" apply false
    id("com.jfrog.bintray") version "1.8.4" apply false
    kotlin("jvm") version "1.3.61" apply false
    kotlin("plugin.spring") version "1.3.61" apply false
}

allprojects {
    repositories {
        jcenter()
        maven { url = uri("https://repo.spring.io/milestone") }
    }
    group = "net.plshark.users"
    version = "0.3.0"
}

subprojects {
    plugins.withType<JavaLibraryPlugin> {
        val internal by configurations.creating {
            isVisible = false
            isCanBeConsumed = false
            isCanBeResolved = false
        }
        configurations["compileClasspath"].extendsFrom(internal)
        configurations["runtimeClasspath"].extendsFrom(internal)
        configurations["testCompileClasspath"].extendsFrom(internal)
        configurations["testRuntimeClasspath"].extendsFrom(internal)
    }

    tasks.withType<io.freefair.gradle.plugins.lombok.tasks.GenerateLombokConfig> {
        enabled = false
    }

    tasks.withType<JavaCompile> {
        options.compilerArgs.add("-parameters")
    }
}

configure(subprojects.filter{ it.name != "platform" }) {
    apply(plugin = "jacoco")

    tasks.withType<JacocoReport> {
        reports {
            xml.isEnabled = true
            html.isEnabled = false
            csv.isEnabled = false
        }
    }
}
