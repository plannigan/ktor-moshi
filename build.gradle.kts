plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.nexus)
}

allprojects {
    group = "com.hypercubetools"
    version = "2.1.0"

    repositories {
        mavenCentral()
    }
}

nexusPublishing {
    repositories {
        sonatype()
    }
}
