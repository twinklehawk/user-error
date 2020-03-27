plugins {
    `java-library`
    groovy
    id("io.freefair.lombok")
    `maven-publish`
}

dependencies {
    internal(enforcedPlatform(project(":platform")))
    api("org.slf4j:slf4j-api")
    api("io.projectreactor:reactor-core")
    api("com.fasterxml.jackson.core:jackson-annotations")
    api("com.google.guava:guava")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    runtimeOnly("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    runtimeOnly("com.fasterxml.jackson.module:jackson-module-parameter-names")
    testImplementation("org.codehaus.groovy:groovy-all")
    testImplementation("org.spockframework:spock-core")
    testImplementation("org.hamcrest:hamcrest-core")
    testRuntimeOnly("net.bytebuddy:byte-buddy")
    testRuntimeOnly("org.objenesis:objenesis")
    testRuntimeOnly("ch.qos.logback:logback-classic")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8

    withSourcesJar()
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
