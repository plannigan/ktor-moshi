import org.gradle.api.tasks.testing.logging.TestExceptionFormat
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
    implementation(kotlin("stdlib-jdk8"))
    implementation(Deps.Ktor.server)
    implementation(Deps.Moshi.reflection)
    implementation(Deps.okio)

    testImplementation(Deps.Junit.api)
    testRuntimeOnly(Deps.Junit.engine)
    testImplementation(Deps.truth)
    testImplementation(Deps.Ktor.testHost)
    "kaptTest"(Deps.Moshi.codeGen)
}

tasks.withType<Test> {
    useJUnitPlatform()

    testLogging {
        showExceptions = true
        showCauses = true
        showStackTraces = true
        exceptionFormat = TestExceptionFormat.FULL
    }

    reports.html.isEnabled = true
    reports.junitXml.isEnabled = true
}

val check by tasks
val jacocoTestReport by tasks

tasks.withType<JacocoCoverageVerification> {
    check.dependsOn(this)
    dependsOn(jacocoTestReport)

    violationRules {
        rule {
            element = "BUNDLE"
            excludes = listOf("com.jacoco.dto.*")
            limit("INSTRUCTION", 0.90)
            limit("BRANCH", 0.5)
        }
    }
}

tasks.withType<JacocoReport> {
    reports {
        html.isEnabled = true
        xml.isEnabled = true
        csv.isEnabled = false
    }
}

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
val POM_DESCRIPTION: String by project
val POM_URL: String by project

val POM_LICENCE_NAME_SHORT: String by project
val POM_LICENCE_NAME: String by project
val POM_LICENCE_URL: String by project
val POM_LICENCE_DIST: String by project

val POM_SCM_URL: String by project
val POM_SCM_CONNECTION: String by project
val POM_SCM_DEV_CONNECTION: String by project

val POM_DEVELOPER_ID: String by project
val POM_DEVELOPER_NAME: String by project

bintray {
    user = System.getenv("BINTRAY_USER") ?: project.properties["bintray.user"]?.toString()
    key = System.getenv("BINTRAY_KEY")
    publish = true

    setPublications("lib")

    with(pkg) {
        repo = project.group.toString()
        name = POM_ARTIFACT_ID
        setLicenses(POM_LICENCE_NAME_SHORT)
        with(version) {
            name = project.version.toString()
        }
    }
}

publishing {
    publications {
        register<MavenPublication>("lib") {
            from(components["java"])

            artifact(tasks.kotlinSourcesJar.get())
            artifact(packageJavadoc.get())

            groupId = project.group.toString()
            artifactId = POM_ARTIFACT_ID
            version = project.version.toString()

            pom {
                name.set(POM_ARTIFACT_ID)
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
                    connection.set(POM_SCM_CONNECTION)
                    developerConnection.set(POM_SCM_DEV_CONNECTION)
                    url.set(POM_SCM_URL)
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

fun JacocoViolationRule.limit(type: String, minValue: Double) {
    this.limit {
        counter = type
        minimum = minValue.toBigDecimal()
    }
}
