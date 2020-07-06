package com.hypercubetools.ktor.moshi

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.throws
import com.squareup.moshi.JsonClass
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.ktor.client.HttpClient
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.client.engine.mock.respond
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.HttpResponseData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.*
import io.ktor.http.content.TextContent
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*

const val SOME_PATH = "test-path"
const val SOME_URL = "http://localhost/$SOME_PATH"
const val SOME_ID = 42
const val SOME_NAME = "Foosius"
val SOME_FOO = Foo(id = SOME_ID, name = SOME_NAME)
const val SOME_FOO_JSON = """{"id":$SOME_ID,"name":"$SOME_NAME"}"""
const val SOME_KEY = "bar-123"
const val SOME_COUNT = 50
val SOME_BAR = Bar(key = SOME_KEY, count = SOME_COUNT)
const val SOME_BAR_JSON = """{"key":"$SOME_KEY","count":$SOME_COUNT}"""
val SOME_NOT_JSON_TYPE = ContentType.Text.Plain

class MoshiSerializerTest {
  @Nested
  inner class Reflection {
    @Test
    fun getRequest_respondJson_responseDeserializes() {
      val client = withTestClient({
        serializer = MoshiSerializer {
          add(KotlinJsonAdapterFactory())
        }
      }, SOME_PATH) {
        respond(SOME_FOO_JSON, ContentType.Application.Json)
      }

      val result = client.blockingGet<Foo>(SOME_URL)

      assertThat(result, equalTo(SOME_FOO))
    }

    @Test
    fun getRequest_respondNotJson_noTransformError() {
      val client = withTestClient({
        serializer = MoshiSerializer {
          add(KotlinJsonAdapterFactory())
        }
      }, SOME_PATH) {
        respond(SOME_FOO_JSON, SOME_NOT_JSON_TYPE)
      }

      assertThat({ client.blockingGet<Foo>(SOME_URL) }, throws<NoTransformationFoundException>())
    }

    @Test
    fun postRequest_sendJson_serializesBody() {
      var postBody : String? = null
      val client = withTestClient({
        serializer = MoshiSerializer {
          add(KotlinJsonAdapterFactory())
        }
      }, SOME_PATH) { request ->
        postBody = (request.body as? TextContent)?.text
        respond(SOME_FOO_JSON, ContentType.Application.Json)
      }

      runBlocking {
        client.post<Foo>(SOME_URL) {
          contentType(ContentType.Application.Json)
          body = SOME_FOO
        }
      }

      assertThat(postBody, equalTo(SOME_FOO_JSON))
    }

    @Test
    fun getRequest_respondNull_IllegalStateError() {
      val client = withTestClient({
        serializer = MoshiSerializer {
          add(KotlinJsonAdapterFactory())
        }
      }, SOME_PATH) {
        respond("null", ContentType.Application.Json)
      }

      assertThat({ client.blockingGet<Foo>(SOME_URL) }, throws<IllegalStateException>())
    }
  }

  @Nested
  inner class Codegen {
    @Test
    fun getRequest_respondJson_responseDeserializes() {
      val client = withTestClient({
        serializer = MoshiSerializer()
      }, SOME_PATH) {
        respond(SOME_BAR_JSON, ContentType.Application.Json)
      }

      val result = client.blockingGet<Bar>(SOME_URL)

      assertThat(result, equalTo(SOME_BAR))
    }

    @Test
    fun getRequest_respondNotJson_noTransformError() {
      val client = withTestClient({
        serializer = MoshiSerializer()
      }, SOME_PATH) {
        respond(SOME_BAR_JSON, SOME_NOT_JSON_TYPE)
      }

      assertThat({ client.blockingGet<Bar>(SOME_URL) }, throws<NoTransformationFoundException>())
    }

    @Test
    fun postRequest_sendJson_serializesBody() {
      var postBody : String? = null
      val client = withTestClient({
        serializer = MoshiSerializer()
      }, SOME_PATH) { request ->
        postBody = (request.body as? TextContent)?.text
        respond(SOME_BAR_JSON, ContentType.Application.Json)
      }

      runBlocking {
        client.post<Bar>(SOME_URL) {
          contentType(ContentType.Application.Json)
          body = SOME_BAR
        }
      }

      assertThat(postBody, equalTo(SOME_BAR_JSON))
    }

    @Test
    fun getRequest_respondNull_IllegalStateError() {
      val client = withTestClient({
        serializer = MoshiSerializer()
      }, SOME_PATH) {
        respond("null", ContentType.Application.Json)
      }

      assertThat({ client.blockingGet<Bar>(SOME_URL) }, throws<IllegalStateException>())
    }
  }
}

fun withTestClient(
        configure: JsonFeature.Config.() -> Unit,
        path: String,
        handler: MockRequestHandler
): HttpClient {
  return HttpClient(MockEngine) {
    install(JsonFeature) {
      configure()
    }
    engine {
      addHandler { request ->
        if (request.url.fullPath.contains(path)) {
          return@addHandler handler(request)
        }
        error("No matching handler for ${request.url}")
      }
    }
  }
}

fun MockRequestHandleScope.respond(jsonText: String, contentType: ContentType): HttpResponseData {
    return respond(jsonText, headers = headersOf("Content-Type" to listOf(contentType.toString())))
}

inline fun <reified T> HttpClient.blockingGet(
    urlString: String,
    crossinline block: HttpRequestBuilder.() -> Unit = {}
): T = runBlocking {
    get<T>(urlString, block)
}

data class Foo(val id: Int, val name: String)

@JsonClass(generateAdapter = true)
data class Bar(val key: String, val count: Int)
