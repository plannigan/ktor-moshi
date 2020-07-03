plugins {
    kotlin("jvm")
    kotlin("kapt")
}

allprojects {
    group = "com.hypercubetools"
    version = "1.1.0-alpha-2"

    repositories {
        jcenter()
        mavenCentral()
    }
}