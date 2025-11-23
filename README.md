[![Kotlin](https://img.shields.io/badge/kotlin-2.2.21-blue.svg)](http://kotlinlang.org)
[![CI](https://github.com/plannigan/ktor-moshi/actions/workflows/main.yaml/badge.svg?branch=main)](https://github.com/plannigan/ktor-moshi/actions/workflows/main.yaml)
[![Maven Central](https://img.shields.io/maven-central/v/com.hypercubetools/ktor-moshi)][maven]
[![codecov](https://codecov.io/gh/plannigan/ktor-moshi/branch/main/graph/badge.svg)](https://codecov.io/gh/plannigan/ktor-moshi)

# Ktor-Moshi

[Ktor][ktor] is a framework for building asynchronous servers and clients in connected systems. Ktor allows the
application to decide how data should be serialized/deserialized when sent over the network.

Ktor-Moshi allows an application to use [Moshi][moshi] when dealing with JSON content.

## Usage

### Server

When implementing a server, the [ContentNegotiation][server_content_negotiation] plugin will convert the content. Below
is an example of installing Moshi for content negotiation:

```kotlin
install(ContentNegotiation) {
    moshi {
        // Configure the Moshi.Builder here.
        add(Date::class.java, Rfc3339DateJsonAdapter())
    }
}
```

A [Moshi.Builder][moshi_builder] is available inside the `moshi` block if it needs to be customized. In this example,
Moshi's pre-build [RFC-3339 Date adapter][date_adapter] is added.

Alternatively, if the application already has a `Moshi` instance, it can be provided instead of creating a new one.

```kotlin
install(ContentNegotiation) {
    moshi(myExistingMoshiInstance)
}
```

Refer to the [ContentNegotiation][server_content_negotiation] documentation for information on how to send and receive
formatted data.

### Client

When implementing a client, the [ContentNegotiation][client_content_negotiation] plugin will convert the content. Below
is an example of installing Moshi for serializing JSON content:

```kotlin
val client = HttpClient(HttpClientEngine) {
    install(ContentNegotiation) {
        moshi {
            add(Rfc3339DateJsonAdapter())
        }
    }
}
```

A [Moshi.Builder][moshi_builder] is available inside the `MoshiSerializer` block if it needs to be customized. In this
example, Moshi's pre-build [RFC-3339 Date adapter][date_adapter] is added.

Alternatively, if the application already has a `Moshi` instance, it can be provided instead of creating a new one.

```kotlin
val client = HttpClient(HttpClientEngine) {
    install(ContentNegotiation) {
        moshi(myExistingMoshiInstance)
    }
}
```

Refer to the [ContentNegotiation][client_content_negotiation] documentation for information on how to send and receive
formatted data.

## Download

Add a Gradle dependency to your project:

Using the Kotlin DSL:

```kotlin
implementation("com.hypercubetools:ktor-moshi:LATEST_VERSION")
```

Using the Groovy DSL:

```groovy
implementation 'com.hypercubetools:ktor-moshi:LATEST_VERSION'
```

## Fork

[Ryan Harter's `ktor-moshi`][old_repo] is the original source for this project. The project has been expanded since it's
initial state.

[maven]: https://central.sonatype.com/artifact/com.hypercubetools/ktor-moshi/
[ktor]: https://ktor.io/
[moshi]: https://github.com/square/moshi/
[server_content_negotiation]: https://ktor.io/docs/serialization.html
[client_content_negotiation]: https://ktor.io/docs/serialization-client.html
[moshi_builder]: https://square.github.io/moshi/1.x/moshi/com/squareup/moshi/Moshi.Builder.html
[date_adapter]: https://github.com/square/moshi/tree/master/adapters#adapters
[old_repo]: https://github.com/rharter/ktor-moshi
