import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.kapt)
    `maven-publish`
    signing
    jacoco
    alias(libs.plugins.jacocolog)
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

tasks.test {
    useJUnitPlatform()

    testLogging {
        showExceptions = true
        showCauses = true
        showStackTraces = true
        exceptionFormat = TestExceptionFormat.FULL
    }

    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
}

tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.jacocoTestReport)

    violationRules {
        rule {
            element = "BUNDLE"
            excludes = listOf("com.jacoco.dto.*")
            limit {
                counter = "INSTRUCTION"
                minimum = 0.90.toBigDecimal()
            }
            limit {
                counter = "BRANCH"
                minimum = 0.50.toBigDecimal()
            }
        }
    }
}

tasks.check {
    dependsOn(tasks.jacocoTestCoverageVerification)
}

// TODO: confirm javadoc packaging works as expected
val dokkaJavadoc by tasks.getting(DokkaTask::class) {
    outputDirectory.set(buildDir.resolve("dokka"))
}

val packageJavadoc by tasks.registering(Jar::class) {
    dependsOn("dokkaJavadoc")
    archiveClassifier.set("javadoc")
    from(dokkaJavadoc.outputDirectory)
}

tasks.register<Jar>("dokkaHtmlJar") {
    dependsOn(tasks.dokkaHtml)
    from(tasks.dokkaHtml.flatMap { it.outputDirectory })
    archiveClassifier.set("html-docs")
}

tasks.register<Jar>("dokkaJavadocJar") {
    dependsOn(tasks.dokkaJavadoc)
    from(tasks.dokkaJavadoc.flatMap { it.outputDirectory })
    archiveClassifier.set("javadoc")
}

val POM_ARTIFACT_ID: String by project
val POM_NAME: String by project
val POM_DESCRIPTION: String by project
val POM_URL: String by project

val POM_LICENCE_NAME: String by project
val POM_LICENCE_URL: String by project
val POM_LICENCE_DIST: String by project

val POM_SCM_URL: String by project
val POM_SCM_CONNECTION: String by project
val POM_SCM_DEV_CONNECTION: String by project

val POM_DEVELOPER_ID: String by project
val POM_DEVELOPER_NAME: String by project

publishing {
    publications {
        create<MavenPublication>("core") {
            from(components["java"])

            artifact(tasks.kotlinSourcesJar.get())
            artifact(packageJavadoc.get())

            groupId = group.toString()
            artifactId = POM_ARTIFACT_ID
            version = version.toString()

            pom {
                name.set(POM_NAME)
                description.set(POM_DESCRIPTION)
                url.set(POM_URL)

                licenses {
                    license {
                        name.set(POM_LICENCE_NAME)
                        url.set(POM_LICENCE_URL)
                        distribution.set(POM_LICENCE_DIST)
                    }
                }

                scm {
                    url.set(POM_SCM_URL)
                    connection.set(POM_SCM_CONNECTION)
                    developerConnection.set(POM_SCM_DEV_CONNECTION)
                }

                developers {
                    developer {
                        id.set(POM_DEVELOPER_ID)
                        name.set(POM_DEVELOPER_NAME)
                    }
                }
            }
        }
    }
}

signing {
    val signingKeyId: String? by project
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
    sign(publishing.publications["core"])
}
