import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    kotlin("jvm")
    kotlin("kapt")
    jacoco
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(Deps.Ktor.clientCore)
    implementation(Deps.Ktor.clientJson)
    implementation(Deps.Ktor.clientJsonJvm)
    implementation(Deps.Moshi.moshi)
    implementation(Deps.okio)

    testImplementation(Deps.Junit.api)
    testRuntimeOnly(Deps.Junit.engine)
    testImplementation(Deps.hamkrest)
    testImplementation(Deps.Ktor.clientMock)
    testImplementation(Deps.Ktor.clientMockJvm)
    testImplementation(Deps.Moshi.reflection)
    testImplementation(kotlin("reflect"))
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

fun JacocoViolationRule.limit(type: String, minValue: Double) {
    this.limit {
        counter = type
        minimum = minValue.toBigDecimal()
    }
}
