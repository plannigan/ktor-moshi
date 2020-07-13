import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar as JvmJar
import org.gradle.api.tasks.bundling.Jar as BundledJar
import org.gradle.kotlin.dsl.*
import com.jfrog.bintray.gradle.BintrayExtension

fun Project.publishAs(distributionName: String,
                      libraryName: String,
                      kotlinSource: TaskProvider<JvmJar>,
                      packageJavadoc: TaskProvider<BundledJar>) {
    val POM_DESCRIPTION: String by project
    val POM_URL: String by project

    val POM_LICENCE_NAME: String by project
    val POM_LICENCE_NAME_SHORT: String by project
    val POM_LICENCE_URL: String by project
    val POM_LICENCE_DIST: String by project

    val POM_SCM_URL: String by project
    val POM_SCM_CONNECTION: String by project
    val POM_SCM_DEV_CONNECTION: String by project

    val POM_DEVELOPER_ID: String by project
    val POM_DEVELOPER_NAME: String by project

    configure<PublishingExtension> {
        publications {
            register<MavenPublication>(libraryName) {
                from(components["java"])

                artifact(kotlinSource.get())
                artifact(packageJavadoc.get())

                groupId = project.group.toString()
                artifactId = distributionName
                version = project.version.toString()

                pom {
                    name.set(distributionName)
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

    configure<BintrayExtension> {
            user = System.getenv("BINTRAY_USER") ?: project.properties["bintray.user"]?.toString()
            key = System.getenv("BINTRAY_KEY")
            publish = true

            setPublications(libraryName)

            with(pkg) {
                repo = project.group.toString()
                name = distributionName
                setLicenses(POM_LICENCE_NAME_SHORT)
                with(version) {
                    name = project.version.toString()
                }
            }
    }
}