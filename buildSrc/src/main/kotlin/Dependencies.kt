// Version information for dependencies

object Versions {
    const val moshi = "1.14.0"
    const val ktor = "2.1.1"
    const val hamkrest = "1.7.0.3"
    const val junit = "5.6.2"
    const val logback = "1.2.3"
    const val clikt = "2.8.0"
}
object Groups {
    const val moshi = "com.squareup.moshi"
    const val okio = "com.squareup.okio"
    const val ktor = "io.ktor"
    const val logback = "ch.qos.logback"
    const val hamkrest = "com.natpryce"
    const val junit = "org.junit.jupiter"
    const val clikt = "com.github.ajalt"
}

object Deps {
    object Moshi {
        const val moshi = "${Groups.moshi}:moshi:${Versions.moshi}"
        const val codeGen = "${Groups.moshi}:moshi-kotlin-codegen:${Versions.moshi}"
        const val adapters = "${Groups.moshi}:moshi-adapters:${Versions.moshi}"
        const val reflection = "${Groups.moshi}:moshi-kotlin:${Versions.moshi}"
    }
    object Ktor {
        const val serialization = "${Groups.ktor}:ktor-serialization:${Versions.ktor}"
        const val server = "${Groups.ktor}:ktor-server-core:${Versions.ktor}"
        const val serverCallLogging = "${Groups.ktor}:ktor-server-call-logging:${Versions.ktor}"
        const val serverContentNegotiation = "${Groups.ktor}:ktor-server-content-negotiation:${Versions.ktor}"
        const val clientCore = "${Groups.ktor}:ktor-client-core:${Versions.ktor}"
        const val clientCIO = "${Groups.ktor}:ktor-client-cio:${Versions.ktor}"
        const val clientContentNegotiation = "${Groups.ktor}:ktor-client-content-negotiation:${Versions.ktor}"
        const val netty = "${Groups.ktor}:ktor-server-netty:${Versions.ktor}"
        const val testHost = "${Groups.ktor}:ktor-server-test-host:${Versions.ktor}"
        const val clientMock = "${Groups.ktor}:ktor-client-mock:${Versions.ktor}"
    }
    const val logback = "${Groups.logback}:logback-classic:${Versions.logback}"
    const val hamkrest = "${Groups.hamkrest}:hamkrest:${Versions.hamkrest}"
    object Junit {
        const val api = "${Groups.junit}:junit-jupiter-api:${Versions.junit}"
        const val engine = "${Groups.junit}:junit-jupiter-engine:${Versions.junit}"
    }
    const val clikt = "${Groups.clikt}:clikt:${Versions.clikt}"
}
