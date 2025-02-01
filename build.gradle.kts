import ru.vyarus.gradle.plugin.python.PythonExtension.Scope
import ru.vyarus.gradle.plugin.python.task.PythonTask

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ksp)
    alias(libs.plugins.nexus)
    alias(libs.plugins.use.python)
}

allprojects {
    group = "com.hypercubetools"
    version = "3.0.0"

    repositories {
        mavenCentral()
    }
}

nexusPublishing {
    repositories {
        sonatype()
    }
}

python {
    scope = Scope.VIRTUALENV
    pythonBinary = "python3.9"
    minPythonVersion = "3.9"
}

tasks.register<PythonTask>("hyperBumpIt") {
    description = "Bump the version number"
    module = "hyper_bump_it"
    command = project.properties.get("args") ?: ""
}
