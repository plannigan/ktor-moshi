include("library", "client", "sample")

pluginManagement {
    plugins {
        kotlin("jvm") version "1.3.72"
        kotlin("kapt") version "1.3.72"
        id("com.jfrog.bintray") version "1.8.5"
    }
}
