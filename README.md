[![Kotlin](https://img.shields.io/badge/kotlin-1.3.72-blue.svg)](http://kotlinlang.org)
[![CircleCI](https://circleci.com/gh/plannigan/ktor-moshi.svg?style=svg)](https://circleci.com/gh/plannigan/ktor-moshi)
[![Maven Central](https://img.shields.io/maven-central/v/com.hypercubetools/ktor-moshi-server)][maven]
[![codecov](https://codecov.io/gh/plannigan/ktor-moshi/branch/main/graph/badge.svg)](https://codecov.io/gh/plannigan/ktor-moshi)

# Ktor-Moshi

[Ktor][ktor] is a framework for building asynchronous servers and clients in connected systems. Ktor allows the
application to decide how data should be serialized/deserialized when sent over the network.

Ktor-Moshi allows an application to use [Moshi][moshi] when dealing with JSON content.

## Usage

### Server

When implementing a server, the [ContentNegotiation][content_negotiation] feature will convert the content. Bellow is
an example of installing Moshi for content negotiation:

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

Refer to the [ContentNegotiation][content_negotiation] documentation for information on how to send and receive
formatted data.

### Client

When implementing a client, the [Json feature][json_feature] will convert the content. Bellow is
an example of installing Moshi for serializing JSON content:

```kotlin
val client = HttpClient(HttpClientEngine) {
    install(JsonFeature) {
        serializer = MoshiSerializer {
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
    install(JsonFeature) {
        serializer = MoshiSerializer(myExistingMoshiInstance)
    }
}
```

Refer to the [Client][client_calls] documentation for information on how to send and receive formatted data.

## Download

Each component (server & client) are published as independent packages.

Add a gradle dependency to your project:

* Server:

```groovy
implementation 'com.hypercubetools:ktor-moshi-server:LATEST_VERSION'
```

* Client:

```groovy
implementation 'com.hypercubetools:ktor-moshi-client:LATEST_VERSION'
```

## Fork

[Ryan Harter's `ktor-moshi`][old_repo] is the original source for this project. The project has been expanded since it's
initial state.

[maven]: https://mvnrepository.com/artifact/com.hypercubetools/ktor-moshi-server
[ktor]: https://ktor.io/
[moshi]: https://github.com/square/moshi/
[content_negotiation]: http://ktor.io/servers/features/content-negotiation.html
[moshi_builder]: http://square.github.io/moshi/1.x/moshi/com/squareup/moshi/Moshi.Builder.html
[date_adapter]: https://github.com/square/moshi/tree/master/adapters#adapters
[json_feature]: https://ktor.io/clients/http-client/features/json-feature.html
[client_calls]: https://ktor.io/clients/index.html#calls-requests-and-responses
[old_repo]: https://github.com/rharter/ktor-moshi
