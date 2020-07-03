import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.gradle.testing.jacoco.tasks.rules.JacocoViolationRule

plugins {
    kotlin("jvm")
    kotlin("kapt")
    jacoco
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(Deps.Ktor.server)
    implementation(Deps.Moshi.reflection)
    implementation(Deps.okio)

    testImplementation(Deps.junit)
    testImplementation(Deps.truth)
    testImplementation(Deps.Ktor.testHost)
    "kaptTest"(Deps.Moshi.codeGen)
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
            limit("INSTRUCTION", 0.65)
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

fun JacocoViolationRule.limit(type: String, minValue: Double) {
    this.limit {
        counter = type
        minimum = minValue.toBigDecimal()
    }
}
