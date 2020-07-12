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
