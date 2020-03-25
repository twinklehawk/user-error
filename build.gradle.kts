plugins {
    id("io.freefair.lombok") version "4.1.6" apply false
    id("org.springframework.boot") version "2.2.5.RELEASE" apply false
    id("com.jfrog.bintray") version "1.8.4" apply false
}

val javaProjects = listOf(project(":users-api"), project(":users-client"), project(":users-service"))

allprojects {
    repositories {
        jcenter()
        maven { url = uri("https://repo.spring.io/milestone") }
    }
    group = "net.plshark.users"
    version = "0.2.2"
}

configure(javaProjects) {
    tasks.withType<io.freefair.gradle.plugins.lombok.tasks.GenerateLombokConfig> {
        enabled = false
    }

    tasks.withType<JavaCompile> {
        sourceCompatibility = JavaVersion.VERSION_1_8.toString()
        targetCompatibility = JavaVersion.VERSION_1_8.toString()

        options.compilerArgs.add("-parameters")
    }

    tasks.withType<JacocoReport> {
        reports {
            xml.isEnabled = true
            html.isEnabled = false
            csv.isEnabled = false
        }
    }
}
