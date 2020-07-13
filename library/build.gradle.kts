import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("com.jfrog.bintray")
    jacoco
    `maven-publish`
    id("org.jetbrains.dokka") version "0.10.1"
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(Deps.Ktor.server)
    implementation(Deps.Moshi.moshi)
    implementation(Deps.okio)

    testImplementation(Deps.Junit.api)
    testRuntimeOnly(Deps.Junit.engine)
    testImplementation(Deps.hamkrest)
    testImplementation(Deps.Ktor.testHost)
    testImplementation(Deps.Moshi.reflection)
    testImplementation(kotlin("reflect"))
    "kaptTest"(Deps.Moshi.codeGen)
}

testWithJunit()
coverWithJacoco()

val dokka by tasks.getting(DokkaTask::class) {
    outputFormat = "html"
    outputDirectory = "$buildDir/dokka"
}

val packageJavadoc by tasks.registering(Jar::class) {
    dependsOn("dokka")
    archiveClassifier.set("javadoc")
    from(dokka.outputDirectory)
}

val POM_ARTIFACT_ID: String by project

publishAs(POM_ARTIFACT_ID, "server", tasks.kotlinSourcesJar, packageJavadoc)
