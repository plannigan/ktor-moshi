plugins {
    `kotlin-dsl`
    `maven-publish`
}

dependencies {
    implementation("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.5")
}

repositories {
    jcenter()
}
