import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("kapt")
    application
}

application {
    mainClassName = "MainKt"
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

dependencies {
    implementation(project(":library"))
    implementation(kotlin("stdlib-jdk8"))
    implementation(Deps.Ktor.netty)
    implementation(Deps.logback)
    implementation(Deps.Moshi.adapters)
    implementation(Deps.Moshi.moshi)
    implementation(Deps.Moshi.reflection)
    "kapt"(Deps.Moshi.codeGen)

    testImplementation(Deps.junit)
    testImplementation(Deps.truth)
}