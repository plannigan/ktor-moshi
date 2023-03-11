include("core", "sample")

pluginManagement {
    plugins {
        kotlin("jvm") version "1.7.10"
        kotlin("kapt") version "1.7.10"
        id("com.jfrog.bintray") version "1.8.5"
        id("com.github.johnrengelman.shadow") version "7.1.2"
    }
}
