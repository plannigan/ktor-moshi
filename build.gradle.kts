plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.kapt)
}

allprojects {
    group = "com.hypercubetools"
    version = "2.1.0"

    repositories {
        jcenter()
        mavenCentral()
    }
}
