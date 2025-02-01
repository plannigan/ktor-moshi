package com.hypercubetools.ktor.moshi

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.throws
import com.squareup.moshi.JsonClass
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.ktor.client.HttpClient
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.HttpResponseData
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.http.contentType
import io.ktor.http.fullPath
import io.ktor.http.headersOf
import io.ktor.http.withCharset
import io.ktor.serialization.ContentConvertException
import io.ktor.serialization.JsonConvertException
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.client.plugins.contentnegotiation.ContentNegotiationConfig
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation

const val SOME_PATH = "/test-path"
const val SOME_URL = "http://localhost$SOME_PATH"
const val SOME_ID = 42
const val SOME_NAME = "Foosius"
val SOME_FOO = Foo(id = SOME_ID, name = SOME_NAME)
const val SOME_FOO_JSON = """{"id":$SOME_ID,"name":"$SOME_NAME"}"""
const val SOME_KEY = "bar-123"
const val SOME_COUNT = 50
val SOME_BAR = Bar(key = SOME_KEY, count = SOME_COUNT)
const val SOME_BAR_JSON = """{"key":"$SOME_KEY","count":$SOME_COUNT}"""
val SOME_NOT_JSON_TYPE = ContentType.Text.Plain

class MoshiConverterTest {
    @Nested
    inner class Server {
        @Nested
        inner class Reflection {
            @Test
            fun getRequest_acceptJson_responseObjectSerializedToJson() = testApplication {
                application { reflectionConfig() }
                val response = client.get(SOME_PATH) {
                    accept(ContentType.Application.Json)
                }
                assertAll(
                        { assertThat(response.status, equalTo(HttpStatusCode.OK)) },
                        { assertThat(response.contentType(), utf8Content(ContentType.Application.Json)) },
                        { assertThat(runBlocking { response.bodyAsText() }, equalTo(SOME_FOO_JSON)) }
                )
            }

            @Test
            fun getRequest_acceptNotJson_notAcceptable() = testApplication {
                application { reflectionConfig() }
                val response = client.get(SOME_PATH) {
                    accept(ContentType.Text.Plain)
                }
                assertThat(response.status, equalTo(HttpStatusCode.NotAcceptable))
            }

            @Test
            fun postRequest_contentJson_bodyDeserializesToValue() = testApplication {
                val capturedBody = Box<Foo>()
                application { reflectionConfig(capturedBody) }
                val response = client.post(SOME_PATH) {
                    contentType(ContentType.Application.Json)
                    setBody(SOME_FOO_JSON)
                }
                assertAll(
                        { assertThat(response.status, equalTo(HttpStatusCode.OK)) },
                        { assertThat(capturedBody.value, equalTo(SOME_FOO)) }
                )
            }

            @Test
            fun postRequest_contentNotJson_unsupported() = testApplication {
                val capturedBody = Box<Foo>()
                application { reflectionConfig(capturedBody) }
                val response = client.post(SOME_PATH) {
                    contentType(ContentType.Text.Plain)
                    setBody(SOME_FOO_JSON)
                }
                assertThat(response.status, equalTo(HttpStatusCode.UnsupportedMediaType))
            }

            private fun Application.reflectionConfig(capturedBody: Box<Foo>? = null) {
                install(ServerContentNegotiation) {
                    moshi {
                        add(KotlinJsonAdapterFactory())
                    }
                }
                routing(SOME_FOO, capturedBody)
            }
        }

        @Nested
        inner class Codegen {
            @Test
            fun getRequest_acceptJson_responseObjectSerializedToJson() = testApplication {
                application { codegenConfig() }
                val response = client.get(SOME_PATH) {
                    accept(ContentType.Application.Json)
                }
                assertAll(
                        { assertThat(response.status, equalTo(HttpStatusCode.OK)) },
                        { assertThat(response.contentType(), utf8Content(ContentType.Application.Json)) },
                        { assertThat(runBlocking { response.bodyAsText() }, equalTo(SOME_BAR_JSON)) }
                )
            }

            @Test
            fun getRequest_acceptNotJson_notAcceptable() = testApplication {
                application { codegenConfig() }
                val response = client.get(SOME_PATH) {
                    accept(ContentType.Text.Plain)
                }
                assertThat(response.status, equalTo(HttpStatusCode.NotAcceptable))
            }

            @Test
            fun postRequest_contentJson_bodyDeserializesToValue() = testApplication {
                val capturedBody = Box<Bar>()
                application { codegenConfig(capturedBody) }
                val response = client.post(SOME_PATH) {
                    contentType(ContentType.Application.Json)
                    setBody(SOME_BAR_JSON)
                }
                assertAll(
                        { assertThat(response.status, equalTo(HttpStatusCode.OK)) },
                        { assertThat(capturedBody.value, equalTo(SOME_BAR)) }
                )
            }

            @Test
            fun postRequest_contentNotJson_unsupported() = testApplication {
                val capturedBody = Box<Bar>()
                application { codegenConfig(capturedBody) }
                val response = client.post(SOME_PATH) {
                    contentType(ContentType.Text.Plain)
                    setBody(SOME_BAR_JSON)
                }
                assertThat(response.status, equalTo(HttpStatusCode.UnsupportedMediaType))
            }

            private fun Application.codegenConfig(capturedBody: Box<Bar>? = null) {
                install(ServerContentNegotiation) {
                    moshi()
                }
                routing(SOME_BAR, capturedBody)
            }
        }

        @Nested
        inner class RegisterNonJson {
            private val registeredNonJson = ContentType.Text.Plain
            private val otherNonJson = ContentType.Text.Xml

            @Test
            fun getRequest_acceptRegistered_responseObjectSerializedToJson() = testApplication {
                application { registerConfig() }
                val response = client.get(SOME_PATH) {
                    accept(registeredNonJson)
                }
                assertAll(
                        { assertThat(response.status, equalTo(HttpStatusCode.OK)) },
                        { assertThat(response.contentType(), utf8Content(registeredNonJson)) },
                        { assertThat(runBlocking { response.bodyAsText() }, equalTo(SOME_BAR_JSON)) }
                )
            }

            @Test
            fun getRequest_acceptNotRegistered_notAcceptable() = testApplication {
                application { registerConfig() }
                val response = client.get(SOME_PATH) {
                    accept(otherNonJson)
                }
                assertThat(response.status, equalTo(HttpStatusCode.NotAcceptable))
            }

            @Test
            fun postRequest_contentRegistered_bodyDeserializesToValue() = testApplication {
                val capturedBody = Box<Bar>()
                application { registerConfig(capturedBody) }
                val response = client.post(SOME_PATH) {
                    contentType(registeredNonJson)
                    setBody(SOME_BAR_JSON)
                }
                assertAll(
                        { assertThat(response.status, equalTo(HttpStatusCode.OK)) },
                        { assertThat(capturedBody.value, equalTo(SOME_BAR)) }
                )
            }

            @Test
            fun postRequest_contentNotRegistered_unsupported() = testApplication {
                val capturedBody = Box<Bar>()
                application { registerConfig(capturedBody) }
                val response = client.post(SOME_PATH) {
                    contentType(otherNonJson)
                    setBody(SOME_BAR_JSON)
                }
                assertThat(response.status, equalTo(HttpStatusCode.UnsupportedMediaType))
            }

            private fun Application.registerConfig(capturedBody: Box<Bar>? = null) {
                install(ServerContentNegotiation) {
                    register(registeredNonJson, MoshiConverter())
                }
                routing(SOME_BAR, capturedBody)
            }
        }

        private inline fun <reified T : Any> Application.routing(getResponse: T, capturedBody: Box<T>? = null) {
            routing {
                get(SOME_PATH) {
                    call.respond(getResponse)
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
    }

    @Nested
    inner class Client {
        @Nested
        inner class Reflection {
            @Test
            fun getRequest_respondJson_responseDeserializes() {
                val client = withTestClient({
                    moshi {
                        add(KotlinJsonAdapterFactory())
                    }
                }) {
                    respond(SOME_FOO_JSON, ContentType.Application.Json)
                }

                val result = client.blockingGet<Foo>(SOME_URL)

                assertThat(result, equalTo(SOME_FOO))
            }

            @Test
            fun getRequest_respondNotJson_noTransformError() {
                val client = withTestClient({
                    moshi {
                        add(KotlinJsonAdapterFactory())
                    }
                }) {
                    respond(SOME_FOO_JSON, SOME_NOT_JSON_TYPE)
                }

                assertThat({ client.blockingGet<Foo>(SOME_URL) }, throws<NoTransformationFoundException>())
            }

            @Test
            fun postRequest_sendJson_serializesBody() {
                var postBody: String? = null
                val client = withTestClient({
                    moshi {
                        add(KotlinJsonAdapterFactory())
                    }
                }) { request ->
                    postBody = (request.body as? TextContent)?.text
                    respond(SOME_FOO_JSON, ContentType.Application.Json)
                }

                runBlocking {
                    client.post(SOME_URL) {
                        contentType(ContentType.Application.Json)
                        setBody(SOME_FOO)
                    }
                }

                assertThat(postBody, equalTo(SOME_FOO_JSON))
            }

            @Test
            fun getRequest_respondNull_IllegalStateError() {
                val client = withTestClient({
                    moshi {
                        add(KotlinJsonAdapterFactory())
                    }
                }) {
                    respond("null", ContentType.Application.Json)
                }

                assertThat({ client.blockingGet<Foo>(SOME_URL) }, throws<ContentConvertException>())
            }
        }

        @Nested
        inner class Codegen {
            @Test
            fun getRequest_respondJson_responseDeserializes() {
                val client = withTestClient({ moshi() }) {
                    respond(SOME_BAR_JSON, ContentType.Application.Json)
                }

                val result = client.blockingGet<Bar>(SOME_URL)

                assertThat(result, equalTo(SOME_BAR))
            }

            @Test
            fun getRequest_respondNotJson_noTransformError() {
                val client = withTestClient({ moshi() }) {
                    respond(SOME_BAR_JSON, SOME_NOT_JSON_TYPE)
                }

                assertThat({ client.blockingGet<Bar>(SOME_URL) }, throws<NoTransformationFoundException>())
            }

            @Test
            fun postRequest_sendJson_serializesBody() {
                var postBody: String? = null
                val client = withTestClient({ moshi() }) { request ->
                    postBody = (request.body as? TextContent)?.text
                    respond(SOME_BAR_JSON, ContentType.Application.Json)
                }

                runBlocking {
                    client.post(SOME_URL) {
                        contentType(ContentType.Application.Json)
                        setBody(SOME_BAR)
                    }
                }

                assertThat(postBody, equalTo(SOME_BAR_JSON))
            }

            @Test
            fun getRequest_respondNull_JsonConvertException() {
                val client = withTestClient({ moshi() }) {
                    respond("null", ContentType.Application.Json)
                }

                assertThat({ client.blockingGet<Bar>(SOME_URL) }, throws<ContentConvertException>())
            }

            @Test
            fun getRequest_respondInvalidJsonStructure_JsonConvertException() {
                val client = withTestClient({ moshi() }) {
                    respond("{\"key\":\"missing count\"}", ContentType.Application.Json)
                }

                assertThat({ client.blockingGet<Bar>(SOME_URL) }, throws<JsonConvertException>())
            }

            @Test
            fun getRequest_respondMalformedJson_JsonConvertException() {
                val client = withTestClient({ moshi() }) {
                    respond("not JSON", ContentType.Application.Json)
                }

                assertThat({ client.blockingGet<Bar>(SOME_URL) }, throws<JsonConvertException>())
            }
        }

        private fun withTestClient(
                configure: ContentNegotiationConfig.() -> Unit,
                handler: MockRequestHandler
        ): HttpClient {
            return HttpClient(MockEngine) {
                install(ClientContentNegotiation) {
                    configure()
                }
                engine {
                    addHandler { request ->
                        if (request.url.fullPath.contains(SOME_PATH)) {
                            return@addHandler handler(request)
                        }
                        error("No matching handler for ${request.url}")
                    }
                }
            }
        }

        private inline fun <reified T> HttpClient.blockingGet(
                urlString: String,
                crossinline block: HttpRequestBuilder.() -> Unit = {}
        ): T = runBlocking {
            get(urlString, block).body()
        }

        fun MockRequestHandleScope.respond(jsonText: String, contentType: ContentType): HttpResponseData {
            return respond(jsonText, headers = headersOf("Content-Type" to listOf(contentType.toString())))
        }
    }
}

fun utf8Content(contentType: ContentType) = equalTo(contentType.withCharset(Charsets.UTF_8))

data class Foo(val id: Int, val name: String)

@JsonClass(generateAdapter = true)
data class Bar(val key: String, val count: Int)

data class Box<T>(var value: T? = null)
