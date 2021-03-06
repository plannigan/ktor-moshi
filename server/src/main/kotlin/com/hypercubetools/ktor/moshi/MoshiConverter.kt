package com.hypercubetools.ktor.moshi

import com.squareup.moshi.Moshi
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.features.ContentConverter
import io.ktor.features.ContentNegotiation
import io.ktor.features.suitableCharset
import io.ktor.http.ContentType
import io.ktor.http.content.TextContent
import io.ktor.http.withCharset
import io.ktor.request.ApplicationReceiveRequest
import io.ktor.util.pipeline.PipelineContext
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.jvm.javaio.toInputStream
import okio.buffer
import okio.source

class MoshiConverter(private val moshi: Moshi = Moshi.Builder().build()) : ContentConverter {
  override suspend fun convertForReceive(context: PipelineContext<ApplicationReceiveRequest, ApplicationCall>): Any? {
    val request = context.subject
    val channel = request.value as? ByteReadChannel ?: return null
    val source = channel.toInputStream().source().buffer()
    val type = request.type
    return moshi.adapter(type.javaObjectType).fromJson(source)
  }

  override suspend fun convertForSend(context: PipelineContext<Any, ApplicationCall>, contentType: ContentType, value: Any): Any? {
    return TextContent(moshi.adapter(value.javaClass).toJson(value), contentType.withCharset(context.call.suitableCharset()))
  }
}

/**
 * Registers the supplied Moshi instance as a content converter for `application/json`
 * data.
 */
fun ContentNegotiation.Configuration.moshi(moshi: Moshi = Moshi.Builder().build()) {
  register(ContentType.Application.Json, MoshiConverter(moshi))
}

/**
 * Creates a new Moshi instance and registers it as a content converter for
 * `application/json` data.  The supplied block is used to configure the builder.
 */
fun ContentNegotiation.Configuration.moshi(block: Moshi.Builder.() -> Unit) {
  moshi(Moshi.Builder().apply(block).build())
}
