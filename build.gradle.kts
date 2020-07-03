plugins {
    kotlin("jvm")
    kotlin("kapt")
}

allprojects {
    group = "com.hypercubetools"
    version = "1.0.2-SNAPSHOT"

    repositories {
        jcenter()
        mavenCentral()
    }
}