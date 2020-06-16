plugins {
    kotlin("jvm") version "1.3.70" apply false
    kotlin("plugin.spring") version "1.3.70" apply false
    id("org.springframework.boot") version "2.3.1.RELEASE" apply false
    id("com.jfrog.bintray") version "1.8.4" apply false
}

allprojects {
    repositories {
        jcenter()
    }
    group = "net.plshark.users"
    version = "0.3.0"
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
