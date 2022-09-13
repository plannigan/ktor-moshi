import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("com.jfrog.bintray")
    jacoco
    `maven-publish`
    id("org.jetbrains.dokka") version "1.7.10"
}

dependencies {
    api(Deps.Ktor.serialization)
    api(Deps.Moshi.moshi)

    testImplementation(Deps.Junit.api)
    testRuntimeOnly(Deps.Junit.engine)
    testImplementation(Deps.hamkrest)
    testImplementation(Deps.Ktor.testHost)
    testImplementation(Deps.Moshi.reflection)
    testImplementation(Deps.Ktor.server)
    testImplementation(Deps.Ktor.serverContentNegotiation)
    testImplementation(Deps.Ktor.clientMock)
    testImplementation(Deps.Ktor.clientContentNegotiation)
    testImplementation(kotlin("reflect"))
    "kaptTest"(Deps.Moshi.codeGen)
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
