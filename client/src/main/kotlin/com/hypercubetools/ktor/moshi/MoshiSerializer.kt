package com.hypercubetools.ktor.moshi

import com.squareup.moshi.Moshi
import io.ktor.client.call.TypeInfo
import io.ktor.client.features.json.JsonSerializer
import io.ktor.http.ContentType
import io.ktor.http.content.OutgoingContent
import io.ktor.http.content.TextContent
import io.ktor.utils.io.core.Input
import io.ktor.utils.io.core.readText

class MoshiSerializer(private val moshi: Moshi = Moshi.Builder().build()) : JsonSerializer {
    constructor(block: Moshi.Builder.() -> Unit) : this(Moshi.Builder().apply(block).build())

    override fun read(type: TypeInfo, body: Input): Any {
        val text = body.readText()
        return checkNotNull(moshi.adapter(type.type.javaObjectType).fromJson(text)) {
            "Parsing a Value to null is not allowed"
        }
    }

    override fun write(data: Any, contentType: ContentType): OutgoingContent =
            TextContent(moshi.adapter(data.javaClass).toJson(data), contentType)
}
