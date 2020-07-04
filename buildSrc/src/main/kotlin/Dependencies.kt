// Version information for dependencies

object Versions {
    const val moshi = "1.9.3"
    const val okio = "2.6.0"
    const val ktor = "1.3.2"
    const val truth = "1.0.1"
    const val junit = "5.6.2"
    const val logback = "1.2.3"
}
object Groups {
    const val moshi = "com.squareup.moshi"
    const val okio = "com.squareup.okio"
    const val ktor = "io.ktor"
    const val logback = "ch.qos.logback"
    const val truth = "com.google.truth"
    const val junit = "org.junit.jupiter"
}

object Deps {
    object Moshi {
        const val moshi = "${Groups.moshi}:moshi:${Versions.moshi}"
        const val codeGen = "${Groups.moshi}:moshi-kotlin-codegen:${Versions.moshi}"
        const val adapters = "${Groups.moshi}:moshi-adapters:${Versions.moshi}"
        const val reflection = "${Groups.moshi}:moshi-kotlin:${Versions.moshi}"
    }
    object Ktor {
        const val server = "${Groups.ktor}:ktor-server-core:${Versions.ktor}"
        const val netty = "${Groups.ktor}:ktor-server-netty:${Versions.ktor}"
        const val testHost = "${Groups.ktor}:ktor-server-test-host:${Versions.ktor}"
    }
    const val okio = "${Groups.okio}:okio:${Versions.okio}"
    const val logback = "${Groups.logback}:logback-classic:${Versions.logback}"
    const val truth = "${Groups.truth}:truth:${Versions.truth}"
    object Junit {
        const val api = "${Groups.junit}:junit-jupiter-api:${Versions.junit}"
        const val engine = "${Groups.junit}:junit-jupiter-engine:${Versions.junit}"
    }
}
