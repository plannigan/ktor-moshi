import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.kapt)
    id("com.jfrog.bintray")  // plugin is already on class path, so can't use version specification
    jacoco
    `maven-publish`
    alias(libs.plugins.dokka)
}

dependencies {
    api(libs.ktor.serialization)
    api(libs.moshi)

    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
    testImplementation(libs.hamkrest)
    testImplementation(libs.ktor.server.testHost)
    testImplementation(libs.moshi.reflection)
    testImplementation(libs.ktor.server.core)
    testImplementation(libs.ktor.server.contentNegotiation)
    testImplementation(libs.ktor.client.mock)
    testImplementation(libs.ktor.client.contentNegotiation)
    testImplementation(libs.kotlin.reflect)
    "kaptTest"(libs.moshi.codeGen)
}

testWithJunit()
coverWithJacoco()

val dokkaJavadoc by tasks.getting(DokkaTask::class) {
    outputDirectory.set(buildDir.resolve("dokka"))
}

val packageJavadoc by tasks.registering(Jar::class) {
    dependsOn("dokkaJavadoc")
    archiveClassifier.set("javadoc")
    from(dokkaJavadoc.outputDirectory)
}

val POM_ARTIFACT_ID: String by project

publishAs(POM_ARTIFACT_ID, "core", tasks.kotlinSourcesJar, packageJavadoc)
