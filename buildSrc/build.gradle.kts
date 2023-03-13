plugins {
    `kotlin-dsl`
    `maven-publish`
}

dependencies {
    implementation(libs.bintray)
}

repositories {
    jcenter()
}
