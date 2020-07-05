package com.hypercubetools.ktor.moshi

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.squareup.moshi.JsonClass
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.*
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.*

const val SOME_PATH = "/"
const val SOME_ID = 42
const val SOME_NAME = "Foosius"
val SOME_FOO = Foo(id = SOME_ID, name = SOME_NAME)
const val SOME_FOO_JSON = """{"id":$SOME_ID,"name":"$SOME_NAME"}"""
const val SOME_KEY = "bar-123"
const val SOME_COUNT = 50
val SOME_BAR = Bar(key = SOME_KEY, count = SOME_COUNT)
const val SOME_BAR_JSON = """{"key":"$SOME_KEY","count":$SOME_COUNT}"""

class MoshiConverterTest {
  @Nested
  inner class Reflection {
    @Test
    fun getRequest_acceptJson_responseObjectSerializedToJson() {
      withTestApplication({
        reflectionConfig()
      }) {
        handleRequest(HttpMethod.Get, SOME_PATH) {
          accept(ContentType.Application.Json)
        }
      }.apply {
        assertAll(
                { assertThat(response.status(), equalTo(HttpStatusCode.OK)) },
                { assertThat(response.contentType(), utf8Content(ContentType.Application.Json)) },
                { assertThat(response.content, equalTo(SOME_FOO_JSON)) }
        )
      }
    }

    @Test
    fun getRequest_acceptNotJson_notAcceptable() {
      withTestApplication({
        reflectionConfig()
      }) {
        handleRequest(HttpMethod.Get, SOME_PATH) {
          accept(ContentType.Text.Plain)
        }
      }.apply {
        assertThat(response.status(), equalTo(HttpStatusCode.NotAcceptable))
      }
    }

    @Test
    fun postRequest_contentJson_bodyDeserializesToValue() {
      val capturedBody = Box<Foo>()
      withTestApplication({
        reflectionConfig(capturedBody)
      }) {
        handleRequest(HttpMethod.Post, SOME_PATH) {
          contentType(ContentType.Application.Json)
          setBody(SOME_FOO_JSON)
        }
      }.apply {
        assertAll(
                { assertThat(response.status(), equalTo(HttpStatusCode.OK)) },
                { assertThat(capturedBody.value, equalTo(SOME_FOO)) }
        )
      }
    }

    @Test
    fun postRequest_contentNotJson_unsupported() {
      val capturedBody = Box<Foo>()
      withTestApplication({
        reflectionConfig(capturedBody)
      }) {
        handleRequest(HttpMethod.Post, SOME_PATH) {
          contentType(ContentType.Text.Plain)
          setBody(SOME_FOO_JSON)
        }
      }.apply {
        assertThat(response.status(), equalTo(HttpStatusCode.UnsupportedMediaType))
      }
    }

    private fun Application.reflectionConfig(capturedBody: Box<Foo>? = null) {
      install(ContentNegotiation) {
        moshi {
          add(KotlinJsonAdapterFactory())
        }
      }
      routing(capturedBody)
    }
  }

  @Nested
  inner class Codegen {
    @Test
    fun getRequest_acceptJson_responseObjectSerializedToJson() {
      withTestApplication({
        codegenConfig()
      }) {
        handleRequest(HttpMethod.Get, SOME_PATH) {
          accept(ContentType.Application.Json)
        }
      }.apply {
        assertAll(
                { assertThat(response.status(), equalTo(HttpStatusCode.OK)) },
                { assertThat(response.contentType(), utf8Content(ContentType.Application.Json)) },
                { assertThat(response.content, equalTo(SOME_BAR_JSON)) }
        )
      }
    }

    @Test
    fun getRequest_acceptNotJson_notAcceptable() {
      withTestApplication({
        codegenConfig()
      }) {
        handleRequest(HttpMethod.Get, SOME_PATH) {
          accept(ContentType.Text.Plain)
        }
      }.apply {
        assertThat(response.status(), equalTo(HttpStatusCode.NotAcceptable))
      }
    }

    @Test
    fun postRequest_contentJson_bodyDeserializesToValue() {
      val capturedBody = Box<Bar>()
      withTestApplication({
        codegenConfig(capturedBody)
      }) {
        handleRequest(HttpMethod.Post, SOME_PATH) {
          contentType(ContentType.Application.Json)
          setBody(SOME_BAR_JSON)
        }
      }.apply {
        assertAll(
                { assertThat(response.status(), equalTo(HttpStatusCode.OK)) },
                { assertThat(capturedBody.value, equalTo(SOME_BAR)) }
        )
      }
    }

    @Test
    fun postRequest_contentNotJson_unsupported() {
      val capturedBody = Box<Bar>()
      withTestApplication({
        codegenConfig(capturedBody)
      }) {
        handleRequest(HttpMethod.Post, SOME_PATH) {
          contentType(ContentType.Text.Plain)
          setBody(SOME_BAR_JSON)
        }
      }.apply {
        assertThat(response.status(), equalTo(HttpStatusCode.UnsupportedMediaType))
      }
    }

    private fun Application.codegenConfig(capturedBody: Box<Bar>? = null) {
      install(ContentNegotiation) {
        moshi()
      }
      routing(capturedBody)
    }
  }
}

private inline fun <reified T : Any> Application.routing(capturedBody: Box<T>? = null) {
  routing {
    get(SOME_PATH) {
      call.respond(SOME_FOO)
    }
    post(SOME_PATH) {
      val request = call.receive<T>()
      if (capturedBody != null) {
        capturedBody.value = request
      }
      call.respond("")
    }
  }
}

fun utf8Content(contentType: ContentType) = equalTo(contentType.withCharset(Charsets.UTF_8))

fun TestApplicationRequest.accept(contentType: ContentType) = addHeader("Accept", contentType)

fun TestApplicationRequest.contentType(contentType: ContentType) = addHeader("Content-Type", contentType)

private fun TestApplicationRequest.addHeader(name: String, contentType: ContentType)
        = addHeader(name, contentType.toString())

data class Foo(val id: Int, val name: String)

@JsonClass(generateAdapter = true)
data class Bar(val key: String, val count: Int)

data class Box<T>(var value: T? = null)
