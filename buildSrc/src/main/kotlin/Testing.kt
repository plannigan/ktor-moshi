import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.kotlin.dsl.*
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.gradle.testing.jacoco.tasks.rules.JacocoViolationRule

fun Project.testWithJunit() {
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
}

fun Project.coverWithJacoco() {
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
}

fun JacocoViolationRule.limit(type: String, minValue: Double) {
    this.limit {
        counter = type
        minimum = minValue.toBigDecimal()
    }
}
